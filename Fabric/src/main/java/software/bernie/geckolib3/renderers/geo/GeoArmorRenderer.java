package software.bernie.geckolib3.renderers.geo;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.ApiStatus.AvailableSince;

import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
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

public abstract class GeoArmorRenderer<T extends ArmorItem & IAnimatable> implements IGeoRenderer<T>, ArmorRenderer {
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
	protected BipedEntityModel baseModel;
	protected float widthScale = 1;
	protected float heightScale = 1;
	protected Matrix4f dispatchedMat = new Matrix4f();
	protected Matrix4f renderEarlyMat = new Matrix4f();

	/*
	 * 0 => Normal model 1 => Magical armor overlay
	 */
	private IRenderCycle currentModelRenderCycle = EModelRenderCycle.INITIAL;

	@AvailableSince(value = "3.0.65")
	@Override
	@Nonnull
	public IRenderCycle getCurrentModelRenderCycle() {
		return this.currentModelRenderCycle;
	}

	@AvailableSince(value = "3.0.65")
	@Override
	public void setCurrentModelRenderCycle(IRenderCycle currentModelRenderCycle) {
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

	public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, ItemStack stack,
			LivingEntity entity, EquipmentSlot slot, int light, BipedEntityModel<LivingEntity> contextModel) {
		setCurrentItem(entity, stack, slot, contextModel);
		this.render(matrices, vertexConsumers, light);
	}

	public void render(float partialTicks, MatrixStack stack, VertexConsumer bufferIn, int packedLightIn) {
		stack.translate(0.0D, 1.497F, 0.0D);
		stack.scale(-1.005F, -1.0F, 1.005F);
		this.dispatchedMat = stack.peek().getPositionMatrix().copy();
		this.setCurrentModelRenderCycle(EModelRenderCycle.INITIAL);
		GeoModel model = modelProvider.getModel(modelProvider.getModelLocation(currentArmorItem));

		AnimationEvent<T> itemEvent = new AnimationEvent<T>(this.currentArmorItem, 0, 0,
				MinecraftClient.getInstance().getTickDelta(), false,
				Arrays.asList(this.itemStack, this.entityLiving, this.armorSlot));
		modelProvider.setLivingAnimations(currentArmorItem, this.getUniqueID(this.currentArmorItem), itemEvent);

		this.fitToBiped();
		this.applySlot(armorSlot);
		stack.push();
		MinecraftClient.getInstance().getTextureManager().bindTexture(getTextureLocation(currentArmorItem));
		Color renderColor = getRenderColor(currentArmorItem, partialTicks, stack, null, bufferIn, packedLightIn);
		RenderLayer renderType = getRenderType(currentArmorItem, partialTicks, stack, null, bufferIn, packedLightIn,
				getTextureLocation(currentArmorItem));
		render(model, currentArmorItem, partialTicks, renderType, stack, null, bufferIn, packedLightIn,
				OverlayTexture.DEFAULT_UV, (float) renderColor.getRed() / 255f, (float) renderColor.getGreen() / 255f,
				(float) renderColor.getBlue() / 255f, (float) renderColor.getAlpha() / 255);

		if (FabricLoader.getInstance().isModLoaded("patchouli")) {
			PatchouliCompat.patchouliLoaded(stack);
		}
		stack.pop();
		stack.scale(-1.005F, -1.0F, 1.005F);
		stack.translate(0.0D, -1.497F, 0.0D);
	}

	public void render(MatrixStack stack, VertexConsumerProvider bufferIn, int packedLightIn) {
		stack.translate(0.0D, 1.497F, 0.0D);
		stack.scale(-1.005F, -1.0F, 1.005F);
		this.setCurrentModelRenderCycle(EModelRenderCycle.INITIAL);
		this.dispatchedMat = stack.peek().getPositionMatrix().copy();
		GeoModel model = modelProvider.getModel(modelProvider.getModelLocation(currentArmorItem));

		AnimationEvent<T> itemEvent = new AnimationEvent<T>(this.currentArmorItem, 0, 0,
				MinecraftClient.getInstance().getTickDelta(), false,
				Arrays.asList(this.itemStack, this.entityLiving, this.armorSlot));
		modelProvider.setLivingAnimations(currentArmorItem, this.getUniqueID(this.currentArmorItem), itemEvent);

		this.fitToBiped();
		this.applySlot(armorSlot);
		stack.push();
		MinecraftClient.getInstance().getTextureManager().bindTexture(getTextureLocation(currentArmorItem));
		Color renderColor = getRenderColor(currentArmorItem, 0, stack, bufferIn, null, packedLightIn);
		RenderLayer renderType = getRenderType(currentArmorItem, 0, stack, bufferIn, null, packedLightIn,
				getTextureLocation(currentArmorItem));
		render(model, currentArmorItem, 0, renderType, stack, bufferIn, null, packedLightIn, OverlayTexture.DEFAULT_UV,
				(float) renderColor.getRed() / 255f, (float) renderColor.getGreen() / 255f,
				(float) renderColor.getBlue() / 255f, (float) renderColor.getAlpha() / 255);

		if (FabricLoader.getInstance().isModLoaded("patchouli")) {
			PatchouliCompat.patchouliLoaded(stack);
		}
		stack.pop();
		stack.scale(-1.005F, -1.0F, 1.005F);
		stack.translate(0.0D, -1.497F, 0.0D);
	}

	@Override
	public void renderEarly(T animatable, MatrixStack stackIn, float partialTicks,
			VertexConsumerProvider renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn,
			int packedOverlayIn, float red, float green, float blue, float alpha) {
		renderEarlyMat = stackIn.peek().getPositionMatrix().copy();
		this.currentArmorItem = animatable;
		IGeoRenderer.super.renderEarly(animatable, stackIn, partialTicks, renderTypeBuffer, vertexBuilder,
				packedLightIn, packedOverlayIn, red, green, blue, alpha);
	}

	@Override
	public void renderRecursively(GeoBone bone, MatrixStack stack, VertexConsumer bufferIn, int packedLightIn,
			int packedOverlayIn, float red, float green, float blue, float alpha) {
		if (bone.isTrackingXform()) {
			MatrixStack.Entry entry = stack.peek();
			Matrix4f boneMat = entry.getPositionMatrix().copy();

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
			// (Offset is the only transform we may want to preserve from the dispatched
			// mat)
			Vec3d renderOffset = this.getPositionOffset(currentArmorItem, 1.0F);
			localPosBoneMat.addToLastColumn(
					new Vec3f((float) renderOffset.getX(), (float) renderOffset.getY(), (float) renderOffset.getZ()));
			bone.setLocalSpaceXform(localPosBoneMat);

			// World space
			// Matrix4f worldPosBoneMat = localPosBoneMat.copy();
			// worldPosBoneMat.addToLastColumn(new Vec3f((float) animatable.getX(), (float)
			// animatable.getY(), (float) animatable.getZ()));
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
		first.load(copy);
	}

	private void fitToBiped() {
		if (!(this.entityLiving instanceof ArmorStandEntity)) {
			if (this.headBone != null) {
				IBone headBone = this.modelProvider.getBone(this.headBone);
				GeoUtils.copyRotations(baseModel.head, headBone);
				headBone.setPositionX(baseModel.head.pivotX);
				headBone.setPositionY(-baseModel.head.pivotY);
				headBone.setPositionZ(baseModel.head.pivotZ);
			}

			if (this.bodyBone != null) {
				IBone bodyBone = this.modelProvider.getBone(this.bodyBone);
				GeoUtils.copyRotations(baseModel.body, bodyBone);
				bodyBone.setPositionX(baseModel.body.pivotX);
				bodyBone.setPositionY(-baseModel.body.pivotY);
				bodyBone.setPositionZ(baseModel.body.pivotZ);
			}
			if (this.rightArmBone != null) {
				IBone rightArmBone = this.modelProvider.getBone(this.rightArmBone);
				GeoUtils.copyRotations(baseModel.rightArm, rightArmBone);
				rightArmBone.setPositionX(baseModel.rightArm.pivotX + 5);
				rightArmBone.setPositionY(2 - baseModel.rightArm.pivotY);
				rightArmBone.setPositionZ(baseModel.rightArm.pivotZ);
			}

			if (this.leftArmBone != null) {
				IBone leftArmBone = this.modelProvider.getBone(this.leftArmBone);
				GeoUtils.copyRotations(baseModel.leftArm, leftArmBone);
				leftArmBone.setPositionX(baseModel.leftArm.pivotX - 5);
				leftArmBone.setPositionY(2 - baseModel.leftArm.pivotY);
				leftArmBone.setPositionZ(baseModel.leftArm.pivotZ);
			}
			if (this.rightLegBone != null) {
				IBone rightLegBone = this.modelProvider.getBone(this.rightLegBone);
				GeoUtils.copyRotations(baseModel.rightLeg, rightLegBone);
				rightLegBone.setPositionX(baseModel.rightLeg.pivotX + 2);
				rightLegBone.setPositionY(12 - baseModel.rightLeg.pivotY);
				rightLegBone.setPositionZ(baseModel.rightLeg.pivotZ);
				if (this.rightBootBone != null) {
					IBone rightBootBone = this.modelProvider.getBone(this.rightBootBone);
					GeoUtils.copyRotations(baseModel.rightLeg, rightBootBone);
					rightBootBone.setPositionX(baseModel.rightLeg.pivotX + 2);
					rightBootBone.setPositionY(12 - baseModel.rightLeg.pivotY);
					rightBootBone.setPositionZ(baseModel.rightLeg.pivotZ);
				}
			}
			if (this.leftLegBone != null) {
				IBone leftLegBone = this.modelProvider.getBone(this.leftLegBone);
				GeoUtils.copyRotations(baseModel.leftLeg, leftLegBone);
				leftLegBone.setPositionX(baseModel.leftLeg.pivotX - 2);
				leftLegBone.setPositionY(12 - baseModel.leftLeg.pivotY);
				leftLegBone.setPositionZ(baseModel.leftLeg.pivotZ);
				if (this.leftBootBone != null) {
					IBone leftBootBone = this.modelProvider.getBone(this.leftBootBone);
					GeoUtils.copyRotations(baseModel.leftLeg, leftBootBone);
					leftBootBone.setPositionX(baseModel.leftLeg.pivotX - 2);
					leftBootBone.setPositionY(12 - baseModel.leftLeg.pivotY);
					leftBootBone.setPositionZ(baseModel.leftLeg.pivotZ);
				}
			}
		}
	}

	@Override
	public AnimatedGeoModel<T> getGeoModelProvider() {
		return this.modelProvider;
	}

	@AvailableSince(value = "3.0.65")
	@Override
	public float getWidthScale(T animatable2) {
		return this.widthScale;
	}

	@AvailableSince(value = "3.0.65")
	@Override
	public float getHeightScale(T entity) {
		return this.heightScale;
	}

	@Override
	public Identifier getTextureLocation(T instance) {
		return this.modelProvider.getTextureLocation(instance);
	}

	/**
	 * Everything after this point needs to be called every frame before rendering
	 */
	public GeoArmorRenderer<T> setCurrentItem(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlot armorSlot,
			BipedEntityModel model) {
		this.entityLiving = entityLiving;
		this.itemStack = itemStack;
		this.armorSlot = armorSlot;
		this.currentArmorItem = (T) itemStack.getItem();
		this.baseModel = model;

		return this;
	}

	@SuppressWarnings("incomplete-switch")
	public GeoArmorRenderer<T> applySlot(EquipmentSlot boneSlot) {
		modelProvider.getModel(modelProvider.getModelLocation(currentArmorItem));

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
				itemStack.hasNbt() ? itemStack.getNbt().toString() : 1, this.entityLiving.getUuid().toString());
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