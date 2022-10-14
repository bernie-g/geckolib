package software.bernie.geckolib3.renderer.geo;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jetbrains.annotations.ApiStatus.AvailableSince;

import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
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
	protected float widthScale;
	protected float heightScale;
	protected Matrix4f dispatchedMat = new Matrix4f();
	protected Matrix4f renderEarlyMat = new Matrix4f();
	protected T animatable;
	protected AnimatedGeoModel<T> modelProvider;
	protected ItemStack currentItemStack;

	/*
	 * 0 => Normal model 1 => Magical armor overlay
	 */
	private IRenderCycle currentModelRenderCycle = EModelRenderCycle.INITIAL;

	@AvailableSince(value = "3.0.95")
	protected IRenderCycle getCurrentModelRenderCycle() {
		return this.currentModelRenderCycle;
	}

	@AvailableSince(value = "3.0.95")
	protected void setCurrentModelRenderCycle(IRenderCycle currentModelRenderCycle) {
		this.currentModelRenderCycle = currentModelRenderCycle;
	}

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

	public GeoItemRenderer(AnimatedGeoModel<T> modelProvider) {
		this.modelProvider = modelProvider;
	}

	public void setModel(AnimatedGeoModel<T> model) {
		this.modelProvider = model;
	}

	@AvailableSince(value = "3.0.95")
	protected float getWidthScale(T entity) {
		return this.widthScale;
	}

	@AvailableSince(value = "3.0.95")
	protected float getHeightScale(T entity) {
		return this.heightScale;
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

	@Override
	public void render(ItemStack itemStack, ModelTransformation.Mode mode, MatrixStack matrixStackIn,
			VertexConsumerProvider bufferIn, int combinedLightIn, int combinedOverlayIn) {
		this.setCurrentModelRenderCycle(EModelRenderCycle.INITIAL);
		this.render((T) itemStack.getItem(), matrixStackIn, bufferIn, combinedLightIn, itemStack);
	}

	public void render(T animatable, MatrixStack stack, VertexConsumerProvider bufferIn, int packedLightIn,
			ItemStack itemStack) {
		this.currentItemStack = itemStack;
		this.dispatchedMat = stack.peek().getModel().copy();
		AnimationEvent<T> itemEvent = new AnimationEvent<>(animatable, 0, 0,
				MinecraftClient.getInstance().getTickDelta(), false, Collections.singletonList(itemStack));
		modelProvider.setLivingAnimations(animatable, this.getUniqueID(animatable), itemEvent);
		stack.push();
		// stack.translate(0, 0.01f, 0);
		stack.translate(0.5, 0.5, 0.5);

		MinecraftClient.getInstance().getTextureManager().bindTexture(getTextureLocation(animatable));
		GeoModel model = modelProvider.getModel(modelProvider.getModelLocation(animatable));
		Color renderColor = getRenderColor(animatable, 0, stack, bufferIn, null, packedLightIn);
		RenderLayer renderType = getRenderType(animatable, 0, stack, bufferIn, null, packedLightIn,
				getTextureLocation(animatable));
		render(model, animatable, 0, renderType, stack, bufferIn, null, packedLightIn, OverlayTexture.DEFAULT_UV,
				(float) renderColor.getRed() / 255f, (float) renderColor.getGreen() / 255f,
				(float) renderColor.getBlue() / 255f, (float) renderColor.getAlpha() / 255);
		stack.pop();
	}

	@Override
	public void render(GeoModel model, T animatable, float partialTicks, RenderLayer type, MatrixStack matrixStackIn,
			VertexConsumerProvider renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn,
			int packedOverlayIn, float red, float green, float blue, float alpha) {
		this.setCurrentModelRenderCycle(EModelRenderCycle.REPEATED);
		IGeoRenderer.super.render(model, animatable, partialTicks, type, matrixStackIn, renderTypeBuffer, vertexBuilder,
				packedLightIn, packedOverlayIn, red, green, blue, alpha);
	}

	@Override
	public void renderEarly(T animatable, MatrixStack stackIn, float partialTicks,
			VertexConsumerProvider renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn,
			int packedOverlayIn, float red, float green, float blue, float alpha) {
		renderEarlyMat = stackIn.peek().getModel().copy();
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
	public void renderRecursively(GeoBone bone, MatrixStack stack, VertexConsumer bufferIn, int packedLightIn,
			int packedOverlayIn, float red, float green, float blue, float alpha) {
		if (bone.isTrackingXform()) {
			MatrixStack.Entry entry = stack.peek();
			Matrix4f boneMat = entry.getModel().copy();

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
			Vec3d renderOffset = this.getPositionOffset(animatable, 1.0F);
			localPosBoneMat.addToLastColumn(new Vec3f((float) renderOffset.getX(), (float) renderOffset.getY(), (float) renderOffset.getZ()));
			bone.setLocalSpaceXform(localPosBoneMat);

			// World space
			// Matrix4f worldPosBoneMat = localPosBoneMat.copy();
			// worldPosBoneMat.addToLastColumn(new Vec3f((float) animatable.getX(), (float) animatable.getY(), (float) animatable.getZ()));
			// bone.setWorldSpaceXform(worldPosBoneMat);
		}
		IGeoRenderer.super.renderRecursively(bone, stack, bufferIn, packedLightIn, packedOverlayIn, red, green, blue,
				alpha);
	}

	public Vec3d getPositionOffset(T entity, float tickDelta) {
		return Vec3d.ZERO;
	}

	public void multiplyBackward(Matrix4f first, Matrix4f other) {
		Matrix4f copy = other.copy();
		copy.multiply(first);
		new Matrix4f(copy);
	}

	@Override
	public Identifier getTextureLocation(T instance) {
		return this.modelProvider.getTextureLocation(instance);
	}

	@Override
	public Integer getUniqueID(T animatable) {
		return GeckoLibUtil.getIDFromStack(currentItemStack);
	}

	protected VertexConsumerProvider rtb = null;

	@Override
	public void setCurrentRTB(VertexConsumerProvider rtb) {
		this.rtb = rtb;
	}

	@Override
	public VertexConsumerProvider getCurrentRTB() {
		return this.rtb;
	}
}
