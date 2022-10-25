package software.bernie.example.registry;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Identifier;
import software.bernie.example.entity.BikeEntity;
import software.bernie.example.entity.CarEntity;
import software.bernie.example.entity.ExtendedRendererEntity;
import software.bernie.example.entity.GeoExampleEntity;
import software.bernie.example.entity.LEEntity;
import software.bernie.example.entity.RocketProjectile;
import software.bernie.example.entity.TexturePerBoneTestEntity;
import software.bernie.geckolib3.GeckoLib;

public class EntityRegistry {
	public static final EntityType<GeoExampleEntity> GEO_EXAMPLE_ENTITY = buildEntity(GeoExampleEntity::new,
			GeoExampleEntity.class, .7F, 1.3F, SpawnGroup.CREATURE);
	public static final EntityType<BikeEntity> BIKE_ENTITY = buildEntity(BikeEntity::new, BikeEntity.class, 0.5f, 0.6F,
			SpawnGroup.CREATURE);
	public static final EntityType<CarEntity> CAR_ENTITY = buildEntity(CarEntity::new, CarEntity.class, 1.5f, 1.5F,
			SpawnGroup.CREATURE);
	public static final EntityType<ExtendedRendererEntity> EXTENDED_RENDERER_EXAMPLE = buildEntity(
			ExtendedRendererEntity::new, ExtendedRendererEntity.class, 0.5F, 1.9F,
			SpawnGroup.CREATURE);
	public static final EntityType<TexturePerBoneTestEntity> TEXTURE_PER_BONE_EXAMPLE = buildEntity(
			TexturePerBoneTestEntity::new, TexturePerBoneTestEntity.class, 0.75F, 0.75F, SpawnGroup.CREATURE);
	public static final EntityType<LEEntity> GEOLAYERENTITY = buildEntity(LEEntity::new, LEEntity.class, 0.45F, 1.0F,
			SpawnGroup.CREATURE);
	public static EntityType<RocketProjectile> ROCKET = buildEntity(RocketProjectile::new, RocketProjectile.class, 0.5F,
			0.5F, SpawnGroup.MISC);

	public static <T extends Entity> EntityType<T> buildEntity(EntityType.EntityFactory<T> entity, Class<T> entityClass,
			float width, float height, SpawnGroup group) {
		if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
			String name = entityClass.getSimpleName().toLowerCase();
			return EntityRegistryBuilder.<T>createBuilder(new Identifier(GeckoLib.ModID, name)).entity(entity)
					.category(group).dimensions(EntityDimensions.changing(width, height)).build();
		}
		return null;
	}

}
