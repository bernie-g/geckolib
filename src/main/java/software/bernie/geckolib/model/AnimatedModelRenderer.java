package software.bernie.geckolib.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.Rotations;
import net.minecraft.util.math.Vec3d;

public class AnimatedModelRenderer extends ModelRenderer
{
	public String modelRendererName;
	private Rotations initialRotation = new Rotations(0, 0, 0);
	private Vec3d initialRotationPoint = new Vec3d(0, 0, 0);

	public float scaleValueX = 1;
	public float scaleValueY = 1;
	public float scaleValueZ = 1;

	public float positionOffsetX = 0;
	public float positionOffsetY = 0;
	public float positionOffsetZ = 0;

	public AnimatedModelRenderer(Model model)
	{
		super(model);
	}

	public String getModelRendererName()
	{
		return modelRendererName;
	}

	public void setModelRendererName(String modelRendererName)
	{
		this.modelRendererName = modelRendererName;
	}

	public void setInitialRotation(Rotations initialRotation)
	{
		this.initialRotation = initialRotation;
	}

	public Rotations getInitialRotation()
	{
		return initialRotation;
	}

	public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
		if (this.showModel) {
			if (!this.cubeList.isEmpty() || !this.childModels.isEmpty()) {
				matrixStackIn.push();
				this.translateRotate(matrixStackIn);
				this.scale(matrixStackIn);
				//this.translate(matrixStackIn);
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
	public void translate(MatrixStack matrixStack)
	{
		matrixStack.translate(positionOffsetX / 16, positionOffsetY / 16, positionOffsetZ / 16);
	}

	public Vec3d getInitialRotationPoint()
	{
		return initialRotationPoint;
	}

	public void setInitialRotationPoint(Vec3d initialRotationPoint)
	{
		this.initialRotationPoint = initialRotationPoint;
	}

	@Override
	public void setRotationPoint(float rotationPointXIn, float rotationPointYIn, float rotationPointZIn)
	{
		super.setRotationPoint(rotationPointXIn, rotationPointYIn, rotationPointZIn);
		this.setInitialRotationPoint(new Vec3d(rotationPointXIn, rotationPointYIn, rotationPointZIn));
	}

	@Override
	public void translateRotate(MatrixStack matrixStackIn)
	{
		matrixStackIn.translate(((rotationPointX + positionOffsetX) / 16F), ((rotationPointY  + positionOffsetY) / 16F), ((rotationPointZ + positionOffsetZ) / 16F));
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
}
