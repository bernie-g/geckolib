package anightdazingzoroark.example.item;

import anightdazingzoroark.example.RiftLibMod;
import anightdazingzoroark.example.entity.BombProjectile;
import anightdazingzoroark.riftlib.core.IAnimatable;
import anightdazingzoroark.riftlib.core.manager.AnimationData;
import anightdazingzoroark.riftlib.core.manager.AnimationFactory;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class BombItem extends Item implements IAnimatable {
    private final AnimationFactory factory = new AnimationFactory(this);

    public BombItem() {
        super();
        this.maxStackSize = 1;
        this.setCreativeTab(RiftLibMod.getRiftlibItemGroup());
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        player.setActiveHand(hand);
        return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
    }

    public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityLivingBase entityLiving, int timeLeft) {
        if (!worldIn.isRemote) {
            EntityPlayer player = (EntityPlayer)entityLiving;
            BombProjectile bomb = new BombProjectile(worldIn, player);
            bomb.shoot(player, player.rotationPitch, player.rotationYaw, 0f, 1.6f, 1f);
            worldIn.spawnEntity(bomb);
        }
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public void registerControllers(AnimationData data) {}

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }
}
