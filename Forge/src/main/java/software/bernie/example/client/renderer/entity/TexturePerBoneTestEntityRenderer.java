package software.bernie.example.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import software.bernie.example.client.model.entity.TexturePerBoneTestEntityModel;
import software.bernie.example.entity.TexturePerBoneTestEntity;
import software.bernie.geckolib3.GeckoLib;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.renderers.geo.ExtendedGeoEntityRenderer;

public class TexturePerBoneTestEntityRenderer extends ExtendedGeoEntityRenderer<TexturePerBoneTestEntity> {
	
	private static final ResourceLocation TEXTURE = new ResourceLocation("minecraft",
			"textures/block/redstone_block.png");
	private static final ResourceLocation TEXTURE_GLASS = new ResourceLocation("minecraft",
			"textures/block/white_stained_glass.png");
	private static final ResourceLocation MODEL_RESLOC = new ResourceLocation(GeckoLib.ModID,
			"geo/textureperbonetestentity.geo.json");

	public TexturePerBoneTestEntityRenderer(EntityRendererManager renderManager) {
		super(renderManager, new TexturePerBoneTestEntityModel<TexturePerBoneTestEntity>(MODEL_RESLOC, TEXTURE, "textureperbonetestentity"));
	}

	@Override
	protected boolean isArmorBone(GeoBone bone) {
		return false;
	}
	
	@Override
	public RenderType getRenderType(TexturePerBoneTestEntity animatable, float partialTicks, MatrixStack stack, IRenderTypeBuffer renderTypeBuffer, IVertexBuilder vertexBuilder, int packedLightIn, ResourceLocation textureLocation) {
		return RenderType.entityTranslucent(textureLocation);
	}
	
	@Override
	protected ResourceLocation getTextureForBone(String boneName, TexturePerBoneTestEntity currentEntity) {
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
	protected void preRenderItem(MatrixStack matrixStack, ItemStack item, String boneName, TexturePerBoneTestEntity currentEntity, IBone bone) {
		
	}

	@Override
	protected void preRenderBlock(MatrixStack matrixStack, BlockState block, String boneName, TexturePerBoneTestEntity currentEntity) {
		
	}

	@Override
	protected void postRenderItem(MatrixStack matrixStack, ItemStack item, String boneName, TexturePerBoneTestEntity currentEntity, IBone bone) {
		
	}

	@Override
	protected void postRenderBlock(MatrixStack matrixStack, BlockState block, String boneName, TexturePerBoneTestEntity currentEntity) {
		
	}

}
