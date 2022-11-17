package software.bernie.geckolib3.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib3.cache.object.BakedGeoModel;
import software.bernie.geckolib3.cache.object.GeoBone;
import software.bernie.geckolib3.core.animatable.GeoAnimatable;
import software.bernie.geckolib3.core.animation.AnimationEvent;
import software.bernie.geckolib3.model.GeoModel;
import software.bernie.geckolib3.renderer.layer.GeoRenderLayer;
import software.bernie.geckolib3.util.RenderUtils;

import java.util.List;

/**
 * Base {@link GeoRenderer} class for rendering {@link BlockEntity Blocks} specifically.<br>
 * All blocks added to be rendered by GeckoLib should use an instance of this class.
 */
public abstract class GeoBlockRenderer<T extends BlockEntity & GeoAnimatable> implements GeoRenderer<T>, BlockEntityRenderer<T> {
	protected final GeoModel<T> model;
	protected final List<GeoRenderLayer<T>> renderLayers = new ObjectArrayList<>();

	protected T animatable;
	protected float scaleWidth = 1;
	protected float scaleHeight = 1;

	protected Matrix4f renderStartPose = new Matrix4f();
	protected Matrix4f preRenderPose = new Matrix4f();

	public GeoBlockRenderer(GeoModel<T> model) {
		this.model = model;
	}

	/**
	 * Gets the model instance for this renderer
	 */
	@Override
	public GeoModel<T> getGeoModel() {
		return this.model;
	}

	/**
	 * Gets the {@link GeoAnimatable} instance currently being rendered
	 */
	@Override
	public T getAnimatable() {
		return this.animatable;
	}

	/**
	 * Gets the id that represents the current animatable's instance for animation purposes.
	 * This is mostly useful for things like items, which have a single registered instance for all objects
	 */
	@Override
	public long getInstanceId(T animatable) {
		return animatable.getBlockPos().hashCode();
	}

	/**
	 * Returns the list of registered {@link GeoRenderLayer GeoRenderLayers} for this renderer
	 */
	@Override
	public List<GeoRenderLayer<T>> getRenderLayers() {
		return this.renderLayers;
	}

	/**
	 * Adds a {@link GeoRenderLayer} to this renderer, to be called after the main model is rendered each frame
	 */
	public GeoBlockRenderer<T> addRenderLayer(GeoRenderLayer<T> renderLayer) {
		this.renderLayers.add(renderLayer);

		return this;
	}

	/**
	 * Sets a scale override for this renderer, telling GeckoLib to pre-scale the model
	 */
	public GeoBlockRenderer<T> withScale(float scale) {
		return withScale(scale, scale);
	}

	/**
	 * Sets a scale override for this renderer, telling GeckoLib to pre-scale the model
	 */
	public GeoBlockRenderer<T> withScale(float scaleWidth, float scaleHeight) {
		this.scaleWidth = scaleWidth;
		this.scaleHeight = scaleHeight;

		return this;
	}

	/**
	 * Called before rendering the model to buffer. Allows for render modifications and preparatory
	 * work such as scaling and translating.<br>
	 * {@link PoseStack} translations made here are kept until the end of the render process
	 */
	@Override
	public void preRender(PoseStack poseStack, T animatable, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer,
						  float partialTick, int packedLight, int packedOverlay, float red, float green, float blue,
						  float alpha) {
		this.preRenderPose = poseStack.last().pose().copy();

		if (this.scaleWidth != 1 && this.scaleHeight != 1)
			poseStack.scale(this.scaleWidth, this.scaleHeight, this.scaleWidth);
	}

	@Override
	public void render(BlockEntity animatable, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource,
			int packedLight, int packedOverlay) {
		this.animatable = (T)animatable;

		defaultRender(poseStack, this.animatable, bufferSource, null, null, 0, partialTick, packedLight);
	}

	/**
	 * The actual render method that subtype renderers should override to handle their specific rendering tasks.<br>
	 * {@link GeoRenderer#preRender} has already been called by this stage, and {@link GeoRenderer#postRender} will be called directly after
	 */
	@Override
	public void actuallyRender(PoseStack poseStack, T animatable, BakedGeoModel model, RenderType renderType,
							   MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight,
							   int packedOverlay, float red, float green, float blue, float alpha) {
		poseStack.pushPose();

		this.renderStartPose = poseStack.last().pose().copy();
		AnimationEvent<T> animationEvent = new AnimationEvent<T>(animatable, 0, 0, partialTick, false);
		long instanceId = getInstanceId(animatable);

		this.model.addAdditionalEventData(animatable, instanceId, animationEvent::setData);
		poseStack.translate(0, 0.01f, 0);
		poseStack.translate(0.5, 0, 0.5);
		rotateBlock(getFacing(animatable), poseStack);
		this.model.handleAnimations(animatable, instanceId, animationEvent);

		RenderSystem.setShaderTexture(0, getTextureLocation(animatable));
		GeoRenderer.super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, partialTick,
				packedLight, packedOverlay, red, green, blue, alpha);
		poseStack.popPose();
	}

	/**
	 * Renders the provided {@link GeoBone} and its associated child bones
	 */
	@Override
	public void renderRecursively(PoseStack poseStack, GeoBone bone, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight,
								  int packedOverlay, float red, float green, float blue, float alpha) {
		if (bone.isTrackingXform()) {
			Matrix4f poseState = poseStack.last().pose().copy();
			Matrix4f localMatrix = RenderUtils.invertAndMultiplyMatrices(poseState, this.renderStartPose);
			BlockPos pos = this.animatable.getBlockPos();

			bone.setModelSpaceMatrix(RenderUtils.invertAndMultiplyMatrices(poseState, this.preRenderPose));
			bone.setLocalSpaceMatrix(localMatrix);

			Matrix4f worldState = localMatrix.copy();

			worldState.translate(new Vector3f(pos.getX(), pos.getY(), pos.getZ()));
			bone.setWorldSpaceMatrix(worldState);
		}

		GeoRenderer.super.renderRecursively(poseStack, bone, bufferSource, buffer, partialTick, packedLight, packedOverlay, red, green, blue,
				alpha);
	}

	/**
	 * Rotate the {@link PoseStack} based on the determined {@link Direction} the block is facing
	 */
	protected void rotateBlock(Direction facing, PoseStack poseStack) {
		switch (facing) {
			case SOUTH -> poseStack.mulPose(Vector3f.YP.rotationDegrees(180));
			case WEST -> poseStack.mulPose(Vector3f.YP.rotationDegrees(90));
			case NORTH -> poseStack.mulPose(Vector3f.YP.rotationDegrees(0));
			case EAST -> poseStack.mulPose(Vector3f.YP.rotationDegrees(270));
			case UP -> poseStack.mulPose(Vector3f.XP.rotationDegrees(90));
			case DOWN -> poseStack.mulPose(Vector3f.XN.rotationDegrees(90));
		}
	}

	/**
	 * Attempt to extract a direction from the block so that the model can be oriented correctly
	 */
	protected Direction getFacing(T block) {
		BlockState blockState = block.getBlockState();
		if (blockState.hasProperty(HorizontalDirectionalBlock.FACING)) {
			return blockState.getValue(HorizontalDirectionalBlock.FACING);
		}
		else if (blockState.hasProperty(DirectionalBlock.FACING)) {
			return blockState.getValue(DirectionalBlock.FACING);
		}
		else {
			return Direction.NORTH;
		}
	}
}
