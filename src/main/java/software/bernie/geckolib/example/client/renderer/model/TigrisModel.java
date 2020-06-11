/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.geckolib.example.client.renderer.model;

import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib.animation.model.AnimatedEntityModel;
import software.bernie.geckolib.animation.model.AnimatedModelRenderer;
import software.bernie.geckolib.example.entity.TigrisEntity;

// Made with Blockbench 3.5.2
// Exported for Minecraft version 1.15.2 for entity models animated with GeckoLib
// Paste this class into your mod and follow the documentation for GeckoLib to use animations.
// Blockbench plugin created by Gecko
public class TigrisModel extends AnimatedEntityModel<TigrisEntity>
{
	private final AnimatedModelRenderer body;
	private final AnimatedModelRenderer leg;
	private final AnimatedModelRenderer knee3;
	private final AnimatedModelRenderer foot_whole;
	private final AnimatedModelRenderer leg2;
	private final AnimatedModelRenderer knee4;
	private final AnimatedModelRenderer foot_whole2;
	private final AnimatedModelRenderer wing_l;
	private final AnimatedModelRenderer wing2;
	private final AnimatedModelRenderer wing_r;
	private final AnimatedModelRenderer wing3;
	private final AnimatedModelRenderer tail;
	private final AnimatedModelRenderer tail2;
	private final AnimatedModelRenderer tail3;
	private final AnimatedModelRenderer neck;
	private final AnimatedModelRenderer head;
	private final AnimatedModelRenderer mouth;
	private final AnimatedModelRenderer ear3;
	private final AnimatedModelRenderer ear2;
	private final AnimatedModelRenderer leg_back;
	private final AnimatedModelRenderer knee;
	private final AnimatedModelRenderer foot_whole3;
	private final AnimatedModelRenderer leg_back2;
	private final AnimatedModelRenderer knee2;
	private final AnimatedModelRenderer foot_whole4;

	public TigrisModel()
	{
		textureWidth = 512;
		textureHeight = 512;
		body = new AnimatedModelRenderer(this);
		body.setRotationPoint(0.0F, -5.7505F, 1.7985F);
		body.setTextureOffset(78, 182).addBox(-6.5F, -6.2495F, -18.7985F, 13.0F, 17.0F, 21.0F, 0.0F, false);
		body.setTextureOffset(216, 182).addBox(-5.5F, 2.7505F, -19.7985F, 11.0F, 14.0F, 23.0F, 0.0F, false);
		body.setTextureOffset(120, 129).addBox(-5.25F, -10.1005F, -11.0299F, 10.0F, 19.0F, 34.0F, 0.0F, false);
		body.setTextureOffset(146, 182).addBox(0.0F, -13.1005F, -11.0299F, 0.0F, 3.0F, 34.0F, 0.0F, false);
		body.setTextureOffset(0, 182).addBox(-8.5F, -5.0835F, 1.0995F, 17.0F, 17.0F, 22.0F, 0.0F, false);
		body.setModelRendererName("body");
		this.registerModelRenderer(body);

		leg = new AnimatedModelRenderer(this);
		leg.setRotationPoint(6.0F, 3.7505F, -7.2985F);
		body.addChild(leg);
		leg.setTextureOffset(210, 259).addBox(-1.0F, -5.0F, -7.5F, 10.0F, 12.0F, 13.0F, 0.0F, false);
		leg.setModelRendererName("leg");
		this.registerModelRenderer(leg);

		knee3 = new AnimatedModelRenderer(this);
		knee3.setRotationPoint(6.3029F, 4.2284F, -0.3172F);
		leg.addChild(knee3);
		knee3.setTextureOffset(250, 284).addBox(4.905F, -1.7861F, -9.1828F, 8.0F, 11.0F, 11.0F, 0.0F, false);
		knee3.setTextureOffset(30, 328).addBox(3.624F, 7.4741F, -8.1828F, 7.0F, 10.0F, 8.0F, 0.0F, false);
		knee3.setTextureOffset(18, 346).addBox(7.124F, 7.4741F, -0.1828F, 0.0F, 7.0F, 8.0F, 0.0F, false);
		knee3.setModelRendererName("knee3");
		this.registerModelRenderer(knee3);

		foot_whole = new AnimatedModelRenderer(this);
		foot_whole.setRotationPoint(6.6971F, 14.8312F, -1.7634F);
		knee3.addChild(foot_whole);
		foot_whole.setTextureOffset(40, 308).addBox(-4.5F, 0.6161F, -7.3644F, 9.0F, 5.0F, 9.0F, 0.0F, false);
		foot_whole.setTextureOffset(156, 346).addBox(2.5F, 2.6161F, -11.3644F, 3.0F, 4.0F, 7.0F, 0.0F, false);
		foot_whole.setTextureOffset(48, 361).addBox(-1.5F, 2.6161F, -11.3644F, 3.0F, 4.0F, 5.0F, 0.0F, false);
		foot_whole.setTextureOffset(136, 346).addBox(-5.5F, 2.6161F, -11.3644F, 3.0F, 4.0F, 7.0F, 0.0F, false);
		foot_whole.setTextureOffset(228, 361).addBox(4.0F, 2.6161F, -15.3644F, 0.0F, 4.0F, 4.0F, 0.0F, false);
		foot_whole.setTextureOffset(218, 361).addBox(0.0F, 2.6161F, -15.3644F, 0.0F, 4.0F, 4.0F, 0.0F, false);
		foot_whole.setTextureOffset(208, 361).addBox(-4.0F, 2.6161F, -15.3644F, 0.0F, 4.0F, 4.0F, 0.0F, false);
		foot_whole.setModelRendererName("foot_whole");
		this.registerModelRenderer(foot_whole);

		leg2 = new AnimatedModelRenderer(this);
		leg2.setRotationPoint(-6.0F, 3.7505F, -7.2985F);
		body.addChild(leg2);
		leg2.setTextureOffset(96, 259).addBox(-9.0F, -5.0F, -7.5F, 10.0F, 12.0F, 13.0F, 0.0F, false);
		leg2.setModelRendererName("leg2");
		this.registerModelRenderer(leg2);

		knee4 = new AnimatedModelRenderer(this);
		knee4.setRotationPoint(-6.3029F, 4.2284F, -0.3172F);
		leg2.addChild(knee4);
		knee4.setTextureOffset(126, 284).addBox(-12.905F, -1.7861F, -9.1828F, 8.0F, 11.0F, 11.0F, 0.0F, false);
		knee4.setTextureOffset(0, 328).addBox(-10.624F, 7.4741F, -8.1828F, 7.0F, 10.0F, 8.0F, 0.0F, false);
		knee4.setTextureOffset(0, 346).addBox(-7.124F, 7.4741F, -0.1828F, 0.0F, 7.0F, 8.0F, 0.0F, false);
		knee4.setModelRendererName("knee4");
		this.registerModelRenderer(knee4);

		foot_whole2 = new AnimatedModelRenderer(this);
		foot_whole2.setRotationPoint(-6.6971F, 14.8312F, -1.7634F);
		knee4.addChild(foot_whole2);
		foot_whole2.setTextureOffset(76, 308).addBox(-4.5F, 0.6161F, -7.3644F, 9.0F, 5.0F, 9.0F, 0.0F, false);
		foot_whole2.setTextureOffset(204, 328).addBox(-5.5F, 2.6161F, -11.3644F, 3.0F, 4.0F, 7.0F, 0.0F, false);
		foot_whole2.setTextureOffset(0, 361).addBox(-1.5F, 2.6161F, -11.3644F, 3.0F, 4.0F, 5.0F, 0.0F, false);
		foot_whole2.setTextureOffset(184, 328).addBox(2.5F, 2.6161F, -11.3644F, 3.0F, 4.0F, 7.0F, 0.0F, false);
		foot_whole2.setTextureOffset(138, 361).addBox(-4.0F, 2.6161F, -15.3644F, 0.0F, 4.0F, 4.0F, 0.0F, false);
		foot_whole2.setTextureOffset(128, 361).addBox(0.0F, 2.6161F, -15.3644F, 0.0F, 4.0F, 4.0F, 0.0F, false);
		foot_whole2.setTextureOffset(118, 361).addBox(4.0F, 2.6161F, -15.3644F, 0.0F, 4.0F, 4.0F, 0.0F, false);
		foot_whole2.setModelRendererName("foot_whole2");
		this.registerModelRenderer(foot_whole2);

		wing_l = new AnimatedModelRenderer(this);
		wing_l.setRotationPoint(4.0F, -7.7495F, -9.7985F);
		body.addChild(wing_l);
		wing_l.setTextureOffset(0, 129).addBox(1.177F, 0.2636F, -24.0F, 18.0F, 0.0F, 42.0F, 0.0F, false);
		wing_l.setTextureOffset(240, 86).addBox(1.177F, 0.5136F, -24.0F, 18.0F, 0.0F, 42.0F, 0.0F, false);
		wing_l.setTextureOffset(102, 328).addBox(-1.0F, -0.634F, -2.0F, 19.0F, 2.0F, 2.0F, 0.0F, false);
		wing_l.setTextureOffset(198, 357).addBox(12.1632F, -0.384F, -13.9924F, 11.0F, 1.0F, 2.0F, 0.0F, false);
		wing_l.setTextureOffset(18, 371).addBox(17.3837F, -0.134F, -21.2037F, 2.0F, 1.0F, 2.0F, 0.0F, false);
		wing_l.setModelRendererName("wing_l");
		this.registerModelRenderer(wing_l);

		wing2 = new AnimatedModelRenderer(this);
		wing2.setRotationPoint(19.0425F, 0.9063F, -9.0385F);
		wing_l.addChild(wing2);
		wing2.setTextureOffset(158, 43).addBox(0.1345F, -0.6427F, -14.9615F, 37.0F, 0.0F, 42.0F, 0.0F, false);
		wing2.setTextureOffset(0, 43).addBox(0.1345F, -0.3927F, -14.9615F, 37.0F, 0.0F, 42.0F, 0.0F, false);
		wing2.setTextureOffset(142, 262).addBox(0.9849F, -1.1427F, -8.5248F, 32.0F, 1.0F, 2.0F, 0.0F, false);
		wing2.setTextureOffset(0, 371).addBox(31.1651F, -1.3927F, -2.3442F, 3.0F, 1.0F, 2.0F, 0.0F, false);
		wing2.setModelRendererName("wing2");
		this.registerModelRenderer(wing2);

		wing_r = new AnimatedModelRenderer(this);
		wing_r.setRotationPoint(-4.0F, -7.7495F, -9.7985F);
		body.addChild(wing_r);
		wing_r.setTextureOffset(0, 129).addBox(-19.177F, 0.2636F, -24.0F, 18.0F, 0.0F, 42.0F, 0.0F, true);
		wing_r.setTextureOffset(240, 86).addBox(-19.177F, 0.5136F, -24.0F, 18.0F, 0.0F, 42.0F, 0.0F, true);
		wing_r.setTextureOffset(102, 328).addBox(-18.0F, -0.634F, -2.0F, 19.0F, 2.0F, 2.0F, 0.0F, true);
		wing_r.setTextureOffset(198, 357).addBox(-23.1632F, -0.384F, -13.9924F, 11.0F, 1.0F, 2.0F, 0.0F, true);
		wing_r.setTextureOffset(18, 371).addBox(-19.3837F, -0.134F, -21.2037F, 2.0F, 1.0F, 2.0F, 0.0F, true);
		wing_r.setModelRendererName("wing_r");
		this.registerModelRenderer(wing_r);

		wing3 = new AnimatedModelRenderer(this);
		wing3.setRotationPoint(-19.0425F, 0.9063F, -9.0385F);
		wing_r.addChild(wing3);
		wing3.setTextureOffset(158, 43).addBox(-37.1345F, -0.6427F, -14.9615F, 37.0F, 0.0F, 42.0F, 0.0F, true);
		wing3.setTextureOffset(0, 43).addBox(-37.1345F, -0.3927F, -14.9615F, 37.0F, 0.0F, 42.0F, 0.0F, true);
		wing3.setTextureOffset(142, 262).addBox(-32.9849F, -1.1427F, -8.5248F, 32.0F, 1.0F, 2.0F, 0.0F, true);
		wing3.setTextureOffset(0, 371).addBox(-34.1651F, -1.3927F, -2.3442F, 3.0F, 1.0F, 2.0F, 0.0F, true);
		wing3.setModelRendererName("wing3");
		this.registerModelRenderer(wing3);

		tail = new AnimatedModelRenderer(this);
		tail.setRotationPoint(0.0F, 2.2778F, 23.2168F);
		body.addChild(tail);
		tail.setTextureOffset(0, 221).addBox(-7.5F, -8.3613F, -3.1173F, 15.0F, 16.0F, 19.0F, 0.0F, false);
		tail.setTextureOffset(126, 221).addBox(-5.5F, -6.3613F, 15.8827F, 11.0F, 13.0F, 16.0F, 0.0F, false);
		tail.setModelRendererName("tail");
		this.registerModelRenderer(tail);

		tail2 = new AnimatedModelRenderer(this);
		tail2.setRotationPoint(0.0F, 0.2387F, 31.8827F);
		tail.addChild(tail2);
		tail2.setTextureOffset(0, 284).addBox(-4.5F, -4.6F, 0.0F, 9.0F, 10.0F, 14.0F, 0.0F, false);
		tail2.setModelRendererName("tail2");
		this.registerModelRenderer(tail2);

		tail3 = new AnimatedModelRenderer(this);
		tail3.setRotationPoint(0.0F, -0.1F, 14.0F);
		tail2.addChild(tail3);
		tail3.setTextureOffset(210, 284).addBox(-4.0F, -3.5F, 0.0F, 8.0F, 8.0F, 12.0F, 0.0F, false);
		tail3.setTextureOffset(148, 308).addBox(-3.0F, -1.5F, 12.0F, 6.0F, 6.0F, 11.0F, 0.0F, false);
		tail3.setTextureOffset(180, 221).addBox(0.0F, -4.5F, 20.0F, 0.0F, 18.0F, 20.0F, 0.0F, false);
		tail3.setModelRendererName("tail3");
		this.registerModelRenderer(tail3);

		neck = new AnimatedModelRenderer(this);
		neck.setRotationPoint(0.0F, 0.0393F, -17.1064F);
		body.addChild(neck);
		setRotationAngle(neck, -0.1745F, 0.0F, 0.0F);
		neck.setTextureOffset(208, 129).addBox(-5.5F, -7.1228F, -18.7941F, 11.0F, 11.0F, 28.0F, 0.0F, false);
		neck.setTextureOffset(68, 221).addBox(0.0F, -10.1228F, -18.7941F, 0.0F, 3.0F, 28.0F, 0.0F, false);
		neck.setTextureOffset(222, 221).addBox(-3.5F, 2.0F, -14.0F, 7.0F, 6.0F, 18.0F, 0.0F, false);
		neck.setModelRendererName("neck");
		this.registerModelRenderer(neck);

		head = new AnimatedModelRenderer(this);
		head.setRotationPoint(0.1782F, 3.6325F, -18.3621F);
		neck.addChild(head);
		head.setTextureOffset(164, 284).addBox(-7.1782F, 1.0787F, -8.33F, 14.0F, 5.0F, 9.0F, 0.0F, false);
		head.setTextureOffset(0, 308).addBox(-5.1782F, -2.9213F, -9.33F, 10.0F, 8.0F, 10.0F, 0.0F, false);
		head.setTextureOffset(76, 346).addBox(-0.1782F, -6.1947F, -11.6076F, 0.0F, 4.0F, 9.0F, 0.0F, false);
		head.setTextureOffset(198, 346).addBox(0.8218F, -3.4725F, -11.1414F, 5.0F, 2.0F, 6.0F, 0.0F, false);
		head.setTextureOffset(176, 346).addBox(-6.1782F, -3.4725F, -11.1414F, 5.0F, 2.0F, 6.0F, 0.0F, false);
		head.setTextureOffset(60, 328).addBox(-4.9282F, 4.0318F, -14.3687F, 9.0F, 5.0F, 8.0F, 0.0F, false);
		head.setTextureOffset(34, 371).addBox(-4.9282F, 3.0318F, -14.3687F, 2.0F, 1.0F, 2.0F, 0.0F, false);
		head.setTextureOffset(26, 371).addBox(2.0718F, 3.0318F, -14.3687F, 2.0F, 1.0F, 2.0F, 0.0F, false);
		head.setModelRendererName("head");
		this.registerModelRenderer(head);

		mouth = new AnimatedModelRenderer(this);
		mouth.setRotationPoint(-0.1782F, 7.5701F, -5.4308F);
		head.addChild(mouth);
		mouth.setTextureOffset(102, 332).addBox(-4.0F, -1.25F, -6.5F, 8.0F, 2.0F, 8.0F, 0.0F, false);
		mouth.setTextureOffset(108, 361).addBox(0.0F, 0.3371F, -3.8802F, 0.0F, 5.0F, 4.0F, 0.0F, false);
		mouth.setTextureOffset(92, 361).addBox(-2.0F, -0.2676F, -5.9319F, 4.0F, 2.0F, 4.0F, 0.0F, false);
		mouth.setModelRendererName("mouth");
		this.registerModelRenderer(mouth);

		ear3 = new AnimatedModelRenderer(this);
		ear3.setRotationPoint(3.8693F, 0.214F, -3.4875F);
		head.addChild(ear3);
		setRotationAngle(ear3, 0.0F, 0.6981F, 0.0873F);
		ear3.setTextureOffset(78, 361).addBox(0.0142F, -6.6632F, -1.9726F, 2.0F, 5.0F, 5.0F, 0.0F, false);
		ear3.setTextureOffset(164, 328).addBox(1.0142F, -8.6632F, 0.0274F, 0.0F, 9.0F, 9.0F, 0.0F, false);
		ear3.setModelRendererName("ear3");
		this.registerModelRenderer(ear3);

		ear2 = new AnimatedModelRenderer(this);
		ear2.setRotationPoint(-4.2257F, 0.214F, -3.4875F);
		head.addChild(ear2);
		setRotationAngle(ear2, 0.0F, -0.6981F, -0.0873F);
		ear2.setTextureOffset(64, 361).addBox(-2.0142F, -6.6632F, -1.9726F, 2.0F, 5.0F, 5.0F, 0.0F, false);
		ear2.setTextureOffset(144, 328).addBox(-1.0142F, -8.6632F, 0.0274F, 0.0F, 9.0F, 9.0F, 0.0F, false);
		ear2.setModelRendererName("ear2");
		this.registerModelRenderer(ear2);

		leg_back = new AnimatedModelRenderer(this);
		leg_back.setRotationPoint(7.9774F, 3.657F, 22.19F);
		body.addChild(leg_back);
		setRotationAngle(leg_back, -0.2618F, 0.0F, 0.0F);
		leg_back.setTextureOffset(48, 259).addBox(-6.5983F, -4.4165F, -5.0231F, 11.0F, 12.0F, 13.0F, 0.0F, false);
		leg_back.setModelRendererName("leg_back");
		this.registerModelRenderer(leg_back);

		knee = new AnimatedModelRenderer(this);
		knee.setRotationPoint(3.3499F, 5.5056F, 0.5702F);
		leg_back.addChild(knee);
		knee.setTextureOffset(86, 284).addBox(-4.6415F, -3.2868F, -3.3893F, 9.0F, 10.0F, 11.0F, 0.0F, false);
		knee.setTextureOffset(248, 308).addBox(0.4144F, 4.7058F, 6.9254F, 7.0F, 12.0F, 8.0F, 0.0F, false);
		knee.setModelRendererName("knee");
		this.registerModelRenderer(knee);

		foot_whole3 = new AnimatedModelRenderer(this);
		foot_whole3.setRotationPoint(3.1727F, 8.7581F, 6.1464F);
		knee.addChild(foot_whole3);
		foot_whole3.setTextureOffset(182, 308).addBox(-4.5F, 3.7189F, 0.2125F, 9.0F, 5.0F, 9.0F, 0.0F, false);
		foot_whole3.setTextureOffset(116, 346).addBox(2.5F, 5.6464F, -3.7593F, 3.0F, 4.0F, 7.0F, 0.0F, false);
		foot_whole3.setTextureOffset(198, 361).addBox(4.0F, 5.6464F, -7.7593F, 0.0F, 4.0F, 4.0F, 0.0F, false);
		foot_whole3.setTextureOffset(188, 361).addBox(-4.0F, 5.6464F, -7.7593F, 0.0F, 4.0F, 4.0F, 0.0F, false);
		foot_whole3.setTextureOffset(178, 361).addBox(0.0F, 5.6464F, -7.7593F, 0.0F, 4.0F, 4.0F, 0.0F, false);
		foot_whole3.setTextureOffset(96, 346).addBox(-5.5F, 5.6464F, -3.7593F, 3.0F, 4.0F, 7.0F, 0.0F, false);
		foot_whole3.setTextureOffset(32, 361).addBox(-1.5F, 5.6464F, -3.7593F, 3.0F, 4.0F, 5.0F, 0.0F, false);
		foot_whole3.setModelRendererName("foot_whole3");
		this.registerModelRenderer(foot_whole3);

		leg_back2 = new AnimatedModelRenderer(this);
		leg_back2.setRotationPoint(-7.9774F, 3.657F, 22.19F);
		body.addChild(leg_back2);
		setRotationAngle(leg_back2, -0.2618F, 0.0F, 0.0F);
		leg_back2.setTextureOffset(48, 259).addBox(-4.4017F, -4.4165F, -5.0231F, 11.0F, 12.0F, 13.0F, 0.0F, true);
		leg_back2.setModelRendererName("leg_back2");
		this.registerModelRenderer(leg_back2);

		knee2 = new AnimatedModelRenderer(this);
		knee2.setRotationPoint(-3.3499F, 5.5056F, 0.5702F);
		leg_back2.addChild(knee2);
		knee2.setTextureOffset(86, 284).addBox(-4.3585F, -3.2868F, -3.3893F, 9.0F, 10.0F, 11.0F, 0.0F, true);
		knee2.setTextureOffset(248, 308).addBox(-7.4144F, 4.7058F, 6.9254F, 7.0F, 12.0F, 8.0F, 0.0F, true);
		knee2.setModelRendererName("knee2");
		this.registerModelRenderer(knee2);

		foot_whole4 = new AnimatedModelRenderer(this);
		foot_whole4.setRotationPoint(-3.1727F, 8.7581F, 6.1464F);
		knee2.addChild(foot_whole4);
		foot_whole4.setTextureOffset(182, 308).addBox(-4.5F, 3.7189F, 0.2125F, 9.0F, 5.0F, 9.0F, 0.0F, true);
		foot_whole4.setTextureOffset(116, 346).addBox(-5.5F, 5.6464F, -3.7593F, 3.0F, 4.0F, 7.0F, 0.0F, true);
		foot_whole4.setTextureOffset(198, 361).addBox(-4.0F, 5.6464F, -7.7593F, 0.0F, 4.0F, 4.0F, 0.0F, true);
		foot_whole4.setTextureOffset(188, 361).addBox(4.0F, 5.6464F, -7.7593F, 0.0F, 4.0F, 4.0F, 0.0F, true);
		foot_whole4.setTextureOffset(178, 361).addBox(0.0F, 5.6464F, -7.7593F, 0.0F, 4.0F, 4.0F, 0.0F, true);
		foot_whole4.setTextureOffset(96, 346).addBox(2.5F, 5.6464F, -3.7593F, 3.0F, 4.0F, 7.0F, 0.0F, true);
		foot_whole4.setTextureOffset(32, 361).addBox(-1.5F, 5.6464F, -3.7593F, 3.0F, 4.0F, 5.0F, 0.0F, true);
		foot_whole4.setModelRendererName("foot_whole4");
		this.registerModelRenderer(foot_whole4);

		this.rootBones.add(body);
	}



	@Override
	public ResourceLocation getAnimationFileLocation()
	{
		return new ResourceLocation("geckolib", "animations/tigris_anim.json");
	}
}