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
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraftforge.common.MinecraftForge;
import software.bernie.geckolib.core.builder.Animation;
import software.bernie.geckolib.core.processor.AnimationProcessor;
import software.bernie.geckolib.core.processor.IBone;
import software.bernie.geckolib.renderers.legacy.AnimatedModelRenderer;
import software.bernie.geckolib.core.IAnimatable;
import software.bernie.geckolib.core.event.predicate.AnimationTestPredicate;
import software.bernie.geckolib.file.AnimationFile;
import software.bernie.geckolib.file.AnimationFileLoader;
import software.bernie.geckolib.item.AnimatedArmorItem;
import software.bernie.geckolib.core.manager.AnimationManager;
import software.bernie.geckolib.core.IAnimatableModel;
import software.bernie.geckolib.model.provider.IAnimatableModelProvider;
import software.bernie.geckolib.model.provider.IGenericModelProvider;
import software.bernie.geckolib.resource.GeckoLibCache;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * An AnimatedEntityModel is the equivalent of an Entity Model, except it provides extra functionality for rendering animations from bedrock json animation files. The entity passed into the generic parameter needs to implement IAnimatedEntity.
 *
 * @param <T> the type parameter
 */
public abstract class AnimatedArmorModel<T extends AnimatedArmorItem & IAnimatable> extends BipedModel implements IAnimatableModel<T>, IAnimatableModelProvider<T>, IGenericModelProvider<T>, IResourceManagerReloadListener
{
	public List<AnimatedModelRenderer> rootBones = new ArrayList<>();
	public double seekTime;
	public double lastGameTickTime;
	private final AnimationProcessor animationProcessor;
	private final MolangParser parser = new MolangParser();

	private AnimatedModelRenderer helmetRenderer;
	private AnimatedModelRenderer chestplateRenderer;

	private AnimatedModelRenderer leftArmRenderer;
	private AnimatedModelRenderer rightArmRenderer;

	private AnimatedModelRenderer leftLegRenderer;
	private AnimatedModelRenderer rightLegRenderer;

	private AnimatedModelRenderer leftBootRenderer;
	private AnimatedModelRenderer rightBootRenderer;

	private boolean hasSetup = false;


	/**
	 * Instantiates a new Animated entity model and loads the current animation file.
	 */
	protected AnimatedArmorModel()
	{
		super(1);
		IReloadableResourceManager resourceManager = (IReloadableResourceManager) Minecraft.getInstance().getResourceManager();
		this.animationProcessor = new AnimationProcessor();

		onResourceManagerReload(resourceManager);
		MinecraftForge.EVENT_BUS.register((IAnimatableModel) this);
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

		AnimationTestPredicate<T> predicate = new AnimationTestPredicate<T>(entity, 0, 0, 0, false);
		animationProcessor.tickAnimation(entity, seekTime, predicate, parser, true);
	}

	@Override
	public Animation getAnimation(String name, IAnimatable animatable)
	{
		return GeckoLibCache.getInstance().animations.get(this.getAnimationFileLocation((T) animatable)).getAnimation(name);
	}

	@Override
	public void render(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha)
	{
		copyModelAngles(this.bipedHead, this.helmetRenderer);
		copyModelAngles(this.bipedBody, this.chestplateRenderer);
		copyModelAngles(this.bipedRightArm, this.rightArmRenderer);
		copyModelAngles(this.bipedLeftArm, this.leftArmRenderer);
		copyModelAngles(this.bipedRightLeg, this.rightLegRenderer);
		copyModelAngles(this.bipedLeftLeg, this.leftLegRenderer);
		copyModelAngles(this.bipedRightLeg, this.rightBootRenderer);
		copyModelAngles(this.bipedLeftLeg, this.leftBootRenderer);

		matrixStack.push();
		if (isSneak)
		{
			matrixStack.translate(0, 0.2, 0);
		}
		for (AnimatedModelRenderer model : rootBones)
		{
			model.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		}
		matrixStack.pop();
	}

	public float getCurrentTick()
	{
		return (Util.milliTime() / 50f);
	}

	private final void copyModelAngles(ModelRenderer in, ModelRenderer out)
	{
		out.rotateAngleX = in.rotateAngleX;
		out.rotateAngleY = in.rotateAngleY;
		out.rotateAngleZ = in.rotateAngleZ;
	}

	public BipedModel applySlot(EquipmentSlotType slot)
	{
		if (!hasSetup)
		{
			setupArmor();
			hasSetup = true;
		}

		helmetRenderer.showModel = false;
		chestplateRenderer.showModel = false;
		rightArmRenderer.showModel = false;
		leftArmRenderer.showModel = false;
		rightLegRenderer.showModel = false;
		leftLegRenderer.showModel = false;
		rightBootRenderer.showModel = false;
		leftBootRenderer.showModel = false;

		switch (slot)
		{
			case HEAD:
				helmetRenderer.showModel = true;
				break;
			case CHEST:
				chestplateRenderer.showModel = true;
				rightArmRenderer.showModel = true;
				leftArmRenderer.showModel = true;
				break;
			case LEGS:
				rightLegRenderer.showModel = true;
				leftLegRenderer.showModel = true;
				break;
			case FEET:
				rightBootRenderer.showModel = true;
				leftBootRenderer.showModel = true;
				break;
			default:
				break;
		}
		return this;
	}

	public void setHelmet(AnimatedModelRenderer helmetRenderer)
	{
		this.helmetRenderer = helmetRenderer;
	}

	public void setChestPlate(AnimatedModelRenderer chestPlate, AnimatedModelRenderer leftArm, AnimatedModelRenderer rightArm)
	{
		this.chestplateRenderer = chestPlate;
		this.leftArmRenderer = leftArm;
		this.rightArmRenderer = rightArm;
	}

	public void setLeggings(AnimatedModelRenderer leftLeg, AnimatedModelRenderer rightLeg)
	{
		this.leftLegRenderer = leftLeg;
		this.rightLegRenderer = rightLeg;
	}

	public void setBoots(AnimatedModelRenderer leftBoot, AnimatedModelRenderer rightBoot)
	{
		this.leftBootRenderer = leftBoot;
		this.rightBootRenderer = rightBoot;
	}

	public abstract void setupArmor();

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
