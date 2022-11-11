/*
package software.bernie.example.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.example.client.model.entity.TexturePerBoneTestEntityModel;
import software.bernie.example.entity.TexturePerBoneTestEntity;
import software.bernie.geckolib3.GeckoLib;
import software.bernie.geckolib3.core.animatable.model.GeoBone;
import software.bernie.geckolib3.renderer.ExtendedGeoEntityRenderer;


public class TexturePerBoneTestEntityRenderer extends ExtendedGeoEntityRenderer<TexturePerBoneTestEntity> {
	
	private static final ResourceLocation TEXTURE = new ResourceLocation("minecraft",
			"textures/block/redstone_block.png");
	private static final ResourceLocation TEXTURE_GLASS = new ResourceLocation("minecraft",
			"textures/block/white_stained_glass.png");
	private static final ResourceLocation MODEL_RESLOC = new ResourceLocation(GeckoLib.MOD_ID,
			"geo/textureperbonetestentity.geo.json");

	public TexturePerBoneTestEntityRenderer(EntityRendererProvider.Context renderManager) {
		super(renderManager, new TexturePerBoneTestEntityModel<TexturePerBoneTestEntity>(MODEL_RESLOC, TEXTURE, "textureperbonetestentity"));
	}

	@Override
	protected boolean isArmorBone(software.bernie.geckolib3.cache.object.GeoBone bone) {
		return false;
	}
	
	@Override
	public RenderType getRenderType(PoseStack poseStack, TexturePerBoneTestEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight) {
		return RenderType.entityTranslucent(texture);
	}
	
	@Override
	protected ResourceLocation getTextureForBone(String boneName, TexturePerBoneTestEntity animatable) {
		if(boneName.equalsIgnoreCase("outer_glass")) {
			return TEXTURE_GLASS;
		}
		return null;
	}

	@Override
	protected ItemStack getHeldItemForBone(String boneName, TexturePerBoneTestEntity currentEntity) {
		return null;
	}

	@Override
	protected TransformType getCameraTransformForItemAtBone(ItemStack boneItem, String boneName) {
		return null;
	}

	@Override
	protected BlockState getHeldBlockForBone(String boneName, TexturePerBoneTestEntity currentEntity) {
		return null;
	}

	@Override
	protected void preRenderItem(PoseStack matrixStack, ItemStack item, String boneName, TexturePerBoneTestEntity currentEntity, GeoBone bone) {
		
	}

	@Override
	protected void preRenderBlock(PoseStack matrixStack, BlockState block, String boneName, TexturePerBoneTestEntity currentEntity) {
		
	}

	@Override
	protected void postRenderItem(PoseStack matrixStack, ItemStack item, String boneName, TexturePerBoneTestEntity currentEntity, GeoBone bone) {
		
	}

	@Override
	protected void postRenderBlock(PoseStack matrixStack, BlockState block, String boneName, TexturePerBoneTestEntity currentEntity) {
		
	}

}
*/
