package software.bernie.example.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3q.util.GeckoLibUtil;

public class TexturePerBoneTestEntity extends PathfinderMob implements IAnimatable {
	private AnimationFactory factory = GeckoLibUtil.createFactory(this);

	public TexturePerBoneTestEntity(EntityType<? extends PathfinderMob> entityType, Level level) {
		super(entityType, level);
	}

	@Override
	public void registerControllers(AnimationData data) {

	}

	@Override
	public AnimationFactory getFactory() {
		return this.factory;
	}

}
