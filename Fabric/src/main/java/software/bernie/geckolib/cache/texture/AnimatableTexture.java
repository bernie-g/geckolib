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
 * Wrapper for {@link SimpleTexture SimpleTexture} implementation allowing for casual use of animated non-atlas textures
 */
public class AnimatableTexture extends SimpleTexture {
	private AnimationContents animationContents = null;
	private boolean isAnimated = false;

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

	private class AnimationContents {
		private final FrameSize frameSize;
		private final Texture animatedTexture;

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

		private record Frame(int index, int time) {}

		private class Texture implements AutoCloseable {
			private final NativeImage baseImage;
			private final Frame[] frames;
			private final int framePanelSize;
			private final boolean interpolating;
			private final NativeImage interpolatedFrame;
			private final int totalFrameTime;

			private int currentFrame;
			private int currentSubframe;

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
						TextureUtil.prepareImage(AnimatableTexture.this.getId(), 0, AnimationContents.this.frameSize.width(), AnimationContents.this.frameSize.height());
						this.baseImage.upload(0, 0, 0, getFrameX(this.currentFrame) * AnimationContents.this.frameSize.width(), getFrameY(this.currentFrame) * AnimationContents.this.frameSize.height(), AnimationContents.this.frameSize.width(), AnimationContents.this.frameSize.height(), false, false);
					});
				}
				else if (this.currentSubframe != lastSubframe && this.interpolating) {
					onRenderThread(this::generateInterpolatedFrame);
				}
			}

			private void generateInterpolatedFrame() {
				Frame frame = this.frames[this.currentFrame];
				double frameProgress = 1 - (double)this.currentSubframe / (double)frame.time;
				int nextFrameIndex = this.frames[(this.currentFrame + 1) % this.frames.length].index;

				if (frame.index != nextFrameIndex) {
					for (int y = 0; y < this.interpolatedFrame.getHeight(); ++y) {
						for (int x = 0; x < this.interpolatedFrame.getWidth(); ++x) {
							int prevFramePixel = getPixel(frame.index, x, y);
							int nextFramePixel = getPixel(nextFrameIndex, x, y);
							int blendedRed = interpolate(frameProgress, prevFramePixel >> 16 & 255, nextFramePixel >> 16 & 255);
							int blendedGreen = interpolate(frameProgress, prevFramePixel >> 8 & 255, nextFramePixel >> 8 & 255);
							int blendedBlue = interpolate(frameProgress, prevFramePixel & 255, nextFramePixel & 255);

							this.interpolatedFrame.setPixelRGBA(x, y, prevFramePixel & -16777216 | blendedRed << 16 | blendedGreen << 8 | blendedBlue);
						}
					}

					TextureUtil.prepareImage(AnimatableTexture.this.getId(), 0, AnimationContents.this.frameSize.width(), AnimationContents.this.frameSize.height());
					this.interpolatedFrame.upload(0, 0, 0, 0, 0, AnimationContents.this.frameSize.width(), AnimationContents.this.frameSize.height(), false, false);
				}
			}

			private int getPixel(int frameIndex, int x, int y) {
				return this.baseImage.getPixelRGBA(x + getFrameX(frameIndex) * AnimationContents.this.frameSize.width(), y + getFrameY(frameIndex) * AnimationContents.this.frameSize.height());
			}

			private int interpolate(double frameProgress, double prevColour, double nextColour) {
				return (int)(frameProgress * prevColour + (1 - frameProgress) * nextColour);
			}

			@Override
			public void close() {
				this.baseImage.close();

				if (this.interpolatedFrame != null)
					this.interpolatedFrame.close();
			}
		}
	}
}
