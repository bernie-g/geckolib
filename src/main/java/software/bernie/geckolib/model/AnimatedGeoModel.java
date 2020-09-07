package software.bernie.geckolib.model;

import com.eliotlash.mclib.math.Variable;
import com.eliotlash.molang.MolangParser;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.glfw.GLFW;
import software.bernie.geckolib.animation.processor.AnimationProcessor;
import software.bernie.geckolib.animation.processor.IBone;
import software.bernie.geckolib.entity.IAnimatable;
import software.bernie.geckolib.event.predicate.SpecialAnimationPredicate;
import software.bernie.geckolib.file.AnimationFileLoader;
import software.bernie.geckolib.file.GeoModelLoader;
import software.bernie.geckolib.geo.render.built.GeoBone;
import software.bernie.geckolib.geo.render.built.GeoModel;
import software.bernie.geckolib.listener.ClientListener;
import software.bernie.geckolib.manager.AnimationManager;

public abstract class AnimatedGeoModel<T extends IAnimatable> implements IAnimatableModel, IGeoModel, IResourceManagerReloadListener
{
	private final GeoModelLoader modelLoader;
	private final AnimationFileLoader animationFileLoader;
	private final MolangParser parser = new MolangParser();
	private final AnimationProcessor processor;

	public double seekTime;
	public double lastGameTickTime;
	public boolean crashWhenCantFindBone = true;

	protected AnimatedGeoModel()
	{
		this.modelLoader = new GeoModelLoader(this);
		this.animationFileLoader = new AnimationFileLoader(this);
		this.processor = new AnimationProcessor();
		registerMolangVariables();
		onResourceManagerReload(Minecraft.getInstance().getResourceManager());
		registerSelf();
	}

	@SubscribeEvent
	public void onReloadInput(InputEvent.KeyInputEvent inputKeyEvent)
	{
		if (inputKeyEvent.getAction() == GLFW.GLFW_RELEASE && inputKeyEvent.getKey() == ClientListener.reloadGeckoLibKeyBind.getKey().getKeyCode())
		{
			reloadOnInputKey();
		}
	}

	private void registerSelf()
	{
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	public void onResourceManagerReload(IResourceManager resourceManager)
	{
		modelLoader.loadModel(resourceManager);
		this.animationFileLoader.loadFile(resourceManager, parser);

		//this.processor.clearModelRendererList();
		for (GeoBone bone : modelLoader.getModel().topLevelBones)
		{
			registerBone(bone);
		}
	}

	public void registerBone(GeoBone bone)
	{
		registerModelRenderer(bone);
		for (GeoBone childBone : bone.childBones)
		{
			registerBone(childBone);
		}
	}

	private void registerMolangVariables()
	{
		parser.register(new Variable("query.anim_time", 0));
	}

	public void setLivingAnimations(T entity)
	{
		// Each animation has it's own collection of animations (called the EntityAnimationManager), which allows for multiple independent animations
		AnimationManager manager = entity.getAnimationManager();
		if (manager.startTick == null)
		{
			manager.startTick = getCurrentTick();
		}

		manager.tick = (getCurrentTick() - manager.startTick);
		double gameTick = manager.tick;
		double deltaTicks = gameTick - lastGameTickTime;
		seekTime += manager.getCurrentAnimationSpeed() * deltaTicks;
		lastGameTickTime = gameTick;

		SpecialAnimationPredicate<T> predicate = new SpecialAnimationPredicate<T>(entity, seekTime);
		if (!this.processor.getModelRendererList().isEmpty())
		{
			processor.tickAnimation(entity, seekTime, predicate, parser, crashWhenCantFindBone);
		}
	}

	@Override
	public AnimationFileLoader getAnimationLoader()
	{
		return this.animationFileLoader;
	}

	@Override
	public AnimationProcessor getAnimationProcessor()
	{
		return this.processor;
	}

	@Override
	public GeoModel getModel()
	{
		return this.modelLoader.getModel();
	}

	public void registerModelRenderer(IBone modelRenderer)
	{
		processor.registerModelRenderer(modelRenderer);
	}

	@Override
	public void reloadOnInputKey()
	{
		this.onResourceManagerReload(Minecraft.getInstance().getResourceManager());
	}

}
