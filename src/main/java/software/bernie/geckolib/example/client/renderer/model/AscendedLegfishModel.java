/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.geckolib.example.client.renderer.model;

import net.minecraft.util.Identifier;
import software.bernie.geckolib.animation.model.AnimatedEntityModel;
import software.bernie.geckolib.animation.model.AnimatedModelRenderer;
import software.bernie.geckolib.example.entity.AscendedLegfishEntity;

// Made with Blockbench 3.5.2
// Exported for Minecraft version 1.15.2 for entity models animated with GeckoLib
// Paste this class into your mod and follow the documentation for GeckoLib to use animations.
// Blockbench plugin created by Gecko
public class AscendedLegfishModel extends AnimatedEntityModel<AscendedLegfishEntity>
{

	private final AnimatedModelRenderer head;
	private final AnimatedModelRenderer larm1;
	private final AnimatedModelRenderer larm2;
	private final AnimatedModelRenderer lhand;
	private final AnimatedModelRenderer rleg1;
	private final AnimatedModelRenderer rleg2;
	private final AnimatedModelRenderer rankle;
	private final AnimatedModelRenderer rfoot;
	private final AnimatedModelRenderer rkneecap;
	private final AnimatedModelRenderer rarm1;
	private final AnimatedModelRenderer rarm2;
	private final AnimatedModelRenderer rhand;
	private final AnimatedModelRenderer lleg1;
	private final AnimatedModelRenderer lleg2;
	private final AnimatedModelRenderer lankle;
	private final AnimatedModelRenderer lfoot;
	private final AnimatedModelRenderer lkneecap;
	private final AnimatedModelRenderer fin;
	private final AnimatedModelRenderer body;

	public AscendedLegfishModel()
	{
		textureWidth = 64;
		textureHeight = 64;
		head = new AnimatedModelRenderer(this);
		head.setPivot(0.0F, -17.5F, 0.0F);
		head.setTextureOffset(0, 0).addCuboid(-4.0F, -4.5F, -7.0F, 8.0F, 5.0F, 14.0F, 0.0F, false);
		head.setModelRendererName("head");
		this.registerModelRenderer(head);

		larm1 = new AnimatedModelRenderer(this);
		larm1.setPivot(8.0F, 0.5F, 0.0F);
		head.addChild(larm1);
		setRotationAngle(larm1, 0.0F, 0.0F, -0.3491F);
		larm1.setTextureOffset(0, 49).addCuboid(-3.0F, -0.5F, -2.5F, 6.0F, 10.0F, 5.0F, 0.0F, false);
		larm1.setModelRendererName("larm1");
		this.registerModelRenderer(larm1);

		larm2 = new AnimatedModelRenderer(this);
		larm2.setPivot(0.0F, 9.0F, 0.0F);
		larm1.addChild(larm2);
		larm2.setTextureOffset(0, 35).addCuboid(-2.5F, 0.0912F, -2.0F, 5.0F, 10.0F, 4.0F, 0.0F, false);
		larm2.setModelRendererName("larm2");
		this.registerModelRenderer(larm2);

		lhand = new AnimatedModelRenderer(this);
		lhand.setPivot(0.0F, 10.0F, 0.5F);
		larm2.addChild(lhand);
		lhand.setTextureOffset(24, 19).addCuboid(-3.5F, 0.0912F, -4.0F, 7.0F, 7.0F, 7.0F, 0.0F, false);
		lhand.setModelRendererName("lhand");
		this.registerModelRenderer(lhand);

		rleg1 = new AnimatedModelRenderer(this);
		rleg1.setPivot(-4.0F, 16.5F, 0.0F);
		head.addChild(rleg1);
		rleg1.setTextureOffset(0, 49).addCuboid(-3.0F, 0.0F, -2.5F, 6.0F, 10.0F, 5.0F, 0.0F, false);
		rleg1.setModelRendererName("rleg1");
		this.registerModelRenderer(rleg1);

		rleg2 = new AnimatedModelRenderer(this);
		rleg2.setPivot(0.0F, 10.0F, -0.5F);
		rleg1.addChild(rleg2);
		rleg2.setTextureOffset(0, 35).addCuboid(-2.5F, 0.0F, -2.0F, 5.0F, 10.0F, 4.0F, 0.0F, false);
		rleg2.setModelRendererName("rleg2");
		this.registerModelRenderer(rleg2);

		rankle = new AnimatedModelRenderer(this);
		rankle.setPivot(0.0F, 10.0F, 0.0F);
		rleg2.addChild(rankle);
		rankle.setTextureOffset(0, 29).addCuboid(-2.0F, 0.0F, -1.5F, 4.0F, 3.0F, 3.0F, 0.0F, false);
		rankle.setModelRendererName("rankle");
		this.registerModelRenderer(rankle);

		rfoot = new AnimatedModelRenderer(this);
		rfoot.setPivot(0.0F, 3.0F, 0.5F);
		rankle.addChild(rfoot);
		rfoot.setTextureOffset(0, 20).addCuboid(-2.5F, 0.0F, -5.0F, 5.0F, 2.0F, 7.0F, 0.0F, false);
		rfoot.setModelRendererName("rfoot");
		this.registerModelRenderer(rfoot);

		rkneecap = new AnimatedModelRenderer(this);
		rkneecap.setPivot(0.0F, 8.0F, -2.5F);
		rleg1.addChild(rkneecap);
		rkneecap.setTextureOffset(0, 0).addCuboid(-1.0F, 0.0F, -1.0F, 2.0F, 3.0F, 1.0F, 0.0F, false);
		rkneecap.setModelRendererName("rkneecap");
		this.registerModelRenderer(rkneecap);

		rarm1 = new AnimatedModelRenderer(this);
		rarm1.setPivot(-8.0F, 0.5F, 0.0F);
		head.addChild(rarm1);
		setRotationAngle(rarm1, 0.0F, 0.0F, 0.3491F);
		rarm1.setTextureOffset(0, 49).addCuboid(-3.0F, -0.5F, -2.5F, 6.0F, 10.0F, 5.0F, 0.0F, false);
		rarm1.setModelRendererName("rarm1");
		this.registerModelRenderer(rarm1);

		rarm2 = new AnimatedModelRenderer(this);
		rarm2.setPivot(0.0F, 9.0F, 0.0F);
		rarm1.addChild(rarm2);
		rarm2.setTextureOffset(0, 35).addCuboid(-2.5F, 0.0F, -2.0F, 5.0F, 10.0F, 4.0F, 0.0F, false);
		rarm2.setModelRendererName("rarm2");
		this.registerModelRenderer(rarm2);

		rhand = new AnimatedModelRenderer(this);
		rhand.setPivot(0.0F, 10.0F, -0.5F);
		rarm2.addChild(rhand);
		rhand.setTextureOffset(24, 33).addCuboid(-3.5F, 0.0F, -3.0F, 7.0F, 7.0F, 7.0F, 0.0F, false);
		rhand.setModelRendererName("rhand");
		this.registerModelRenderer(rhand);

		lleg1 = new AnimatedModelRenderer(this);
		lleg1.setPivot(4.0F, 16.5F, 0.0F);
		head.addChild(lleg1);
		lleg1.setTextureOffset(0, 49).addCuboid(-3.0F, 0.0F, -2.5F, 6.0F, 10.0F, 5.0F, 0.0F, false);
		lleg1.setModelRendererName("lleg1");
		this.registerModelRenderer(lleg1);

		lleg2 = new AnimatedModelRenderer(this);
		lleg2.setPivot(0.0F, 10.5F, -0.5F);
		lleg1.addChild(lleg2);
		lleg2.setTextureOffset(0, 35).addCuboid(-2.5F, -0.5F, -2.0F, 5.0F, 10.0F, 4.0F, 0.0F, false);
		lleg2.setModelRendererName("lleg2");
		this.registerModelRenderer(lleg2);

		lankle = new AnimatedModelRenderer(this);
		lankle.setPivot(0.0F, 9.5F, 0.0F);
		lleg2.addChild(lankle);
		lankle.setTextureOffset(0, 29).addCuboid(-2.0F, 0.0F, -1.5F, 4.0F, 3.0F, 3.0F, 0.0F, false);
		lankle.setModelRendererName("lankle");
		this.registerModelRenderer(lankle);

		lfoot = new AnimatedModelRenderer(this);
		lfoot.setPivot(0.0F, 3.0F, 0.5F);
		lankle.addChild(lfoot);
		lfoot.setTextureOffset(0, 20).addCuboid(-2.5F, 0.0F, -5.0F, 5.0F, 2.0F, 7.0F, 0.0F, false);
		lfoot.setModelRendererName("lfoot");
		this.registerModelRenderer(lfoot);

		lkneecap = new AnimatedModelRenderer(this);
		lkneecap.setPivot(0.0F, 8.0F, -2.5F);
		lleg1.addChild(lkneecap);
		lkneecap.setTextureOffset(0, 0).addCuboid(-1.0F, 0.0F, -1.0F, 2.0F, 3.0F, 1.0F, 0.0F, false);
		lkneecap.setModelRendererName("lkneecap");
		this.registerModelRenderer(lkneecap);

		fin = new AnimatedModelRenderer(this);
		fin.setPivot(0.0F, -6.0F, 0.0F);
		head.addChild(fin);
		fin.setTextureOffset(35, 44).addCuboid(0.0F, -2.5F, -5.0F, 0.0F, 4.0F, 13.0F, 0.0F, false);
		fin.setModelRendererName("fin");
		this.registerModelRenderer(fin);

		body = new AnimatedModelRenderer(this);
		body.setPivot(0.0F, 8.0F, 0.0F);
		head.addChild(body);
		setRotationAngle(body, 0.0F, 0.0F, 1.5708F);
		body.setTextureOffset(30, 0).addCuboid(-5.5F, -4.5F, -2.5F, 12.0F, 9.0F, 5.0F, 2.0F, false);
		body.setModelRendererName("body");
		this.registerModelRenderer(body);
		this.rootBones.add(head);

	}

	@Override
	public Identifier getAnimationFileLocation()
	{
		return new Identifier("geckolib:" + "animations/ascended_leg_fish.json");
	}
}