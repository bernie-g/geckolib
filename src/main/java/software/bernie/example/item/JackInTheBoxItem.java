package software.bernie.example.item;

import net.minecraft.item.Item;
import software.bernie.geckolib.core.IAnimatable;
import software.bernie.geckolib.core.PlayState;
import software.bernie.geckolib.core.builder.AnimationBuilder;
import software.bernie.geckolib.core.controller.AnimationController;
import software.bernie.geckolib.core.event.predicate.AnimationEvent;
import software.bernie.geckolib.core.manager.AnimationData;
import software.bernie.geckolib.core.manager.AnimationFactory;

public class JackInTheBoxItem extends Item implements IAnimatable
{
	public AnimationFactory factory = new AnimationFactory(this);

	private <P extends Item & IAnimatable> PlayState predicate(AnimationEvent<P> event)
	{
		event.getController().setAnimation(new AnimationBuilder().addAnimation("Soaryn_chest_popup", true));
		return PlayState.CONTINUE;
	}

	public JackInTheBoxItem(Settings properties)
	{
		super(properties);
	}

	@Override
	public void registerControllers(AnimationData data)
	{
		data.addAnimationController(new AnimationController(this, "controller", 20, this::predicate));
	}

	@Override
	public AnimationFactory getFactory()
	{
		return this.factory;
	}
}
