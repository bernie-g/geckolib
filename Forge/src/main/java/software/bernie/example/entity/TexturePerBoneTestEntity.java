package software.bernie.example.entity;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.network.IPacket;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class TexturePerBoneTestEntity extends CreatureEntity implements IAnimatable {

	public TexturePerBoneTestEntity(EntityType<? extends CreatureEntity> p_i48575_1_, World p_i48575_2_) {
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
	
	@Override
	public IPacket<?> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}


}
