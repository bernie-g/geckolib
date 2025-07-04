package anightdazingzoroark.riftlib.ridePositionLogic;

import anightdazingzoroark.riftlib.RiftLibLinkerRegistry;
import anightdazingzoroark.riftlib.core.IAnimatable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

public interface IDynamicRideUser {
    //get the parent
    //must always return the entity its being implemented in
    //so its return statement in the entity implementing this should be "return this;"
    EntityLiving getDynamicRideUser();

    //this must be placed in the constructor of the entity
    //and must be the entity itself being entered
    default <T extends Entity & IAnimatable & IDynamicRideUser> void initializeRiderPositions(T entity) {
        DynamicRidePosLinker dynamicRidePosLinker = RiftLibLinkerRegistry.INSTANCE.dynamicRidePosLinkerMap.get(entity.getClass());
        this.createRidePositions(dynamicRidePosLinker.getDynamicRideDefinitions(entity).finalOrderedRiderPositions());
    }

    void createRidePositions(List<Vec3d> value);

    List<Vec3d> ridePositions();

    void setRidePosition(int index, Vec3d value);

    default Vec3d rotateOffset(Vec3d offset) {
        double xOffset = offset.x * ((IAnimatable) this.getDynamicRideUser()).scale();
        double yOffset = offset.y * ((IAnimatable) this.getDynamicRideUser()).scale();
        double zOffset = offset.z * ((IAnimatable) this.getDynamicRideUser()).scale();

        double radians = Math.toRadians(this.getDynamicRideUser().rotationYaw);
        double cos = Math.cos(radians);
        double sin = Math.sin(radians);

        double rotatedX = xOffset * cos - zOffset * sin;
        double rotatedZ = xOffset * sin + zOffset * cos;

        return new Vec3d(rotatedX, yOffset, rotatedZ);
    }

    default void updatePassenger(Entity passenger) {
        if (this.ridePositions().isEmpty()) return;

        //start with the main seat first, which is always the first item in ridePositions
        Vec3d firstPos = this.ridePositions().get(0);
        System.out.println(this.ridePositions());
        System.out.println("firstPos: "+firstPos);

        if (this.getDynamicRideUser().getControllingPassenger() != null && this.getDynamicRideUser().getControllingPassenger().equals(passenger)) {
            if (this.canRotateMounted()) {
                this.getDynamicRideUser().rotationYaw = passenger.rotationYaw;
                this.getDynamicRideUser().prevRotationYaw = this.getDynamicRideUser().rotationYaw;
                this.getDynamicRideUser().rotationPitch = passenger.rotationPitch * 0.5f;
                this.getDynamicRideUser().setRotation(this.getDynamicRideUser().rotationYaw, this.getDynamicRideUser().rotationPitch);
                this.getDynamicRideUser().renderYawOffset = this.getDynamicRideUser().rotationYaw;
            }

            passenger.setPosition(
                    this.getDynamicRideUser().posX + this.rotateOffset(firstPos).x,
                    this.getDynamicRideUser().posY + this.rotateOffset(firstPos).y + this.playerRideOffset(passenger),
                    this.getDynamicRideUser().posZ + this.rotateOffset(firstPos).z
            );

            ((EntityLivingBase)passenger).renderYawOffset = this.getDynamicRideUser().renderYawOffset;
        }

        //now deal with other positions
        List<Vec3d> otherPositions = new ArrayList<>(this.ridePositions());
        otherPositions.remove(0);

        if (!otherPositions.isEmpty() && !passenger.equals(this.getDynamicRideUser().getControllingPassenger())) {
            for (Vec3d otherPos : otherPositions)
                passenger.setPosition(
                        this.getDynamicRideUser().posX + this.rotateOffset(otherPos).x,
                        this.getDynamicRideUser().posY + this.rotateOffset(otherPos).y + this.playerRideOffset(passenger),
                        this.getDynamicRideUser().posZ + this.rotateOffset(otherPos).z
                );
        }

        if (this.getDynamicRideUser().isDead) passenger.dismountRidingEntity();
    }

    default float playerRideOffset(Entity entity) {
        if (entity instanceof EntityPlayer) return -0.6f;
        return 0f;
    }

    default boolean canRotateMounted() {
        return true;
    }
}
