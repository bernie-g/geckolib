package anightdazingzoroark.riftlib.ridePositionLogic;

import anightdazingzoroark.riftlib.core.IAnimatable;
import anightdazingzoroark.riftlib.file.RidePosDefinitionList;
import anightdazingzoroark.riftlib.geo.render.built.GeoLocator;
import anightdazingzoroark.riftlib.geo.render.built.GeoModel;
import anightdazingzoroark.riftlib.resource.RiftLibCache;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;

public abstract class DynamicRidePosLinker<T extends IAnimatable & IDynamicRideUser> {
    //model contains all the locators, it has to be linked to this
    public abstract ResourceLocation getModelLocation(T object);

    public RidePosDefinitionList getDynamicRideDefinitions(T entity) {
        GeoModel model = RiftLibCache.getInstance().getGeoModels().get(this.getModelLocation((T) entity));
        RidePosDefinitionList toReturn = new RidePosDefinitionList();

        for (GeoLocator locator : model.getAllLocators()) {
            if (DynamicRidePosUtils.locatorCanBeRidePos(locator.name)) {
                int ridePosIndex = DynamicRidePosUtils.locatorRideIndex(locator.name);
                toReturn.map.put(
                        ridePosIndex,
                        new Vec3d(
                                locator.positionX + (float) locator.getOffsetFromRotations().x + (float) locator.getOffsetFromDisplacements().x,
                                locator.positionY + (float) locator.getOffsetFromRotations().y + (float) locator.getOffsetFromDisplacements().y,
                                -locator.positionZ - (float) locator.getOffsetFromRotations().z - (float) locator.getOffsetFromDisplacements().z
                        )
                );
            }
        }

        return toReturn;
    }
}
