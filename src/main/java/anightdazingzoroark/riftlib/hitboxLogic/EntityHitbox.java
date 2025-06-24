package anightdazingzoroark.riftlib.hitboxLogic;

import anightdazingzoroark.riftlib.core.IAnimatable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.MultiPartEntityPart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;

public class EntityHitbox extends MultiPartEntityPart {
    private final float damageMultiplier;
    public final float initWidth;
    public final float initHeight;
    private float xOffset;
    private float yOffset;
    private float zOffset;
    public final boolean affectedByAnim;
    private boolean isDisabled;

    public EntityHitbox(IMultiHitboxUser parent, String partName, float damageMultiplier, float width, float height, float xOffset, float yOffset, float zOffset, boolean affectedByAnim) {
        super(parent, partName, width, height);
        this.damageMultiplier = damageMultiplier;
        this.initWidth = width;
        this.initHeight = height;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.zOffset = zOffset;
        this.affectedByAnim = affectedByAnim;
    }

    @Override
    public void onUpdate() {
        // Step 1: Scale initial offsets
        double xOffset = this.xOffset * (this.width / this.initWidth);
        double yOffset = this.yOffset * (this.height / this.initHeight);
        double zOffset = this.zOffset * (this.width / this.initWidth);

        double yawRadians = Math.toRadians(this.getParentAsEntityLiving().renderYawOffset);

        double cosYaw = Math.cos(yawRadians);
        double sinYaw = Math.sin(yawRadians);

        double rotatedX = xOffset * cosYaw - zOffset * sinYaw;
        double rotatedZ = xOffset * sinYaw + zOffset * cosYaw;

        this.setPositionAndUpdate(
                this.getParentAsEntityLiving().posX + rotatedX,
                this.getParentAsEntityLiving().posY + yOffset,
                this.getParentAsEntityLiving().posZ + rotatedZ
        );

        if (!this.getParentAsEntityLiving().isEntityAlive()) {
            this.world.removeEntityDangerously(this);
        }
        super.onUpdate();
    }

    @Override
    public boolean processInitialInteract(EntityPlayer player, EnumHand hand) {
        return this.getParentAsEntityLiving().processInitialInteract(player, hand);
    }

    public void resize(float scale) {
        this.setSize(this.initWidth * scale, this.initHeight * scale);
    }

    public void changeOffset(float xOffset, float yOffset, float zOffset) {
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.zOffset = zOffset;
    }

    public float getHitboxXOffset() {
        return this.xOffset;
    }

    public float getHitboxYOffset() {
        return this.yOffset;
    }

    public float getHitboxZOffset() {
        return this.zOffset;
    }

    //recommended instead of using the parent variable
    public IMultiHitboxUser getParent() {
        return (IMultiHitboxUser) this.parent;
    }

    private EntityLiving getParentAsEntityLiving() {
        return (EntityLiving) this.parent;
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
