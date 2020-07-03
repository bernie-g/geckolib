/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.geckolib.example.registry;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCategory;
import net.minecraft.entity.EntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import software.bernie.geckolib.GeckoLib;
import software.bernie.geckolib.example.entity.AscendedLegfishEntity;
import software.bernie.geckolib.example.entity.StingrayTestEntity;
import software.bernie.geckolib.example.entity.TigrisEntity;

public class Entities
{
	public static final DeferredRegister<EntityType<?>> ENTITIES = new DeferredRegister<>(ForgeRegistries.ENTITIES, GeckoLib.ModID);
	public static final RegistryObject<EntityType<StingrayTestEntity>> STING_RAY = BuildEntity(StingrayTestEntity::new, StingrayTestEntity.class, 2.845F, 0.3125F);
	public static final RegistryObject<EntityType<AscendedLegfishEntity>> ASCENDED_LEG_FISH = BuildEntity(AscendedLegfishEntity::new, AscendedLegfishEntity.class, 2.4F, 4.2F);
	public static final RegistryObject<EntityType<TigrisEntity>> TIGRIS = BuildEntity(TigrisEntity::new, TigrisEntity.class, 5F, 3F);


	public static <T extends Entity> RegistryObject<EntityType<T>> BuildEntity(EntityType.EntityFactory<T> entity, Class<T> entityClass, float width, float height)
	{
		String name = entityClass.getSimpleName().toLowerCase();
		return ENTITIES.register(name,
				() -> EntityType.Builder.create(entity, EntityCategory.field_6294)
						.setDimensions(width, height).build(name));
	}
}
