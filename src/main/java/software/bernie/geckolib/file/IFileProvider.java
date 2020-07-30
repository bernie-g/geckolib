package software.bernie.geckolib.file;

import net.minecraft.util.ResourceLocation;

public interface IFileProvider
{
	/**
	 * This resource location needs to point to a json file of your animation file, i.e. "geckolib:animations/frog_animation.json"
	 *
	 * @return the animation file location
	 */
	ResourceLocation getAnimationFileLocation();
}
