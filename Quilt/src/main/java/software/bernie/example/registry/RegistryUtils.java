package software.bernie.example.registry;

import org.quiltmc.loader.api.QuiltLoader;

import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import software.bernie.geckolib3q.GeckoLib;

public class RegistryUtils {

	public static <B extends Block> B register(B block, ResourceLocation name) {
		return register(block, name, CreativeModeTab.TAB_DECORATIONS);
	}

	public static <B extends Block> B register(String name, B block) {
		return register(block, new ResourceLocation(GeckoLib.ModID, name), CreativeModeTab.TAB_DECORATIONS);
	}

	public static <B extends Block> B register(String name, B block, CreativeModeTab itemGroup) {
		return register(block, new ResourceLocation(GeckoLib.ModID, name), itemGroup);
	}

	public static <B extends Block> B register(B block, ResourceLocation name, CreativeModeTab itemGroup) {
		if (QuiltLoader.isDevelopmentEnvironment()) {
			Registry.register(Registry.BLOCK, name, block);
			BlockItem item = new BlockItem(block, (new Properties()).tab(itemGroup));
			item.registerBlocks(Item.BY_BLOCK, item);
			Registry.register(Registry.ITEM, name, item);
		}
		return block;
	}

	public static <B extends Block> B registerBlockWithoutItem(B block, ResourceLocation ResourceLocation) {
		if (QuiltLoader.isDevelopmentEnvironment()) {
			Registry.register(Registry.BLOCK, ResourceLocation, block);
		}
		return block;
	}

	public static <B extends Block> B registerBlockWithoutItem(String name, B block) {
		if (QuiltLoader.isDevelopmentEnvironment()) {
			Registry.register(Registry.BLOCK, new ResourceLocation(GeckoLib.ModID, name), block);
		}
		return block;
	}

	public static <I extends Item> I registerItem(I item, ResourceLocation name) {
		if (QuiltLoader.isDevelopmentEnvironment()) {
			return Registry.register(Registry.ITEM, name, item);
		}
		return null;
	}

	public static <I extends Item> I registerItem(String name, I item) {
		if (QuiltLoader.isDevelopmentEnvironment()) {
			return Registry.register(Registry.ITEM, new ResourceLocation(GeckoLib.ModID, name), item);
		}
		return null;
	}

	public static Block registerBlockWithWallBlock(Block block, Block wallBlock, ResourceLocation name) {
		Registry.register(Registry.BLOCK, name, block);
		Registry.register(Registry.ITEM, name,
				new StandingAndWallBlockItem(block, wallBlock, new Properties().tab(CreativeModeTab.TAB_DECORATIONS)));
		return block;
	}

	public static <T extends BlockEntity> BlockEntityType<T> registerBlockEntity(String name,
			BlockEntityType.Builder<T> builder) {
		if (QuiltLoader.isDevelopmentEnvironment()) {
			BlockEntityType<T> blockEntityType = builder.build(null);
			Registry.register(Registry.BLOCK_ENTITY_TYPE, new ResourceLocation(GeckoLib.ModID, name), blockEntityType);
			return blockEntityType;
		}
		return null;
	}

	public static Block registerNetherStem(ResourceLocation name, MaterialColor MapColor) {
		return register(new RotatedPillarBlock(BlockBehaviour.Properties.of(Material.WOOD, (blockState) -> MapColor)
				.strength(1.0F).sound(SoundType.STEM)), name);
	}

	public static Block registerLog(ResourceLocation name, MaterialColor MapColor, MaterialColor MapColor2) {
		return register(new RotatedPillarBlock(BlockBehaviour.Properties.of(Material.WOOD,
				(blockState) -> blockState.getValue(RotatedPillarBlock.AXIS) == Direction.Axis.Y ? MapColor : MapColor2)
				.strength(2.0F).sound(SoundType.WOOD)), name);
	}

	public static Block registerNetherStem(String name, MaterialColor MapColor) {
		return register(
				new RotatedPillarBlock(BlockBehaviour.Properties.of(Material.WOOD, (blockState) -> MapColor)
						.strength(1.0F).sound(SoundType.STEM)),
				new ResourceLocation(GeckoLib.ModID, name), CreativeModeTab.TAB_BUILDING_BLOCKS);
	}

	public static Block registerLog(String name, MaterialColor MapColor, MaterialColor MapColor2) {
		return register(
				new RotatedPillarBlock(BlockBehaviour.Properties.of(Material.WOOD,
						(blockState) -> blockState.getValue(RotatedPillarBlock.AXIS) == Direction.Axis.Y ? MapColor
								: MapColor2)
						.strength(2.0F).sound(SoundType.WOOD)),
				new ResourceLocation(GeckoLib.ModID, name));
	}

}
