/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.geckolib.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraftforge.common.MinecraftForge;
import software.bernie.geckolib.core.IAnimatable;
import software.bernie.geckolib.core.IAnimatableModel;
import software.bernie.geckolib.core.builder.Animation;
import software.bernie.geckolib.core.controller.AnimationController;
import software.bernie.geckolib.core.event.predicate.AnimationTestPredicate;
import software.bernie.geckolib.core.manager.AnimationManager;
import software.bernie.geckolib.core.processor.AnimationProcessor;
import software.bernie.geckolib.core.processor.IBone;
import software.bernie.geckolib.model.provider.IAnimatableModelProvider;
import software.bernie.geckolib.model.provider.IGenericModelProvider;
import software.bernie.geckolib.renderers.legacy.AnimatedModelRenderer;
import software.bernie.geckolib.resource.GeckoLibCache;
import software.bernie.geckolib.util.AnimationUtils;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * An AnimatedEntityModel is the equivalent of an Entity Model, except it provides extra functionality for rendering animations from bedrock json animation files. The entity passed into the generic parameter needs to implement IAnimatedEntity.
 *
 * @param <T> the type parameter
 */
@Deprecated
public abstract class AnimatedEntityModel<T extends Entity & IAnimatable> extends EntityModel<T> implements IAnimatableModel<T>, IAnimatableModelProvider<T>, IGenericModelProvider<T>, IResourceManagerReloadListener
{
	static
	{
		AnimationController.addModelFetcher((Object object) ->
		{
			if (object instanceof Entity)
			{
				return AnimationUtils.getModelForEntity((Entity) object);
			}
			return null;
		});
	}

	public List<AnimatedModelRenderer> rootBones = new ArrayList<>();
	public double seekTime;
	public double lastGameTickTime;
	private final AnimationProcessor animationProcessor;

	/**
	 * Instantiates a new Animated entity model.
	 */
	protected AnimatedEntityModel()
	{
		super();
		IReloadableResourceManager resourceManager = (IReloadableResourceManager) Minecraft.getInstance().getResourceManager();
		this.animationProcessor = new AnimationProcessor();

		MinecraftForge.EVENT_BUS.register(this);
		onResourceManagerReload(resourceManager);
	}


	@Override
	public Animation getAnimation(String name, IAnimatable animatable)
	{
		return GeckoLibCache.getInstance().animations.get(this.getAnimationFileLocation((T) animatable)).getAnimation(name);
	}

	/**
	 * Internal method for handling reloads of animation files. Do not override.
	 */
	@Override
	public void onResourceManagerReload(IResourceManager resourceManager)
	{
		this.animationProcessor.reloadAnimations = true;
	}

	/**
	 * Gets a bone by name.
	 *
	 * @param boneName The bone name
	 * @return the bone
	 */
	public IBone getBone(String boneName)
	{
		return animationProcessor.getBone(boneName);
	}

	/**
	 * Register model renderer. Each AnimatedModelRenderer (group in blockbench) NEEDS to be registered via this method.
	 *
	 * @param modelRenderer The model renderer
	 */
	public void registerModelRenderer(IBone modelRenderer)
	{
		animationProcessor.registerModelRenderer(modelRenderer);
	}


	/**
	 * Sets a rotation angle.
	 *
	 * @param modelRenderer The animated model renderer
	 * @param x             x
	 * @param y             y
	 * @param z             z
	 */
	public void setRotationAngle(AnimatedModelRenderer modelRenderer, float x, float y, float z)
	{
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}

	@Override
	public void setLivingAnimations(T entity, @Nullable AnimationTestPredicate customPredicate)
	{
		this.setLivingAnimations(entity, customPredicate.getLimbSwing(), customPredicate.getLimbSwingAmount(), customPredicate.getPartialTick());
	}

	@Override
	public void setLivingAnimations(T entity, float limbSwing, float limbSwingAmount, float partialTick)
	{
		// Each animation has it's own collection of animations (called the EntityAnimationManager), which allows for multiple independent animations
		AnimationManager manager = entity.getAnimationManager();

		manager.tick = entity.ticksExisted + partialTick;
		double gameTick = manager.tick;
		double deltaTicks = gameTick - lastGameTickTime;
		seekTime += manager.getCurrentAnimationSpeed() * deltaTicks;
		lastGameTickTime = gameTick;

		AnimationTestPredicate<T> predicate = new AnimationTestPredicate<T>(entity, limbSwing,
				limbSwingAmount, partialTick, !(limbSwingAmount > -0.15F && limbSwingAmount < 0.15F));
		predicate.animationTick = seekTime;
		animationProcessor.tickAnimation(entity, seekTime, predicate, GeckoLibCache.getInstance().parser, true);
	}

	@Override
	public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch)
	{
	}


	@Override
	public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha)
	{
		for (AnimatedModelRenderer model : rootBones)
		{
			model.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		}
	}


	@Override
	public AnimationProcessor getAnimationProcessor()
	{
		return this.animationProcessor;
	}

	@Override
	public void reload()
	{
		this.onResourceManagerReload(Minecraft.getInstance().getResourceManager());
	}
}
