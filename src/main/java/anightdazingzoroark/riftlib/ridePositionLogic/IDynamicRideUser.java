package anightdazingzoroark.riftlib.ridePositionLogic;

import anightdazingzoroark.riftlib.RiftLibLinkerRegistry;
import anightdazingzoroark.riftlib.core.IAnimatable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
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
        double radians = Math.toRadians(this.getDynamicRideUser().rotationYaw);
        double cos = Math.cos(radians);
        double sin = Math.sin(radians);

        double x = offset.x * cos - offset.z * sin;
        double z = offset.x * sin + offset.z * cos;

        return new Vec3d(x, offset.y, z);
    }

    default void updatePassenger(Entity passenger) {
        if (this.ridePositions().isEmpty()) return;

        //start with the main seat first, which is always the first item in ridePositions
        Vec3d firstPos = this.ridePositions().get(0);

        if (this.getDynamicRideUser().getControllingPassenger() != null && this.getDynamicRideUser().getControllingPassenger().equals(passenger)) {
            this.getDynamicRideUser().rotationYaw = passenger.rotationYaw;
            this.getDynamicRideUser().prevRotationYaw = this.getDynamicRideUser().rotationYaw;
            this.getDynamicRideUser().rotationPitch = passenger.rotationPitch * 0.5f;
            this.getDynamicRideUser().setRotation(this.getDynamicRideUser().rotationYaw, this.getDynamicRideUser().rotationPitch);
            this.getDynamicRideUser().renderYawOffset = this.getDynamicRideUser().rotationYaw;

            passenger.setPosition(
                    this.getDynamicRideUser().posX + this.rotateOffset(firstPos).x,
                    this.getDynamicRideUser().posY + firstPos.y + passenger.height,
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
                        this.getDynamicRideUser().posX + this.rotateOffset(firstPos).x,
                        this.getDynamicRideUser().posY + otherPos.y + passenger.height,
                        this.getDynamicRideUser().posZ + this.rotateOffset(firstPos).z
                );
        }

        if (this.getDynamicRideUser().isDead) passenger.dismountRidingEntity();
    }
}
