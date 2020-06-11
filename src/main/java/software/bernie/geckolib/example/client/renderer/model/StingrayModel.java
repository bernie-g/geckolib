/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

// Made with Blockbench 3.5.0
// Exported for Minecraft version 1.15
// Paste this class into your mod and generate all required imports
package software.bernie.geckolib.example.client.renderer.model;


import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib.example.entity.StingrayTestEntity;
import software.bernie.geckolib.animation.model.AnimatedEntityModel;
import software.bernie.geckolib.animation.model.AnimatedModelRenderer;

public class StingrayModel extends AnimatedEntityModel<StingrayTestEntity>
{
	private final AnimatedModelRenderer Stingray;
	private final AnimatedModelRenderer Body;
	private final AnimatedModelRenderer mouth;
	private final AnimatedModelRenderer Lefthand;
	private final AnimatedModelRenderer smallfinleft;
	private final AnimatedModelRenderer Righthand;
	private final AnimatedModelRenderer smallfinright;
	private final AnimatedModelRenderer Tail;
	private final AnimatedModelRenderer tail1;
	private final AnimatedModelRenderer tail2;
	private final AnimatedModelRenderer tail3;
	private final AnimatedModelRenderer tail4;
	private final AnimatedModelRenderer stinger;
	private final AnimatedModelRenderer bone;
	private final AnimatedModelRenderer AllExcept12;
	private final AnimatedModelRenderer AllExcept123;

	public StingrayModel()
	{
		textureWidth = 128;
		textureHeight = 128;

		Stingray = new AnimatedModelRenderer(this);
		Stingray.setRotationPoint(2.0F, 24.0F, 12.0F);
		Stingray.setTextureOffset(6, 6).addBox(1.0F, -5.0F, -19.0F, 2.0F, 1.0F, 2.0F, 0.0F, false);
		Stingray.setTextureOffset(0, 4).addBox(-7.0F, -5.0F, -19.0F, 2.0F, 1.0F, 2.0F, 0.0F, false);
		Stingray.setModelRendererName("Stingray");

		Body = new AnimatedModelRenderer(this);
		Body.setRotationPoint(-2.0F, 0.0F, -12.0F);
		Stingray.addChild(Body);
		Body.setTextureOffset(70, 41).addBox(-4.0F, -2.0F, -8.0F, 8.0F, 2.0F, 16.0F, 0.0F, false);
		Body.setTextureOffset(0, 36).addBox(-5.0F, -4.0F, -8.0F, 10.0F, 2.0F, 16.0F, 0.0F, false);
		Body.setTextureOffset(50, 18).addBox(-7.0F, -3.0F, -10.0F, 14.0F, 2.0F, 2.0F, 0.0F, false);
		Body.setModelRendererName("Body");

		mouth = new AnimatedModelRenderer(this);
		mouth.setRotationPoint(0.0F, -2.0F, -2.0F);
		Body.addChild(mouth);
		setRotationAngle(mouth, 1.8326F, 0.0F, 0.0F);
		mouth.setTextureOffset(50, 22).addBox(-2.0F, -2.2251F, -2.6648F, 4.0F, 1.0F, 1.0F, 0.0F, false);
		mouth.setModelRendererName("mouth");

		Lefthand = new AnimatedModelRenderer(this);
		Lefthand.setRotationPoint(-4.0F, -0.75F, 0.0F);
		Body.addChild(Lefthand);
		Lefthand.setTextureOffset(4, 18).addBox(-13.0F, -1.25F, -8.0F, 13.0F, 2.0F, 16.0F, 0.0F, false);
		Lefthand.setModelRendererName("Lefthand");

		smallfinleft = new AnimatedModelRenderer(this);
		smallfinleft.setRotationPoint(-12.0F, 0.75F, 0.0F);
		Lefthand.addChild(smallfinleft);
		smallfinleft.setTextureOffset(50, 0).addBox(-5.0F, -2.0F, -6.0F, 4.0F, 2.0F, 12.0F, 0.0F, false);
		smallfinleft.setModelRendererName("smallfinleft");

		Righthand = new AnimatedModelRenderer(this);
		Righthand.setRotationPoint(4.0F, -0.75F, 0.0F);
		Body.addChild(Righthand);
		Righthand.setTextureOffset(4, 0).addBox(0.0F, -1.25F, -8.0F, 13.0F, 2.0F, 16.0F, 0.0F, false);
		Righthand.setModelRendererName("Righthand");

		smallfinright = new AnimatedModelRenderer(this);
		smallfinright.setRotationPoint(13.0F, 0.75F, 0.0F);
		Righthand.addChild(smallfinright);
		smallfinright.setTextureOffset(38, 38).addBox(0.0F, -2.0F, -6.0F, 4.0F, 2.0F, 12.0F, 0.0F, false);
		smallfinright.setModelRendererName("smallfinright");

		Tail = new AnimatedModelRenderer(this);
		Tail.setRotationPoint(-2.0F, -2.0F, -4.0F);
		Stingray.addChild(Tail);
		Tail.setModelRendererName("Tail");

		tail1 = new AnimatedModelRenderer(this);
		tail1.setRotationPoint(1.0F, 2.0F, -1.0F);
		Tail.addChild(tail1);
		tail1.setTextureOffset(20, 54).addBox(-3.0F, -4.0F, 1.0F, 4.0F, 4.0F, 6.0F, 0.0F, false);
		tail1.setModelRendererName("tail1");

		tail2 = new AnimatedModelRenderer(this);
		tail2.setRotationPoint(0.0F, 1.0F, 6.0F);
		Tail.addChild(tail2);
		tail2.setModelRendererName("tail2");

		tail3 = new AnimatedModelRenderer(this);
		tail3.setRotationPoint(0.0F, 0.0F, 12.0F);
		Tail.addChild(tail3);
		tail3.setModelRendererName("tail3");

		tail4 = new AnimatedModelRenderer(this);
		tail4.setRotationPoint(0.0F, 0.0F, 18.0F);
		Tail.addChild(tail4);
		tail4.setModelRendererName("tail4");

		stinger = new AnimatedModelRenderer(this);
		stinger.setRotationPoint(0.0F, 0.5F, 5.5F);
		tail4.addChild(stinger);
		stinger.setModelRendererName("stinger");

		bone = new AnimatedModelRenderer(this);
		bone.setRotationPoint(0.1057F, -0.4766F, -17.75F);
		stinger.addChild(bone);
		bone.setTextureOffset(34, 58).addBox(-2.1057F, -2.0234F, 0.25F, 4.0F, 4.0F, 6.0F, 0.0F, false);
		bone.setModelRendererName("bone");

		AllExcept12 = new AnimatedModelRenderer(this);
		AllExcept12.setRotationPoint(0.067F, 0.0703F, 6.0F);
		bone.addChild(AllExcept12);
		AllExcept12.setTextureOffset(0, 54).addBox(-2.1726F, -2.0937F, 0.25F, 4.0F, 4.0F, 6.0F, 0.0F, false);
		AllExcept12.setModelRendererName("AllExcept12");

		AllExcept123 = new AnimatedModelRenderer(this);
		AllExcept123.setRotationPoint(0.0F, 0.25F, 6.25F);
		AllExcept12.addChild(AllExcept123);
		AllExcept123.setTextureOffset(48, 52).addBox(-2.1726F, -2.3437F, 0.0F, 4.0F, 4.0F, 6.0F, 0.0F, false);
		AllExcept123.setTextureOffset(11, 80).addBox(-0.75F, -1.25F, 6.0F, 2.0F, 2.0F, 6.0F, 0.0F, false);
		AllExcept123.setModelRendererName("AllExcept123");

		this.registerModelRenderer(Stingray);
		this.registerModelRenderer(Body);
		this.registerModelRenderer(mouth);
		this.registerModelRenderer(Lefthand);
		this.registerModelRenderer(smallfinleft);
		this.registerModelRenderer(Righthand);
		this.registerModelRenderer(smallfinright);
		this.registerModelRenderer(Tail);
		this.registerModelRenderer(tail1);
		this.registerModelRenderer(tail2);
		this.registerModelRenderer(tail3);
		this.registerModelRenderer(tail4);
		this.registerModelRenderer(stinger);
		this.registerModelRenderer(bone);
		this.registerModelRenderer(AllExcept12);
		this.registerModelRenderer(AllExcept123);
		this.rootBones.add(Stingray);
	}

	@Override
	public ResourceLocation getAnimationFileLocation()
	{
		return new ResourceLocation("geckolib:animations/stingrayanimation.json");
	}



}