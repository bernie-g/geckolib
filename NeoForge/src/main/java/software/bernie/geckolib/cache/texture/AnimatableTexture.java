package software.bernie.geckolib.cache.texture;

import com.mojang.blaze3d.pipeline.RenderCall;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Mth;
import software.bernie.geckolib.GeckoLib;
import software.bernie.geckolib.util.RenderUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

/**
 * Wrapper for {@link net.minecraft.client.renderer.texture.SimpleTexture SimpleTexture} implementation allowing for casual use of animated non-atlas textures
 */
public class AnimatableTexture extends SimpleTexture {
	protected AnimationContents animationContents = null;
	protected boolean isAnimated = false;

	public AnimatableTexture(final ResourceLocation location) {
		super(location);
	}

	@Override
	public void load(ResourceManager manager) throws IOException {
		Resource resource = manager.getResourceOrThrow(this.location);

		try {
			NativeImage nativeImage;

			try (InputStream inputstream = resource.open()) {
				nativeImage = NativeImage.read(inputstream);
			}

			this.animationContents = resource.metadata().getSection(AnimationMetadataSection.SERIALIZER).map(animMeta -> new AnimationContents(nativeImage, animMeta)).orElse(null);

			if (this.animationContents != null) {
				if (!this.animationContents.isValid()) {
					nativeImage.close();

					return;
				}

				this.isAnimated = true;

				onRenderThread(() -> {
					TextureUtil.prepareImage(getId(), 0, this.animationContents.frameSize.width(), this.animationContents.frameSize.height());
					nativeImage.upload(0, 0, 0, 0, 0, this.animationContents.frameSize.width(), this.animationContents.frameSize.height(), false, false);
				});
			}
		}
		catch (RuntimeException exception) {
			GeckoLib.LOGGER.warn("Failed reading metadata of: {}", this.location, exception);
		}
	}

	/**
	 * Returns whether the texture found any valid animation metadata when loading.
	 * <p>
	 * If false, then this is no different to a standard {@link SimpleTexture}
	 */
	public boolean isAnimated() {
		return this.isAnimated;
	}

	public static void setAndUpdate(ResourceLocation texturePath) {
		setAndUpdate(texturePath, (int) RenderUtils.getCurrentTick());
	}

	/**
	 * Setting a specific frame for the animated texture does not work well because of how Minecraft buffers rendering passes.
	 * <p>Use the non-specified method above unless you know what you're doing</p>
	 */
	public static void setAndUpdate(ResourceLocation texturePath, int frameTick) {
		AbstractTexture texture = Minecraft.getInstance().getTextureManager().getTexture(texturePath);

		if (texture instanceof AnimatableTexture animatableTexture)
			animatableTexture.setAnimationFrame(frameTick);

		RenderSystem.setShaderTexture(0, texture.getId());
	}

	public void setAnimationFrame(int tick) {
		if (this.animationContents != null)
			this.animationContents.animatedTexture.setCurrentFrame(tick);
	}

	private static void onRenderThread(RenderCall renderCall) {
		if (!RenderSystem.isOnRenderThread()) {
			RenderSystem.recordRenderCall(renderCall);
		}
		else {
			renderCall.execute();
		}
	}

	protected class AnimationContents {
		protected final FrameSize frameSize;
		protected final Texture animatedTexture;

		private AnimationContents(NativeImage image, AnimationMetadataSection animMeta) {
			this.frameSize = animMeta.calculateFrameSize(image.getWidth(), image.getHeight());
			this.animatedTexture = generateAnimatedTexture(image, animMeta);
		}

		private boolean isValid() {
			return this.animatedTexture != null;
		}

		private Texture generateAnimatedTexture(NativeImage image, AnimationMetadataSection animMeta) {
			if (!Mth.isMultipleOf(image.getWidth(), this.frameSize.width()) || !Mth.isMultipleOf(image.getHeight(), this.frameSize.height())) {
				GeckoLib.LOGGER.error("Image {} size {},{} is not multiple of frame size {},{}", AnimatableTexture.this.location, image.getWidth(), image.getHeight(), this.frameSize.width(), this.frameSize.height());

				return null;
			}

			int columns = image.getWidth() / this.frameSize.width();
			int rows = image.getHeight() / this.frameSize.height();
			int frameCount = columns * rows;
			List<Frame> frames = new ObjectArrayList<>();

			animMeta.forEachFrame((frame, frameTime) -> frames.add(new Frame(frame, frameTime)));

			if (frames.isEmpty()) {
				for (int frame = 0; frame < frameCount; ++frame) {
					frames.add(new Frame(frame, animMeta.getDefaultFrameTime()));
				}
			}
			else {
				int index = 0;
				IntSet unusedFrames = new IntOpenHashSet();

				for (Frame frame : frames) {
					if (frame.time <= 0) {
						GeckoLib.LOGGER.warn("Invalid frame duration on sprite {} frame {}: {}", AnimatableTexture.this.location, index, frame.time);
						unusedFrames.add(frame.index);
					}
					else if (frame.index < 0 || frame.index >= frameCount) {
						GeckoLib.LOGGER.warn("Invalid frame index on sprite {} frame {}: {}", AnimatableTexture.this.location, index, frame.index);
						unusedFrames.add(frame.index);
					}

					index++;
				}

				if (!unusedFrames.isEmpty())
					GeckoLib.LOGGER.warn("Unused frames in sprite {}: {}", AnimatableTexture.this.location, Arrays.toString(unusedFrames.toArray()));
			}

			return frames.size() <= 1 ? null : new Texture(image, frames.toArray(new Frame[0]), columns, animMeta.isInterpolatedFrames());
		}

		protected record Frame(int index, int time) {}

		protected class Texture implements AutoCloseable {
			protected final NativeImage baseImage;
			protected final Frame[] frames;
			protected final int framePanelSize;
			protected final boolean interpolating;
			protected final NativeImage interpolatedFrame;
			protected final int totalFrameTime;

			protected int glowMaskTextureId = -1;
			protected NativeImage glowmaskImage = null;
			protected NativeImage glowmaskInterpolatedFrame = null;

			protected int currentFrame;
			protected int currentSubframe;

			private Texture(NativeImage baseImage, Frame[] frames, int framePanelSize, boolean interpolating) {
				this.baseImage = baseImage;
				this.frames = frames;
				this.framePanelSize = framePanelSize;
				this.interpolating = interpolating;
				this.interpolatedFrame = interpolating ? new NativeImage(AnimationContents.this.frameSize.width(), AnimationContents.this.frameSize.height(), false) : null;
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
				this.glowMaskTextureId = texture.getId();
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
					onRenderThread(() -> {
						TextureUtil.prepareImage(getId(), 0, AnimationContents.this.frameSize.width(), AnimationContents.this.frameSize.height());
						this.baseImage.upload(0, 0, 0, getFrameX(this.currentFrame) * AnimationContents.this.frameSize.width(), getFrameY(this.currentFrame) * AnimationContents.this.frameSize.height(), AnimationContents.this.frameSize.width(), AnimationContents.this.frameSize.height(), false, false);

						if (this.glowmaskImage != null) {
							TextureUtil.prepareImage(this.glowMaskTextureId, 0, AnimationContents.this.frameSize.width(), AnimationContents.this.frameSize.height());
							this.glowmaskImage.upload(0, 0, 0, getFrameX(this.currentFrame) * AnimationContents.this.frameSize.width(), getFrameY(this.currentFrame) * AnimationContents.this.frameSize.height(), AnimationContents.this.frameSize.width(), AnimationContents.this.frameSize.height(), false, false);
						}
					});
				}
				else if (this.currentSubframe != lastSubframe && this.interpolating) {
					onRenderThread(() -> {
						generateInterpolatedFrame(getId(), this.baseImage, this.interpolatedFrame);

						if (this.glowmaskImage != null)
							generateInterpolatedFrame(this.glowMaskTextureId, this.glowmaskImage, this.glowmaskInterpolatedFrame);
					});
				}
			}

			private void generateInterpolatedFrame(int textureId, NativeImage image, NativeImage interpolatedFrame) {
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

							interpolatedFrame.setPixelRGBA(x, y, prevFramePixel & -16777216 | blendedRed << 16 | blendedGreen << 8 | blendedBlue);
						}
					}

					TextureUtil.prepareImage(textureId, 0, AnimationContents.this.frameSize.width(), AnimationContents.this.frameSize.height());
					interpolatedFrame.upload(0, 0, 0, 0, 0, AnimationContents.this.frameSize.width(), AnimationContents.this.frameSize.height(), false, false);
				}
			}

			private int getPixel(NativeImage image, int frameIndex, int x, int y) {
				return image.getPixelRGBA(x + getFrameX(frameIndex) * AnimationContents.this.frameSize.width(), y + getFrameY(frameIndex) * AnimationContents.this.frameSize.height());
			}

			private int interpolate(double frameProgress, double prevColour, double nextColour) {
				return (int)(frameProgress * prevColour + (1 - frameProgress) * nextColour);
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
