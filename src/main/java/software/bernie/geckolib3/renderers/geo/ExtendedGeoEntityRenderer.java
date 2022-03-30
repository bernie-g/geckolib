package software.bernie.geckolib3.renderers.geo;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.function.Function;

import javax.annotation.Nullable;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import it.unimi.dsi.fastutil.objects.ObjectList;
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
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.IDyeableArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ForgeHooksClient;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.geo.render.built.GeoCube;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;

/**
 * @author DerToaster98
 * Copyright (c) 30.03.2022 Developed by DerToaster98 GitHub: https://github.com/DerToaster98
 * 
 * Purpose of this class: This class is a extended version of {@code GeoEnttiyRenderer}. It automates the process of rendering items at hand bones as well as standard armor at certain bones. The model must feature a few special bones for this to work.
 */
@OnlyIn(Dist.CLIENT)
public abstract class ExtendedGeoEntityRenderer<T extends LivingEntity & IAnimatable> extends GeoEntityRenderer<T> {
	
	static enum EModelRenderCycle {
		INITIAL,
		REPEATED
	}

	//TODO: Replace with AT, couldn't get that to work sadly so i used reflect instead
	protected static Field FIELD_CUBES = null;
	static final String FIELD_NAME_CUBES_OBF = "field_78804_l ";
	static final String FIELD_NAME_CUBES_DEOBF = "cubes";
	
	static {
		try {
			FIELD_CUBES = ModelRenderer.class.getDeclaredField(FIELD_NAME_CUBES_OBF);
		} catch (NoSuchFieldException e) {
			try {
				FIELD_CUBES = ModelRenderer.class.getDeclaredField(FIELD_NAME_CUBES_DEOBF);
			} catch (NoSuchFieldException e1) {
				e1.printStackTrace();
			} catch (SecurityException e1) {
				e1.printStackTrace();
			}
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		if(FIELD_CUBES != null) {
			FIELD_CUBES.setAccessible(true);
		}
	}
	
	protected float widthScale;
	protected float heightScale;

	public final Function<T, ResourceLocation> TEXTURE_GETTER;
	public final Function<T, ResourceLocation> MODEL_ID_GETTER;

	protected ExtendedGeoEntityRenderer(EntityRendererManager renderManager, AnimatedGeoModel<T> modelProvider) {
		this(renderManager, modelProvider, 1F, 1F, 0);
	}

	@SuppressWarnings("resource")
	protected void bindTexture(ResourceLocation textureLocation) {
		Minecraft.getInstance().textureManager.bind(textureLocation);
	}

	protected ExtendedGeoEntityRenderer(EntityRendererManager renderManager, AnimatedGeoModel<T> modelProvider, float widthScale, float heightScale, float shadowSize) {
		super(renderManager, modelProvider);

		this.MODEL_ID_GETTER = modelProvider::getModelLocation;
		this.TEXTURE_GETTER = modelProvider::getTextureLocation;

		this.shadowRadius = shadowSize;
		this.widthScale = widthScale;
		this.heightScale = heightScale;
	}

	/*
	 * 0 => Normal model 1 => Magical armor overlay
	 */
	private EModelRenderCycle currentModelRenderCycle = EModelRenderCycle.INITIAL;

	// Entrypoint for rendering, calls everything else
	@Override
	public void render(T entity, float entityYaw, float partialTicks, MatrixStack stack, IRenderTypeBuffer bufferIn, int packedLightIn) {
		this.currentModelRenderCycle = EModelRenderCycle.INITIAL;
		super.render(entity, entityYaw, partialTicks, stack, bufferIn, packedLightIn);
	}

	// Rendercall to render the model itself
	@Override
	public void render(GeoModel model, T animatable, float partialTicks, RenderType type, MatrixStack matrixStackIn, IRenderTypeBuffer renderTypeBuffer, IVertexBuilder vertexBuilder, int packedLightIn, int packedOverlayIn, float red, float green,
			float blue, float alpha) {
		super.render(model, animatable, partialTicks, type, matrixStackIn, renderTypeBuffer, vertexBuilder, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		this.currentModelRenderCycle = EModelRenderCycle.REPEATED;
	}

	protected float getWidthScale(T entity) {
		return this.widthScale;
	}

	protected float getHeightScale(T entity) {
		return this.heightScale;
	}

	@Override
	public void renderEarly(T animatable, MatrixStack stackIn, float ticks, IRenderTypeBuffer renderTypeBuffer, IVertexBuilder vertexBuilder, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float partialTicks) {
		this.rtb = renderTypeBuffer;
		super.renderEarly(animatable, stackIn, ticks, renderTypeBuffer, vertexBuilder, packedLightIn, packedOverlayIn, red, green, blue, partialTicks);
		if (this.currentModelRenderCycle == EModelRenderCycle.INITIAL /* Pre-Layers */) {
			float width = this.getWidthScale(animatable);
			float height = this.getHeightScale(animatable);
			stackIn.scale(width, height, width);
		}
	}

	@Override
	public ResourceLocation getTextureLocation(T entity) {
		return this.TEXTURE_GETTER.apply(entity);
	}

	private T currentEntityBeingRendered;
	private IRenderTypeBuffer rtb;

	@Override
	public void renderLate(T animatable, MatrixStack stackIn, float ticks, IRenderTypeBuffer renderTypeBuffer, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float partialTicks) {
		super.renderLate(animatable, stackIn, ticks, renderTypeBuffer, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, partialTicks);
		this.currentEntityBeingRendered = animatable;
	}

	protected final BipedModel<LivingEntity> DEFAULT_BIPED_ARMOR_MODEL_INNER = new BipedModel<>(0.5F);
	protected final BipedModel<LivingEntity> DEFAULT_BIPED_ARMOR_MODEL_OUTER = new BipedModel<>(1.0F);

	@Nullable
	protected ObjectList<ModelRenderer.ModelBox> accessCubesOf(ModelRenderer mr) {
		if(FIELD_CUBES == null) {
			return null;
		}
		try {
			Object o = FIELD_CUBES.get(mr);
			try {
				@SuppressWarnings("unchecked")
				ObjectList<ModelRenderer.ModelBox> ret = (ObjectList<ModelRenderer.ModelBox>) o;
				return ret;
			} catch(ClassCastException cce) {
				cce.printStackTrace();
				return null;
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public void renderRecursively(GeoBone bone, MatrixStack stack, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
		boolean customTextureMarker = this.currentModelRenderCycle == EModelRenderCycle.INITIAL && this.getTextureForBone(bone.getName(), this.currentEntityBeingRendered) != null;
		ResourceLocation currentTexture = this.getTextureLocation(this.currentEntityBeingRendered);
		if (customTextureMarker) {
			currentTexture = this.getTextureForBone(bone.getName(), this.currentEntityBeingRendered);
			this.bindTexture(currentTexture);
		}
		if (this.currentModelRenderCycle == EModelRenderCycle.INITIAL) {
			stack.pushPose();

			// Render armor
			if (bone.getName().startsWith("armor")) {
				final ItemStack armorForBone = this.getArmorForBone(bone.getName(), currentEntityBeingRendered);
				final EquipmentSlotType boneSlot = this.getEquipmentSlotForArmorBone(bone.getName(), currentEntityBeingRendered);
				if (armorForBone != null && armorForBone.getItem() instanceof ArmorItem && boneSlot != null) {
					final ArmorItem armorItem = (ArmorItem) armorForBone.getItem();
					final BipedModel armorModel = ForgeHooksClient.getArmorModel(currentEntityBeingRendered, armorForBone, boneSlot, boneSlot == EquipmentSlotType.LEGS ? DEFAULT_BIPED_ARMOR_MODEL_INNER : DEFAULT_BIPED_ARMOR_MODEL_OUTER);
					if (armorModel != null) {
						ModelRenderer sourceLimb = this.getArmorPartForBone(bone.getName(), armorModel);
						ObjectList<ModelRenderer.ModelBox> cubeList = accessCubesOf(sourceLimb);
						if (sourceLimb != null && cubeList != null && !cubeList.isEmpty()) {
							// IMPORTANT: The first cube is used to define the armor part!!
							GeoCube firstCube = bone.childCubes.get(0);
							final float targetSizeX = firstCube.size.x();
							final float targetSizeY = firstCube.size.y();
							final float targetSizeZ = firstCube.size.z();
							final ModelBox armorCube = cubeList.get(0);
							float scaleX = targetSizeX / Math.abs(armorCube.maxX - armorCube.minX);
							float scaleY = targetSizeY / Math.abs(armorCube.maxY - armorCube.minY);
							float scaleZ = targetSizeZ / Math.abs(armorCube.maxZ - armorCube.minZ);

							sourceLimb.setPos(-bone.getPivotX(), -bone.getPivotY(), bone.getPivotZ());
							sourceLimb.xRot = -bone.getRotationX();
							sourceLimb.yRot = -bone.getRotationY();
							sourceLimb.zRot = bone.getRotationZ();
							stack.scale(-1, -1, 1);

							stack.pushPose();

							stack.scale(scaleX, scaleY, scaleZ);

							ResourceLocation armorResource = this.getArmorResource(currentEntityBeingRendered, armorForBone, boneSlot, null);
							this.bindTexture(armorResource);

							if (armorItem instanceof IDyeableArmorItem) {
								int i = ((net.minecraft.item.IDyeableArmorItem) armorItem).getColor(armorForBone);
								float r = (float) (i >> 16 & 255) / 255.0F;
								float g = (float) (i >> 8 & 255) / 255.0F;
								float b = (float) (i & 255) / 255.0F;
								
								renderArmorPart(stack, sourceLimb, packedLightIn, packedOverlayIn, r, g, b, 1, armorForBone, armorResource);
								renderArmorPart(stack, sourceLimb, packedLightIn, packedOverlayIn, 1, 1, 1, 1, armorForBone, getArmorResource(currentEntityBeingRendered, armorForBone, boneSlot, "overlay"));
							} else {
								renderArmorPart(stack, sourceLimb, packedLightIn, packedOverlayIn, 1, 1, 1, 1, armorForBone, armorResource);
							}

							stack.popPose();

							this.bindTexture(currentTexture);
							bufferIn = rtb.getBuffer(RenderType.entityTranslucent(currentTexture));
						}
					}
				}
			} else {
				ItemStack boneItem = this.getHeldItemForBone(bone.getName(), this.currentEntityBeingRendered);
				BlockState boneBlock = this.getHeldBlockForBone(bone.getName(), this.currentEntityBeingRendered);
				if (boneItem != null || boneBlock != null) {

					stack.pushPose();
					// First, let's move our render position to the pivot point...
					stack.translate(bone.getPivotX() / 16, bone.getPivotY() / 16, bone.getPositionZ() / 16);

					stack.mulPose(Vector3f.XP.rotationDegrees(bone.getRotationX()));
					stack.mulPose(Vector3f.YP.rotationDegrees(bone.getRotationY()));
					stack.mulPose(Vector3f.ZP.rotationDegrees(bone.getRotationZ()));

					if (boneItem != null) {
						this.preRenderItem(stack, boneItem, bone.getName(), this.currentEntityBeingRendered, bone);

						Minecraft.getInstance().getItemInHandRenderer().renderItem(currentEntityBeingRendered, boneItem, this.getCameraTransformForItemAtBone(boneItem, bone.getName()), false, stack, rtb, packedLightIn);

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

		super.renderRecursively(bone, stack, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);

		if (customTextureMarker) {
			this.bindTexture(this.getTextureLocation(this.currentEntityBeingRendered));
		}
	}

	//Internal use only. Basically renders the passed "part" of the armor model on a pre-setup location
	protected void renderArmorPart(MatrixStack stack, ModelRenderer sourceLimb, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha, ItemStack armorForBone, ResourceLocation armorResource) {
		IVertexBuilder ivb = ItemRenderer.getArmorFoilBuffer(rtb, RenderType.armorCutoutNoCull(armorResource), false, armorForBone.hasFoil());
		sourceLimb.render(stack, ivb, packedLightIn, packedOverlayIn, red, green, blue, alpha);
	}

	/*
	 * Return null, if the entity's texture is used
	 */
	@Nullable
	protected abstract ResourceLocation getTextureForBone(String boneName, T currentEntity);

	protected void renderBlock(MatrixStack matrixStack, IRenderTypeBuffer rtb, int packedLightIn, BlockState iBlockState) {
		//TODO: Re-implement, doesn't do anyhting yet
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

	protected abstract void preRenderItem(MatrixStack matrixStack, ItemStack item, String boneName, T currentEntity, IBone bone);

	protected abstract void preRenderBlock(BlockState block, String boneName, T currentEntity);

	protected abstract void postRenderItem(MatrixStack matrixStack, ItemStack item, String boneName, T currentEntity, IBone bone);

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
	protected ModelRenderer getArmorPartForBone(String name, BipedModel armorModel) {
		return null;
	}

	//Copied from BipedArmorLayer
	/**
	 * More generic ForgeHook version of the above function, it allows for Items to have more control over what texture they provide.
	 *
	 * @param entity Entity wearing the armor
	 * @param stack  ItemStack for the armor
	 * @param slot   Slot ID that the item is in
	 * @param type   Subtype, can be null or "overlay"
	 * @return ResourceLocation pointing at the armor's texture
	 */
	private static final Map<String, ResourceLocation> ARMOR_TEXTURE_RES_MAP = Maps.newHashMap();

	protected ResourceLocation getArmorResource(net.minecraft.entity.Entity entity, ItemStack stack, EquipmentSlotType slot, String type) {
		ArmorItem item = (ArmorItem) stack.getItem();
		String texture = item.getMaterial().getName();
		String domain = "minecraft";
		int idx = texture.indexOf(':');
		if (idx != -1) {
			domain = texture.substring(0, idx);
			texture = texture.substring(idx + 1);
		}
		String s1 = String.format("%s:textures/models/armor/%s_layer_%d%s.png", domain, texture, (slot == EquipmentSlotType.LEGS ? 2 : 1), type == null ? "" : String.format("_%s", type));

		s1 = net.minecraftforge.client.ForgeHooksClient.getArmorTexture(entity, stack, s1, slot, type);
		ResourceLocation resourcelocation = (ResourceLocation) ARMOR_TEXTURE_RES_MAP.get(s1);

		if (resourcelocation == null) {
			resourcelocation = new ResourceLocation(s1);
			ARMOR_TEXTURE_RES_MAP.put(s1, resourcelocation);
		}

		return resourcelocation;
	}

}
