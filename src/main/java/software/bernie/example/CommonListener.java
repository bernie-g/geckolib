package software.bernie.example;

import net.minecraft.block.Block;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.registry.GameRegistry;
import software.bernie.example.block.BotariumBlock;
import software.bernie.example.block.FertilizerBlock;
import software.bernie.example.block.tile.BotariumTileEntity;
import software.bernie.example.block.tile.FertilizerTileEntity;
import software.bernie.example.entity.BikeEntity;
import software.bernie.example.entity.GeoExampleEntity;
import software.bernie.example.item.JackInTheBoxItem;
import software.bernie.example.item.PotatoArmorItem;
import software.bernie.example.registry.BlockRegistry;
import software.bernie.example.registry.ItemRegistry;
import software.bernie.example.registry.SoundRegistry;
import software.bernie.geckolib3.GeckoLib;

public class CommonListener
{
	@SubscribeEvent
	public void onRegisterBlocks(RegistryEvent.Register<Block> event)
	{
		BlockRegistry.BOTARIUM_BLOCK = new BotariumBlock();
		BlockRegistry.FERTILIZER_BLOCK = new FertilizerBlock();

		BlockRegistry.BOTARIUM_BLOCK.setCreativeTab(GeckoLibMod.getGeckolibItemGroup());
		BlockRegistry.FERTILIZER_BLOCK.setCreativeTab(GeckoLibMod.getGeckolibItemGroup());

		event.getRegistry().register(BlockRegistry.BOTARIUM_BLOCK.setRegistryName(new ResourceLocation(GeckoLib.ModID, "botariumblock")));
		event.getRegistry().register(BlockRegistry.FERTILIZER_BLOCK.setRegistryName(new ResourceLocation(GeckoLib.ModID, "fertilizerblock")));
	}

	@SubscribeEvent
	public void onRegisterEntities(RegistryEvent.Register<EntityEntry> event)
	{
		int id = 0;

		event.getRegistry().register(EntityEntryBuilder.create().entity(BikeEntity.class).name("Bike").id(new ResourceLocation(GeckoLib.ModID, "bike"), id++).tracker(160, 2, false).build());
		event.getRegistry().register(EntityEntryBuilder.create().entity(GeoExampleEntity.class).name("Example").id(new ResourceLocation(GeckoLib.ModID, "example"), id++).tracker(160, 2, false).build());

		/* Tile entities */
		GameRegistry.registerTileEntity(BotariumTileEntity.class, new ResourceLocation(GeckoLib.ModID, "botariumtile"));
		GameRegistry.registerTileEntity(FertilizerTileEntity.class, new ResourceLocation(GeckoLib.ModID, "fertilizertile"));
	}

	@SubscribeEvent
	public void onRegisterItems(RegistryEvent.Register<Item> event)
	{
		ItemRegistry.JACK_IN_THE_BOX = new JackInTheBoxItem();

		ItemRegistry.POTATO_HEAD = new PotatoArmorItem(ItemArmor.ArmorMaterial.DIAMOND, 0, EntityEquipmentSlot.HEAD);
		ItemRegistry.POTATO_CHEST = new PotatoArmorItem(ItemArmor.ArmorMaterial.DIAMOND, 0, EntityEquipmentSlot.CHEST);
		ItemRegistry.POTATO_LEGGINGS = new PotatoArmorItem(ItemArmor.ArmorMaterial.DIAMOND, 0, EntityEquipmentSlot.LEGS);
		ItemRegistry.POTATO_BOOTS = new PotatoArmorItem(ItemArmor.ArmorMaterial.DIAMOND, 0, EntityEquipmentSlot.FEET);

		ItemRegistry.JACK_IN_THE_BOX.setRegistryName(new ResourceLocation(GeckoLib.ModID, "jackintheboxitem"));

		ItemRegistry.POTATO_HEAD.setRegistryName(new ResourceLocation(GeckoLib.ModID, "potato_head"));
		ItemRegistry.POTATO_CHEST.setRegistryName(new ResourceLocation(GeckoLib.ModID, "potato_chest"));
		ItemRegistry.POTATO_LEGGINGS.setRegistryName(new ResourceLocation(GeckoLib.ModID, "potato_leggings"));
		ItemRegistry.POTATO_BOOTS.setRegistryName(new ResourceLocation(GeckoLib.ModID, "potato_boots"));

		event.getRegistry().register(ItemRegistry.JACK_IN_THE_BOX);

		event.getRegistry().register(ItemRegistry.POTATO_HEAD);
		event.getRegistry().register(ItemRegistry.POTATO_CHEST);
		event.getRegistry().register(ItemRegistry.POTATO_LEGGINGS);
		event.getRegistry().register(ItemRegistry.POTATO_BOOTS);
	}

	@SubscribeEvent
	public void onRegisterSoundEvents(RegistryEvent.Register<SoundEvent> event)
	{
		ResourceLocation location = new ResourceLocation(GeckoLib.ModID, "jack_music");

		SoundRegistry.JACK_MUSIC = new SoundEvent(location).setRegistryName(location);

		event.getRegistry().register(SoundRegistry.JACK_MUSIC);
	}
}