package software.bernie.geckolib3q.renderers.geo;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;

import it.unimi.dsi.fastutil.ints.IntIntPair;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.SkullModelBase;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.ModelPart.Cube;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeableArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PlayerHeadItem;
import net.minecraft.world.level.block.AbstractSkullBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3q.ArmorRenderingRegistryImpl;
import software.bernie.geckolib3q.geo.render.built.GeoBone;
import software.bernie.geckolib3q.geo.render.built.GeoCube;
import software.bernie.geckolib3q.geo.render.built.GeoModel;
import software.bernie.geckolib3q.geo.render.built.GeoQuad;
import software.bernie.geckolib3q.geo.render.built.GeoVertex;
import software.bernie.geckolib3q.model.AnimatedGeoModel;
import software.bernie.geckolib3q.util.EModelRenderCycle;
import software.bernie.geckolib3q.util.RenderUtils;

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
	protected static Map<ResourceLocation, IntIntPair> TEXTURE_DIMENSIONS_CACHE = new Object2ObjectOpenHashMap<>();
	protected static Map<ResourceLocation, Tuple<Integer, Integer>> TEXTURE_SIZE_CACHE = new Object2ObjectOpenHashMap<>(); // TODO Remove in 1.20+
	private static final Map<String, ResourceLocation> ARMOR_TEXTURE_RES_MAP = new Object2ObjectOpenHashMap<>();
	protected static final HumanoidModel<LivingEntity> DEFAULT_BIPED_ARMOR_MODEL_INNER = new HumanoidModel<>(
			Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.PLAYER_INNER_ARMOR));
	protected static final HumanoidModel<LivingEntity> DEFAULT_BIPED_ARMOR_MODEL_OUTER = new HumanoidModel<>(
			Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR));


	protected float widthScale;
	protected float heightScale;

	protected T currentEntityBeingRendered;
	private float currentPartialTicks;
	protected ResourceLocation textureForBone = null;

	protected final Queue<Tuple<GeoBone, ItemStack>> HEAD_QUEUE = new ArrayDeque<>();

	protected ExtendedGeoEntityRenderer(EntityRendererProvider.Context renderManager,
			AnimatedGeoModel<T> modelProvider) {
		this(renderManager, modelProvider, 1F, 1F, 0);
	}

	protected ExtendedGeoEntityRenderer(EntityRendererProvider.Context renderManager, AnimatedGeoModel<T> modelProvider,
			float widthScale, float heightScale, float shadowSize) {
		super(renderManager, modelProvider);
		
		this.shadowRadius = shadowSize;
		this.widthScale = widthScale;
		this.heightScale = heightScale;
	}

	// Yes, this is necessary to be done after everything else, otherwise it will
	// mess up the texture cause the rendertypebuffer will be modified
	protected void renderHeads(PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
		while (!this.HEAD_QUEUE.isEmpty()) {
			Tuple<GeoBone, ItemStack> entry = this.HEAD_QUEUE.poll();

			GeoBone bone = entry.getA();
			ItemStack itemStack = entry.getB();
			GameProfile skullOwnerProfile = null;

			poseStack.pushPose();
			RenderUtils.translateAndRotateMatrixForBone(poseStack, bone);

			if (itemStack.hasTag()) {
				Tag skullOwnerTag = itemStack.getTag().get(PlayerHeadItem.TAG_SKULL_OWNER);

				if (skullOwnerTag != null) {
					if (skullOwnerTag instanceof CompoundTag tag) {
						skullOwnerProfile = NbtUtils.readGameProfile(tag);
					}
					else if (skullOwnerTag instanceof StringTag tag) {
						String skullOwner = tag.getAsString();

						if (!StringUtils.isBlank(skullOwner)) {
							SkullBlockEntity.updateGameprofile(new GameProfile(null, skullOwner), name ->
									itemStack.getTag().put(PlayerHeadItem.TAG_SKULL_OWNER, NbtUtils.writeGameProfile(new CompoundTag(), name)));
						}
					}
				}
			}

			float relativeScaleX = 1.1875F;
			float relativeScaleY = 1.1875F;
			float relativeScaleZ = 1.1875F;

			// Calculate scale in relation to a vanilla head (8x8x8 units)
			if (bone.childCubes.size() > 0) {
				GeoCube firstCube = bone.childCubes.get(0);
				relativeScaleX *= firstCube.size.x() / 8f;
				relativeScaleY *= firstCube.size.y() / 8f;
				relativeScaleZ *= firstCube.size.z() / 8f;
			}

			poseStack.scale(relativeScaleX, relativeScaleY, relativeScaleZ);
			poseStack.translate(-0.5, 0, -0.5);

			SkullBlock.Type skullBlockType = ((AbstractSkullBlock)((BlockItem)itemStack.getItem()).getBlock()).getType();
			SkullModelBase skullmodelbase = SkullBlockRenderer
					.createSkullRenderers(Minecraft.getInstance().getEntityModels()).get(skullBlockType);
			RenderType rendertype = SkullBlockRenderer.getRenderType(skullBlockType, skullOwnerProfile);

			SkullBlockRenderer.renderSkull(null, 0, 0, poseStack, buffer, packedLight, skullmodelbase, rendertype);
			poseStack.popPose();
		}
	}

	// Rendercall to render the model itself
	@Override
	public void render(GeoModel model, T animatable, float partialTick, RenderType type, PoseStack poseStack,
					   MultiBufferSource bufferSource, VertexConsumer buffer, int packedLight, int packedOverlay,
					   float red, float green, float blue, float alpha) {
		super.render(model, animatable, partialTick, type, poseStack, bufferSource, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		// Now, render the heads
		renderHeads(poseStack, bufferSource, packedLight);
	}

	@Override
	public ResourceLocation getTextureLocation(T animatable) {
		return this.modelProvider.getTextureResource(animatable);
	}

	@Override
	public void renderLate(T animatable, PoseStack poseStack, float partialTick, MultiBufferSource bufferSource,
						   VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue,
						   float partialTicks) {
		super.renderLate(animatable, poseStack, partialTick, bufferSource, buffer, packedLight, packedOverlay, red,
				green, blue, partialTicks);

		this.currentEntityBeingRendered = animatable;
		this.currentPartialTicks = partialTicks;
	}

	protected abstract boolean isArmorBone(final GeoBone bone);

	protected void handleArmorRenderingForBone(GeoBone bone, PoseStack stack, VertexConsumer buffer, int packedLight,
			int packedOverlay, ResourceLocation currentTexture) {
		ItemStack armorForBone = getArmorForBone(bone.getName(), this.currentEntityBeingRendered);
		EquipmentSlot boneSlot = getEquipmentSlotForArmorBone(bone.getName(), this.currentEntityBeingRendered);

		if (armorForBone == null || boneSlot == null)
			return;

		Item armorItem = armorForBone.getItem();

		if (armorForBone.getItem()instanceof BlockItem blockItem
				&& blockItem.getBlock() instanceof AbstractSkullBlock) {
			this.HEAD_QUEUE.add(new Tuple<>(bone, armorForBone));

			return;
		}

		if (armorItem instanceof ArmorItem geoArmorItem) {
			final HumanoidModel<?> armorModel = (HumanoidModel<?>) ArmorRenderingRegistryImpl.getArmorModel(
					currentEntityBeingRendered, armorForBone, boneSlot,
					boneSlot == EquipmentSlot.LEGS ? DEFAULT_BIPED_ARMOR_MODEL_INNER : DEFAULT_BIPED_ARMOR_MODEL_OUTER);

			if (armorModel == null)
				return;

			ModelPart sourceLimb = getArmorPartForBone(bone.getName(), armorModel);

			if (sourceLimb == null)
				return;

			List<Cube> cubeList = sourceLimb.cubes;

			if (cubeList.isEmpty())
				return;

			if (IAnimatable.class.isAssignableFrom(geoArmorItem.getClass())) {
				final GeoArmorRenderer<? extends ArmorItem> geoArmorRenderer = GeoArmorRenderer
						.getRenderer(geoArmorItem.getClass());

				VertexConsumer ivb = ItemRenderer.getArmorFoilBuffer(rtb,
						RenderType.armorCutoutNoCull(
								GeoArmorRenderer.getRenderer(geoArmorItem.getClass()).getTextureLocation(geoArmorItem)),
						false, armorForBone.hasFoil());

				stack.pushPose();
				stack.scale(-1, -1, 1);
				this.prepareArmorPositionAndScale(bone, cubeList, sourceLimb, stack, true,
						boneSlot == EquipmentSlot.CHEST);
				geoArmorRenderer.setCurrentItem(this.currentEntityBeingRendered, armorForBone, boneSlot, armorModel);
				// Just to be safe, it does some modelprovider stuff in there too
				geoArmorRenderer.applySlot(boneSlot);
				setLimbBoneVisible(geoArmorRenderer, sourceLimb, armorModel, boneSlot);
				geoArmorRenderer.render(this.currentPartialTicks, stack, ivb, packedLight);
				stack.popPose();
			} else {
				ResourceLocation armorResource = this.getArmorResource(currentEntityBeingRendered, armorForBone,
						boneSlot, null);

				stack.pushPose();
				stack.scale(-1, -1, 1);
				prepareArmorPositionAndScale(bone, cubeList, sourceLimb, stack);
				renderArmorOfItem(geoArmorItem, armorForBone, boneSlot, armorResource, sourceLimb, stack, packedLight,
						packedOverlay);
				stack.popPose();
			}
		}
	}

	protected void setLimbBoneVisible(GeoArmorRenderer<? extends ArmorItem> armorRenderer, ModelPart limb,
			HumanoidModel<?> armorModel, EquipmentSlot slot) {
		IBone gbHead = armorRenderer.getAndHideBone(armorRenderer.headBone);
		IBone gbBody = armorRenderer.getAndHideBone(armorRenderer.bodyBone);
		IBone gbArmL = armorRenderer.getAndHideBone(armorRenderer.leftArmBone);
		IBone gbArmR = armorRenderer.getAndHideBone(armorRenderer.rightArmBone);
		IBone gbLegL = armorRenderer.getAndHideBone(armorRenderer.leftLegBone);
		IBone gbLegR = armorRenderer.getAndHideBone(armorRenderer.rightLegBone);
		IBone gbBootL = armorRenderer.getAndHideBone(armorRenderer.leftBootBone);
		IBone gbBootR = armorRenderer.getAndHideBone(armorRenderer.rightBootBone);
		if (limb == armorModel.head || limb == armorModel.hat) {
			gbHead.setHidden(false);
			return;
		}
		if (limb == armorModel.body) {
			gbBody.setHidden(false);
			return;
		}
		if (limb == armorModel.leftArm) {
			gbArmL.setHidden(false);
			return;
		}
		if (limb == armorModel.leftLeg) {
			if (slot == EquipmentSlot.FEET) {
				gbBootL.setHidden(false);
			} else {
				gbLegL.setHidden(false);
			}
			return;
		}
		if (limb == armorModel.rightArm) {
			gbArmR.setHidden(false);
			return;
		}
		if (limb == armorModel.rightLeg) {
			if (slot == EquipmentSlot.FEET) {
				gbBootR.setHidden(false);
			} else {
				gbLegR.setHidden(false);
			}
			return;
		}
	}

	/**
	 * Use
	 * {@link ExtendedGeoEntityRenderer#setLimbBoneVisible(GeoArmorRenderer, ModelPart, HumanoidModel, EquipmentSlot)}<br>
	 * Remove in 1.20+
	 */
	@Deprecated(forRemoval = true)
	protected void handleGeoArmorBoneVisibility(GeoArmorRenderer<? extends ArmorItem> geoArmorRenderer,
			ModelPart sourceLimb, HumanoidModel<?> armorModel, EquipmentSlot slot) {
		setLimbBoneVisible(geoArmorRenderer, sourceLimb, armorModel, slot);
	}

	protected void renderArmorOfItem(ArmorItem armorItem, ItemStack armorForBone, EquipmentSlot boneSlot,
			ResourceLocation armorResource, ModelPart sourceLimb, PoseStack poseStack, int packedLight,
			int packedOverlay) {
		if (armorItem instanceof DyeableArmorItem dyableArmor) {
			int color = dyableArmor.getColor(armorForBone);

			renderArmorPart(poseStack, sourceLimb, packedLight, packedOverlay, (color >> 16 & 255) / 255f,
					(color >> 8 & 255) / 255f, (color & 255) / 255f, 1, armorForBone, armorResource);
			renderArmorPart(poseStack, sourceLimb, packedLight, packedOverlay, 1, 1, 1, 1, armorForBone,
					getArmorResource(currentEntityBeingRendered, armorForBone, boneSlot, "overlay"));
		} else {
			renderArmorPart(poseStack, sourceLimb, packedLight, packedOverlay, 1, 1, 1, 1, armorForBone, armorResource);
		}
	}

	protected void prepareArmorPositionAndScale(GeoBone bone, List<Cube> cubeList, ModelPart sourceLimb,
			PoseStack stack) {
		prepareArmorPositionAndScale(bone, cubeList, sourceLimb, stack, false, false);
	}

	protected void prepareArmorPositionAndScale(GeoBone bone, List<Cube> cubeList, ModelPart sourceLimb,
			PoseStack poseStack, boolean geoArmor, boolean modMatrixRot) {
		GeoCube firstCube = bone.childCubes.get(0);
		Cube armorCube = cubeList.get(0);
		float targetSizeX = firstCube.size.x();
		float targetSizeY = firstCube.size.y();
		float targetSizeZ = firstCube.size.z();
		float sourceSizeX = Math.abs(armorCube.maxX - armorCube.minX);
		float sourceSizeY = Math.abs(armorCube.maxY - armorCube.minY);
		float sourceSizeZ = Math.abs(armorCube.maxZ - armorCube.minZ);
		float scaleX = targetSizeX / sourceSizeX;
		float scaleY = targetSizeY / sourceSizeY;
		float scaleZ = targetSizeZ / sourceSizeZ;

		// Modify position to move point to correct location, otherwise it will be off
		// when the sizes are different
		// Modifications of X and Z don't seem to be necessary here, so let's ignore
		// them. For now.
		sourceLimb.setPos(-(bone.getPivotX() - ((bone.getPivotX() * scaleX) - bone.getPivotX()) / scaleX),
				-(bone.getPivotY() - ((bone.getPivotY() * scaleY) - bone.getPivotY()) / scaleY),
				(bone.getPivotZ() - ((bone.getPivotZ() * scaleZ) - bone.getPivotZ()) / scaleZ));

		if (!geoArmor) {
			sourceLimb.xRot = -bone.getRotationX();
			sourceLimb.yRot = -bone.getRotationY();
			sourceLimb.zRot = bone.getRotationZ();
		} else {
			// All those * 2 calls ARE necessary, otherwise the geo armor will apply
			// rotations twice, so to have it only applied one time in the correct direction
			// we add 2x the negative rotation to it
			float xRot = bone.getRotationX() * -2;
			float yRot = bone.getRotationY() * -2;
			float zRot = bone.getRotationZ() * 2;

			for (GeoBone parentBone = bone.parent; parentBone != null; parentBone = parentBone.parent) {
				xRot -= parentBone.getRotationX();
				yRot -= parentBone.getRotationY();
				zRot += parentBone.getRotationZ();
			}

			if (modMatrixRot) {
				poseStack.mulPose(new Quaternion(0, 0, (float)Math.toRadians(zRot), false));
				poseStack.mulPose(new Quaternion(0, (float)Math.toRadians(yRot), 0, false));
				poseStack.mulPose(new Quaternion((float)Math.toRadians(xRot), 0, 0, false));
			} else {
				sourceLimb.xRot = xRot;
				sourceLimb.yRot = yRot;
				sourceLimb.zRot = zRot;
			}
		}

		poseStack.scale(scaleX, scaleY, scaleZ);
	}

	@Override
	public void renderRecursively(GeoBone bone, PoseStack poseStack, VertexConsumer buffer, int packedLight,
			int packedOverlay, float red, float green, float blue, float alpha) {
		MultiBufferSource bufferSource = getCurrentRTB();

		if (bufferSource == null)
			throw new NullPointerException(
					"Can't render with a null RenderTypeBuffer! (GeoEntityRenderer.rtb is null)");

		if (getCurrentModelRenderCycle() != EModelRenderCycle.INITIAL) {
			super.renderRecursively(bone, poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);

			return;
		}

		this.textureForBone = getCurrentModelRenderCycle() != EModelRenderCycle.INITIAL ? null
				: getTextureForBone(bone.getName(), this.currentEntityBeingRendered);
		boolean useCustomTexture = this.textureForBone != null;
		ResourceLocation currentTexture = getTextureLocation(this.currentEntityBeingRendered);

		RenderType renderType = useCustomTexture
				? getRenderTypeForBone(bone, this.currentEntityBeingRendered, this.currentPartialTicks, poseStack,
						buffer, bufferSource, packedLight, this.textureForBone)
				: getRenderType(this.currentEntityBeingRendered, this.currentPartialTicks, poseStack, bufferSource,
						buffer, packedLight, currentTexture);
		buffer = bufferSource.getBuffer(renderType);

		if (getCurrentModelRenderCycle() == EModelRenderCycle.INITIAL) {
			poseStack.pushPose();

			// Render armor
			if (this.isArmorBone(bone)) {
				handleArmorRenderingForBone(bone, poseStack, buffer, packedLight, packedOverlay, currentTexture);
			} else {
				ItemStack boneItem = this.getHeldItemForBone(bone.getName(), this.currentEntityBeingRendered);
				BlockState boneBlock = this.getHeldBlockForBone(bone.getName(), this.currentEntityBeingRendered);
				if (boneItem != null || boneBlock != null) {
					handleItemAndBlockBoneRendering(poseStack, bone, boneItem, boneBlock, packedLight, packedOverlay);

					buffer = rtb.getBuffer(RenderType.entityTranslucent(currentTexture));
				}
			}

			poseStack.popPose();
		}

		customBoneSpecificRenderingHook(bone, poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha,
				useCustomTexture, currentTexture);

		poseStack.pushPose();
		RenderUtils.prepMatrixForBone(poseStack, bone);
		super.renderCubesOfBone(bone, poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);

		// Reset buffer
		if (useCustomTexture) {
			buffer = bufferSource.getBuffer(this.getRenderType(this.currentEntityBeingRendered,
					this.currentPartialTicks, poseStack, bufferSource, buffer, packedLight, currentTexture));
			// Reset the marker...
			this.textureForBone = null;
		}

		super.renderChildBones(bone, poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		poseStack.popPose();
	}

	/*
	 * Gets called after armor and item rendering but in every render cycle. This
	 * serves as a hook for modders to include their own bone specific rendering
	 */
	protected void customBoneSpecificRenderingHook(GeoBone bone, PoseStack poseStack, VertexConsumer buffer,
			int packedLight, int packedOverlay, float red, float green, float blue, float alpha,
			boolean customTextureMarker, ResourceLocation currentTexture) {
	}

	protected void handleItemAndBlockBoneRendering(PoseStack poseStack, GeoBone bone, @Nullable ItemStack boneItem,
			@Nullable BlockState boneBlock, int packedLight, int packedOverlay) {
		RenderUtils.prepMatrixForBone(poseStack, bone);
		RenderUtils.translateAndRotateMatrixForBone(poseStack, bone);

		if (boneItem != null) {
			preRenderItem(poseStack, boneItem, bone.getName(), this.currentEntityBeingRendered, bone);
			renderItemStack(poseStack, getCurrentRTB(), packedLight, boneItem, bone.getName());
			postRenderItem(poseStack, boneItem, bone.getName(), this.currentEntityBeingRendered, bone);
		}

		if (boneBlock != null) {
			preRenderBlock(poseStack, boneBlock, bone.getName(), this.currentEntityBeingRendered);
			renderBlock(poseStack, getCurrentRTB(), packedLight, boneBlock);
			postRenderBlock(poseStack, boneBlock, bone.getName(), this.currentEntityBeingRendered);
		}
	}

	protected void renderItemStack(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight,
			ItemStack stack, String boneName) {
		Minecraft.getInstance().getItemRenderer().renderStatic(this.currentEntityBeingRendered, stack,
				getCameraTransformForItemAtBone(stack, boneName), false, poseStack, bufferSource, null, packedLight,
				LivingEntityRenderer.getOverlayCoords(this.currentEntityBeingRendered, 0.0F),
				currentEntityBeingRendered.getId());
	}

	protected RenderType getRenderTypeForBone(GeoBone bone, T animatable, float partialTick, PoseStack poseStack,
			VertexConsumer buffer, MultiBufferSource bufferSource, int packedLight, ResourceLocation texture) {
		return getRenderType(animatable, partialTick, poseStack, bufferSource, buffer, packedLight, texture);
	}

	// Internal use only. Basically renders the passed "part" of the armor model on
	protected void renderArmorPart(PoseStack poseStack, ModelPart sourceLimb, int packedLight, int packedOverlay,
			float red, float green, float blue, float alpha, ItemStack armorStack, ResourceLocation texture) {
		VertexConsumer buffer = ItemRenderer.getArmorFoilBuffer(getCurrentRTB(), RenderType.armorCutoutNoCull(texture),
				false, armorStack.hasFoil());

		sourceLimb.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
	}

	/**
	 * Return a specific texture for a given bone, or null to use the existing
	 * texture
	 * 
	 * @param boneName   The name of the bone to be rendered
	 * @param animatable The animatable instance
	 * @return The specified texture path, or null if no override
	 */
	@Nullable
	protected abstract ResourceLocation getTextureForBone(String boneName, T currentEntity);

	/*
	 * Return null if there is no item
	 */
	@Nullable
	protected abstract ItemStack getHeldItemForBone(String boneName, T currentEntity);

	protected abstract TransformType getCameraTransformForItemAtBone(ItemStack boneItem, String boneName);

	/*
	 * Return null if there is no held block
	 */
	@Nullable
	protected abstract BlockState getHeldBlockForBone(String boneName, T currentEntity);

	protected abstract void preRenderItem(PoseStack PoseStack, ItemStack item, String boneName, T currentEntity,
			IBone bone);

	protected abstract void preRenderBlock(PoseStack PoseStack, BlockState block, String boneName, T currentEntity);

	protected abstract void postRenderItem(PoseStack PoseStack, ItemStack item, String boneName, T currentEntity,
			IBone bone);

	protected abstract void postRenderBlock(PoseStack PoseStack, BlockState block, String boneName, T currentEntity);

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
	protected ModelPart getArmorPartForBone(String name, HumanoidModel<?> armorModel) {
		return null;
	}

	// TODO in 1.20+ Remove null check from type. Users should not be providing null
	// to type, provide an empty string instead
	protected ResourceLocation getArmorResource(Entity entity, ItemStack stack, EquipmentSlot slot,
			@Nonnull String type) {
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

		ResourceLocation ResourceLocation = (ResourceLocation) ARMOR_TEXTURE_RES_MAP.get(s1);

		if (ResourceLocation == null) {
			ResourceLocation = new ResourceLocation(s1);
			ARMOR_TEXTURE_RES_MAP.put(s1, ResourceLocation);
		}

		return ResourceLocation;
	}

	// Auto UV recalculations for texturePerBone
	@Override
	public void createVerticesOfQuad(GeoQuad quad, Matrix4f poseState, Vector3f normal, VertexConsumer buffer,
			int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		// If no textureForBone is used we can proceed normally
		if (this.textureForBone == null) {
			super.createVerticesOfQuad(quad, poseState, normal, buffer, packedLight, packedOverlay, red, green, blue,
					alpha);
		}
		IntIntPair boneTextureSize = computeTextureSize(this.textureForBone);
		IntIntPair entityTextureSize = computeTextureSize(getTextureLocation(this.currentEntityBeingRendered));

		if (boneTextureSize == null || entityTextureSize == null) {
			super.createVerticesOfQuad(quad, poseState, normal, buffer, packedLight, packedOverlay, red, green, blue,
					alpha);

			return;
		}

		for (GeoVertex vertex : quad.vertices) {
			Vector4f vector4f = new Vector4f(vertex.position.x(), vertex.position.y(), vertex.position.z(), 1);
			float texU = (vertex.textureU * entityTextureSize.firstInt()) / boneTextureSize.firstInt();
			float texV = (vertex.textureV * entityTextureSize.secondInt()) / boneTextureSize.secondInt();

			vector4f.transform(poseState);

			buffer.vertex(vector4f.x(), vector4f.y(), vector4f.z(), red, green, blue, alpha, texU, texV, packedOverlay,
					packedLight, normal.x(), normal.y(), normal.z());
		}
	}

	protected IntIntPair computeTextureSize(ResourceLocation texture) {
		return TEXTURE_DIMENSIONS_CACHE.computeIfAbsent(texture, RenderUtils::getTextureDimensions);
	}

	protected void renderBlock(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, BlockState state) {
		if (state.getRenderShape() != RenderShape.MODEL)
			return;

		poseStack.pushPose();
		poseStack.translate(-0.25f, -0.25f, -0.25f);
		poseStack.scale(0.5F, 0.5F, 0.5F);
		Minecraft.getInstance().getBlockRenderer().renderSingleBlock(state, poseStack, bufferSource, packedLight,
				OverlayTexture.NO_OVERLAY);
		poseStack.popPose();
	}

	/**
	 * Use {@link RenderUtils#getTextureDimensions(ResourceLocation)}<br>
	 * Remove in 1.20+
	 */
	@Deprecated(forRemoval = true)
	protected Tuple<Integer, Integer> getSizeOfTexture(ResourceLocation texture) {
		IntIntPair dimensions = RenderUtils.getTextureDimensions(texture);

		return dimensions == null ? null : new Tuple<>(dimensions.firstInt(), dimensions.secondInt());
	}

	/**
	 * Use
	 * {@link RenderUtils#translateAndRotateMatrixForBone(PoseStack, GeoBone)}<br>
	 * Remove in 1.20+
	 */
	@Deprecated(forRemoval = true)
	protected void moveAndRotateMatrixToMatchBone(PoseStack stack, GeoBone bone) {
		RenderUtils.translateAndRotateMatrixForBone(stack, bone);
	}

	/**
	 * Use
	 * {@link ExtendedGeoEntityRenderer#computeTextureSize(ResourceLocation)}<br>
	 * Remove in 1.20+
	 */
	@Deprecated(forRemoval = true)
	protected Tuple<Integer, Integer> getOrCreateTextureSize(ResourceLocation texture) {
		return TEXTURE_SIZE_CACHE.computeIfAbsent(texture, key -> getSizeOfTexture(texture));
	}

}
