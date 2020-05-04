package software.bernie.geckolib.client.renderer.model;// Made with Blockbench 3.5.2
// Exported for Minecraft version 1.15
// Paste this class into your mod and generate all required imports


import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib.entity.TurretEntity;
import software.bernie.geckolib.model.AnimatedEntityModel;
import software.bernie.geckolib.model.AnimatedModelRenderer;

public class TurretModel extends AnimatedEntityModel<TurretEntity>
{
	private final AnimatedModelRenderer body;
	private final AnimatedModelRenderer left;
	private final AnimatedModelRenderer right;
	private final AnimatedModelRenderer backleg;
	private final AnimatedModelRenderer frontlegr;
	private final AnimatedModelRenderer frontlegl;
	private final AnimatedModelRenderer rightgun;
	private final AnimatedModelRenderer bodygun;
	private final AnimatedModelRenderer gunbarrels;
	private final AnimatedModelRenderer leftgun;
	private final AnimatedModelRenderer bodygun2;
	private final AnimatedModelRenderer gunbarrels2;

	@Override
	public ResourceLocation getAnimationFileLocation()
	{
		return null;
	}

	@Override
	public String getDefaultAnimation()
	{
		return null;
	}

	public TurretModel() {
		textureWidth = 64;
		textureHeight = 64;

		body = new AnimatedModelRenderer(this);
		body.setRotationPoint(0.0F, 15.1212F, -2.1155F);
		body.setTextureOffset(0, 0).addBox(-2.0F, -21.0212F, -2.3845F, 4.0F, 23.0F, 9.0F, 0.0F, false);
		body.setTextureOffset(0, 0).addBox(-1.0F, -13.0212F, -3.3845F, 2.0F, 2.0F, 1.0F, 0.0F, false);


		left = new AnimatedModelRenderer(this);
		left.setRotationPoint(8.0F, 8.8788F, 2.1155F);
		body.addChild(left);
		left.setTextureOffset(0, 0).addBox(2.0F, -27.9F, -3.5F, 2.0F, 19.0F, 7.0F, 0.0F, false);

		right = new AnimatedModelRenderer(this);
		right.setRotationPoint(-8.0F, 8.8788F, 2.1155F);
		body.addChild(right);
		right.setTextureOffset(0, 0).addBox(-4.0F, -27.9F, -3.5F, 2.0F, 19.0F, 7.0F, 0.0F, true);

		backleg = new AnimatedModelRenderer(this);
		backleg.setRotationPoint(0.0F, 16.1212F, 1.8845F);
		backleg.setTextureOffset(0, 0).addBox(-1.0F, 0.0183F, 2.2658F, 2.0F, 2.0F, 7.0F, 0.0F, true);
		backleg.setTextureOffset(0, 0).addBox(-0.5F, 0.7265F, 8.3795F, 1.0F, 9.0F, 1.0F, 0.0F, true);

		frontlegr = new AnimatedModelRenderer(this);
		frontlegr.setRotationPoint(0.0F, 15.1212F, -2.1155F);
		frontlegr.setTextureOffset(0, 0).addBox(1.3166F, 4.0472F, -9.8806F, 1.0F, 6.0F, 1.0F, 0.0F, false);
		frontlegr.setTextureOffset(0, 0).addBox(0.7806F, 2.0788F, -10.0797F, 2.0F, 2.0F, 6.0F, 0.0F, false);
		frontlegr.setTextureOffset(0, 0).addBox(1.2205F, 1.3971F, -4.8608F, 1.0F, 1.0F, 6.0F, 0.0F, false);

		frontlegl = new AnimatedModelRenderer(this);
		frontlegl.setRotationPoint(0.0F, 15.1212F, -2.1155F);
		frontlegl.setTextureOffset(0, 0).addBox(-2.4793F, 1.6471F, -3.9278F, 1.0F, 1.0F, 6.0F, 0.0F, true);
		frontlegl.setTextureOffset(0, 0).addBox(-3.0394F, 2.0788F, -9.1138F, 2.0F, 2.0F, 6.0F, 0.0F, true);
		frontlegl.setTextureOffset(0, 0).addBox(-2.5754F, 3.963F, -8.9184F, 1.0F, 6.0F, 1.0F, 0.0F, true);

		rightgun = new AnimatedModelRenderer(this);
		rightgun.setRotationPoint(0.0F, 6.0F, 0.0F);


		bodygun = new AnimatedModelRenderer(this);
		bodygun.setRotationPoint(-4.0F, 18.0F, 0.0F);
		rightgun.addChild(bodygun);
		bodygun.setTextureOffset(0, 0).addBox(-1.0F, -17.9F, -1.5F, 3.0F, 1.0F, 4.0F, 0.0F, true);
		bodygun.setTextureOffset(0, 0).addBox(-6.0F, -17.9F, -1.5F, 1.0F, 1.0F, 4.0F, 0.0F, true);
		bodygun.setTextureOffset(0, 0).addBox(-6.0F, -19.9F, -1.5F, 1.0F, 1.0F, 4.0F, 0.0F, true);
		bodygun.setTextureOffset(0, 0).addBox(-1.0F, -19.9F, -1.5F, 3.0F, 1.0F, 3.0F, 0.0F, true);
		bodygun.setTextureOffset(0, 0).addBox(-5.0F, -20.9F, -2.5F, 4.0F, 5.0F, 6.0F, 0.0F, true);

		gunbarrels = new AnimatedModelRenderer(this);
		gunbarrels.setRotationPoint(-7.0F, -0.4F, 0.3333F);
		rightgun.addChild(gunbarrels);
		gunbarrels.setTextureOffset(0, 0).addBox(-0.5F, 0.5F, -8.8333F, 1.0F, 1.0F, 6.0F, 0.0F, true);
		gunbarrels.setTextureOffset(0, 0).addBox(-0.5F, -1.5F, -8.8333F, 1.0F, 1.0F, 6.0F, 0.0F, true);
		gunbarrels.setTextureOffset(0, 0).addBox(-1.5F, -2.0F, -3.8333F, 3.0F, 4.0F, 1.0F, 0.0F, true);

		leftgun = new AnimatedModelRenderer(this);
		leftgun.setRotationPoint(0.0F, 6.0F, 0.0F);


		bodygun2 = new AnimatedModelRenderer(this);
		bodygun2.setRotationPoint(4.0F, 18.0F, 0.0F);
		leftgun.addChild(bodygun2);
		bodygun2.setTextureOffset(0, 0).addBox(-2.0F, -17.9F, -1.5F, 3.0F, 1.0F, 4.0F, 0.0F, false);
		bodygun2.setTextureOffset(0, 0).addBox(5.0F, -17.9F, -1.5F, 1.0F, 1.0F, 4.0F, 0.0F, false);
		bodygun2.setTextureOffset(0, 0).addBox(5.0F, -19.9F, -1.5F, 1.0F, 1.0F, 4.0F, 0.0F, false);
		bodygun2.setTextureOffset(0, 0).addBox(-2.0F, -19.9F, -1.5F, 3.0F, 1.0F, 3.0F, 0.0F, false);
		bodygun2.setTextureOffset(0, 0).addBox(1.0F, -20.9F, -2.5F, 4.0F, 5.0F, 6.0F, 0.0F, false);

		gunbarrels2 = new AnimatedModelRenderer(this);
		gunbarrels2.setRotationPoint(7.0F, -0.4F, 1.3333F);
		leftgun.addChild(gunbarrels2);
		gunbarrels2.setTextureOffset(0, 0).addBox(-0.5F, 0.5F, -9.8333F, 1.0F, 1.0F, 6.0F, 0.0F, false);
		gunbarrels2.setTextureOffset(0, 0).addBox(-0.5F, -1.5F, -9.8333F, 1.0F, 1.0F, 6.0F, 0.0F, false);
		gunbarrels2.setTextureOffset(0, 0).addBox(-1.5F, -2.0F, -4.8333F, 3.0F, 4.0F, 1.0F, 0.0F, false);
	}

	@Override
	public void setRotationAngles(TurretEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch){
		//previously the render function, render code was moved to a method below
	}

	@Override
	public void render(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
		body.render(matrixStack, buffer, packedLight, packedOverlay);
		backleg.render(matrixStack, buffer, packedLight, packedOverlay);
		frontlegr.render(matrixStack, buffer, packedLight, packedOverlay);
		frontlegl.render(matrixStack, buffer, packedLight, packedOverlay);
		rightgun.render(matrixStack, buffer, packedLight, packedOverlay);
		leftgun.render(matrixStack, buffer, packedLight, packedOverlay);
	}

	public void setRotationAngle(AnimatedModelRenderer AnimatedModelRenderer, float x, float y, float z) {
		AnimatedModelRenderer.rotateAngleX = x;
		AnimatedModelRenderer.rotateAngleY = y;
		AnimatedModelRenderer.rotateAngleZ = z;
	}
}