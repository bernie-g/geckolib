package software.bernie.example.registry;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import software.bernie.example.entity.*;
import software.bernie.geckolib3.GeckoLib;

public final class EntityRegistry {
	public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES,
			GeckoLib.MOD_ID);

	public static final RegistryObject<EntityType<BatEntity>> GEO_EXAMPLE_ENTITY = buildEntity("bat",
			BatEntity::new, 0.7F, 1.3F);
	public static final RegistryObject<EntityType<BikeEntity>> BIKE_ENTITY = buildEntity("bike", BikeEntity::new, 0.5f, 0.6F);
	public static final RegistryObject<EntityType<RaceCarEntity>> CAR_ENTITY = buildEntity("race_car", RaceCarEntity::new,
			1.5f, 1.5F);
	public static final RegistryObject<EntityType<ParasiteEntity>> PARASITE = buildEntity("parasite", ParasiteEntity::new,
			1.5f, 1.5F);
	/*public static final RegistryObject<EntityType<ExtendedRendererEntity>> EXTENDED_RENDERER_EXAMPLE = buildEntity(
			ExtendedRendererEntity::new, ExtendedRendererEntity.class, 0.5F, 1.9F);*/
	public static final RegistryObject<EntityType<TexturePerBoneTestEntity>> TEXTURE_PER_BONE_EXAMPLE = buildEntity("texture_per_bone_entity",
			TexturePerBoneTestEntity::new, 0.75F, 0.75F);
	public static final RegistryObject<EntityType<ExampleRenderLayerEntity>> GEOLAYERENTITY = buildEntity("example_render_layer_entity", ExampleRenderLayerEntity::new,
			0.45F, 1.0F);

	public static <T extends Entity> RegistryObject<EntityType<T>> buildEntity(String name, EntityType.EntityFactory<T> entity,
			float width, float height) {

		return ENTITIES.register(name,
				() -> EntityType.Builder.of(entity, MobCategory.CREATURE).sized(width, height).build(name));
	}
}
