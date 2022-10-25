package software.bernie.example.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.world.World;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

public class TexturePerBoneTestEntity extends PathAwareEntity implements IAnimatable {
	private AnimationFactory factory = GeckoLibUtil.createFactory(this);

	public TexturePerBoneTestEntity(EntityType<? extends PathAwareEntity> entityType, World world) {
		super(entityType, world);
	}

	@Override
	public void registerControllers(AnimationData data) {

	}

	@Override
	public AnimationFactory getFactory() {
		return this.factory;
	}

}
