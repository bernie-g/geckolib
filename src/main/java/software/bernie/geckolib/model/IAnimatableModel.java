package software.bernie.geckolib.model;

import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.glfw.GLFW;
import software.bernie.geckolib.animation.builder.Animation;
import software.bernie.geckolib.animation.processor.AnimationProcessor;
import software.bernie.geckolib.animation.processor.IBone;
import software.bernie.geckolib.file.AnimationFileLoader;
import software.bernie.geckolib.listener.ClientListener;

public interface IAnimatableModel<T extends IResourceManagerReloadListener>
{
	/**
	 * This resource location needs to point to a json file of your animation file, i.e. "geckolib:animations/frog_animation.json"
	 *
	 * @return the animation file location
	 */
	ResourceLocation getAnimationFileLocation();


	default float getCurrentTick()
	{
		return (Util.milliTime() / 50f);
	}

	AnimationFileLoader getAnimationLoader();

	AnimationProcessor getAnimationProcessor();

	/**
	 * If animations should loop by default and ignore their pre-existing loop settings (that you can enable in blockbench by right clicking)
	 */
	default boolean isLoopByDefault()
	{
		return this.getAnimationLoader().isLoopByDefault();
	}

	/**
	 * If animations should loop by default and ignore their pre-existing loop settings (that you can enable in blockbench by right clicking)
	 */
	default void setLoopByDefault(boolean loopByDefault)
	{
		this.getAnimationLoader().setLoopByDefault(loopByDefault);
	}

	default Animation getAnimation(String name)
	{
		return this.getAnimationLoader().getAnimation(name);
	}

	/**
	 * Gets a bone by name.
	 *
	 * @param boneName The bone name
	 * @return the bone
	 */
	default IBone getBone(String boneName)
	{
		return this.getAnimationProcessor().getBone(boneName);
	}


	void reloadOnInputKey();
}
