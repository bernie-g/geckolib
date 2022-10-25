package software.bernie.example.registry;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import software.bernie.example.entity.BikeEntity;
import software.bernie.example.entity.CarEntity;
import software.bernie.example.entity.ExtendedRendererEntity;
import software.bernie.example.entity.GeoExampleEntity;
import software.bernie.example.entity.LEEntity;
import software.bernie.example.entity.TexturePerBoneTestEntity;
import software.bernie.geckolib3.GeckoLib;

public class EntityRegistry {
	public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES,
			GeckoLib.ModID);

	public static final RegistryObject<EntityType<GeoExampleEntity>> GEO_EXAMPLE_ENTITY = buildEntity(
			GeoExampleEntity::new, GeoExampleEntity.class, .7F, 1.3F);
	public static final RegistryObject<EntityType<BikeEntity>> BIKE_ENTITY = buildEntity(BikeEntity::new,
			BikeEntity.class, 0.5f, 0.6F);
	public static final RegistryObject<EntityType<CarEntity>> CAR_ENTITY = buildEntity(CarEntity::new, CarEntity.class,
			1.5f, 1.5F);
	
	public static final RegistryObject<EntityType<ExtendedRendererEntity>> EXTENDED_RENDERER_EXAMPLE = buildEntity(
			ExtendedRendererEntity::new, ExtendedRendererEntity.class, 0.5F, 1.9F);
	
	public static final RegistryObject<EntityType<TexturePerBoneTestEntity>> TEXTURE_PER_BONE_EXAMPLE = buildEntity(
			TexturePerBoneTestEntity::new, TexturePerBoneTestEntity.class, 0.75F, 0.75F);

	public static final RegistryObject<EntityType<LEEntity>> GEOLAYERENTITY = buildEntity(LEEntity::new,
			LEEntity.class, 0.45F,1.0F);

	public static <T extends Entity> RegistryObject<EntityType<T>> buildEntity(EntityType.IFactory<T> entity,
			Class<T> entityClass, float width, float height) {
		String name = entityClass.getSimpleName().toLowerCase();
		return ENTITIES.register(name,
				() -> EntityType.Builder.of(entity, EntityClassification.CREATURE).sized(width, height).build(name));
	}
}
