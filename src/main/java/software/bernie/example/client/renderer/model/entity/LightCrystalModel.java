package software.bernie.example.client.renderer.model.entity;

import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib.model.AnimatedEntityModel;
import software.bernie.geckolib.animation.render.AnimatedModelRenderer;
import software.bernie.example.entity.LightCrystalEntity;

// Made with Blockbench 3.5.2
// Exported for Minecraft version 1.12.2 or 1.15.2 (same format for both) for entity models animated with GeckoLib
// Paste this class into your mod and follow the documentation for GeckoLib to use animations. You can find the documentation here: https://github.com/bernie-g/geckolib
// Blockbench plugin created by Gecko
public class LightCrystalModel extends AnimatedEntityModel<LightCrystalEntity>
{

	private final AnimatedModelRenderer Crystail_main;
	private final AnimatedModelRenderer C1;
	private final AnimatedModelRenderer C1_1;
	private final AnimatedModelRenderer C1_2;
	private final AnimatedModelRenderer C1_3;
	private final AnimatedModelRenderer C1_4;
	private final AnimatedModelRenderer C1_5;
	private final AnimatedModelRenderer C1_6;
	private final AnimatedModelRenderer C1_7;
	private final AnimatedModelRenderer C1_8;
	private final AnimatedModelRenderer C1_9;
	private final AnimatedModelRenderer C1_10;
	private final AnimatedModelRenderer Base;
	private final AnimatedModelRenderer bone;
	private final AnimatedModelRenderer bone4;
	private final AnimatedModelRenderer bone6;
	private final AnimatedModelRenderer bone2;
	private final AnimatedModelRenderer bone7;
	private final AnimatedModelRenderer bone8;
	private final AnimatedModelRenderer bone3;
	private final AnimatedModelRenderer bone9;
	private final AnimatedModelRenderer bone10;
	private final AnimatedModelRenderer bone5;
	private final AnimatedModelRenderer bone11;
	private final AnimatedModelRenderer bone12;

	public LightCrystalModel()
	{
		textureWidth = 48;
		textureHeight = 48;
		Crystail_main = new AnimatedModelRenderer(this);
		Crystail_main.setRotationPoint(-0.3F, 20.1F, 0.0F);

		Crystail_main.setModelRendererName("Crystail_main");
		this.registerModelRenderer(Crystail_main);

		C1 = new AnimatedModelRenderer(this);
		C1.setRotationPoint(0.3886F, -40.5035F, -0.0922F);
		Crystail_main.addChild(C1);
		C1.setTextureOffset(0, 6).addBox(-1.7886F, 11.0997F, -1.4178F, 3.0F, 11.0F, 3.0F, 0.0F, false);
		C1.setModelRendererName("C1");
		this.registerModelRenderer(C1);

		C1_1 = new AnimatedModelRenderer(this);
		C1_1.setRotationPoint(1.701F, 12.9615F, 8.4983F);
		C1.addChild(C1_1);
		C1_1.setTextureOffset(0, 6).addBox(-3.7896F, -4.0152F, -9.3694F, 2.0F, 12.0F, 2.0F, 0.0F, false);
		C1_1.setModelRendererName("C1_1");
		this.registerModelRenderer(C1_1);

		C1_2 = new AnimatedModelRenderer(this);
		C1_2.setRotationPoint(0.6691F, 14.7462F, 6.4983F);
		C1.addChild(C1_2);
		C1_2.setTextureOffset(2, 10).addBox(-1.8577F, -4.2436F, -8.43F, 2.0F, 7.0F, 2.0F, 0.0F, false);
		C1_2.setModelRendererName("C1_2");
		this.registerModelRenderer(C1_2);

		C1_3 = new AnimatedModelRenderer(this);
		C1_3.setRotationPoint(3.4702F, 15.8615F, 6.4983F);
		C1.addChild(C1_3);
		C1_3.setTextureOffset(1, 7).addBox(-3.7588F, -3.8989F, -6.646F, 2.0F, 6.0F, 2.0F, 0.0F, false);
		C1_3.setModelRendererName("C1_3");
		this.registerModelRenderer(C1_3);

		C1_4 = new AnimatedModelRenderer(this);
		C1_4.setRotationPoint(1.7146F, 19.4726F, 6.0983F);
		C1.addChild(C1_4);
		C1_4.setTextureOffset(2, 8).addBox(-1.6032F, -4.311F, -8.1875F, 2.0F, 10.0F, 2.0F, 0.0F, false);
		C1_4.setModelRendererName("C1_4");
		this.registerModelRenderer(C1_4);

		C1_5 = new AnimatedModelRenderer(this);
		C1_5.setRotationPoint(3.5911F, 18.7049F, 8.8983F);
		C1.addChild(C1_5);
		C1_5.setTextureOffset(0, 8).addBox(-6.1797F, -4.311F, -8.911F, 2.0F, 9.0F, 2.0F, 0.0F, false);
		C1_5.setModelRendererName("C1_5");
		this.registerModelRenderer(C1_5);

		C1_6 = new AnimatedModelRenderer(this);
		C1_6.setRotationPoint(3.9756F, 18.8152F, 6.6983F);
		C1.addChild(C1_6);
		C1_6.setTextureOffset(1, 4).addBox(-4.2642F, 0.7858F, -6.7146F, 2.0F, 7.0F, 2.0F, 0.0F, false);
		C1_6.setModelRendererName("C1_6");
		this.registerModelRenderer(C1_6);

		C1_7 = new AnimatedModelRenderer(this);
		C1_7.setRotationPoint(4.2547F, 14.2139F, 7.6983F);
		C1.addChild(C1_7);
		C1_7.setTextureOffset(0, 3).addBox(-5.6433F, -1.311F, -7.0474F, 2.0F, 9.0F, 2.0F, 0.0F, false);
		C1_7.setModelRendererName("C1_7");
		this.registerModelRenderer(C1_7);

		C1_8 = new AnimatedModelRenderer(this);
		C1_8.setRotationPoint(2.8765F, 19.0202F, 7.6983F);
		C1.addChild(C1_8);
		C1_8.setTextureOffset(3, 3).addBox(-4.2651F, 1.689F, -8.4256F, 2.0F, 8.0F, 2.0F, 0.0F, false);
		C1_8.setModelRendererName("C1_8");
		this.registerModelRenderer(C1_8);

		C1_9 = new AnimatedModelRenderer(this);
		C1_9.setRotationPoint(1.2322F, 11.1852F, 9.0983F);
		C1.addChild(C1_9);
		C1_9.setTextureOffset(4, 10).addBox(-3.7208F, 1.689F, -11.4699F, 2.0F, 6.0F, 2.0F, 0.0F, false);
		C1_9.setModelRendererName("C1_9");
		this.registerModelRenderer(C1_9);

		C1_10 = new AnimatedModelRenderer(this);
		C1_10.setRotationPoint(1.4702F, 18.3402F, 8.6983F);
		C1.addChild(C1_10);
		C1_10.setTextureOffset(2, 9).addBox(-3.8588F, 1.5438F, -10.2497F, 2.0F, 6.0F, 2.0F, 0.0F, false);
		C1_10.setModelRendererName("C1_10");
		this.registerModelRenderer(C1_10);

		Base = new AnimatedModelRenderer(this);
		Base.setRotationPoint(0.3725F, 21.0F, -0.3582F);
		Base.setTextureOffset(0, 31).addBox(-5.8725F, -3.0F, -5.1418F, 11.0F, 6.0F, 11.0F, 0.0F, false);
		Base.setModelRendererName("Base");
		this.registerModelRenderer(Base);

		bone = new AnimatedModelRenderer(this);
		bone.setRotationPoint(-0.5F, 0.0F, -0.5F);
		Base.addChild(bone);
		bone.setTextureOffset(36, 0).addBox(-1.4F, -6.0F, 5.4F, 3.0F, 9.0F, 3.0F, 0.0F, false);
		bone.setModelRendererName("bone");
		this.registerModelRenderer(bone);

		bone4 = new AnimatedModelRenderer(this);
		bone4.setRotationPoint(-0.5F, -6.5F, 7.7F);
		bone.addChild(bone4);
		setRotationAngle(bone4, 0.3491F, 0.0F, 0.0F);
		bone4.setTextureOffset(12, 0).addBox(-0.4F, -5.2907F, -2.5132F, 2.0F, 6.0F, 3.0F, 0.0F, false);
		bone4.setModelRendererName("bone4");
		this.registerModelRenderer(bone4);

		bone6 = new AnimatedModelRenderer(this);
		bone6.setRotationPoint(0.6F, -4.9972F, 1.3289F);
		bone4.addChild(bone6);
		setRotationAngle(bone6, -0.8727F, 0.0F, 0.0F);
		bone6.setTextureOffset(22, 0).addBox(-1.5F, -1.4804F, -2.7517F, 3.0F, 6.0F, 2.0F, 0.0F, false);
		bone6.setModelRendererName("bone6");
		this.registerModelRenderer(bone6);

		bone2 = new AnimatedModelRenderer(this);
		bone2.setRotationPoint(-0.3725F, -1.5F, -6.1F);
		Base.addChild(bone2);
		bone2.setTextureOffset(36, 0).addBox(-1.5F, -4.5F, -1.0F, 3.0F, 9.0F, 3.0F, 0.0F, false);
		bone2.setModelRendererName("bone2");
		this.registerModelRenderer(bone2);

		bone7 = new AnimatedModelRenderer(this);
		bone7.setRotationPoint(1.3725F, -5.5F, 0.1F);
		bone2.addChild(bone7);
		setRotationAngle(bone7, 0.3491F, 3.1416F, 0.0F);
		bone7.setTextureOffset(12, 0).addBox(0.3725F, -4.6841F, -2.3084F, 2.0F, 6.0F, 3.0F, 0.0F, false);
		bone7.setModelRendererName("bone7");
		this.registerModelRenderer(bone7);

		bone8 = new AnimatedModelRenderer(this);
		bone8.setRotationPoint(1.3725F, -5.4872F, -1.4213F);
		bone7.addChild(bone8);
		setRotationAngle(bone8, -0.8727F, 0.0F, 0.0F);
		bone8.setTextureOffset(22, 0).addBox(-1.5F, -3.0392F, -0.0121F, 3.0F, 6.0F, 2.0F, 0.0F, false);
		bone8.setModelRendererName("bone8");
		this.registerModelRenderer(bone8);

		bone3 = new AnimatedModelRenderer(this);
		bone3.setRotationPoint(-7.0F, 0.0F, -6.0F);
		Base.addChild(bone3);
		bone3.setTextureOffset(36, 12).addBox(-1.0F, -6.0F, 4.8582F, 3.0F, 9.0F, 3.0F, 0.0F, false);
		bone3.setModelRendererName("bone3");
		this.registerModelRenderer(bone3);

		bone9 = new AnimatedModelRenderer(this);
		bone9.setRotationPoint(0.5F, -6.0F, 6.0F);
		bone3.addChild(bone9);
		setRotationAngle(bone9, 0.0F, -1.5708F, 0.3491F);
		bone9.setTextureOffset(12, 0).addBox(-0.6418F, -5.487F, -1.5905F, 2.0F, 6.0F, 3.0F, 0.0F, false);
		bone9.setModelRendererName("bone9");
		this.registerModelRenderer(bone9);

		bone10 = new AnimatedModelRenderer(this);
		bone10.setRotationPoint(0.3725F, -8.9412F, 0.4649F);
		bone9.addChild(bone10);
		setRotationAngle(bone10, -0.8727F, 0.0F, 0.0F);
		bone10.setTextureOffset(22, 0).addBox(-1.5143F, -0.4401F, 1.2678F, 3.0F, 6.0F, 2.0F, 0.0F, false);
		bone10.setModelRendererName("bone10");
		this.registerModelRenderer(bone10);

		bone5 = new AnimatedModelRenderer(this);
		bone5.setRotationPoint(-0.3725F, 3.0F, 0.3582F);
		Base.addChild(bone5);
		bone5.setTextureOffset(36, 12).addBox(4.4725F, -9.0F, -1.5F, 3.0F, 9.0F, 3.0F, 0.0F, false);
		bone5.setModelRendererName("bone5");
		this.registerModelRenderer(bone5);

		bone11 = new AnimatedModelRenderer(this);
		bone11.setRotationPoint(6.1654F, -8.3494F, 0.2505F);
		bone5.addChild(bone11);
		setRotationAngle(bone11, 0.0F, 1.5708F, -0.3491F);
		bone11.setTextureOffset(12, 0).addBox(-0.7495F, -6.1643F, -1.5492F, 2.0F, 6.0F, 3.0F, 0.0F, false);
		bone11.setModelRendererName("bone11");
		this.registerModelRenderer(bone11);

		bone12 = new AnimatedModelRenderer(this);
		bone12.setRotationPoint(0.2505F, -6.2681F, -0.1454F);
		bone11.addChild(bone12);
		setRotationAngle(bone12, -0.8727F, 0.0F, 0.0F);
		bone12.setTextureOffset(22, 0).addBox(-1.5F, -3.1F, -0.8268F, 3.0F, 6.0F, 2.0F, 0.0F, false);
		bone12.setModelRendererName("bone12");
		this.registerModelRenderer(bone12);

		this.rootBones.add(Crystail_main);
		this.rootBones.add(Base);
	}


	@Override
	public ResourceLocation getAnimationFileLocation(LightCrystalEntity entity)
	{
		return new ResourceLocation("geckolib", "animations/lightcrystal.json");
	}
}