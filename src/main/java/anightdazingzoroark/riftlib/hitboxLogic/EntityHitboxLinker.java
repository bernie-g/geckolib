package anightdazingzoroark.riftlib.hitboxLogic;

import anightdazingzoroark.riftlib.core.IAnimatable;
import anightdazingzoroark.riftlib.file.HitboxDefinitionList;
import anightdazingzoroark.riftlib.geo.render.built.GeoLocator;
import anightdazingzoroark.riftlib.geo.render.built.GeoModel;
import anightdazingzoroark.riftlib.resource.RiftLibCache;
import anightdazingzoroark.riftlib.util.json.JsonHitboxUtils;
import net.minecraft.util.ResourceLocation;

//this class is, just like with the model classes for rendering an entity
//is for assigning files to be linked to a creatures hitboxes
public abstract class EntityHitboxLinker<T extends IAnimatable & IMultiHitboxUser> {
    //model contains all the locators, it has to be linked to this
    public abstract ResourceLocation getModelLocation(T object);

    //obvious choice
    public abstract ResourceLocation getHitboxFileLocation(T entity);

    public HitboxDefinitionList getHitboxDefinitionList(IMultiHitboxUser animatable) {
        HitboxDefinitionList toReturn = RiftLibCache.getInstance().getHitboxDefinitions().get(this.getHitboxFileLocation((T) animatable));
        GeoModel model = RiftLibCache.getInstance().getGeoModels().get(this.getModelLocation((T) animatable));

        //first of all, add initial positions to the definitions from the model locations
        if (toReturn != null) {
            //add positions to the hitboxes
            for (GeoLocator locator : model.getAllLocators()) {
                if (JsonHitboxUtils.locatorCanBeHitbox(locator.name)) {
                    String hitboxName = JsonHitboxUtils.locatorHitboxToHitbox(locator.name);
                    toReturn.editHitboxDefinitionPosition(
                            hitboxName,
                            locator.positionX + (float) locator.getOffsetFromRotations().x,
                            locator.positionY + (float) locator.getOffsetFromRotations().y - toReturn.getHitboxDefinitionByName(hitboxName).height / 2f,
                            -locator.positionZ - (float) locator.getOffsetFromRotations().z
                    );
                }
            }
        }

        return toReturn;
    }
}
