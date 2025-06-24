package anightdazingzoroark.riftlib;

import anightdazingzoroark.riftlib.hitboxLogic.EntityHitboxLinker;
import anightdazingzoroark.riftlib.ridePositionLogic.DynamicRidePosLinker;
import com.google.common.collect.Maps;
import net.minecraft.entity.EntityLiving;

import java.util.Map;

public class RiftLibLinkerRegistry {
    public static RiftLibLinkerRegistry INSTANCE = new RiftLibLinkerRegistry();

    public Map<Class<? extends EntityLiving>, EntityHitboxLinker> hitboxLinkerMap = Maps.newHashMap();
    public Map<Class<? extends EntityLiving>, DynamicRidePosLinker> dynamicRidePosLinkerMap = Maps.newHashMap();

    public static void registerEntityHitboxLinker(Class<? extends EntityLiving> entityClass, EntityHitboxLinker linker) {
        INSTANCE.hitboxLinkerMap.put(entityClass, linker);
    }

    public static void registerDynamicRidePosLinker(Class<? extends EntityLiving> entityClass, DynamicRidePosLinker linker) {
        INSTANCE.dynamicRidePosLinkerMap.put(entityClass, linker);
    }
}
