package anightdazingzoroark.riftlib.hitboxLogic;

import net.minecraft.entity.IEntityMultiPart;
import net.minecraft.entity.MultiPartEntityPart;

public class Hitbox extends MultiPartEntityPart {
    public Hitbox(IEntityMultiPart parent, String partName, float width, float height) {
        super(parent, partName, width, height);
    }
}
