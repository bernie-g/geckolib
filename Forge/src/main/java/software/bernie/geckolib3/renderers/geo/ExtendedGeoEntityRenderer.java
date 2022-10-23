package software.bernie.geckolib3.renderers.geo;

import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;
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
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeableArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.AbstractSkullBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ForgeHooksClient;
import org.apache.commons.lang3.StringUtils;
import software.bernie.geckolib3.GeckoLib;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.geo.render.built.*;
import software.bernie.geckolib3.item.GeoArmorItem;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.util.EModelRenderCycle;
import software.bernie.geckolib3.util.RenderUtils;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 * @author DerToaster98 Copyright (c) 30.03.2022 Developed by DerToaster98
 *         GitHub: https://github.com/DerToaster98
 * 
 *         Purpose of this class: This class is a extended version of
 *         {@code GeoEnttiyRenderer}. It automates the process of rendering
 *         items at hand bones as well as standard armor at certain bones. The
 *         model must feature a few special bones for this to work.
 */
@OnlyIn(Dist.CLIENT)
public abstract class ExtendedGeoEntityRenderer<T extends LivingEntity & IAnimatable> extends GeoEntityRenderer<T> {

	protected final HumanoidModel<LivingEntity> DEFAULT_BIPED_ARMOR_MODEL_INNER = new HumanoidModel<LivingEntity>(
			Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.PLAYER_INNER_ARMOR));
	protected final HumanoidModel<LivingEntity> DEFAULT_BIPED_ARMOR_MODEL_OUTER = new HumanoidModel<LivingEntity>(
			Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR));

	protected float widthScale;
	protected float heightScale;

	protected T currentEntityBeingRendered;

	private float currentPartialTicks;
	protected ResourceLocation textureForBone = null;

	protected final Queue<Tuple<GeoBone, ItemStack>> HEAD_QUEUE = new ArrayDeque<>();

	/* TODO: Replace with fastutil equivalent */
	protected static Map<ResourceLocation, Tuple<Integer, Integer>> TEXTURE_SIZE_CACHE = new Object2ObjectOpenHashMap<>();

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
	protected void renderHeads(PoseStack stack, MultiBufferSource buffer, int packedLightIn) {
		while (!this.HEAD_QUEUE.isEmpty()) {
			Tuple<GeoBone, ItemStack> entry = this.HEAD_QUEUE.poll();

			GeoBone bone = entry.getA();
			ItemStack itemStack = entry.getB();

			stack.pushPose();

			this.moveAndRotateMatrixToMatchBone(stack, bone);

			GameProfile skullOwnerProfile = null;
			if (itemStack.hasTag()) {
				CompoundTag compoundnbt = itemStack.getTag();
				if (compoundnbt.contains("SkullOwner", 10)) {
					skullOwnerProfile = NbtUtils.readGameProfile(compoundnbt.getCompound("SkullOwner"));
				} else if (compoundnbt.contains("SkullOwner", 8)) {
					String s = compoundnbt.getString("SkullOwner");
					if (!StringUtils.isBlank(s)) {
						SkullBlockEntity.updateGameprofile(new GameProfile((UUID) null, s), (p_172560_) -> {
							compoundnbt.put("SkullOwner", NbtUtils.writeGameProfile(new CompoundTag(), p_172560_));
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
					sx = firstCube.size.x() / 8;
					sy = firstCube.size.y() / 8;
					sz = firstCube.size.z() / 8;
				}
			} catch (IndexOutOfBoundsException ioobe) {
				// Ignore
			}
			stack.scale(1.1875F * sx, 1.1875F * sy, 1.1875F * sz);
			stack.translate(-0.5, 0, -0.5);
			SkullBlock.Type skullblock$type = ((AbstractSkullBlock) ((BlockItem) itemStack.getItem()).getBlock())
					.getType();
			SkullModelBase skullmodelbase = SkullBlockRenderer
					.createSkullRenderers(Minecraft.getInstance().getEntityModels()).get(skullblock$type);
			RenderType rendertype = SkullBlockRenderer.getRenderType(skullblock$type, skullOwnerProfile);
			SkullBlockRenderer.renderSkull((Direction) null, 0, 0, stack, buffer, packedLightIn, skullmodelbase,
					rendertype);
			stack.popPose();

		}
		;
	}

	// Rendercall to render the model itself
	@Override
	public void render(GeoModel model, T animatable, float partialTicks, RenderType type, PoseStack matrixStackIn,
			MultiBufferSource renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn, int packedOverlayIn,
			float red, float green, float blue, float alpha) {
		super.render(model, animatable, partialTicks, type, matrixStackIn, renderTypeBuffer, vertexBuilder,
				packedLightIn, packedOverlayIn, red, green, blue, alpha);
		// Now, render the heads
		this.renderHeads(matrixStackIn, renderTypeBuffer, packedLightIn);
	}

	@Override
	public ResourceLocation getTextureLocation(T entity) {
		return this.modelProvider.getTextureResource(entity);
	}

	@Override
	public void renderLate(T animatable, PoseStack stackIn, float ticks, MultiBufferSource renderTypeBuffer,
			VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue,
			float partialTicks) {
		super.renderLate(animatable, stackIn, ticks, renderTypeBuffer, bufferIn, packedLightIn, packedOverlayIn, red,
				green, blue, partialTicks);
		this.currentEntityBeingRendered = animatable;
		this.currentPartialTicks = partialTicks;
	}

	protected abstract boolean isArmorBone(final GeoBone bone);

	protected void moveAndRotateMatrixToMatchBone(PoseStack stack, GeoBone bone) {
		// First, let's move our render position to the pivot point...
		stack.translate(bone.getPivotX() / 16, bone.getPivotY() / 16, bone.getPivotZ() / 16);

		stack.mulPose(Vector3f.XP.rotationDegrees(bone.getRotationX()));
		stack.mulPose(Vector3f.YP.rotationDegrees(bone.getRotationY()));
		stack.mulPose(Vector3f.ZP.rotationDegrees(bone.getRotationZ()));
	}

	protected void handleArmorRenderingForBone(GeoBone bone, PoseStack stack, VertexConsumer bufferIn,
			int packedLightIn, int packedOverlayIn, ResourceLocation currentTexture) {
		final ItemStack armorForBone = this.getArmorForBone(bone.getName(), currentEntityBeingRendered);
		final EquipmentSlot boneSlot = this.getEquipmentSlotForArmorBone(bone.getName(), currentEntityBeingRendered);
		if (armorForBone != null && boneSlot != null) {
			// Standard armor
			if (armorForBone.getItem() instanceof ArmorItem && !(armorForBone.getItem() instanceof GeoArmorItem)) {
				final ArmorItem armorItem = (ArmorItem) armorForBone.getItem();
				final HumanoidModel<?> armorModel = (HumanoidModel<?>) ForgeHooksClient.getArmorModel(
						currentEntityBeingRendered, armorForBone, boneSlot,
						boneSlot == EquipmentSlot.LEGS ? DEFAULT_BIPED_ARMOR_MODEL_INNER
								: DEFAULT_BIPED_ARMOR_MODEL_OUTER);
				if (armorModel != null) {
					ModelPart sourceLimb = this.getArmorPartForBone(bone.getName(), armorModel);
					List<Cube> cubeList = sourceLimb.cubes;
					if (sourceLimb != null && cubeList != null && !cubeList.isEmpty()) {
						// IMPORTANT: The first cube is used to define the armor part!!
						this.prepareArmorPositionAndScale(bone, cubeList, sourceLimb, stack);
						stack.scale(-1, -1, 1);

						stack.pushPose();

						ResourceLocation armorResource = this.getArmorResource(currentEntityBeingRendered, armorForBone,
								boneSlot, null);

						this.renderArmorOfItem(armorItem, armorForBone, boneSlot, armorResource, sourceLimb, stack,
								packedLightIn, packedOverlayIn);

						stack.popPose();

						bufferIn = rtb.getBuffer(RenderType.entityTranslucent(currentTexture));
					}
				}
			}
			// Geo Armor
			else if (armorForBone.getItem() instanceof GeoArmorItem) {
				final GeoArmorItem armorItem = (GeoArmorItem) armorForBone.getItem();
				final GeoArmorRenderer<? extends GeoArmorItem> geoArmorRenderer = GeoArmorRenderer
						.getRenderer(armorItem.getClass(), this.currentEntityBeingRendered);
				final HumanoidModel<?> armorModel = (HumanoidModel<?>) geoArmorRenderer;

				if (armorModel != null) {
					ModelPart sourceLimb = this.getArmorPartForBone(bone.getName(), armorModel);
					if (sourceLimb != null) {
						List<Cube> cubeList = sourceLimb.cubes;
						if (cubeList != null && !cubeList.isEmpty()) {
							// IMPORTANT: The first cube is used to define the armor part!!
							stack.scale(-1, -1, 1);
							stack.pushPose();

							this.prepareArmorPositionAndScale(bone, cubeList, sourceLimb, stack, true,
									boneSlot == EquipmentSlot.CHEST);

							geoArmorRenderer.setCurrentItem(this.currentEntityBeingRendered, armorForBone, boneSlot);
							// Just to be safe, it does some modelprovider stuff in there too
							geoArmorRenderer.applySlot(boneSlot);
							this.handleGeoArmorBoneVisibility(geoArmorRenderer, sourceLimb, armorModel, boneSlot);

							VertexConsumer ivb = ItemRenderer.getArmorFoilBuffer(rtb,
									RenderType.armorCutoutNoCull(GeoArmorRenderer
											.getRenderer(armorItem.getClass(), this.currentEntityBeingRendered)
											.getTextureLocation(armorItem)),
									false, armorForBone.hasFoil());

							geoArmorRenderer.render(this.currentPartialTicks, stack, ivb, packedLightIn);

							stack.popPose();

							bufferIn = rtb.getBuffer(RenderType.entityTranslucent(currentTexture));
						}
					}
				}
			}
			// Head blocks
			else if (armorForBone.getItem() instanceof BlockItem
					&& ((BlockItem) armorForBone.getItem()).getBlock() instanceof AbstractSkullBlock) {
				this.HEAD_QUEUE.add(new Tuple<>(bone, armorForBone));
			}
		}
	}

	protected void handleGeoArmorBoneVisibility(GeoArmorRenderer<? extends GeoArmorItem> geoArmorRenderer,
			ModelPart sourceLimb, HumanoidModel<?> armorModel, EquipmentSlot slot) {
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
			ResourceLocation armorResource, ModelPart sourceLimb, PoseStack stack, int packedLightIn,
			int packedOverlayIn) {
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

	protected void prepareArmorPositionAndScale(GeoBone bone, List<Cube> cubeList, ModelPart sourceLimb,
			PoseStack stack) {
		prepareArmorPositionAndScale(bone, cubeList, sourceLimb, stack, false, false);
	}

	protected void prepareArmorPositionAndScale(GeoBone bone, List<Cube> cubeList, ModelPart sourceLimb,
			PoseStack stack, boolean geoArmor, boolean modMatrixRot) {
		GeoCube firstCube = bone.childCubes.get(0);
		final Cube armorCube = cubeList.get(0);

		final float targetSizeX = firstCube.size.x();
		final float targetSizeY = firstCube.size.y();
		final float targetSizeZ = firstCube.size.z();

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
		sourceLimb.setPos(-(bone.getPivotX() - ((bone.getPivotX() * scaleX) - bone.getPivotX()) / scaleX),
				-(bone.getPivotY() - ((bone.getPivotY() * scaleY) - bone.getPivotY()) / scaleY),
				(bone.getPivotZ() - ((bone.getPivotZ() * scaleZ) - bone.getPivotZ()) / scaleZ));

		if (!geoArmor) {
			sourceLimb.xRot = -bone.getRotationX();
			sourceLimb.yRot = -bone.getRotationY();
			sourceLimb.zRot = bone.getRotationZ();
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

				stack.mulPose(new Quaternion(0, 0, zRot, false));
				stack.mulPose(new Quaternion(0, yRot, 0, false));
				stack.mulPose(new Quaternion(xRot, 0, 0, false));
			} else {
				sourceLimb.xRot = xRot;
				sourceLimb.yRot = yRot;
				sourceLimb.zRot = zRot;
			}
		}

		stack.scale(scaleX, scaleY, scaleZ);
	}

	@Override
	public void renderRecursively(GeoBone bone, PoseStack stack, VertexConsumer bufferIn, int packedLightIn,
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
		ResourceLocation currentTexture = this.getTextureLocation(this.currentEntityBeingRendered);

		final RenderType rt = customTextureMarker
				? this.getRenderTypeForBone(bone, this.currentEntityBeingRendered, this.currentPartialTicks, stack,
						bufferIn, this.getCurrentRTB(), packedLightIn, this.textureForBone)
				: this.getRenderType(this.currentEntityBeingRendered, this.currentPartialTicks, stack,
						this.getCurrentRTB(), bufferIn, packedLightIn, currentTexture);
		bufferIn = this.getCurrentRTB().getBuffer(rt);

		if (this.getCurrentModelRenderCycle() == EModelRenderCycle.INITIAL) {
			stack.pushPose();

			// Render armor
			if (this.isArmorBone(bone)) {
				stack.pushPose();
				this.handleArmorRenderingForBone(bone, stack, bufferIn, packedLightIn, packedOverlayIn, currentTexture);
				stack.popPose();

				// Reset buffer...
				bufferIn = this.getCurrentRTB().getBuffer(rt);
			} else {
				ItemStack boneItem = this.getHeldItemForBone(bone.getName(), this.currentEntityBeingRendered);
				BlockState boneBlock = this.getHeldBlockForBone(bone.getName(), this.currentEntityBeingRendered);
				if (boneItem != null || boneBlock != null) {

					stack.pushPose();
					this.handleItemAndBlockBoneRendering(stack, bone, boneItem, boneBlock, packedLightIn,
							packedOverlayIn);
					stack.popPose();

					bufferIn = rtb.getBuffer(RenderType.entityTranslucent(currentTexture));
				}
			}
			stack.popPose();
		}
		this.customBoneSpecificRenderingHook(bone, stack, bufferIn, packedLightIn, packedOverlayIn, red, green, blue,
				alpha, customTextureMarker, currentTexture);

		////////////////////////////////////
		stack.pushPose();
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
		stack.popPose();
		////////////////////////////////////
	}

	/*
	 * Gets called after armor and item rendering but in every render cycle. This
	 * serves as a hook for modders to include their own bone specific rendering
	 */
	protected void customBoneSpecificRenderingHook(GeoBone bone, PoseStack stack, VertexConsumer bufferIn,
			int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha,
			boolean customTextureMarker, ResourceLocation currentTexture) {
	}

	protected void handleItemAndBlockBoneRendering(PoseStack stack, GeoBone bone, @Nullable ItemStack boneItem,
			@Nullable BlockState boneBlock, int packedLightIn, int packedOverlayIn) {
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

	protected void renderItemStack(PoseStack stack, MultiBufferSource rtb, int packedLightIn, ItemStack boneItem,
			String boneName) {
		Minecraft.getInstance().getItemRenderer().renderStatic(currentEntityBeingRendered, boneItem,
				this.getCameraTransformForItemAtBone(boneItem, boneName), false, stack, rtb, null, packedLightIn,
				LivingEntityRenderer.getOverlayCoords(currentEntityBeingRendered, 0.0F),
				currentEntityBeingRendered.getId());
	}

	protected RenderType getRenderTypeForBone(GeoBone bone, T currentEntityBeingRendered2, float currentPartialTicks2,
			PoseStack stack, VertexConsumer bufferIn, MultiBufferSource currentRenderTypeBufferInUse2,
			int packedLightIn, ResourceLocation currentTexture) {
		return this.getRenderType(currentEntityBeingRendered2, currentPartialTicks2, stack,
				currentRenderTypeBufferInUse2, bufferIn, packedLightIn, currentTexture);
	}

	// Internal use only. Basically renders the passed "part" of the armor model on
	// a pre-setup location
	protected void renderArmorPart(PoseStack stack, ModelPart sourceLimb, int packedLightIn, int packedOverlayIn,
			float red, float green, float blue, float alpha, ItemStack armorForBone, ResourceLocation armorResource) {
		VertexConsumer ivb = ItemRenderer.getArmorFoilBuffer(rtb, RenderType.armorCutoutNoCull(armorResource), false,
				armorForBone.hasFoil());
		sourceLimb.render(stack, ivb, packedLightIn, packedOverlayIn, red, green, blue, alpha);
	}

	/*
	 * Return null, if the entity's texture is used ALso doesn't work yet, or, well,
	 * i haven't tested it, so, maybe it works...
	 */
	@Nullable
	protected abstract ResourceLocation getTextureForBone(String boneName, T currentEntity);

	protected void renderBlock(PoseStack matrixStack, MultiBufferSource rtb, int packedLightIn,
			BlockState iBlockState) {
		if (iBlockState.getRenderShape() != RenderShape.MODEL) {
			return;
		}
		matrixStack.pushPose();
		matrixStack.translate(-0.25F, -0.25F, -0.25F);
		matrixStack.scale(0.5F, 0.5F, 0.5F);
		Minecraft.getInstance().getBlockRenderer().renderSingleBlock(iBlockState, matrixStack, rtb, packedLightIn,
				OverlayTexture.NO_OVERLAY);
		matrixStack.popPose();
	}

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

	protected abstract void preRenderItem(PoseStack matrixStack, ItemStack item, String boneName, T currentEntity,
			IBone bone);

	protected abstract void preRenderBlock(PoseStack matrixStack, BlockState block, String boneName, T currentEntity);

	protected abstract void postRenderItem(PoseStack matrixStack, ItemStack item, String boneName, T currentEntity,
			IBone bone);

	protected abstract void postRenderBlock(PoseStack matrixStack, BlockState block, String boneName, T currentEntity);

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

	// Copied from BipedArmorLayer
	/**
	 * More generic ForgeHook version of the above function, it allows for Items to
	 * have more control over what texture they provide.
	 *
	 * @param entity Entity wearing the armor
	 * @param stack  ItemStack for the armor
	 * @param slot   Slot ID that the item is in
	 * @param type   Subtype, can be null or "overlay"
	 * @return ResourceLocation pointing at the armor's texture
	 */
	private static final Map<String, ResourceLocation> ARMOR_TEXTURE_RES_MAP = Maps.newHashMap();

	protected ResourceLocation getArmorResource(Entity entity, ItemStack stack, EquipmentSlot slot, String type) {
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

		s1 = net.minecraftforge.client.ForgeHooksClient.getArmorTexture(entity, stack, s1, slot, type);
		ResourceLocation resourcelocation = (ResourceLocation) ARMOR_TEXTURE_RES_MAP.get(s1);

		if (resourcelocation == null) {
			resourcelocation = new ResourceLocation(s1);
			ARMOR_TEXTURE_RES_MAP.put(s1, resourcelocation);
		}

		return resourcelocation;
	}

	// Auto UV recalculations for texturePerBone
	@Override
	public void createVerticesOfQuad(GeoQuad quad, Matrix4f matrix4f, Vector3f normal, VertexConsumer bufferIn,
			int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha)
			throws IOException {
		// If no textureForBone is used we can proceed normally
		if (this.textureForBone == null) {
			super.createVerticesOfQuad(quad, matrix4f, normal, bufferIn, packedLightIn, packedOverlayIn, red, green,
					blue, alpha);
		}
		Tuple<Integer, Integer> tfbSize = this.getOrCreateTextureSize(this.textureForBone);
		Tuple<Integer, Integer> textureSize = this
				.getOrCreateTextureSize(this.getTextureLocation(this.currentEntityBeingRendered));

		if (tfbSize == null || textureSize == null) {
			super.createVerticesOfQuad(quad, matrix4f, normal, bufferIn, packedLightIn, packedOverlayIn, red, green,
					blue, alpha);
			// Exit here, cause texture sizes are null
			return;
		}

		for (GeoVertex vertex : quad.vertices) {
			Vector4f vector4f = new Vector4f(vertex.position.x(), vertex.position.y(), vertex.position.z(), 1.0F);
			vector4f.transform(matrix4f);

			// Recompute the UV coordinates to the texture override
			float texU = (vertex.textureU * textureSize.getA()) / tfbSize.getA();
			float texV = (vertex.textureV * textureSize.getB()) / tfbSize.getB();

			bufferIn.vertex(vector4f.x(), vector4f.y(), vector4f.z(), red, green, blue, alpha, texU, texV,
					packedOverlayIn, packedLightIn, normal.x(), normal.y(), normal.z());
		}
	}

	protected Tuple<Integer, Integer> getOrCreateTextureSize(ResourceLocation tex) throws IOException {
		if (TEXTURE_SIZE_CACHE.containsKey(tex)) {
			return TEXTURE_SIZE_CACHE.get(tex);
		}
		// For some reason it can't find the texture during the first 6(?) frames?
		Tuple<Integer, Integer> size = this.getSizeOfTexture(tex);
		if (size == null) {
			return null;
		}
		return TEXTURE_SIZE_CACHE.computeIfAbsent(tex, (rs) -> size);
	}

	// Accesses the actual images behind the texture to read the size of the texture
	protected Tuple<Integer, Integer> getSizeOfTexture(ResourceLocation tex) throws IOException {
		if (tex == null) {
			return null;
		}
		AbstractTexture originalTexture = null;
		final Minecraft mc = Minecraft.getInstance();
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
			NativeImage image = originalTexture instanceof DynamicTexture
					? ((DynamicTexture) originalTexture).getPixels()
					: NativeImage.read(mc.getResourceManager().getResource(tex).get().open());
			if (image != null) {
				return new Tuple<>(image.getWidth(), image.getHeight());
			}
		}

		return null;
	}

}
