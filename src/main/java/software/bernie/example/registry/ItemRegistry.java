package software.bernie.example.registry;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorMaterials;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import software.bernie.example.GeckoLibMod;
import software.bernie.example.item.JackInTheBoxItem;
import software.bernie.example.item.PotatoArmorItem;

public class ItemRegistry {
    public static final JackInTheBoxItem JACK_IN_THE_BOX = RegistryUtils.registerItem("jackintheboxitem", new JackInTheBoxItem(new Item.Settings().group(GeckoLibMod.geckolibItemGroup)));

    public static final PotatoArmorItem POTATO_HEAD = RegistryUtils.registerItem("potato_head", new PotatoArmorItem(ArmorMaterials.DIAMOND, EquipmentSlot.HEAD, new Item.Settings().group(GeckoLibMod.geckolibItemGroup)));
    public static final PotatoArmorItem POTATO_CHEST = RegistryUtils.registerItem("potato_chest", new PotatoArmorItem(ArmorMaterials.DIAMOND, EquipmentSlot.CHEST, new Item.Settings().group(GeckoLibMod.geckolibItemGroup)));
    public static final PotatoArmorItem POTATO_LEGGINGS = RegistryUtils.registerItem("potato_leggings", new PotatoArmorItem(ArmorMaterials.DIAMOND, EquipmentSlot.LEGS, new Item.Settings().group(GeckoLibMod.geckolibItemGroup)));
    public static final PotatoArmorItem POTATO_BOOTS = RegistryUtils.registerItem("potato_boots", new PotatoArmorItem(ArmorMaterials.DIAMOND, EquipmentSlot.FEET, new Item.Settings().group(GeckoLibMod.geckolibItemGroup)));
    public static final BlockItem BOTARIUM = RegistryUtils.registerItem("botarium", new BlockItem(BlockRegistry.BOTARIUM_BLOCK, new Item.Settings().group(GeckoLibMod.geckolibItemGroup)));
    public static final BlockItem FERTILIZER = RegistryUtils.registerItem("fertilizer", new BlockItem(BlockRegistry.FERTILIZER_BLOCK, new Item.Settings().group(GeckoLibMod.geckolibItemGroup)));
}
