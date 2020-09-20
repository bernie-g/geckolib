/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.geckolib.renderers.legacy;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import software.bernie.geckolib.core.processor.IBone;
import software.bernie.geckolib.core.snapshot.BoneSnapshot;

public class AnimatedModelRenderer extends ModelRenderer implements IBone
{
	private BoneSnapshot boneSnapshot;
	public String currentAnimationName;

	private float scaleValueX = 1;
	private float scaleValueY = 1;
	private float scaleValueZ = 1;

	private float positionOffsetX = 0;
	private float positionOffsetY = 0;
	private float positionOffsetZ = 0;

	public String name;
	private BoneSnapshot initialSnapshot;

	public AnimatedModelRenderer(Model model)
	{
		super(model);
	}

	@Override
	public void setModelRendererName(String modelRendererName)
	{
		this.name = modelRendererName;
	}

	public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
		if (this.showModel) {
			if (!this.cubeList.isEmpty() || !this.childModels.isEmpty()) {
				matrixStackIn.push();
				this.translateRotate(matrixStackIn);
				this.scale(matrixStackIn);
				this.doRender(matrixStackIn.getLast(), bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);

				for(ModelRenderer modelrenderer : this.childModels) {
					modelrenderer.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
				}
				matrixStackIn.pop();
			}
		}
	}

	public void scale(MatrixStack matrixStack)
	{
		matrixStack.scale(scaleValueX, scaleValueY, scaleValueZ);
	}


	@Override
	public void setRotationPoint(float rotationPointXIn, float rotationPointYIn, float rotationPointZIn)
	{
		super.setRotationPoint(rotationPointXIn, rotationPointYIn, rotationPointZIn);
	}

	@Override
	public void translateRotate(MatrixStack matrixStackIn)
	{
		matrixStackIn.translate(((rotationPointX) / 16F), (rotationPointY / 16F), ((rotationPointZ) / 16F));

		if (this.rotateAngleZ != 0.0F) {
			matrixStackIn.rotate(Vector3f.ZP.rotation(this.rotateAngleZ));
		}

		if (this.rotateAngleY != 0.0F) {
			matrixStackIn.rotate(Vector3f.YP.rotation(this.rotateAngleY));
		}

		if (this.rotateAngleX != 0.0F) {
			matrixStackIn.rotate(Vector3f.XP.rotation(this.rotateAngleX));
		}

	}

	@Override
	public void saveInitialSnapshot()
	{
		this.initialSnapshot = new BoneSnapshot(this);
	}

	@Override
	public BoneSnapshot getInitialSnapshot()
	{
		return this.initialSnapshot;
	}

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public float getRotationX()
	{
		return rotateAngleX;
	}

	@Override
	public float getRotationY()
	{
		return rotateAngleY;
	}

	@Override
	public float getRotationZ()
	{
		return rotateAngleZ;
	}

	@Override
	public float getPositionX()
	{
		return positionOffsetX;
	}

	@Override
	public float getPositionY()
	{
		return positionOffsetY;
	}

	@Override
	public float getPositionZ()
	{
		return positionOffsetZ;
	}

	@Override
	public float getScaleX()
	{
		return scaleValueX;
	}

	@Override
	public float getScaleY()
	{
		return scaleValueY;
	}

	@Override
	public float getScaleZ()
	{
		return scaleValueZ;
	}

	@Override
	public void setRotationX(float value)
	{
		this.rotateAngleX = value;
	}

	@Override
	public void setRotationY(float value)
	{
		this.rotateAngleY = value;
	}

	@Override
	public void setRotationZ(float value)
	{
		this.rotateAngleZ = value;
	}

	@Override
	public void setPositionX(float value)
	{
		this.positionOffsetX = value;
	}

	@Override
	public void setPositionY(float value)
	{
		this.positionOffsetY = value;
	}

	@Override
	public void setPositionZ(float value)
	{
		this.positionOffsetZ = value;
	}

	@Override
	public void setScaleX(float value)
	{
		this.scaleValueX = value;
	}

	@Override
	public void setScaleY(float value)
	{
		this.scaleValueY= value;
	}

	@Override
	public void setScaleZ(float value)
	{
		this.scaleValueZ= value;
	}
}
