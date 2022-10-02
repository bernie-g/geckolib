package software.bernie.example.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.world.World;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class TexturePerBoneTestEntity extends PathAwareEntity implements IAnimatable {

	public TexturePerBoneTestEntity(EntityType<? extends PathAwareEntity> p_i48575_1_, World p_i48575_2_) {
		super(p_i48575_1_, p_i48575_2_);
	}

	@Override
	public void registerControllers(AnimationData data) {
		
	}

	private AnimationFactory factory = new AnimationFactory(this);
	
	@Override
	public AnimationFactory getFactory() {
		return this.factory;
	}


}
