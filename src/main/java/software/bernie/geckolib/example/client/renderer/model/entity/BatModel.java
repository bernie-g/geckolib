// Made with Blockbench 3.6.6
// Exported for Minecraft version 1.12.2 or 1.15.2 (same format for both) for entity models animated with GeckoLib
// Paste this class into your mod and follow the documentation for GeckoLib to use animations. You can find the documentation here: https://github.com/bernie-g/geckolib
// Blockbench plugin created by Gecko
package software.bernie.geckolib.example.client.renderer.model.entity;

import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib.model.AnimatedEntityModel;
import software.bernie.geckolib.animation.render.AnimatedModelRenderer;
import software.bernie.geckolib.example.entity.BatEntity;

public class BatModel extends AnimatedEntityModel<BatEntity> {

	private final AnimatedModelRenderer main;
	private final AnimatedModelRenderer body;
	private final AnimatedModelRenderer head;
	private final AnimatedModelRenderer jaw;
	private final AnimatedModelRenderer leftArm;
	private final AnimatedModelRenderer bone5;
	private final AnimatedModelRenderer bone6;
	private final AnimatedModelRenderer bone;
	private final AnimatedModelRenderer bone9;
	private final AnimatedModelRenderer bone4;
	private final AnimatedModelRenderer bone7;
	private final AnimatedModelRenderer rightArm;
	private final AnimatedModelRenderer bone2;
	private final AnimatedModelRenderer bone3;
	private final AnimatedModelRenderer bone8;
	private final AnimatedModelRenderer bone10;
	private final AnimatedModelRenderer bone11;
	private final AnimatedModelRenderer bone12;
	private final AnimatedModelRenderer upperBody;
	private final AnimatedModelRenderer rightLeg;
	private final AnimatedModelRenderer leftLeg;

	public BatModel()
	{
		textureWidth = 256;
		textureHeight = 256;
		main = new AnimatedModelRenderer(this);
		main.setRotationPoint(0.0F, 0.0F, 0.0F);

		main.setModelRendererName("main");
		this.registerModelRenderer(main);

		body = new AnimatedModelRenderer(this);
		body.setRotationPoint(0.0F, 12.0F, 0.0F);
		main.addChild(body);
		body.setTextureOffset(80, 56).addBox(-4.0F, -7.0F, -1.0F, 8.0F, 7.0F, 5.0F, 0.0F, false);
		body.setModelRendererName("body");
		this.registerModelRenderer(body);

		head = new AnimatedModelRenderer(this);
		head.setRotationPoint(0.0F, -12.0F, 0.0F);
		body.addChild(head);
		head.setTextureOffset(65, 69).addBox(-4.0F, -5.1075F, -5.8879F, 8.0F, 9.0F, 7.0F, 0.0F, false);
		head.setTextureOffset(32, 76).addBox(-3.5F, -4.6075F, -4.8879F, 7.0F, 7.0F, 6.0F, 0.0F, false);
		head.setTextureOffset(33, 130).addBox(1.0F, -10.7808F, -2.0143F, 5.0F, 7.0F, 0.0F, 0.0F, false);
		head.setTextureOffset(84, 117).addBox(-6.0F, -10.7808F, -2.0143F, 5.0F, 7.0F, 0.0F, 0.0F, false);
		head.setTextureOffset(0, 0).addBox(-2.5F, -3.75F, -10.0F, 5.0F, 5.0F, 7.0F, 0.0F, false);
		head.setTextureOffset(0, 12).addBox(-2.5F, 1.25F, -10.0F, 5.0F, 1.0F, 7.0F, 0.0F, false);
		head.setModelRendererName("head");
		this.registerModelRenderer(head);

		jaw = new AnimatedModelRenderer(this);
		jaw.setRotationPoint(0.0F, 0.0F, 0.0F);
		head.addChild(jaw);
		setRotationAngle(jaw, -0.0873F, 0.0F, 0.0F);
		jaw.setTextureOffset(84, 87).addBox(-2.0F, 1.0F, -9.5F, 4.0F, 2.0F, 5.0F, 0.0F, false);
		jaw.setTextureOffset(53, 85).addBox(-2.0F, -1.0F, -9.5F, 4.0F, 2.0F, 5.0F, 0.0F, false);
		jaw.setModelRendererName("jaw");
		this.registerModelRenderer(jaw);

		leftArm = new AnimatedModelRenderer(this);
		leftArm.setRotationPoint(5.0F, -10.0F, 0.0F);
		body.addChild(leftArm);
		setRotationAngle(leftArm, 0.0F, 0.0F, 1.0908F);

		leftArm.setModelRendererName("leftArm");
		this.registerModelRenderer(leftArm);

		bone5 = new AnimatedModelRenderer(this);
		bone5.setRotationPoint(0.9991F, 0.0436F, 0.0F);
		leftArm.addChild(bone5);
		bone5.setTextureOffset(181, 20).addBox(-1.0F, 0.25F, 2.0F, 12.0F, 0.0F, 13.0F, 0.0F, false);
		bone5.setTextureOffset(72, 37).addBox(-1.0F, -1.0F, -1.0F, 12.0F, 2.0F, 3.0F, 0.0F, false);
		bone5.setTextureOffset(0, 36).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 4.0F, 5.0F, 0.0F, false);
		bone5.setModelRendererName("bone5");
		this.registerModelRenderer(bone5);

		bone6 = new AnimatedModelRenderer(this);
		bone6.setRotationPoint(11.0F, 0.0F, -1.0F);
		bone5.addChild(bone6);
		bone6.setTextureOffset(88, 68).addBox(0.0F, -1.0F, 0.0F, 6.0F, 2.0F, 3.0F, 0.0F, false);
		bone6.setTextureOffset(0, 45).addBox(4.0F, -1.5F, 0.0F, 3.0F, 3.0F, 3.0F, 0.1F, false);
		bone6.setModelRendererName("bone6");
		this.registerModelRenderer(bone6);

		bone = new AnimatedModelRenderer(this);
		bone.setRotationPoint(5.2509F, -0.0436F, 1.25F);
		bone6.addChild(bone);
		bone.setTextureOffset(133, 172).addBox(-5.251F, 0.3436F, -5.25F, 24.0F, 0.0F, 24.0F, 0.0F, false);
		bone.setModelRendererName("bone");
		this.registerModelRenderer(bone);

		bone9 = new AnimatedModelRenderer(this);
		bone9.setRotationPoint(5.7509F, -0.0436F, 1.0F);
		bone6.addChild(bone9);
		bone9.setTextureOffset(132, 204).addBox(-5.751F, 0.0436F, -5.0F, 24.0F, 0.0F, 24.0F, 0.0F, false);
		bone9.setModelRendererName("bone9");
		this.registerModelRenderer(bone9);

		bone4 = new AnimatedModelRenderer(this);
		bone4.setRotationPoint(5.5F, 0.0F, 1.5F);
		bone6.addChild(bone4);
		bone4.setTextureOffset(122, 12).addBox(-4.5F, 0.0F, -10.5F, 9.0F, 0.0F, 12.0F, 0.0F, false);
		bone4.setModelRendererName("bone4");
		this.registerModelRenderer(bone4);

		bone7 = new AnimatedModelRenderer(this);
		bone7.setRotationPoint(11.5F, 0.0F, 0.0F);
		bone5.addChild(bone7);
		bone7.setTextureOffset(99, 142).addBox(-6.5F, -0.1F, 2.0F, 12.0F, 0.0F, 13.0F, 0.0F, false);
		bone7.setModelRendererName("bone7");
		this.registerModelRenderer(bone7);

		rightArm = new AnimatedModelRenderer(this);
		rightArm.setRotationPoint(-5.0F, -10.0F, 0.0F);
		body.addChild(rightArm);
		setRotationAngle(rightArm, 0.0F, 0.0F, -1.0908F);

		rightArm.setModelRendererName("rightArm");
		this.registerModelRenderer(rightArm);

		bone2 = new AnimatedModelRenderer(this);
		bone2.setRotationPoint(-0.9991F, 0.0436F, 0.0F);
		rightArm.addChild(bone2);
		bone2.setTextureOffset(181, 20).addBox(-11.0F, 0.25F, 2.0F, 12.0F, 0.0F, 13.0F, 0.0F, true);
		bone2.setTextureOffset(72, 37).addBox(-11.0F, -1.0F, -1.0F, 12.0F, 2.0F, 3.0F, 0.0F, true);
		bone2.setTextureOffset(0, 36).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 4.0F, 5.0F, 0.0F, true);
		bone2.setModelRendererName("bone2");
		this.registerModelRenderer(bone2);

		bone3 = new AnimatedModelRenderer(this);
		bone3.setRotationPoint(-11.0F, 0.0F, -1.0F);
		bone2.addChild(bone3);
		bone3.setTextureOffset(88, 68).addBox(-6.0F, -1.0F, 0.0F, 6.0F, 2.0F, 3.0F, 0.0F, true);
		bone3.setTextureOffset(0, 45).addBox(-7.0F, -1.5F, 0.0F, 3.0F, 3.0F, 3.0F, 0.1F, true);
		bone3.setModelRendererName("bone3");
		this.registerModelRenderer(bone3);

		bone8 = new AnimatedModelRenderer(this);
		bone8.setRotationPoint(-5.2509F, -0.0436F, 1.25F);
		bone3.addChild(bone8);
		bone8.setTextureOffset(133, 172).addBox(-18.749F, 0.3436F, -5.25F, 24.0F, 0.0F, 24.0F, 0.0F, true);
		bone8.setModelRendererName("bone8");
		this.registerModelRenderer(bone8);

		bone10 = new AnimatedModelRenderer(this);
		bone10.setRotationPoint(-5.7509F, -0.0436F, 1.0F);
		bone3.addChild(bone10);
		bone10.setTextureOffset(132, 204).addBox(-18.249F, 0.0436F, -5.0F, 24.0F, 0.0F, 24.0F, 0.0F, true);
		bone10.setModelRendererName("bone10");
		this.registerModelRenderer(bone10);

		bone11 = new AnimatedModelRenderer(this);
		bone11.setRotationPoint(-5.5F, 0.0F, 1.5F);
		bone3.addChild(bone11);
		bone11.setTextureOffset(122, 12).addBox(-4.5F, 0.0F, -10.5F, 9.0F, 0.0F, 12.0F, 0.0F, true);
		bone11.setModelRendererName("bone11");
		this.registerModelRenderer(bone11);

		bone12 = new AnimatedModelRenderer(this);
		bone12.setRotationPoint(-11.5F, 0.0F, 0.0F);
		bone2.addChild(bone12);
		bone12.setTextureOffset(99, 142).addBox(-5.5F, -0.1F, 2.0F, 12.0F, 0.0F, 13.0F, 0.0F, true);
		bone12.setModelRendererName("bone12");
		this.registerModelRenderer(bone12);

		upperBody = new AnimatedModelRenderer(this);
		upperBody.setRotationPoint(0.0F, -1.0F, 2.0F);
		body.addChild(upperBody);
		setRotationAngle(upperBody, 0.3054F, 0.0F, 0.0F);
		upperBody.setTextureOffset(0, 61).addBox(-5.5F, -12.0F, -3.5F, 11.0F, 12.0F, 8.0F, 0.0F, false);
		upperBody.setTextureOffset(38, 61).addBox(-5.0F, -11.0F, -3.0F, 10.0F, 8.0F, 7.0F, 0.0F, false);
		upperBody.setModelRendererName("upperBody");
		this.registerModelRenderer(upperBody);

		rightLeg = new AnimatedModelRenderer(this);
		rightLeg.setRotationPoint(0.25F, 12.0F, 1.0F);
		main.addChild(rightLeg);
		setRotationAngle(rightLeg, 0.0F, 0.0F, -0.0873F);
		rightLeg.setTextureOffset(0, 81).addBox(0.0F, 0.0F, -1.5F, 3.0F, 12.0F, 4.0F, 0.0F, false);
		rightLeg.setTextureOffset(86, 47).addBox(0.0F, 10.0F, -6.5F, 3.0F, 2.0F, 5.0F, 0.0F, false);
		rightLeg.setModelRendererName("rightLeg");
		this.registerModelRenderer(rightLeg);

		leftLeg = new AnimatedModelRenderer(this);
		leftLeg.setRotationPoint(-0.25F, 12.0F, 1.0F);
		main.addChild(leftLeg);
		setRotationAngle(leftLeg, 0.0F, 0.0F, 0.0873F);
		leftLeg.setTextureOffset(0, 81).addBox(-3.0F, 0.0F, -1.5F, 3.0F, 12.0F, 4.0F, 0.0F, true);
		leftLeg.setTextureOffset(86, 47).addBox(-3.0F, 10.0F, -6.5F, 3.0F, 2.0F, 5.0F, 0.0F, true);
		leftLeg.setModelRendererName("leftLeg");
		this.registerModelRenderer(leftLeg);

		this.rootBones.add(main);
	}


	@Override
	public ResourceLocation getAnimationFileLocation(BatEntity entity)
	{
		return new ResourceLocation("geckolib", "animations/bat.json");
	}
}