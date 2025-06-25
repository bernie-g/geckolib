package anightdazingzoroark.example;

import anightdazingzoroark.example.client.renderer.item.BombRenderer;
import anightdazingzoroark.example.entity.*;
import anightdazingzoroark.example.item.BombItem;
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
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;
import anightdazingzoroark.example.block.BotariumBlock;
import anightdazingzoroark.example.block.FertilizerBlock;
import anightdazingzoroark.example.block.tile.BotariumTileEntity;
import anightdazingzoroark.example.block.tile.FertilizerTileEntity;
import anightdazingzoroark.example.item.PotatoArmorItem;
import anightdazingzoroark.example.registry.BlockRegistry;
import anightdazingzoroark.example.registry.ItemRegistry;
import anightdazingzoroark.example.registry.SoundRegistry;
import anightdazingzoroark.riftlib.RiftLib;

public class CommonListener {
	private static IForgeRegistry<Item> itemRegistry;
	private static IForgeRegistry<Block> blockRegistry;

	@SubscribeEvent
	public void onRegisterBlocks(RegistryEvent.Register<Block> event) {
		blockRegistry = event.getRegistry();
		BlockRegistry.BOTARIUM_BLOCK = new BotariumBlock();
		BlockRegistry.FERTILIZER_BLOCK = new FertilizerBlock();

		BlockRegistry.BOTARIUM_BLOCK.setCreativeTab(RiftLibMod.getRiftlibItemGroup());
		BlockRegistry.FERTILIZER_BLOCK.setCreativeTab(RiftLibMod.getRiftlibItemGroup());

		registerBlock(BlockRegistry.BOTARIUM_BLOCK, "botariumblock");
		registerBlock(BlockRegistry.FERTILIZER_BLOCK, "fertilizerblock");
	}

	@SubscribeEvent
	public void onRegisterEntities(RegistryEvent.Register<EntityEntry> event) {
		int id = 0;

		//entity register
		event.getRegistry().register(EntityEntryBuilder.create()
				.entity(DragonEntity.class)
				.name("dragon")
				.id(new ResourceLocation(RiftLib.ModID, "dragon"), id++)
				.tracker(160, 2, false)
				.build()
		);
		event.getRegistry().register(EntityEntryBuilder.create()
				.entity(FlyingPufferfishEntity.class)
				.name("flying_pufferfish")
				.id(new ResourceLocation(RiftLib.ModID, "flying_pufferfish"), id++)
				.tracker(160, 2, false)
				.build()
		);
		event.getRegistry().register(EntityEntryBuilder.create()
				.entity(BombProjectile.class)
				.name("bomb_projectile")
				.id(new ResourceLocation(RiftLib.ModID, "bomb_projectile"), id++)
				.tracker(160, 2, false)
				.build()
		);

		//egg registry
		EntityRegistry.registerEgg(new ResourceLocation(RiftLib.ModID, "dragon"), 0x980d0d, 0xca7824);
		EntityRegistry.registerEgg(new ResourceLocation(RiftLib.ModID, "flying_pufferfish"), 0xffae00, 0xbfc700);

		//tile entity registry
		GameRegistry.registerTileEntity(BotariumTileEntity.class, new ResourceLocation(RiftLib.ModID, "botariumtile"));
		GameRegistry.registerTileEntity(FertilizerTileEntity.class,
				new ResourceLocation(RiftLib.ModID, "fertilizertile"));
	}

	@SubscribeEvent
	public void onRegisterItems(RegistryEvent.Register<Item> event) {
		itemRegistry = event.getRegistry();
		ItemRegistry.BOMB = registerItem(new BombItem(), "bomb");

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
		registerItem(item, new ResourceLocation(RiftLib.ModID, name));
		return item;
	}

	public static <T extends Item> T registerItem(T item, ResourceLocation name) {
		itemRegistry.register(item.setRegistryName(name).setTranslationKey(name.toString().replace(":", ".")));
		return item;
	}

	public static void registerBlock(Block block, String name) {
		registerBlock(block, new ResourceLocation(RiftLib.ModID, name));
	}

	public static void registerBlock(Block block, ResourceLocation name) {
		blockRegistry.register(block.setRegistryName(name).setTranslationKey(name.toString().replace(":", ".")));
	}

	@SubscribeEvent
	public void onRegisterSoundEvents(RegistryEvent.Register<SoundEvent> event) {
		ResourceLocation location = new ResourceLocation(RiftLib.ModID, "jack_music");

		SoundRegistry.JACK_MUSIC = new SoundEvent(location).setRegistryName(location);

		event.getRegistry().register(SoundRegistry.JACK_MUSIC);
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onModelRegistry(ModelRegistryEvent event) {
		ModelLoader.setCustomModelResourceLocation(
				ItemRegistry.BOMB,
				0,
				new ModelResourceLocation(RiftLib.ModID+":bomb", "inventory")
		);
		ModelLoader.setCustomModelResourceLocation(ItemRegistry.BOTARIUM, 0,
				new ModelResourceLocation(RiftLib.ModID + ":botarium", "inventory"));
		ModelLoader.setCustomModelResourceLocation(ItemRegistry.FERTILIZER, 0,
				new ModelResourceLocation(RiftLib.ModID + ":fertilizer", "inventory"));
		ModelLoader.setCustomModelResourceLocation(ItemRegistry.POTATO_HEAD, 0,
				new ModelResourceLocation(RiftLib.ModID + ":potato_head", "inventory"));
		ModelLoader.setCustomModelResourceLocation(ItemRegistry.POTATO_CHEST, 0,
				new ModelResourceLocation(RiftLib.ModID + ":potato_chest", "inventory"));
		ModelLoader.setCustomModelResourceLocation(ItemRegistry.POTATO_LEGGINGS, 0,
				new ModelResourceLocation(RiftLib.ModID + ":potato_leggings", "inventory"));
		ModelLoader.setCustomModelResourceLocation(ItemRegistry.POTATO_BOOTS, 0,
				new ModelResourceLocation(RiftLib.ModID + ":potato_boots", "inventory"));

		ItemRegistry.BOMB.setTileEntityItemStackRenderer(new BombRenderer());
	}
}