// Made with Blockbench 3.5.4
// Exported for Minecraft version 1.12.2 or 1.15.2 (same format for both) for entity models animated with GeckoLib
// Paste this class into your mod and follow the documentation for GeckoLib to use animations. You can find the documentation here: https://github.com/bernie-g/geckolib
// Blockbench plugin created by Gecko
package software.bernie.geckolib.example.client.renderer.model;

import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib.animation.model.AnimatedEntityModel;
import software.bernie.geckolib.animation.render.AnimatedModelRenderer;
import software.bernie.geckolib.example.entity.EasingDemoEntity;

public class EasingDemoModel extends AnimatedEntityModel<EasingDemoEntity>
{

	private final AnimatedModelRenderer bone;

	public EasingDemoModel()
	{
		textureWidth = 16;
		textureHeight = 16;
		bone = new AnimatedModelRenderer(this);
		bone.setRotationPoint(0.0F, 24.0F, 0.0F);
		bone.setTextureOffset(0, 0).addBox(-1.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, 0.0F, false);
		bone.setModelRendererName("bone");
		this.registerModelRenderer(bone);

		this.rootBones.add(bone);
	}


	@Override
	public ResourceLocation getAnimationFileLocation()
	{
		return new ResourceLocation("geckolib", "animations/easing_demo.json");
	}
}