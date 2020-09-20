// Made with Blockbench 3.6.6
// Exported for Minecraft version 1.12.2 or 1.15.2 (same format for both) for entity models animated with GeckoLib
// Paste this class into your mod and follow the documentation for GeckoLib to use animations. You can find the documentation here: https://github.com/bernie-g/geckolib
// Blockbench plugin created by Gecko
package software.bernie.example.client.renderer.model.armor;

import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib.model.AnimatedArmorModel;
import software.bernie.geckolib.renderers.legacy.AnimatedModelRenderer;
import software.bernie.example.item.PotatoArmor;

public class PotatoArmorModel extends AnimatedArmorModel<PotatoArmor>
{
	private final AnimatedModelRenderer armor;
	private final AnimatedModelRenderer helmet;
	private final AnimatedModelRenderer plates;
	private final AnimatedModelRenderer plate1;
	private final AnimatedModelRenderer rightPlate;
	private final AnimatedModelRenderer leftPlate;
	private final AnimatedModelRenderer centerPlate;
	private final AnimatedModelRenderer plate2;
	private final AnimatedModelRenderer rightPlate2;
	private final AnimatedModelRenderer leftPlate2;
	private final AnimatedModelRenderer frontPlates;
	private final AnimatedModelRenderer frontPlate1;
	private final AnimatedModelRenderer frontPlate2;
	private final AnimatedModelRenderer chestplate;
	private final AnimatedModelRenderer core;
	private final AnimatedModelRenderer rightArm;
	private final AnimatedModelRenderer rightPad;
	private final AnimatedModelRenderer rightPotato;
	private final AnimatedModelRenderer rightGauntlet;
	private final AnimatedModelRenderer leftArm;
	private final AnimatedModelRenderer leftPad;
	private final AnimatedModelRenderer leftPotato;
	private final AnimatedModelRenderer leftGauntlet;
	private final AnimatedModelRenderer leggings;
	private final AnimatedModelRenderer leftLeg;
	private final AnimatedModelRenderer leftBase;
	private final AnimatedModelRenderer rightLeg;
	private final AnimatedModelRenderer rightBase;
	private final AnimatedModelRenderer boots;
	private final AnimatedModelRenderer leftBoot;
	private final AnimatedModelRenderer leftFront;
	private final AnimatedModelRenderer leftBone;
	private final AnimatedModelRenderer leftBone2;
	private final AnimatedModelRenderer rightBoot;
	private final AnimatedModelRenderer rightFront;
	private final AnimatedModelRenderer rightBone;
	private final AnimatedModelRenderer rightBone2;

	public PotatoArmorModel()
	{
		textureWidth = 128;
		textureHeight = 128;
		armor = new AnimatedModelRenderer(this);
		armor.setRotationPoint(0.0F, 24.0F, 0.0F);

		armor.setModelRendererName("armor");
		this.registerModelRenderer(armor);

		helmet = new AnimatedModelRenderer(this);
		helmet.setRotationPoint(0.0F, -27.75F, 0.0F);
		armor.addChild(helmet);
		helmet.setTextureOffset(0, 0).addBox(-4.0F, -4.25F, -4.0F, 8.0F, 6.0F, 9.0F, 0.6F, false);
		helmet.setModelRendererName("helmet");
		this.registerModelRenderer(helmet);

		plates = new AnimatedModelRenderer(this);
		plates.setRotationPoint(0.0F, -4.25F, 4.0F);
		helmet.addChild(plates);

		plates.setModelRendererName("plates");
		this.registerModelRenderer(plates);

		plate1 = new AnimatedModelRenderer(this);
		plate1.setRotationPoint(0.0F, 0.0F, 0.0F);
		plates.addChild(plate1);

		plate1.setModelRendererName("plate1");
		this.registerModelRenderer(plate1);

		rightPlate = new AnimatedModelRenderer(this);
		rightPlate.setRotationPoint(-5.5F, 0.5F, 0.5F);
		plate1.addChild(rightPlate);
		setRotationAngle(rightPlate, -0.48F, 0.0F, -0.2967F);
		rightPlate.setTextureOffset(49, 50).addBox(-0.5F, -3.5F, -1.7F, 2.0F, 8.0F, 3.0F, 0.0F, false);
		rightPlate.setModelRendererName("rightPlate");
		this.registerModelRenderer(rightPlate);

		leftPlate = new AnimatedModelRenderer(this);
		leftPlate.setRotationPoint(5.5F, 0.0F, 0.6539F);
		plate1.addChild(leftPlate);
		setRotationAngle(leftPlate, -0.5236F, 0.0F, 0.2967F);
		leftPlate.setTextureOffset(39, 50).addBox(-1.5091F, -3.0F, -1.5F, 2.0F, 8.0F, 3.0F, 0.0F, false);
		leftPlate.setModelRendererName("leftPlate");
		this.registerModelRenderer(leftPlate);

		centerPlate = new AnimatedModelRenderer(this);
		centerPlate.setRotationPoint(0.0313F, -1.4688F, 0.3125F);
		plate1.addChild(centerPlate);
		setRotationAngle(centerPlate, -0.5236F, 0.0F, 0.0F);
		centerPlate.setTextureOffset(34, 10).addBox(-5.0F, -1.0F, -0.5F, 10.0F, 3.0F, 1.0F, 0.0F, false);
		centerPlate.setModelRendererName("centerPlate");
		this.registerModelRenderer(centerPlate);

		plate2 = new AnimatedModelRenderer(this);
		plate2.setRotationPoint(0.0F, 1.0F, -2.0F);
		plates.addChild(plate2);

		plate2.setModelRendererName("plate2");
		this.registerModelRenderer(plate2);

		rightPlate2 = new AnimatedModelRenderer(this);
		rightPlate2.setRotationPoint(-5.5F, 0.5F, 0.5F);
		plate2.addChild(rightPlate2);
		setRotationAngle(rightPlate2, -0.48F, 0.0F, -0.8203F);
		rightPlate2.setTextureOffset(0, 51).addBox(-0.5F, -3.5F, -1.7F, 2.0F, 7.0F, 3.0F, 0.0F, false);
		rightPlate2.setModelRendererName("rightPlate2");
		this.registerModelRenderer(rightPlate2);

		leftPlate2 = new AnimatedModelRenderer(this);
		leftPlate2.setRotationPoint(5.5F, 0.0F, 0.6539F);
		plate2.addChild(leftPlate2);
		setRotationAngle(leftPlate2, -0.5236F, 0.0F, 0.9512F);
		leftPlate2.setTextureOffset(23, 53).addBox(-1.5091F, -3.0F, -1.5F, 2.0F, 7.0F, 3.0F, 0.0F, false);
		leftPlate2.setModelRendererName("leftPlate2");
		this.registerModelRenderer(leftPlate2);

		frontPlates = new AnimatedModelRenderer(this);
		frontPlates.setRotationPoint(0.0F, 0.0F, 0.0F);
		plates.addChild(frontPlates);

		frontPlates.setModelRendererName("frontPlates");
		this.registerModelRenderer(frontPlates);

		frontPlate1 = new AnimatedModelRenderer(this);
		frontPlate1.setRotationPoint(0.0F, 0.0F, 0.0F);
		frontPlates.addChild(frontPlate1);
		frontPlate1.setTextureOffset(59, 55).addBox(-1.0F, -2.0F, -10.1563F, 2.0F, 2.0F, 6.0F, 0.0F, false);
		frontPlate1.setModelRendererName("frontPlate1");
		this.registerModelRenderer(frontPlate1);

		frontPlate2 = new AnimatedModelRenderer(this);
		frontPlate2.setRotationPoint(0.0F, 0.0F, 0.0F);
		frontPlates.addChild(frontPlate2);
		frontPlate2.setTextureOffset(75, 55).addBox(-1.0F, -1.0F, -10.1563F, 2.0F, 6.0F, 2.0F, 0.0F, false);
		frontPlate2.setModelRendererName("frontPlate2");
		this.registerModelRenderer(frontPlate2);

		chestplate = new AnimatedModelRenderer(this);
		chestplate.setRotationPoint(0.0F, -18.0F, 0.0F);
		armor.addChild(chestplate);
		chestplate.setTextureOffset(0, 15).addBox(-4.0F, -6.0F, -2.0F, 8.0F, 12.0F, 4.0F, 0.6F, false);
		chestplate.setModelRendererName("chestplate");
		this.registerModelRenderer(chestplate);

		core = new AnimatedModelRenderer(this);
		core.setRotationPoint(0.0F, -1.0F, -2.4375F);
		chestplate.addChild(core);
		setRotationAngle(core, 0.3927F, 0.0F, 0.0F);
		core.setTextureOffset(54, 21).addBox(-2.0F, -2.0F, -1.0F, 4.0F, 4.0F, 2.0F, 0.0F, false);
		core.setModelRendererName("core");
		this.registerModelRenderer(core);

		rightArm = new AnimatedModelRenderer(this);
		rightArm.setRotationPoint(-6.5F, -3.5F, 0.0F);
		chestplate.addChild(rightArm);
		rightArm.setTextureOffset(48, 30).addBox(-1.5F, -2.5F, -2.0F, 3.0F, 5.0F, 4.0F, 0.6F, false);
		rightArm.setTextureOffset(24, 15).addBox(-3.0F, 2.0F, -3.0F, 4.0F, 2.0F, 6.0F, 0.0F, false);
		rightArm.setModelRendererName("rightArm");
		this.registerModelRenderer(rightArm);

		rightPad = new AnimatedModelRenderer(this);
		rightPad.setRotationPoint(-1.5F, -2.0F, 0.0F);
		rightArm.addChild(rightPad);
		setRotationAngle(rightPad, 0.0F, 0.0F, -0.1745F);
		rightPad.setTextureOffset(28, 42).addBox(-1.5F, -2.0F, -2.0F, 3.0F, 7.0F, 4.0F, 0.0F, false);
		rightPad.setModelRendererName("rightPad");
		this.registerModelRenderer(rightPad);

		rightPotato = new AnimatedModelRenderer(this);
		rightPotato.setRotationPoint(-4.0F, -8.0F, 0.0F);
		rightArm.addChild(rightPotato);
		rightPotato.setTextureOffset(32, 32).addBox(-2.0F, -3.0F, -2.0F, 4.0F, 6.0F, 4.0F, 0.0F, false);
		rightPotato.setModelRendererName("rightPotato");
		this.registerModelRenderer(rightPotato);

		rightGauntlet = new AnimatedModelRenderer(this);
		rightGauntlet.setRotationPoint(0.0F, 0.0F, 0.0F);
		rightArm.addChild(rightGauntlet);
		rightGauntlet.setTextureOffset(0, 43).addBox(-1.4688F, 6.0F, -2.0F, 4.0F, 4.0F, 4.0F, 0.4F, false);
		rightGauntlet.setModelRendererName("rightGauntlet");
		this.registerModelRenderer(rightGauntlet);

		leftArm = new AnimatedModelRenderer(this);
		leftArm.setRotationPoint(6.5F, -3.5F, 0.0F);
		chestplate.addChild(leftArm);
		leftArm.setTextureOffset(12, 47).addBox(-1.5F, -2.5F, -2.0F, 3.0F, 5.0F, 4.0F, 0.6F, false);
		leftArm.setTextureOffset(24, 24).addBox(-1.0F, 2.0F, -3.0F, 4.0F, 2.0F, 6.0F, 0.0F, false);
		leftArm.setModelRendererName("leftArm");
		this.registerModelRenderer(leftArm);

		leftPad = new AnimatedModelRenderer(this);
		leftPad.setRotationPoint(1.5F, -2.0F, 0.0F);
		leftArm.addChild(leftPad);
		setRotationAngle(leftPad, 0.0F, 0.0F, 0.1745F);
		leftPad.setTextureOffset(40, 19).addBox(-1.5F, -2.0F, -2.0F, 3.0F, 7.0F, 4.0F, 0.0F, false);
		leftPad.setModelRendererName("leftPad");
		this.registerModelRenderer(leftPad);

		leftPotato = new AnimatedModelRenderer(this);
		leftPotato.setRotationPoint(4.0F, -8.0F, 0.0F);
		leftArm.addChild(leftPotato);
		leftPotato.setTextureOffset(34, 0).addBox(-2.0F, -3.0F, -2.0F, 4.0F, 6.0F, 4.0F, 0.0F, false);
		leftPotato.setModelRendererName("leftPotato");
		this.registerModelRenderer(leftPotato);

		leftGauntlet = new AnimatedModelRenderer(this);
		leftGauntlet.setRotationPoint(0.0F, 0.0F, 0.0F);
		leftArm.addChild(leftGauntlet);
		leftGauntlet.setTextureOffset(42, 42).addBox(-2.5313F, 6.0F, -2.0F, 4.0F, 4.0F, 4.0F, 0.4F, false);
		leftGauntlet.setModelRendererName("leftGauntlet");
		this.registerModelRenderer(leftGauntlet);

		leggings = new AnimatedModelRenderer(this);
		leggings.setRotationPoint(0.0F, -8.0F, 0.0F);
		armor.addChild(leggings);

		leggings.setModelRendererName("leggings");
		this.registerModelRenderer(leggings);

		leftLeg = new AnimatedModelRenderer(this);
		leftLeg.setRotationPoint(0.0F, 8.0F, 0.0F);
		leggings.addChild(leftLeg);

		leftLeg.setModelRendererName("leftLeg");
		this.registerModelRenderer(leftLeg);

		leftBase = new AnimatedModelRenderer(this);
		leftBase.setRotationPoint(0.0F, 0.0F, 0.0F);
		leftLeg.addChild(leftBase);
		leftBase.setTextureOffset(16, 32).addBox(0.125F, -12.0F, -2.0F, 4.0F, 8.0F, 4.0F, 0.2F, false);
		leftBase.setModelRendererName("leftBase");
		this.registerModelRenderer(leftBase);

		rightLeg = new AnimatedModelRenderer(this);
		rightLeg.setRotationPoint(0.0F, 8.0F, 0.0F);
		leggings.addChild(rightLeg);

		rightLeg.setModelRendererName("rightLeg");
		this.registerModelRenderer(rightLeg);

		rightBase = new AnimatedModelRenderer(this);
		rightBase.setRotationPoint(0.0F, 0.0F, 0.0F);
		rightLeg.addChild(rightBase);
		rightBase.setTextureOffset(0, 31).addBox(-4.0938F, -12.0F, -2.0F, 4.0F, 8.0F, 4.0F, 0.2F, false);
		rightBase.setModelRendererName("rightBase");
		this.registerModelRenderer(rightBase);

		boots = new AnimatedModelRenderer(this);
		boots.setRotationPoint(0.0F, 0.0F, 0.0F);
		armor.addChild(boots);

		boots.setModelRendererName("boots");
		this.registerModelRenderer(boots);

		leftBoot = new AnimatedModelRenderer(this);
		leftBoot.setRotationPoint(0.0F, 0.0F, 0.0F);
		boots.addChild(leftBoot);
		leftBoot.setTextureOffset(50, 14).addBox(-3.6875F, -3.5938F, -2.0F, 3.0F, 3.0F, 4.0F, 0.7F, false);
		leftBoot.setModelRendererName("leftBoot");
		this.registerModelRenderer(leftBoot);

		leftFront = new AnimatedModelRenderer(this);
		leftFront.setRotationPoint(0.0F, 0.0F, 0.0F);
		leftBoot.addChild(leftFront);

		leftFront.setModelRendererName("leftFront");
		this.registerModelRenderer(leftFront);

		leftBone = new AnimatedModelRenderer(this);
		leftBone.setRotationPoint(4.8125F, -2.9688F, 2.5313F);
		leftBoot.addChild(leftBone);
		setRotationAngle(leftBone, 0.4565F, 0.8446F, -0.0193F);
		leftBone.setTextureOffset(54, 39).addBox(-1.5F, -0.5F, -2.0F, 2.0F, 2.0F, 4.0F, 0.0F, false);
		leftBone.setModelRendererName("leftBone");
		this.registerModelRenderer(leftBone);

		leftBone2 = new AnimatedModelRenderer(this);
		leftBone2.setRotationPoint(-0.5F, -0.5F, 3.0F);
		leftBone.addChild(leftBone2);
		setRotationAngle(leftBone2, -1.5272F, 0.0F, 0.0F);
		leftBone2.setTextureOffset(0, 0).addBox(-0.5F, -1.5F, -1.0F, 1.0F, 5.0F, 2.0F, 0.0F, false);
		leftBone2.setModelRendererName("leftBone2");
		this.registerModelRenderer(leftBone2);

		rightBoot = new AnimatedModelRenderer(this);
		rightBoot.setRotationPoint(0.0F, 0.0F, 0.0F);
		boots.addChild(rightBoot);
		rightBoot.setTextureOffset(50, 0).addBox(0.6875F, -3.5938F, -2.0F, 3.0F, 3.0F, 4.0F, 0.7F, false);
		rightBoot.setModelRendererName("rightBoot");
		this.registerModelRenderer(rightBoot);

		rightFront = new AnimatedModelRenderer(this);
		rightFront.setRotationPoint(0.0F, 0.0F, 0.0F);
		rightBoot.addChild(rightFront);

		rightFront.setModelRendererName("rightFront");
		this.registerModelRenderer(rightFront);

		rightBone = new AnimatedModelRenderer(this);
		rightBone.setRotationPoint(-4.0313F, -2.9688F, 3.0F);
		rightFront.addChild(rightBone);
		setRotationAngle(rightBone, 0.125F, -0.8437F, 0.3528F);
		rightBone.setTextureOffset(10, 56).addBox(-1.5F, -0.5F, -2.0F, 2.0F, 2.0F, 4.0F, 0.0F, false);
		rightBone.setModelRendererName("rightBone");
		this.registerModelRenderer(rightBone);

		rightBone2 = new AnimatedModelRenderer(this);
		rightBone2.setRotationPoint(-0.5F, -0.5F, 3.0F);
		rightBone.addChild(rightBone2);
		setRotationAngle(rightBone2, -1.5272F, 0.0F, 0.0F);
		rightBone2.setTextureOffset(24, 23).addBox(-0.5F, -1.5F, -1.0F, 1.0F, 5.0F, 2.0F, 0.0F, false);
		rightBone2.setModelRendererName("rightBone2");
		this.registerModelRenderer(rightBone2);

		this.rootBones.add(armor);
	}

	@Override
	public void setupArmor()
	{
		setHelmet(this.helmet);
		setChestPlate(this.chestplate, this.leftArm, this.rightArm);
		setLeggings(this.leftLeg, this.rightLeg);
		setBoots(this.leftBoot, this.rightBoot);
	}


	@Override
	public ResourceLocation getAnimationFileLocation(PotatoArmor armor)
	{
		return new ResourceLocation("geckolib", "animations/potato_armor.json");
	}
}