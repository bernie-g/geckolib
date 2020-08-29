package software.bernie.geckolib.example.item;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import software.bernie.geckolib.animation.builder.AnimationBuilder;
import software.bernie.geckolib.entity.IAnimatable;
import software.bernie.geckolib.event.predicate.SpecialAnimationPredicate;
import software.bernie.geckolib.example.KeyboardHandler;
import software.bernie.geckolib.example.client.renderer.item.JackInTheBoxItemRenderer;
import software.bernie.geckolib.manager.AnimationManager;
import software.bernie.geckolib.block.SpecialAnimationController;

public class JackInTheBoxItem extends BlockItem implements IAnimatable
{
	public JackInTheBoxItem(Block blockIn)
	{
		super(blockIn, new Item.Properties().setISTER(() -> () -> new JackInTheBoxItemRenderer()));
		manager.addAnimationController(controller);
	}

	private final AnimationManager manager = new AnimationManager();
	private final SpecialAnimationController controller = new SpecialAnimationController(this, "controller", 0, this::predicate);

	private <E extends TileEntity & IAnimatable> boolean predicate(SpecialAnimationPredicate<E> eSpecialAnimationPredicate)
	{
		controller.transitionLengthTicks = 20;
		if (KeyboardHandler.isQDown)
		{
			controller.markNeedsReload();
		}
		controller.setAnimation(new AnimationBuilder().addAnimation("animation.model.ChestPopUp", false).addAnimation("animation.model.ChestPopUpIdle", false).addAnimation("animation.model.ChestClose").addAnimation("animation.model.ChestPeek"));
		return true;
	}

	@Override
	public AnimationManager getAnimationManager()
	{
		return manager;
	}
}
