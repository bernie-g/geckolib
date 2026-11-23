package com.geckolib.renderer.texture;

import com.geckolib.GeckoLibConstants;
import com.mojang.blaze3d.GpuFormat;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.AddressMode;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTexture;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.client.resources.metadata.animation.AnimationFrame;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.client.resources.metadata.texture.TextureMetadataSection;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.ARGB;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;

/// Animated texture handler for GeckoLib animated textures.
///
/// Uses the vanilla [animated texture schema][AnimationMetadataSection], but extrapolates it for non-atlas textures
///
/// **<u>NOTE:</u>** Initially, GeckoLib wraps all texture retrievals in this, to check for animation meta. If it [exists][#isAnimated()], this instance is kept,
/// otherwise, a new instance of [SimpleTexture] is returned to preserve expected runtime operation for things like Iris.
public class GeckoLibAnimatedTexture extends SimpleTexture implements TickableTexture {
    protected final AtomicReference<@Nullable AnimationInfo> animation = new AtomicReference<>();
    protected final AtomicReference<@Nullable AnimationInfo> pendingAnimation = new AtomicReference<>();
    protected volatile boolean hasApplied = false;

    public GeckoLibAnimatedTexture(Identifier location) {
        super(location);
    }

    /// If GeckoLib found and constructed a valid animated texture schema.
    ///
    /// Returning false from here makes this no different from a standard [SimpleTexture], and an instance of that should be used instead
    public boolean isAnimated() {
        return this.animation.get() != null || this.pendingAnimation.get() != null;
    }

    @Override
    public TextureContents loadContents(ResourceManager resourceManager) throws IOException {
        final AnimationInfo stalePending = this.pendingAnimation.getAndSet(null);

        if (stalePending != null)
            stalePending.close();

        final Resource resource = resourceManager.getResourceOrThrow(resourceId());
        final AnimationMetadataSection animMeta = resource.metadata().getSection(AnimationMetadataSection.TYPE).orElse(null);
        final TextureMetadataSection textureMeta = resource.metadata().getSection(TextureMetadataSection.TYPE).orElse(null);

        if (animMeta == null && !this.hasApplied)
            return new TextureContents(MissingTextureAtlasSprite.generateMissingImage(1, 1), textureMeta);

        final NativeImage image;

        try (InputStream stream = resource.open()) {
            image = NativeImage.read(stream);
        }

        if (animMeta != null) {
            final AnimationInfo animation = buildAnimatedTexture(image, animMeta);

            if (animation == null) {
                if (this.hasApplied)
                    return new TextureContents(image, textureMeta);

                image.close();

                return new TextureContents(MissingTextureAtlasSprite.generateMissingImage(1, 1), textureMeta);
            }

            this.pendingAnimation.set(animation);
        }

        return new TextureContents(image, textureMeta);
    }

    @Override
    public void apply(TextureContents textureContents) {
        final AnimationInfo animation = this.pendingAnimation.getAndSet(null);
        final AnimationInfo prevAnimation = this.animation.getAndSet(animation);
        final AddressMode address = textureContents.clamp() ? AddressMode.CLAMP_TO_EDGE : AddressMode.REPEAT;
        final FilterMode filter = textureContents.blur() ? FilterMode.LINEAR : FilterMode.NEAREST;
        this.sampler = RenderSystem.getSamplerCache().getSampler(address, address, filter, filter, false);

        doLoad(textureContents.image());

        if (animation == null)
            textureContents.image().close();

        if (prevAnimation != null)
            prevAnimation.close();

        this.hasApplied = true;
    }

    @Override
    public void doLoad(NativeImage image) {
        releaseTextures();

        final GpuDevice gpuDevice = RenderSystem.getDevice();
        final Identifier textureId = Objects.requireNonNull(resourceId());
        final AnimationInfo animation = this.animation.get();
        final int width = animation == null ? image.getWidth() : animation.frameBuffer.getWidth();
        final int height = animation == null ? image.getHeight() : animation.frameBuffer.getHeight();
        this.texture = gpuDevice.createTexture(textureId::toString, 5, GpuFormat.RGBA8_UNORM, width, height, 1, 1);
        this.textureView = gpuDevice.createTextureView(this.texture);

        if (animation != null) {
            animation.uploadDiscreteFrame(animation.frames.getFirst().index);
        }
        else {
            uploadFrame(gpuDevice, image, width, height, 0, 0, this.texture);
        }
    }

    /// Compile the AnimatedTexture information for this texture instance
    ///
    /// Mostly used for interpolation handling and tick-frame advancement
    protected GeckoLibAnimatedTexture.@Nullable AnimationInfo buildAnimatedTexture(NativeImage texture, AnimationMetadataSection animMeta) {
        final FrameSize frameSize = animMeta.calculateFrameSize(texture.getWidth(), texture.getHeight());
        final int frameWidth = frameSize.width();
        final int frameHeight = frameSize.height();
        final int frameColumns = texture.getWidth() / frameWidth;
        final int frameRows = texture.getHeight() / frameHeight;
        final int frames = frameColumns * frameRows;
        final int defaultFrameTime = animMeta.defaultFrameTime();
        final int frameCount = animMeta.frames().map(List::size).orElse(frames);

        if (frameCount <= 1)
            return null;

        final List<FrameInfo> frameList = new ObjectArrayList<>(frameCount);

        if (animMeta.frames().isEmpty()) {
            for (int i = 0; i < frames; i++) {
                frameList.add(new FrameInfo(i, defaultFrameTime));
            }
        }
        else {
            for (AnimationFrame frame : animMeta.frames().get()) {
                frameList.add(new FrameInfo(frame.index(), frame.timeOr(defaultFrameTime)));
            }

            int frameIndex = 0;
            IntSet validFrames = new IntOpenHashSet();

            for (Iterator<FrameInfo> iterator = frameList.iterator(); iterator.hasNext(); frameIndex++) {
                FrameInfo frameInfo = iterator.next();
                boolean validFrame = true;

                if (frameInfo.time <= 0) {
                    GeckoLibConstants.LOGGER.warn("Invalid frame duration on sprite {} frame {}: {}", resourceId(), frameIndex, frameInfo.time);
                    validFrame = false;
                }

                if (frameInfo.index < 0 || frameInfo.index >= frames) {
                    GeckoLibConstants.LOGGER.warn("Invalid frame index on sprite {} frame {}: {}", resourceId(), frameIndex, frameInfo.index);
                    validFrame = false;
                }

                if (validFrame) {
                    validFrames.add(frameInfo.index);
                }
                else {
                    iterator.remove();
                }
            }

            int[] unusedFrames = IntStream.range(0, frames).filter(frame -> !validFrames.contains(frame)).toArray();

            if (unusedFrames.length > 0)
                GeckoLibConstants.LOGGER.warn("Unused frames in sprite {}: {}", resourceId(), Arrays.toString(unusedFrames));
        }

        return new AnimationInfo(texture, List.copyOf(frameList), frameWidth, frameHeight, frameColumns, animMeta.interpolatedFrames());
    }

    /// Upload the given [NativeImage] to the in-memory texture buffer, with an optional offset for non-interpolated frames
    protected void uploadFrame(GpuDevice gpuDevice, NativeImage image, int width, int height, int x, int y, GpuTexture gpuTexture) {
        gpuDevice.createCommandEncoder().writeToTexture(gpuTexture, image.getPixelBytes(), 0, 0, x, y, width, height);
    }

    /// Called by [TextureManager] every tick to allow the texture to update itself as necessary.
    ///
    /// This effectively caps "real" frames at 20fps, but interpolation allows us to fudge this a little.
    @Override
    public void tick() {
        final AnimationInfo animation = this.animation.get();

        if (animation != null)
            animation.tick();
    }

    @Override
    public void close() {
        final AnimationInfo animation = this.animation.getAndSet(null);
        final AnimationInfo pendingAnimation = this.pendingAnimation.getAndSet(null);

        if (animation != null)
            animation.close();

        if (pendingAnimation != null)
            pendingAnimation.close();

        super.close();
    }

    /// Container class for the animation information for this texture instance
    ///
    /// Functionally somewhat of a clone of [SpriteContents.AnimationState], but extrapolated to be extensible and manageable
    protected class AnimationInfo implements AutoCloseable {
        protected final NativeImage texture;
        protected final NativeImage frameBuffer;
        protected final List<FrameInfo> frames;
        protected final int frameRowSize;
        protected final boolean interpolateFrames;

        int currentFrame;
        int subFrame;

        public AnimationInfo(NativeImage texture, List<FrameInfo> frames, int frameWidth, int frameHeight, int frameRowSize, boolean interpolateFrames) {
            this.texture = texture;
            this.frameBuffer = new NativeImage(frameWidth, frameHeight, false);
            this.frames = frames;
            this.frameRowSize = frameRowSize;
            this.interpolateFrames = interpolateFrames;
        }

        int getFrameColumn(int frameIndex) {
            return frameIndex % this.frameRowSize;
        }

        int getFrameRow(int frameIndex) {
            return frameIndex / this.frameRowSize;
        }

        public void tick() {
            this.subFrame++;
            final FrameInfo prevFrameInfo = this.frames.get(this.currentFrame);

            if (this.subFrame >= prevFrameInfo.time) {
                this.currentFrame = (this.currentFrame + 1) % this.frames.size();
                this.subFrame = 0;
                final int frameIndex = this.frames.get(this.currentFrame).index;

                if (prevFrameInfo.index != frameIndex)
                    uploadDiscreteFrame(frameIndex);
            }
            else if (this.interpolateFrames) {
                uploadInterpolatedFrame(this.texture, this.frameBuffer, getTexture());
            }
        }

        /// Upload the given discrete frame to buffer
        protected void uploadDiscreteFrame(int frameIndex) {
            final int width = this.frameBuffer.getWidth();
            final int height = this.frameBuffer.getHeight();
            final int frameX = getFrameColumn(frameIndex) * width;
            final int frameY = getFrameRow(frameIndex) * height;

            this.texture.copyRect(this.frameBuffer, frameX, frameY, 0, 0, width, height, false, false);
            uploadFrame(RenderSystem.getDevice(), this.frameBuffer, width, height, 0, 0, getTexture());
        }

        /// Check and upload a newly created, interpolated frame, as necessary
        protected void uploadInterpolatedFrame(NativeImage image, NativeImage frameBuffer, GpuTexture gpuTexture) {
            final List<FrameInfo> frames = this.frames;
            final FrameInfo currentFrameInfo = frames.get(this.currentFrame);
            final int nextFrameIndex = frames.get((this.currentFrame + 1) % frames.size()).index;

            if (currentFrameInfo.index != nextFrameIndex) {
                final float partialFrame = this.subFrame / (float)currentFrameInfo.time;
                final int width = frameBuffer.getWidth();
                final int height = frameBuffer.getHeight();

                for (int pixelY = 0; pixelY < height; pixelY++) {
                    for (int pixelX = 0; pixelX < width; pixelX++) {
                        int framePixel = getPixel(image, currentFrameInfo.index, pixelX, pixelY, width, height);
                        int nextFramePixel = getPixel(image, nextFrameIndex, pixelX, pixelY, width, height);

                        frameBuffer.setPixel(pixelX, pixelY, ARGB.linearLerp(partialFrame, framePixel, nextFramePixel));
                    }
                }

                uploadFrame(RenderSystem.getDevice(), frameBuffer, width, height, 0, 0, gpuTexture);
            }
        }

        /// Get the frame-relative pixel for the given input coordinates and frame index from the root texture
        protected int getPixel(NativeImage image, int frameIndex, int x, int y, int frameWidth, int frameHeight) {
            return image.getPixel(x + getFrameColumn(frameIndex) * frameWidth, y + getFrameRow(frameIndex) * frameHeight);
        }

        @Override
        public void close() {
            this.texture.close();
            this.frameBuffer.close();
        }
    }

    /// Container class for holding a single animation frame's data
    protected record FrameInfo(int index, int time) {}
}
