package software.bernie.example.registry;

import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import software.bernie.example.GeckoLibMod;
import software.bernie.example.client.renderer.item.JackInTheBoxRenderer;
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
			() -> new JackInTheBoxItem(new Item.Properties().setISTER(() -> JackInTheBoxRenderer::new)));

	public static final RegistryObject<PistolItem> PISTOL = ITEMS.register("pistol", () -> new PistolItem());

	public static final RegistryObject<GeckoArmorItem> GECKOARMOR_HEAD = ITEMS.register("geckoarmor_head",
			() -> new GeckoArmorItem(ArmorMaterial.DIAMOND, EquipmentSlotType.HEAD, new Item.Properties()));
	public static final RegistryObject<GeckoArmorItem> GECKOARMOR_CHEST = ITEMS.register("geckoarmor_chest",
			() -> new GeckoArmorItem(ArmorMaterial.DIAMOND, EquipmentSlotType.CHEST, new Item.Properties()));
	public static final RegistryObject<GeckoArmorItem> GECKOARMOR_LEGGINGS = ITEMS.register("geckoarmor_leggings",
			() -> new GeckoArmorItem(ArmorMaterial.DIAMOND, EquipmentSlotType.LEGS, new Item.Properties()));
	public static final RegistryObject<GeckoArmorItem> GECKOARMOR_BOOTS = ITEMS.register("geckoarmor_boots",
			() -> new GeckoArmorItem(ArmorMaterial.DIAMOND, EquipmentSlotType.FEET, new Item.Properties()));

}
