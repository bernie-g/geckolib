package anightdazingzoroark.riftlib.ui.uiElement;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;

public class RiftLibTextField extends GuiTextField {
    public final String id;

    public RiftLibTextField(String id, FontRenderer fontrendererObj, int x, int y, int par5Width, int par6Height) {
        super(0, fontrendererObj, x, y, par5Width, par6Height);
        this.id = id;
    }
}
