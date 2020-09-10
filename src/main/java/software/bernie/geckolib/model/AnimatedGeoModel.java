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
import software.bernie.geckolib.event.predicate.SpecialAnimationPredicate;
import software.bernie.geckolib.file.AnimationFile;
import software.bernie.geckolib.file.AnimationFileLoader;
import software.bernie.geckolib.file.GeoModelLoader;
import software.bernie.geckolib.geo.render.built.GeoBone;
import software.bernie.geckolib.geo.render.built.GeoModel;
import software.bernie.geckolib.listener.ClientListener;
import software.bernie.geckolib.manager.AnimationManager;

import java.util.concurrent.ExecutionException;

public abstract class AnimatedGeoModel<T extends IAnimatable> implements IAnimatableModel<T>, IGeoModelProvider<T>, IResourceManagerReloadListener
{
	private final GeoModelLoader modelLoader;
	private final AnimationFileLoader loader;
	private final MolangParser parser = new MolangParser();
	private final AnimationProcessor processor;

	public double seekTime;
	public double lastGameTickTime;
	public boolean crashWhenCantFindBone = true;
	private boolean loopByDefault;

	private final LoadingCache<ResourceLocation, GeoModel> modelCache = CacheBuilder.newBuilder().build(new CacheLoader<ResourceLocation, GeoModel>()
	{
		@Override
		public GeoModel load(ResourceLocation key) throws Exception
		{
			GeoModel geoModel = AnimatedGeoModel.this.modelLoader.loadModel(Minecraft.getInstance().getResourceManager(), key);
			for (GeoBone bone : geoModel.topLevelBones)
			{
				registerBone(bone);
			}
			return geoModel;
		}
	});

	private final LoadingCache<ResourceLocation, AnimationFile> animationCache = CacheBuilder.newBuilder().build(new CacheLoader<ResourceLocation, AnimationFile>()
	{
		@Override
		public AnimationFile load(ResourceLocation key)
		{
			AnimatedGeoModel<T> model = AnimatedGeoModel.this;
			return model.loader.loadAllAnimations(model.parser, model.loopByDefault, key);
		}
	});


	protected AnimatedGeoModel()
	{
		this.modelLoader = new GeoModelLoader(this);
		this.loader = new AnimationFileLoader();
		this.processor = new AnimationProcessor();
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
		animationCache.invalidateAll();
		modelCache.invalidateAll();
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
	public AnimationProcessor getAnimationProcessor()
	{
		return this.processor;
	}

	@Override
	public GeoModel getModel(ResourceLocation location)
	{
		try
		{
			return this.modelCache.get(location);
		}
		catch (ExecutionException e)
		{
			throw new RuntimeException(e);
		}
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
