package software.bernie.example.entity;

import java.util.List;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import software.bernie.example.ClientListener;
import software.bernie.example.registry.EntityRegistry;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.ILoopType.EDefaultLoopTypes;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3q.util.GeckoLibUtil;

public class RocketProjectile extends AbstractArrow implements IAnimatable {
	protected int timeInAir;
	protected boolean inAir;
	private int ticksInAir;
	private LivingEntity shooter;

	private AnimationFactory factory = GeckoLibUtil.createFactory(this);

	public RocketProjectile(EntityType<? extends RocketProjectile> entityType, Level world) {
		super(entityType, world);
		this.pickup = AbstractArrow.Pickup.DISALLOWED;
	}

	public RocketProjectile(Level world, LivingEntity owner) {
		super(EntityRegistry.ROCKET, owner, world);
		this.shooter = owner;
	}

	protected RocketProjectile(EntityType<? extends RocketProjectile> type, double x, double y, double z, Level world) {
		this(type, world);
	}

	protected RocketProjectile(EntityType<? extends RocketProjectile> type, LivingEntity owner, Level world) {
		this(type, owner.getX(), owner.getEyeY() - 0.10000000149011612D, owner.getZ(), world);
		this.setOwner(owner);
		if (owner instanceof Player) {
			this.pickup = AbstractArrow.Pickup.ALLOWED;
		}

	}

	private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
		event.getController().setAnimation(new AnimationBuilder().addAnimation("idle", EDefaultLoopTypes.LOOP));
		return PlayState.CONTINUE;
	}

	@Override
	public void registerControllers(AnimationData data) {
		data.addAnimationController(new AnimationController<RocketProjectile>(this, "controller", 0, this::predicate));
	}

	@Override
	public AnimationFactory getFactory() {
		return this.factory;
	}

	@Override
	public Packet<?> getAddEntityPacket() {
		return ClientListener.EntityPacket.createPacket(this);
	}

	@Override
	public void tickDespawn() {
		++this.ticksInAir;
		if (this.ticksInAir >= 40) {
			this.remove(Entity.RemovalReason.DISCARDED);
		}
	}

	@Override
	protected void doPostHurtEffects(LivingEntity living) {
		super.doPostHurtEffects(living);
		if (!(living instanceof Player)) {
			living.setDeltaMovement(0, 0, 0);
			living.invulnerableTime = 0;
		}
	}

	@Override
	public void shoot(double x, double y, double z, float speed, float divergence) {
		super.shoot(x, y, z, speed, divergence);
		this.ticksInAir = 0;
	}

	@Override
	public void addAdditionalSaveData(CompoundTag tag) {
		super.addAdditionalSaveData(tag);
		tag.putShort("life", (short) this.ticksInAir);
	}

	@Override
	public void readAdditionalSaveData(CompoundTag tag) {
		super.readAdditionalSaveData(tag);
		this.ticksInAir = tag.getShort("life");
	}

	public void initFromStack(ItemStack stack) {
		if (stack.getItem() == Items.AIR) {
		}
	}

	@Override
	public boolean isNoGravity() {
		if (this.isUnderWater()) {
			return false;
		} else {
			return true;
		}
	}

	public SoundEvent hitSound = this.getDefaultHitGroundSoundEvent();

	@Override
	public void setSoundEvent(SoundEvent soundIn) {
		this.hitSound = soundIn;
	}

	@Override
	protected SoundEvent getDefaultHitGroundSoundEvent() {
		return SoundEvents.GENERIC_EXPLODE;
	}

	@Override
	protected void onHitBlock(BlockHitResult blockHitResult) {
		super.onHitBlock(blockHitResult);
		if (!this.level.isClientSide) {
			this.doDamage();
			this.level.explode(this, this.getX(), this.getY(0.0625D), this.getZ(), 0.0F,
					Explosion.BlockInteraction.BREAK);
			this.remove(Entity.RemovalReason.DISCARDED);
		}
		this.setSoundEvent(SoundEvents.GENERIC_EXPLODE);
	}

	@Override
	protected void onHitEntity(EntityHitResult entityHitResult) {
		if (!this.level.isClientSide) {
			this.doDamage();
			this.level.explode(this, this.getX(), this.getY(0.0625D), this.getZ(), 0.0F,
					Explosion.BlockInteraction.BREAK);
			this.remove(Entity.RemovalReason.DISCARDED);
		}
	}

	@Override
	protected ItemStack getPickupItem() {
		return new ItemStack(Items.AIR);
	}

	@Override
	@Environment(EnvType.CLIENT)
	public boolean shouldRenderAtSqrDistance(double distance) {
		return true;
	}

	public void doDamage() {
		float q = 4.0F;
		int k = Mth.floor(this.getX() - (double) q - 1.0D);
		int l = Mth.floor(this.getX() + (double) q + 1.0D);
		int t = Mth.floor(this.getY() - (double) q - 1.0D);
		int u = Mth.floor(this.getY() + (double) q + 1.0D);
		int v = Mth.floor(this.getZ() - (double) q - 1.0D);
		int w = Mth.floor(this.getZ() + (double) q + 1.0D);
		List<Entity> list = this.level.getEntities(this,
				new AABB((double) k, (double) t, (double) v, (double) l, (double) u, (double) w));
		Vec3 vec3d = new Vec3(this.getX(), this.getY(), this.getZ());
		for (int x = 0; x < list.size(); ++x) {
			Entity entity = (Entity) list.get(x);
			double y = (double) (Mth.sqrt((float) entity.distanceToSqr(vec3d)) / q);
			if (y <= 1.0D) {
				if (entity instanceof LivingEntity) {
					entity.hurt(DamageSource.playerAttack((Player) this.shooter), 20);
				}
				this.level.explode(this, this.getX(), this.getY(0.0625D), this.getZ(), 0.0F,
						Explosion.BlockInteraction.NONE);
			}
		}
	}

}
