package anightdazingzoroark.riftlib.hitboxLogic;

import com.google.common.collect.Maps;
import net.minecraft.entity.EntityLiving;

import java.util.Map;

public class EntityHitboxLinkerRegistry {
    public static EntityHitboxLinkerRegistry INSTANCE = new EntityHitboxLinkerRegistry();

    public Map<Class<? extends EntityLiving>, EntityHitboxLinker> hitboxLinkerMap = Maps.newHashMap();

    public static void registerEntityHitboxLinker(Class<? extends EntityLiving> entityClass, EntityHitboxLinker linker) {
        INSTANCE.hitboxLinkerMap.put(entityClass, linker);
    }
}
