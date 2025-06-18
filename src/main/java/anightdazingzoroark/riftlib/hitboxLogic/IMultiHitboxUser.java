package anightdazingzoroark.riftlib.hitboxLogic;

import anightdazingzoroark.riftlib.core.IAnimatable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IEntityMultiPart;
import net.minecraft.entity.MultiPartEntityPart;
import net.minecraft.util.DamageSource;

public interface IMultiHitboxUser extends IEntityMultiPart {
    //get the parent
    //must always return the entity its being implemented in
    //so its return statement in the entity implementing this should be "return this;"
    Entity getMultiHitboxUser();

    //this must be placed in the constructor of the entity
    //and must be the entity itself being entered
    default void initializeHitboxes(Entity entity) {
        //todo: add logic here somehow that makes it so that it gets hitboxes from the hitbox file
        //and positions from the model file to make the hitboxes
    }

    //an array of hitboxes associated with the entity is to be created
    Entity[] getParts();

    void setParts(Entity[] hitboxes);

    //this is to be placed in a method like onUpdate() or onLivingUpdate()
    //to update all hitboxes every tick
    default void updateParts() {
        for (Entity entity : this.getParts()) {
            if (entity instanceof EntityHitbox) {
                entity.onUpdate();
                ((EntityHitbox) entity).resize(((IAnimatable) this.getMultiHitboxUser()).scale());
            }
        }
    }

    //this is for dealing with damage multipliers from attacking at different parts
    default boolean attackEntityFromPart(MultiPartEntityPart part, DamageSource source, float damage) {
        EntityHitbox hitbox = (EntityHitbox) part;
        if (damage > 0.0f && !hitbox.isDisabled()) {
            float newDamage = hitbox.getDamageMultiplier() * damage;
            return this.getMultiHitboxUser().attackEntityFrom(source, newDamage);
        }
        return false;
    }
}
