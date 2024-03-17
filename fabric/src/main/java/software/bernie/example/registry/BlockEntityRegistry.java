package software.bernie.example.registry;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import software.bernie.example.block.entity.BotariumBlockEntity;
import software.bernie.example.block.entity.GeckoHabitatBlockEntity;
import software.bernie.geckolib.GeckoLibConstants;

public final class BlockEntityRegistry {
	public static final BlockEntityType<GeckoHabitatBlockEntity> GECKO_HABITAT = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, GeckoLibConstants.MODID + ":gecko_habitat", FabricBlockEntityTypeBuilder.create(GeckoHabitatBlockEntity::new, BlockRegistry.GECKO_HABITAT_BLOCK).build(null));
	public static final BlockEntityType<BotariumBlockEntity> BOTARIUM_BLOCK = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, GeckoLibConstants.MODID + ":botarium", FabricBlockEntityTypeBuilder.create(BotariumBlockEntity::new, BlockRegistry.BOTARIUM_BLOCK).build(null));
}
