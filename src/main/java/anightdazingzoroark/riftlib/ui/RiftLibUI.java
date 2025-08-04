package anightdazingzoroark.riftlib.ui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class RiftLibUI extends GuiScreen {
    private List<RiftLibUISection> uiSections = new ArrayList<>();

    //position
    public final int x;
    public final int y;
    public final int z;

    public RiftLibUI(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    //get all the sections that make up the ui
    public abstract List<RiftLibUISection> uiSections();

    //add the background texture
    public abstract ResourceLocation drawBackground();

    //background texture size is an array, 0 is width and 1 is height
    public abstract int[] backgroundTextureSize();

    //background uv is an array, 0 is x and 1 is y
    public abstract int[] backgroundUV();

    //background size is an array, 0 is width and 1 is height
    public abstract int[] backgroundSize();

    @Override
    public void initGui() {
        super.initGui();
        this.uiSections = this.uiSections();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (this.mc != null && this.mc.world != null) this.drawDefaultBackground();
        else return;

        //background
        this.drawGuiContainerBackgroundLayer();

        //draw all the sections in uiSections
        for (RiftLibUISection section : this.uiSections) section.drawSectionContents(mouseX, mouseY, partialTicks);
    }

    private void drawGuiContainerBackgroundLayer() {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(this.drawBackground());
        int k = (this.width - this.backgroundSize()[0]) / 2;
        int l = (this.height - this.backgroundSize()[1]) / 2;
        drawModalRectWithCustomSizedTexture(k, l,
                this.backgroundUV()[0],
                this.backgroundUV()[1],
                this.backgroundSize()[0],
                this.backgroundSize()[1],
                this.backgroundTextureSize()[0],
                this.backgroundTextureSize()[1]
        );
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int delta = Mouse.getEventDWheel();
        if (delta != 0) {
            int mouseX = Mouse.getEventX() * this.width / this.mc.displayWidth;
            int mouseY = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
            //handle scrolling with scrollbar on sections
            for (RiftLibUISection section : this.uiSections) section.handleScrollWithScrollbar(mouseX, mouseY, delta);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        //manually move scroll bar on sections
        for (RiftLibUISection section : this.uiSections) section.handleClickOnScrollSection(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        for (RiftLibUISection section : this.uiSections) section.handleReleaseClickOnScrollbar(mouseX, mouseY, state);
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
        for (RiftLibUISection section : this.uiSections) section.handleScrollWithClick(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
    }
}
