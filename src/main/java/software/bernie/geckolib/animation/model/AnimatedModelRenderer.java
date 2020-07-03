/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.geckolib.animation.model;

import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;

public class AnimatedModelRenderer extends ModelPart
{
	private BoneSnapshot boneSnapshot;
	public String currentAnimationName;

	public float scaleValueX = 1;
	public float scaleValueY = 1;
	public float scaleValueZ = 1;

	public float positionOffsetX = 0;
	public float positionOffsetY = 0;
	public float positionOffsetZ = 0;

	public String name;
	public BoneSnapshot initialSnapshot;

	public AnimatedModelRenderer(Model model)
	{
		super(model);
	}

	public void setModelRendererName(String modelRendererName)
	{
		this.name = modelRendererName;
	}

	public void render(MatrixStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
		if (this.visible) {
			if (!this.cuboids.isEmpty() || !this.children.isEmpty()) {
				matrixStackIn.push();
				this.rotate(matrixStackIn);
				this.scale(matrixStackIn);
				this.renderCuboids(matrixStackIn.peek(), bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);

				for(ModelPart modelrenderer : this.children) {
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
	public void setPivot(float rotationPointXIn, float rotationPointYIn, float rotationPointZIn)
	{
		super.setPivot(rotationPointXIn, rotationPointYIn, rotationPointZIn);
	}

	@Override
	public void rotate(MatrixStack matrixStackIn)
	{
		matrixStackIn.translate(((pivotX + positionOffsetX) / 16F), ((pivotY  - positionOffsetY) / 16F), ((pivotZ + positionOffsetZ) / 16F));
		if (this.roll != 0.0F) {
			matrixStackIn.multiply(Vector3f.POSITIVE_Z.getRadialQuaternion(this.roll));
		}

		if (this.yaw != 0.0F) {
			matrixStackIn.multiply(Vector3f.POSITIVE_Y.getRadialQuaternion(this.yaw));
		}

		if (this.pitch != 0.0F) {
			matrixStackIn.multiply(Vector3f.POSITIVE_X.getRadialQuaternion(this.pitch));
		}
	}

	public void saveInitialSnapshot()
	{
		this.initialSnapshot = new BoneSnapshot(this);
	}

	public BoneSnapshot getInitialSnapshot()
	{
		return this.initialSnapshot;
	}

	public BoneSnapshot saveSnapshot()
	{
		return new BoneSnapshot(this);
	}

}
