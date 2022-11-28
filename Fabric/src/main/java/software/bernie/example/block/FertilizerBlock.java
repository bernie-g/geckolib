package software.bernie.example.block;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Material;
import software.bernie.example.block.entity.FertilizerBlockEntity;
import software.bernie.example.client.renderer.block.FertilizerBlockRenderer;
import software.bernie.example.registry.BlockEntityRegistry;
import software.bernie.geckolib.GeckoLib;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Example animated block using GeckoLib animations.<br>
 * There's nothing to see here since the {@link Block} class itself has little to do with animations
 * @see software.bernie.example.client.model.block.FertilizerModel
 * @see FertilizerBlockEntity
 * @see FertilizerBlockRenderer
 */
public class FertilizerBlock extends DirectionalBlock implements EntityBlock {
	public FertilizerBlock() {
		super(Properties.of(Material.STONE).noOcclusion());
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return BlockEntityRegistry.FERTILIZER_BLOCK.create(pos, state);
	}

	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.ENTITYBLOCK_ANIMATED;
	}

	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return defaultBlockState().setValue(FACING, context.getNearestLookingDirection().getOpposite());
	}

	@Override
	public void appendHoverText(ItemStack stack, BlockGetter blockGetter, List<Component> tooltip, TooltipFlag tooltipFlag) {
		tooltip.add(Component.translatable("block." + GeckoLib.MOD_ID + ".fertilizer.tooltip"));

		super.appendHoverText(stack, blockGetter, tooltip, tooltipFlag);
	}
}
