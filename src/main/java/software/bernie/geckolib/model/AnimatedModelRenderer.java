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
	private BoneSnapshot boneSnapshot;
	public String currentAnimationName;

	public float scaleValueX = 1;
	public float scaleValueY = 1;
	public float scaleValueZ = 1;

	public float positionOffsetX = 0;
	public float positionOffsetY = 0;
	public float positionOffsetZ = 0;

	private String modelRendererName;
	public BoneSnapshot initialSnapshot;

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
		matrixStackIn.translate(((rotationPointX + positionOffsetX) / 16F), ((rotationPointY  - positionOffsetY) / 16F), ((rotationPointZ + positionOffsetZ) / 16F));
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
