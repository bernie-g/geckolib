package software.bernie.geckolib3.renderers.geo;

import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import com.google.common.collect.Maps;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPart.Cuboid;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation.Mode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.DyeableArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3f;
import software.bernie.geckolib3.ArmorRenderingRegistryImpl;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.geo.render.built.GeoCube;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;

/**
 * @author DerToaster98 Copyright (c) 30.03.2022 Developed by DerToaster98
 *         GitHub: https://github.com/DerToaster98
 * 
 *         Purpose of this class: This class is a extended version of
 *         {@code GeoEnttiyRenderer}. It automates the process of rendering
 *         items at hand bones as well as standard armor at certain bones. The
 *         model must feature a few special bones for this to work.
 */
public abstract class ExtendedGeoEntityRenderer<T extends LivingEntity & IAnimatable> extends GeoEntityRenderer<T> {

	static enum EModelRenderCycle {
		INITIAL, REPEATED, SPECIAL /* For special use by the user */
	}

	protected float widthScale;
	protected float heightScale;

	/*
	 * 0 => Normal model 1 => Magical armor overlay
	 */
	private EModelRenderCycle currentModelRenderCycle = EModelRenderCycle.INITIAL;

	protected EModelRenderCycle getCurrentModelRenderCycle() {
		return this.currentModelRenderCycle;
	}

	protected void setCurrentModelRenderCycle(EModelRenderCycle currentModelRenderCycle) {
		this.currentModelRenderCycle = currentModelRenderCycle;
	}

	protected ExtendedGeoEntityRenderer(EntityRendererFactory.Context renderManager,
			AnimatedGeoModel<T> modelProvider) {
		this(renderManager, modelProvider, 1F, 1F, 0);
	}

	protected void bindTexture(Identifier textureLocation) {
		MinecraftClient.getInstance().getTextureManager().bindTexture(textureLocation);
	}

	protected ExtendedGeoEntityRenderer(EntityRendererFactory.Context renderManager, AnimatedGeoModel<T> modelProvider,
			float widthScale, float heightScale, float shadowSize) {
		super(renderManager, modelProvider);
		this.shadowRadius = shadowSize;
		this.widthScale = widthScale;
		this.heightScale = heightScale;
	}

	// Entrypoint for rendering, calls everything else
	@Override
	public void render(T entity, float entityYaw, float partialTicks, MatrixStack stack,
			VertexConsumerProvider bufferIn, int packedLightIn) {
		this.setCurrentModelRenderCycle(EModelRenderCycle.INITIAL);
		super.render(entity, entityYaw, partialTicks, stack, bufferIn, packedLightIn);
	}

	// Rendercall to render the model itself
	@Override
	public void render(GeoModel model, T animatable, float partialTicks, RenderLayer type, MatrixStack matrixStackIn,
			VertexConsumerProvider renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn,
			int packedOverlayIn, float red, float green, float blue, float alpha) {
		super.render(model, animatable, partialTicks, type, matrixStackIn, renderTypeBuffer, vertexBuilder,
				packedLightIn, packedOverlayIn, red, green, blue, alpha);
		this.setCurrentModelRenderCycle(EModelRenderCycle.REPEATED);
	}

	protected float getWidthScale(T entity) {
		return this.widthScale;
	}

	protected float getHeightScale(T entity) {
		return this.heightScale;
	}

	@Override
	public void renderEarly(T animatable, MatrixStack stackIn, float ticks, VertexConsumerProvider renderTypeBuffer,
			VertexConsumer vertexBuilder, int packedLightIn, int packedOverlayIn, float red, float green, float blue,
			float partialTicks) {
		this.rtb = renderTypeBuffer;
		super.renderEarly(animatable, stackIn, ticks, renderTypeBuffer, vertexBuilder, packedLightIn, packedOverlayIn,
				red, green, blue, partialTicks);
		if (this.getCurrentModelRenderCycle() == EModelRenderCycle.INITIAL /* Pre-Layers */) {
			float width = this.getWidthScale(animatable);
			float height = this.getHeightScale(animatable);
			stackIn.scale(width, height, width);
		}
	}

	@Override
	public Identifier getTextureLocation(T entity) {
		return this.modelProvider.getTextureLocation(entity);
	}

	private T currentEntityBeingRendered;
	private VertexConsumerProvider rtb;

	@Override
	public void renderLate(T animatable, MatrixStack stackIn, float ticks, VertexConsumerProvider renderTypeBuffer,
			VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue,
			float partialTicks) {
		super.renderLate(animatable, stackIn, ticks, renderTypeBuffer, bufferIn, packedLightIn, packedOverlayIn, red,
				green, blue, partialTicks);
		this.currentEntityBeingRendered = animatable;
		this.currentVertexBuilderInUse = bufferIn;
		this.currentPartialTicks = partialTicks;
	}

	protected final BipedEntityModel<LivingEntity> DEFAULT_BIPED_ARMOR_MODEL_INNER = new BipedEntityModel<LivingEntity>(
			MinecraftClient.getInstance().getEntityModelLoader().getModelPart(EntityModelLayers.PLAYER_INNER_ARMOR));
	protected final BipedEntityModel<LivingEntity> DEFAULT_BIPED_ARMOR_MODEL_OUTER = new BipedEntityModel<LivingEntity>(
			MinecraftClient.getInstance().getEntityModelLoader().getModelPart(EntityModelLayers.PLAYER_OUTER_ARMOR));

	protected abstract boolean isArmorBone(final GeoBone bone);

	private VertexConsumer currentVertexBuilderInUse;
	private float currentPartialTicks;

	protected void moveAndRotateMatrixToMatchBone(MatrixStack stack, GeoBone bone) {
		// First, let's move our render position to the pivot point...
		stack.translate(bone.getPivotX() / 16, bone.getPivotY() / 16, bone.getPositionZ() / 16);

		stack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(bone.getRotationX()));
		stack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(bone.getRotationY()));
		stack.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(bone.getRotationZ()));
	}

	protected void handleArmorRenderingForBone(GeoBone bone, MatrixStack stack, VertexConsumer bufferIn,
			int packedLightIn, int packedOverlayIn, Identifier currentTexture) {
		final ItemStack armorForBone = this.getArmorForBone(bone.getName(), currentEntityBeingRendered);
		final EquipmentSlot boneSlot = this.getEquipmentSlotForArmorBone(bone.getName(), currentEntityBeingRendered);
		if (armorForBone != null && armorForBone.getItem() instanceof ArmorItem
				&& armorForBone.getItem() instanceof ArmorItem && boneSlot != null) {
			final ArmorItem armorItem = (ArmorItem) armorForBone.getItem();
			final BipedEntityModel<?> armorModel = (BipedEntityModel<?>) ArmorRenderingRegistryImpl
					.getArmorModel(currentEntityBeingRendered, armorForBone, boneSlot,
							boneSlot == EquipmentSlot.LEGS ? DEFAULT_BIPED_ARMOR_MODEL_INNER
									: DEFAULT_BIPED_ARMOR_MODEL_OUTER);
			if (armorModel != null) {
				ModelPart sourceLimb = this.getArmorPartForBone(bone.getName(), armorModel);
				List<Cuboid> cubeList = sourceLimb.cuboids;
				if (sourceLimb != null && cubeList != null && !cubeList.isEmpty()) {
					// IMPORTANT: The first cube is used to define the armor part!!
					this.prepareArmorPositionAndScale(bone, cubeList, sourceLimb, stack);
					stack.scale(-1, -1, 1);

					stack.push();

					Identifier armorResource = this.getArmorResource(currentEntityBeingRendered, armorForBone,
							boneSlot, null);
					this.bindTexture(armorResource);

					this.renderArmorOfItem(armorItem, armorForBone, boneSlot, armorResource, sourceLimb, stack,
							packedLightIn, packedOverlayIn);

					stack.pop();

					this.bindTexture(currentTexture);
					bufferIn = rtb.getBuffer(RenderLayer.getEntityTranslucent(currentTexture));
				}
			}
		}
	}

	protected void renderArmorOfItem(ArmorItem armorItem, ItemStack armorForBone, EquipmentSlot boneSlot,
			Identifier armorResource, ModelPart sourceLimb, MatrixStack stack, int packedLightIn, int packedOverlayIn) {
		if (armorItem instanceof DyeableArmorItem) {
			int i = ((DyeableArmorItem) armorItem).getColor(armorForBone);
			float r = (float) (i >> 16 & 255) / 255.0F;
			float g = (float) (i >> 8 & 255) / 255.0F;
			float b = (float) (i & 255) / 255.0F;

			renderArmorPart(stack, sourceLimb, packedLightIn, packedOverlayIn, r, g, b, 1, armorForBone, armorResource);
			renderArmorPart(stack, sourceLimb, packedLightIn, packedOverlayIn, 1, 1, 1, 1, armorForBone,
					getArmorResource(currentEntityBeingRendered, armorForBone, boneSlot, "overlay"));
		} else {
			renderArmorPart(stack, sourceLimb, packedLightIn, packedOverlayIn, 1, 1, 1, 1, armorForBone, armorResource);
		}
	}

	protected void prepareArmorPositionAndScale(GeoBone bone, List<Cuboid> cubeList, ModelPart sourceLimb,
			MatrixStack stack) {
		GeoCube firstCube = bone.childCubes.get(0);
		final Cuboid armorCube = cubeList.get(0);
		final float targetSizeX = firstCube.size.getX();
		final float targetSizeY = firstCube.size.getY();
		final float targetSizeZ = firstCube.size.getZ();

		final float sourceSizeX = Math.abs(armorCube.maxX - armorCube.minX);
		final float sourceSizeY = Math.abs(armorCube.maxY - armorCube.minY);
		final float sourceSizeZ = Math.abs(armorCube.maxZ - armorCube.minZ);

		float scaleX = targetSizeX / sourceSizeX;
		float scaleY = targetSizeY / sourceSizeY;
		float scaleZ = targetSizeZ / sourceSizeZ;

		// Modify position to move point to correct location, otherwise it will be off
		// when the sizes are different
		sourceLimb.setPivot(-(bone.getPivotX() + sourceSizeX - targetSizeX),
				-(bone.getPivotY() + sourceSizeY - targetSizeY), (bone.getPivotZ() + sourceSizeZ - targetSizeZ));

		sourceLimb.pitch = -bone.getRotationX();
		sourceLimb.yaw = -bone.getRotationY();
		sourceLimb.roll = bone.getRotationZ();

		stack.scale(scaleX, scaleY, scaleZ);
	}

	@Override
	public void renderRecursively(GeoBone bone, MatrixStack stack, VertexConsumer bufferIn, int packedLightIn,
			int packedOverlayIn, float red, float green, float blue, float alpha) {
		Identifier tfb = this.getCurrentModelRenderCycle() != EModelRenderCycle.INITIAL ? null
				: this.getTextureForBone(bone.getName(), this.currentEntityBeingRendered);
		boolean customTextureMarker = tfb != null;
		Identifier currentTexture = this.getTextureLocation(this.currentEntityBeingRendered);
		if (customTextureMarker) {
			currentTexture = tfb;
			this.bindTexture(currentTexture);
			RenderLayer rt = this.getRenderTypeForBone(bone, this.currentEntityBeingRendered,
					this.currentPartialTicks, stack, bufferIn, this.rtb, packedLightIn, currentTexture);
			bufferIn = this.rtb.getBuffer(rt);
		}
		if (this.getCurrentModelRenderCycle() == EModelRenderCycle.INITIAL) {
			stack.push();
			// Render armor

			if (this.isArmorBone(bone)) {
				this.handleArmorRenderingForBone(bone, stack, bufferIn, packedLightIn, packedOverlayIn, currentTexture);
			} else {
				ItemStack boneItem = this.getHeldItemForBone(bone.getName(), this.currentEntityBeingRendered);
				BlockState boneBlock = this.getHeldBlockForBone(bone.getName(), this.currentEntityBeingRendered);
				if (boneItem != null || boneBlock != null) {
//					stack.pop();
//					// First, let's move our render position to the pivot point...
					stack.translate(bone.getPivotX() / 16, bone.getPivotY() / 16, bone.getPositionZ() / 16);
//
					stack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(bone.getRotationX()));
					stack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(bone.getRotationY()));
					stack.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(bone.getRotationZ()));
					if (boneItem != null) {
						this.preRenderItem(stack, boneItem, bone.getName(), this.currentEntityBeingRendered, bone);

						MinecraftClient.getInstance().getHeldItemRenderer().renderItem(currentEntityBeingRendered,
								boneItem, this.getCameraTransformForItemAtBone(boneItem, bone.getName()), false, stack,
								rtb, packedLightIn);

						this.postRenderItem(stack, boneItem, bone.getName(), this.currentEntityBeingRendered, bone);
					}
					if (boneBlock != null) {
						this.preRenderBlock(boneBlock, bone.getName(), this.currentEntityBeingRendered);

						this.renderBlock(stack, this.rtb, packedLightIn, boneBlock);

						this.postRenderBlock(boneBlock, bone.getName(), this.currentEntityBeingRendered);
					}
//
//					stack.pop();
					bufferIn = rtb.getBuffer(RenderLayer.getEntityTranslucent(currentTexture));
				}
			}
			stack.pop();
		}

		if (customTextureMarker) {
			this.bindTexture(this.getTextureLocation(this.currentEntityBeingRendered));
		}
		// reset buffer
		if (customTextureMarker) {
			bufferIn = this.currentVertexBuilderInUse;
		}

		super.renderRecursively(bone, stack, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
	}

	private RenderLayer getRenderTypeForBone(GeoBone bone, T currentEntityBeingRendered2, float currentPartialTicks2,
			MatrixStack stack, VertexConsumer bufferIn, VertexConsumerProvider currentRenderTypeBufferInUse2,
			int packedLightIn, Identifier currentTexture) {
		return this.getRenderType(currentEntityBeingRendered2, currentPartialTicks2, stack,
				currentRenderTypeBufferInUse2, bufferIn, packedLightIn, currentTexture);
	}

	// Internal use only. Basically renders the passed "part" of the armor model on
	// a pre-setup location
	protected void renderArmorPart(MatrixStack stack, ModelPart sourceLimb, int packedLightIn, int packedOverlayIn,
			float red, float green, float blue, float alpha, ItemStack armorForBone, Identifier armorResource) {
		VertexConsumer ivb = ItemRenderer.getArmorGlintConsumer(rtb, RenderLayer.getArmorCutoutNoCull(armorResource),
				false, armorForBone.hasGlint());
		sourceLimb.render(stack, ivb, packedLightIn, packedOverlayIn, red, green, blue, alpha);
	}

	/*
	 * Return null, if the entity's texture is used ALso doesn't work yet, or, well,
	 * i haven't tested it, so, maybe it works...
	 */
	@Nullable
	protected abstract Identifier getTextureForBone(String boneName, T currentEntity);

	protected void renderBlock(MatrixStack matrixStack, VertexConsumerProvider rtb, int packedLightIn,
			BlockState iBlockState) {
		// TODO: Re-implement, doesn't do anything yet
	}

	/*
	 * Return null if there is no item
	 */
	@Nullable
	protected abstract ItemStack getHeldItemForBone(String boneName, T currentEntity);

	protected abstract Mode getCameraTransformForItemAtBone(ItemStack boneItem, String boneName);

	/*
	 * Return null if there is no held block
	 */
	@Nullable
	protected abstract BlockState getHeldBlockForBone(String boneName, T currentEntity);

	protected abstract void preRenderItem(MatrixStack matrixStack, ItemStack item, String boneName, T currentEntity,
			IBone bone);

	protected abstract void preRenderBlock(BlockState block, String boneName, T currentEntity);

	protected abstract void postRenderItem(MatrixStack matrixStack, ItemStack item, String boneName, T currentEntity,
			IBone bone);

	protected abstract void postRenderBlock(BlockState block, String boneName, T currentEntity);

	/*
	 * Return null, if there is no armor on this bone
	 * 
	 */
	@Nullable
	protected ItemStack getArmorForBone(String boneName, T currentEntity) {
		return null;
	}

	@Nullable
	protected EquipmentSlot getEquipmentSlotForArmorBone(String boneName, T currentEntity) {
		return null;
	}

	@Nullable
	protected ModelPart getArmorPartForBone(String name, BipedEntityModel<?> armorModel) {
		return null;
	}

	// Copied from BipedArmorLayer
	/**
	 * More generic ForgeHook version of the above function, it allows for Items to
	 * have more control over what texture they provide.
	 *
	 * @param entity Entity wearing the armor
	 * @param stack  ItemStack for the armor
	 * @param slot   Slot ID that the item is in
	 * @param type   Subtype, can be null or "overlay"
	 * @return Identifier pointing at the armor's texture
	 */
	private static final Map<String, Identifier> ARMOR_TEXTURE_RES_MAP = Maps.newHashMap();

	protected Identifier getArmorResource(Entity entity, ItemStack stack, EquipmentSlot slot, String type) {
		ArmorItem item = (ArmorItem) stack.getItem();
		String texture = item.getMaterial().getName();
		String domain = "minecraft";
		int idx = texture.indexOf(':');
		if (idx != -1) {
			domain = texture.substring(0, idx);
			texture = texture.substring(idx + 1);
		}
		String s1 = String.format("%s:textures/models/armor/%s_layer_%d%s.png", domain, texture,
				(slot == EquipmentSlot.LEGS ? 2 : 1), type == null ? "" : String.format("_%s", type));

		Identifier Identifier = (Identifier) ARMOR_TEXTURE_RES_MAP.get(s1);

		if (Identifier == null) {
			Identifier = new Identifier(s1);
			ARMOR_TEXTURE_RES_MAP.put(s1, Identifier);
		}

		return Identifier;
	}

}
