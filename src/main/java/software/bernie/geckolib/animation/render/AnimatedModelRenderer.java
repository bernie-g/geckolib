/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.geckolib.animation.render;

import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import software.bernie.geckolib.animation.snapshot.BoneSnapshot;

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

	@Override
	public AnimatedModelRenderer setTextureOffset(int textureOffsetU, int textureOffsetV)
	{
		this.textureOffsetU = textureOffsetU;
		this.textureOffsetV = textureOffsetV;
		return this;
	}


	public AnimatedModelRenderer addBox(String name, float x, float y, float z, int sizeX, int sizeY, int sizeZ, float extra, int textureOffsetU, int textureOffsetV)
	{
		this.setTextureOffset(textureOffsetU, textureOffsetV);
		this.addCuboid(this.textureOffsetU, this.textureOffsetV, x, y, z, (float)sizeX, (float)sizeY, (float)sizeZ, extra, extra, extra, this.mirror, false);
		return this;
	}


	public AnimatedModelRenderer addBox(float x, float y, float z, float sizeX, float sizeY, float sizeZ)
	{
		this.addCuboid(this.textureOffsetU, this.textureOffsetV, x, y, z, sizeX, sizeY, sizeZ, 0.0F, 0.0F, 0.0F, this.mirror, false);
		return this;
	}


	public AnimatedModelRenderer addBox(float x, float y, float z, float sizeX, float sizeY, float sizeZ, boolean mirror)
	{
		this.addCuboid(this.textureOffsetU, this.textureOffsetV, x, y, z, sizeX, sizeY, sizeZ, 0.0F, 0.0F, 0.0F, mirror, false);
		return this;
	}


	public void addBox(float x, float y, float z, float sizeX, float sizeY, float sizeZ, float extra)
	{
		this.addCuboid(this.textureOffsetU, this.textureOffsetV, x, y, z, sizeX, sizeY, sizeZ, extra, extra, extra, this.mirror, false);
	}


	public void addBox(float x, float y, float z, float sizeX, float sizeY, float sizeZ, float extraX, float extraY, float extraZ)
	{
		this.addCuboid(this.textureOffsetU, this.textureOffsetV, x, y, z, sizeX, sizeY, sizeZ, extraX, extraY, extraZ, this.mirror, false);
	}


	public void addBox(float x, float y, float z, float sizeX, float sizeY, float sizeZ, float extra, boolean mirror)
	{
		this.addCuboid(this.textureOffsetU, this.textureOffsetV, x, y, z, sizeX, sizeY, sizeZ, extra, extra, extra, mirror, false);
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

	public void setRotationPoint(float rotationPointXIn, float rotationPointYIn, float rotationPointZIn)
	{
		this.setPivot(rotationPointXIn, rotationPointYIn, rotationPointZIn);
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
