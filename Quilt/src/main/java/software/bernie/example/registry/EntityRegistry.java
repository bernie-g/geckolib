package software.bernie.example.registry;

import org.quiltmc.loader.api.QuiltLoader;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import software.bernie.example.entity.BikeEntity;
import software.bernie.example.entity.CarEntity;
import software.bernie.example.entity.ExtendedRendererEntity;
import software.bernie.example.entity.GeoExampleEntity;
import software.bernie.example.entity.LEEntity;
import software.bernie.example.entity.RocketProjectile;
import software.bernie.example.entity.TexturePerBoneTestEntity;
import software.bernie.geckolib3q.GeckoLib;

public class EntityRegistry {
	public static final EntityType<GeoExampleEntity> GEO_EXAMPLE_ENTITY = buildEntity(GeoExampleEntity::new,
			GeoExampleEntity.class, .7F, 1.3F, MobCategory.CREATURE);
	public static final EntityType<BikeEntity> BIKE_ENTITY = buildEntity(BikeEntity::new, BikeEntity.class, 0.5f, 0.6F,
			MobCategory.CREATURE);
	public static final EntityType<CarEntity> CAR_ENTITY = buildEntity(CarEntity::new, CarEntity.class, 1.5f, 1.5F,
			MobCategory.CREATURE);
	public static final EntityType<ExtendedRendererEntity> EXTENDED_RENDERER_EXAMPLE = buildEntity(
			ExtendedRendererEntity::new, ExtendedRendererEntity.class, 0.5F, 1.9F, MobCategory.CREATURE);
	public static final EntityType<TexturePerBoneTestEntity> TEXTURE_PER_BONE_EXAMPLE = buildEntity(
			TexturePerBoneTestEntity::new, TexturePerBoneTestEntity.class, 0.75F, 0.75F, MobCategory.CREATURE);
	public static final EntityType<LEEntity> GEOLAYERENTITY = buildEntity(LEEntity::new, LEEntity.class, 0.45F, 1.0F,
			MobCategory.CREATURE);
	public static EntityType<RocketProjectile> ROCKET = buildEntity(RocketProjectile::new, RocketProjectile.class, 0.5F,
			0.5F, MobCategory.MISC);

	public static <T extends Entity> EntityType<T> buildEntity(EntityType.EntityFactory<T> entity, Class<T> entityClass,
			float width, float height, MobCategory group) {
		if (QuiltLoader.isDevelopmentEnvironment()) {
			String name = entityClass.getSimpleName().toLowerCase();
			return EntityRegistryBuilder.<T>createBuilder(new ResourceLocation(GeckoLib.ModID, name)).entity(entity)
					.category(group).dimensions(EntityDimensions.scalable(width, height)).build();
		}
		return null;
	}

}
