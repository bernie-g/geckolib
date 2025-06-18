package anightdazingzoroark.riftlib.model;

import anightdazingzoroark.riftlib.core.IAnimatable;
import anightdazingzoroark.riftlib.file.HitboxDefinitionList;
import anightdazingzoroark.riftlib.geo.render.built.GeoLocator;
import anightdazingzoroark.riftlib.hitboxLogic.IMultiHitboxUser;
import anightdazingzoroark.riftlib.resource.RiftLibCache;
import net.minecraft.util.ResourceLocation;

public abstract class AnimatedMultiHitboxGeoModel<T extends IAnimatable & IMultiHitboxUser> extends AnimatedGeoModel<T> {
    public abstract ResourceLocation getHitboxFileLocation(T entity);

    public HitboxDefinitionList getHitboxDefinitionList(IMultiHitboxUser animatable) {
        return RiftLibCache.getInstance().getHitboxDefinitions().get(this.getHitboxFileLocation((T) animatable));
    }

    public GeoLocator getAssociatedLocator(HitboxDefinitionList.HitboxDefinition hitbox) {
        String locatorNameToCheck = "hitbox_"+hitbox.locator;
        return this.currentModel.getLocator(locatorNameToCheck);
    }
}
