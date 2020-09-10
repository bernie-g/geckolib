package software.bernie.geckolib.geo.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib.entity.IAnimatable;
import software.bernie.geckolib.geo.render.built.GeoModel;
import software.bernie.geckolib.model.AnimatedGeoModel;
import software.bernie.geckolib.model.IGeoModelProvider;

import java.awt.*;

public abstract class GeoEntityRenderer<T extends Entity & IAnimatable> extends EntityRenderer<T> implements IGeoRenderer<T>
{
	private final AnimatedGeoModel<T> modelProvider;

	protected GeoEntityRenderer(EntityRendererManager renderManager, AnimatedGeoModel<T> modelProvider)
	{
		super(renderManager);
		this.modelProvider = modelProvider;
		this.modelProvider.crashWhenCantFindBone = false;
	}

	@Override
	public void render(T entityIn, float entityYaw, float partialTicks, MatrixStack stack, IRenderTypeBuffer bufferIn, int packedLightIn)
	{
		modelProvider.setLivingAnimations(entityIn);
		stack.push();
		stack.translate(0, 0.01f, 0);
		Minecraft.getInstance().textureManager.bindTexture(getEntityTexture(entityIn));
		GeoModel model = modelProvider.getModel(modelProvider.getModelLocation(entityIn));
		Color renderColor = getRenderColor(entityIn, partialTicks, stack, bufferIn, packedLightIn);
		RenderType renderType = getRenderType(entityIn, partialTicks, stack, bufferIn, packedLightIn, getEntityTexture(entityIn));
		render(model, entityIn, partialTicks, stack, bufferIn.getBuffer(renderType), packedLightIn, getPackedOverlay((LivingEntity) entityIn, 0), (float) renderColor.getRed() / 255f, (float) renderColor.getBlue() / 255f, (float) renderColor.getGreen() / 255f, (float) renderColor.getAlpha() / 255);
		stack.pop();
		super.render(entityIn, entityYaw, partialTicks, stack, bufferIn, packedLightIn);
	}

	@Override
	public ResourceLocation getEntityTexture(T entity)
	{
		return getTexture(entity);
	}

	@Override
	public IGeoModelProvider getGeoModelProvider()
	{
		return this.modelProvider;
	}

	public static int getPackedOverlay(LivingEntity livingEntityIn, float uIn)
	{
		return OverlayTexture.getPackedUV(OverlayTexture.getU(uIn), OverlayTexture.getV(livingEntityIn.hurtTime > 0 || livingEntityIn.deathTime > 0));
	}
}
