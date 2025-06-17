package software.bernie.geckolib.renderer.texture;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.TextureFormat;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureContents;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.Tickable;
import net.minecraft.client.resources.metadata.animation.AnimationFrame;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.client.resources.metadata.texture.TextureMetadataSection;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.ARGB;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.GeckoLibConstants;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

/**
 * Animated texture handler for GeckoLib animated textures.
 * <p>
 * Uses the vanilla {@link AnimationMetadataSection animated texture schema}, but extrapolates it for non-atlas textures
 * <p>
 * <b><u>NOTE:</u></b> Initially, GeckoLib wraps all texture retrievals in this, to check for animation meta. If it {@link #isAnimated() exists}, this instance is kept,
 * otherwise, a new instance of {@link SimpleTexture} is returned to preserve expected runtime operation for things like Iris.
 */
public class GeckoLibAnimatedTexture extends SimpleTexture implements Tickable {
    protected AnimationInfo animatedTexture;
    protected int frameWidth;
    protected int frameHeight;
    protected NativeImage baseImage;

    public GeckoLibAnimatedTexture(ResourceLocation location) {
        super(location);
    }

    /**
     * If GeckoLib found and constructed a valid animated texture schema.
     * <p>
     * Returning false from here makes this no different from a standard {@link SimpleTexture}, and an instance of that should be used instead
     */
    public boolean isAnimated() {
        return this.animatedTexture != null;
    }

    @Override
    public TextureContents loadContents(ResourceManager resourceManager) throws IOException {
        Resource resource = resourceManager.getResourceOrThrow(resourceId());

        try (InputStream stream = resource.open()) {
            this.baseImage = NativeImage.read(stream);
        }

        if (this.baseImage != null)
            this.animatedTexture = resource.metadata().getSection(AnimationMetadataSection.TYPE).map(this::buildAnimatedTexture).orElse(null);

        return new TextureContents(this.baseImage, resource.metadata().getSection(TextureMetadataSection.TYPE).orElse(null));
    }

    @Override
    public void apply(TextureContents textureContents) {
        boolean clamp = textureContents.clamp();
        boolean blur = textureContents.blur();

        doLoad(this.baseImage, blur, clamp);
    }

    @Override
    public void doLoad(NativeImage image, boolean blur, boolean clamp) {
        ResourceLocation textureId = resourceId();

        Objects.requireNonNull(textureId);

        this.texture = RenderSystem.getDevice().createTexture(textureId.toString(), 5, TextureFormat.RGBA8, this.frameWidth, this.frameHeight, 1, 1);
        this.texture.setTextureFilter(FilterMode.NEAREST, false);
        this.textureView = RenderSystem.getDevice().createTextureView(this.texture);

        setFilter(blur, false);
        setClamp(clamp);
        uploadFrame(image, 0, 0, this.texture);
    }

    /**
     * Compile the AnimatedTexture information for this texture instance
     * <p>
     * Mostly used for interpolation handling and tick-frame advancement
     */
    @Nullable
    protected GeckoLibAnimatedTexture.AnimationInfo buildAnimatedTexture(AnimationMetadataSection animMeta) {
        final FrameSize frameSize = animMeta.calculateFrameSize(this.baseImage.getWidth(), this.baseImage.getHeight());
        this.frameWidth = frameSize.width();
        this.frameHeight = frameSize.height();
        final int frameColumns = this.baseImage.getWidth() / this.frameWidth;
        final int frameRows = this.baseImage.getHeight() / this.frameHeight;
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

        return new AnimationInfo(List.copyOf(frameList), frameColumns, animMeta.interpolatedFrames());
    }

    /**
     * Upload the given {@link NativeImage} to the in-memory texture buffer, with an optional offset for non-interpolated frames
     */
    protected void uploadFrame(NativeImage image, int x, int y, GpuTexture gpuTexture) {
        RenderSystem.getDevice().createCommandEncoder().writeToTexture(gpuTexture, image, 0, 0, x, y, this.frameWidth, this.frameHeight, 0, 0);
    }

    /**
     * Called by {@link TextureManager} every tick to allow the texture to update itself as necessary.
     * <p>
     * This effectively caps "real" frames at 20fps, but interpolation allows us to fudge this a little.
     */
    @Override
    public void tick() {
        this.animatedTexture.tick();
    }

    @Override
    public void close() {
        if (this.baseImage != null)
            this.baseImage.close();

        if (this.animatedTexture != null)
            this.animatedTexture.close();

        super.close();
    }

    /**
     * Container class for the animation information for this texture instance
     */
    protected class AnimationInfo implements AutoCloseable {
        protected final List<FrameInfo> frames;
        protected final int frameRowSize;
        protected final boolean interpolateFrames;

        @Nullable
        protected final InterpolationData interpolationData;
        protected final NativeImage currentFrameBuffer;
        int currentFrame;
        int subFrame;

        public AnimationInfo(List<FrameInfo> frames, int frameRowSize, boolean interpolateFrames) {
            this.frames = frames;
            this.frameRowSize = frameRowSize;
            this.interpolateFrames = interpolateFrames;
            this.interpolationData = this.interpolateFrames ? new InterpolationData(GeckoLibAnimatedTexture.this.frameWidth, GeckoLibAnimatedTexture.this.frameHeight) : null;
            this.currentFrameBuffer = new NativeImage(GeckoLibAnimatedTexture.this.frameWidth, GeckoLibAnimatedTexture.this.frameHeight, false);
        }

        int getFrameColumn(int frameIndex) {
            return frameIndex % this.frameRowSize;
        }

        int getFrameRow(int frameIndex) {
            return frameIndex / this.frameRowSize;
        }

        public void tick() {
            this.subFrame++;
            FrameInfo prevFrameInfo = this.frames.get(this.currentFrame);

            if (this.subFrame >= prevFrameInfo.time) {
                this.currentFrame = (this.currentFrame + 1) % this.frames.size();
                this.subFrame = 0;
                int frameIndex = this.frames.get(this.currentFrame).index;

                if (prevFrameInfo.index != frameIndex) {
                    GeckoLibAnimatedTexture instance = GeckoLibAnimatedTexture.this;
                    int frameX = getFrameColumn(frameIndex) * instance.frameWidth;
                    int frameY = getFrameRow(frameIndex) * instance.frameHeight;

                    instance.baseImage.copyRect(this.currentFrameBuffer, frameX, frameY, 0, 0, instance.frameWidth, instance.frameHeight, false, false);

                    uploadFrame(this.currentFrameBuffer, 0, 0, getTexture());
                }
            }
            else if (this.interpolationData != null) {
                this.interpolationData.tickAndUpload(getTexture());
            }
        }

        @Override
        public void close() {
            if (this.interpolationData != null)
                this.interpolationData.close();
        }

        /**
         * Handler class for interpolated frame generation and injection
         * <p>
         * This class is only instantiated if the {@link AnimationMetadataSection} enables {@link AnimationMetadataSection#interpolatedFrames() interpolation}
         */
        protected class InterpolationData implements AutoCloseable {
            protected final NativeImage buffer;

            public InterpolationData(int frameWidth, int frameHeight) {
                this.buffer = new NativeImage(frameWidth, frameHeight, false);
            }

            /**
             * Check and upload a newly created, interpolated frame, as necessary
             */
            protected void tickAndUpload(GpuTexture gpuTexture) {
                AnimationInfo instance = AnimationInfo.this;
                List<FrameInfo> frames = instance.frames;
                FrameInfo currentFrameInfo = frames.get(instance.currentFrame);
                int nextFrameIndex = frames.get((instance.currentFrame + 1) % frames.size()).index;

                if (currentFrameInfo.index != nextFrameIndex) {
                    float partialFrame = instance.subFrame / (float)currentFrameInfo.time;

                    for (int pixelY = 0; pixelY < GeckoLibAnimatedTexture.this.frameHeight; pixelY++) {
                        for (int pixelX = 0; pixelX < GeckoLibAnimatedTexture.this.frameWidth; pixelX++) {
                            int framePixel = getPixel(instance, currentFrameInfo.index, pixelX, pixelY);
                            int nextFramePixel = getPixel(instance, nextFrameIndex, pixelX, pixelY);

                            this.buffer.setPixel(pixelX, pixelY, ARGB.lerp(partialFrame, framePixel, nextFramePixel));
                        }
                    }

                    GeckoLibAnimatedTexture.this.uploadFrame(this.buffer, 0, 0, gpuTexture);
                }
            }

            /**
             * Get the frame-relative pixel for the given input coordinates and frame index from the root texture
             */
            protected int getPixel(AnimationInfo animationInfo, int frameIndex, int x, int y) {
                GeckoLibAnimatedTexture texture = GeckoLibAnimatedTexture.this;

                return texture.baseImage.getPixel(x + animationInfo.getFrameColumn(frameIndex) * texture.frameWidth, y + animationInfo.getFrameRow(frameIndex) * texture.frameHeight);
            }

            @Override
            public void close() {
                this.buffer.close();
            }
        }
    }

    /**
     * Container class for holding a single animation frame's data
     */
    protected record FrameInfo(int index, int time) {}
}
