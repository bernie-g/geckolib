package software.bernie.geckolib.example.tileentity;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import software.bernie.geckolib.manager.TileEntityAnimationManager;
import software.bernie.geckolib.tileentity.IAnimatedTileEntity;

public class JackInTheBoxTileEntity extends TileEntity implements IAnimatedTileEntity
{
	public TileEntityAnimationManager animationManager = new TileEntityAnimationManager();

	public JackInTheBoxTileEntity(TileEntityType<?> tileEntityTypeIn)
	{
		super(tileEntityTypeIn);
	}

	@Override
	public TileEntityAnimationManager getAnimationManager()
	{
		return animationManager;
	}
}
