package software.bernie.geckolib.example.tileentity;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import software.bernie.geckolib.manager.TileEntityAnimationManager;
import software.bernie.geckolib.tileentity.IAnimatedTileEntity;

public class JackInTheBoxTileEntity extends BlockEntity implements IAnimatedTileEntity
{
	public TileEntityAnimationManager animationManager = new TileEntityAnimationManager();

	public JackInTheBoxTileEntity(BlockEntityType<?> tileEntityTypeIn)
	{
		super(tileEntityTypeIn);
	}

	@Override
	public TileEntityAnimationManager getAnimationManager()
	{
		return animationManager;
	}
}
