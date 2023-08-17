package software.bernie.example.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import software.bernie.example.item.*;
import software.bernie.geckolib.GeckoLib;

public final class ItemRegistry {
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, GeckoLib.MOD_ID);
	public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, GeckoLib.MOD_ID);

	public static final RegistryObject<BlockItem> GECKO_HABITAT = ITEMS.register("gecko_habitat",
			() -> new GeckoHabitatItem(BlockRegistry.GECKO_HABITAT.get(),
					new Item.Properties()));
	public static final RegistryObject<BlockItem> FERTILIZER = ITEMS.register("fertilizer",
			() -> new BlockItem(BlockRegistry.FERTILIZER.get(),
					new Item.Properties()));

	public static final RegistryObject<JackInTheBoxItem> JACK_IN_THE_BOX = ITEMS.register("jack_in_the_box",
			() -> new JackInTheBoxItem(new Item.Properties()));

	public static final RegistryObject<PistolItem> PISTOL = ITEMS.register("pistol", PistolItem::new);

	public static final RegistryObject<WolfArmorItem> WOLF_ARMOR_HELMET = ITEMS.register("wolf_armor_helmet",
			() -> new WolfArmorItem(ArmorMaterials.DIAMOND, ArmorItem.Type.HELMET, new Item.Properties()));
	public static final RegistryObject<WolfArmorItem> WOLF_ARMOR_CHESTPLATE = ITEMS.register("wolf_armor_chestplate",
			() -> new WolfArmorItem(ArmorMaterials.DIAMOND, ArmorItem.Type.CHESTPLATE, new Item.Properties()));
	public static final RegistryObject<WolfArmorItem> WOLF_ARMOR_LEGGINGS = ITEMS.register("wolf_armor_leggings",
			() -> new WolfArmorItem(ArmorMaterials.DIAMOND, ArmorItem.Type.LEGGINGS, new Item.Properties()));
	public static final RegistryObject<WolfArmorItem> WOLF_ARMOR_BOOTS = ITEMS.register("wolf_armor_boots",
			() -> new WolfArmorItem(ArmorMaterials.DIAMOND, ArmorItem.Type.BOOTS, new Item.Properties()));

	public static final RegistryObject<GeckoArmorItem> GECKO_ARMOR_HELMET = ITEMS.register("gecko_armor_helmet",
			() -> new GeckoArmorItem(ArmorMaterials.DIAMOND, ArmorItem.Type.HELMET, new Item.Properties()));
	public static final RegistryObject<GeckoArmorItem> GECKO_ARMOR_CHESTPLATE = ITEMS.register("gecko_armor_chestplate",
			() -> new GeckoArmorItem(ArmorMaterials.DIAMOND, ArmorItem.Type.CHESTPLATE, new Item.Properties()));
	public static final RegistryObject<GeckoArmorItem> GECKO_ARMOR_LEGGINGS = ITEMS.register("gecko_armor_leggings",
			() -> new GeckoArmorItem(ArmorMaterials.DIAMOND, ArmorItem.Type.LEGGINGS, new Item.Properties()));
	public static final RegistryObject<GeckoArmorItem> GECKO_ARMOR_BOOTS = ITEMS.register("gecko_armor_boots",
			() -> new GeckoArmorItem(ArmorMaterials.DIAMOND, ArmorItem.Type.BOOTS, new Item.Properties()));
    
    public static final RegistryObject<ForgeSpawnEggItem> BAT_SPAWN_EGG = ITEMS.register("bat_spawn_egg", () -> new ForgeSpawnEggItem(EntityRegistry.BAT, 0x1F1F1F, 0x0D0D0D, new Item.Properties()));
    public static final RegistryObject<ForgeSpawnEggItem> BIKE_SPAWN_EGG = ITEMS.register("bike_spawn_egg", () -> new ForgeSpawnEggItem(EntityRegistry.BIKE, 0xD3E3E6, 0xE9F1F5, new Item.Properties()));
    public static final RegistryObject<ForgeSpawnEggItem> RACE_CAR_SPAWN_EGG = ITEMS.register("race_car_spawn_egg",() ->  new ForgeSpawnEggItem(EntityRegistry.RACE_CAR, 0x9E1616, 0x595959, new Item.Properties()));
    public static final RegistryObject<ForgeSpawnEggItem> PARASITE_SPAWN_EGG = ITEMS.register("parasite_spawn_egg", () -> new ForgeSpawnEggItem(EntityRegistry.PARASITE, 0x302219, 0xACACAC, new Item.Properties()));
    public static final RegistryObject<ForgeSpawnEggItem> MUTANT_ZOMBIE_SPAWN_EGG = ITEMS.register("mutant_zombie_spawn_egg", () -> new ForgeSpawnEggItem(EntityRegistry.MUTANT_ZOMBIE, 0x3C6236, 0x579989, new Item.Properties()));
    public static final RegistryObject<ForgeSpawnEggItem> FAKE_GLASS_SPAWN_EGG = ITEMS.register("fake_glass_spawn_egg", () -> new ForgeSpawnEggItem(EntityRegistry.FAKE_GLASS, 0xDD0000, 0xD8FFF7, new Item.Properties()));
    public static final RegistryObject<ForgeSpawnEggItem> COOL_KID_SPAWN_EGG = ITEMS.register("cool_kid_spawn_egg", () -> new ForgeSpawnEggItem(EntityRegistry.COOL_KID, 0x5F2A31, 0x6F363E, new Item.Properties()));
    public static final RegistryObject<ForgeSpawnEggItem> GREMLIN_SPAWN_EGG = ITEMS.register("gremlin_spawn_egg", () -> new ForgeSpawnEggItem(EntityRegistry.GREMLIN, 0x505050, 0x606060, new Item.Properties()));

	public static final RegistryObject<CreativeModeTab> GECKOLIB_TAB = TABS.register("geckolib_examples", () -> CreativeModeTab.builder()
			.title(Component.translatable("itemGroup." + GeckoLib.MOD_ID + ".geckolib_examples"))
			.icon(() -> new ItemStack(ItemRegistry.JACK_IN_THE_BOX.get()))
			.displayItems((enabledFeatures, entries) -> {
				entries.accept(ItemRegistry.JACK_IN_THE_BOX.get());
				entries.accept(ItemRegistry.PISTOL.get());
				entries.accept(ItemRegistry.GECKO_ARMOR_HELMET.get());
				entries.accept(ItemRegistry.GECKO_ARMOR_CHESTPLATE.get());
				entries.accept(ItemRegistry.GECKO_ARMOR_LEGGINGS.get());
				entries.accept(ItemRegistry.GECKO_ARMOR_BOOTS.get());
				entries.accept(ItemRegistry.WOLF_ARMOR_HELMET.get());
				entries.accept(ItemRegistry.WOLF_ARMOR_CHESTPLATE.get());
				entries.accept(ItemRegistry.WOLF_ARMOR_LEGGINGS.get());
				entries.accept(ItemRegistry.WOLF_ARMOR_BOOTS.get());
				entries.accept(ItemRegistry.GECKO_HABITAT.get());
				entries.accept(ItemRegistry.FERTILIZER.get());
				entries.accept(ItemRegistry.BAT_SPAWN_EGG.get());
				entries.accept(ItemRegistry.BIKE_SPAWN_EGG.get());
				entries.accept(ItemRegistry.RACE_CAR_SPAWN_EGG.get());
				entries.accept(ItemRegistry.PARASITE_SPAWN_EGG.get());
				entries.accept(ItemRegistry.MUTANT_ZOMBIE_SPAWN_EGG.get());
				entries.accept(ItemRegistry.GREMLIN_SPAWN_EGG.get());
				entries.accept(ItemRegistry.FAKE_GLASS_SPAWN_EGG.get());
				entries.accept(ItemRegistry.COOL_KID_SPAWN_EGG.get());
			})
			.build());
}
