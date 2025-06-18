package anightdazingzoroark.riftlib.hitboxLogic;

import anightdazingzoroark.riftlib.core.IAnimatable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MultiPartEntityPart;

public class EntityHitbox extends MultiPartEntityPart {
    private final float damageMultiplier;
    private final float initWidth;
    private final float initHeight;
    private final float xOffset;
    private final float yOffset;
    private final float zOffset;
    private boolean isDisabled;

    public EntityHitbox(IMultiHitboxUser parent, String partName, float damageMultiplier, float width, float height, float xOffset, float yOffset, float zOffset) {
        super(parent, partName, width, height);
        this.damageMultiplier = damageMultiplier;
        this.initWidth = width;
        this.initHeight = height;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.zOffset = zOffset;
    }

    @Override
    public void onUpdate() {
        //define and set offsets
        double xOffset = this.xOffset * (this.width / (this.initWidth * this.getParentScale()));
        double yOffset = this.yOffset * (this.height / (this.initHeight * this.getParentScale()));
        double zOffset = this.zOffset * (this.width / (this.initWidth * this.getParentScale()));
        this.setPositionAndUpdate(this.getParentAsEntity().posX + xOffset, this.getParentAsEntity().posY + yOffset, this.getParentAsEntity().posZ + zOffset);
        if (!this.getParentAsEntity().isEntityAlive()) this.world.removeEntityDangerously(this);
        super.onUpdate();
    }

    //recommended instead of using the parent variable
    public IMultiHitboxUser getParent() {
        return (IMultiHitboxUser) this.parent;
    }

    private Entity getParentAsEntity() {
        return (Entity) this.parent;
    }

    //get the scale of the parent as an IAnimatable
    private float getParentScale() {
        if (this.parent instanceof IAnimatable) {
            return ((IAnimatable) this.parent).scale();
        }
        return 1f;
    }

    public void setDisabled(boolean value) {
        this.isDisabled = value;
    }

    public boolean isDisabled() {
        return this.isDisabled;
    }

    public float getDamageMultiplier() {
        return this.damageMultiplier;
    }
}
