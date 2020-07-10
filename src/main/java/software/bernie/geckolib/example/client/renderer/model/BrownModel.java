// Made with Blockbench 3.5.4
// Exported for Minecraft version 1.12.2 or 1.15.2 (same format for both) for entity models animated with GeckoLib
// Paste this class into your mod and follow the documentation for GeckoLib to use animations. You can find the documentation here: https://github.com/bernie-g/geckolib
// Blockbench plugin created by Gecko
package software.bernie.geckolib.example.client.renderer.model;

import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib.animation.model.AnimatedEntityModel;
import software.bernie.geckolib.animation.model.AnimatedModelRenderer;
import software.bernie.geckolib.example.entity.BrownEntity;

public class BrownModel extends AnimatedEntityModel<BrownEntity>
{

	private final AnimatedModelRenderer all;
	private final AnimatedModelRenderer rightleg;
	private final AnimatedModelRenderer rightleg3;
	private final AnimatedModelRenderer right_feet;
	private final AnimatedModelRenderer leftleg;
	private final AnimatedModelRenderer leftleg2;
	private final AnimatedModelRenderer left_feet;
	private final AnimatedModelRenderer slam;
	private final AnimatedModelRenderer warn;
	private final AnimatedModelRenderer body;
	private final AnimatedModelRenderer body2;
	private final AnimatedModelRenderer body3;
	private final AnimatedModelRenderer rightarm;
	private final AnimatedModelRenderer rightarm2;
	private final AnimatedModelRenderer righthand;
	private final AnimatedModelRenderer righthand2;
	private final AnimatedModelRenderer righthand7;
	private final AnimatedModelRenderer righthand3;
	private final AnimatedModelRenderer righthand8;
	private final AnimatedModelRenderer righthand4;
	private final AnimatedModelRenderer righthand9;
	private final AnimatedModelRenderer righthand5;
	private final AnimatedModelRenderer righthand10;
	private final AnimatedModelRenderer righthand6;
	private final AnimatedModelRenderer righthand11;
	private final AnimatedModelRenderer arm1;
	private final AnimatedModelRenderer head;
	private final AnimatedModelRenderer siren1;
	private final AnimatedModelRenderer teeth2;
	private final AnimatedModelRenderer teeth;
	private final AnimatedModelRenderer siren2;
	private final AnimatedModelRenderer teeth4;
	private final AnimatedModelRenderer teeth3;
	private final AnimatedModelRenderer leftarm;
	private final AnimatedModelRenderer leftarm2;
	private final AnimatedModelRenderer lefthand;
	private final AnimatedModelRenderer lefthand2;
	private final AnimatedModelRenderer lefthand11;
	private final AnimatedModelRenderer lefthand3;
	private final AnimatedModelRenderer lefthand10;
	private final AnimatedModelRenderer lefthand4;
	private final AnimatedModelRenderer lefthand9;
	private final AnimatedModelRenderer lefthand5;
	private final AnimatedModelRenderer lefthand8;
	private final AnimatedModelRenderer lefthand6;
	private final AnimatedModelRenderer lefthand7;
	private final AnimatedModelRenderer arm2;
	private final AnimatedModelRenderer cen_bone;
	private final AnimatedModelRenderer bone;
	private final AnimatedModelRenderer bone2;
	private final AnimatedModelRenderer bone9;
	private final AnimatedModelRenderer bone10;
	private final AnimatedModelRenderer bone11;
	private final AnimatedModelRenderer bone12;
	private final AnimatedModelRenderer bone3;
	private final AnimatedModelRenderer bone4;
	private final AnimatedModelRenderer bone5;
	private final AnimatedModelRenderer bone6;
	private final AnimatedModelRenderer bone7;
	private final AnimatedModelRenderer bone8;
	private final AnimatedModelRenderer cen_bone4;
	private final AnimatedModelRenderer bone37;
	private final AnimatedModelRenderer bone38;
	private final AnimatedModelRenderer bone39;
	private final AnimatedModelRenderer bone40;
	private final AnimatedModelRenderer bone41;
	private final AnimatedModelRenderer bone42;
	private final AnimatedModelRenderer bone43;
	private final AnimatedModelRenderer bone44;
	private final AnimatedModelRenderer bone45;
	private final AnimatedModelRenderer bone46;
	private final AnimatedModelRenderer bone47;
	private final AnimatedModelRenderer bone48;
	private final AnimatedModelRenderer cen_bone2;
	private final AnimatedModelRenderer bone13;
	private final AnimatedModelRenderer bone14;
	private final AnimatedModelRenderer bone15;
	private final AnimatedModelRenderer bone16;
	private final AnimatedModelRenderer bone17;
	private final AnimatedModelRenderer bone18;
	private final AnimatedModelRenderer bone19;
	private final AnimatedModelRenderer bone20;
	private final AnimatedModelRenderer bone21;
	private final AnimatedModelRenderer bone22;
	private final AnimatedModelRenderer bone23;
	private final AnimatedModelRenderer bone24;
	private final AnimatedModelRenderer cen_bone3;
	private final AnimatedModelRenderer bone25;
	private final AnimatedModelRenderer bone26;
	private final AnimatedModelRenderer bone27;
	private final AnimatedModelRenderer bone28;
	private final AnimatedModelRenderer bone29;
	private final AnimatedModelRenderer bone30;
	private final AnimatedModelRenderer bone31;
	private final AnimatedModelRenderer bone32;
	private final AnimatedModelRenderer bone33;
	private final AnimatedModelRenderer bone34;
	private final AnimatedModelRenderer bone35;
	private final AnimatedModelRenderer bone36;
	private final AnimatedModelRenderer bone50;
	private final AnimatedModelRenderer bone51;
	private final AnimatedModelRenderer bone49;
	private final AnimatedModelRenderer bone52;

	public BrownModel()
	{
		textureWidth = 128;
		textureHeight = 128;
		all = new AnimatedModelRenderer(this);
		all.setRotationPoint(0.0F, -16.0F, 0.0F);

		all.setModelRendererName("all");
		this.registerModelRenderer(all);

		rightleg = new AnimatedModelRenderer(this);
		rightleg.setRotationPoint(4.0F, 0.0F, 0.0F);
		all.addChild(rightleg);
		rightleg.setTextureOffset(66, 66).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 18.0F, 4.0F, 0.0F, false);
		rightleg.setModelRendererName("rightleg");
		this.registerModelRenderer(rightleg);

		rightleg3 = new AnimatedModelRenderer(this);
		rightleg3.setRotationPoint(0.0F, 18.0F, 0.0F);
		rightleg.addChild(rightleg3);
		rightleg3.setTextureOffset(82, 82).addBox(-2.0F, 0.0F, -2.0F, 1.0F, 21.0F, 4.0F, 0.0F, false);
		rightleg3.setTextureOffset(48, 81).addBox(1.0F, 0.0F, -2.0F, 1.0F, 21.0F, 4.0F, 0.0F, false);
		rightleg3.setTextureOffset(54, 112).addBox(-1.0F, 19.0F, -2.0F, 2.0F, 2.0F, 4.0F, 0.0F, false);
		rightleg3.setTextureOffset(101, 110).addBox(-1.0F, 0.0F, -2.0F, 2.0F, 3.0F, 4.0F, 0.0F, false);
		rightleg3.setModelRendererName("rightleg3");
		this.registerModelRenderer(rightleg3);

		right_feet = new AnimatedModelRenderer(this);
		right_feet.setRotationPoint(0.0F, 21.0F, 0.0F);
		rightleg3.addChild(right_feet);
		right_feet.setTextureOffset(35, 0).addBox(-2.4F, 0.0F, -2.0F, 4.0F, 1.0F, 4.0F, 0.001F, false);
		right_feet.setTextureOffset(42, 5).addBox(1.0F, 0.0F, -6.0F, 1.0F, 1.0F, 4.0F, 0.0F, false);
		right_feet.setTextureOffset(26, 114).addBox(-1.0F, 0.0F, -6.0F, 1.0F, 1.0F, 4.0F, 0.0F, false);
		right_feet.setTextureOffset(36, 114).addBox(-3.0F, 0.0F, -5.0F, 1.0F, 1.0F, 4.0F, 0.0F, false);
		right_feet.setModelRendererName("right_feet");
		this.registerModelRenderer(right_feet);

		leftleg = new AnimatedModelRenderer(this);
		leftleg.setRotationPoint(-4.0F, 0.0F, 0.0F);
		all.addChild(leftleg);
		leftleg.setTextureOffset(0, 61).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 18.0F, 4.0F, 0.0F, false);
		leftleg.setModelRendererName("leftleg");
		this.registerModelRenderer(leftleg);

		leftleg2 = new AnimatedModelRenderer(this);
		leftleg2.setRotationPoint(0.0F, 18.0F, 0.0F);
		leftleg.addChild(leftleg2);
		leftleg2.setTextureOffset(72, 20).addBox(-2.0F, 0.0F, -2.0F, 1.0F, 21.0F, 4.0F, 0.0F, false);
		leftleg2.setTextureOffset(16, 61).addBox(1.0F, 0.0F, -2.0F, 1.0F, 21.0F, 4.0F, 0.0F, false);
		leftleg2.setTextureOffset(111, 46).addBox(-1.0F, 19.0F, -2.0F, 2.0F, 2.0F, 4.0F, 0.0F, false);
		leftleg2.setTextureOffset(89, 110).addBox(-1.0F, 0.0F, -2.0F, 2.0F, 3.0F, 4.0F, 0.0F, false);
		leftleg2.setModelRendererName("leftleg2");
		this.registerModelRenderer(leftleg2);

		left_feet = new AnimatedModelRenderer(this);
		left_feet.setRotationPoint(0.0F, 21.0F, 0.0F);
		leftleg2.addChild(left_feet);
		left_feet.setTextureOffset(66, 0).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 1.0F, 4.0F, 0.001F, false);
		left_feet.setTextureOffset(49, 25).addBox(-2.0F, 0.0F, -6.0F, 1.0F, 1.0F, 4.0F, 0.0F, false);
		left_feet.setTextureOffset(78, 62).addBox(0.0F, 0.0F, -6.0F, 1.0F, 1.0F, 4.0F, 0.0F, false);
		left_feet.setTextureOffset(37, 87).addBox(2.0F, 0.0F, -5.0F, 1.0F, 1.0F, 4.0F, 0.0F, false);
		left_feet.setModelRendererName("left_feet");
		this.registerModelRenderer(left_feet);

		slam = new AnimatedModelRenderer(this);
		slam.setRotationPoint(0.0F, 28.0F, 0.0F);
		all.addChild(slam);
		slam.setTextureOffset(124, 126).addBox(-1.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, 0.0F, false);
		slam.setModelRendererName("slam");
		this.registerModelRenderer(slam);

		warn = new AnimatedModelRenderer(this);
		warn.setRotationPoint(0.0F, 17.0F, 0.0F);
		all.addChild(warn);
		warn.setTextureOffset(124, 126).addBox(-1.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, 0.0F, false);
		warn.setModelRendererName("warn");
		this.registerModelRenderer(warn);

		body = new AnimatedModelRenderer(this);
		body.setRotationPoint(0.0F, 0.0F, 0.0F);
		all.addChild(body);
		body.setTextureOffset(65, 5).addBox(-6.0F, -7.0F, -3.0F, 4.0F, 7.0F, 7.0F, 0.0F, false);
		body.setTextureOffset(63, 46).addBox(2.0F, -7.0F, -3.0F, 4.0F, 7.0F, 7.0F, 0.0F, false);
		body.setTextureOffset(78, 39).addBox(-2.0F, -7.0F, -2.0F, 4.0F, 7.0F, 6.0F, 0.0F, false);
		body.setModelRendererName("body");
		this.registerModelRenderer(body);

		body2 = new AnimatedModelRenderer(this);
		body2.setRotationPoint(0.0F, -8.0F, 0.0F);
		body.addChild(body2);
		body2.setTextureOffset(32, 32).addBox(-4.0F, -8.0F, -2.0F, 9.0F, 9.0F, 5.0F, 0.0F, false);
		body2.setModelRendererName("body2");
		this.registerModelRenderer(body2);

		body3 = new AnimatedModelRenderer(this);
		body3.setRotationPoint(0.0F, -8.0F, 0.0F);
		body2.addChild(body3);
		body3.setTextureOffset(0, 0).addBox(-7.0F, -17.0F, -3.0F, 14.0F, 17.0F, 7.0F, 0.0F, false);
		body3.setTextureOffset(0, 93).addBox(-1.2F, -16.0F, -4.0F, 2.0F, 13.0F, 1.0F, 0.0F, false);
		body3.setTextureOffset(58, 85).addBox(-1.0F, -17.0F, 4.0F, 2.0F, 17.0F, 1.0F, 0.0F, false);
		body3.setTextureOffset(70, 111).addBox(7.0F, -17.0F, -2.0F, 1.0F, 4.0F, 4.0F, 0.0F, false);
		body3.setTextureOffset(16, 111).addBox(-8.0F, -17.0F, -2.0F, 1.0F, 4.0F, 4.0F, 0.0F, false);
		body3.setModelRendererName("body3");
		this.registerModelRenderer(body3);

		rightarm = new AnimatedModelRenderer(this);
		rightarm.setRotationPoint(9.0F, -14.5F, 0.0F);
		body3.addChild(rightarm);
		rightarm.setTextureOffset(16, 24).addBox(-1.0F, -2.5F, -2.0F, 4.0F, 33.0F, 4.0F, 0.0F, false);
		rightarm.setModelRendererName("rightarm");
		this.registerModelRenderer(rightarm);

		rightarm2 = new AnimatedModelRenderer(this);
		rightarm2.setRotationPoint(1.0F, 30.5F, 0.0F);
		rightarm.addChild(rightarm2);
		rightarm2.setTextureOffset(60, 24).addBox(-2.0F, 0.0F, -2.0F, 1.0F, 25.0F, 4.0F, 0.0F, false);
		rightarm2.setTextureOffset(28, 58).addBox(1.0F, 0.0F, -2.0F, 1.0F, 25.0F, 4.0F, 0.0F, false);
		rightarm2.setTextureOffset(42, 110).addBox(-1.0F, 22.0F, -2.0F, 2.0F, 3.0F, 4.0F, 0.0F, false);
		rightarm2.setTextureOffset(92, 92).addBox(-1.0F, 0.0F, -2.0F, 2.0F, 6.0F, 4.0F, 0.0F, false);
		rightarm2.setModelRendererName("rightarm2");
		this.registerModelRenderer(rightarm2);

		righthand = new AnimatedModelRenderer(this);
		righthand.setRotationPoint(0.0F, 25.0F, 0.0F);
		rightarm2.addChild(righthand);
		righthand.setTextureOffset(113, 113).addBox(-2.0F, 0.0F, -1.0F, 4.0F, 3.0F, 2.0F, 0.0F, false);
		righthand.setModelRendererName("righthand");
		this.registerModelRenderer(righthand);

		righthand2 = new AnimatedModelRenderer(this);
		righthand2.setRotationPoint(1.5F, 3.0F, -0.5F);
		righthand.addChild(righthand2);
		setRotationAngle(righthand2, 0.1745F, 0.0F, -0.1745F);
		righthand2.setTextureOffset(16, 24).addBox(-0.5F, 0.0F, -0.5F, 1.0F, 1.0F, 1.0F, 0.0F, false);
		righthand2.setModelRendererName("righthand2");
		this.registerModelRenderer(righthand2);

		righthand7 = new AnimatedModelRenderer(this);
		righthand7.setRotationPoint(0.0F, 1.0F, 0.0F);
		righthand2.addChild(righthand7);
		setRotationAngle(righthand7, 0.2618F, 0.0F, 0.0F);
		righthand7.setTextureOffset(55, 33).addBox(-0.5F, 0.0F, -0.5F, 1.0F, 2.0F, 1.0F, 0.0F, false);
		righthand7.setModelRendererName("righthand7");
		this.registerModelRenderer(righthand7);

		righthand3 = new AnimatedModelRenderer(this);
		righthand3.setRotationPoint(0.5F, 3.0F, -0.5F);
		righthand.addChild(righthand3);
		setRotationAngle(righthand3, 0.1745F, 0.0F, -0.0873F);
		righthand3.setTextureOffset(55, 30).addBox(-0.5F, 0.0F, -0.5F, 1.0F, 2.0F, 1.0F, 0.0F, false);
		righthand3.setModelRendererName("righthand3");
		this.registerModelRenderer(righthand3);

		righthand8 = new AnimatedModelRenderer(this);
		righthand8.setRotationPoint(0.0F, 1.5F, 0.0F);
		righthand3.addChild(righthand8);
		setRotationAngle(righthand8, 0.1745F, 0.0F, 0.0F);
		righthand8.setTextureOffset(49, 49).addBox(-0.5F, -0.5F, -0.5F, 1.0F, 3.0F, 1.0F, 0.0F, false);
		righthand8.setModelRendererName("righthand8");
		this.registerModelRenderer(righthand8);

		righthand4 = new AnimatedModelRenderer(this);
		righthand4.setRotationPoint(-0.5F, 3.0F, -0.5F);
		righthand.addChild(righthand4);
		setRotationAngle(righthand4, 0.0873F, 0.0F, 0.0F);
		righthand4.setTextureOffset(48, 5).addBox(-0.5F, 0.0F, -0.5F, 1.0F, 3.0F, 1.0F, 0.0F, false);
		righthand4.setModelRendererName("righthand4");
		this.registerModelRenderer(righthand4);

		righthand9 = new AnimatedModelRenderer(this);
		righthand9.setRotationPoint(0.0F, 2.5F, 0.0F);
		righthand4.addChild(righthand9);
		setRotationAngle(righthand9, 0.2618F, 0.0F, 0.0F);
		righthand9.setTextureOffset(42, 5).addBox(-0.5F, -0.5F, -0.5F, 1.0F, 3.0F, 1.0F, 0.0F, false);
		righthand9.setModelRendererName("righthand9");
		this.registerModelRenderer(righthand9);

		righthand5 = new AnimatedModelRenderer(this);
		righthand5.setRotationPoint(-1.5F, 3.0F, -0.5F);
		righthand.addChild(righthand5);
		setRotationAngle(righthand5, 0.0F, 0.0F, 0.1745F);
		righthand5.setTextureOffset(55, 25).addBox(-0.5F, 0.0F, -0.5F, 1.0F, 2.0F, 1.0F, 0.0F, false);
		righthand5.setModelRendererName("righthand5");
		this.registerModelRenderer(righthand5);

		righthand10 = new AnimatedModelRenderer(this);
		righthand10.setRotationPoint(0.0F, 1.5F, 0.0F);
		righthand5.addChild(righthand10);
		setRotationAngle(righthand10, 0.2618F, 0.0F, 0.0F);
		righthand10.setTextureOffset(35, 0).addBox(-0.5F, -0.5F, -0.5F, 1.0F, 3.0F, 1.0F, 0.0F, false);
		righthand10.setModelRendererName("righthand10");
		this.registerModelRenderer(righthand10);

		righthand6 = new AnimatedModelRenderer(this);
		righthand6.setRotationPoint(-2.0F, 1.0F, 0.0F);
		righthand.addChild(righthand6);
		setRotationAngle(righthand6, 0.0F, 0.0F, 0.1745F);
		righthand6.setTextureOffset(47, 0).addBox(-1.0F, 0.0F, -1.0F, 1.0F, 2.0F, 2.0F, 0.0F, false);
		righthand6.setModelRendererName("righthand6");
		this.registerModelRenderer(righthand6);

		righthand11 = new AnimatedModelRenderer(this);
		righthand11.setRotationPoint(-0.5F, 1.5F, 0.0F);
		righthand6.addChild(righthand11);
		setRotationAngle(righthand11, 0.1745F, 0.0F, 0.0F);
		righthand11.setTextureOffset(82, 30).addBox(-0.5F, -0.5F, -1.0F, 1.0F, 3.0F, 2.0F, 0.0F, false);
		righthand11.setModelRendererName("righthand11");
		this.registerModelRenderer(righthand11);

		arm1 = new AnimatedModelRenderer(this);
		arm1.setRotationPoint(0.0F, 0.5F, 0.0F);
		righthand.addChild(arm1);

		arm1.setModelRendererName("arm1");
		this.registerModelRenderer(arm1);

		head = new AnimatedModelRenderer(this);
		head.setRotationPoint(0.0F, -16.75F, 0.5F);
		body3.addChild(head);
		head.setTextureOffset(32, 25).addBox(-3.0F, -1.25F, -2.5F, 6.0F, 1.0F, 5.0F, 0.0F, false);
		head.setTextureOffset(48, 58).addBox(-1.0F, -21.25F, -1.0F, 2.0F, 20.0F, 2.0F, 0.0F, false);
		head.setModelRendererName("head");
		this.registerModelRenderer(head);

		siren1 = new AnimatedModelRenderer(this);
		siren1.setRotationPoint(2.0385F, -9.25F, 0.0F);
		head.addChild(siren1);
		siren1.setTextureOffset(92, 82).addBox(-1.0385F, -3.0F, -1.5F, 3.0F, 6.0F, 3.0F, 0.0F, false);
		siren1.setTextureOffset(92, 117).addBox(1.9615F, -3.0F, -3.5F, 1.0F, 6.0F, 2.0F, 0.0F, false);
		siren1.setTextureOffset(8, 111).addBox(4.9615F, -5.0F, -5.5F, 3.0F, 10.0F, 1.0F, 0.0F, false);
		siren1.setTextureOffset(86, 117).addBox(1.9615F, -3.0F, 1.5F, 1.0F, 6.0F, 2.0F, 0.0F, false);
		siren1.setTextureOffset(0, 111).addBox(4.9615F, -5.0F, 4.5F, 3.0F, 10.0F, 1.0F, 0.0F, false);
		siren1.setTextureOffset(46, 117).addBox(2.9615F, -4.0F, 3.5F, 2.0F, 8.0F, 1.0F, 0.0F, false);
		siren1.setTextureOffset(80, 114).addBox(2.9615F, -4.0F, -4.5F, 2.0F, 8.0F, 1.0F, 0.0F, false);
		siren1.setTextureOffset(64, 88).addBox(1.9615F, -4.0F, -3.5F, 1.0F, 1.0F, 7.0F, 0.0F, false);
		siren1.setTextureOffset(48, 12).addBox(4.9615F, -6.0F, -5.5F, 3.0F, 1.0F, 11.0F, 0.0F, false);
		siren1.setTextureOffset(32, 46).addBox(4.9615F, 5.0F, -5.5F, 3.0F, 1.0F, 11.0F, 0.0F, false);
		siren1.setTextureOffset(82, 20).addBox(2.9615F, 4.0F, -4.5F, 2.0F, 1.0F, 9.0F, 0.0F, false);
		siren1.setTextureOffset(80, 0).addBox(2.9615F, -5.0F, -4.5F, 2.0F, 1.0F, 9.0F, 0.0F, false);
		siren1.setTextureOffset(28, 87).addBox(1.9615F, 3.0F, -3.5F, 1.0F, 1.0F, 7.0F, 0.0F, false);
		siren1.setModelRendererName("siren1");
		this.registerModelRenderer(siren1);

		teeth2 = new AnimatedModelRenderer(this);
		teeth2.setRotationPoint(3.9615F, 4.0F, 0.0F);
		siren1.addChild(teeth2);
		teeth2.setTextureOffset(82, 72).addBox(-1.0F, -3.0F, -3.5F, 2.0F, 3.0F, 7.0F, 0.0F, false);
		teeth2.setModelRendererName("teeth2");
		this.registerModelRenderer(teeth2);

		teeth = new AnimatedModelRenderer(this);
		teeth.setRotationPoint(3.9615F, -4.0F, 0.0F);
		siren1.addChild(teeth);
		teeth.setTextureOffset(82, 62).addBox(-1.0F, 0.0F, -3.5F, 2.0F, 3.0F, 7.0F, 0.0F, false);
		teeth.setModelRendererName("teeth");
		this.registerModelRenderer(teeth);

		siren2 = new AnimatedModelRenderer(this);
		siren2.setRotationPoint(-1.9615F, -16.25F, 0.0F);
		head.addChild(siren2);
		setRotationAngle(siren2, 0.0F, 3.1416F, 0.0F);
		siren2.setTextureOffset(91, 10).addBox(-1.0385F, -3.0F, -1.5F, 3.0F, 6.0F, 3.0F, 0.0F, false);
		siren2.setTextureOffset(116, 74).addBox(1.9615F, -3.0F, -3.5F, 1.0F, 6.0F, 2.0F, 0.0F, false);
		siren2.setTextureOffset(48, 12).addBox(4.9615F, -5.0F, -5.5F, 3.0F, 10.0F, 1.0F, 0.0F, false);
		siren2.setTextureOffset(64, 116).addBox(1.9615F, -3.0F, 1.5F, 1.0F, 6.0F, 2.0F, 0.0F, false);
		siren2.setTextureOffset(32, 46).addBox(4.9615F, -5.0F, 4.5F, 3.0F, 10.0F, 1.0F, 0.0F, false);
		siren2.setTextureOffset(82, 20).addBox(2.9615F, -4.0F, 3.5F, 2.0F, 8.0F, 1.0F, 0.0F, false);
		siren2.setTextureOffset(82, 0).addBox(2.9615F, -4.0F, -4.5F, 2.0F, 8.0F, 1.0F, 0.0F, false);
		siren2.setTextureOffset(9, 86).addBox(1.9615F, -4.0F, -3.5F, 1.0F, 1.0F, 7.0F, 0.0F, false);
		siren2.setTextureOffset(42, 0).addBox(4.9615F, -6.0F, -5.5F, 3.0F, 1.0F, 11.0F, 0.0F, false);
		siren2.setTextureOffset(31, 13).addBox(4.9615F, 5.0F, -5.5F, 3.0F, 1.0F, 11.0F, 0.0F, false);
		siren2.setTextureOffset(78, 10).addBox(2.9615F, 4.0F, -4.5F, 2.0F, 1.0F, 9.0F, 0.0F, false);
		siren2.setTextureOffset(76, 52).addBox(2.9615F, -5.0F, -4.5F, 2.0F, 1.0F, 9.0F, 0.0F, false);
		siren2.setTextureOffset(82, 30).addBox(1.9615F, 3.0F, -3.5F, 1.0F, 1.0F, 7.0F, 0.0F, false);
		siren2.setModelRendererName("siren2");
		this.registerModelRenderer(siren2);

		teeth4 = new AnimatedModelRenderer(this);
		teeth4.setRotationPoint(4.4615F, -4.0F, 0.0F);
		siren2.addChild(teeth4);
		teeth4.setTextureOffset(19, 80).addBox(-0.5F, 0.0F, -3.5F, 1.0F, 3.0F, 7.0F, 0.0F, false);
		teeth4.setModelRendererName("teeth4");
		this.registerModelRenderer(teeth4);

		teeth3 = new AnimatedModelRenderer(this);
		teeth3.setRotationPoint(4.4615F, 4.0F, 0.0F);
		siren2.addChild(teeth3);
		teeth3.setTextureOffset(0, 83).addBox(-0.5F, -3.0F, -3.5F, 1.0F, 3.0F, 7.0F, 0.0F, false);
		teeth3.setModelRendererName("teeth3");
		this.registerModelRenderer(teeth3);

		leftarm = new AnimatedModelRenderer(this);
		leftarm.setRotationPoint(-9.0F, -14.5F, 0.0F);
		body3.addChild(leftarm);
		leftarm.setTextureOffset(0, 24).addBox(-3.0F, -2.5F, -2.0F, 4.0F, 33.0F, 4.0F, 0.0F, false);
		leftarm.setModelRendererName("leftarm");
		this.registerModelRenderer(leftarm);

		leftarm2 = new AnimatedModelRenderer(this);
		leftarm2.setRotationPoint(-1.0F, 30.5F, 0.0F);
		leftarm.addChild(leftarm2);
		leftarm2.setTextureOffset(38, 58).addBox(1.0F, 0.0F, -2.0F, 1.0F, 25.0F, 4.0F, 0.0F, false);
		leftarm2.setTextureOffset(62, 108).addBox(-1.0F, 22.0F, -2.0F, 2.0F, 3.0F, 4.0F, 0.0F, false);
		leftarm2.setTextureOffset(89, 52).addBox(-1.0F, 0.0F, -2.0F, 2.0F, 5.0F, 4.0F, 0.0F, false);
		leftarm2.setTextureOffset(56, 56).addBox(-2.0F, 0.0F, -2.0F, 1.0F, 25.0F, 4.0F, 0.0F, false);
		leftarm2.setModelRendererName("leftarm2");
		this.registerModelRenderer(leftarm2);

		lefthand = new AnimatedModelRenderer(this);
		lefthand.setRotationPoint(0.0F, 25.5F, 0.0F);
		leftarm2.addChild(lefthand);
		lefthand.setTextureOffset(6, 94).addBox(-2.0F, -0.5F, -1.0F, 4.0F, 3.0F, 2.0F, 0.0F, false);
		lefthand.setModelRendererName("lefthand");
		this.registerModelRenderer(lefthand);

		lefthand2 = new AnimatedModelRenderer(this);
		lefthand2.setRotationPoint(1.5F, 2.5F, -0.5F);
		lefthand.addChild(lefthand2);
		setRotationAngle(lefthand2, 0.0F, 0.0F, -0.0873F);
		lefthand2.setTextureOffset(53, 46).addBox(-0.5F, 0.0F, -0.5F, 1.0F, 2.0F, 1.0F, 0.0F, false);
		lefthand2.setModelRendererName("lefthand2");
		this.registerModelRenderer(lefthand2);

		lefthand11 = new AnimatedModelRenderer(this);
		lefthand11.setRotationPoint(0.0F, 1.4F, 0.0F);
		lefthand2.addChild(lefthand11);
		setRotationAngle(lefthand11, 0.2618F, 0.0F, 0.0F);
		lefthand11.setTextureOffset(32, 25).addBox(-0.5F, -0.4F, -0.5F, 1.0F, 3.0F, 1.0F, 0.0F, false);
		lefthand11.setModelRendererName("lefthand11");
		this.registerModelRenderer(lefthand11);

		lefthand3 = new AnimatedModelRenderer(this);
		lefthand3.setRotationPoint(0.5F, 2.5F, -0.5F);
		lefthand.addChild(lefthand3);
		lefthand3.setTextureOffset(32, 32).addBox(-0.5F, 0.0F, -0.5F, 1.0F, 3.0F, 1.0F, 0.0F, false);
		lefthand3.setModelRendererName("lefthand3");
		this.registerModelRenderer(lefthand3);

		lefthand10 = new AnimatedModelRenderer(this);
		lefthand10.setRotationPoint(0.0F, 2.5F, 0.0F);
		lefthand3.addChild(lefthand10);
		setRotationAngle(lefthand10, 0.4363F, 0.0F, 0.0F);
		lefthand10.setTextureOffset(28, 24).addBox(-0.5F, -0.5F, -0.5F, 1.0F, 3.0F, 1.0F, 0.0F, false);
		lefthand10.setModelRendererName("lefthand10");
		this.registerModelRenderer(lefthand10);

		lefthand4 = new AnimatedModelRenderer(this);
		lefthand4.setRotationPoint(-0.5F, 2.5F, -0.5F);
		lefthand.addChild(lefthand4);
		setRotationAngle(lefthand4, 0.0F, 0.0F, 0.0873F);
		lefthand4.setTextureOffset(49, 46).addBox(-0.5F, 0.0F, -0.5F, 1.0F, 2.0F, 1.0F, 0.0F, false);
		lefthand4.setModelRendererName("lefthand4");
		this.registerModelRenderer(lefthand4);

		lefthand9 = new AnimatedModelRenderer(this);
		lefthand9.setRotationPoint(0.0F, 1.5F, 0.0F);
		lefthand4.addChild(lefthand9);
		setRotationAngle(lefthand9, 0.5236F, 0.0F, 0.0F);
		lefthand9.setTextureOffset(0, 24).addBox(-0.5F, -0.5F, -0.5F, 1.0F, 3.0F, 1.0F, 0.0F, false);
		lefthand9.setModelRendererName("lefthand9");
		this.registerModelRenderer(lefthand9);

		lefthand5 = new AnimatedModelRenderer(this);
		lefthand5.setRotationPoint(-1.5F, 2.5F, -0.5F);
		lefthand.addChild(lefthand5);
		setRotationAngle(lefthand5, 0.0F, 0.0F, 0.1745F);
		lefthand5.setTextureOffset(0, 5).addBox(-0.5F, 0.0F, -0.5F, 1.0F, 1.0F, 1.0F, 0.0F, false);
		lefthand5.setModelRendererName("lefthand5");
		this.registerModelRenderer(lefthand5);

		lefthand8 = new AnimatedModelRenderer(this);
		lefthand8.setRotationPoint(0.0F, 1.0F, 0.0F);
		lefthand5.addChild(lefthand8);
		setRotationAngle(lefthand8, 0.5236F, 0.0F, 0.0F);
		lefthand8.setTextureOffset(49, 25).addBox(-0.5F, 0.0F, -0.5F, 1.0F, 2.0F, 1.0F, 0.0F, false);
		lefthand8.setModelRendererName("lefthand8");
		this.registerModelRenderer(lefthand8);

		lefthand6 = new AnimatedModelRenderer(this);
		lefthand6.setRotationPoint(2.0F, 0.5F, -0.5F);
		lefthand.addChild(lefthand6);
		setRotationAngle(lefthand6, 0.0F, 0.0F, -0.1745F);
		lefthand6.setTextureOffset(12, 24).addBox(0.0F, 0.0F, -0.5F, 1.0F, 2.0F, 2.0F, 0.0F, false);
		lefthand6.setModelRendererName("lefthand6");
		this.registerModelRenderer(lefthand6);

		lefthand7 = new AnimatedModelRenderer(this);
		lefthand7.setRotationPoint(0.5F, 1.5F, 0.0F);
		lefthand6.addChild(lefthand7);
		setRotationAngle(lefthand7, 0.4363F, 0.0F, 0.0F);
		lefthand7.setTextureOffset(0, 0).addBox(-0.5F, -0.5F, -0.5F, 1.0F, 3.0F, 2.0F, 0.0F, false);
		lefthand7.setModelRendererName("lefthand7");
		this.registerModelRenderer(lefthand7);

		arm2 = new AnimatedModelRenderer(this);
		arm2.setRotationPoint(0.0F, 0.0F, 0.0F);
		lefthand.addChild(arm2);

		arm2.setModelRendererName("arm2");
		this.registerModelRenderer(arm2);

		cen_bone = new AnimatedModelRenderer(this);
		cen_bone.setRotationPoint(0.0F, -6.0F, -3.0F);
		body3.addChild(cen_bone);

		cen_bone.setModelRendererName("cen_bone");
		this.registerModelRenderer(cen_bone);

		bone = new AnimatedModelRenderer(this);
		bone.setRotationPoint(1.0F, -4.5F, 0.0F);
		cen_bone.addChild(bone);
		setRotationAngle(bone, 0.0F, -0.1745F, 0.1745F);
		bone.setTextureOffset(30, 110).addBox(0.0F, -1.5F, -1.0F, 6.0F, 2.0F, 2.0F, 0.0F, false);
		bone.setModelRendererName("bone");
		this.registerModelRenderer(bone);

		bone2 = new AnimatedModelRenderer(this);
		bone2.setRotationPoint(6.0F, 0.0F, 0.0F);
		bone.addChild(bone2);
		setRotationAngle(bone2, 0.0F, -1.6581F, 0.0F);
		bone2.setTextureOffset(109, 62).addBox(0.0F, -1.5F, -1.0F, 6.0F, 2.0F, 2.0F, 0.0F, false);
		bone2.setModelRendererName("bone2");
		this.registerModelRenderer(bone2);

		bone9 = new AnimatedModelRenderer(this);
		bone9.setRotationPoint(1.0F, -6.5F, 0.0F);
		cen_bone.addChild(bone9);
		setRotationAngle(bone9, 0.0F, -0.1745F, 0.0F);
		bone9.setTextureOffset(16, 107).addBox(0.0F, -1.5F, -1.0F, 6.0F, 2.0F, 2.0F, 0.0F, false);
		bone9.setModelRendererName("bone9");
		this.registerModelRenderer(bone9);

		bone10 = new AnimatedModelRenderer(this);
		bone10.setRotationPoint(6.0F, 0.0F, 0.0F);
		bone9.addChild(bone10);
		setRotationAngle(bone10, 0.0F, -1.6581F, 0.0F);
		bone10.setTextureOffset(0, 107).addBox(0.0F, -1.5F, -1.0F, 6.0F, 2.0F, 2.0F, 0.0F, false);
		bone10.setModelRendererName("bone10");
		this.registerModelRenderer(bone10);

		bone11 = new AnimatedModelRenderer(this);
		bone11.setRotationPoint(1.0F, -8.5F, 0.0F);
		cen_bone.addChild(bone11);
		setRotationAngle(bone11, 0.0F, -0.1745F, -0.2618F);
		bone11.setTextureOffset(106, 99).addBox(0.0F, -1.5F, -1.0F, 6.0F, 2.0F, 2.0F, 0.0F, false);
		bone11.setModelRendererName("bone11");
		this.registerModelRenderer(bone11);

		bone12 = new AnimatedModelRenderer(this);
		bone12.setRotationPoint(6.0F, 0.0F, 0.0F);
		bone11.addChild(bone12);
		setRotationAngle(bone12, 0.0F, -1.6581F, 0.0F);
		bone12.setTextureOffset(106, 106).addBox(0.0F, -1.5F, -1.0F, 6.0F, 2.0F, 2.0F, 0.0F, false);
		bone12.setModelRendererName("bone12");
		this.registerModelRenderer(bone12);

		bone3 = new AnimatedModelRenderer(this);
		bone3.setRotationPoint(1.0F, -1.5F, 0.0F);
		cen_bone.addChild(bone3);
		setRotationAngle(bone3, 0.0F, -0.1745F, 0.0873F);
		bone3.setTextureOffset(109, 22).addBox(0.0F, -1.5F, -1.0F, 6.0F, 2.0F, 2.0F, 0.0F, false);
		bone3.setModelRendererName("bone3");
		this.registerModelRenderer(bone3);

		bone4 = new AnimatedModelRenderer(this);
		bone4.setRotationPoint(6.0F, 0.0F, 0.0F);
		bone3.addChild(bone4);
		setRotationAngle(bone4, 0.0F, -1.6581F, 0.0F);
		bone4.setTextureOffset(108, 38).addBox(0.0F, -1.5F, -1.0F, 6.0F, 2.0F, 2.0F, 0.0F, false);
		bone4.setModelRendererName("bone4");
		this.registerModelRenderer(bone4);

		bone5 = new AnimatedModelRenderer(this);
		bone5.setRotationPoint(1.0F, 0.5F, 0.0F);
		cen_bone.addChild(bone5);
		setRotationAngle(bone5, 0.0F, -0.1745F, 0.1745F);
		bone5.setTextureOffset(50, 108).addBox(0.0F, -1.5F, -1.0F, 6.0F, 2.0F, 2.0F, 0.0F, false);
		bone5.setModelRendererName("bone5");
		this.registerModelRenderer(bone5);

		bone6 = new AnimatedModelRenderer(this);
		bone6.setRotationPoint(6.0F, 0.0F, 0.0F);
		bone5.addChild(bone6);
		setRotationAngle(bone6, 0.0F, -1.6581F, 0.0F);
		bone6.setTextureOffset(107, 70).addBox(0.0F, -1.5F, -1.0F, 6.0F, 2.0F, 2.0F, 0.0F, false);
		bone6.setModelRendererName("bone6");
		this.registerModelRenderer(bone6);

		bone7 = new AnimatedModelRenderer(this);
		bone7.setRotationPoint(1.0F, 2.5F, 0.0F);
		cen_bone.addChild(bone7);
		setRotationAngle(bone7, 0.0F, -0.1745F, 0.2618F);
		bone7.setTextureOffset(107, 2).addBox(0.0F, -1.5F, -1.0F, 6.0F, 2.0F, 2.0F, 0.0F, false);
		bone7.setModelRendererName("bone7");
		this.registerModelRenderer(bone7);

		bone8 = new AnimatedModelRenderer(this);
		bone8.setRotationPoint(6.0F, 0.0F, 0.0F);
		bone7.addChild(bone8);
		setRotationAngle(bone8, 0.2618F, -1.2217F, -1.8326F);
		bone8.setTextureOffset(70, 107).addBox(0.0F, -1.5F, -1.0F, 6.0F, 2.0F, 2.0F, 0.0F, false);
		bone8.setModelRendererName("bone8");
		this.registerModelRenderer(bone8);

		cen_bone4 = new AnimatedModelRenderer(this);
		cen_bone4.setRotationPoint(0.0F, -6.0F, 3.0F);
		body3.addChild(cen_bone4);
		setRotationAngle(cen_bone4, 0.0F, -2.8798F, 0.0F);

		cen_bone4.setModelRendererName("cen_bone4");
		this.registerModelRenderer(cen_bone4);

		bone37 = new AnimatedModelRenderer(this);
		bone37.setRotationPoint(1.0F, -4.5F, 0.0F);
		cen_bone4.addChild(bone37);
		setRotationAngle(bone37, 0.0F, -0.1745F, 0.1745F);
		bone37.setTextureOffset(97, 50).addBox(0.0F, -1.5F, -1.0F, 6.0F, 2.0F, 2.0F, 0.0F, false);
		bone37.setModelRendererName("bone37");
		this.registerModelRenderer(bone37);

		bone38 = new AnimatedModelRenderer(this);
		bone38.setRotationPoint(6.0F, 0.0F, 0.0F);
		bone37.addChild(bone38);
		setRotationAngle(bone38, 0.0F, -1.6581F, 0.0F);
		bone38.setTextureOffset(64, 96).addBox(0.0F, -1.5F, -1.0F, 6.0F, 2.0F, 2.0F, 0.0F, false);
		bone38.setModelRendererName("bone38");
		this.registerModelRenderer(bone38);

		bone39 = new AnimatedModelRenderer(this);
		bone39.setRotationPoint(1.0F, -6.5F, 0.0F);
		cen_bone4.addChild(bone39);
		setRotationAngle(bone39, 0.0F, -0.1745F, 0.0F);
		bone39.setTextureOffset(95, 24).addBox(0.0F, -1.5F, -1.0F, 6.0F, 2.0F, 2.0F, 0.0F, false);
		bone39.setModelRendererName("bone39");
		this.registerModelRenderer(bone39);

		bone40 = new AnimatedModelRenderer(this);
		bone40.setRotationPoint(6.0F, 0.0F, 0.0F);
		bone39.addChild(bone40);
		setRotationAngle(bone40, 0.0F, -1.6581F, 0.0F);
		bone40.setTextureOffset(95, 20).addBox(0.0F, -1.5F, -1.0F, 6.0F, 2.0F, 2.0F, 0.0F, false);
		bone40.setModelRendererName("bone40");
		this.registerModelRenderer(bone40);

		bone41 = new AnimatedModelRenderer(this);
		bone41.setRotationPoint(1.0F, -8.5F, 0.0F);
		cen_bone4.addChild(bone41);
		setRotationAngle(bone41, 0.0F, -0.1745F, -0.2618F);
		bone41.setTextureOffset(32, 95).addBox(0.0F, -1.5F, -1.0F, 6.0F, 2.0F, 2.0F, 0.0F, false);
		bone41.setModelRendererName("bone41");
		this.registerModelRenderer(bone41);

		bone42 = new AnimatedModelRenderer(this);
		bone42.setRotationPoint(6.0F, 0.0F, 0.0F);
		bone41.addChild(bone42);
		setRotationAngle(bone42, 0.0F, -1.6581F, 0.0F);
		bone42.setTextureOffset(93, 72).addBox(0.0F, -1.5F, -1.0F, 6.0F, 2.0F, 2.0F, 0.0F, false);
		bone42.setModelRendererName("bone42");
		this.registerModelRenderer(bone42);

		bone43 = new AnimatedModelRenderer(this);
		bone43.setRotationPoint(1.0F, -1.5F, 0.0F);
		cen_bone4.addChild(bone43);
		setRotationAngle(bone43, 0.0F, -0.1745F, 0.0873F);
		bone43.setTextureOffset(93, 62).addBox(0.0F, -1.5F, -1.0F, 6.0F, 2.0F, 2.0F, 0.0F, false);
		bone43.setModelRendererName("bone43");
		this.registerModelRenderer(bone43);

		bone44 = new AnimatedModelRenderer(this);
		bone44.setRotationPoint(6.0F, 0.0F, 0.0F);
		bone43.addChild(bone44);
		setRotationAngle(bone44, 0.0F, -1.6581F, 0.0F);
		bone44.setTextureOffset(93, 4).addBox(0.0F, -1.5F, -1.0F, 6.0F, 2.0F, 2.0F, 0.0F, false);
		bone44.setModelRendererName("bone44");
		this.registerModelRenderer(bone44);

		bone45 = new AnimatedModelRenderer(this);
		bone45.setRotationPoint(1.0F, 0.5F, 0.0F);
		cen_bone4.addChild(bone45);
		setRotationAngle(bone45, 0.0F, -0.1745F, 0.1745F);
		bone45.setTextureOffset(93, 0).addBox(0.0F, -1.5F, -1.0F, 6.0F, 2.0F, 2.0F, 0.0F, false);
		bone45.setModelRendererName("bone45");
		this.registerModelRenderer(bone45);

		bone46 = new AnimatedModelRenderer(this);
		bone46.setRotationPoint(6.0F, 0.0F, 0.0F);
		bone45.addChild(bone46);
		setRotationAngle(bone46, 0.0F, -1.6581F, 0.0F);
		bone46.setTextureOffset(92, 38).addBox(0.0F, -1.5F, -1.0F, 6.0F, 2.0F, 2.0F, 0.0F, false);
		bone46.setModelRendererName("bone46");
		this.registerModelRenderer(bone46);

		bone47 = new AnimatedModelRenderer(this);
		bone47.setRotationPoint(1.0F, 2.5F, 0.0F);
		cen_bone4.addChild(bone47);
		setRotationAngle(bone47, 0.0F, -0.1745F, 0.2618F);
		bone47.setTextureOffset(91, 30).addBox(0.0F, -1.5F, -1.0F, 6.0F, 2.0F, 2.0F, 0.0F, false);
		bone47.setModelRendererName("bone47");
		this.registerModelRenderer(bone47);

		bone48 = new AnimatedModelRenderer(this);
		bone48.setRotationPoint(6.0F, 0.0F, 0.0F);
		bone47.addChild(bone48);
		setRotationAngle(bone48, 0.2618F, -1.2217F, -1.8326F);
		bone48.setTextureOffset(66, 62).addBox(0.0F, -1.5F, -1.0F, 6.0F, 2.0F, 2.0F, 0.0F, false);
		bone48.setModelRendererName("bone48");
		this.registerModelRenderer(bone48);

		cen_bone2 = new AnimatedModelRenderer(this);
		cen_bone2.setRotationPoint(0.0F, -6.0F, -3.0F);
		body3.addChild(cen_bone2);
		setRotationAngle(cen_bone2, 0.0F, -2.8798F, 0.0F);

		cen_bone2.setModelRendererName("cen_bone2");
		this.registerModelRenderer(cen_bone2);

		bone13 = new AnimatedModelRenderer(this);
		bone13.setRotationPoint(1.0F, -4.5F, 0.0F);
		cen_bone2.addChild(bone13);
		setRotationAngle(bone13, 0.0F, -0.1745F, 0.1745F);
		bone13.setTextureOffset(90, 106).addBox(0.0F, -1.5F, -1.0F, 6.0F, 2.0F, 2.0F, 0.0F, false);
		bone13.setModelRendererName("bone13");
		this.registerModelRenderer(bone13);

		bone14 = new AnimatedModelRenderer(this);
		bone14.setRotationPoint(6.0F, 0.0F, 0.0F);
		bone13.addChild(bone14);
		setRotationAngle(bone14, 0.0F, 1.6581F, 0.0F);
		bone14.setTextureOffset(36, 106).addBox(0.0F, -1.5F, -1.0F, 6.0F, 2.0F, 2.0F, 0.0F, false);
		bone14.setModelRendererName("bone14");
		this.registerModelRenderer(bone14);

		bone15 = new AnimatedModelRenderer(this);
		bone15.setRotationPoint(1.0F, -6.5F, 0.0F);
		cen_bone2.addChild(bone15);
		setRotationAngle(bone15, 0.0F, -0.1745F, 0.0F);
		bone15.setTextureOffset(105, 28).addBox(0.0F, -1.5F, -1.0F, 6.0F, 2.0F, 2.0F, 0.0F, false);
		bone15.setModelRendererName("bone15");
		this.registerModelRenderer(bone15);

		bone16 = new AnimatedModelRenderer(this);
		bone16.setRotationPoint(6.0F, 0.0F, 0.0F);
		bone15.addChild(bone16);
		setRotationAngle(bone16, 0.0F, 1.6581F, 0.0F);
		bone16.setTextureOffset(104, 95).addBox(0.0F, -1.5F, -1.0F, 6.0F, 2.0F, 2.0F, 0.0F, false);
		bone16.setModelRendererName("bone16");
		this.registerModelRenderer(bone16);

		bone17 = new AnimatedModelRenderer(this);
		bone17.setRotationPoint(1.0F, -8.5F, 0.0F);
		cen_bone2.addChild(bone17);
		setRotationAngle(bone17, 0.0F, -0.1745F, -0.2618F);
		bone17.setTextureOffset(104, 84).addBox(0.0F, -1.5F, -1.0F, 6.0F, 2.0F, 2.0F, 0.0F, false);
		bone17.setModelRendererName("bone17");
		this.registerModelRenderer(bone17);

		bone18 = new AnimatedModelRenderer(this);
		bone18.setRotationPoint(6.0F, 0.0F, 0.0F);
		bone17.addChild(bone18);
		setRotationAngle(bone18, 0.0F, 1.6581F, 0.0F);
		bone18.setTextureOffset(56, 104).addBox(0.0F, -1.5F, -1.0F, 6.0F, 2.0F, 2.0F, 0.0F, false);
		bone18.setModelRendererName("bone18");
		this.registerModelRenderer(bone18);

		bone19 = new AnimatedModelRenderer(this);
		bone19.setRotationPoint(1.0F, -1.5F, 0.0F);
		cen_bone2.addChild(bone19);
		setRotationAngle(bone19, 0.0F, -0.1745F, 0.0873F);
		bone19.setTextureOffset(103, 16).addBox(0.0F, -1.5F, -1.0F, 6.0F, 2.0F, 2.0F, 0.0F, false);
		bone19.setModelRendererName("bone19");
		this.registerModelRenderer(bone19);

		bone20 = new AnimatedModelRenderer(this);
		bone20.setRotationPoint(6.0F, 0.0F, 0.0F);
		bone19.addChild(bone20);
		setRotationAngle(bone20, 0.0F, 1.6581F, 0.0F);
		bone20.setTextureOffset(103, 12).addBox(0.0F, -1.5F, -1.0F, 6.0F, 2.0F, 2.0F, 0.0F, false);
		bone20.setModelRendererName("bone20");
		this.registerModelRenderer(bone20);

		bone21 = new AnimatedModelRenderer(this);
		bone21.setRotationPoint(1.0F, 0.5F, 0.0F);
		cen_bone2.addChild(bone21);
		setRotationAngle(bone21, 0.0F, -0.1745F, 0.1745F);
		bone21.setTextureOffset(22, 103).addBox(0.0F, -1.5F, -1.0F, 6.0F, 2.0F, 2.0F, 0.0F, false);
		bone21.setModelRendererName("bone21");
		this.registerModelRenderer(bone21);

		bone22 = new AnimatedModelRenderer(this);
		bone22.setRotationPoint(6.0F, 0.0F, 0.0F);
		bone21.addChild(bone22);
		setRotationAngle(bone22, 0.0F, 1.6581F, 0.0F);
		bone22.setTextureOffset(6, 103).addBox(0.0F, -1.5F, -1.0F, 6.0F, 2.0F, 2.0F, 0.0F, false);
		bone22.setModelRendererName("bone22");
		this.registerModelRenderer(bone22);

		bone23 = new AnimatedModelRenderer(this);
		bone23.setRotationPoint(1.0F, 2.5F, 0.0F);
		cen_bone2.addChild(bone23);
		setRotationAngle(bone23, 0.0F, -0.1745F, 0.2618F);
		bone23.setTextureOffset(92, 102).addBox(0.0F, -1.5F, -1.0F, 6.0F, 2.0F, 2.0F, 0.0F, false);
		bone23.setModelRendererName("bone23");
		this.registerModelRenderer(bone23);

		bone24 = new AnimatedModelRenderer(this);
		bone24.setRotationPoint(6.0F, 0.0F, 0.0F);
		bone23.addChild(bone24);
		setRotationAngle(bone24, 0.2618F, 1.2217F, -1.8326F);
		bone24.setTextureOffset(101, 80).addBox(0.0F, -1.5F, -1.0F, 6.0F, 2.0F, 2.0F, 0.0F, false);
		bone24.setModelRendererName("bone24");
		this.registerModelRenderer(bone24);

		cen_bone3 = new AnimatedModelRenderer(this);
		cen_bone3.setRotationPoint(0.0F, -6.0F, 3.3F);
		body3.addChild(cen_bone3);
		setRotationAngle(cen_bone3, 0.0F, 0.0698F, 0.0F);

		cen_bone3.setModelRendererName("cen_bone3");
		this.registerModelRenderer(cen_bone3);

		bone25 = new AnimatedModelRenderer(this);
		bone25.setRotationPoint(1.0F, -4.5F, 0.0F);
		cen_bone3.addChild(bone25);
		setRotationAngle(bone25, 0.0F, -0.1745F, 0.1745F);
		bone25.setTextureOffset(101, 58).addBox(0.0F, -1.5F, -1.0F, 6.0F, 2.0F, 2.0F, 0.0F, false);
		bone25.setModelRendererName("bone25");
		this.registerModelRenderer(bone25);

		bone26 = new AnimatedModelRenderer(this);
		bone26.setRotationPoint(6.0F, 0.0F, 0.0F);
		bone25.addChild(bone26);
		setRotationAngle(bone26, 0.0F, 1.7453F, 0.0F);
		bone26.setTextureOffset(101, 54).addBox(0.0F, -1.5F, -1.0F, 6.0F, 2.0F, 2.0F, 0.0F, false);
		bone26.setModelRendererName("bone26");
		this.registerModelRenderer(bone26);

		bone27 = new AnimatedModelRenderer(this);
		bone27.setRotationPoint(1.0F, -6.5F, 0.0F);
		cen_bone3.addChild(bone27);
		setRotationAngle(bone27, 0.0F, -0.1745F, 0.0F);
		bone27.setTextureOffset(100, 91).addBox(0.0F, -1.5F, -1.0F, 6.0F, 2.0F, 2.0F, 0.0F, false);
		bone27.setModelRendererName("bone27");
		this.registerModelRenderer(bone27);

		bone28 = new AnimatedModelRenderer(this);
		bone28.setRotationPoint(6.0F, 0.0F, 0.0F);
		bone27.addChild(bone28);
		setRotationAngle(bone28, 0.0F, 1.7453F, 0.0F);
		bone28.setTextureOffset(100, 76).addBox(0.0F, -1.5F, -1.0F, 6.0F, 2.0F, 2.0F, 0.0F, false);
		bone28.setModelRendererName("bone28");
		this.registerModelRenderer(bone28);

		bone29 = new AnimatedModelRenderer(this);
		bone29.setRotationPoint(1.0F, -8.5F, 0.0F);
		cen_bone3.addChild(bone29);
		setRotationAngle(bone29, 0.0F, -0.1745F, -0.2618F);
		bone29.setTextureOffset(100, 66).addBox(0.0F, -1.5F, -1.0F, 6.0F, 2.0F, 2.0F, 0.0F, false);
		bone29.setModelRendererName("bone29");
		this.registerModelRenderer(bone29);

		bone30 = new AnimatedModelRenderer(this);
		bone30.setRotationPoint(6.0F, 0.0F, 0.0F);
		bone29.addChild(bone30);
		setRotationAngle(bone30, 0.0F, 1.7453F, 0.0F);
		bone30.setTextureOffset(100, 8).addBox(0.0F, -1.5F, -1.0F, 6.0F, 2.0F, 2.0F, 0.0F, false);
		bone30.setModelRendererName("bone30");
		this.registerModelRenderer(bone30);

		bone31 = new AnimatedModelRenderer(this);
		bone31.setRotationPoint(1.0F, -1.5F, 0.0F);
		cen_bone3.addChild(bone31);
		setRotationAngle(bone31, 0.0F, -0.1745F, 0.0873F);
		bone31.setTextureOffset(64, 100).addBox(0.0F, -1.5F, -1.0F, 6.0F, 2.0F, 2.0F, 0.0F, false);
		bone31.setModelRendererName("bone31");
		this.registerModelRenderer(bone31);

		bone32 = new AnimatedModelRenderer(this);
		bone32.setRotationPoint(6.0F, 0.0F, 0.0F);
		bone31.addChild(bone32);
		setRotationAngle(bone32, 0.0F, 1.7453F, 0.0F);
		bone32.setTextureOffset(30, 99).addBox(0.0F, -1.5F, -1.0F, 6.0F, 2.0F, 2.0F, 0.0F, false);
		bone32.setModelRendererName("bone32");
		this.registerModelRenderer(bone32);

		bone33 = new AnimatedModelRenderer(this);
		bone33.setRotationPoint(1.0F, 0.5F, 0.0F);
		cen_bone3.addChild(bone33);
		setRotationAngle(bone33, 0.0F, -0.1745F, 0.1745F);
		bone33.setTextureOffset(6, 99).addBox(0.0F, -1.5F, -1.0F, 6.0F, 2.0F, 2.0F, 0.0F, false);
		bone33.setModelRendererName("bone33");
		this.registerModelRenderer(bone33);

		bone34 = new AnimatedModelRenderer(this);
		bone34.setRotationPoint(6.0F, 0.0F, 0.0F);
		bone33.addChild(bone34);
		setRotationAngle(bone34, 0.0F, 1.7453F, 0.0F);
		bone34.setTextureOffset(98, 46).addBox(0.0F, -1.5F, -1.0F, 6.0F, 2.0F, 2.0F, 0.0F, false);
		bone34.setModelRendererName("bone34");
		this.registerModelRenderer(bone34);

		bone35 = new AnimatedModelRenderer(this);
		bone35.setRotationPoint(1.0F, 2.5F, 0.0F);
		cen_bone3.addChild(bone35);
		setRotationAngle(bone35, 0.0F, -0.1745F, 0.2618F);
		bone35.setTextureOffset(98, 42).addBox(0.0F, -1.5F, -1.0F, 6.0F, 2.0F, 2.0F, 0.0F, false);
		bone35.setModelRendererName("bone35");
		this.registerModelRenderer(bone35);

		bone36 = new AnimatedModelRenderer(this);
		bone36.setRotationPoint(6.0F, 0.0F, 0.0F);
		bone35.addChild(bone36);
		setRotationAngle(bone36, 0.2618F, 1.2217F, -1.8326F);
		bone36.setTextureOffset(98, 34).addBox(0.0F, -1.5F, -1.0F, 6.0F, 2.0F, 2.0F, 0.0F, false);
		bone36.setModelRendererName("bone36");
		this.registerModelRenderer(bone36);

		bone50 = new AnimatedModelRenderer(this);
		bone50.setRotationPoint(-5.5F, 1.0F, 0.5F);
		body2.addChild(bone50);
		setRotationAngle(bone50, 0.0F, 0.0F, 0.1745F);
		bone50.setTextureOffset(20, 90).addBox(0.5F, -5.0F, -2.5F, 1.0F, 6.0F, 5.0F, 0.0F, false);
		bone50.setModelRendererName("bone50");
		this.registerModelRenderer(bone50);

		bone51 = new AnimatedModelRenderer(this);
		bone51.setRotationPoint(-4.8F, -4.0F, 0.5F);
		body2.addChild(bone51);
		setRotationAngle(bone51, 0.0F, 0.0F, -0.0873F);
		bone51.setTextureOffset(59, 0).addBox(0.5F, -5.0F, -2.5F, 1.0F, 6.0F, 5.0F, 0.0F, false);
		bone51.setModelRendererName("bone51");
		this.registerModelRenderer(bone51);

		bone49 = new AnimatedModelRenderer(this);
		bone49.setRotationPoint(5.5F, -7.0F, 0.5F);
		body.addChild(bone49);
		setRotationAngle(bone49, 0.0F, 0.0F, 0.2618F);
		bone49.setTextureOffset(81, 107).addBox(-0.5F, -1.0F, -2.5F, 1.0F, 2.0F, 5.0F, 0.0F, false);
		bone49.setModelRendererName("bone49");
		this.registerModelRenderer(bone49);

		bone52 = new AnimatedModelRenderer(this);
		bone52.setRotationPoint(-5.5F, -7.0F, 0.5F);
		body.addChild(bone52);
		setRotationAngle(bone52, 0.0F, 0.0F, -0.2618F);
		bone52.setTextureOffset(49, 49).addBox(-0.5F, -1.0F, -2.5F, 1.0F, 2.0F, 5.0F, 0.0F, false);
		bone52.setModelRendererName("bone52");
		this.registerModelRenderer(bone52);

		this.rootBones.add(all);
	}


	@Override
	public ResourceLocation getAnimationFileLocation()
	{
		return new ResourceLocation("geckolib", "animations/brown.json");
	}
}