package software.bernie.geckolib3.renderers.geo;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import org.jetbrains.annotations.ApiStatus.AvailableSince;
import org.quiltmc.loader.api.QuiltLoader;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;

import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib3.compat.PatchouliCompat;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.core.util.Color;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.util.EModelRenderCycle;
import software.bernie.geckolib3.util.GeoUtils;
import software.bernie.geckolib3.util.IRenderCycle;

public class GeoArmorRenderer<T extends ArmorItem & IAnimatable> implements IGeoRenderer<T>, ArmorRenderer {
	public static final Map<Class<? extends ArmorItem>, GeoArmorRenderer> renderers = new ConcurrentHashMap<>();

	static {
		AnimationController.addModelFetcher((IAnimatable object) -> {
			if (object instanceof ArmorItem) {
				GeoArmorRenderer renderer = renderers.get(object.getClass());
				return renderer == null ? null : renderer.getGeoModelProvider();
			}
			return null;
		});
	}

	protected AnimatedGeoModel<T> modelProvider;
	protected ItemStack currentItemStack;

	// Set these to the names of your armor's bones, or null if you aren't using
	// them
	public String headBone = "armorHead";
	public String bodyBone = "armorBody";
	public String rightArmBone = "armorRightArm";
	public String leftArmBone = "armorLeftArm";
	public String rightLegBone = "armorRightLeg";
	public String leftLegBone = "armorLeftLeg";
	public String rightBootBone = "armorRightBoot";
	public String leftBootBone = "armorLeftBoot";

	protected T currentArmorItem;
	protected LivingEntity entityLiving;
	protected ItemStack itemStack;
	protected EquipmentSlot armorSlot;
	protected HumanoidModel baseModel;
	protected float widthScale;
	protected float heightScale;
	protected Matrix4f dispatchedMat = new Matrix4f();
	protected Matrix4f renderEarlyMat = new Matrix4f();

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

	public GeoArmorRenderer(AnimatedGeoModel<T> modelProvider) {
		this.modelProvider = modelProvider;
	}

	public void setModel(AnimatedGeoModel<T> model) {
		this.modelProvider = model;
	}

	public static <E extends Entity> void registerArmorRenderer(GeoArmorRenderer renderer, Item... items) {
		for (Item item : items) {
			registerArmorRenderer(renderer, item);
		}
	}

	public static <E extends Entity> void registerArmorRenderer(GeoArmorRenderer renderer, Item item) {
		if (item instanceof ArmorItem) {
			renderers.put((Class<? extends ArmorItem>) item.getClass(), renderer);
			ArmorRenderer.register(renderer, item);
		}
	}

	public static GeoArmorRenderer getRenderer(Class<? extends ArmorItem> item) {
		final GeoArmorRenderer renderer = renderers.get(item);
		if (renderer == null) {
			throw new IllegalArgumentException("Renderer not registered for item " + item);
		}
		return renderer;
	}

	public void render(PoseStack matrices, MultiBufferSource vertexConsumers, ItemStack stack, LivingEntity entity,
			EquipmentSlot slot, int light, HumanoidModel<LivingEntity> contextModel) {
		setCurrentItem(entity, stack, slot, contextModel);
		this.render(matrices, vertexConsumers, light);
	}

	public void render(float partialTicks, PoseStack stack, VertexConsumer bufferIn, int packedLightIn) {
		stack.translate(0.0D, 1.497F, 0.0D);
		stack.scale(-1.005F, -1.0F, 1.005F);
		this.setCurrentModelRenderCycle(EModelRenderCycle.INITIAL);
		this.dispatchedMat = stack.last().pose().copy();
		GeoModel model = modelProvider.getModel(modelProvider.getModelResource(currentArmorItem));

		AnimationEvent<T> itemEvent = new AnimationEvent<T>(this.currentArmorItem, 0, 0,
				Minecraft.getInstance().getFrameTime(), false,
				Arrays.asList(this.itemStack, this.entityLiving, this.armorSlot));
		modelProvider.setLivingAnimations(currentArmorItem, this.getUniqueID(this.currentArmorItem), itemEvent);

		this.fitToBiped();
		this.applySlot(armorSlot);
		stack.pushPose();
		RenderSystem.setShaderTexture(0, getTextureLocation(currentArmorItem));
		Color renderColor = getRenderColor(currentArmorItem, partialTicks, stack, null, bufferIn, packedLightIn);
		RenderType renderType = getRenderType(currentArmorItem, partialTicks, stack, null, bufferIn, packedLightIn,
				getTextureLocation(currentArmorItem));
		render(model, currentArmorItem, partialTicks, renderType, stack, null, bufferIn, packedLightIn,
				OverlayTexture.NO_OVERLAY, (float) renderColor.getRed() / 255f, (float) renderColor.getGreen() / 255f,
				(float) renderColor.getBlue() / 255f, (float) renderColor.getAlpha() / 255);

		if (QuiltLoader.isModLoaded("patchouli")) {
			PatchouliCompat.patchouliLoaded(stack);
		}
		stack.popPose();
		stack.scale(-1.005F, -1.0F, 1.005F);
		stack.translate(0.0D, -1.497F, 0.0D);
	}

	public void render(PoseStack stack, MultiBufferSource bufferIn, int packedLightIn) {
		stack.translate(0.0D, 1.497F, 0.0D);
		stack.scale(-1.005F, -1.0F, 1.005F);
		this.setCurrentModelRenderCycle(EModelRenderCycle.INITIAL);
		this.dispatchedMat = stack.last().pose().copy();
		GeoModel model = modelProvider.getModel(modelProvider.getModelResource(currentArmorItem));

		AnimationEvent<T> itemEvent = new AnimationEvent<T>(this.currentArmorItem, 0, 0,
				Minecraft.getInstance().getFrameTime(), false,
				Arrays.asList(this.itemStack, this.entityLiving, this.armorSlot));
		modelProvider.setLivingAnimations(currentArmorItem, this.getUniqueID(this.currentArmorItem), itemEvent);

		this.fitToBiped();
		this.applySlot(armorSlot);
		stack.pushPose();
		RenderSystem.setShaderTexture(0, getTextureLocation(currentArmorItem));
		Color renderColor = getRenderColor(currentArmorItem, 0, stack, bufferIn, null, packedLightIn);
		RenderType renderType = getRenderType(currentArmorItem, 0, stack, bufferIn, null, packedLightIn,
				getTextureLocation(currentArmorItem));
		render(model, currentArmorItem, 0, renderType, stack, bufferIn, null, packedLightIn, OverlayTexture.NO_OVERLAY,
				(float) renderColor.getRed() / 255f, (float) renderColor.getGreen() / 255f,
				(float) renderColor.getBlue() / 255f, (float) renderColor.getAlpha() / 255);

		if (QuiltLoader.isModLoaded("patchouli")) {
			PatchouliCompat.patchouliLoaded(stack);
		}
		stack.popPose();
		stack.scale(-1.005F, -1.0F, 1.005F);
		stack.translate(0.0D, -1.497F, 0.0D);
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
		this.currentArmorItem = animatable;
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
			Vec3 renderOffset = this.getPositionOffset(currentArmorItem, 1.0F);
			localPosBoneMat.translate(new Vector3f((float) renderOffset.x(), (float) renderOffset.y(), (float) renderOffset.z()));
			bone.setLocalSpaceXform(localPosBoneMat);

			// World space
//			 Matrix4f worldPosBoneMat = localPosBoneMat.copy();
//			 worldPosBoneMat.translate(new Vector3f((float) animatable.getX(), (float) animatable.getY(), (float) animatable.getZ()));
//			 bone.setWorldSpaceXform(worldPosBoneMat);
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

	private void fitToBiped() {
		if (this.headBone != null) {
			IBone headBone = this.modelProvider.getBone(this.headBone);
			GeoUtils.copyRotations(baseModel.head, headBone);
			headBone.setPositionX(baseModel.head.x);
			headBone.setPositionY(-baseModel.head.y);
			headBone.setPositionZ(baseModel.head.z);
		}

		if (this.bodyBone != null) {
			IBone bodyBone = this.modelProvider.getBone(this.bodyBone);
			GeoUtils.copyRotations(baseModel.body, bodyBone);
			bodyBone.setPositionX(baseModel.body.x);
			bodyBone.setPositionY(-baseModel.body.y);
			bodyBone.setPositionZ(baseModel.body.z);
		}
		if (this.rightArmBone != null) {
			IBone rightArmBone = this.modelProvider.getBone(this.rightArmBone);
			GeoUtils.copyRotations(baseModel.rightArm, rightArmBone);
			rightArmBone.setPositionX(baseModel.rightArm.x + 5);
			rightArmBone.setPositionY(2 - baseModel.rightArm.y);
			rightArmBone.setPositionZ(baseModel.rightArm.z);
		}

		if (this.leftArmBone != null) {
			IBone leftArmBone = this.modelProvider.getBone(this.leftArmBone);
			GeoUtils.copyRotations(baseModel.leftArm, leftArmBone);
			leftArmBone.setPositionX(baseModel.leftArm.x - 5);
			leftArmBone.setPositionY(2 - baseModel.leftArm.y);
			leftArmBone.setPositionZ(baseModel.leftArm.z);
		}
		if (this.rightLegBone != null) {
			IBone rightLegBone = this.modelProvider.getBone(this.rightLegBone);
			GeoUtils.copyRotations(baseModel.rightLeg, rightLegBone);
			rightLegBone.setPositionX(baseModel.rightLeg.x + 2);
			rightLegBone.setPositionY(12 - baseModel.rightLeg.y);
			rightLegBone.setPositionZ(baseModel.rightLeg.z);
			if (this.rightBootBone != null) {
				IBone rightBootBone = this.modelProvider.getBone(this.rightBootBone);
				GeoUtils.copyRotations(baseModel.rightLeg, rightBootBone);
				rightBootBone.setPositionX(baseModel.rightLeg.x + 2);
				rightBootBone.setPositionY(12 - baseModel.rightLeg.y);
				rightBootBone.setPositionZ(baseModel.rightLeg.z);
			}
		}
		if (this.leftLegBone != null) {
			IBone leftLegBone = this.modelProvider.getBone(this.leftLegBone);
			GeoUtils.copyRotations(baseModel.leftLeg, leftLegBone);
			leftLegBone.setPositionX(baseModel.leftLeg.x - 2);
			leftLegBone.setPositionY(12 - baseModel.leftLeg.y);
			leftLegBone.setPositionZ(baseModel.leftLeg.z);
			if (this.leftBootBone != null) {
				IBone leftBootBone = this.modelProvider.getBone(this.leftBootBone);
				GeoUtils.copyRotations(baseModel.leftLeg, leftBootBone);
				leftBootBone.setPositionX(baseModel.leftLeg.x - 2);
				leftBootBone.setPositionY(12 - baseModel.leftLeg.y);
				leftBootBone.setPositionZ(baseModel.leftLeg.z);
			}
		}
	}

	@Override
	public AnimatedGeoModel<T> getGeoModelProvider() {
		return this.modelProvider;
	}

	@AvailableSince(value = "3.1.23")
	protected float getWidthScale(T entity) {
		return this.widthScale;
	}

	@AvailableSince(value = "3.1.23")
	protected float getHeightScale(T entity) {
		return this.heightScale;
	}

	@Override
	public ResourceLocation getTextureLocation(T instance) {
		return this.modelProvider.getTextureResource(instance);
	}

	@Override
	public ResourceLocation getTextureResource(T entity) {
		return this.modelProvider.getTextureResource(entity);
	}

	/**
	 * Everything after this point needs to be called every frame before rendering
	 */
	public GeoArmorRenderer<T> setCurrentItem(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlot armorSlot,
			HumanoidModel model) {
		this.entityLiving = entityLiving;
		this.itemStack = itemStack;
		this.armorSlot = armorSlot;
		this.currentArmorItem = (T) itemStack.getItem();
		this.baseModel = model;

		return this;
	}

	@SuppressWarnings("incomplete-switch")
	public GeoArmorRenderer<T> applySlot(EquipmentSlot boneSlot) {
		modelProvider.getModel(modelProvider.getModelResource(currentArmorItem));

		IBone headBone = this.getAndHideBone(this.headBone);
		IBone bodyBone = this.getAndHideBone(this.bodyBone);
		IBone rightArmBone = this.getAndHideBone(this.rightArmBone);
		IBone leftArmBone = this.getAndHideBone(this.leftArmBone);
		IBone rightLegBone = this.getAndHideBone(this.rightLegBone);
		IBone leftLegBone = this.getAndHideBone(this.leftLegBone);
		IBone rightBootBone = this.getAndHideBone(this.rightBootBone);
		IBone leftBootBone = this.getAndHideBone(this.leftBootBone);

		switch (boneSlot) {
		case HEAD:
			if (headBone != null)
				headBone.setHidden(false);
			break;
		case CHEST:
			if (bodyBone != null)
				bodyBone.setHidden(false);
			if (rightArmBone != null)
				rightArmBone.setHidden(false);
			if (leftArmBone != null)
				leftArmBone.setHidden(false);
			break;
		case LEGS:
			if (rightLegBone != null)
				rightLegBone.setHidden(false);
			if (leftLegBone != null)
				leftLegBone.setHidden(false);
			break;
		case FEET:
			if (rightBootBone != null)
				rightBootBone.setHidden(false);
			if (leftBootBone != null)
				leftBootBone.setHidden(false);
			break;
		}
		return this;
	}

	protected IBone getAndHideBone(String boneName) {
		if (boneName != null) {
			final IBone bone = this.modelProvider.getBone(boneName);
			bone.setHidden(true);
			return bone;
		}
		return null;
	}

	@Override
	public Integer getUniqueID(T animatable) {
		return Objects.hash(this.armorSlot, itemStack.getItem(), itemStack.getCount(),
				itemStack.hasTag() ? itemStack.getTag().toString() : 1, this.entityLiving.getUUID().toString());
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