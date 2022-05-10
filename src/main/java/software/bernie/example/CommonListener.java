package software.bernie.example;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;
import software.bernie.example.block.BotariumBlock;
import software.bernie.example.block.FertilizerBlock;
import software.bernie.example.block.tile.BotariumTileEntity;
import software.bernie.example.block.tile.FertilizerTileEntity;
import software.bernie.example.client.renderer.item.JackInTheBoxRenderer;
import software.bernie.example.entity.BikeEntity;
import software.bernie.example.entity.GeoExampleEntity;
import software.bernie.example.entity.GeoExampleEntityLayer;
import software.bernie.example.item.JackInTheBoxItem;
import software.bernie.example.item.PotatoArmorItem;
import software.bernie.example.registry.BlockRegistry;
import software.bernie.example.registry.ItemRegistry;
import software.bernie.example.registry.SoundRegistry;
import software.bernie.geckolib3.GeckoLib;

public class CommonListener {
	private static IForgeRegistry<Item> itemRegistry;
	private static IForgeRegistry<Block> blockRegistry;

	@SubscribeEvent
	public void onRegisterBlocks(RegistryEvent.Register<Block> event) {
		blockRegistry = event.getRegistry();
		BlockRegistry.BOTARIUM_BLOCK = new BotariumBlock();
		BlockRegistry.FERTILIZER_BLOCK = new FertilizerBlock();

		BlockRegistry.BOTARIUM_BLOCK.setCreativeTab(GeckoLibMod.getGeckolibItemGroup());
		BlockRegistry.FERTILIZER_BLOCK.setCreativeTab(GeckoLibMod.getGeckolibItemGroup());

		registerBlock(BlockRegistry.BOTARIUM_BLOCK, "botariumblock");
		registerBlock(BlockRegistry.FERTILIZER_BLOCK, "fertilizerblock");
	}

	@SubscribeEvent
	public void onRegisterEntities(RegistryEvent.Register<EntityEntry> event) {
		int id = 0;

		event.getRegistry().register(EntityEntryBuilder.create().entity(BikeEntity.class).name("Bike")
				.id(new ResourceLocation(GeckoLib.ModID, "bike"), id++).tracker(160, 2, false).build());
		event.getRegistry().register(EntityEntryBuilder.create().entity(GeoExampleEntity.class).name("Example")
				.id(new ResourceLocation(GeckoLib.ModID, "example"), id++).tracker(160, 2, false).build());
		event.getRegistry()
				.register(EntityEntryBuilder.create().entity(GeoExampleEntityLayer.class).name("ExampleLayer")
						.id(new ResourceLocation(GeckoLib.ModID, "examplelayer"), id++).tracker(160, 2, false).build());

		/* Tile entities */
		GameRegistry.registerTileEntity(BotariumTileEntity.class, new ResourceLocation(GeckoLib.ModID, "botariumtile"));
		GameRegistry.registerTileEntity(FertilizerTileEntity.class,
				new ResourceLocation(GeckoLib.ModID, "fertilizertile"));
	}

	@SubscribeEvent
	public void onRegisterItems(RegistryEvent.Register<Item> event) {
		itemRegistry = event.getRegistry();
		ItemRegistry.JACK_IN_THE_BOX = registerItem(new JackInTheBoxItem(), "jackintheboxitem");

		ItemRegistry.POTATO_HEAD = registerItem(
				new PotatoArmorItem(ItemArmor.ArmorMaterial.DIAMOND, 0, EntityEquipmentSlot.HEAD), "potato_head");
		ItemRegistry.POTATO_CHEST = registerItem(
				new PotatoArmorItem(ItemArmor.ArmorMaterial.DIAMOND, 0, EntityEquipmentSlot.CHEST), "potato_chest");
		ItemRegistry.POTATO_LEGGINGS = registerItem(
				new PotatoArmorItem(ItemArmor.ArmorMaterial.DIAMOND, 0, EntityEquipmentSlot.LEGS), "potato_leggings");
		ItemRegistry.POTATO_BOOTS = registerItem(
				new PotatoArmorItem(ItemArmor.ArmorMaterial.DIAMOND, 0, EntityEquipmentSlot.FEET), "potato_boots");

		ItemRegistry.BOTARIUM = registerItem(new ItemBlock(BlockRegistry.BOTARIUM_BLOCK), "botarium");
		ItemRegistry.FERTILIZER = registerItem(new ItemBlock(BlockRegistry.FERTILIZER_BLOCK), "fertilizer");
	}

	public static <T extends Item> T registerItem(T item, String name) {
		registerItem(item, new ResourceLocation(GeckoLib.ModID, name));
		return item;
	}

	public static <T extends Item> T registerItem(T item, ResourceLocation name) {
		itemRegistry.register(item.setRegistryName(name).setUnlocalizedName(name.toString().replace(":", ".")));
		return item;
	}

	public static void registerBlock(Block block, String name) {
		registerBlock(block, new ResourceLocation(GeckoLib.ModID, name));
	}

	public static void registerBlock(Block block, ResourceLocation name) {
		blockRegistry.register(block.setRegistryName(name).setUnlocalizedName(name.toString().replace(":", ".")));
	}

	@SubscribeEvent
	public void onRegisterSoundEvents(RegistryEvent.Register<SoundEvent> event) {
		ResourceLocation location = new ResourceLocation(GeckoLib.ModID, "jack_music");

		SoundRegistry.JACK_MUSIC = new SoundEvent(location).setRegistryName(location);

		event.getRegistry().register(SoundRegistry.JACK_MUSIC);
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onModelRegistry(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(ItemRegistry.JACK_IN_THE_BOX, 0,
				new ModelResourceLocation(GeckoLib.ModID + ":jackintheboxitem", "inventory"));
		ModelLoader.setCustomModelResourceLocation(ItemRegistry.BOTARIUM, 0,
				new ModelResourceLocation(GeckoLib.ModID + ":botarium", "inventory"));
		ModelLoader.setCustomModelResourceLocation(ItemRegistry.FERTILIZER, 0,
				new ModelResourceLocation(GeckoLib.ModID + ":fertilizer", "inventory"));
		ModelLoader.setCustomModelResourceLocation(ItemRegistry.POTATO_HEAD, 0,
				new ModelResourceLocation(GeckoLib.ModID + ":potato_head", "inventory"));
		ModelLoader.setCustomModelResourceLocation(ItemRegistry.POTATO_CHEST, 0,
				new ModelResourceLocation(GeckoLib.ModID + ":potato_chest", "inventory"));
		ModelLoader.setCustomModelResourceLocation(ItemRegistry.POTATO_LEGGINGS, 0,
				new ModelResourceLocation(GeckoLib.ModID + ":potato_leggings", "inventory"));
		ModelLoader.setCustomModelResourceLocation(ItemRegistry.POTATO_BOOTS, 0,
				new ModelResourceLocation(GeckoLib.ModID + ":potato_boots", "inventory"));

		ItemRegistry.JACK_IN_THE_BOX.setTileEntityItemStackRenderer(new JackInTheBoxRenderer());
	}
}