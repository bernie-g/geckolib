package software.bernie.geckolib.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.world.World;

public class TurretEntity extends MobEntity
{
	protected TurretEntity(EntityType<? extends MobEntity> type, World worldIn)
	{
		super(type, worldIn);
	}
}
