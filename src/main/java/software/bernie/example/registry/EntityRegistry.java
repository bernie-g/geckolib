/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.example.registry;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import software.bernie.geckolib.GeckoLib;
import software.bernie.example.entity.*;

public class EntityRegistry
{
	public static final DeferredRegister<EntityType<?>> ENTITIES = new DeferredRegister<>(ForgeRegistries.ENTITIES, GeckoLib.ModID);
	public static final RegistryObject<EntityType<StingrayTestEntity>> STING_RAY = BuildEntity(StingrayTestEntity::new, StingrayTestEntity.class, 2.845F, 0.3125F);
	public static final RegistryObject<EntityType<AscendedLegfishEntity>> ASCENDED_LEG_FISH = BuildEntity(AscendedLegfishEntity::new, AscendedLegfishEntity.class, 2.4F, 4.2F);
	public static final RegistryObject<EntityType<TigrisEntity>> TIGRIS = BuildEntity(TigrisEntity::new, TigrisEntity.class, 5F, 3F);
	public static final RegistryObject<EntityType<LightCrystalEntity>> LIGHTCRYSTAL = BuildEntity(LightCrystalEntity::new, LightCrystalEntity.class, 2F, 2F);
	public static final RegistryObject<EntityType<BrownEntity>> BROWN = BuildEntity(BrownEntity::new, BrownEntity.class, 2F, 2F);
	public static final RegistryObject<EntityType<EasingDemoEntity>> EASING_DEMO = BuildEntity(EasingDemoEntity::new, EasingDemoEntity.class, 0.1F, 0.1F);
	public static final RegistryObject<EntityType<RobotEntity>> ROBOT = BuildEntity(RobotEntity::new, RobotEntity.class, .7F, 1.3F);
	public static final RegistryObject<EntityType<EntityColorfulPig>> COLORFUL_PIG = BuildEntity(EntityColorfulPig::new, EntityColorfulPig.class, .7F, 1.3F);
	public static final RegistryObject<EntityType<BatEntity>> BAT = BuildEntity(BatEntity::new, BatEntity.class, .7F, 1.3F);
	public static final RegistryObject<EntityType<EntityBotarium>> BOTARIUM_TEST_ENTITY = BuildEntity(EntityBotarium::new, EntityBotarium.class, .7F, 1.3F);
	public static final RegistryObject<EntityType<GeoExampleEntity>> GEO_EXAMPLE_ENTITY = BuildEntity(GeoExampleEntity::new, GeoExampleEntity.class, .7F, 1.3F);


	public static <T extends Entity> RegistryObject<EntityType<T>> BuildEntity(EntityType.IFactory<T> entity, Class<T> entityClass, float width, float height)
	{
		String name = entityClass.getSimpleName().toLowerCase();
		return ENTITIES.register(name,
				() -> EntityType.Builder.create(entity, EntityClassification.CREATURE)
						.size(width, height).build(name));
	}
}
