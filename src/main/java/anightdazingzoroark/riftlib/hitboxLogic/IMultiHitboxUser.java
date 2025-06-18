package anightdazingzoroark.riftlib.hitboxLogic;

import net.minecraft.entity.Entity;
import net.minecraft.entity.IEntityMultiPart;

public interface IMultiHitboxUser extends IEntityMultiPart {
    //this must be placed in the constructor of the entity
    //and must be the entity itself being entered
    default void initializeHitboxes(Entity entity) {
        //todo: add logic here somehow that makes it so that it gets hitboxes from the hitbox file
        //and positions from the model file to make the hitboxes
    }

    //an array of hitboxes associated with the entity is to be created
    Hitbox[] hitboxes();
}
