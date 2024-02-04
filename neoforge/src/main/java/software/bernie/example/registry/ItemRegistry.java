package software.bernie.example.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import software.bernie.example.item.GeckoArmorItem;
import software.bernie.example.item.GeckoHabitatItem;
import software.bernie.example.item.JackInTheBoxItem;
import software.bernie.example.item.WolfArmorItem;
import software.bernie.geckolib.GeckoLibConstants;

public final class ItemRegistry {
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, GeckoLibConstants.MODID);
	public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, GeckoLibConstants.MODID);

	public static final DeferredHolder<Item, BlockItem> GECKO_HABITAT = ITEMS.register("gecko_habitat",
			() -> new GeckoHabitatItem(BlockRegistry.GECKO_HABITAT.get(),
					new Item.Properties()));
	public static final DeferredHolder<Item, BlockItem> FERTILIZER = ITEMS.register("fertilizer",
			() -> new BlockItem(BlockRegistry.FERTILIZER.get(),
					new Item.Properties()));

	public static final DeferredHolder<Item, JackInTheBoxItem> JACK_IN_THE_BOX = ITEMS.register("jack_in_the_box",
			() -> new JackInTheBoxItem(new Item.Properties()));

	public static final DeferredHolder<Item, WolfArmorItem> WOLF_ARMOR_HELMET = ITEMS.register("wolf_armor_helmet",
			() -> new WolfArmorItem(ArmorMaterials.DIAMOND, ArmorItem.Type.HELMET, new Item.Properties()));
	public static final DeferredHolder<Item, WolfArmorItem> WOLF_ARMOR_CHESTPLATE = ITEMS.register("wolf_armor_chestplate",
			() -> new WolfArmorItem(ArmorMaterials.DIAMOND, ArmorItem.Type.CHESTPLATE, new Item.Properties()));
	public static final DeferredHolder<Item, WolfArmorItem> WOLF_ARMOR_LEGGINGS = ITEMS.register("wolf_armor_leggings",
			() -> new WolfArmorItem(ArmorMaterials.DIAMOND, ArmorItem.Type.LEGGINGS, new Item.Properties()));
	public static final DeferredHolder<Item, WolfArmorItem> WOLF_ARMOR_BOOTS = ITEMS.register("wolf_armor_boots",
			() -> new WolfArmorItem(ArmorMaterials.DIAMOND, ArmorItem.Type.BOOTS, new Item.Properties()));

	public static final DeferredHolder<Item, GeckoArmorItem> GECKO_ARMOR_HELMET = ITEMS.register("gecko_armor_helmet",
			() -> new GeckoArmorItem(ArmorMaterials.DIAMOND, ArmorItem.Type.HELMET, new Item.Properties()));
	public static final DeferredHolder<Item, GeckoArmorItem> GECKO_ARMOR_CHESTPLATE = ITEMS.register("gecko_armor_chestplate",
			() -> new GeckoArmorItem(ArmorMaterials.DIAMOND, ArmorItem.Type.CHESTPLATE, new Item.Properties()));
	public static final DeferredHolder<Item, GeckoArmorItem> GECKO_ARMOR_LEGGINGS = ITEMS.register("gecko_armor_leggings",
			() -> new GeckoArmorItem(ArmorMaterials.DIAMOND, ArmorItem.Type.LEGGINGS, new Item.Properties()));
	public static final DeferredHolder<Item, GeckoArmorItem> GECKO_ARMOR_BOOTS = ITEMS.register("gecko_armor_boots",
			() -> new GeckoArmorItem(ArmorMaterials.DIAMOND, ArmorItem.Type.BOOTS, new Item.Properties()));
    
    public static final DeferredHolder<Item, DeferredSpawnEggItem> BAT_SPAWN_EGG = ITEMS.register("bat_spawn_egg", () -> new DeferredSpawnEggItem(EntityRegistry.BAT, 0x1F1F1F, 0x0D0D0D, new Item.Properties()));
    public static final DeferredHolder<Item, DeferredSpawnEggItem> BIKE_SPAWN_EGG = ITEMS.register("bike_spawn_egg", () -> new DeferredSpawnEggItem(EntityRegistry.BIKE, 0xD3E3E6, 0xE9F1F5, new Item.Properties()));
    public static final DeferredHolder<Item, DeferredSpawnEggItem> RACE_CAR_SPAWN_EGG = ITEMS.register("race_car_spawn_egg",() ->  new DeferredSpawnEggItem(EntityRegistry.RACE_CAR, 0x9E1616, 0x595959, new Item.Properties()));
    public static final DeferredHolder<Item, DeferredSpawnEggItem> PARASITE_SPAWN_EGG = ITEMS.register("parasite_spawn_egg", () -> new DeferredSpawnEggItem(EntityRegistry.PARASITE, 0x302219, 0xACACAC, new Item.Properties()));
    public static final DeferredHolder<Item, DeferredSpawnEggItem> MUTANT_ZOMBIE_SPAWN_EGG = ITEMS.register("mutant_zombie_spawn_egg", () -> new DeferredSpawnEggItem(EntityRegistry.MUTANT_ZOMBIE, 0x3C6236, 0x579989, new Item.Properties()));
    public static final DeferredHolder<Item, DeferredSpawnEggItem> FAKE_GLASS_SPAWN_EGG = ITEMS.register("fake_glass_spawn_egg", () -> new DeferredSpawnEggItem(EntityRegistry.FAKE_GLASS, 0xDD0000, 0xD8FFF7, new Item.Properties()));
    public static final DeferredHolder<Item, DeferredSpawnEggItem> COOL_KID_SPAWN_EGG = ITEMS.register("cool_kid_spawn_egg", () -> new DeferredSpawnEggItem(EntityRegistry.COOL_KID, 0x5F2A31, 0x6F363E, new Item.Properties()));
    public static final DeferredHolder<Item, DeferredSpawnEggItem> GREMLIN_SPAWN_EGG = ITEMS.register("gremlin_spawn_egg", () -> new DeferredSpawnEggItem(EntityRegistry.GREMLIN, 0x505050, 0x606060, new Item.Properties()));

	public static final DeferredHolder<CreativeModeTab, CreativeModeTab> GECKOLIB_TAB = TABS.register("geckolib_examples", () -> CreativeModeTab.builder()
			.title(Component.translatable("itemGroup." + GeckoLibConstants.MODID + ".geckolib_examples"))
			.icon(() -> new ItemStack(ItemRegistry.JACK_IN_THE_BOX.get()))
			.displayItems((enabledFeatures, entries) -> {
				entries.accept(ItemRegistry.JACK_IN_THE_BOX.get());
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
