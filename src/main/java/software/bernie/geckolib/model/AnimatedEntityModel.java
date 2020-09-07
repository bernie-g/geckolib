/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.geckolib.model;

import com.eliotlash.mclib.math.Variable;
import com.eliotlash.molang.MolangParser;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.resources.*;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import software.bernie.geckolib.animation.builder.Animation;
import software.bernie.geckolib.animation.processor.AnimationProcessor;
import software.bernie.geckolib.animation.processor.IBone;
import software.bernie.geckolib.event.predicate.EntityAnimationPredicate;
import software.bernie.geckolib.file.AnimationFileLoader;
import software.bernie.geckolib.manager.AnimationManager;
import software.bernie.geckolib.animation.render.AnimatedModelRenderer;
import software.bernie.geckolib.entity.IAnimatable;

import java.util.*;

/**
 * An AnimatedEntityModel is the equivalent of an Entity Model, except it provides extra functionality for rendering animations from bedrock json animation files. The entity passed into the generic parameter needs to implement IAnimatedEntity.
 *
 * @param <T> the type parameter
 */
public abstract class AnimatedEntityModel<T extends Entity & IAnimatable> extends EntityModel<T> implements IAnimatableModel, IResourceManagerReloadListener
{
	public List<AnimatedModelRenderer> rootBones = new ArrayList<>();
	public double seekTime;
	public double lastGameTickTime;
	private final AnimationProcessor processor;
	private final AnimationFileLoader loader;
	private final MolangParser parser = new MolangParser();

	/**
	 * Instantiates a new Animated entity model and loads the current animation file.
	 */
	protected AnimatedEntityModel()
	{
		super();
		IReloadableResourceManager resourceManager = (IReloadableResourceManager) Minecraft.getInstance().getResourceManager();
		this.processor = new AnimationProcessor();
		this.loader = new AnimationFileLoader(this);

		registerMolangVariables();
		MinecraftForge.EVENT_BUS.register(this);
		onResourceManagerReload(resourceManager);
	}

	private void registerMolangVariables()
	{
		parser.register(new Variable("query.anim_time", 0));
	}


	/**
	 * Internal method for handling reloads of animation files. Do not override.
	 */
	@Override
	public void onResourceManagerReload(IResourceManager resourceManager)
	{
		this.loader.loadFile(resourceManager, parser);
	}

	/**
	 * Gets a bone by name.
	 *
	 * @param boneName The bone name
	 * @return the bone
	 */
	public IBone getBone(String boneName)
	{
		return processor.getBone(boneName);
	}

	/**
	 * Register model renderer. Each AnimatedModelRenderer (group in blockbench) NEEDS to be registered via this method.
	 *
	 * @param modelRenderer The model renderer
	 */
	public void registerModelRenderer(IBone modelRenderer)
	{
		processor.registerModelRenderer(modelRenderer);
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
	public void setLivingAnimations(T entity, float limbSwing, float limbSwingAmount, float partialTick)
	{
		// Each animation has it's own collection of animations (called the EntityAnimationManager), which allows for multiple independent animations
		AnimationManager manager = entity.getAnimationManager();

		manager.tick = entity.ticksExisted + partialTick;
		double gameTick = manager.tick;
		double deltaTicks = gameTick - lastGameTickTime;
		seekTime += manager.getCurrentAnimationSpeed() * deltaTicks;
		lastGameTickTime = gameTick;

		EntityAnimationPredicate<T> predicate = new EntityAnimationPredicate<T>(entity, seekTime, limbSwing,
				limbSwingAmount, partialTick, !(limbSwingAmount > -0.15F && limbSwingAmount < 0.15F));

		processor.tickAnimation(entity, seekTime, predicate, parser, true);
	}

	@Override
	public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) { }

	public Animation getAnimation(String name)
	{
		return loader.getAnimation(name);
	}

	@Override
	public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha)
	{
		for (AnimatedModelRenderer model : rootBones)
		{
			model.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		}
	}

	/**
	 * If animations should loop by default and ignore their pre-existing loop settings (that you can enable in blockbench by right clicking)
	 */
	public boolean isLoopByDefault()
	{
		return loader.isLoopByDefault();
	}

	/**
	 * If animations should loop by default and ignore their pre-existing loop settings (that you can enable in blockbench by right clicking)
	 */
	public void setLoopByDefault(boolean loopByDefault)
	{
		this.loader.setLoopByDefault(loopByDefault);
	}


	@Override
	public ResourceLocation getAnimationFileLocation()
	{
		return this.getAnimationFileLocation();
	}

	@Override
	public AnimationFileLoader getAnimationLoader()
	{
		return this.getAnimationLoader();
	}

	@Override
	public AnimationProcessor getAnimationProcessor()
	{
		return this.processor;
	}

	@Override
	public void reloadOnInputKey()
	{
		this.onResourceManagerReload(Minecraft.getInstance().getResourceManager());
	}
}
