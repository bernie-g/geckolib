package software.bernie.geckolib.animation.model;

import net.minecraft.client.model.Model;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import java.util.function.Function;

public abstract class BaseAnimatedModel<T> extends Model
{
	public float swingProgress;
	public boolean isSitting;
	public boolean isChild = true;

	protected BaseAnimatedModel() {
		this(RenderLayer::getEntityCutoutNoCull);
	}

	protected BaseAnimatedModel(Function<Identifier, RenderLayer> p_i225945_1_) {
		super(p_i225945_1_);
	}

	/**
	 * Sets this entity's model rotation angles
	 */
	public abstract void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch);

	public void setLivingAnimations(T entityIn, int ticksExisted, float limbSwing, float limbSwingAmount, float partialTick) { }

}