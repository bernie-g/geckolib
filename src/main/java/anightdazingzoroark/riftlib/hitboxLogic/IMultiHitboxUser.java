package anightdazingzoroark.riftlib.hitboxLogic;

import anightdazingzoroark.riftlib.core.IAnimatable;
import anightdazingzoroark.riftlib.file.HitboxDefinitionList;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
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
    default <T extends Entity & IAnimatable & IMultiHitboxUser> void initializeHitboxes(T entity) {
        EntityHitboxLinker hitboxLinker = EntityHitboxLinkerRegistry.INSTANCE.hitboxLinkerMap.get(entity.getClass());
        for (HitboxDefinitionList.HitboxDefinition hitboxDefinition : hitboxLinker.getHitboxDefinitionList(entity).list) {
            EntityHitbox hitbox = new EntityHitbox(
                    entity,
                    hitboxDefinition.locator,
                    hitboxDefinition.damageMultiplier,
                    hitboxDefinition.width,
                    hitboxDefinition.height,
                    (float) hitboxDefinition.position.x,
                    (float) hitboxDefinition.position.y,
                    (float) hitboxDefinition.position.z
            );
            this.addPart(hitbox);
        }
    }

    //an array of hitboxes associated with the entity is to be created
    Entity[] getParts();

    void setParts(Entity[] hitboxes);

    default void addPart(EntityHitbox hitbox) {
        Entity[] newHitbox = new Entity[this.getParts().length + 1];
        for (int x = 0; x < newHitbox.length; x++) {
            if (x < newHitbox.length - 1) newHitbox[x] = this.getParts()[x];
            else newHitbox[x] = hitbox;
        }
        this.setParts(newHitbox);
    }

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
