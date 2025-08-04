package anightdazingzoroark.riftlib;

import mezz.jei.api.IJeiRuntime;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IRecipesGui;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.recipe.IFocus;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

@JEIPlugin
public class RiftLibJEI implements IModPlugin {
    public static final String JEI_MOD_ID = "jei";
    private static IJeiRuntime jeiRuntime;

    @Override
    public void onRuntimeAvailable(@Nonnull IJeiRuntime jeiRuntime) {
        RiftLibJEI.jeiRuntime = jeiRuntime;
    }

    public static boolean showRecipesForItemStack(ItemStack itemStack, boolean isUses) {
        jeiRuntime.getRecipesGui().show(jeiRuntime.getRecipeRegistry().createFocus(isUses ? IFocus.Mode.INPUT : IFocus.Mode.OUTPUT, itemStack));
        return Minecraft.getMinecraft().currentScreen instanceof IRecipesGui;
    }
}
