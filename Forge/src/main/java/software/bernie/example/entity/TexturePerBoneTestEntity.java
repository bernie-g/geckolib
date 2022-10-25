package software.bernie.example.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

public class TexturePerBoneTestEntity extends PathfinderMob implements IAnimatable {

	public TexturePerBoneTestEntity(EntityType<? extends PathfinderMob> p_i48575_1_, Level p_i48575_2_) {
		super(p_i48575_1_, p_i48575_2_);
	}

	@Override
	public void registerControllers(AnimationData data) {
		
	}

	private AnimationFactory factory = GeckoLibUtil.createFactory(this);
	
	@Override
	public AnimationFactory getFactory() {
		return this.factory;
	}


}
