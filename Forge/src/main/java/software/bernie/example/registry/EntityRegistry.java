package software.bernie.example.registry;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import software.bernie.example.GeckoLibMod;
import software.bernie.example.entity.*;
import software.bernie.geckolib3.GeckoLib;

public final class EntityRegistry {
	public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES,
			GeckoLib.MOD_ID);

	public static final RegistryObject<EntityType<BatEntity>> BAT = registerMob("bat", BatEntity::new,
			0.7f, 1.3f, 0x1F1F1F, 0x0D0D0D);
	public static final RegistryObject<EntityType<BikeEntity>> BIKE = registerMob("bike", BikeEntity::new,
			0.5f, 0.6f, 0xD3E3E6, 0xE9F1F5);
	public static final RegistryObject<EntityType<RaceCarEntity>> RACE_CAR = registerMob("race_car", RaceCarEntity::new,
			1.5f, 1.5f, 0x9E1616, 0x595959);
	public static final RegistryObject<EntityType<ParasiteEntity>> PARASITE = registerMob("parasite", ParasiteEntity::new,
			1.5f, 1.5f, 0x302219, 0xACACAC);
	/*public static final RegistryObject<EntityType<ExtendedRendererEntity>> EXTENDED_RENDERER_EXAMPLE = buildEntity(ExtendedRendererEntity::new, ExtendedRendererEntity.class,
	0.5f, 1.9f, 0, 0);*/
	public static final RegistryObject<EntityType<TexturePerBoneTestEntity>> TEXTURE_PER_BONE_EXAMPLE = registerMob("texture_per_bone_entity", TexturePerBoneTestEntity::new,
			0.75f, 0.75f, 0, 0);
	public static final RegistryObject<EntityType<CoolKidEntity>> COOL_KID = registerMob("cool_kid", CoolKidEntity::new,
			0.45f, 1f, 0x5F2A31, 0x6F363E);

	public static <T extends Mob> RegistryObject<EntityType<T>> registerMob(String name, EntityType.EntityFactory<T> entity,
																			float width, float height, int primaryEggColor, int secondaryEggColor) {
		RegistryObject<EntityType<T>> entityType = ENTITIES.register(name,
				() -> EntityType.Builder.of(entity, MobCategory.CREATURE).sized(width, height).build(name));
		ItemRegistry.ITEMS.register(name + "_spawn_egg", () -> new ForgeSpawnEggItem(entityType, primaryEggColor, secondaryEggColor, new Item.Properties().tab(GeckoLibMod.ITEM_GROUP)));

		return entityType;
	}
}
