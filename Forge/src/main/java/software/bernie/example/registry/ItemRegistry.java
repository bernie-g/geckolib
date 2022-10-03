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
import software.bernie.example.item.GeckoArmorItem;
import software.bernie.geckolib3.GeckoLib;

public class ItemRegistry {
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, GeckoLib.ModID);

	public static final RegistryObject<BlockItem> HABITAT = ITEMS.register("habitat",
			() -> new BlockItem(BlockRegistry.HABITAT_BLOCK.get(),
					new Item.Properties().tab(GeckoLibMod.geckolibItemGroup)));
	public static final RegistryObject<BlockItem> FERTILIZER_ITEM = ITEMS.register("fertilizer",
			() -> new BlockItem(BlockRegistry.FERTILIZER_BLOCK.get(),
					new Item.Properties().tab(GeckoLibMod.geckolibItemGroup)));

	public static final RegistryObject<JackInTheBoxItem> JACK_IN_THE_BOX = ITEMS.register("jackintheboxitem",
			() -> new JackInTheBoxItem(new Item.Properties()));

	public static final RegistryObject<PistolItem> PISTOL = ITEMS.register("pistol", () -> new PistolItem());

	public static final RegistryObject<GeckoArmorItem> GECKOARMOR_HEAD = ITEMS.register("geckoarmor_head",
			() -> new GeckoArmorItem(ArmorMaterials.DIAMOND, EquipmentSlot.HEAD, new Item.Properties()));
	public static final RegistryObject<GeckoArmorItem> GECKOARMOR_CHEST = ITEMS.register("geckoarmor_chest",
			() -> new GeckoArmorItem(ArmorMaterials.DIAMOND, EquipmentSlot.CHEST, new Item.Properties()));
	public static final RegistryObject<GeckoArmorItem> GECKOARMOR_LEGGINGS = ITEMS.register("geckoarmor_leggings",
			() -> new GeckoArmorItem(ArmorMaterials.DIAMOND, EquipmentSlot.LEGS, new Item.Properties()));
	public static final RegistryObject<GeckoArmorItem> GECKOARMOR_BOOTS = ITEMS.register("geckoarmor_boots",
			() -> new GeckoArmorItem(ArmorMaterials.DIAMOND, EquipmentSlot.FEET, new Item.Properties()));

}
