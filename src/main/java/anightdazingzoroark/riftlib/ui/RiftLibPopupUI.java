package anightdazingzoroark.riftlib.ui;

import anightdazingzoroark.riftlib.ui.uiElement.RiftLibUIElement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

import java.util.List;

//this is for popups that can show up above regular RiftLibUI instances
//their size is completely fixed sadly
public class RiftLibPopupUI {
    private final List<RiftLibUIElement.Element> elements;
    private final int[] contentSize =  {166, 86};
    private int guiWidth, guiHeight;

    //other important stuff
    private final FontRenderer fontRenderer;
    private final Minecraft minecraft;

    public RiftLibPopupUI(List<RiftLibUIElement.Element> elements, int guiWidth, int guiHeight, FontRenderer fontRenderer, Minecraft minecraft) {
        this.elements = elements;
        this.guiWidth = guiWidth;
        this.guiHeight = guiHeight;
        this.fontRenderer = fontRenderer;
        this.minecraft = minecraft;
    }

    public void resizeGUISizes(int guiWidth, int guiHeight) {
        this.guiWidth = guiWidth;
        this.guiHeight = guiHeight;
    }

    public RiftLibUISection getSection() {
        return new RiftLibUISection("popup", this.guiWidth, this.guiHeight, this.contentSize[0], this.contentSize[1], 0, 0, this.fontRenderer, this.minecraft) {
            @Override
            public List<RiftLibUIElement.Element> defineSectionContents() {
                return elements;
            }

            @Override
            protected boolean elementsCenteredVertically() {
                return true;
            }
        };
    }
}
