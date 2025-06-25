package anightdazingzoroark.example.entity;

import anightdazingzoroark.riftlib.core.manager.AnimationData;
import anightdazingzoroark.riftlib.projectile.RiftLibProjectile;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public class BombProjectile extends RiftLibProjectile {
    public BombProjectile(World worldIn) {
        super(worldIn);
    }

    public BombProjectile(World worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
    }

    public BombProjectile(World worldIn, EntityLivingBase shooter) {
        super(worldIn, shooter);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
    }

    @Override
    public void projectileEntityEffects(EntityLivingBase entityLivingBase) {
        this.world.createExplosion(this, this.posX, this.posY, this.posZ, 4f, true);
    }

    @Override
    public double getDamage() {
        return 0f;
    }

    @Override
    public void registerControllers(AnimationData data) {

    }

    @Override
    public SoundEvent getOnProjectileHitSound() {
        return null;
    }
}
