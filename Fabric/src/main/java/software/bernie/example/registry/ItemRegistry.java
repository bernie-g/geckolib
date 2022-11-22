package software.bernie.example.registry;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.*;
import software.bernie.example.item.GeckoArmorItem;
import software.bernie.example.item.JackInTheBoxItem;
import software.bernie.example.item.PistolItem;
import software.bernie.example.item.WolfArmorItem;
import software.bernie.geckolib.GeckoLib;

public class ItemRegistry {

    public static final JackInTheBoxItem JACK_IN_THE_BOX = registerItem("jack_in_the_box", new JackInTheBoxItem(new Item.Properties()));
    public static final PistolItem PISTOL = registerItem("pistol", new PistolItem());

    public static final GeckoArmorItem GECKO_ARMOR_HELMET = registerItem("gecko_armor_helmet", new GeckoArmorItem(ArmorMaterials.NETHERITE, EquipmentSlot.HEAD, new Item.Properties()));
    public static final GeckoArmorItem GECKO_ARMOR_CHESTPLATE = registerItem("gecko_armor_chestplate", new GeckoArmorItem(ArmorMaterials.NETHERITE, EquipmentSlot.CHEST, new Item.Properties()));
    public static final GeckoArmorItem GECKO_ARMOR_LEGGINGS = registerItem("gecko_armor_leggings", new GeckoArmorItem(ArmorMaterials.NETHERITE, EquipmentSlot.LEGS, new Item.Properties()));
    public static final GeckoArmorItem GECKO_ARMOR_BOOTS = registerItem("gecko_armor_boots", new GeckoArmorItem(ArmorMaterials.NETHERITE, EquipmentSlot.FEET, new Item.Properties()));

    public static final WolfArmorItem WOLF_ARMOR_HELMET = registerItem("wolf_armor_helmet", new WolfArmorItem(ArmorMaterials.DIAMOND, EquipmentSlot.HEAD, new Item.Properties()));
    public static final WolfArmorItem WOLF_ARMOR_CHESTPLATE = registerItem("wolf_armor_chestplate", new WolfArmorItem(ArmorMaterials.DIAMOND, EquipmentSlot.CHEST, new Item.Properties()));
    public static final WolfArmorItem WOLF_ARMOR_LEGGINGS = registerItem("wolf_armor_leggings", new WolfArmorItem(ArmorMaterials.DIAMOND, EquipmentSlot.LEGS, new Item.Properties()));
    public static final WolfArmorItem WOLF_ARMOR_BOOTS = registerItem("wolf_armor_boots", new WolfArmorItem(ArmorMaterials.DIAMOND, EquipmentSlot.FEET, new Item.Properties()));

    public static final CreativeModeTab ITEMS_GROUP = FabricItemGroup
            .builder(new ResourceLocation(GeckoLib.ModID, "geckolib_examples"))
            .icon(() -> new ItemStack(ItemRegistry.JACK_IN_THE_BOX))
            .displayItems((enabledFeatures, entries, operatorEnabled) -> {
                entries.accept(ItemRegistry.JACK_IN_THE_BOX);
                entries.accept(ItemRegistry.PISTOL);
                entries.accept(ItemRegistry.GECKO_ARMOR_HELMET);
                entries.accept(ItemRegistry.GECKO_ARMOR_CHESTPLATE);
                entries.accept(ItemRegistry.GECKO_ARMOR_LEGGINGS);
                entries.accept(ItemRegistry.GECKO_ARMOR_BOOTS);
                entries.accept(ItemRegistry.WOLF_ARMOR_HELMET);
                entries.accept(ItemRegistry.WOLF_ARMOR_CHESTPLATE);
                entries.accept(ItemRegistry.WOLF_ARMOR_LEGGINGS);
                entries.accept(ItemRegistry.WOLF_ARMOR_BOOTS);
                entries.accept(BlockRegistry.GECKO_HABITAT_BLOCK);
                entries.accept(BlockRegistry.FERTILIZER_BLOCK);
            }).build();

    public static <I extends Item> I registerItem(String name, I item) {
        return Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(GeckoLib.ModID, name), item);
    }
}
