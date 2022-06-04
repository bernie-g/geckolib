package software.bernie.example.block;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import software.bernie.example.registry.TileRegistry;

public class HabitatBlock extends DirectionalBlock implements EntityBlock {
	public HabitatBlock() {
		super(Properties.of(Material.STONE).noOcclusion());
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
		return TileRegistry.HABITAT_TILE.get().create(blockPos, blockState);
	}

	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.ENTITYBLOCK_ANIMATED;
	}

	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rotation) {
		return (BlockState) state.setValue(FACING, rotation.rotate((Direction) state.getValue(FACING)));
	}

	@SuppressWarnings("deprecation")
	@Override
	public BlockState mirror(BlockState state, Mirror mirror) {
		return state.rotate(mirror.getRotation((Direction) state.getValue(FACING)));
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return this.defaultBlockState().setValue(FACING, context.getNearestLookingDirection().getOpposite());
	}

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

	@Override
	public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
		Direction direction = (Direction) state.getValue(FACING);
		switch (direction) {
		case NORTH: {
			return world.getBlockState(pos.east()).isAir();
		}
		case SOUTH: {
			return world.getBlockState(pos.west()).isAir();
		}
		case WEST: {
			return world.getBlockState(pos.north()).isAir();
		}
		default:
			return world.getBlockState(pos.south()).isAir();
		}
	}
}
