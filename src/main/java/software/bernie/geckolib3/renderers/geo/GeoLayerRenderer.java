package software.bernie.geckolib3.renderers.geo;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.model.provider.GeoModelProvider;

public abstract class GeoLayerRenderer<T extends Entity & IAnimatable>
{
	private final IGeoRenderer<T> entityRenderer;

	public GeoLayerRenderer(IGeoRenderer<T> entityRendererIn)
	{
		this.entityRenderer = entityRendererIn;
	}

	protected static <T extends LivingEntity> void renderCopyModel(EntityModel<T> modelParentIn, EntityModel<T> modelIn, ResourceLocation textureLocationIn, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float partialTicks, float red, float green, float blue)
	{
		if (!entityIn.isInvisible())
		{
			modelParentIn.copyModelAttributesTo(modelIn);
			modelIn.setLivingAnimations(entityIn, limbSwing, limbSwingAmount, partialTicks);
			modelIn.setRotationAngles(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
			renderModel(modelIn, textureLocationIn, matrixStackIn, bufferIn, packedLightIn, entityIn, red, green, blue);
		}
	}

	protected static <T extends LivingEntity> void renderModel(EntityModel<T> modelIn, ResourceLocation textureLocationIn, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, T entityIn, float red, float green, float blue)
	{
		if(modelIn instanceof IGeoRenderer){
			IVertexBuilder ivertexbuilder = bufferIn.getBuffer(((IGeoRenderer)modelIn).getRenderType(null,0,matrixStackIn,bufferIn,null,packedLightIn,textureLocationIn));
			modelIn.render(matrixStackIn, ivertexbuilder, packedLightIn, LivingRenderer.getPackedOverlay(entityIn, 0.0F), red, green, blue, 1.0F);
		}else {
			IVertexBuilder ivertexbuilder = bufferIn.getBuffer(RenderType.getEntityCutoutNoCull(textureLocationIn));
			modelIn.render(matrixStackIn, ivertexbuilder, packedLightIn, LivingRenderer.getPackedOverlay(entityIn, 0.0F), red, green, blue, 1.0F);
		}
	}

	public GeoModelProvider getEntityModel()
	{
		return this.entityRenderer.getGeoModelProvider();
	}

	protected ResourceLocation getEntityTexture(T entityIn)
	{
		return this.entityRenderer.getTextureLocation(entityIn);
	}

	public abstract void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch);
}
