package anightdazingzoroark.riftlib.model;

import anightdazingzoroark.riftlib.core.IAnimatable;
import anightdazingzoroark.riftlib.hitboxLogic.IMultiHitboxUser;
import net.minecraft.util.ResourceLocation;

public abstract class AnimatedMultiHitboxGeoModel<T extends IAnimatable & IMultiHitboxUser> extends AnimatedGeoModel<T> {
    public abstract ResourceLocation getHitboxFileLocation(T entity);
}
