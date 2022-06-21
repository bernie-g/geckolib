package software.bernie.geckolib3.renderers.geo;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;

import net.minecraft.block.AbstractSkullBlock;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.SkullBlock;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPart.Cuboid;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.SkullBlockEntityModel;
import net.minecraft.client.render.block.entity.SkullBlockEntityRenderer;
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
import net.minecraft.item.BlockItem;
import net.minecraft.item.DyeableArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3f;
import software.bernie.geckolib3.ArmorRenderingRegistryImpl;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.geo.render.built.GeoCube;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.util.RenderUtils;

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

	/*
	 * Allows the end user to introduce custom render cycles
	 */
	public static interface IRenderCycle {
		public String name();
	}

	public static enum EModelRenderCycle implements IRenderCycle {
		INITIAL, REPEATED, SPECIAL /* For special use by the user */
	}

	protected float widthScale;
	protected float heightScale;

	protected final Queue<Pair<GeoBone, ItemStack>> HEAD_QUEUE = new ArrayDeque<>();

	/*
	 * 0 => Normal model 1 => Magical armor overlay
	 */
	private IRenderCycle currentModelRenderCycle = EModelRenderCycle.INITIAL;

	protected IRenderCycle getCurrentModelRenderCycle() {
		return this.currentModelRenderCycle;
	}

	protected void setCurrentModelRenderCycle(IRenderCycle currentModelRenderCycle) {
		this.currentModelRenderCycle = currentModelRenderCycle;
	}

	protected ExtendedGeoEntityRenderer(EntityRendererFactory.Context renderManager,
			AnimatedGeoModel<T> modelProvider) {
		this(renderManager, modelProvider, 1F, 1F, 0);
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

	// Yes, this is necessary to be done after everything else, otherwise it will
	// mess up the texture cause the rendertypebuffer will be modified
	protected void renderHeads(MatrixStack stack, VertexConsumerProvider buffer, int packedLightIn) {
		while (!this.HEAD_QUEUE.isEmpty()) {
			Pair<GeoBone, ItemStack> entry = this.HEAD_QUEUE.poll();

			GeoBone bone = entry.getLeft();
			ItemStack itemStack = entry.getRight();

			stack.push();

			this.moveAndRotateMatrixToMatchBone(stack, bone);

			GameProfile skullOwnerProfile = null;
			if (itemStack.hasNbt()) {
				NbtCompound compoundnbt = itemStack.getNbt();
				if (compoundnbt.contains("SkullOwner", 10)) {
					skullOwnerProfile = NbtHelper.toGameProfile(compoundnbt.getCompound("SkullOwner"));
				} else if (compoundnbt.contains("SkullOwner", 8)) {
					String s = compoundnbt.getString("SkullOwner");
					if (!StringUtils.isBlank(s)) {
						SkullBlockEntity.loadProperties(new GameProfile(null, s), (p_172560_) -> {
							compoundnbt.put("SkullOwner", NbtHelper.writeGameProfile(new NbtCompound(), p_172560_));
						});
					}
				}
			}
			float sx = 1;
			float sy = 1;
			float sz = 1;
			try {
				GeoCube firstCube = bone.childCubes.get(0);
				if (firstCube != null) {
					// Calculate scale in relation to a vanilla head (8x8x8 units)
					sx = firstCube.size.getX() / 8;
					sy = firstCube.size.getY() / 8;
					sz = firstCube.size.getZ() / 8;
				}
			} catch (IndexOutOfBoundsException ioobe) {
				// Ignore
			}
			stack.scale(1.1875F * sx, 1.1875F * sy, 1.1875F * sz);
			stack.translate(-0.5, 0, -0.5);
			SkullBlock.SkullType skullType = ((AbstractSkullBlock) ((BlockItem) itemStack.getItem()).getBlock())
					.getSkullType();
			SkullBlockEntityModel skullmodelbase = SkullBlockEntityRenderer
					.getModels(MinecraftClient.getInstance().getEntityModelLoader()).get(skullType);
			RenderLayer rendertype = SkullBlockEntityRenderer.getRenderLayer(skullType, skullOwnerProfile);
			SkullBlockEntityRenderer.renderSkull((Direction) null, 0.0F, 0.0F, stack, buffer, packedLightIn,
					skullmodelbase, rendertype);
			stack.pop();

		}
		;
	}

	// Rendercall to render the model itself
	@Override
	public void render(GeoModel model, T animatable, float partialTicks, RenderLayer type, MatrixStack matrixStackIn,
			VertexConsumerProvider renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn,
			int packedOverlayIn, float red, float green, float blue, float alpha) {
		super.render(model, animatable, partialTicks, type, matrixStackIn, renderTypeBuffer, vertexBuilder,
				packedLightIn, packedOverlayIn, red, green, blue, alpha);
		this.setCurrentModelRenderCycle(EModelRenderCycle.REPEATED);
		this.renderHeads(matrixStackIn, renderTypeBuffer, packedLightIn);
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
	public Identifier getTexture(T entity) {
		return getTextureResource(entity);
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
		stack.translate(bone.getPivotX() / 16, bone.getPivotY() / 16, bone.getPivotZ() / 16);

		stack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(bone.getRotationX()));
		stack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(bone.getRotationY()));
		stack.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(bone.getRotationZ()));
	}

	@SuppressWarnings("unchecked")
	protected void handleArmorRenderingForBone(GeoBone bone, MatrixStack stack, VertexConsumer bufferIn,
			int packedLightIn, int packedOverlayIn, Identifier currentTexture) {
		final ItemStack armorForBone = this.getArmorForBone(bone.getName(), currentEntityBeingRendered);
		final EquipmentSlot boneSlot = this.getEquipmentSlotForArmorBone(bone.getName(), currentEntityBeingRendered);
		if (armorForBone != null && armorForBone.getItem() instanceof ArmorItem
				&& armorForBone.getItem() instanceof ArmorItem && boneSlot != null) {
			final ArmorItem armorItem = (ArmorItem) armorForBone.getItem();
			final BipedEntityModel<?> armorModel = (BipedEntityModel<?>) ArmorRenderingRegistryImpl.getArmorModel(
					currentEntityBeingRendered, armorForBone, boneSlot,
					boneSlot == EquipmentSlot.LEGS ? DEFAULT_BIPED_ARMOR_MODEL_INNER : DEFAULT_BIPED_ARMOR_MODEL_OUTER);
			if (armorModel != null) {
				ModelPart sourceLimb = this.getArmorPartForBone(bone.getName(), armorModel);
				List<Cuboid> cubeList = sourceLimb.cuboids;
				if (sourceLimb != null && cubeList != null && !cubeList.isEmpty()) {
					stack.push();
					if (IAnimatable.class.isAssignableFrom(armorItem.getClass())) {
						final GeoArmorRenderer<? extends ArmorItem> geoArmorRenderer = GeoArmorRenderer
								.getRenderer(armorItem.getClass());
						stack.scale(-1, -1, 1);
						this.prepareArmorPositionAndScale(bone, cubeList, sourceLimb, stack, true,
								boneSlot == EquipmentSlot.CHEST);
						geoArmorRenderer.setCurrentItem(((LivingEntity) this.currentEntityBeingRendered), armorForBone,
								boneSlot, armorModel);
						geoArmorRenderer.applySlot(boneSlot);
						VertexConsumer ivb = ItemRenderer
								.getArmorGlintConsumer(rtb,
										RenderLayer.getArmorCutoutNoCull(GeoArmorRenderer
												.getRenderer(armorItem.getClass()).getTextureResource(armorItem)),
										false, armorForBone.hasGlint());

						geoArmorRenderer.render(this.currentPartialTicks, stack, ivb, packedLightIn);
					} else {
						stack.scale(-1, -1, 1);
						this.prepareArmorPositionAndScale(bone, cubeList, sourceLimb, stack);
						Identifier armorResource = this.getArmorResource(currentEntityBeingRendered, armorForBone,
								boneSlot, null);
						this.prepareArmorPositionAndScale(bone, cubeList, sourceLimb, stack);
						this.renderArmorOfItem(armorItem, armorForBone, boneSlot, armorResource, sourceLimb, stack,
								packedLightIn, packedOverlayIn);
					}

					stack.pop();
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
		prepareArmorPositionAndScale(bone, cubeList, sourceLimb, stack, false, false);
	}

	protected void prepareArmorPositionAndScale(GeoBone bone, List<Cuboid> cubeList, ModelPart sourceLimb,
			MatrixStack stack, boolean geoArmor, boolean modMatrixRot) {
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
		if (!geoArmor) {
			sourceLimb.pitch = -bone.getRotationX();
			sourceLimb.yaw = -bone.getRotationY();
			sourceLimb.roll = bone.getRotationZ();
		} else {
			float xRot = -bone.getRotationX();
			float yRot = -bone.getRotationY();
			float zRot = bone.getRotationZ();
			GeoBone tmpBone = bone.parent;
			while (tmpBone != null) {
				xRot -= tmpBone.getRotationX();
				yRot -= tmpBone.getRotationY();
				zRot += tmpBone.getRotationZ();
				tmpBone = tmpBone.parent;
			}

			if (modMatrixRot) {
				xRot = (float) Math.toRadians(xRot);
				yRot = (float) Math.toRadians(yRot);
				zRot = (float) Math.toRadians(zRot);

				stack.multiply(new Quaternion(0, 0, zRot, false));
				stack.multiply(new Quaternion(0, yRot, 0, false));
				stack.multiply(new Quaternion(xRot, 0, 0, false));
			} else {
				sourceLimb.pitch = xRot;
				sourceLimb.yaw = yRot;
				sourceLimb.roll = zRot;
			}
		}

		stack.scale(scaleX, scaleY, scaleZ);
	}

	@Override
	public void renderRecursively(GeoBone bone, MatrixStack stack, VertexConsumer bufferIn, int packedLightIn,
			int packedOverlayIn, float red, float green, float blue, float alpha) {
		Identifier tfb = this.currentModelRenderCycle != EModelRenderCycle.INITIAL ? null
				: this.getTextureForBone(bone.getName(), this.currentEntityBeingRendered);
		boolean customTextureMarker = tfb != null;
		Identifier currentTexture = this.getTexture(this.currentEntityBeingRendered);
		if (customTextureMarker) {
			currentTexture = tfb;
			RenderLayer rt = this.getRenderTypeForBone(bone, this.currentEntityBeingRendered, this.currentPartialTicks,
					stack, bufferIn, this.rtb, packedLightIn, currentTexture);
			bufferIn = this.rtb.getBuffer(rt);
		}
		if (this.currentModelRenderCycle == EModelRenderCycle.INITIAL) {
			stack.push();
			// Render armor

			if (this.isArmorBone(bone)) {
				this.handleArmorRenderingForBone(bone, stack, bufferIn, packedLightIn, packedOverlayIn, currentTexture);
			} else {
				ItemStack boneItem = this.getHeldItemForBone(bone.getName(), this.currentEntityBeingRendered);
				BlockState boneBlock = this.getHeldBlockForBone(bone.getName(), this.currentEntityBeingRendered);
				if (boneItem != null || boneBlock != null) {
					this.handleItemAndBlockBoneRendering(stack, bone, boneItem, boneBlock, packedLightIn);
					bufferIn = rtb.getBuffer(RenderLayer.getEntityTranslucent(currentTexture));
				}
			}
			stack.pop();
		}
		this.customBoneSpecificRenderingHook(bone, stack, bufferIn, packedLightIn, packedOverlayIn, red, green, blue,
				alpha, customTextureMarker, currentTexture);
		// reset buffer
		if (customTextureMarker) {
			bufferIn = this.currentVertexBuilderInUse;
		}

		super.renderRecursively(bone, stack, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
	}

	/*
	 * Gets called after armor and item rendering but in every render cycle. This
	 * serves as a hook for modders to include their own bone specific rendering
	 */
	protected void customBoneSpecificRenderingHook(GeoBone bone, MatrixStack stack, VertexConsumer bufferIn,
			int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha,
			boolean customTextureMarker, Identifier currentTexture) {
	}

	protected void handleItemAndBlockBoneRendering(MatrixStack stack, GeoBone bone, @Nullable ItemStack boneItem,
			@Nullable BlockState boneBlock, int packedLightIn) {
		RenderUtils.translate(bone, stack);
		RenderUtils.moveToPivot(bone, stack);
		RenderUtils.rotate(bone, stack);
		RenderUtils.scale(bone, stack);
		RenderUtils.moveBackFromPivot(bone, stack);

		this.moveAndRotateMatrixToMatchBone(stack, bone);

		if (boneItem != null) {
			this.preRenderItem(stack, boneItem, bone.getName(), this.currentEntityBeingRendered, bone);

			this.renderItemStack(stack, this.rtb, packedLightIn, boneItem, bone.getName());

			this.postRenderItem(stack, boneItem, bone.getName(), this.currentEntityBeingRendered, bone);
		}
		if (boneBlock != null) {
			this.preRenderBlock(stack, boneBlock, bone.getName(), this.currentEntityBeingRendered);

			this.renderBlock(stack, this.rtb, packedLightIn, boneBlock);

			this.postRenderBlock(stack, boneBlock, bone.getName(), this.currentEntityBeingRendered);
		}
	}

	protected void renderItemStack(MatrixStack stack, VertexConsumerProvider rtb, int packedLightIn, ItemStack boneItem,
			String boneName) {
		MinecraftClient.getInstance().getItemRenderer().renderItem(currentEntityBeingRendered, boneItem,
				this.getCameraTransformForItemAtBone(boneItem, boneName), false, stack, rtb, null, packedLightIn,
				packedLightIn, packedLightIn);
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
		if (iBlockState.getRenderType() != BlockRenderType.MODEL) {
			return;
		}
		matrixStack.push();
		matrixStack.translate(-0.25F, -0.25F, -0.25F);
		matrixStack.scale(0.5F, 0.5F, 0.5F);

		MinecraftClient.getInstance().getBlockRenderManager().renderBlockAsEntity(iBlockState, matrixStack, rtb,
				packedLightIn, packedLightIn);
		matrixStack.pop();
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

	protected abstract void preRenderBlock(MatrixStack matrixStack, BlockState block, String boneName, T currentEntity);

	protected abstract void postRenderItem(MatrixStack matrixStack, ItemStack item, String boneName, T currentEntity,
			IBone bone);

	protected abstract void postRenderBlock(MatrixStack matrixStack, BlockState block, String boneName,
			T currentEntity);

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
	 * @return Identifier pointing at theec armor's texture
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
