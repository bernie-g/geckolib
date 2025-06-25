package anightdazingzoroark.example.entity;

import anightdazingzoroark.riftlib.core.IAnimatable;
import anightdazingzoroark.riftlib.core.PlayState;
import anightdazingzoroark.riftlib.core.builder.AnimationBuilder;
import anightdazingzoroark.riftlib.core.controller.AnimationController;
import anightdazingzoroark.riftlib.core.event.predicate.AnimationEvent;
import anightdazingzoroark.riftlib.core.manager.AnimationData;
import anightdazingzoroark.riftlib.core.manager.AnimationFactory;
import anightdazingzoroark.riftlib.hitboxLogic.IMultiHitboxUser;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityFlying;
import net.minecraft.world.World;

public class FlyingPufferfishEntity extends EntityFlying implements IAnimatable, IMultiHitboxUser {
    private AnimationFactory factory = new AnimationFactory(this);
    private Entity[] hitboxes = {};

    public FlyingPufferfishEntity(World worldIn) {
        super(worldIn);
        this.setSize(1f, 1f);
        this.initializeHitboxes(this);
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        this.updateParts();
    }

    //hitbox stuff starts here
    @Override
    public Entity getMultiHitboxUser() {
        return this;
    }

    @Override
    public Entity[] getParts() {
        return this.hitboxes;
    }

    @Override
    public void setParts(Entity[] hitboxes) {
        this.hitboxes = hitboxes;
    }

    @Override
    public World getWorld() {
        return this.world;
    }
    //hitbox stuff ends here


    @Override
    public float scale() {
        return 2f;
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController(this, "puff", 0, new AnimationController.IAnimationPredicate() {
            @Override
            public PlayState test(AnimationEvent event) {
                event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.flying_pufferfish.inflate_loop", true));
                return PlayState.CONTINUE;
            }
        }));
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }
}
