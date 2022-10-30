package software.bernie.example.client.renderer.entity;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformation.Mode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import software.bernie.example.client.model.entity.TexturePerBoneTestEntityModel;
import software.bernie.example.entity.TexturePerBoneTestEntity;
import software.bernie.geckolib3.GeckoLib;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.renderers.geo.ExtendedGeoEntityRenderer;

public class TexturePerBoneTestEntityRenderer extends ExtendedGeoEntityRenderer<TexturePerBoneTestEntity> {
	
	private static final Identifier TEXTURE = new Identifier("minecraft",
			"textures/block/redstone_block.png");
	private static final Identifier TEXTURE_GLASS = new Identifier("minecraft",
			"textures/block/white_stained_glass.png");
	private static final Identifier MODEL_RESLOC = new Identifier(GeckoLib.ModID,
			"geo/textureperbonetestentity.geo.json");

	public TexturePerBoneTestEntityRenderer(EntityRendererFactory.Context renderManager) {
		super(renderManager, new TexturePerBoneTestEntityModel<TexturePerBoneTestEntity>(MODEL_RESLOC, TEXTURE,
				"textureperbonetestentity"));
	}

	@Override
	protected boolean isArmorBone(GeoBone bone) {
		return false;
	}
	
	@Override
	public RenderLayer getRenderType(TexturePerBoneTestEntity animatable, float partialTick, MatrixStack poseStack,
			VertexConsumerProvider bufferSource, VertexConsumer buffer, int packedLight,
			Identifier texture) {
		return RenderLayer.getEntityTranslucent(texture);
	}
	
	@Override
	protected Identifier getTextureForBone(String boneName, TexturePerBoneTestEntity animatable) {
		if(boneName.equalsIgnoreCase("outer_glass")) {
			return TEXTURE_GLASS;
		}
		return null;
	}

	@Override
	protected ItemStack getHeldItemForBone(String boneName, TexturePerBoneTestEntity animatable) {
		return null;
	}

	@Override
	protected Mode getCameraTransformForItemAtBone(ItemStack boneItem, String boneName) {
		return null;
	}

	@Override
	protected BlockState getHeldBlockForBone(String boneName, TexturePerBoneTestEntity animatable) {
		return null;
	}

	@Override
	protected void preRenderItem(MatrixStack matrixStack, ItemStack item, String boneName, TexturePerBoneTestEntity animatable, IBone bone) {
		
	}

	@Override
	protected void preRenderBlock(MatrixStack matrixStack, BlockState block, String boneName, TexturePerBoneTestEntity animatable) {
		
	}

	@Override
	protected void postRenderItem(MatrixStack matrixStack, ItemStack item, String boneName, TexturePerBoneTestEntity animatable, IBone bone) {
		
	}

	@Override
	protected void postRenderBlock(MatrixStack matrixStack, BlockState block, String boneName, TexturePerBoneTestEntity animatable) {
		
	}

}
