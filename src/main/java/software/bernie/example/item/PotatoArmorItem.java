package software.bernie.example.item;

import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IArmorMaterial;
import software.bernie.geckolib.core.IAnimatable;
import software.bernie.geckolib.core.PlayState;
import software.bernie.geckolib.core.builder.AnimationBuilder;
import software.bernie.geckolib.core.controller.AnimationController;
import software.bernie.geckolib.core.event.predicate.AnimationEvent;
import software.bernie.geckolib.core.manager.AnimationManager;
import software.bernie.geckolib.item.GeoArmorItem;

public class PotatoArmorItem extends GeoArmorItem implements IAnimatable
{
	private AnimationManager manager = new AnimationManager();
	private AnimationController controller = new AnimationController(this, "controller", 20, this::predicate);

	private <P extends IAnimatable> PlayState predicate(AnimationEvent<P> event)
	{
		this.controller.setAnimation(new AnimationBuilder().addAnimation("animation.potato_armor.new", true));
		return PlayState.CONTINUE;
	}

	public PotatoArmorItem(IArmorMaterial materialIn, EquipmentSlotType slot, Properties builder)
	{
		super(materialIn, slot, builder);
		this.manager.addAnimationController(controller);
	}

	@Override
	public AnimationManager getAnimationManager()
	{
		return manager;
	}
}
