package software.bernie.example.registry;

import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorMaterials;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import software.bernie.example.item.JackInTheBoxItem;
import software.bernie.example.item.PistolItem;
import software.bernie.example.item.GeckoArmorItem;
import software.bernie.geckolib3.GeckoLib;

public class ItemRegistry {

	public static ItemGroup geckolibItemGroup = FabricItemGroupBuilder
			.create(new Identifier(GeckoLib.ModID, "geckolib_examples"))
			.icon(() -> new ItemStack(ItemRegistry.JACK_IN_THE_BOX)).build();

	public static final JackInTheBoxItem JACK_IN_THE_BOX = RegistryUtils.registerItem("jackintheboxitem",
			new JackInTheBoxItem(new Item.Settings().group(geckolibItemGroup)));
	public static final PistolItem PISTOL = RegistryUtils.registerItem("pistol", new PistolItem());
	public static final GeckoArmorItem GECKOARMOR_HEAD = RegistryUtils.registerItem("geckoarmor_head", new GeckoArmorItem(
			ArmorMaterials.DIAMOND, EquipmentSlot.HEAD, new Item.Settings().group(geckolibItemGroup)));
	public static final GeckoArmorItem GECKOARMOR_CHEST = RegistryUtils.registerItem("geckoarmor_chest", new GeckoArmorItem(
			ArmorMaterials.DIAMOND, EquipmentSlot.CHEST, new Item.Settings().group(geckolibItemGroup)));
	public static final GeckoArmorItem GECKOARMOR_LEGGINGS = RegistryUtils.registerItem("geckoarmor_leggings",
			new GeckoArmorItem(ArmorMaterials.DIAMOND, EquipmentSlot.LEGS,
					new Item.Settings().group(geckolibItemGroup)));
	public static final GeckoArmorItem GECKOARMOR_BOOTS = RegistryUtils.registerItem("geckoarmor_boots", new GeckoArmorItem(
			ArmorMaterials.DIAMOND, EquipmentSlot.FEET, new Item.Settings().group(geckolibItemGroup)));
	public static final BlockItem HABITAT = RegistryUtils.registerItem("habitat",
			new BlockItem(BlockRegistry.HABITAT_BLOCK, new Item.Settings().group(geckolibItemGroup)));
	public static final BlockItem FERTILIZER = RegistryUtils.registerItem("fertilizer",
			new BlockItem(BlockRegistry.FERTILIZER_BLOCK, new Item.Settings().group(geckolibItemGroup)));
}
