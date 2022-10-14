package software.bernie.geckolib3.renderers.geo;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jetbrains.annotations.ApiStatus.AvailableSince;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;

import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.util.Color;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.util.EModelRenderCycle;
import software.bernie.geckolib3.util.GeckoLibUtil;
import software.bernie.geckolib3.util.IRenderCycle;

public class GeoItemRenderer<T extends Item & IAnimatable>
		implements IGeoRenderer<T>, BuiltinItemRendererRegistry.DynamicItemRenderer {
	protected static final Map<Class<? extends Item>, GeoItemRenderer> renderers = new ConcurrentHashMap<>();

	// Register a model fetcher for this renderer
	static {
		AnimationController.addModelFetcher((IAnimatable object) -> {
			if (object instanceof Item) {
				GeoItemRenderer renderer = renderers.get(object.getClass());
				return renderer == null ? null : renderer.getGeoModelProvider();
			}
			return null;
		});
	}

	protected AnimatedGeoModel<T> modelProvider;
	protected ItemStack currentItemStack;
	protected Matrix4f dispatchedMat = new Matrix4f();
	protected Matrix4f renderEarlyMat = new Matrix4f();
	protected T animatable;
	protected float widthScale;
	protected float heightScale;

	public GeoItemRenderer(AnimatedGeoModel<T> modelProvider) {
		this.modelProvider = modelProvider;
	}

	/*
	 * 0 => Normal model 1 => Magical armor overlay
	 */
	private IRenderCycle currentModelRenderCycle = EModelRenderCycle.INITIAL;

	@AvailableSince(value = "3.1.23")
	protected IRenderCycle getCurrentModelRenderCycle() {
		return this.currentModelRenderCycle;
	}

	@AvailableSince(value = "3.1.23")
	protected void setCurrentModelRenderCycle(IRenderCycle currentModelRenderCycle) {
		this.currentModelRenderCycle = currentModelRenderCycle;
	}

	public void setModel(AnimatedGeoModel<T> model) {
		this.modelProvider = model;
	}

	public static void registerItemRenderer(Item item, GeoItemRenderer renderer) {
		renderers.put(item.getClass(), renderer);
		BuiltinItemRendererRegistry.INSTANCE.register(item, renderer);
	}

	public static GeoItemRenderer getRenderer(Class<? extends Item> item) {
		return renderers.get(item);
	}

	@Override
	public AnimatedGeoModel<T> getGeoModelProvider() {
		return modelProvider;
	}

	@AvailableSince(value = "3.1.23")
	protected float getWidthScale(Object animatable2) {
		return this.widthScale;
	}

	@AvailableSince(value = "3.1.23")
	protected float getHeightScale(Object entity) {
		return this.heightScale;
	}

	@Override
	public void render(ItemStack itemStack, ItemTransforms.TransformType mode, PoseStack matrixStackIn,
			MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
		this.render((T) itemStack.getItem(), matrixStackIn, bufferIn, combinedLightIn, itemStack);
	}

	public void render(T animatable, PoseStack stack, MultiBufferSource bufferIn, int packedLightIn,
			ItemStack itemStack) {

		this.setCurrentModelRenderCycle(EModelRenderCycle.INITIAL);
		this.dispatchedMat = stack.last().pose().copy();
		this.currentItemStack = itemStack;
		AnimationEvent<T> itemEvent = new AnimationEvent<>(animatable, 0, 0, Minecraft.getInstance().getFrameTime(),
				false, Collections.singletonList(itemStack));
		modelProvider.setLivingAnimations(animatable, this.getUniqueID(animatable), itemEvent);
		stack.pushPose();
		// stack.translate(0, 0.01f, 0);
		stack.translate(0.5, 0.5, 0.5);

		RenderSystem.setShaderTexture(0, getTextureLocation(animatable));
		GeoModel model = modelProvider.getModel(modelProvider.getModelResource(animatable));
		Color renderColor = getRenderColor(animatable, 0, stack, bufferIn, null, packedLightIn);
		RenderType renderType = getRenderType(animatable, 0, stack, bufferIn, null, packedLightIn,
				getTextureLocation(animatable));
		render(model, animatable, 0, renderType, stack, bufferIn, null, packedLightIn, OverlayTexture.NO_OVERLAY,
				(float) renderColor.getRed() / 255f, (float) renderColor.getGreen() / 255f,
				(float) renderColor.getBlue() / 255f, (float) renderColor.getAlpha() / 255);
		stack.popPose();
	}
	
	@Override
	public void render(GeoModel model, T animatable, float partialTicks, RenderType type, PoseStack matrixStackIn,
			MultiBufferSource renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn, int packedOverlayIn,
			float red, float green, float blue, float alpha) {
		this.setCurrentModelRenderCycle(EModelRenderCycle.REPEATED);
		IGeoRenderer.super.render(model, animatable, partialTicks, type, matrixStackIn, renderTypeBuffer, vertexBuilder,
				packedLightIn, packedOverlayIn, red, green, blue, alpha);
	}

	@Override
	public void renderEarly(T animatable, PoseStack stackIn, float partialTicks, MultiBufferSource renderTypeBuffer,
			VertexConsumer vertexBuilder, int packedLightIn, int packedOverlayIn, float red, float green, float blue,
			float alpha) {
		renderEarlyMat = stackIn.last().pose().copy();
		this.animatable = animatable;
		IGeoRenderer.super.renderEarly(animatable, stackIn, partialTicks, renderTypeBuffer, vertexBuilder,
				packedLightIn, packedOverlayIn, red, green, blue, alpha);
		if (this.getCurrentModelRenderCycle() == EModelRenderCycle.INITIAL /* Pre-Layers */) {
			float width = this.getWidthScale(animatable);
			float height = this.getHeightScale(animatable);
			stackIn.scale(width, height, width);
		}
	}

	@Override
	public void renderRecursively(GeoBone bone, PoseStack stack, VertexConsumer bufferIn, int packedLightIn,
			int packedOverlayIn, float red, float green, float blue, float alpha) {
		if (bone.isTrackingXform()) {
			PoseStack.Pose entry = stack.last();
			Matrix4f boneMat = entry.pose().copy();

			// Model space
			Matrix4f renderEarlyMatInvert = renderEarlyMat.copy();
			renderEarlyMatInvert.invert();
			Matrix4f modelPosBoneMat = boneMat.copy();
			multiplyBackward(modelPosBoneMat, renderEarlyMatInvert);
			bone.setModelSpaceXform(modelPosBoneMat);

			// Local space
			Matrix4f dispatchedMatInvert = this.dispatchedMat.copy();
			dispatchedMatInvert.invert();
			Matrix4f localPosBoneMat = boneMat.copy();
			multiplyBackward(localPosBoneMat, dispatchedMatInvert);
			// (Offset is the only transform we may want to preserve from the dispatched mat)
			Vec3 renderOffset = this.getPositionOffset(animatable, 1.0F);
			localPosBoneMat.translate(new Vector3f((float) renderOffset.x(), (float) renderOffset.y(), (float) renderOffset.z()));
			bone.setLocalSpaceXform(localPosBoneMat);

			// World space
			// Matrix4f worldPosBoneMat = localPosBoneMat.copy();
			// worldPosBoneMat.translate(new Vec3f((float) animatable.getX(), (float) animatable.getY(), (float) animatable.getZ()));
			// bone.setWorldSpaceXform(worldPosBoneMat);
		}
		IGeoRenderer.super.renderRecursively(bone, stack, bufferIn, packedLightIn, packedOverlayIn, red, green, blue,
				alpha);
	}

	public Vec3 getPositionOffset(T entity, float tickDelta) {
		return Vec3.ZERO;
	}

	public void multiplyBackward(Matrix4f first, Matrix4f other) {
		Matrix4f copy = other.copy();
		copy.multiply(first);
		first.load(copy);
	}

	@Override
	public ResourceLocation getTextureLocation(T instance) {
		return this.modelProvider.getTextureResource(instance);
	}

	@Override
	public ResourceLocation getTextureResource(T entity) {
		return this.modelProvider.getTextureResource(entity);
	}

	@Override
	public Integer getUniqueID(T animatable) {
		return GeckoLibUtil.getIDFromStack(currentItemStack);
	}

	protected MultiBufferSource rtb = null;

	@Override
	public void setCurrentRTB(MultiBufferSource rtb) {
		this.rtb = rtb;
	}

	@Override
	public MultiBufferSource getCurrentRTB() {
		return this.rtb;
	}
}
