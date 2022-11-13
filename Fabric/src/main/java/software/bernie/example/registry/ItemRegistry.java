package software.bernie.example.registry;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import software.bernie.example.item.GeckoArmorItem;
import software.bernie.example.item.JackInTheBoxItem;
import software.bernie.example.item.PistolItem;
import software.bernie.geckolib3.GeckoLib;

public class ItemRegistry {

	public static final CreativeModeTab geckolibItemGroup = FabricItemGroup
			.builder(new ResourceLocation(GeckoLib.ModID, "geckolib_examples"))
			.icon(() -> new ItemStack(ItemRegistry.JACK_IN_THE_BOX))
			.displayItems((enabledFeatures, entries, operatorEnabled) -> {
				entries.accept(ItemRegistry.JACK_IN_THE_BOX);
				entries.accept(ItemRegistry.PISTOL);
				entries.accept(ItemRegistry.GECKOARMOR_HEAD);
				entries.accept(ItemRegistry.GECKOARMOR_CHEST);
				entries.accept(ItemRegistry.GECKOARMOR_LEGGINGS);
				entries.accept(ItemRegistry.GECKOARMOR_BOOTS);
				entries.accept(ItemRegistry.HABITAT);
				entries.accept(ItemRegistry.FERTILIZER);
			}).build();

	public static final JackInTheBoxItem JACK_IN_THE_BOX = RegistryUtils.registerItem("jackintheboxitem",
			new JackInTheBoxItem(new Item.Properties()));
	public static final PistolItem PISTOL = RegistryUtils.registerItem("pistol", new PistolItem());
	public static final GeckoArmorItem GECKOARMOR_HEAD = RegistryUtils.registerItem("geckoarmor_head",
			new GeckoArmorItem(ArmorMaterials.DIAMOND, EquipmentSlot.HEAD, new Item.Properties()));
	public static final GeckoArmorItem GECKOARMOR_CHEST = RegistryUtils.registerItem("geckoarmor_chest",
			new GeckoArmorItem(ArmorMaterials.DIAMOND, EquipmentSlot.CHEST, new Item.Properties()));
	public static final GeckoArmorItem GECKOARMOR_LEGGINGS = RegistryUtils.registerItem("geckoarmor_leggings",
			new GeckoArmorItem(ArmorMaterials.DIAMOND, EquipmentSlot.LEGS, new Item.Properties()));
	public static final GeckoArmorItem GECKOARMOR_BOOTS = RegistryUtils.registerItem("geckoarmor_boots",
			new GeckoArmorItem(ArmorMaterials.DIAMOND, EquipmentSlot.FEET, new Item.Properties()));
	public static final BlockItem HABITAT = RegistryUtils.registerItem("habitat",
			new BlockItem(BlockRegistry.HABITAT_BLOCK, new Item.Properties()));
	public static final BlockItem FERTILIZER = RegistryUtils.registerItem("fertilizer",
			new BlockItem(BlockRegistry.FERTILIZER_BLOCK, new Item.Properties()));
}
