package software.bernie.geckolib3.renderers.geo;

import java.util.ArrayDeque;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;

import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import it.unimi.dsi.fastutil.objects.ObjectList;
import net.minecraft.block.AbstractSkullBlock;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.model.ModelRenderer.ModelBox;
import net.minecraft.client.renderer.tileentity.SkullTileEntityRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.IDyeableArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.SkullTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ForgeHooksClient;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.geo.render.built.GeoCube;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.item.GeoArmorItem;
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
@OnlyIn(Dist.CLIENT)
public abstract class ExtendedGeoEntityRenderer<T extends LivingEntity & IAnimatable> extends GeoEntityRenderer<T> {

	static enum EModelRenderCycle {
		INITIAL, REPEATED, SPECIAL /* For special use by the user */
	}

	protected float widthScale;
	protected float heightScale;
	
	protected final Queue<Tuple<GeoBone, ItemStack>> HEAD_QUEUE = new ArrayDeque<>();

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

	protected ExtendedGeoEntityRenderer(EntityRendererManager renderManager, AnimatedGeoModel<T> modelProvider) {
		this(renderManager, modelProvider, 1F, 1F, 0);
	}

	protected ExtendedGeoEntityRenderer(EntityRendererManager renderManager, AnimatedGeoModel<T> modelProvider,
			float widthScale, float heightScale, float shadowSize) {
		super(renderManager, modelProvider);

		this.shadowRadius = shadowSize;
		this.widthScale = widthScale;
		this.heightScale = heightScale;
	}

	// Entrypoint for rendering, calls everything else
	@Override
	public void render(T entity, float entityYaw, float partialTicks, MatrixStack stack, IRenderTypeBuffer bufferIn,
			int packedLightIn) {
		this.setCurrentModelRenderCycle(EModelRenderCycle.INITIAL);
		super.render(entity, entityYaw, partialTicks, stack, bufferIn, packedLightIn);
	}

	// Rendercall to render the model itself
	@Override
	public void render(GeoModel model, T animatable, float partialTicks, RenderType type, MatrixStack matrixStackIn,
			IRenderTypeBuffer renderTypeBuffer, IVertexBuilder vertexBuilder, int packedLightIn, int packedOverlayIn,
			float red, float green, float blue, float alpha) {
		super.render(model, animatable, partialTicks, type, matrixStackIn, renderTypeBuffer, vertexBuilder,
				packedLightIn, packedOverlayIn, red, green, blue, alpha);
		this.setCurrentModelRenderCycle(EModelRenderCycle.REPEATED);
		
		//Now, render the heads
		this.renderHeads(matrixStackIn, renderTypeBuffer, packedLightIn);
	}
	
	//Yes, this is necessary to be done after everything else, otherwise it will mess up the texture cause the rendertypebuffer will be modified
	protected void renderHeads(MatrixStack stack, IRenderTypeBuffer buffer, int packedLightIn) {
		while(!this.HEAD_QUEUE.isEmpty()) {
			Tuple<GeoBone, ItemStack> entry = this.HEAD_QUEUE.poll();
			
			GeoBone bone = entry.getA();
			ItemStack itemStack = entry.getB();
			
			stack.pushPose();
			
			this.moveAndRotateMatrixToMatchBone(stack, bone);
			
			GameProfile skullOwnerProfile = null;
			if (itemStack.hasTag()) {
				CompoundNBT compoundnbt = itemStack.getTag();
	             if (compoundnbt.contains("SkullOwner", 10)) {
	            	 skullOwnerProfile = NBTUtil.readGameProfile(compoundnbt.getCompound("SkullOwner"));
	             } else if (compoundnbt.contains("SkullOwner", 8)) {
	            	 String s = compoundnbt.getString("SkullOwner");
	            	 if (!StringUtils.isBlank(s)) {
	            		 skullOwnerProfile = SkullTileEntity.updateGameprofile(new GameProfile((UUID)null, s));
	                     compoundnbt.put("SkullOwner", NBTUtil.writeGameProfile(new CompoundNBT(), skullOwnerProfile));
	            	 }
	             }
            }
			float sx = 1;
			float sy = 1;
			float sz = 1;
			try {
				GeoCube firstCube = bone.childCubes.get(0);
				if(firstCube != null) {
					//Calculate scale in relation to a vanilla head (8x8x8 units)
					sx = firstCube.size.x() / 8;
					sy = firstCube.size.y() / 8;
					sz = firstCube.size.z() / 8;
				}
			} catch(IndexOutOfBoundsException ioobe) {
				//Ignore
			}
            stack.scale(1.1875F * sx, 1.1875F * sy, 1.1875F * sz);
            stack.translate(-0.5, 0, -0.5);
            SkullTileEntityRenderer.renderSkull((Direction)null, 0.0F, ((AbstractSkullBlock)((BlockItem)itemStack.getItem()).getBlock()).getType(), skullOwnerProfile, 0F /* limbswing, controls rotation*/, stack, buffer, packedLightIn);
            stack.popPose();
			
		};
	}

	protected float getWidthScale(T entity) {
		return this.widthScale;
	}

	protected float getHeightScale(T entity) {
		return this.heightScale;
	}

	@Override
	public void renderEarly(T animatable, MatrixStack stackIn, float ticks, IRenderTypeBuffer renderTypeBuffer,
			IVertexBuilder vertexBuilder, int packedLightIn, int packedOverlayIn, float red, float green, float blue,
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
	public ResourceLocation getTextureLocation(T entity) {
		return this.modelProvider.getTextureLocation(entity);
	}

	private T currentEntityBeingRendered;
	private IRenderTypeBuffer rtb;

	@Override
	public void renderLate(T animatable, MatrixStack stackIn, float ticks, IRenderTypeBuffer renderTypeBuffer,
			IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue,
			float partialTicks) {
		super.renderLate(animatable, stackIn, ticks, renderTypeBuffer, bufferIn, packedLightIn, packedOverlayIn, red,
				green, blue, partialTicks);
		this.currentEntityBeingRendered = animatable;
		this.currentVertexBuilderInUse = bufferIn;
		this.currentPartialTicks = partialTicks;
	}

	protected final BipedModel<LivingEntity> DEFAULT_BIPED_ARMOR_MODEL_INNER = new BipedModel<>(0.5F);
	protected final BipedModel<LivingEntity> DEFAULT_BIPED_ARMOR_MODEL_OUTER = new BipedModel<>(1.0F);
	
	protected abstract boolean isArmorBone(final GeoBone bone);
	
	private IVertexBuilder currentVertexBuilderInUse;
	private float currentPartialTicks;
	
	@Override
	public void renderRecursively(GeoBone bone, MatrixStack stack, IVertexBuilder bufferIn, int packedLightIn,
			int packedOverlayIn, float red, float green, float blue, float alpha) {
		ResourceLocation tfb = this.getCurrentModelRenderCycle() != EModelRenderCycle.INITIAL ? null
				: this.getTextureForBone(bone.getName(), this.currentEntityBeingRendered);
		boolean customTextureMarker = tfb != null;
		ResourceLocation currentTexture = this.getTextureLocation(this.currentEntityBeingRendered);
		if (customTextureMarker) {
			currentTexture = tfb;
			
			if(this.rtb != null) {
				RenderType rt = this.getRenderTypeForBone(bone, this.currentEntityBeingRendered, this.currentPartialTicks, stack, bufferIn, this.rtb, packedLightIn, currentTexture);
				bufferIn = this.rtb.getBuffer(rt);
			}
		}
		if (this.getCurrentModelRenderCycle() == EModelRenderCycle.INITIAL) {
			stack.pushPose();

			// Render armor

			if (this.isArmorBone(bone)) {
				this.handleArmorRenderingForBone(bone, stack, bufferIn, packedLightIn, packedOverlayIn, currentTexture);
			} else {
				ItemStack boneItem = this.getHeldItemForBone(bone.getName(), this.currentEntityBeingRendered);
				BlockState boneBlock = this.getHeldBlockForBone(bone.getName(), this.currentEntityBeingRendered);
				if (boneItem != null || boneBlock != null) {

					stack.pushPose();
					
					this.moveAndRotateMatrixToMatchBone(stack, bone);

					if (boneItem != null) {
						this.preRenderItem(stack, boneItem, bone.getName(), this.currentEntityBeingRendered, bone);

						Minecraft.getInstance().getItemInHandRenderer().renderItem(currentEntityBeingRendered, boneItem,
								this.getCameraTransformForItemAtBone(boneItem, bone.getName()), false, stack, rtb,
								packedLightIn);

						this.postRenderItem(stack, boneItem, bone.getName(), this.currentEntityBeingRendered, bone);
					}
					if (boneBlock != null) {
						this.preRenderBlock(boneBlock, bone.getName(), this.currentEntityBeingRendered);

						this.renderBlock(stack, this.rtb, packedLightIn, boneBlock);

						this.postRenderBlock(boneBlock, bone.getName(), this.currentEntityBeingRendered);
					}

					stack.popPose();

					bufferIn = rtb.getBuffer(RenderType.entityTranslucent(currentTexture));
				}
			}
			stack.popPose();
		}
		//reset buffer
		if (customTextureMarker) {
			bufferIn = this.currentVertexBuilderInUse;
		}

		super.renderRecursively(bone, stack, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
	}

	private RenderType getRenderTypeForBone(GeoBone bone, T currentEntityBeingRendered2, float currentPartialTicks2, MatrixStack stack, IVertexBuilder bufferIn, IRenderTypeBuffer currentRenderTypeBufferInUse2, int packedLightIn,
			ResourceLocation currentTexture) {
		return this.getRenderType(currentEntityBeingRendered2, currentPartialTicks2, stack, currentRenderTypeBufferInUse2, bufferIn, packedLightIn, currentTexture);
	}

	protected void moveAndRotateMatrixToMatchBone(MatrixStack stack, GeoBone bone) {
		// First, let's move our render position to the pivot point...
		stack.translate(bone.getPivotX() / 16, bone.getPivotY() / 16, bone.getPositionZ() / 16);

		stack.mulPose(Vector3f.XP.rotationDegrees(bone.getRotationX()));
		stack.mulPose(Vector3f.YP.rotationDegrees(bone.getRotationY()));
		stack.mulPose(Vector3f.ZP.rotationDegrees(bone.getRotationZ()));
	}

	protected void handleArmorRenderingForBone(GeoBone bone, MatrixStack stack, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, ResourceLocation currentTexture) {
		final ItemStack armorForBone = this.getArmorForBone(bone.getName(), currentEntityBeingRendered);
		final EquipmentSlotType boneSlot = this.getEquipmentSlotForArmorBone(bone.getName(),
				currentEntityBeingRendered);
		//Armor and geo armor
		if (armorForBone != null && boneSlot != null) {
			//Standard armor
			if (armorForBone.getItem() instanceof ArmorItem && !(armorForBone.getItem() instanceof GeoArmorItem)) {
				final ArmorItem armorItem = (ArmorItem) armorForBone.getItem();
				final BipedModel<?> armorModel = ForgeHooksClient.getArmorModel(currentEntityBeingRendered,
						armorForBone, boneSlot,
						boneSlot == EquipmentSlotType.LEGS ? DEFAULT_BIPED_ARMOR_MODEL_INNER
								: DEFAULT_BIPED_ARMOR_MODEL_OUTER);
				if (armorModel != null) {
					ModelRenderer sourceLimb = this.getArmorPartForBone(bone.getName(), armorModel);
					ObjectList<ModelRenderer.ModelBox> cubeList = sourceLimb.cubes;
					if (sourceLimb != null && cubeList != null && !cubeList.isEmpty()) {
						// IMPORTANT: The first cube is used to define the armor part!!
						this.prepareArmorPositionAndScale(bone, cubeList, sourceLimb, stack);
						stack.scale(-1, -1, 1);

						stack.pushPose();

						ResourceLocation armorResource = this.getArmorResource(currentEntityBeingRendered,
								armorForBone, boneSlot, null);

						this.renderArmorOfItem(armorItem, armorForBone, boneSlot, armorResource, sourceLimb, stack, packedLightIn, packedOverlayIn);

						stack.popPose();

						bufferIn = rtb.getBuffer(RenderType.entityTranslucent(currentTexture));
					}
				}
			}
			//Geo Armor
			else if (armorForBone.getItem() instanceof GeoArmorItem) {
				final GeoArmorItem armorItem = (GeoArmorItem) armorForBone.getItem();
				final BipedModel<?> armorModel = armorItem.getArmorModel(this.currentEntityBeingRendered, armorForBone, boneSlot, boneSlot == EquipmentSlotType.LEGS ? DEFAULT_BIPED_ARMOR_MODEL_INNER
								: DEFAULT_BIPED_ARMOR_MODEL_OUTER);
				if (armorModel != null) {
					ModelRenderer sourceLimb = this.getArmorPartForBone(bone.getName(), armorModel);
					ObjectList<ModelRenderer.ModelBox> cubeList = sourceLimb.cubes;
					if (sourceLimb != null && cubeList != null && !cubeList.isEmpty()) {
						// IMPORTANT: The first cube is used to define the armor part!!
						this.prepareArmorPositionAndScale(bone, cubeList, sourceLimb, stack);
						stack.scale(-1, -1, 1);

						stack.pushPose();

						GeoArmorRenderer<? extends GeoArmorItem> geoArmorRenderer = GeoArmorRenderer.getRenderer(armorItem.getClass());
						geoArmorRenderer.applySlot(boneSlot);
						geoArmorRenderer.fitToBiped();
						
						IVertexBuilder ivb = ItemRenderer.getArmorFoilBuffer(rtb, RenderType.armorCutoutNoCull(GeoArmorRenderer.getRenderer(armorItem.getClass()).getTextureLocation(armorItem)), false,
								armorForBone.hasFoil());
						
						geoArmorRenderer.render(0, stack, ivb, packedLightIn);
						

						stack.popPose();

						bufferIn = rtb.getBuffer(RenderType.entityTranslucent(currentTexture));
					}
				}
			}
			//Head blocks
			else if(armorForBone.getItem() instanceof BlockItem && ((BlockItem)armorForBone.getItem()).getBlock() instanceof AbstractSkullBlock) {
				this.HEAD_QUEUE.add(new Tuple<>(bone, armorForBone));
			}
		}
	}

	protected void renderArmorOfItem(ArmorItem armorItem, ItemStack armorForBone, EquipmentSlotType boneSlot, ResourceLocation armorResource, ModelRenderer sourceLimb, MatrixStack stack, int packedLightIn, int packedOverlayIn) {
		if (armorItem instanceof IDyeableArmorItem) {
			int i = ((net.minecraft.item.IDyeableArmorItem) armorItem).getColor(armorForBone);
			float r = (float) (i >> 16 & 255) / 255.0F;
			float g = (float) (i >> 8 & 255) / 255.0F;
			float b = (float) (i & 255) / 255.0F;

			renderArmorPart(stack, sourceLimb, packedLightIn, packedOverlayIn, r, g, b, 1,
					armorForBone, armorResource);
			renderArmorPart(stack, sourceLimb, packedLightIn, packedOverlayIn, 1, 1, 1, 1,
					armorForBone, getArmorResource(currentEntityBeingRendered, armorForBone,
							boneSlot, "overlay"));
		} else {
			renderArmorPart(stack, sourceLimb, packedLightIn, packedOverlayIn, 1, 1, 1, 1,
					armorForBone, armorResource);
		}
	}

	protected void prepareArmorPositionAndScale(GeoBone bone, ObjectList<ModelBox> cubeList, ModelRenderer sourceLimb, MatrixStack stack) {
		GeoCube firstCube = bone.childCubes.get(0);
		final ModelBox armorCube = cubeList.get(0);
		
		final float targetSizeX = firstCube.size.x();
		final float targetSizeY = firstCube.size.y();
		final float targetSizeZ = firstCube.size.z();
		
		final float sourceSizeX = Math.abs(armorCube.maxX - armorCube.minX);
		final float sourceSizeY = Math.abs(armorCube.maxY - armorCube.minY);
		final float sourceSizeZ = Math.abs(armorCube.maxZ - armorCube.minZ);
		
		float scaleX = targetSizeX / sourceSizeX;
		float scaleY = targetSizeY / sourceSizeY;
		float scaleZ = targetSizeZ / sourceSizeZ;

		//Modify position to move point to correct location, otherwise it will be off when the sizes are different
		sourceLimb.setPos(-(bone.getPivotX() + sourceSizeX - targetSizeX), -(bone.getPivotY() + sourceSizeY - targetSizeY), (bone.getPivotZ() + sourceSizeZ - targetSizeZ));
		
		sourceLimb.xRot = -bone.getRotationX();
		sourceLimb.yRot = -bone.getRotationY();
		sourceLimb.zRot = bone.getRotationZ();
		
		stack.scale(scaleX, scaleY, scaleZ);
	}

	// Internal use only. Basically renders the passed "part" of the armor model on
	// a pre-setup location
	protected void renderArmorPart(MatrixStack stack, ModelRenderer sourceLimb, int packedLightIn, int packedOverlayIn,
			float red, float green, float blue, float alpha, ItemStack armorForBone, ResourceLocation armorResource) {
		IVertexBuilder ivb = ItemRenderer.getArmorFoilBuffer(rtb, RenderType.armorCutoutNoCull(armorResource), false,
				armorForBone.hasFoil());
		sourceLimb.render(stack, ivb, packedLightIn, packedOverlayIn, red, green, blue, alpha);
	}

	/*
	 * Return null, if the entity's texture is used ALso doesn't work yet, or, well,
	 * i haven't tested it, so, maybe it works...
	 */
	@Nullable
	protected abstract ResourceLocation getTextureForBone(String boneName, T currentEntity);

	protected void renderBlock(MatrixStack matrixStack, IRenderTypeBuffer rtb, int packedLightIn,
			BlockState iBlockState) {
		// TODO: Re-implement, doesn't do anyhting yet
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
	protected EquipmentSlotType getEquipmentSlotForArmorBone(String boneName, T currentEntity) {
		return null;
	}

	@Nullable
	protected ModelRenderer getArmorPartForBone(String name, BipedModel<?> armorModel) {
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

	protected ResourceLocation getArmorResource(net.minecraft.entity.Entity entity, ItemStack stack,
			EquipmentSlotType slot, String type) {
		ArmorItem item = (ArmorItem) stack.getItem();
		String texture = item.getMaterial().getName();
		String domain = "minecraft";
		int idx = texture.indexOf(':');
		if (idx != -1) {
			domain = texture.substring(0, idx);
			texture = texture.substring(idx + 1);
		}
		String s1 = String.format("%s:textures/models/armor/%s_layer_%d%s.png", domain, texture,
				(slot == EquipmentSlotType.LEGS ? 2 : 1), type == null ? "" : String.format("_%s", type));

		s1 = net.minecraftforge.client.ForgeHooksClient.getArmorTexture(entity, stack, s1, slot, type);
		ResourceLocation resourcelocation = (ResourceLocation) ARMOR_TEXTURE_RES_MAP.get(s1);

		if (resourcelocation == null) {
			resourcelocation = new ResourceLocation(s1);
			ARMOR_TEXTURE_RES_MAP.put(s1, resourcelocation);
		}

		return resourcelocation;
	}

}
