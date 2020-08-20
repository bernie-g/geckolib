/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.geckolib.animation.model;

import com.eliotlash.mclib.math.Variable;
import com.eliotlash.molang.MolangParser;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib.animation.builder.Animation;
import software.bernie.geckolib.animation.processor.AnimationProcessor;
import software.bernie.geckolib.animation.processor.IBone;
import software.bernie.geckolib.animation.render.AnimatedModelRenderer;
import software.bernie.geckolib.event.EntityAnimationPredicate;
import software.bernie.geckolib.event.TileAnimationPredicate;
import software.bernie.geckolib.file.AnimationFileLoader;
import software.bernie.geckolib.file.IFileProvider;
import software.bernie.geckolib.reload.ReloadManager;
import software.bernie.geckolib.tesr.BlockAnimationManager;
import software.bernie.geckolib.tesr.ITileAnimatable;

import java.util.ArrayList;
import java.util.List;

/**
 * An AnimatedEntityModel is the equivalent of an Entity Model, except it provides extra functionality for rendering animations from bedrock json animation files. The entity passed into the generic parameter needs to implement IAnimatedEntity.
 *
 * @param <T> the type parameter
 */
public abstract class AnimatedBlockModel<T extends TileEntity & ITileAnimatable> extends Model implements IFileProvider, IResourceManagerReloadListener
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
	protected AnimatedBlockModel()
	{
		super(RenderType::getEntityCutoutNoCull);
		ReloadManager.registerModel(this);
		IReloadableResourceManager resourceManager = (IReloadableResourceManager) Minecraft.getInstance().getResourceManager();
		this.processor = new AnimationProcessor();
		this.loader = new AnimationFileLoader(this);
		registerMolangVariables();

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
		this.loader.onResourceManagerReload(resourceManager, parser);
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

	public void setLivingAnimations(T entity, float limbSwing, float limbSwingAmount, float partialTick)
	{
		// Each animation has it's own collection of animations (called the EntityAnimationManager), which allows for multiple independent animations
		BlockAnimationManager manager = entity.getAnimationManager();

		manager.tick = manager.ticksExisted + partialTick;
		double gameTick = manager.tick;
		double deltaTicks = gameTick - lastGameTickTime;
		seekTime += manager.getCurrentAnimationSpeed() * deltaTicks;
		lastGameTickTime = gameTick;

		TileAnimationPredicate<T> predicate = new TileAnimationPredicate<T>(entity, seekTime);
		processor.tickAnimation(entity, seekTime, predicate, parser);
	}


	public Animation getAnimation(String name)
	{
		return loader.getAnimation(name);
	}

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
}
