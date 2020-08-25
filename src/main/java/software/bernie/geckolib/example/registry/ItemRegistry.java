package software.bernie.geckolib.example.registry;

import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import software.bernie.geckolib.GeckoLib;
import software.bernie.geckolib.example.item.JackInTheBoxItem;
import software.bernie.geckolib.example.item.PotatoArmor;

public class ItemRegistry
{
	public static final DeferredRegister<Item> ITEMS = new DeferredRegister<>(ForgeRegistries.ITEMS, GeckoLib.ModID);

	public static final RegistryObject<BlockItem> JACK_IN_THE_BOX_ITEM = ITEMS.register("jackintheboxitem", () -> new JackInTheBoxItem(BlockRegistry.JACK_IN_THE_BOX.get()));

	public static final RegistryObject<ArmorItem> POTATO_HELMET = ITEMS.register("potato_helmet", () -> new PotatoArmor(EquipmentSlotType.HEAD));
	public static final RegistryObject<ArmorItem> POTATO_CHEST = ITEMS.register("potato_chest", () -> new PotatoArmor(EquipmentSlotType.CHEST));
	public static final RegistryObject<ArmorItem> POTATO_LEGS = ITEMS.register("potato_legs", () -> new PotatoArmor(EquipmentSlotType.LEGS));
	public static final RegistryObject<ArmorItem> POTATO_BOOTS = ITEMS.register("potato_boots", () -> new PotatoArmor(EquipmentSlotType.FEET));

}
