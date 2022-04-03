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
import software.bernie.example.entity.BikeEntity;
import software.bernie.example.entity.ExtendedRendererEntity;
import software.bernie.example.entity.GeoExampleEntity;
import software.bernie.geckolib3.GeckoLib;

public class EntityRegistry {
	public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES,
			GeckoLib.ModID);

	public static final RegistryObject<EntityType<GeoExampleEntity>> GEO_EXAMPLE_ENTITY = buildEntity(
			GeoExampleEntity::new, GeoExampleEntity.class, .7F, 1.3F);
	public static final RegistryObject<EntityType<BikeEntity>> BIKE_ENTITY = buildEntity(BikeEntity::new,
			BikeEntity.class, 0.5f, 0.6F);
	
	public static final RegistryObject<EntityType<ExtendedRendererEntity>> EXTENDED_RENDERER_EXAMPLE = buildEntity(
			ExtendedRendererEntity::new, ExtendedRendererEntity.class, 0.5F, 1.9F);

	public static <T extends Entity> RegistryObject<EntityType<T>> buildEntity(EntityType.IFactory<T> entity,
			Class<T> entityClass, float width, float height) {
		String name = entityClass.getSimpleName().toLowerCase();
		return ENTITIES.register(name,
				() -> EntityType.Builder.of(entity, EntityClassification.CREATURE).sized(width, height).build(name));
	}
}
