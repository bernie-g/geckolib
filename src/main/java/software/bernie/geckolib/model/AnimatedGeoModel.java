package software.bernie.geckolib.model;

import com.eliotlash.mclib.math.Variable;
import com.eliotlash.molang.MolangParser;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.glfw.GLFW;
import software.bernie.geckolib.animation.builder.Animation;
import software.bernie.geckolib.animation.processor.AnimationProcessor;
import software.bernie.geckolib.animation.processor.IBone;
import software.bernie.geckolib.entity.IAnimatable;
import software.bernie.geckolib.event.predicate.AnimationTestPredicate;
import software.bernie.geckolib.file.AnimationFile;
import software.bernie.geckolib.file.AnimationFileLoader;
import software.bernie.geckolib.geo.render.built.GeoBone;
import software.bernie.geckolib.geo.render.built.GeoModel;
import software.bernie.geckolib.listener.ClientListener;
import software.bernie.geckolib.manager.AnimationManager;
import software.bernie.geckolib.model.provider.GeoModelProvider;
import software.bernie.geckolib.model.provider.IAnimatableModelProvider;
import software.bernie.geckolib.model.provider.IGenericModelProvider;

import javax.annotation.Nullable;
import java.util.concurrent.ExecutionException;

public abstract class AnimatedGeoModel<T extends IAnimatable> extends GeoModelProvider<T> implements IAnimatableModelProvider<T>, IGenericModelProvider<T>, IResourceManagerReloadListener
{
	private final AnimationFileLoader loader;
	private final MolangParser parser = new MolangParser();
	private final AnimationProcessor animationProcessor;

	private final LoadingCache<ResourceLocation, AnimationFile> animationCache = CacheBuilder.newBuilder().build(new CacheLoader<ResourceLocation, AnimationFile>()
	{
		@Override
		public AnimationFile load(ResourceLocation key)
		{
			AnimatedGeoModel<T> model = AnimatedGeoModel.this;
			return model.loader.loadAllAnimations(model.parser, key);
		}
	});

	protected AnimatedGeoModel()
	{
		this.genericModelProvider = this;
		this.loader = new AnimationFileLoader();
		this.animationProcessor = new AnimationProcessor();
		registerMolangVariables();
		onResourceManagerReload(Minecraft.getInstance().getResourceManager());
		registerSelf();
	}

	@Override
	public Animation getAnimation(String name, ResourceLocation location)
	{
		try
		{
			return this.animationCache.get(location).getAnimation(name);
		}
		catch (ExecutionException e)
		{
			throw new RuntimeException(e);
		}
	}

	@SubscribeEvent
	public void onReloadInput(InputEvent.KeyInputEvent inputKeyEvent)
	{
		if (inputKeyEvent.getAction() == GLFW.GLFW_RELEASE && inputKeyEvent.getKey() == ClientListener.reloadGeckoLibKeyBind.getKey().getKeyCode())
		{
			reload();
		}
	}

	private void registerSelf()
	{
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	public void onResourceManagerReload(IResourceManager resourceManager)
	{
		modelCache.invalidateAll();
		animationCache.invalidateAll();
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


	@Override
	public void setLivingAnimations(T entity, @Nullable AnimationTestPredicate customPredicate)
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

		AnimationTestPredicate<T> predicate;
		if (customPredicate == null)
		{
			predicate = new AnimationTestPredicate<T>(entity, 0, 0, 0, false);
		}
		else
		{
			predicate = customPredicate;
		}

		predicate.animationTick = seekTime;

		if (!this.animationProcessor.getModelRendererList().isEmpty())
		{
			animationProcessor.tickAnimation(entity, seekTime, predicate, parser, shouldCrashOnMissing);
		}
	}

	@Override
	public AnimationProcessor getAnimationProcessor()
	{
		return this.animationProcessor;
	}


	public void registerModelRenderer(IBone modelRenderer)
	{
		animationProcessor.registerModelRenderer(modelRenderer);
	}

	@Override
	public void reload()
	{
		this.onResourceManagerReload(Minecraft.getInstance().getResourceManager());
	}

	@Override
	public void reloadModel(GeoModel model)
	{
		animationProcessor.clearModelRendererList();
		for (GeoBone bone : model.topLevelBones)
		{
			registerBone(bone);
		}
	}
}
