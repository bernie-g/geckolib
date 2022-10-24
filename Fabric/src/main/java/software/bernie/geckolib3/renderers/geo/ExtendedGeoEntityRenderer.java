package software.bernie.geckolib3.renderers.geo;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ExecutionException;

import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
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
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation.Mode;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.TextureManager;
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
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.math.Vector4f;
import software.bernie.geckolib3.ArmorRenderingRegistryImpl;
import software.bernie.geckolib3.GeckoLib;
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

	protected final BipedEntityModel<LivingEntity> DEFAULT_BIPED_ARMOR_MODEL_INNER = new BipedEntityModel<LivingEntity>(
			MinecraftClient.getInstance().getEntityModelLoader().getModelPart(EntityModelLayers.PLAYER_INNER_ARMOR));
	protected final BipedEntityModel<LivingEntity> DEFAULT_BIPED_ARMOR_MODEL_OUTER = new BipedEntityModel<LivingEntity>(
			MinecraftClient.getInstance().getEntityModelLoader().getModelPart(EntityModelLayers.PLAYER_OUTER_ARMOR));

	protected float widthScale;
	protected float heightScale;

	protected T currentEntityBeingRendered;

	protected float currentPartialTicks;
	protected Identifier textureForBone = null;

	protected final Queue<Pair<GeoBone, ItemStack>> HEAD_QUEUE = new ArrayDeque<>();

	protected static Map<Identifier, Pair<Integer, Integer>> TEXTURE_SIZE_CACHE = new Object2ObjectOpenHashMap<>();

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
		// Now, render the heads
		this.renderHeads(matrixStackIn, renderTypeBuffer, packedLightIn);
	}

	@Override
	public Identifier getTextureLocation(T entity) {
		return this.modelProvider.getTextureLocation(entity);
	}

	@Override
	public void renderLate(T animatable, MatrixStack stackIn, float ticks, VertexConsumerProvider renderTypeBuffer,
			VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue,
			float partialTicks) {
		super.renderLate(animatable, stackIn, ticks, renderTypeBuffer, bufferIn, packedLightIn, packedOverlayIn, red,
				green, blue, partialTicks);
		this.currentEntityBeingRendered = animatable;
		this.currentPartialTicks = partialTicks;
	}

	protected abstract boolean isArmorBone(final GeoBone bone);

	protected void moveAndRotateMatrixToMatchBone(MatrixStack stack, GeoBone bone) {
		// First, let's move our render position to the pivot point...
		stack.translate(bone.getPivotX() / 16, bone.getPivotY() / 16, bone.getPivotZ() / 16);

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
						// Just to be safe, it does some modelprovider stuff in there too
						geoArmorRenderer.applySlot(boneSlot);
						this.handleGeoArmorBoneVisibility(geoArmorRenderer, sourceLimb, armorModel, boneSlot);
						VertexConsumer ivb = ItemRenderer
								.getArmorGlintConsumer(rtb,
										RenderLayer.getArmorCutoutNoCull(GeoArmorRenderer
												.getRenderer(armorItem.getClass()).getTextureLocation(armorItem)),
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
		} else if (armorForBone.getItem() instanceof BlockItem
				&& ((BlockItem) armorForBone.getItem()).getBlock() instanceof AbstractSkullBlock) {
			this.HEAD_QUEUE.add(new Pair<>(bone, armorForBone));
		}
	}

	protected void handleGeoArmorBoneVisibility(GeoArmorRenderer<? extends ArmorItem> geoArmorRenderer,
			ModelPart sourceLimb, BipedEntityModel<?> armorModel, EquipmentSlot slot) {
		IBone gbHead = geoArmorRenderer.getAndHideBone(geoArmorRenderer.headBone);
		IBone gbBody = geoArmorRenderer.getAndHideBone(geoArmorRenderer.bodyBone);
		IBone gbArmL = geoArmorRenderer.getAndHideBone(geoArmorRenderer.leftArmBone);
		IBone gbArmR = geoArmorRenderer.getAndHideBone(geoArmorRenderer.rightArmBone);
		IBone gbLegL = geoArmorRenderer.getAndHideBone(geoArmorRenderer.leftLegBone);
		IBone gbLegR = geoArmorRenderer.getAndHideBone(geoArmorRenderer.rightLegBone);
		IBone gbBootL = geoArmorRenderer.getAndHideBone(geoArmorRenderer.leftBootBone);
		IBone gbBootR = geoArmorRenderer.getAndHideBone(geoArmorRenderer.rightBootBone);

		if (sourceLimb == armorModel.head || sourceLimb == armorModel.hat) {
			gbHead.setHidden(false);
			return;
		}
		if (sourceLimb == armorModel.body) {
			gbBody.setHidden(false);
			return;
		}
		if (sourceLimb == armorModel.leftArm) {
			gbArmL.setHidden(false);
			return;
		}
		if (sourceLimb == armorModel.leftLeg) {
			if (slot == EquipmentSlot.FEET) {
				gbBootL.setHidden(false);
			} else {
				gbLegL.setHidden(false);
			}
			return;
		}
		if (sourceLimb == armorModel.rightArm) {
			gbArmR.setHidden(false);
			return;
		}
		if (sourceLimb == armorModel.rightLeg) {
			if (slot == EquipmentSlot.FEET) {
				gbBootR.setHidden(false);
			} else {
				gbLegR.setHidden(false);
			}
			return;
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
		// Modifications of X and Z doon't seem to be necessary here, so let's ignore
		// them. For now.
		sourceLimb.setPivot(-(bone.getPivotX() - ((bone.getPivotX() * scaleX) - bone.getPivotX()) / scaleX),
				-(bone.getPivotY() - ((bone.getPivotY() * scaleY) - bone.getPivotY()) / scaleY),
				(bone.getPivotZ() - ((bone.getPivotZ() * scaleZ) - bone.getPivotZ()) / scaleZ));

		sourceLimb.pitch = -bone.getRotationX();
		sourceLimb.yaw = -bone.getRotationY();
		sourceLimb.roll = bone.getRotationZ();
		if (!geoArmor) {
			sourceLimb.pitch = -bone.getRotationX();
			sourceLimb.yaw = -bone.getRotationY();
			sourceLimb.roll = bone.getRotationZ();
		} else {
			// All those *= 2 calls ARE necessary, otherwise the geo armor will apply
			// rotations twice, so to have it only applied one time in the correct direction
			// we add 2x the negative rotation to it
			float xRot = -bone.getRotationX();
			xRot *= 2;
			float yRot = -bone.getRotationY();
			yRot *= 2;
			float zRot = bone.getRotationZ();
			zRot *= 2;
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
		if (this.getCurrentRTB() == null) {
			throw new IllegalStateException("RenderTypeBuffer must never be null at this point!");
		}

		if (this.getCurrentModelRenderCycle() != EModelRenderCycle.INITIAL) {
			super.renderRecursively(bone, stack, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
			return;
		}

		this.textureForBone = this.getCurrentModelRenderCycle() != EModelRenderCycle.INITIAL ? null
				: this.getTextureForBone(bone.getName(), this.currentEntityBeingRendered);
		boolean customTextureMarker = this.textureForBone != null;
		Identifier currentTexture = this.getTextureLocation(this.currentEntityBeingRendered);

		final RenderLayer rt = customTextureMarker
				? this.getRenderTypeForBone(bone, this.currentEntityBeingRendered, this.currentPartialTicks, stack,
						bufferIn, this.getCurrentRTB(), packedLightIn, this.textureForBone)
				: this.getRenderType(this.currentEntityBeingRendered, this.currentPartialTicks, stack,
						this.getCurrentRTB(), bufferIn, packedLightIn, currentTexture);
		bufferIn = this.getCurrentRTB().getBuffer(rt);

		if (this.getCurrentModelRenderCycle() == EModelRenderCycle.INITIAL) {
			stack.push();

			// Render armor
			if (this.isArmorBone(bone)) {
				stack.pop();
				this.handleArmorRenderingForBone(bone, stack, bufferIn, packedLightIn, packedOverlayIn, currentTexture);
				stack.push();

				// Reset buffer...
				bufferIn = this.getCurrentRTB().getBuffer(rt);
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

		////////////////////////////////////
		stack.push();
		super.preparePositionRotationScale(bone, stack);
		super.renderCubesOfBone(bone, stack, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		//////////////////////////////////////
		// reset buffer
		if (customTextureMarker) {
			bufferIn = this.getCurrentRTB().getBuffer(this.getRenderType(currentEntityBeingRendered,
					this.currentPartialTicks, stack, rtb, bufferIn, packedLightIn, currentTexture));
			// Reset the marker...
			this.textureForBone = null;
		}
		//////////////////////////////////////
		super.renderChildBones(bone, stack, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		stack.pop();
		////////////////////////////////////
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

			this.renderItemStack(stack, this.getCurrentRTB(), packedLightIn, boneItem, bone.getName());

			this.postRenderItem(stack, boneItem, bone.getName(), this.currentEntityBeingRendered, bone);
		}
		if (boneBlock != null) {
			this.preRenderBlock(stack, boneBlock, bone.getName(), this.currentEntityBeingRendered);

			this.renderBlock(stack, this.getCurrentRTB(), packedLightIn, boneBlock);

			this.postRenderBlock(stack, boneBlock, bone.getName(), this.currentEntityBeingRendered);
		}
	}

	protected void renderItemStack(MatrixStack stack, VertexConsumerProvider rtb, int packedLightIn, ItemStack boneItem,
			String boneName) {
		MinecraftClient.getInstance().getItemRenderer().renderItem(currentEntityBeingRendered, boneItem,
				this.getCameraTransformForItemAtBone(boneItem, boneName), false, stack, rtb, null, packedLightIn,
				LivingEntityRenderer.getOverlay(currentEntityBeingRendered, 0.0F), currentEntityBeingRendered.getId());
	}

	protected RenderLayer getRenderTypeForBone(GeoBone bone, T currentEntityBeingRendered2, float currentPartialTicks2,
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

	// Auto UV recalculations for texturePerBone
	@Override
	public void createVerticesOfQuad(GeoQuad quad, Matrix4f matrix4f, Vec3f normal, VertexConsumer bufferIn,
			int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
		// If no textureForBone is used we can proceed normally
		if (this.textureForBone == null) {
			super.createVerticesOfQuad(quad, matrix4f, normal, bufferIn, packedLightIn, packedOverlayIn, red, green,
					blue, alpha);
		}
		Pair<Integer, Integer> tfbSize = this.getOrCreateTextureSize(this.textureForBone);
		Pair<Integer, Integer> textureSize = this
				.getOrCreateTextureSize(this.getTextureLocation(this.currentEntityBeingRendered));

		if (tfbSize == null || textureSize == null) {
			super.createVerticesOfQuad(quad, matrix4f, normal, bufferIn, packedLightIn, packedOverlayIn, red, green,
					blue, alpha);
			// Exit here, cause texture sizes are null
			return;
		}

		for (GeoVertex vertex : quad.vertices) {
			Vector4f vector4f = new Vector4f(vertex.position.getX(), vertex.position.getY(), vertex.position.getZ(),
					1.0F);
			vector4f.transform(matrix4f);

			// Recompute the UV coordinates to the texture override
			float texU = (vertex.textureU * textureSize.getLeft()) / tfbSize.getLeft();
			float texV = (vertex.textureV * textureSize.getRight()) / tfbSize.getRight();

			bufferIn.vertex(vector4f.getX(), vector4f.getY(), vector4f.getZ(), red, green, blue, alpha, texU, texV,
					packedOverlayIn, packedLightIn, normal.getX(), normal.getY(), normal.getZ());
		}
	}

	protected Pair<Integer, Integer> getOrCreateTextureSize(Identifier tex) {
		if (TEXTURE_SIZE_CACHE.containsKey(tex)) {
			return TEXTURE_SIZE_CACHE.get(tex);
		}
		// For some reason it can't find the texture during the first 6(?) frames?
		Pair<Integer, Integer> size = this.getSizeOfTexture(tex);
		if (size == null) {
			return null;
		}
		return TEXTURE_SIZE_CACHE.computeIfAbsent(tex, (rs) -> size);
	}

	// Accesses the actual images behind the texture to read the size of the texture
	protected Pair<Integer, Integer> getSizeOfTexture(Identifier tex) {
		if (tex == null) {
			return null;
		}
		AbstractTexture originalTexture = null;
		final MinecraftClient mc = MinecraftClient.getInstance();
		final TextureManager textureManager = mc.getTextureManager();
		try {
			originalTexture = mc.submit(() -> {
				AbstractTexture texture = textureManager.getTexture(tex);
				if (texture == null) {
					return null;
				}
				return texture;
			}).get();
		} catch (InterruptedException | ExecutionException e) {
			GeckoLib.LOGGER.warn("Failed to load image for id {}", tex);
			e.printStackTrace();
		}

		if (originalTexture != null) {
			try (Resource res = mc.getResourceManager().getResource(tex)) {
				if (res != null) {
					NativeImage image = originalTexture instanceof NativeImageBackedTexture
							? ((NativeImageBackedTexture) originalTexture).getImage()
							: NativeImage.read(res.getInputStream());
					if (image != null) {
						return new Pair<>(image.getWidth(), image.getHeight());
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			GeckoLib.LOGGER.warn("Found no image file for id {}", tex);
		}
		return null;
	}

}
