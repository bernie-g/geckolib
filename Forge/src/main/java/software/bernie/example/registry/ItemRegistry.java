package software.bernie.example.registry;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import software.bernie.example.GeckoLibMod;
import software.bernie.example.item.JackInTheBoxItem;
import software.bernie.example.item.PistolItem;
import software.bernie.example.item.WolfArmorItem;
import software.bernie.geckolib3.GeckoLib;

public final class ItemRegistry {
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, GeckoLib.MOD_ID);

	public static final RegistryObject<BlockItem> GECKO_HABITAT = ITEMS.register("gecko_habitat",
			() -> new BlockItem(BlockRegistry.GECKO_HABITAT.get(),
					new Item.Properties().tab(GeckoLibMod.ITEM_GROUP)));
	public static final RegistryObject<BlockItem> FERTILIZER = ITEMS.register("fertilizer",
			() -> new BlockItem(BlockRegistry.FERTILIZER.get(),
					new Item.Properties().tab(GeckoLibMod.ITEM_GROUP)));

	public static final RegistryObject<JackInTheBoxItem> JACK_IN_THE_BOX = ITEMS.register("jack_in_the_box",
			() -> new JackInTheBoxItem(new Item.Properties().tab(GeckoLibMod.ITEM_GROUP)));

	public static final RegistryObject<PistolItem> PISTOL = ITEMS.register("pistol", PistolItem::new);

	public static final RegistryObject<WolfArmorItem> WOLF_ARMOR_HELMET = ITEMS.register("wolf_armor_helmet",
			() -> new WolfArmorItem(ArmorMaterials.DIAMOND, EquipmentSlot.HEAD, new Item.Properties().tab(GeckoLibMod.ITEM_GROUP)));
	public static final RegistryObject<WolfArmorItem> WOLF_ARMOR_CHESTPLATE = ITEMS.register("wolf_armor_chestplate",
			() -> new WolfArmorItem(ArmorMaterials.DIAMOND, EquipmentSlot.CHEST, new Item.Properties().tab(GeckoLibMod.ITEM_GROUP)));
	public static final RegistryObject<WolfArmorItem> WOLF_ARMOR_LEGGINGS = ITEMS.register("wolf_armor_leggings",
			() -> new WolfArmorItem(ArmorMaterials.DIAMOND, EquipmentSlot.LEGS, new Item.Properties().tab(GeckoLibMod.ITEM_GROUP)));
	public static final RegistryObject<WolfArmorItem> WOLF_ARMOR_BOOTS = ITEMS.register("wolf_armor_boots",
			() -> new WolfArmorItem(ArmorMaterials.DIAMOND, EquipmentSlot.FEET, new Item.Properties().tab(GeckoLibMod.ITEM_GROUP)));
}
