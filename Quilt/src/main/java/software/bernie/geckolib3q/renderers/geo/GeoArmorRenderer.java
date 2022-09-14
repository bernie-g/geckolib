package software.bernie.geckolib3q.renderers.geo;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import org.quiltmc.loader.api.QuiltLoader;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

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
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.core.util.Color;
import software.bernie.geckolib3q.compat.PatchouliCompat;
import software.bernie.geckolib3q.geo.render.built.GeoModel;
import software.bernie.geckolib3q.model.AnimatedGeoModel;
import software.bernie.geckolib3q.util.GeoUtils;

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

	private T currentArmorItem;
	private LivingEntity entityLiving;
	private ItemStack itemStack;
	private EquipmentSlot armorSlot;
	private HumanoidModel baseModel;

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

	@Override
	public void render(GeoModel model, T animatable, float partialTicks, RenderType type, PoseStack PoseStackIn,
			MultiBufferSource renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn, int packedOverlayIn,
			float red, float green, float blue, float alpha) {
		IGeoRenderer.super.render(model, animatable, partialTicks, type, PoseStackIn, renderTypeBuffer, vertexBuilder,
				packedLightIn, packedOverlayIn, red, green, blue, alpha);
	}

	public void render(PoseStack matrices, MultiBufferSource vertexConsumers, ItemStack stack, LivingEntity entity,
			EquipmentSlot slot, int light, HumanoidModel<LivingEntity> contextModel) {
		setCurrentItem(entity, stack, slot, contextModel);
		this.render(matrices, vertexConsumers, light);
	}

	public void render(float partialTicks, PoseStack stack, VertexConsumer bufferIn, int packedLightIn) {
		stack.translate(0.0D, 1.497F, 0.0D);
		stack.scale(-1.005F, -1.0F, 1.005F);
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
}