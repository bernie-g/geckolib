// Made with Blockbench 3.6.6
// Exported for Minecraft version 1.12.2 or 1.15.2 (same format for both) for entity models animated with GeckoLib
// Paste this class into your mod and follow the documentation for GeckoLib to use animations. You can find the documentation here: https://github.com/bernie-g/geckolib
// Blockbench plugin created by Gecko
package software.bernie.geckolib.example.client.renderer.model.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib.model.AnimatedEntityModel;
import software.bernie.geckolib.animation.render.AnimatedModelRenderer;
import software.bernie.geckolib.example.entity.EntityBotarium;

public class EntityBotariumModel extends AnimatedEntityModel<EntityBotarium>
{
	private final AnimatedModelRenderer Machine;
	private final AnimatedModelRenderer topmachine;
	private final AnimatedModelRenderer ceilingplatform;
	private final AnimatedModelRenderer topplatform;
	private final AnimatedModelRenderer top_crochets;
	private final AnimatedModelRenderer frontcrochet;
	private final AnimatedModelRenderer rightcrochet;
	private final AnimatedModelRenderer leftcrochet;
	private final AnimatedModelRenderer backcrochet;
	private final AnimatedModelRenderer glass;
	private final AnimatedModelRenderer screen;
	private final AnimatedModelRenderer left_tube;
	private final AnimatedModelRenderer right_tube;
	private final AnimatedModelRenderer back_tube;
	private final AnimatedModelRenderer baseplatform;
	private final AnimatedModelRenderer mediumplatform;
	private final AnimatedModelRenderer plantsurface;
	private final AnimatedModelRenderer groundplatform;
	private final AnimatedModelRenderer Box;
	private final AnimatedModelRenderer frontpanel;
	private final AnimatedModelRenderer rightpanel;
	private final AnimatedModelRenderer toppanel;
	private final AnimatedModelRenderer leftpanel;
	private final AnimatedModelRenderer backpanel;

	public EntityBotariumModel()
	{
		textureWidth = 128;
		textureHeight = 128;
		Machine = new AnimatedModelRenderer(this);
		Machine.setRotationPoint(0.0F, 24.0F, 0.0F);

		Machine.setModelRendererName("Machine");
		this.registerModelRenderer(Machine);

		topmachine = new AnimatedModelRenderer(this);
		topmachine.setRotationPoint(0.0F, -14F, 0.0F);
		Machine.addChild(topmachine);

		topmachine.setModelRendererName("topmachine");
		this.registerModelRenderer(topmachine);

		ceilingplatform = new AnimatedModelRenderer(this);
		ceilingplatform.setRotationPoint(0.0F, -17.0F, 0.0F);
		topmachine.addChild(ceilingplatform);
		ceilingplatform.setTextureOffset(0, 36).addBox(-7.0F, -1.0F, -7.0F, 14.0F, 3.0F, 14.0F, 0.0F, false);
		ceilingplatform.setModelRendererName("ceilingplatform");
		this.registerModelRenderer(ceilingplatform);

		topplatform = new AnimatedModelRenderer(this);
		topplatform.setRotationPoint(0.0F, -15.0F, 0.0F);
		topmachine.addChild(topplatform);
		topplatform.setTextureOffset(42, 17).addBox(-6.0F, 0.0F, -6.0F, 12.0F, 2.0F, 12.0F, 0.0F, false);
		topplatform.setModelRendererName("topplatform");
		this.registerModelRenderer(topplatform);

		top_crochets = new AnimatedModelRenderer(this);
		top_crochets.setRotationPoint(0.5F, -15.0F, 1.75F);
		topmachine.addChild(top_crochets);

		top_crochets.setModelRendererName("top_crochets");
		this.registerModelRenderer(top_crochets);

		frontcrochet = new AnimatedModelRenderer(this);
		frontcrochet.setRotationPoint(-0.5F, 2.0F, -8.0F);
		top_crochets.addChild(frontcrochet);
		frontcrochet.setTextureOffset(0, 22).addBox(-1.5F, -2.0F, 0.0F, 3.0F, 5.0F, 2.0F, 0.0F, false);
		frontcrochet.setModelRendererName("frontcrochet");
		this.registerModelRenderer(frontcrochet);

		rightcrochet = new AnimatedModelRenderer(this);
		rightcrochet.setRotationPoint(5.75F, 2.0F, -1.75F);
		top_crochets.addChild(rightcrochet);
		setRotationAngle(rightcrochet, 0.0F, -1.5708F, 0.0F);
		rightcrochet.setTextureOffset(0, 22).addBox(-1.5F, -2.0F, 0.0F, 3.0F, 5.0F, 2.0F, 0.0F, false);
		rightcrochet.setModelRendererName("rightcrochet");
		this.registerModelRenderer(rightcrochet);

		leftcrochet = new AnimatedModelRenderer(this);
		leftcrochet.setRotationPoint(-6.75F, 2.0F, -1.75F);
		top_crochets.addChild(leftcrochet);
		setRotationAngle(leftcrochet, 0.0F, 1.5708F, 0.0F);
		leftcrochet.setTextureOffset(0, 22).addBox(-1.5F, -2.0F, 0.0F, 3.0F, 5.0F, 2.0F, 0.0F, false);
		leftcrochet.setModelRendererName("leftcrochet");
		this.registerModelRenderer(leftcrochet);

		backcrochet = new AnimatedModelRenderer(this);
		backcrochet.setRotationPoint(-0.5F, 2.0F, 4.5F);
		top_crochets.addChild(backcrochet);
		setRotationAngle(backcrochet, 0.0F, 3.1416F, 0.0F);
		backcrochet.setTextureOffset(0, 22).addBox(-1.5F, -2.0F, 0.0F, 3.0F, 5.0F, 2.0F, 0.0F, false);
		backcrochet.setModelRendererName("backcrochet");
		this.registerModelRenderer(backcrochet);

		glass = new AnimatedModelRenderer(this);
		glass.setRotationPoint(0.0F, -8.0F, 0.0F);
		Machine.addChild(glass);
		glass.setTextureOffset(60, 60).addBox(-5.0F, -22.0F, -5.0F, 10.0F, 22.0F, 0.0F, 0.0F, false);
		glass.setTextureOffset(60, 50).addBox(5.0F, -22.0F, -5.0F, 0.0F, 22.0F, 10.0F, 0.0F, false);
		glass.setTextureOffset(60, 50).addBox(-5.0F, -22.0F, -5.0F, 0.0F, 22.0F, 10.0F, 0.0F, false);
		glass.setTextureOffset(60, 60).addBox(-5.0F, -22.0F, 5.0F, 10.0F, 22.0F, 0.0F, 0.0F, false);
		glass.setModelRendererName("glass");
		this.registerModelRenderer(glass);

		screen = new AnimatedModelRenderer(this);
		screen.setRotationPoint(0.0F, -9.0F, -6.0F);
		Machine.addChild(screen);
		setRotationAngle(screen, -0.3927F, 0.0F, 0.0F);
		screen.setTextureOffset(0, 17).addBox(-3.0F, -1.713F, -1.1929F, 6.0F, 4.0F, 1.0F, 0.0F, false);
		screen.setTextureOffset(0, 0).addBox(-2.5F, -1.213F, -1.1929F, 5.0F, 3.0F, 3.0F, 0.0F, false);
		screen.setTextureOffset(60, 50).addBox(-4.0F, -2.713F, -1.6929F, 8.0F, 6.0F, 1.0F, 0.0F, false);
		screen.setModelRendererName("screen");
		this.registerModelRenderer(screen);

		left_tube = new AnimatedModelRenderer(this);
		left_tube.setRotationPoint(-4.9F, -8.0F, 0.0F);
		Machine.addChild(left_tube);
		setRotationAngle(left_tube, 0.0F, 3.1416F, 0.0F);
		left_tube.setTextureOffset(0, 6).addBox(-0.5F, -2.0F, -2.0F, 2.0F, 4.0F, 4.0F, 0.0F, false);
		left_tube.setTextureOffset(0, 36).addBox(1.0F, -3.0F, -3.0F, 2.0F, 6.0F, 2.0F, 0.0F, false);
		left_tube.setTextureOffset(42, 42).addBox(1.0F, -3.0F, -1.0F, 2.0F, 2.0F, 2.0F, 0.0F, false);
		left_tube.setTextureOffset(6, 42).addBox(1.0F, -3.0F, 1.0F, 2.0F, 6.0F, 2.0F, 0.0F, false);
		left_tube.setTextureOffset(8, 6).addBox(1.0F, 1.0F, -1.0F, 2.0F, 2.0F, 2.0F, 0.0F, false);
		left_tube.setModelRendererName("left_tube");
		this.registerModelRenderer(left_tube);

		right_tube = new AnimatedModelRenderer(this);
		right_tube.setRotationPoint(-0.1F, -1.0F, 0.0F);
		Machine.addChild(right_tube);
		right_tube.setTextureOffset(0, 6).addBox(4.5F, -9.0F, -2.0F, 2.0F, 4.0F, 4.0F, 0.0F, false);
		right_tube.setTextureOffset(0, 36).addBox(6.0F, -10.0F, -3.0F, 2.0F, 6.0F, 2.0F, 0.0F, false);
		right_tube.setTextureOffset(42, 42).addBox(6.0F, -10.0F, -1.0F, 2.0F, 2.0F, 2.0F, 0.0F, false);
		right_tube.setTextureOffset(6, 42).addBox(6.0F, -10.0F, 1.0F, 2.0F, 6.0F, 2.0F, 0.0F, false);
		right_tube.setTextureOffset(8, 6).addBox(6.0F, -6.0F, -1.0F, 2.0F, 2.0F, 2.0F, 0.0F, false);
		right_tube.setModelRendererName("right_tube");
		this.registerModelRenderer(right_tube);

		back_tube = new AnimatedModelRenderer(this);
		back_tube.setRotationPoint(0.0F, -1.0F, -0.1F);
		Machine.addChild(back_tube);
		setRotationAngle(back_tube, 0.0F, -1.5708F, 0.0F);
		back_tube.setTextureOffset(0, 6).addBox(4.5F, -9.0F, -2.0F, 2.0F, 4.0F, 4.0F, 0.0F, false);
		back_tube.setTextureOffset(0, 36).addBox(6.0F, -10.0F, -3.0F, 2.0F, 6.0F, 2.0F, 0.0F, false);
		back_tube.setTextureOffset(42, 42).addBox(6.0F, -10.0F, -1.0F, 2.0F, 2.0F, 2.0F, 0.0F, false);
		back_tube.setTextureOffset(6, 42).addBox(6.0F, -10.0F, 1.0F, 2.0F, 6.0F, 2.0F, 0.0F, false);
		back_tube.setTextureOffset(8, 6).addBox(6.0F, -6.0F, -1.0F, 2.0F, 2.0F, 2.0F, 0.0F, false);
		back_tube.setModelRendererName("back_tube");
		this.registerModelRenderer(back_tube);

		baseplatform = new AnimatedModelRenderer(this);
		baseplatform.setRotationPoint(0.0F, -1.0F, 0.0F);
		Machine.addChild(baseplatform);
		baseplatform.setTextureOffset(0, 17).addBox(-7.0F, -5.0F, -7.0F, 14.0F, 5.0F, 14.0F, 0.0F, false);
		baseplatform.setModelRendererName("baseplatform");
		this.registerModelRenderer(baseplatform);

		mediumplatform = new AnimatedModelRenderer(this);
		mediumplatform.setRotationPoint(0.0F, -5.0F, 0.0F);
		baseplatform.addChild(mediumplatform);
		mediumplatform.setTextureOffset(42, 36).addBox(-6.0F, -2.0F, -6.0F, 12.0F, 2.0F, 12.0F, 0.0F, false);
		mediumplatform.setModelRendererName("mediumplatform");
		this.registerModelRenderer(mediumplatform);

		plantsurface = new AnimatedModelRenderer(this);
		plantsurface.setRotationPoint(0.5F, -3.0F, -0.5F);
		mediumplatform.addChild(plantsurface);
		plantsurface.setTextureOffset(48, 0).addBox(-5.0F, -2.0F, -4.0F, 9.0F, 3.0F, 9.0F, 0.0F, false);
		plantsurface.setModelRendererName("plantsurface");
		this.registerModelRenderer(plantsurface);

		groundplatform = new AnimatedModelRenderer(this);
		groundplatform.setRotationPoint(0.0F, 0.0F, 0.0F);
		Machine.addChild(groundplatform);
		groundplatform.setTextureOffset(0, 0).addBox(-8.0F, -1.0F, -8.0F, 16.0F, 1.0F, 16.0F, -0.01F, false);
		groundplatform.setModelRendererName("groundplatform");
		this.registerModelRenderer(groundplatform);

		Box = new AnimatedModelRenderer(this);
		Box.setRotationPoint(0.0F, 24.0F, 0.0F);

		Box.setModelRendererName("Box");
		this.registerModelRenderer(Box);

		frontpanel = new AnimatedModelRenderer(this);
		frontpanel.setRotationPoint(0.0F, 0.0F, -8.0F);
		Box.addChild(frontpanel);
		frontpanel.setTextureOffset(0, 55).addBox(-8.0F, -16.0F, 0.0F, 16.0F, 16.0F, 0.0F, 0.0F, false);
		frontpanel.setModelRendererName("frontpanel");
		this.registerModelRenderer(frontpanel);

		rightpanel = new AnimatedModelRenderer(this);
		rightpanel.setRotationPoint(8.0F, 0.0F, 0.0F);
		Box.addChild(rightpanel);
		rightpanel.setTextureOffset(0, 55).addBox(0.0F, -16.0F, -8.0F, 0.0F, 16.0F, 16.0F, 0.0F, false);
		rightpanel.setModelRendererName("rightpanel");
		this.registerModelRenderer(rightpanel);

		toppanel = new AnimatedModelRenderer(this);
		toppanel.setRotationPoint(0.0F, -16.0F, 0.0F);
		rightpanel.addChild(toppanel);
		toppanel.setTextureOffset(0, 88).addBox(-16.0F, 0.0F, -8.0F, 16.0F, 0.0F, 16.0F, 0.0F, false);
		toppanel.setModelRendererName("toppanel");
		this.registerModelRenderer(toppanel);

		leftpanel = new AnimatedModelRenderer(this);
		leftpanel.setRotationPoint(-8.0F, 0.0F, 0.0F);
		Box.addChild(leftpanel);
		leftpanel.setTextureOffset(0, 55).addBox(0.0F, -16.0F, -8.0F, 0.0F, 16.0F, 16.0F, 0.0F, false);
		leftpanel.setModelRendererName("leftpanel");
		this.registerModelRenderer(leftpanel);

		backpanel = new AnimatedModelRenderer(this);
		backpanel.setRotationPoint(0.0F, 0.0F, 8.0F);
		Box.addChild(backpanel);
		backpanel.setTextureOffset(0, 55).addBox(-8.0F, -16.0F, 0.0F, 16.0F, 16.0F, 0.0F, 0.0F, false);
		backpanel.setModelRendererName("backpanel");
		this.registerModelRenderer(backpanel);

		this.rootBones.add(Machine);
		this.rootBones.add(Box);
	}


	@Override
	public ResourceLocation getAnimationFileLocation(EntityBotarium entity)
	{
		return new ResourceLocation("geckolib", "animations/botarium_tier1_anim.json");
	}

	@Override
	public void setLivingAnimations(EntityBotarium entity, float limbSwing, float limbSwingAmount, float partialTick)
	{
/*		this.topmachine.setScaleX(1);
		this.topmachine.setScaleY(1);
		this.topmachine.setScaleZ(1);
		this.topmachine.setPositionY(10);
		this.topmachine.rotationPointY = 24;
		for(ModelRenderer renderer : topmachine.childModels)
		{
			renderer.rotationPointY = 0;
			AnimatedModelRenderer renderer1 = (AnimatedModelRenderer) renderer;
			renderer1.setPositionY(0);
		}*/
		super.setLivingAnimations(entity, limbSwing, limbSwingAmount, partialTick);

	}

	@Override
	public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha)
	{
		super.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		//this.topmachine.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
	}
}