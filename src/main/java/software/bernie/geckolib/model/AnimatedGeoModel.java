package software.bernie.geckolib.model;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import software.bernie.geckolib.core.IAnimatable;
import software.bernie.geckolib.core.IAnimatableModel;
import software.bernie.geckolib.core.builder.Animation;
import software.bernie.geckolib.core.event.predicate.AnimationEvent;
import software.bernie.geckolib.core.manager.AnimationManager;
import software.bernie.geckolib.core.processor.AnimationProcessor;
import software.bernie.geckolib.core.processor.IBone;
import software.bernie.geckolib.geo.exception.GeoModelException;
import software.bernie.geckolib.geo.render.built.GeoBone;
import software.bernie.geckolib.geo.render.built.GeoModel;
import software.bernie.geckolib.model.provider.GeoModelProvider;
import software.bernie.geckolib.model.provider.IAnimatableModelProvider;
import software.bernie.geckolib.resource.GeckoLibCache;

import javax.annotation.Nullable;
import java.util.Collections;

public abstract class AnimatedGeoModel<T extends IAnimatable> extends GeoModelProvider<T> implements IAnimatableModel<T>, IAnimatableModelProvider<T>, IResourceManagerReloadListener
{
	private final AnimationProcessor animationProcessor;
	private GeoModel currentModel;

	protected AnimatedGeoModel()
	{
		this.animationProcessor = new AnimationProcessor();
		onResourceManagerReload(Minecraft.getInstance().getResourceManager());
		registerSelf();
	}


	private void registerSelf()
	{
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	public void onResourceManagerReload(IResourceManager resourceManager)
	{
		this.animationProcessor.reloadAnimations = true;
	}

	public void registerBone(GeoBone bone)
	{
		registerModelRenderer(bone);

		for (GeoBone childBone : bone.childBones)
		{
			registerBone(childBone);
		}
	}

	@Override
	public void setLivingAnimations(T entity, @Nullable AnimationEvent customPredicate)
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

		AnimationEvent<T> predicate;
		if (customPredicate == null)
		{
			predicate = new AnimationEvent<T>(entity, 0, 0, 0, false, Collections.emptyList());
		}
		else
		{
			predicate = customPredicate;
		}

		predicate.animationTick = seekTime;

		if (!this.animationProcessor.getModelRendererList().isEmpty())
		{
			animationProcessor.tickAnimation(entity, seekTime, predicate, GeckoLibCache.getInstance().parser, shouldCrashOnMissing);
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
	public Animation getAnimation(String name, IAnimatable animatable)
	{
		return GeckoLibCache.getInstance().animations.get(this.getAnimationFileLocation((T) animatable)).getAnimation(name);
	}

	@Override
	public GeoModel getModel(ResourceLocation location)
	{
		GeoModel model = super.getModel(location);
		if (model == null)
		{
			throw new GeoModelException(location, "Could not find model.");
		}
		if(model != currentModel)
		{
			this.animationProcessor.clearModelRendererList();
			for (GeoBone bone : model.topLevelBones)
			{
				registerBone(bone);
			}
			this.currentModel = model;
		}
		return model;
	}
}
