/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.geckolib.model;

import com.eliotlash.mclib.math.Variable;
import com.eliotlash.molang.MolangParser;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import software.bernie.geckolib.animation.builder.Animation;
import software.bernie.geckolib.animation.processor.AnimationProcessor;
import software.bernie.geckolib.animation.processor.IBone;
import software.bernie.geckolib.animation.render.AnimatedModelRenderer;
import software.bernie.geckolib.entity.IAnimatable;
import software.bernie.geckolib.event.predicate.AnimationTestPredicate;
import software.bernie.geckolib.file.AnimationFile;
import software.bernie.geckolib.file.AnimationFileLoader;
import software.bernie.geckolib.manager.AnimationManager;
import software.bernie.geckolib.model.provider.IAnimatableModelProvider;
import software.bernie.geckolib.model.provider.IGenericModelProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public abstract class SpecialAnimatedModel<T extends IAnimatable> extends Model implements IAnimatableModelProvider<T>, IGenericModelProvider<T>, IResourceManagerReloadListener
{
	public List<AnimatedModelRenderer> rootBones = new ArrayList<>();
	public double seekTime;
	public double lastGameTickTime;
	private final AnimationProcessor processor;
	private final AnimationFileLoader loader;
	private final MolangParser parser = new MolangParser();
	public boolean crashWhenCantFindBone = true;

	private final LoadingCache<ResourceLocation, AnimationFile> animationCache = CacheBuilder.newBuilder().build(new CacheLoader<ResourceLocation, AnimationFile>()
	{
		@Override
		public AnimationFile load(ResourceLocation key)
		{
			SpecialAnimatedModel<T> model = SpecialAnimatedModel.this;
			return model.loader.loadAllAnimations(model.parser, key);
		}
	});

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


	protected SpecialAnimatedModel()
	{
		super(RenderType::getEntityCutoutNoCull);
		IReloadableResourceManager resourceManager = (IReloadableResourceManager) Minecraft.getInstance().getResourceManager();
		this.processor = new AnimationProcessor();
		this.loader = new AnimationFileLoader();
		registerMolangVariables();
		onResourceManagerReload(resourceManager);
		MinecraftForge.EVENT_BUS.register(this);
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
		this.animationCache.invalidateAll();
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

		AnimationTestPredicate<T> predicate = new AnimationTestPredicate<T>(entity, 0, 0, 0, false);
		processor.tickAnimation(entity, seekTime, predicate, parser, crashWhenCantFindBone);
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
		return this.processor;
	}


	@Override
	public void reload()
	{
		this.onResourceManagerReload(Minecraft.getInstance().getResourceManager());
	}
}
