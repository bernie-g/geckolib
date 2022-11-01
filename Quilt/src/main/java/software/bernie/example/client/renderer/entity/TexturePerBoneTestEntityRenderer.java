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
import software.bernie.example.client.EntityResources;
import software.bernie.example.client.model.entity.TexturePerBoneTestEntityModel;
import software.bernie.example.entity.TexturePerBoneTestEntity;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3q.geo.render.built.GeoBone;
import software.bernie.geckolib3q.renderers.geo.ExtendedGeoEntityRenderer;

public class TexturePerBoneTestEntityRenderer extends ExtendedGeoEntityRenderer<TexturePerBoneTestEntity> {

	public TexturePerBoneTestEntityRenderer(EntityRendererProvider.Context renderManager) {
		super(renderManager, new TexturePerBoneTestEntityModel<TexturePerBoneTestEntity>(EntityResources.TEXTUREPERBONE_MODEL, EntityResources.TEXTUREPERBONE_TEXTURE, "textureperbonetestentity"));
	}

	@Override
	protected boolean isArmorBone(GeoBone bone) {
		return false;
	}
	
	@Override
	public RenderType getRenderType(TexturePerBoneTestEntity animatable, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, VertexConsumer buffer, int packedLight, ResourceLocation texture) {
		return RenderType.entityTranslucent(texture);
	}
	
	@Override
	protected ResourceLocation getTextureForBone(String boneName, TexturePerBoneTestEntity animatable) {
		if(boneName.equalsIgnoreCase("outer_glass")) {
			return EntityResources.TEXTUREPERBONE_GLASS_TEXTURE;
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
	protected void preRenderItem(PoseStack matrixStack, ItemStack item, String boneName, TexturePerBoneTestEntity currentEntity, IBone bone) {
		
	}

	@Override
	protected void preRenderBlock(PoseStack matrixStack, BlockState block, String boneName, TexturePerBoneTestEntity currentEntity) {
		
	}

	@Override
	protected void postRenderItem(PoseStack matrixStack, ItemStack item, String boneName, TexturePerBoneTestEntity currentEntity, IBone bone) {
		
	}

	@Override
	protected void postRenderBlock(PoseStack matrixStack, BlockState block, String boneName, TexturePerBoneTestEntity currentEntity) {
		
	}

}
