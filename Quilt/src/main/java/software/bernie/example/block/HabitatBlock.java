package software.bernie.example.block;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import software.bernie.example.registry.TileRegistry;

public class HabitatBlock extends BaseEntityBlock implements EntityBlock {

	public static final DirectionProperty FACING = BlockStateProperties.FACING;

	public HabitatBlock() {
		super(BlockBehaviour.Properties.of(Material.GLASS).noOcclusion());
	}

	/*
	 * Hides the normal block and only shows the block entity created below
	 */
	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.ENTITYBLOCK_ANIMATED;
	}

	/*
	 * Adds that our block is faceable
	 */
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	/*
	 * Sets the correct facing, needed to flip this block on the 180, should have
	 * done in the model in BB but eh
	 */
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return this.defaultBlockState()
				.setValue(FACING,
				context.getHorizontalDirection().getClockWise().getClockWise());
	}

	/*
	 * Creates the block entity that we have playing our animations and rendering
	 * the block
	 */
	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return TileRegistry.HABITAT_TILE.create(pos, state);
	}

	/*
	 * Sets the correct shape depending on your facing
	 */
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		Direction direction = (Direction) state.getValue(FACING);
		switch (direction) {
		case NORTH: {
			return Block.box(0, 0, 0, 32, 16, 16);
		}
		case SOUTH: {
			return Block.box(-16, 0, 0, 16, 16, 16);
		}
		case WEST: {
			return Block.box(0, 0, -16, 16, 16, 16);
		}
		default:
			return Block.box(0, 0, 0, 16, 16, 32);
		}
	}

	/*
	 * Tests for air 1 block out from the facing pos to ensure it's air so the block
	 * doesn't place into another block
	 */
	@Override
	public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
		for (BlockPos testPos : BlockPos.betweenClosed(pos,
				pos.relative((Direction) state.getValue(FACING).getClockWise(), 2))) {
			if (!testPos.equals(pos) && !world.getBlockState(testPos).isAir())
				return false;
		}
		return true;
	}
}