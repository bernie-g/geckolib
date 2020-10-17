package software.bernie.example.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import software.bernie.geckolib.core.AnimationState;
import software.bernie.geckolib.core.IAnimatable;
import software.bernie.geckolib.core.PlayState;
import software.bernie.geckolib.core.builder.AnimationBuilder;
import software.bernie.geckolib.core.controller.AnimationController;
import software.bernie.geckolib.core.event.predicate.AnimationEvent;
import software.bernie.geckolib.core.manager.AnimationData;
import software.bernie.geckolib.core.manager.AnimationFactory;
import software.bernie.geckolib.util.GeckoLibUtil;

public class JackInTheBoxItem extends Item implements IAnimatable {
    public AnimationFactory factory = new AnimationFactory(this);
    private String controllerName = "popupController";

    public JackInTheBoxItem(Settings properties) {
        super(properties);
    }

    private <P extends Item & IAnimatable> PlayState predicate(AnimationEvent<P> event) {
        //Not setting an animation here as that's handled in onItemRightClick
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController(this, controllerName, 20, this::predicate));
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient)
        {
            return super.use(world, user, hand);
        }
        // Gets the item that the player is holding, should be a JackInTheBox
        ItemStack stack = user.getStackInHand(hand);

        // Always use GeckoLibUtil to get animationcontrollers when you don't have access to an AnimationEvent
        AnimationController controller = GeckoLibUtil.getControllerForStack(this.factory, stack, controllerName);

        if (controller.getAnimationState() == AnimationState.Stopped)
        {
            user.sendMessage(new LiteralText("Opening the jack in the box!"), true);
            // If you don't do this, the popup animation will only play once because the animation will be cached.
            controller.markNeedsReload();
            //Set the animation to open the jackinthebox which will start playing music and eventually do the actual animation. Also sets it to not loop
            controller.setAnimation(new AnimationBuilder().addAnimation("Soaryn_chest_popup", false));
        }
        return super.use(world, user, hand);
    }
}
