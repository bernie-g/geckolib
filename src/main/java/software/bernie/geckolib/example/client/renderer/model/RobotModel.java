// Made with Blockbench 3.6.3
// Exported for Minecraft version 1.12.2 or 1.15.2 (same format for both) for entity models animated with GeckoLib
// Paste this class into your mod and follow the documentation for GeckoLib to use animations. You can find the documentation here: https://github.com/bernie-g/geckolib
// Blockbench plugin created by Gecko
package software.bernie.geckolib.example.client.renderer.model;

import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib.animation.model.AnimatedEntityModel;
import software.bernie.geckolib.animation.render.AnimatedModelRenderer;
import software.bernie.geckolib.example.entity.RobotEntity;

public class RobotModel extends AnimatedEntityModel<RobotEntity> {

	private final AnimatedModelRenderer body;
	private final AnimatedModelRenderer key;
	private final AnimatedModelRenderer lleg;
	private final AnimatedModelRenderer rleg;
	private final AnimatedModelRenderer larm;
	private final AnimatedModelRenderer rarm;
	private final AnimatedModelRenderer head;

	public RobotModel()
	{
		textureWidth = 64;
		textureHeight = 64;
		body = new AnimatedModelRenderer(this);
		body.setRotationPoint(0.0F, 12.0F, 0.0F);
		body.setTextureOffset(20, 20).addBox(-4.0F, -6.0F, -2.0F, 8.0F, 12.0F, 4.0F, 0.0F, false);
		body.setModelRendererName("body");
		this.registerModelRenderer(body);

		key = new AnimatedModelRenderer(this);
		key.setRotationPoint(0.0F, -0.5F, 2.0F);
		body.addChild(key);
		key.setTextureOffset(0, 32).addBox(0.0F, -5.5F, 0.0F, 0.0F, 11.0F, 11.0F, 0.0F, false);
		key.setModelRendererName("key");
		this.registerModelRenderer(key);

		lleg = new AnimatedModelRenderer(this);
		lleg.setRotationPoint(2.0F, 6.0F, 0.0F);
		body.addChild(lleg);
		lleg.setTextureOffset(24, 36).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 5.0F, 2.0F, 0.0F, false);
		lleg.setTextureOffset(29, 0).addBox(-1.5F, 5.0F, -3.5F, 3.0F, 1.0F, 5.0F, 0.0F, false);
		lleg.setModelRendererName("lleg");
		this.registerModelRenderer(lleg);

		rleg = new AnimatedModelRenderer(this);
		rleg.setRotationPoint(-2.0F, 6.0F, 0.0F);
		body.addChild(rleg);
		rleg.setTextureOffset(16, 36).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 5.0F, 2.0F, 0.0F, false);
		rleg.setTextureOffset(0, 26).addBox(-1.5F, 5.0F, -3.5F, 3.0F, 1.0F, 5.0F, 0.0F, false);
		rleg.setModelRendererName("rleg");
		this.registerModelRenderer(rleg);

		larm = new AnimatedModelRenderer(this);
		larm.setRotationPoint(4.0F, -4.0F, 0.0F);
		body.addChild(larm);
		larm.setTextureOffset(8, 32).addBox(0.0F, -1.0F, -1.0F, 2.0F, 8.0F, 2.0F, 0.0F, false);
		larm.setTextureOffset(36, 6).addBox(0.5F, 7.0F, -1.5F, 1.0F, 2.0F, 3.0F, 0.0F, false);
		larm.setModelRendererName("larm");
		this.registerModelRenderer(larm);

		rarm = new AnimatedModelRenderer(this);
		rarm.setRotationPoint(-4.0F, -4.0F, 0.0F);
		body.addChild(rarm);
		rarm.setTextureOffset(0, 32).addBox(-2.0F, -1.0F, -1.0F, 2.0F, 8.0F, 2.0F, 0.0F, false);
		rarm.setTextureOffset(11, 26).addBox(-1.5F, 7.0F, -1.5F, 1.0F, 2.0F, 3.0F, 0.0F, false);
		rarm.setModelRendererName("rarm");
		this.registerModelRenderer(rarm);

		head = new AnimatedModelRenderer(this);
		head.setRotationPoint(0.0F, -6.0F, 0.0F);
		body.addChild(head);
		head.setTextureOffset(0, 0).addBox(-5.5F, -9.0F, -3.5F, 11.0F, 9.0F, 7.0F, 0.0F, false);
		head.setTextureOffset(32, 36).addBox(5.5F, -6.0F, -1.5F, 1.0F, 3.0F, 3.0F, 0.0F, false);
		head.setTextureOffset(33, 13).addBox(-6.5F, -6.0F, -1.5F, 1.0F, 3.0F, 3.0F, 0.0F, false);
		head.setModelRendererName("head");
		this.registerModelRenderer(head);

		this.rootBones.add(body);
	}


	@Override
	public ResourceLocation getAnimationFileLocation()
	{
		return new ResourceLocation("geckolib", "animations/robot.json");
	}
}