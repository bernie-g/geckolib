// Made with Blockbench 3.6.5
// Exported for Minecraft version 1.12.2 or 1.15.2 (same format for both) for entity models animated with GeckoLib
// Paste this class into your mod and follow the documentation for GeckoLib to use animations. You can find the documentation here: https://github.com/bernie-g/geckolib
// Blockbench plugin created by Gecko
package software.bernie.geckolib.example.client.renderer.model.tile;

import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib.animation.model.AnimatedBlockModel;
import software.bernie.geckolib.animation.render.AnimatedModelRenderer;
import software.bernie.geckolib.example.block.tile.TileEntityJackInTheBox;

public class JackInTheBoxModel extends AnimatedBlockModel<TileEntityJackInTheBox>
{

	private final AnimatedModelRenderer Chest_body;
	private final AnimatedModelRenderer Top;
	private final AnimatedModelRenderer Lock;
	private final AnimatedModelRenderer Botttom;
	private final AnimatedModelRenderer Body_All;
	private final AnimatedModelRenderer Hand2;
	private final AnimatedModelRenderer bone4;
	private final AnimatedModelRenderer bone26;
	private final AnimatedModelRenderer Fingers2;
	private final AnimatedModelRenderer Finger1R;
	private final AnimatedModelRenderer Finger2R;
	private final AnimatedModelRenderer Finger3R;
	private final AnimatedModelRenderer Hand1;
	private final AnimatedModelRenderer bone21;
	private final AnimatedModelRenderer bone25;
	private final AnimatedModelRenderer Fingers1;
	private final AnimatedModelRenderer Finger1L;
	private final AnimatedModelRenderer Finger2L;
	private final AnimatedModelRenderer Finger3L;
	private final AnimatedModelRenderer Body;
	private final AnimatedModelRenderer Spine1;
	private final AnimatedModelRenderer Spine1_1;
	private final AnimatedModelRenderer Spine_parts1;
	private final AnimatedModelRenderer bone8;
	private final AnimatedModelRenderer bone9;
	private final AnimatedModelRenderer bone6;
	private final AnimatedModelRenderer bone7;
	private final AnimatedModelRenderer Spine2;
	private final AnimatedModelRenderer Spine1_2;
	private final AnimatedModelRenderer Spine_parts2;
	private final AnimatedModelRenderer bone5;
	private final AnimatedModelRenderer bone10;
	private final AnimatedModelRenderer bone11;
	private final AnimatedModelRenderer bone12;
	private final AnimatedModelRenderer Spine3;
	private final AnimatedModelRenderer Spine1_3;
	private final AnimatedModelRenderer Spine_parts3;
	private final AnimatedModelRenderer bone13;
	private final AnimatedModelRenderer bone14;
	private final AnimatedModelRenderer bone15;
	private final AnimatedModelRenderer bone16;
	private final AnimatedModelRenderer Spine4;
	private final AnimatedModelRenderer Spine1_4;
	private final AnimatedModelRenderer Spine_parts4;
	private final AnimatedModelRenderer bone17;
	private final AnimatedModelRenderer bone18;
	private final AnimatedModelRenderer bone19;
	private final AnimatedModelRenderer bone20;
	private final AnimatedModelRenderer Head;
	private final AnimatedModelRenderer Horns;
	private final AnimatedModelRenderer Horn1;
	private final AnimatedModelRenderer bone2;
	private final AnimatedModelRenderer Horn2;
	private final AnimatedModelRenderer bone3;
	private final AnimatedModelRenderer bone;
	private final AnimatedModelRenderer NeckBase;

	public JackInTheBoxModel()
	{
		textureWidth = 80;
		textureHeight = 80;
		Chest_body = new AnimatedModelRenderer(this);
		Chest_body.setRotationPoint(0.0F, 25.0F, 0.0F);

		Chest_body.setModelRendererName("Chest_body");
		this.registerModelRenderer(Chest_body);

		Top = new AnimatedModelRenderer(this);
		Top.setRotationPoint(0.5F, -10.0F, 7.5F);
		Chest_body.addChild(Top);
		Top.setTextureOffset(0, 0).addBox(-7.5F, -5.0F, -14.0F, 14.0F, 5.0F, 14.0F, 0.0F, false);
		Top.setModelRendererName("Top");
		this.registerModelRenderer(Top);

		Lock = new AnimatedModelRenderer(this);
		Lock.setRotationPoint(0.0F, -1.0F, 0.0F);
		Top.addChild(Lock);
		Lock.setTextureOffset(0, 0).addBox(-1.5F, -1.5F, -15.0F, 2.0F, 4.0F, 1.0F, 0.0F, false);
		Lock.setModelRendererName("Lock");
		this.registerModelRenderer(Lock);

		Botttom = new AnimatedModelRenderer(this);
		Botttom.setRotationPoint(8.0F, -11.0F, -7.5F);
		Chest_body.addChild(Botttom);
		Botttom.setTextureOffset(0, 20).addBox(-15.0F, 1.0F, 1.0F, 14.0F, 9.0F, 14.0F, 0.0F, false);
		Botttom.setModelRendererName("Botttom");
		this.registerModelRenderer(Botttom);

		Body_All = new AnimatedModelRenderer(this);
		Body_All.setRotationPoint(0.0F, 24.0F, 0.0F);

		Body_All.setModelRendererName("Body_All");
		this.registerModelRenderer(Body_All);

		Hand2 = new AnimatedModelRenderer(this);
		Hand2.setRotationPoint(-6.5F, -19.0F, 3.0F);
		Body_All.addChild(Hand2);
		setRotationAngle(Hand2, 0.0F, 0.7854F, 0.6981F);

		Hand2.setModelRendererName("Hand2");
		this.registerModelRenderer(Hand2);

		bone4 = new AnimatedModelRenderer(this);
		bone4.setRotationPoint(-12.5F, 11.5F, -3.0F);
		Hand2.addChild(bone4);
		bone4.setTextureOffset(61, 26).addBox(11.5F, -12.5F, 0.0F, 2.0F, 2.0F, 5.0F, 0.0F, false);
		bone4.setModelRendererName("bone4");
		this.registerModelRenderer(bone4);

		bone26 = new AnimatedModelRenderer(this);
		bone26.setRotationPoint(0.0F, 0.0F, 0.0F);
		bone4.addChild(bone26);
		bone26.setTextureOffset(56, 27).addBox(11.0F, -13.0F, 2.2F, 3.0F, 3.0F, 1.0F, 0.0F, false);
		bone26.setModelRendererName("bone26");
		this.registerModelRenderer(bone26);

		Fingers2 = new AnimatedModelRenderer(this);
		Fingers2.setRotationPoint(-12.5F, 11.5F, -3.0F);
		Hand2.addChild(Fingers2);

		Fingers2.setModelRendererName("Fingers2");
		this.registerModelRenderer(Fingers2);

		Finger1R = new AnimatedModelRenderer(this);
		Finger1R.setRotationPoint(12.5F, -10.5F, 0.5F);
		Fingers2.addChild(Finger1R);
		setRotationAngle(Finger1R, 0.3491F, -0.4363F, 0.0F);
		Finger1R.setTextureOffset(70, 15).addBox(-0.5F, -0.3F, -3.0F, 1.0F, 1.0F, 3.0F, 0.0F, false);
		Finger1R.setModelRendererName("Finger1R");
		this.registerModelRenderer(Finger1R);

		Finger2R = new AnimatedModelRenderer(this);
		Finger2R.setRotationPoint(12.5F, -11.5F, 0.5F);
		Fingers2.addChild(Finger2R);
		setRotationAngle(Finger2R, 0.0F, -0.4363F, 0.0F);
		Finger2R.setTextureOffset(70, 15).addBox(-0.5F, -0.5F, -3.0F, 1.0F, 1.0F, 3.0F, 0.0F, false);
		Finger2R.setModelRendererName("Finger2R");
		this.registerModelRenderer(Finger2R);

		Finger3R = new AnimatedModelRenderer(this);
		Finger3R.setRotationPoint(12.5F, -12.5F, 0.5F);
		Fingers2.addChild(Finger3R);
		setRotationAngle(Finger3R, -0.2618F, -0.3491F, 0.0F);
		Finger3R.setTextureOffset(70, 15).addBox(-0.5F, -0.7F, -3.0F, 1.0F, 1.0F, 3.0F, 0.0F, false);
		Finger3R.setModelRendererName("Finger3R");
		this.registerModelRenderer(Finger3R);

		Hand1 = new AnimatedModelRenderer(this);
		Hand1.setRotationPoint(7.5F, -19.5F, 3.0F);
		Body_All.addChild(Hand1);
		setRotationAngle(Hand1, -0.3491F, -0.5236F, -0.5236F);

		Hand1.setModelRendererName("Hand1");
		this.registerModelRenderer(Hand1);

		bone21 = new AnimatedModelRenderer(this);
		bone21.setRotationPoint(-12.5F, 11.5F, -3.0F);
		Hand1.addChild(bone21);
		bone21.setTextureOffset(47, 26).addBox(11.5F, -12.5F, 0.0F, 2.0F, 2.0F, 5.0F, 0.0F, true);
		bone21.setModelRendererName("bone21");
		this.registerModelRenderer(bone21);

		bone25 = new AnimatedModelRenderer(this);
		bone25.setRotationPoint(0.0F, 0.0F, 0.0F);
		bone21.addChild(bone25);
		bone25.setTextureOffset(56, 23).addBox(11.0F, -13.0F, 2.2F, 3.0F, 3.0F, 1.0F, 0.0F, false);
		bone25.setModelRendererName("bone25");
		this.registerModelRenderer(bone25);

		Fingers1 = new AnimatedModelRenderer(this);
		Fingers1.setRotationPoint(-12.5F, 11.5F, -3.0F);
		Hand1.addChild(Fingers1);

		Fingers1.setModelRendererName("Fingers1");
		this.registerModelRenderer(Fingers1);

		Finger1L = new AnimatedModelRenderer(this);
		Finger1L.setRotationPoint(12.5F, -10.5F, 0.5F);
		Fingers1.addChild(Finger1L);
		setRotationAngle(Finger1L, 0.3491F, 0.6109F, 0.0F);
		Finger1L.setTextureOffset(70, 15).addBox(-0.5F, -0.3F, -3.0F, 1.0F, 1.0F, 3.0F, 0.0F, true);
		Finger1L.setModelRendererName("Finger1L");
		this.registerModelRenderer(Finger1L);

		Finger2L = new AnimatedModelRenderer(this);
		Finger2L.setRotationPoint(12.5F, -11.5F, 0.5F);
		Fingers1.addChild(Finger2L);
		setRotationAngle(Finger2L, 0.0F, 0.6109F, 0.0F);
		Finger2L.setTextureOffset(70, 15).addBox(-0.5F, -0.5F, -3.0F, 1.0F, 1.0F, 3.0F, 0.0F, true);
		Finger2L.setModelRendererName("Finger2L");
		this.registerModelRenderer(Finger2L);

		Finger3L = new AnimatedModelRenderer(this);
		Finger3L.setRotationPoint(12.5F, -12.5F, 0.5F);
		Fingers1.addChild(Finger3L);
		setRotationAngle(Finger3L, -0.2618F, 0.5236F, 0.0F);
		Finger3L.setTextureOffset(70, 15).addBox(-0.5F, -0.7F, -3.0F, 1.0F, 1.0F, 3.0F, 0.0F, true);
		Finger3L.setModelRendererName("Finger3L");
		this.registerModelRenderer(Finger3L);

		Body = new AnimatedModelRenderer(this);
		Body.setRotationPoint(-0.5F, -7.2F, 1.7F);
		Body_All.addChild(Body);

		Body.setModelRendererName("Body");
		this.registerModelRenderer(Body);

		Spine1 = new AnimatedModelRenderer(this);
		Spine1.setRotationPoint(0.5F, -5.0F, 2.0F);
		Body.addChild(Spine1);
		Spine1.setTextureOffset(42, 0).addBox(-0.5F, -0.15F, -0.75F, 1.0F, 1.0F, 1.0F, 0.0F, false);
		Spine1.setModelRendererName("Spine1");
		this.registerModelRenderer(Spine1);

		Spine1_1 = new AnimatedModelRenderer(this);
		Spine1_1.setRotationPoint(0.0F, 1.65F, -0.25F);
		Spine1.addChild(Spine1_1);
		Spine1_1.setTextureOffset(0, 24).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 3.0F, 2.0F, 0.0F, false);
		Spine1_1.setModelRendererName("Spine1_1");
		this.registerModelRenderer(Spine1_1);

		Spine_parts1 = new AnimatedModelRenderer(this);
		Spine_parts1.setRotationPoint(0.0F, 0.0F, 0.0F);
		Spine1_1.addChild(Spine_parts1);

		Spine_parts1.setModelRendererName("Spine_parts1");
		this.registerModelRenderer(Spine_parts1);

		bone8 = new AnimatedModelRenderer(this);
		bone8.setRotationPoint(2.0F, 0.5F, 0.5F);
		Spine_parts1.addChild(bone8);
		setRotationAngle(bone8, 0.0F, 3.1416F, -0.2618F);
		bone8.setTextureOffset(8, 5).addBox(2.5095F, -2.2253F, 0.0F, 2.0F, 3.0F, 1.0F, 0.0F, false);
		bone8.setModelRendererName("bone8");
		this.registerModelRenderer(bone8);

		bone9 = new AnimatedModelRenderer(this);
		bone9.setRotationPoint(2.0F, 0.0F, -0.5F);
		bone8.addChild(bone9);
		bone9.setTextureOffset(8, 19).addBox(2.5095F, -2.2253F, 0.0F, 1.0F, 3.0F, 2.0F, 0.0F, true);
		bone9.setModelRendererName("bone9");
		this.registerModelRenderer(bone9);

		bone6 = new AnimatedModelRenderer(this);
		bone6.setRotationPoint(2.0F, 0.5F, 0.0F);
		Spine_parts1.addChild(bone6);
		setRotationAngle(bone6, 0.0F, 0.0F, 0.2618F);
		bone6.setTextureOffset(8, 10).addBox(-1.3542F, -1.1901F, -0.5F, 2.0F, 3.0F, 1.0F, 0.0F, false);
		bone6.setModelRendererName("bone6");
		this.registerModelRenderer(bone6);

		bone7 = new AnimatedModelRenderer(this);
		bone7.setRotationPoint(2.0F, 0.0F, -0.5F);
		bone6.addChild(bone7);
		bone7.setTextureOffset(8, 19).addBox(-1.3542F, -1.1901F, -0.5F, 1.0F, 3.0F, 2.0F, 0.0F, false);
		bone7.setModelRendererName("bone7");
		this.registerModelRenderer(bone7);

		Spine2 = new AnimatedModelRenderer(this);
		Spine2.setRotationPoint(0.0F, -1.65F, 0.25F);
		Spine1_1.addChild(Spine2);
		Spine2.setTextureOffset(42, 0).addBox(-0.5F, -3.65F, -0.75F, 1.0F, 1.0F, 1.0F, 0.0F, false);
		Spine2.setModelRendererName("Spine2");
		this.registerModelRenderer(Spine2);

		Spine1_2 = new AnimatedModelRenderer(this);
		Spine1_2.setRotationPoint(0.0F, 0.15F, -0.25F);
		Spine2.addChild(Spine1_2);
		Spine1_2.setTextureOffset(0, 19).addBox(-1.0F, -3.0F, -1.0F, 2.0F, 3.0F, 2.0F, 0.0F, false);
		Spine1_2.setModelRendererName("Spine1_2");
		this.registerModelRenderer(Spine1_2);

		Spine_parts2 = new AnimatedModelRenderer(this);
		Spine_parts2.setRotationPoint(0.0F, -2.0F, 0.0F);
		Spine1_2.addChild(Spine_parts2);

		Spine_parts2.setModelRendererName("Spine_parts2");
		this.registerModelRenderer(Spine_parts2);

		bone5 = new AnimatedModelRenderer(this);
		bone5.setRotationPoint(2.0F, 0.5F, 0.5F);
		Spine_parts2.addChild(bone5);
		setRotationAngle(bone5, 0.0F, 3.1416F, -0.2618F);
		bone5.setTextureOffset(0, 10).addBox(2.5095F, -2.2253F, 0.0F, 2.0F, 3.0F, 1.0F, 0.0F, false);
		bone5.setModelRendererName("bone5");
		this.registerModelRenderer(bone5);

		bone10 = new AnimatedModelRenderer(this);
		bone10.setRotationPoint(2.0F, 0.0F, -0.5F);
		bone5.addChild(bone10);
		bone10.setTextureOffset(8, 24).addBox(2.5095F, -2.2253F, 0.0F, 1.0F, 3.0F, 2.0F, 0.0F, true);
		bone10.setModelRendererName("bone10");
		this.registerModelRenderer(bone10);

		bone11 = new AnimatedModelRenderer(this);
		bone11.setRotationPoint(2.0F, 0.5F, 0.0F);
		Spine_parts2.addChild(bone11);
		setRotationAngle(bone11, 0.0F, 0.0F, 0.2618F);
		bone11.setTextureOffset(0, 10).addBox(-1.3542F, -1.1901F, -0.5F, 2.0F, 3.0F, 1.0F, 0.0F, false);
		bone11.setModelRendererName("bone11");
		this.registerModelRenderer(bone11);

		bone12 = new AnimatedModelRenderer(this);
		bone12.setRotationPoint(2.0F, 0.0F, -0.5F);
		bone11.addChild(bone12);
		bone12.setTextureOffset(8, 24).addBox(-1.3542F, -1.1901F, -0.5F, 1.0F, 3.0F, 2.0F, 0.0F, false);
		bone12.setModelRendererName("bone12");
		this.registerModelRenderer(bone12);

		Spine3 = new AnimatedModelRenderer(this);
		Spine3.setRotationPoint(0.0F, -3.15F, 0.25F);
		Spine1_2.addChild(Spine3);
		Spine3.setTextureOffset(42, 0).addBox(-0.5F, -4.15F, -0.75F, 1.0F, 1.0F, 1.0F, 0.0F, false);
		Spine3.setModelRendererName("Spine3");
		this.registerModelRenderer(Spine3);

		Spine1_3 = new AnimatedModelRenderer(this);
		Spine1_3.setRotationPoint(0.0F, 0.15F, -0.25F);
		Spine3.addChild(Spine1_3);
		Spine1_3.setTextureOffset(0, 24).addBox(-1.0F, -3.5F, -1.0F, 2.0F, 3.0F, 2.0F, 0.0F, true);
		Spine1_3.setModelRendererName("Spine1_3");
		this.registerModelRenderer(Spine1_3);

		Spine_parts3 = new AnimatedModelRenderer(this);
		Spine_parts3.setRotationPoint(0.0F, -2.5F, 0.0F);
		Spine1_3.addChild(Spine_parts3);

		Spine_parts3.setModelRendererName("Spine_parts3");
		this.registerModelRenderer(Spine_parts3);

		bone13 = new AnimatedModelRenderer(this);
		bone13.setRotationPoint(2.0F, 0.5F, 0.5F);
		Spine_parts3.addChild(bone13);
		setRotationAngle(bone13, 0.0F, 3.1416F, -0.2618F);
		bone13.setTextureOffset(0, 5).addBox(2.5095F, -2.2253F, 0.0F, 2.0F, 3.0F, 1.0F, 0.0F, false);
		bone13.setModelRendererName("bone13");
		this.registerModelRenderer(bone13);

		bone14 = new AnimatedModelRenderer(this);
		bone14.setRotationPoint(2.0F, 0.0F, -0.5F);
		bone13.addChild(bone14);
		bone14.setTextureOffset(8, 24).addBox(2.5095F, -2.2253F, 0.0F, 1.0F, 3.0F, 2.0F, 0.0F, true);
		bone14.setModelRendererName("bone14");
		this.registerModelRenderer(bone14);

		bone15 = new AnimatedModelRenderer(this);
		bone15.setRotationPoint(2.0F, 0.5F, 0.0F);
		Spine_parts3.addChild(bone15);
		setRotationAngle(bone15, 0.0F, 0.0F, 0.2618F);
		bone15.setTextureOffset(8, 5).addBox(-1.3542F, -1.1901F, -0.5F, 2.0F, 3.0F, 1.0F, 0.0F, false);
		bone15.setModelRendererName("bone15");
		this.registerModelRenderer(bone15);

		bone16 = new AnimatedModelRenderer(this);
		bone16.setRotationPoint(2.0F, 0.0F, -0.5F);
		bone15.addChild(bone16);
		bone16.setTextureOffset(8, 19).addBox(-1.3542F, -1.1901F, -0.5F, 1.0F, 3.0F, 2.0F, 0.0F, false);
		bone16.setModelRendererName("bone16");
		this.registerModelRenderer(bone16);

		Spine4 = new AnimatedModelRenderer(this);
		Spine4.setRotationPoint(0.0F, -4.15F, 0.25F);
		Spine1_3.addChild(Spine4);
		Spine4.setTextureOffset(42, 0).addBox(-0.5F, -3.65F, -0.75F, 1.0F, 1.0F, 1.0F, 0.0F, false);
		Spine4.setModelRendererName("Spine4");
		this.registerModelRenderer(Spine4);

		Spine1_4 = new AnimatedModelRenderer(this);
		Spine1_4.setRotationPoint(0.0F, 0.15F, -0.25F);
		Spine4.addChild(Spine1_4);
		Spine1_4.setTextureOffset(0, 19).addBox(-1.0F, -3.0F, -1.0F, 2.0F, 3.0F, 2.0F, 0.0F, true);
		Spine1_4.setModelRendererName("Spine1_4");
		this.registerModelRenderer(Spine1_4);

		Spine_parts4 = new AnimatedModelRenderer(this);
		Spine_parts4.setRotationPoint(0.0F, -2.0F, 0.0F);
		Spine1_4.addChild(Spine_parts4);

		Spine_parts4.setModelRendererName("Spine_parts4");
		this.registerModelRenderer(Spine_parts4);

		bone17 = new AnimatedModelRenderer(this);
		bone17.setRotationPoint(2.0F, 0.5F, 0.5F);
		Spine_parts4.addChild(bone17);
		setRotationAngle(bone17, 0.0F, 3.1416F, -0.2618F);
		bone17.setTextureOffset(6, 1).addBox(2.5095F, -2.2253F, 0.0F, 2.0F, 3.0F, 1.0F, 0.0F, false);
		bone17.setModelRendererName("bone17");
		this.registerModelRenderer(bone17);

		bone18 = new AnimatedModelRenderer(this);
		bone18.setRotationPoint(2.0F, 0.0F, -0.5F);
		bone17.addChild(bone18);
		bone18.setTextureOffset(8, 19).addBox(2.5095F, -2.2253F, 0.0F, 1.0F, 3.0F, 2.0F, 0.0F, true);
		bone18.setModelRendererName("bone18");
		this.registerModelRenderer(bone18);

		bone19 = new AnimatedModelRenderer(this);
		bone19.setRotationPoint(2.0F, 0.5F, 0.0F);
		Spine_parts4.addChild(bone19);
		setRotationAngle(bone19, 0.0F, 0.0F, 0.2618F);
		bone19.setTextureOffset(6, 1).addBox(-1.3542F, -1.1901F, -0.5F, 2.0F, 3.0F, 1.0F, 0.0F, false);
		bone19.setModelRendererName("bone19");
		this.registerModelRenderer(bone19);

		bone20 = new AnimatedModelRenderer(this);
		bone20.setRotationPoint(2.0F, 0.0F, -0.5F);
		bone19.addChild(bone20);
		bone20.setTextureOffset(8, 19).addBox(-1.3542F, -1.1901F, -0.5F, 1.0F, 3.0F, 2.0F, 0.0F, false);
		bone20.setModelRendererName("bone20");
		this.registerModelRenderer(bone20);

		Head = new AnimatedModelRenderer(this);
		Head.setRotationPoint(0.0F, -3.75F, -0.55F);
		Spine4.addChild(Head);
		Head.setTextureOffset(42, 0).addBox(-3.0F, -7.25F, -3.25F, 6.0F, 6.0F, 6.0F, 0.0F, false);
		Head.setModelRendererName("Head");
		this.registerModelRenderer(Head);

		Horns = new AnimatedModelRenderer(this);
		Horns.setRotationPoint(1.0F, -0.5F, 0.0F);
		Head.addChild(Horns);

		Horns.setModelRendererName("Horns");
		this.registerModelRenderer(Horns);

		Horn1 = new AnimatedModelRenderer(this);
		Horn1.setRotationPoint(-2.5F, -5.7036F, -2.5278F);
		Horns.addChild(Horn1);
		setRotationAngle(Horn1, 0.5236F, -0.0873F, 0.0F);
		Horn1.setTextureOffset(42, 19).addBox(-1.0F, -1.4683F, 0.0138F, 2.0F, 2.0F, 3.0F, 0.0F, false);
		Horn1.setModelRendererName("Horn1");
		this.registerModelRenderer(Horn1);

		bone2 = new AnimatedModelRenderer(this);
		bone2.setRotationPoint(1.0F, -2.3977F, 6.122F);
		Horn1.addChild(bone2);
		setRotationAngle(bone2, -0.2618F, 0.0F, 0.0F);
		bone2.setTextureOffset(60, 0).addBox(-1.5F, 1.7851F, -2.8827F, 1.0F, 2.0F, 4.0F, 0.0F, false);
		bone2.setModelRendererName("bone2");
		this.registerModelRenderer(bone2);

		Horn2 = new AnimatedModelRenderer(this);
		Horn2.setRotationPoint(0.5F, -5.7036F, -2.5278F);
		Horns.addChild(Horn2);
		setRotationAngle(Horn2, 0.5236F, 0.0873F, 0.0F);
		Horn2.setTextureOffset(42, 24).addBox(-1.0F, -1.4683F, 0.0138F, 2.0F, 2.0F, 3.0F, 0.0F, false);
		Horn2.setModelRendererName("Horn2");
		this.registerModelRenderer(Horn2);

		bone3 = new AnimatedModelRenderer(this);
		bone3.setRotationPoint(1.0F, -2.3977F, 6.122F);
		Horn2.addChild(bone3);
		setRotationAngle(bone3, -0.2618F, 0.0F, 0.0F);
		bone3.setTextureOffset(70, 0).addBox(-1.5F, 1.7851F, -2.8827F, 1.0F, 2.0F, 4.0F, 0.0F, false);
		bone3.setModelRendererName("bone3");
		this.registerModelRenderer(bone3);

		bone = new AnimatedModelRenderer(this);
		bone.setRotationPoint(0.0F, -1.5F, -3.5F);
		Head.addChild(bone);
		setRotationAngle(bone, -0.3491F, 0.0F, 0.0F);
		bone.setTextureOffset(68, 6).addBox(-1.0F, -3.2802F, -0.579F, 2.0F, 4.0F, 4.0F, 0.0F, false);
		bone.setModelRendererName("bone");
		this.registerModelRenderer(bone);

		NeckBase = new AnimatedModelRenderer(this);
		NeckBase.setRotationPoint(0.0F, 0.35F, 0.25F);
		Head.addChild(NeckBase);
		NeckBase.setTextureOffset(56, 13).addBox(-1.5F, -3.0F, -1.5F, 3.0F, 3.0F, 3.0F, 0.0F, false);
		NeckBase.setModelRendererName("NeckBase");
		this.registerModelRenderer(NeckBase);

		this.rootBones.add(Chest_body);
		this.rootBones.add(Body_All);
	}


	@Override
	public ResourceLocation getAnimationFileLocation()
	{
		return new ResourceLocation("geckolib", "animations/jackinthebox_anim.json");
	}
}