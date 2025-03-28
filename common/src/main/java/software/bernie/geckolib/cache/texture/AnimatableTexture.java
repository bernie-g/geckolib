package software.bernie.geckolib.cache.texture;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.TextureUtil;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureContents;
import net.minecraft.client.renderer.texture.Tickable;
import net.minecraft.client.resources.metadata.animation.AnimationFrame;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.client.resources.metadata.texture.TextureMetadataSection;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceMetadata;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.GeckoLibConstants;
import software.bernie.geckolib.util.RenderUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Wrapper for {@link SimpleTexture SimpleTexture} implementation allowing for casual use of animated non-atlas textures
 */
public class AnimatableTexture extends SimpleTexture implements Tickable {
	protected AnimationContents animationContents = null;
	protected boolean isAnimated = false;

	public AnimatableTexture(final ResourceLocation location) {
		super(location);
	}

	/**
	 * Returns whether the texture found any valid animation metadata when loading.
	 * <p>
	 * If false, then this is no different to a standard {@link SimpleTexture}
	 */
	public boolean isAnimated() {
		return this.isAnimated;
	}

	@Override
	public TextureContents loadContents(ResourceManager resourceManager) throws IOException {
		Resource resource = resourceManager.getResourceOrThrow(resourceId());
		ResourceMetadata resourceMeta = resource.metadata();
		Optional<AnimationMetadataSection> animationMeta = resourceMeta.getSection(AnimationMetadataSection.TYPE);
		TextureMetadataSection textureMeta = resourceMeta.getSection(TextureMetadataSection.TYPE).orElse(null);

		NativeImage image;
		TextureContents textureContents;

		if (animationMeta.isEmpty()) {
			image = new NativeImage(1, 1, false);
			textureContents = new TextureContents(image, textureMeta);

			image.close();
		}
		else {
			try (InputStream inputstream = resource.open()) {
				image = NativeImage.read(inputstream);
			}

			textureContents = new TextureContents(image, textureMeta);
			this.animationContents = new AnimationContents(image, animationMeta.get());

			if (this.animationContents.isValid()) {
				this.isAnimated = true;
				this.defaultBlur = textureContents.blur();

				tick();
			}
		}

		return textureContents;
	}

	@Override
	public void apply(TextureContents textureContents) {
		tick();
	}

	@Override
	public void doLoad(NativeImage image, boolean blur, boolean clamp) {
		TextureUtil.prepareImage(getId(), 0, image.getWidth(), image.getHeight());
		setFilter(blur, false);
		setClamp(clamp);
		image.upload(0, 0, 0, 0, 0, image.getWidth(), image.getHeight(), false);
	}

	@Override
	public void tick() {
		setAnimationFrame((int)RenderUtil.getCurrentTick());
	}

	public void setAnimationFrame(int tick) {
		if (this.animationContents != null && this.animationContents.animatedTexture != null)
			this.animationContents.animatedTexture.setCurrentFrame(tick);
	}

	protected class AnimationContents {
		protected final FrameSize frameSize;
		@Nullable
		protected final Texture animatedTexture;

		private AnimationContents(NativeImage image, AnimationMetadataSection animMeta) {
			this.frameSize = animMeta.calculateFrameSize(image.getWidth(), image.getHeight());
			this.animatedTexture = createTexture(image, animMeta);
		}

		private boolean isValid() {
			return this.animatedTexture != null;
		}

		@Nullable
		private Texture createTexture(NativeImage image, AnimationMetadataSection animMeta) {
			if (!Mth.isMultipleOf(image.getWidth(), this.frameSize.width()) || !Mth.isMultipleOf(image.getHeight(), this.frameSize.height())) {
				GeckoLibConstants.LOGGER.error("Image {} size {},{} is not multiple of frame size {},{}", AnimatableTexture.this.resourceId(), image.getWidth(), image.getHeight(), this.frameSize.width(), this.frameSize.height());

				return null;
			}

			int columns = image.getWidth() / this.frameSize.width();
			int rows = image.getHeight() / this.frameSize.height();
			int maxFrameCount = columns * rows;
			int defaultFrameTime = animMeta.defaultFrameTime();
			List<Frame> frames = new ObjectArrayList<>(animMeta.frames().map(List::size).orElse(maxFrameCount));

			animMeta.frames().ifPresentOrElse(animFrames -> {
				for (AnimationFrame frame : animFrames) {
					frames.add(new Frame(frame.index(), frame.timeOr(defaultFrameTime)));
				}

				int index = 0;
				IntSet unusedFrames = new IntOpenHashSet();

				for (Frame frame : frames) {
					if (frame.time <= 0) {
						GeckoLibConstants.LOGGER.warn("Invalid frame duration on sprite {} frame {}: {}", AnimatableTexture.this.resourceId(), index, frame.time);
						unusedFrames.add(frame.index);
					}
					else if (frame.index < 0 || frame.index >= maxFrameCount) {
						GeckoLibConstants.LOGGER.warn("Invalid frame index on sprite {} frame {}: {}", AnimatableTexture.this.resourceId(), index, frame.index);
						unusedFrames.add(frame.index);
					}

					index++;
				}

				if (!unusedFrames.isEmpty())
					GeckoLibConstants.LOGGER.warn("Unused frames in sprite {}: {}", AnimatableTexture.this.resourceId(), Arrays.toString(unusedFrames.toArray()));
			}, () -> {
				for (int i = 0; i < maxFrameCount; i++) {
					frames.add(new Frame(i, defaultFrameTime));
				}
			});

			return frames.size() <= 1 ? null : new Texture(image, frames.toArray(new Frame[0]), columns, animMeta.interpolatedFrames(),
					this.animatedTexture != null && this.animatedTexture.clamp, this.animatedTexture != null && this.animatedTexture.blur);
		}

		protected record Frame(int index, int time) {}

		protected class Texture implements AutoCloseable {
			protected final NativeImage baseImage;
			protected final Frame[] frames;
			protected final int framePanelSize;
			protected final boolean interpolating;
			protected final NativeImage interpolatedFrame;
			protected final int totalFrameTime;
			protected final boolean clamp;
			protected final boolean blur;
			protected AbstractTexture glowMaskTexture = null;
			protected NativeImage glowmaskImage = null;
			protected NativeImage glowmaskInterpolatedFrame = null;

			protected int currentFrame;
			protected int currentSubframe;

			private Texture(NativeImage baseImage, Frame[] frames, int framePanelSize, boolean interpolating, boolean clamp, boolean blur) {
				this.baseImage = baseImage;
				this.frames = frames;
				this.framePanelSize = framePanelSize;
				this.interpolating = interpolating;
				this.interpolatedFrame = interpolating ? new NativeImage(AnimationContents.this.frameSize.width(), AnimationContents.this.frameSize.height(), false) : null;
				this.clamp = clamp;
				this.blur = blur;
				int time = 0;

				for (Frame frame : this.frames) {
					time += frame.time;
				}

				this.totalFrameTime = time;
			}

			private int getFrameX(int frameIndex) {
				return frameIndex % this.framePanelSize;
			}

			private int getFrameY(int frameIndex) {
				return frameIndex / this.framePanelSize;
			}

			public void setGlowMaskTexture(AutoGlowingTexture texture, NativeImage baseImage, NativeImage glowMask) {
				this.glowMaskTexture = texture;
				this.glowmaskImage = glowMask;
				this.glowmaskInterpolatedFrame = this.interpolating ? new NativeImage(AnimationContents.this.frameSize.width(), AnimationContents.this.frameSize.height(), false) : null;
				this.baseImage.copyFrom(baseImage);
			}

			public void setCurrentFrame(int ticks) {
				ticks %= this.totalFrameTime;

				if (ticks == this.currentSubframe)
					return;

				int lastSubframe = this.currentSubframe;
				int lastFrame = this.currentFrame;
				int time = 0;

				for (Frame frame : this.frames) {
					time += frame.time;

					if (ticks < time) {
						this.currentFrame = frame.index;
						this.currentSubframe = ticks % frame.time;

						break;
					}
				}

				if (this.currentFrame != lastFrame && this.currentSubframe == 0) {
					int frameWidth = AnimationContents.this.frameSize.width();
					int frameHeight = AnimationContents.this.frameSize.height();

					GeoAbstractTexture.uploadTexture(AnimatableTexture.this, this.baseImage, this.clamp, this.blur,
							getFrameX(this.currentFrame) * frameWidth, getFrameY(this.currentFrame) * frameHeight, frameWidth, frameHeight, false);

					if (this.glowmaskImage != null) {
						GeoAbstractTexture.uploadTexture(this.glowmaskTexture, this.glowmaskImage, this.clamp, this.blur,
														 getFrameX(this.currentFrame) * frameWidth, getFrameY(this.currentFrame) * frameHeight, frameWidth, frameHeight, false);
					}
				}
				else if (this.currentSubframe != lastSubframe && this.interpolating) {
					GeoAbstractTexture.runOnRenderThread(() -> {
						generateInterpolatedFrame(AnimatableTexture.this, this.baseImage, this.interpolatedFrame);

						if (this.glowmaskImage != null)
							generateInterpolatedFrame(this.glowMaskTexture, this.glowmaskImage, this.glowmaskInterpolatedFrame);
					});
				}
			}

			private void generateInterpolatedFrame(AbstractTexture texture, NativeImage image, NativeImage interpolatedFrame) {
				Frame frame = this.frames[this.currentFrame];
				double frameProgress = 1 - (double)this.currentSubframe / (double)frame.time;
				int nextFrameIndex = this.frames[(this.currentFrame + 1) % this.frames.length].index;

				if (frame.index != nextFrameIndex) {
					for (int y = 0; y < interpolatedFrame.getHeight(); ++y) {
						for (int x = 0; x < interpolatedFrame.getWidth(); ++x) {
							int prevFramePixel = getPixel(image, frame.index, x, y);
							int nextFramePixel = getPixel(image, nextFrameIndex, x, y);
							int blendedRed = interpolate(frameProgress, prevFramePixel >> 16 & 255, nextFramePixel >> 16 & 255);
							int blendedGreen = interpolate(frameProgress, prevFramePixel >> 8 & 255, nextFramePixel >> 8 & 255);
							int blendedBlue = interpolate(frameProgress, prevFramePixel & 255, nextFramePixel & 255);

							interpolatedFrame.setPixel(x, y, prevFramePixel & -16777216 | blendedRed << 16 | blendedGreen << 8 | blendedBlue);
						}
					}

					GeoAbstractTexture.uploadTexture(texture, this.interpolatedFrame, this.clamp, this.blur,
							0, 0, AnimationContents.this.frameSize.width(), AnimationContents.this.frameSize.height(), false);
				}
			}

			private int getPixel(NativeImage image, int frameIndex, int x, int y) {
				return image.getPixel(x + getFrameX(frameIndex) * AnimationContents.this.frameSize.width(), y + getFrameY(frameIndex) * AnimationContents.this.frameSize.height());
			}

			private int interpolate(double frameProgress, double prevColor, double nextColor) {
				return (int)(frameProgress * prevColor + (1 - frameProgress) * nextColor);
			}

			@Override
			public void close() {
				this.baseImage.close();

				if (this.interpolatedFrame != null)
					this.interpolatedFrame.close();

				if (this.glowmaskImage != null)
					this.glowmaskImage.close();

				if (this.glowmaskInterpolatedFrame != null)
					this.glowmaskInterpolatedFrame.close();
			}
		}
	}
}
