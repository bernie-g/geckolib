package software.bernie.example.registry;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import software.bernie.example.entity.BatEntity;
import software.bernie.example.entity.BikeEntity;
import software.bernie.example.entity.CoolKidEntity;
import software.bernie.example.entity.FakeGlassEntity;
import software.bernie.example.entity.MutantZombieEntity;
import software.bernie.example.entity.ParasiteEntity;
import software.bernie.example.entity.RaceCarEntity;
import software.bernie.geckolib.GeckoLib;

public class EntityRegistry {

    public static final EntityType<BatEntity> BAT = registerMob("bat", BatEntity::new,
            0.7f, 1.3f, 0x1F1F1F, 0x0D0D0D);
    public static final EntityType<BikeEntity> BIKE = registerMob("bike", BikeEntity::new,
            0.5f, 0.6f, 0xD3E3E6, 0xE9F1F5);

    public static final EntityType<RaceCarEntity> RACE_CAR = registerMob("race_car", RaceCarEntity::new,
            1.5f, 1.5f, 0x9E1616, 0x595959);
    public static final EntityType<ParasiteEntity> PARASITE = registerMob("parasite", ParasiteEntity::new,
            1.5f, 1.5f, 0x302219, 0xACACAC);

    public static final EntityType<MutantZombieEntity> GREMLIN = registerMob("gremlin", MutantZombieEntity::new,
            0.5f, 1.9f, 0x3C6236, 0x579989);
    public static final EntityType<MutantZombieEntity> MUTANT_ZOMBIE = registerMob("mutant_zombie", MutantZombieEntity::new,
            0.5f, 1.9f, 0x3C6236, 0x579989);
    public static final EntityType<FakeGlassEntity> FAKE_GLASS = registerMob("fake_glass", FakeGlassEntity::new,
            1, 1, 0xDD0000, 0xD8FFF7);

    public static final EntityType<CoolKidEntity> COOL_KID = registerMob("cool_kid", CoolKidEntity::new,
            0.45f, 1f, 0x5F2A31, 0x6F363E);

    public static <T extends Mob> EntityType<T> registerMob(String name, EntityType.EntityFactory<T> entity,
                                                            float width, float height, int primaryEggColor, int secondaryEggColor) {
    	EntityType<T> entityType = Registry.register(BuiltInRegistries.ENTITY_TYPE,
    			new ResourceLocation(GeckoLib.ModID, name),FabricEntityTypeBuilder.create(MobCategory.CREATURE, entity).dimensions(EntityDimensions.scalable(width, height)).build());
        return entityType;
    }
}
