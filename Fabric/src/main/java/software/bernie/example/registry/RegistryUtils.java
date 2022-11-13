package software.bernie.example.registry;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import software.bernie.geckolib3.GeckoLib;

public class RegistryUtils {

	public static <B extends Block> B register(String name, B block) {
		return register(block, new ResourceLocation(GeckoLib.ModID, name));
	}

	public static <B extends Block> B register(B block, ResourceLocation name) {
		if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
			Registry.register(BuiltInRegistries.BLOCK, name, block);
			BlockItem item = new BlockItem(block, (new Properties()));
			item.registerBlocks(Item.BY_BLOCK, item);
			Registry.register(BuiltInRegistries.ITEM, name, item);
		}
		return block;
	}

	public static <B extends Block> B registerBlockWithoutItem(B block, ResourceLocation ResourceLocation) {
		if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
			Registry.register(BuiltInRegistries.BLOCK, ResourceLocation, block);
		}
		return block;
	}

	public static <B extends Block> B registerBlockWithoutItem(String name, B block) {
		if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
			Registry.register(BuiltInRegistries.BLOCK, new ResourceLocation(GeckoLib.ModID, name), block);
		}
		return block;
	}

	public static <I extends Item> I registerItem(I item, ResourceLocation name) {
		if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
			return Registry.register(BuiltInRegistries.ITEM, name, item);
		}
		return null;
	}

	public static <I extends Item> I registerItem(String name, I item) {
		if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
			return Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(GeckoLib.ModID, name), item);
		}
		return null;
	}

	public static <T extends BlockEntity> BlockEntityType<T> registerBlockEntity(String name,
			BlockEntityType.Builder<T> builder) {
		if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
			BlockEntityType<T> blockEntityType = builder.build(null);
			Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, new ResourceLocation(GeckoLib.ModID, name), blockEntityType);
			return blockEntityType;
		}
		return null;
	}

	public static Block registerLog(ResourceLocation name, MaterialColor MapColor, MaterialColor MapColor2) {
		return register(new RotatedPillarBlock(BlockBehaviour.Properties.of(Material.WOOD,
				(blockState) -> blockState.getValue(RotatedPillarBlock.AXIS) == Direction.Axis.Y ? MapColor : MapColor2)
				.strength(2.0F).sound(SoundType.WOOD)), name);
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
