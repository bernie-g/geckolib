package anightdazingzoroark.riftlib.projectile;

import anightdazingzoroark.riftlib.core.IAnimatable;
import anightdazingzoroark.riftlib.core.manager.AnimationData;
import anightdazingzoroark.riftlib.core.manager.AnimationFactory;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MultiPartEntityPart;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public abstract class RiftLibProjectile extends EntityArrow implements IAnimatable {
    private final AnimationFactory factory = new AnimationFactory(this);

    public RiftLibProjectile(World worldIn) {
        super(worldIn);
    }

    public RiftLibProjectile(World worldIn, double x, double y, double z) {
        this(worldIn);
        this.setPosition(x, y, z);
    }

    public RiftLibProjectile(World worldIn, EntityLivingBase shooter) {
        this(worldIn, shooter.posX, shooter.posY + (double)shooter.getEyeHeight() - 0.10000000149011612D, shooter.posZ);
        this.shootingEntity = shooter;
    }

    protected void onHit(RayTraceResult raytraceResultIn) {
        Entity entity = raytraceResultIn.entityHit;
        BlockPos blockPos = raytraceResultIn.getBlockPos();

        if (!this.world.isRemote) {
            if (entity != null && entity != this.shootingEntity) {
                float f = MathHelper.sqrt(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
                int i = MathHelper.ceil((double) f * this.getDamage());

                if (this.getIsCritical()) i += this.rand.nextInt(i / 2 + 2);

                DamageSource damagesource;

                if (this.shootingEntity == null) damagesource = DamageSource.causeArrowDamage(this, this);
                else damagesource = DamageSource.causeArrowDamage(this, this.shootingEntity);

                if (entity instanceof MultiPartEntityPart && ((MultiPartEntityPart) entity).parent instanceof EntityLivingBase) {
                    EntityLivingBase parent = (EntityLivingBase)(((MultiPartEntityPart) entity).parent);
                    this.projectileEntityEffects(parent);
                }
                else if (entity instanceof EntityLivingBase) {
                    EntityLivingBase entitylivingbase = (EntityLivingBase) entity;
                    this.projectileEntityEffects(entitylivingbase);
                }

                if (entity.attackEntityFrom(damagesource, (float) i)) {
                    this.playSound(this.getOnProjectileHitSound(), 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
                    if (this.canSelfDestroyUponHit()) this.setDead();
                }
                else {
                    this.motionX *= -0.10000000149011612D;
                    this.motionY *= -0.10000000149011612D;
                    this.motionZ *= -0.10000000149011612D;
                    this.rotationYaw += 180.0F;
                    this.prevRotationYaw += 180.0F;
                    if (this.canSelfDestroyUponHit()) this.setDead();
                }
            }
            else if (blockPos != null) {
                this.xTile = blockPos.getX();
                this.yTile = blockPos.getY();
                this.zTile = blockPos.getZ();
                IBlockState iblockstate = this.world.getBlockState(blockPos);
                this.inTile = iblockstate.getBlock();
                this.inData = this.inTile.getMetaFromState(iblockstate);
                this.motionX = raytraceResultIn.hitVec.x - this.posX;
                this.motionY = raytraceResultIn.hitVec.y - this.posY;
                this.motionZ = raytraceResultIn.hitVec.z - this.posZ;
                float f2 = MathHelper.sqrt(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
                this.posX -= this.motionX / (double)f2 * 0.05000000074505806D;
                this.posY -= this.motionY / (double)f2 * 0.05000000074505806D;
                this.posZ -= this.motionZ / (double)f2 * 0.05000000074505806D;
                this.playSound(this.getOnProjectileHitSound(), 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
                this.inGround = true;
                this.setIsCritical(false);

                if (iblockstate.getMaterial() != Material.AIR) this.inTile.onEntityCollision(this.world, blockPos, iblockstate, this);
                this.projectileEntityEffects(null);
                if (this.canSelfDestroyUponHit()) this.setDead();
            }
        }
        else super.onHit(raytraceResultIn);
    }

    public abstract void projectileEntityEffects(EntityLivingBase entityLivingBase);

    public boolean canSelfDestroyUponHit() {
        return true;
    }

    public boolean canRotateToAimDirection() {
        return true;
    }

    public abstract double getDamage();

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }

    @Override
    public abstract void registerControllers(AnimationData data);

    public abstract SoundEvent getOnProjectileHitSound();

    @Override
    protected ItemStack getArrowStack() {
        return null;
    }
}
