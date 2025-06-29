package anightdazingzoroark.riftlib.hitboxLogic;

import anightdazingzoroark.riftlib.core.IAnimatable;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.MultiPartEntityPart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EntityHitbox extends MultiPartEntityPart {
    private final float damageMultiplier;
    public final float initWidth;
    public final float initHeight;
    private float widthScaleFromAnim = 1f;
    private float heightScaleFromAnim = 1f;
    private float xOffset;
    private float yOffset;
    private float zOffset;
    public final boolean affectedByAnim;
    private boolean isDisabled;
    public final List<EntityHitboxDamageDefinition> damageDefinitions = new ArrayList<>();

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
        double xOffset = this.xOffset * this.width / this.initWidth;
        double yOffset = this.yOffset * this.height / this.initHeight;
        double zOffset = this.zOffset * this.width / this.initWidth;

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

    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (this.damageSourceIsRider(source)) return false;
        return super.attackEntityFrom(source, amount);
    }

    private boolean damageSourceIsRider(DamageSource source) {
        if (this.getParentAsEntityLiving() == null || source == null) return false;

        if (this.getParentAsEntityLiving().isBeingRidden()) {
            if (source.getImmediateSource() != null && this.getParentAsEntityLiving().isPassenger(source.getImmediateSource())) return true;
            if (source.getTrueSource() != null && this.getParentAsEntityLiving().isPassenger(source.getTrueSource())) return true;
        }

        return false;
    }

    public void resize(float scale) {
        this.setSize(this.initWidth * this.widthScaleFromAnim * scale, this.initHeight * this.heightScaleFromAnim * scale);
    }

    public void resizeByAnim(float newWidth, float newHeight) {
        this.widthScaleFromAnim = newWidth;
        this.heightScaleFromAnim = newHeight;
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

    public boolean damageSourceWithinDamageDefinitions(DamageSource damageSource) {
        for (EntityHitboxDamageDefinition damageDefinition : this.damageDefinitions) {
            if (damageDefinition.damageSource != null) {
                if (damageDefinition.damageSource.equals(damageSource.damageType)) return true;
            }
            else if (damageDefinition.damageType != null) {
                switch (damageDefinition.damageType) {
                    case "projectile":
                        if (damageSource.isProjectile()) return true;
                    case "magic":
                        if (damageSource.isMagicDamage()) return true;
                    case "fire":
                        if (damageSource.isFireDamage()) return true;
                    case "explosion":
                        if (damageSource.isExplosion()) return true;
                }
            }
        }
        return false;
    }

    public float getDamageMultiplierForSource(DamageSource damageSource) {
        float toReturn = 1f;
        for (EntityHitboxDamageDefinition damageDefinition : this.damageDefinitions) {
            if (damageDefinition.damageSource != null) {
                if (damageDefinition.damageSource.equals(damageSource.damageType)) {
                    toReturn *= damageDefinition.damageMultiplier;
                }
            }
            else if (damageDefinition.damageType != null) {
                switch (damageDefinition.damageType) {
                    case "projectile":
                        if (damageSource.isProjectile()) {
                            toReturn *= damageDefinition.damageMultiplier;
                            break;
                        }
                    case "magic":
                        if (damageSource.isMagicDamage()) {
                            toReturn *= damageDefinition.damageMultiplier;
                            break;
                        }
                    case "fire":
                        if (damageSource.isFireDamage()) {
                            toReturn *= damageDefinition.damageMultiplier;
                            break;
                        }
                    case "explosion":
                        if (damageSource.isExplosion()) {
                            toReturn *= damageDefinition.damageMultiplier;
                            break;
                        }
                }
            }
        }
        return toReturn;
    }

    static class EntityHitboxDamageDefinition {
        public final String damageSource;
        public final String damageType;
        public final float damageMultiplier;

        //either one of damageSource or damageType must be null
        //damageSource is an instance of the DamageSource object (arrow, cactus, etc)
        //damageType is one of the booleans associated with a DamageSource object (projectile, magic, etc)
        //if damageSource or damageType both not null, damageSource will be prioritized
        public EntityHitboxDamageDefinition(String damageSource, String damageType, float damageMultiplier) {
            this.damageSource = damageSource;
            this.damageType = damageType;
            this.damageMultiplier = damageMultiplier;
        }

        @Override
        public String toString() {
            return "[source="+this.damageSource+", type="+this.damageType+", multiplier="+this.damageMultiplier+"]";
        }
    }
}
