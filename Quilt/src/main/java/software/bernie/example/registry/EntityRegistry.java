package software.bernie.example.registry;

import org.quiltmc.qsl.entity.api.QuiltEntityTypeBuilder;

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
import software.bernie.example.entity.DynamicExampleEntity;
import software.bernie.example.entity.FakeGlassEntity;
import software.bernie.example.entity.ParasiteEntity;
import software.bernie.example.entity.RaceCarEntity;
import software.bernie.geckolib.GeckoLib;

public class EntityRegistry {

    public static final EntityType<BatEntity> BAT = registerMob("bat", BatEntity::new, 0.7f, 1.3f);
    public static final EntityType<BikeEntity> BIKE = registerMob("bike", BikeEntity::new, 0.5f, 0.6f);

    public static final EntityType<RaceCarEntity> RACE_CAR = registerMob("race_car", RaceCarEntity::new, 1.5f, 1.5f);
    public static final EntityType<ParasiteEntity> PARASITE = registerMob("parasite", ParasiteEntity::new, 1.5f, 1.5f);

    public static final EntityType<DynamicExampleEntity> GREMLIN = registerMob("gremlin", DynamicExampleEntity::new, 0.5f, 1.9f);
    public static final EntityType<DynamicExampleEntity> MUTANT_ZOMBIE = registerMob("mutant_zombie", DynamicExampleEntity::new, 0.5f, 1.9f);
    public static final EntityType<FakeGlassEntity> FAKE_GLASS = registerMob("fake_glass", FakeGlassEntity::new, 1, 1);

    public static final EntityType<CoolKidEntity> COOL_KID = registerMob("cool_kid", CoolKidEntity::new, 0.45f, 1f);

    public static <T extends Mob> EntityType<T> registerMob(String name, EntityType.EntityFactory<T> entity,
                                                            float width, float height) {
        return Registry.register(BuiltInRegistries.ENTITY_TYPE,
                new ResourceLocation(GeckoLib.MOD_ID, name),QuiltEntityTypeBuilder.create(MobCategory.CREATURE, entity).setDimensions(EntityDimensions.scalable(width, height)).build());
    }
}
