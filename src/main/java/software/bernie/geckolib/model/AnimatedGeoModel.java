package software.bernie.geckolib.model;

import com.eliotlash.molang.MolangParser;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import software.bernie.geckolib.core.IAnimatable;
import software.bernie.geckolib.core.IAnimatableModel;
import software.bernie.geckolib.core.builder.Animation;
import software.bernie.geckolib.core.event.predicate.AnimationEvent;
import software.bernie.geckolib.core.manager.AnimationData;
import software.bernie.geckolib.core.processor.AnimationProcessor;
import software.bernie.geckolib.core.processor.IBone;
import software.bernie.geckolib.geo.exception.GeoModelException;
import software.bernie.geckolib.geo.render.built.GeoBone;
import software.bernie.geckolib.geo.render.built.GeoModel;
import software.bernie.geckolib.model.provider.GeoModelProvider;
import software.bernie.geckolib.model.provider.IAnimatableModelProvider;
import software.bernie.geckolib.resource.GeckoLibCache;
import software.bernie.geckolib.util.MolangUtils;
import software.bernie.geckolib.util.VectorUtils;

import javax.annotation.Nullable;
import java.util.Collections;

public abstract class AnimatedGeoModel<T extends IAnimatable> extends GeoModelProvider<T> implements IAnimatableModel<T>, IAnimatableModelProvider<T>
{
	private final AnimationProcessor animationProcessor;
	private GeoModel currentModel;

	protected AnimatedGeoModel()
	{
		this.animationProcessor = new AnimationProcessor(this);
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
	public void setLivingAnimations(T entity, Integer uniqueID, @Nullable AnimationEvent customPredicate)
	{
		// Each animation has it's own collection of animations (called the EntityAnimationManager), which allows for multiple independent animations
		AnimationData manager = entity.getFactory().getOrCreateAnimationData(uniqueID);
		if (manager.startTick == null)
		{
			manager.startTick = getCurrentTick();
		}

		manager.tick = (getCurrentTick() - manager.startTick);
		double gameTick = manager.tick;
		double deltaTicks = gameTick - lastGameTickTime;
		seekTime += deltaTicks;
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
		animationProcessor.preAnimationSetup(predicate.getAnimatable(), seekTime);
		if (!this.animationProcessor.getModelRendererList().isEmpty())
		{
			animationProcessor.tickAnimation(entity, uniqueID, seekTime, predicate, GeckoLibCache.getInstance().parser, shouldCrashOnMissing);
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
		return GeckoLibCache.getInstance().getAnimations().get(this.getAnimationFileLocation((T) animatable)).getAnimation(name);
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

	@Override
	public void setMolangQueries(IAnimatable animatable, double currentTick)
	{
		MolangParser parser = GeckoLibCache.getInstance().parser;
		Minecraft minecraftInstance = Minecraft.getInstance();

		parser.setValue("query.actor_count", minecraftInstance.world.getCountLoadedEntities());
		parser.setValue("query.time_of_day", MolangUtils.normalizeTime(minecraftInstance.world.getDayTime()));
		parser.setValue("query.moon_phase", minecraftInstance.world.getMoonPhase());

		if (animatable instanceof Entity) {
			parser.setValue("query.distance_from_camera",
					minecraftInstance.gameRenderer.getActiveRenderInfo().getProjectedView()
							.distanceTo(((Entity) animatable).getPositionVec()));
			parser.setValue("query.is_on_ground", MolangUtils.booleanToFloat(((Entity) animatable).onGround));
			parser.setValue("query.is_in_water", MolangUtils.booleanToFloat(((Entity) animatable).isInWater()));
			//Should probably check specifically whether it's in rain?
			parser.setValue("query.is_in_water_or_rain", MolangUtils.booleanToFloat(((Entity) animatable).isInWaterRainOrBubbleColumn()));


			if (animatable instanceof LivingEntity) {
				LivingEntity livingEntity = (LivingEntity) animatable;
				parser.setValue("query.health", livingEntity.getHealth());
				parser.setValue("query.max_health", livingEntity.getMaxHealth());
				parser.setValue("query.is_on_fire", MolangUtils.booleanToFloat(livingEntity.isBurning()));
				//Doesn't work for some reason?
				parser.setValue("query.on_fire_time", livingEntity.getFireTimer());

				Vec3d velocity = livingEntity.getMotion();
				//Must be always positive to prevent NaNs
				float groundSpeed = MathHelper.sqrt(+velocity.x * +velocity.z);
				parser.setValue("query.ground_speed", groundSpeed);

				float yawSpeed = livingEntity.getYaw((float) currentTick) - livingEntity.getYaw((float) (currentTick - 0.1));
				parser.setValue("query.yaw_speed", yawSpeed);
			}
		}
	}
}
