package software.bernie.geckolib3.renderers.geo;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;

import com.mojang.authlib.GameProfile;

import it.unimi.dsi.fastutil.ints.IntIntPair;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.block.AbstractSkullBlock;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.SkullBlock;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPart.Cuboid;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.SkullBlockEntityModel;
import net.minecraft.client.render.block.entity.SkullBlockEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
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
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SkullItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtString;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.math.Vector4f;
import software.bernie.geckolib3.ArmorRenderingRegistryImpl;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.geo.render.built.GeoCube;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.geo.render.built.GeoQuad;
import software.bernie.geckolib3.geo.render.built.GeoVertex;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.util.EModelRenderCycle;
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
	protected static Map<Identifier, IntIntPair> TEXTURE_DIMENSIONS_CACHE = new Object2ObjectOpenHashMap<>();
	protected static Map<Identifier, Pair<Integer, Integer>> TEXTURE_SIZE_CACHE = new Object2ObjectOpenHashMap<>(); // TODO Remove in 1.20+
	private static final Map<String, Identifier> ARMOR_TEXTURE_RES_MAP = new Object2ObjectOpenHashMap<>();
	protected static final BipedEntityModel<LivingEntity> DEFAULT_BIPED_ARMOR_MODEL_INNER = new BipedEntityModel<>(
			MinecraftClient.getInstance().getEntityModelLoader().getModelPart(EntityModelLayers.PLAYER_INNER_ARMOR));
	protected static final BipedEntityModel<LivingEntity> DEFAULT_BIPED_ARMOR_MODEL_OUTER = new BipedEntityModel<>(
			MinecraftClient.getInstance().getEntityModelLoader().getModelPart(EntityModelLayers.PLAYER_OUTER_ARMOR));


	protected float widthScale;
	protected float heightScale;

	protected T currentEntityBeingRendered;
	private float currentPartialTicks;
	protected Identifier textureForBone = null;

	protected final Queue<Pair<GeoBone, ItemStack>> HEAD_QUEUE = new ArrayDeque<>();

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

	// Yes, this is necessary to be done after everything else, otherwise it will
	// mess up the texture cause the rendertypebuffer will be modified
	protected void renderHeads(MatrixStack poseStack, VertexConsumerProvider buffer, int packedLight) {
		while (!this.HEAD_QUEUE.isEmpty()) {
			Pair<GeoBone, ItemStack> entry = this.HEAD_QUEUE.poll();

			GeoBone bone = entry.getLeft();
			ItemStack itemStack = entry.getRight();
			GameProfile skullOwnerProfile = null;

			poseStack.push();
			RenderUtils.translateAndRotateMatrixForBone(poseStack, bone);

			if (itemStack.hasNbt()) {
				NbtElement skullOwnerTag = itemStack.getNbt().get(SkullItem.SKULL_OWNER_KEY);

				if (skullOwnerTag != null) {
					if (skullOwnerTag instanceof NbtCompound tag) {
						skullOwnerProfile = NbtHelper.toGameProfile(tag);
					}
					else if (skullOwnerTag instanceof NbtString tag) {
						String skullOwner = tag.asString();

						if (!StringUtils.isBlank(skullOwner)) {
							SkullBlockEntity.loadProperties(new GameProfile(null, skullOwner), name ->
									itemStack.getNbt().put(SkullItem.SKULL_OWNER_KEY, NbtHelper.writeGameProfile(new NbtCompound(), name)));
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
				relativeScaleX *= firstCube.size.getX() / 8f;
				relativeScaleY *= firstCube.size.getY() / 8f;
				relativeScaleZ *= firstCube.size.getZ() / 8f;
			}

			poseStack.scale(relativeScaleX, relativeScaleY, relativeScaleZ);
			poseStack.translate(-0.5, 0, -0.5);

			SkullBlock.SkullType skullBlockType = ((AbstractSkullBlock)((BlockItem)itemStack.getItem()).getBlock()).getSkullType();
			SkullBlockEntityModel skullmodelbase = SkullBlockEntityRenderer
					.getModels(MinecraftClient.getInstance().getEntityModelLoader()).get(skullBlockType);
			RenderLayer rendertype = SkullBlockEntityRenderer.getRenderLayer(skullBlockType, skullOwnerProfile);

			SkullBlockEntityRenderer.renderSkull(null, 0, 0, poseStack, buffer, packedLight, skullmodelbase, rendertype);
			poseStack.pop();
		}
	}

	// Rendercall to render the model itself
	@Override
	public void render(GeoModel model, T animatable, float partialTick, RenderLayer type, MatrixStack poseStack,
					   VertexConsumerProvider bufferSource, VertexConsumer buffer, int packedLight, int packedOverlay,
					   float red, float green, float blue, float alpha) {
		super.render(model, animatable, partialTick, type, poseStack, bufferSource, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		// Now, render the heads
		renderHeads(poseStack, bufferSource, packedLight);
	}

	@Override
	public Identifier getTextureLocation(T animatable) {
		return this.modelProvider.getTextureLocation(animatable);
	}

	@Override
	public void renderLate(T animatable, MatrixStack poseStack, float partialTick, VertexConsumerProvider bufferSource,
						   VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue,
						   float partialTicks) {
		super.renderLate(animatable, poseStack, partialTick, bufferSource, buffer, packedLight, packedOverlay, red,
				green, blue, partialTicks);

		this.currentEntityBeingRendered = animatable;
		this.currentPartialTicks = partialTicks;
	}

	protected abstract boolean isArmorBone(final GeoBone bone);

	protected void handleArmorRenderingForBone(GeoBone bone, MatrixStack stack, VertexConsumer buffer, int packedLight,
			int packedOverlay, Identifier currentTexture) {
		ItemStack armorForBone = getArmorForBone(bone.getName(), this.currentEntityBeingRendered);
		EquipmentSlot boneSlot = getEquipmentSlotForArmorBone(bone.getName(), this.currentEntityBeingRendered);

		if (armorForBone == null || boneSlot == null)
			return;

		Item armorItem = armorForBone.getItem();

		if (armorForBone.getItem()instanceof BlockItem blockItem
				&& blockItem.getBlock() instanceof AbstractSkullBlock) {
			this.HEAD_QUEUE.add(new Pair<>(bone, armorForBone));

			return;
		}

		if (armorItem instanceof ArmorItem geoArmorItem) {
			final BipedEntityModel<?> armorModel = (BipedEntityModel<?>) ArmorRenderingRegistryImpl.getArmorModel(
					currentEntityBeingRendered, armorForBone, boneSlot,
					boneSlot == EquipmentSlot.LEGS ? DEFAULT_BIPED_ARMOR_MODEL_INNER : DEFAULT_BIPED_ARMOR_MODEL_OUTER);

			if (armorModel == null)
				return;

			ModelPart sourceLimb = getArmorPartForBone(bone.getName(), armorModel);

			if (sourceLimb == null)
				return;

			List<Cuboid> cubeList = sourceLimb.cuboids;

			if (cubeList.isEmpty())
				return;

			if (IAnimatable.class.isAssignableFrom(geoArmorItem.getClass())) {
				final GeoArmorRenderer<? extends ArmorItem> geoArmorRenderer = GeoArmorRenderer
						.getRenderer(geoArmorItem.getClass());

				VertexConsumer ivb = ItemRenderer.getArmorGlintConsumer(rtb,
						RenderLayer.getArmorCutoutNoCull(
								GeoArmorRenderer.getRenderer(geoArmorItem.getClass()).getTextureLocation(geoArmorItem)),
						false, armorForBone.hasGlint());

				stack.push();
				stack.scale(-1, -1, 1);
				this.prepareArmorPositionAndScale(bone, cubeList, sourceLimb, stack, true,
						boneSlot == EquipmentSlot.CHEST);
				geoArmorRenderer.setCurrentItem(this.currentEntityBeingRendered, armorForBone, boneSlot, armorModel);
				// Just to be safe, it does some modelprovider stuff in there too
				geoArmorRenderer.applySlot(boneSlot);
				setLimbBoneVisible(geoArmorRenderer, sourceLimb, armorModel, boneSlot);
				geoArmorRenderer.render(this.currentPartialTicks, stack, ivb, packedLight);
				stack.pop();
			} else {
				Identifier armorResource = this.getArmorResource(currentEntityBeingRendered, armorForBone,
						boneSlot, null);

				stack.push();
				stack.scale(-1, -1, 1);
				prepareArmorPositionAndScale(bone, cubeList, sourceLimb, stack);
				renderArmorOfItem(geoArmorItem, armorForBone, boneSlot, armorResource, sourceLimb, stack, packedLight,
						packedOverlay);
				stack.pop();
			}
		}
	}

	protected void setLimbBoneVisible(GeoArmorRenderer<? extends ArmorItem> armorRenderer, ModelPart limb,
			BipedEntityModel<?> armorModel, EquipmentSlot slot) {
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
	 * {@link ExtendedGeoEntityRenderer#setLimbBoneVisible(GeoArmorRenderer, ModelPart, BipedEntityModel, EquipmentSlot)}<br>
	 * Remove in 1.20+
	 */
	@Deprecated(forRemoval = true)
	protected void handleGeoArmorBoneVisibility(GeoArmorRenderer<? extends ArmorItem> geoArmorRenderer,
			ModelPart sourceLimb, BipedEntityModel<?> armorModel, EquipmentSlot slot) {
		setLimbBoneVisible(geoArmorRenderer, sourceLimb, armorModel, slot);
	}

	protected void renderArmorOfItem(ArmorItem armorItem, ItemStack armorForBone, EquipmentSlot boneSlot,
			Identifier armorResource, ModelPart sourceLimb, MatrixStack poseStack, int packedLight,
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

	protected void prepareArmorPositionAndScale(GeoBone bone, List<Cuboid> cubeList, ModelPart sourceLimb,
			MatrixStack stack) {
		prepareArmorPositionAndScale(bone, cubeList, sourceLimb, stack, false, false);
	}

	protected void prepareArmorPositionAndScale(GeoBone bone, List<Cuboid> cubeList, ModelPart sourceLimb,
			MatrixStack poseStack, boolean geoArmor, boolean modMatrixRot) {
		GeoCube firstCube = bone.childCubes.get(0);
		Cuboid armorCube = cubeList.get(0);
		float targetSizeX = firstCube.size.getX();
		float targetSizeY = firstCube.size.getY();
		float targetSizeZ = firstCube.size.getZ();
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
		sourceLimb.setPivot(-(bone.getPivotX() - ((bone.getPivotX() * scaleX) - bone.getPivotX()) / scaleX),
				-(bone.getPivotY() - ((bone.getPivotY() * scaleY) - bone.getPivotY()) / scaleY),
				(bone.getPivotZ() - ((bone.getPivotZ() * scaleZ) - bone.getPivotZ()) / scaleZ));

		if (!geoArmor) {
			sourceLimb.pitch = -bone.getRotationX();
			sourceLimb.yaw = -bone.getRotationY();
			sourceLimb.roll = bone.getRotationZ();
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
				poseStack.multiply(new Quaternion(0, 0, (float)Math.toRadians(zRot), false));
				poseStack.multiply(new Quaternion(0, (float)Math.toRadians(yRot), 0, false));
				poseStack.multiply(new Quaternion((float)Math.toRadians(xRot), 0, 0, false));
			} else {
				sourceLimb.pitch = xRot;
				sourceLimb.yaw = yRot;
				sourceLimb.roll = zRot;
			}
		}

		poseStack.scale(scaleX, scaleY, scaleZ);
	}

	@Override
	public void renderRecursively(GeoBone bone, MatrixStack poseStack, VertexConsumer buffer, int packedLight,
			int packedOverlay, float red, float green, float blue, float alpha) {
		VertexConsumerProvider bufferSource = getCurrentRTB();

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
		Identifier currentTexture = getTextureLocation(this.currentEntityBeingRendered);

		RenderLayer renderType = useCustomTexture
				? getRenderTypeForBone(bone, this.currentEntityBeingRendered, this.currentPartialTicks, poseStack,
						buffer, bufferSource, packedLight, this.textureForBone)
				: getRenderType(this.currentEntityBeingRendered, this.currentPartialTicks, poseStack, bufferSource,
						buffer, packedLight, currentTexture);
		buffer = bufferSource.getBuffer(renderType);

		if (getCurrentModelRenderCycle() == EModelRenderCycle.INITIAL) {
			poseStack.push();

			// Render armor
			if (this.isArmorBone(bone)) {
				handleArmorRenderingForBone(bone, poseStack, buffer, packedLight, packedOverlay, currentTexture);
			} else {
				ItemStack boneItem = this.getHeldItemForBone(bone.getName(), this.currentEntityBeingRendered);
				BlockState boneBlock = this.getHeldBlockForBone(bone.getName(), this.currentEntityBeingRendered);
				if (boneItem != null || boneBlock != null) {
					handleItemAndBlockBoneRendering(poseStack, bone, boneItem, boneBlock, packedLight, packedOverlay);

					buffer = rtb.getBuffer(RenderLayer.getEntityTranslucent(currentTexture));
				}
			}

			poseStack.pop();
		}

		customBoneSpecificRenderingHook(bone, poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha,
				useCustomTexture, currentTexture);

		poseStack.push();
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
		poseStack.pop();
	}

	/*
	 * Gets called after armor and item rendering but in every render cycle. This
	 * serves as a hook for modders to include their own bone specific rendering
	 */
	protected void customBoneSpecificRenderingHook(GeoBone bone, MatrixStack poseStack, VertexConsumer buffer,
			int packedLight, int packedOverlay, float red, float green, float blue, float alpha,
			boolean customTextureMarker, Identifier currentTexture) {
	}

	protected void handleItemAndBlockBoneRendering(MatrixStack poseStack, GeoBone bone, @Nullable ItemStack boneItem,
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

	protected void renderItemStack(MatrixStack poseStack, VertexConsumerProvider bufferSource, int packedLight,
			ItemStack stack, String boneName) {
		MinecraftClient.getInstance().getItemRenderer().renderItem(this.currentEntityBeingRendered, stack,
				getCameraTransformForItemAtBone(stack, boneName), false, poseStack, bufferSource, null, packedLight,
				LivingEntityRenderer.getOverlay(this.currentEntityBeingRendered, 0.0F),
				currentEntityBeingRendered.getId());
	}

	protected RenderLayer getRenderTypeForBone(GeoBone bone, T animatable, float partialTick, MatrixStack poseStack,
			VertexConsumer buffer, VertexConsumerProvider bufferSource, int packedLight, Identifier texture) {
		return getRenderType(animatable, partialTick, poseStack, bufferSource, buffer, packedLight, texture);
	}

	// Internal use only. Basically renders the passed "part" of the armor model on
	protected void renderArmorPart(MatrixStack poseStack, ModelPart sourceLimb, int packedLight, int packedOverlay,
			float red, float green, float blue, float alpha, ItemStack armorStack, Identifier texture) {
		VertexConsumer buffer = ItemRenderer.getArmorGlintConsumer(getCurrentRTB(), RenderLayer.getArmorCutoutNoCull(texture),
				false, armorStack.hasGlint());

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
	protected abstract Identifier getTextureForBone(String boneName, T currentEntity);

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

	protected abstract void preRenderItem(MatrixStack MatrixStack, ItemStack item, String boneName, T currentEntity,
			IBone bone);

	protected abstract void preRenderBlock(MatrixStack MatrixStack, BlockState block, String boneName, T currentEntity);

	protected abstract void postRenderItem(MatrixStack MatrixStack, ItemStack item, String boneName, T currentEntity,
			IBone bone);

	protected abstract void postRenderBlock(MatrixStack MatrixStack, BlockState block, String boneName, T currentEntity);

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

	// TODO in 1.20+ Remove null check from type. Users should not be providing null
	// to type, provide an empty string instead
	protected Identifier getArmorResource(Entity entity, ItemStack stack, EquipmentSlot slot,
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

		Identifier ResourceLocation = (Identifier) ARMOR_TEXTURE_RES_MAP.get(s1);

		if (ResourceLocation == null) {
			ResourceLocation = new Identifier(s1);
			ARMOR_TEXTURE_RES_MAP.put(s1, ResourceLocation);
		}

		return ResourceLocation;
	}

	// Auto UV recalculations for texturePerBone
	@Override
	public void createVerticesOfQuad(GeoQuad quad, Matrix4f poseState, Vec3f normal, VertexConsumer buffer,
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
			Vector4f vector4f = new Vector4f(vertex.position.getX(), vertex.position.getY(), vertex.position.getZ(), 1);
			float texU = (vertex.textureU * entityTextureSize.firstInt()) / boneTextureSize.firstInt();
			float texV = (vertex.textureV * entityTextureSize.secondInt()) / boneTextureSize.secondInt();

			vector4f.transform(poseState);

			buffer.vertex(vector4f.getX(), vector4f.getY(), vector4f.getZ(), red, green, blue, alpha, texU, texV, packedOverlay,
					packedLight, normal.getX(), normal.getY(), normal.getZ());
		}
	}

	protected IntIntPair computeTextureSize(Identifier texture) {
		return TEXTURE_DIMENSIONS_CACHE.computeIfAbsent(texture, RenderUtils::getTextureDimensions);
	}

	protected void renderBlock(MatrixStack poseStack, VertexConsumerProvider bufferSource, int packedLight, BlockState state) {
		if (state.getRenderType() != BlockRenderType.MODEL)
			return;

		poseStack.push();
		poseStack.translate(-0.25f, -0.25f, -0.25f);
		poseStack.scale(0.5F, 0.5F, 0.5F);
		MinecraftClient.getInstance().getBlockRenderManager().renderBlockAsEntity(state, poseStack, bufferSource, packedLight,
				OverlayTexture.DEFAULT_UV);
		poseStack.pop();
	}

	/**
	 * Use {@link RenderUtils#getTextureDimensions(Identifier)}<br>
	 * Remove in 1.20+
	 */
	@Deprecated(forRemoval = true)
	protected Pair<Integer, Integer> getSizeOfTexture(Identifier texture) {
		IntIntPair dimensions = RenderUtils.getTextureDimensions(texture);

		return dimensions == null ? null : new Pair<>(dimensions.firstInt(), dimensions.secondInt());
	}

	/**
	 * Use
	 * {@link RenderUtils#translateAndRotateMatrixForBone(MatrixStack, GeoBone)}<br>
	 * Remove in 1.20+
	 */
	@Deprecated(forRemoval = true)
	protected void moveAndRotateMatrixToMatchBone(MatrixStack stack, GeoBone bone) {
		RenderUtils.translateAndRotateMatrixForBone(stack, bone);
	}

	/**
	 * Use
	 * {@link ExtendedGeoEntityRenderer#computeTextureSize(Identifier)}<br>
	 * Remove in 1.20+
	 */
	@Deprecated(forRemoval = true)
	protected Pair<Integer, Integer> getOrCreateTextureSize(Identifier texture) {
		return TEXTURE_SIZE_CACHE.computeIfAbsent(texture, key -> getSizeOfTexture(texture));
	}

}
