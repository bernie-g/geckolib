package software.bernie.geckolib.renderer.layer;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.SkullModelBase;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.ModelPart.Cube;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.AbstractSkullBlock;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.RenderProvider;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.cache.object.GeoCube;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.util.RenderUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

/**
 * Builtin class for handling dynamic armor rendering on GeckoLib entities
 * <p>
 * Supports both {@link GeoItem GeckoLib} and {@link ArmorItem Vanilla} armor models
 * <p>
 * Unlike a traditional armor renderer, this renderer renders per-bone, giving much more flexible armor rendering
 */
public class ItemArmorGeoLayer<T extends LivingEntity & GeoAnimatable> extends GeoRenderLayer<T> {
	protected static final Map<String, ResourceLocation> ARMOR_PATH_CACHE = new Object2ObjectOpenHashMap<>();
	protected static final HumanoidModel<LivingEntity> INNER_ARMOR_MODEL = new HumanoidModel<>(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.PLAYER_INNER_ARMOR));
	protected static final HumanoidModel<LivingEntity> OUTER_ARMOR_MODEL = new HumanoidModel<>(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR));

	@Nullable protected ItemStack mainHandStack;
	@Nullable protected ItemStack offhandStack;
	@Nullable protected ItemStack helmetStack;
	@Nullable protected ItemStack chestplateStack;
	@Nullable protected ItemStack leggingsStack;
	@Nullable protected ItemStack bootsStack;

	public ItemArmorGeoLayer(GeoRenderer<T> geoRenderer) {
		super(geoRenderer);
	}

	/**
	 * Return an EquipmentSlot for a given {@link ItemStack} and animatable instance
	 * <p>
	 * This is what determines the base model to use for rendering a particular stack
	 */
	@Nonnull
	protected EquipmentSlot getEquipmentSlotForBone(GeoBone bone, ItemStack stack, T animatable) {
		for (EquipmentSlot slot : EquipmentSlot.values()) {
			if (slot.getType() == EquipmentSlot.Type.ARMOR) {
				if (stack == animatable.getItemBySlot(slot))
					return slot;
			}
		}

		return EquipmentSlot.CHEST;
	}

	/**
	 * Return a ModelPart for a given {@link GeoBone}
	 * <p>
	 * This is then transformed into position for the final render
	 */
	@Nonnull
	protected ModelPart getModelPartForBone(GeoBone bone, EquipmentSlot slot, ItemStack stack, T animatable, HumanoidModel<?> baseModel) {
		return baseModel.body;
	}

	/**
	 * Get the {@link ItemStack} relevant to the bone being rendered
	 *
	 * @return The ItemStack for the bone being rendered, or null if this bone should be ignored
	 */
	@Nullable
	protected ItemStack getArmorItemForBone(GeoBone bone, T animatable) {
		return null;
	}

	/**
	 * This method is called by the {@link GeoRenderer} before rendering, immediately after {@link GeoRenderer#preRender} has been called
	 * <p>
	 * This allows for RenderLayers to perform pre-render manipulations such as hiding or showing bones
	 */
	@Override
	public void preRender(PoseStack poseStack, T animatable, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource,
						  VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
		this.mainHandStack = animatable.getItemBySlot(EquipmentSlot.MAINHAND);
		this.offhandStack = animatable.getItemBySlot(EquipmentSlot.OFFHAND);
		this.helmetStack = animatable.getItemBySlot(EquipmentSlot.HEAD);
		this.chestplateStack = animatable.getItemBySlot(EquipmentSlot.CHEST);
		this.leggingsStack = animatable.getItemBySlot(EquipmentSlot.LEGS);
		this.bootsStack = animatable.getItemBySlot(EquipmentSlot.FEET);
	}

	/**
	 * This method is called by the {@link GeoRenderer} for each bone being rendered.
	 * <p>
	 * This is a more expensive call, particularly if being used to render something on a different buffer<br>
	 * It does however have the benefit of having the matrix translations and other transformations already applied from render-time<br>
	 * It's recommended to avoid using this unless necessary
	 * <p>
	 * The {@link GeoBone} in question has already been rendered by this stage.
	 * <p>
	 * If you <i>do</i> use it, and you render something that changes the {@link VertexConsumer buffer}, you need to reset it back to the previous buffer
	 * using {@link MultiBufferSource#getBuffer} before ending the method
	 */
	@Override
	public void renderForBone(PoseStack poseStack, T animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource,
							  VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
		ItemStack armorStack = getArmorItemForBone(bone, animatable);

		if (armorStack == null)
			return;

		if (armorStack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof AbstractSkullBlock skullBlock) {
			renderSkullAsArmor(poseStack, bone, armorStack, skullBlock, bufferSource, packedLight);
		}
		else {
			EquipmentSlot slot = getEquipmentSlotForBone(bone, armorStack, animatable);
			HumanoidModel<?> model = getModelForItem(bone, slot, armorStack, animatable);
			ModelPart modelPart = getModelPartForBone(bone, slot, armorStack, animatable, model);

			if (!modelPart.cubes.isEmpty()) {
				poseStack.pushPose();
				poseStack.scale(-1, -1, 1);

				if (model instanceof GeoArmorRenderer<?> geoArmorRenderer) {
					prepModelPartForRender(poseStack, bone, modelPart);
					geoArmorRenderer.prepForRender(animatable, armorStack, slot, model);
					geoArmorRenderer.applyBoneVisibilityByPart(slot, modelPart, model);
					geoArmorRenderer.renderToBuffer(poseStack, null, packedLight, packedOverlay, 1, 1, 1, 1);
				}
				else if (armorStack.getItem() instanceof ArmorItem) {
					prepModelPartForRender(poseStack, bone, modelPart);
					renderVanillaArmorPiece(poseStack, animatable, bone, slot, armorStack, modelPart, bufferSource, partialTick, packedLight, packedOverlay);
				}

				poseStack.popPose();
			}
		}

		buffer = bufferSource.getBuffer(renderType);
	}

	/**
	 * Renders an individual armor piece base on the given {@link GeoBone} and {@link ItemStack}
	 */
	protected <I extends Item & GeoItem> void renderVanillaArmorPiece(PoseStack poseStack, T animatable, GeoBone bone, EquipmentSlot slot, ItemStack armorStack,
															   ModelPart modelPart, MultiBufferSource bufferSource, float partialTick, int packedLight, int packedOverlay) {
			ResourceLocation texture = getVanillaArmorResource(animatable, armorStack, slot, "");
			VertexConsumer buffer = getArmorBuffer(bufferSource, null, texture, armorStack.hasFoil());

			if (armorStack.getItem() instanceof DyeableArmorItem dyable) {
				int color = dyable.getColor(armorStack);

				modelPart.render(poseStack, buffer, packedLight, packedOverlay, (color >> 16 & 255) / 255f, (color >> 8 & 255) / 255f, (color & 255) / 255f, 1);

				texture = getVanillaArmorResource(animatable, armorStack, slot, "overlay");
				buffer = getArmorBuffer(bufferSource, null, texture, false);
			}

			modelPart.render(poseStack, buffer, packedLight, packedOverlay, 1, 1, 1, 1);
	}

	/**
	 * Returns the standard VertexConsumer for armor rendering from the given buffer source.
	 *
	 * @param bufferSource The BufferSource to draw the buffer from
	 * @param renderType The RenderType to use for rendering, or null to use the default
	 * @param texturePath The texture path for the render. May be null if renderType is not null
	 * @param enchanted Whether the render should have an enchanted glint or not
	 * @return The buffer to draw to
	 */
	protected VertexConsumer getArmorBuffer(MultiBufferSource bufferSource, @Nullable RenderType renderType, @Nullable ResourceLocation texturePath, boolean enchanted) {
		if (renderType == null)
			renderType = RenderType.armorCutoutNoCull(texturePath);

		return ItemRenderer.getArmorFoilBuffer(bufferSource, renderType, false, enchanted);
	}

	/**
	 * Returns a cached instance of a base HumanoidModel that is used for rendering/modelling the provided {@link ItemStack}
	 */
	@Nonnull
	protected HumanoidModel<?> getModelForItem(GeoBone bone, EquipmentSlot slot, ItemStack stack, T animatable) {
		HumanoidModel<LivingEntity> defaultModel = slot == EquipmentSlot.LEGS ? INNER_ARMOR_MODEL : OUTER_ARMOR_MODEL;
		
		return RenderProvider.of(stack).getHumanoidArmorModel(animatable, stack, slot, defaultModel);
	}

	/**
	 * Gets a cached resource path for the vanilla armor layer texture for this armor piece.
	 * <p>
	 * Equivalent to {@link net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer#getArmorLocation HumanoidArmorLayer.getArmorLocation}
	 */
	public ResourceLocation getVanillaArmorResource(Entity entity, ItemStack stack, EquipmentSlot slot, String type) {
		String domain = "minecraft";
		String path = ((ArmorItem) stack.getItem()).getMaterial().getName();
		String[] materialNameSplit = path.split(":", 2);

		if (materialNameSplit.length > 1) {
			domain = materialNameSplit[0];
			path = materialNameSplit[1];
		}

		if (!type.isBlank())
			type = "_" + type;

		String texture = String.format("%s:textures/models/armor/%s_layer_%d%s.png", domain, path, (slot == EquipmentSlot.LEGS ? 2 : 1), type);
		return ARMOR_PATH_CACHE.computeIfAbsent(texture, ResourceLocation::new);
	}

	/**
	 * Render a given {@link AbstractSkullBlock} as a worn armor piece in relation to a given {@link GeoBone}
	 */
	protected void renderSkullAsArmor(PoseStack poseStack, GeoBone bone, ItemStack stack, AbstractSkullBlock skullBlock, MultiBufferSource bufferSource, int packedLight) {
		GameProfile skullProfile = null;
		CompoundTag stackTag = stack.getTag();

		if (stackTag != null) {
			Tag skullTag = stackTag.get(PlayerHeadItem.TAG_SKULL_OWNER);

			if (skullTag instanceof CompoundTag compoundTag) {
				skullProfile = NbtUtils.readGameProfile(compoundTag);
			}
			else if (skullTag instanceof StringTag tag) {
				String skullOwner = tag.getAsString();

				if (!skullOwner.isBlank()) {
					CompoundTag profileTag = new CompoundTag();

					SkullBlockEntity.updateGameprofile(new GameProfile(null, skullOwner), name ->
							stackTag.put(PlayerHeadItem.TAG_SKULL_OWNER, NbtUtils.writeGameProfile(profileTag, name)));

					skullProfile = NbtUtils.readGameProfile(profileTag);
				}
			}
		}

		SkullBlock.Type type = skullBlock.getType();
		SkullModelBase model = SkullBlockRenderer.createSkullRenderers(Minecraft.getInstance().getEntityModels()).get(type);
		RenderType renderType = SkullBlockRenderer.getRenderType(type, skullProfile);

		poseStack.pushPose();
		RenderUtils.translateAndRotateMatrixForBone(poseStack, bone);
		poseStack.scale(1.1875f, 1.1875f, 1.1875f);
		poseStack.translate(-0.5f, 0, -0.5f);
		SkullBlockRenderer.renderSkull(null, 0, 0, poseStack, bufferSource, packedLight, model, renderType);
		poseStack.popPose();
	}

	/**
	 * Prepares the given {@link ModelPart} for render by setting its translation, position, and rotation values based on the provided {@link GeoBone}
	 *
	 * @param poseStack The PoseStack being used for rendering
	 * @param bone The GeoBone to base the translations on
	 * @param sourcePart The ModelPart to translate
	 */
	protected void prepModelPartForRender(PoseStack poseStack, GeoBone bone, ModelPart sourcePart) {
		final GeoCube firstCube = bone.getCubes().get(0);
		final Cube armorCube = getReferenceCubeForModel(bone, sourcePart);
		final double armorBoneSizeX = firstCube.size().x();
		final double armorBoneSizeY = firstCube.size().y();
		final double armorBoneSizeZ = firstCube.size().z();
		final double actualArmorSizeX = Math.abs(armorCube.maxX - armorCube.minX);
		final double actualArmorSizeY = Math.abs(armorCube.maxY - armorCube.minY);
		final double actualArmorSizeZ = Math.abs(armorCube.maxZ - armorCube.minZ);
		float scaleX = (float)(armorBoneSizeX / actualArmorSizeX);
		float scaleY = (float)(armorBoneSizeY / actualArmorSizeY);
		float scaleZ = (float)(armorBoneSizeZ / actualArmorSizeZ);

		sourcePart.setPos(-(bone.getPivotX() - ((bone.getPivotX() * scaleX) - bone.getPivotX()) / scaleX),
				-(bone.getPivotY() - ((bone.getPivotY() * scaleY) - bone.getPivotY()) / scaleY),
				(bone.getPivotZ() - ((bone.getPivotZ() * scaleZ) - bone.getPivotZ()) / scaleZ));

		sourcePart.xRot = -bone.getRotX();
		sourcePart.yRot = -bone.getRotY();
		sourcePart.zRot = bone.getRotZ();

		poseStack.scale(scaleX, scaleY, scaleZ);
	}

	/**
	 * Get the cube from the source part that GeckoLib should use as the reference cube for scaling the GeckoLib bone
	 * for rendering.
	 * <p>
	 * By default, this uses the <b><u>FIRST</u></b> cube in the source part
	 * to determine the scale and position of the GeoArmor to be rendered
	 */
	protected Cube getReferenceCubeForModel(GeoBone bone, ModelPart sourcePart) {
		return sourcePart.cubes.get(0);
	}
}
