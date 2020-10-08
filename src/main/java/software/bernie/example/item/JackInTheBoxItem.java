package software.bernie.example.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import software.bernie.geckolib.core.IAnimatable;
import software.bernie.geckolib.core.PlayState;
import software.bernie.geckolib.core.builder.AnimationBuilder;
import software.bernie.geckolib.core.controller.AnimationController;
import software.bernie.geckolib.core.event.predicate.AnimationEvent;
import software.bernie.geckolib.core.manager.AnimationManager;

public class JackInTheBoxItem extends Item implements IAnimatable
{
	public AnimationManager manager = new AnimationManager();
	public AnimationController controller = new AnimationController(this, "controller", 20, this::predicate);

	private <P extends Item & IAnimatable> PlayState predicate(AnimationEvent<P> event)
	{
		ItemStack itemstack = event.getExtraDataOfType(ItemStack.class).get(0);
		this.controller.setAnimation(new AnimationBuilder().addAnimation("Soaryn_chest_popup", true));
		return PlayState.CONTINUE;
	}

	public JackInTheBoxItem(Properties properties)
	{
		super(properties);
		this.manager.addAnimationController(controller);
	}

	@Override
	public AnimationManager getAnimationManager()
	{
		return manager;
	}
}
