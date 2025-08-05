package anightdazingzoroark.riftlib.ui;

import anightdazingzoroark.riftlib.RiftLibJEI;
import anightdazingzoroark.riftlib.ui.uiElement.RiftLibButtonElement;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
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
        if (this.uiSections.isEmpty() && !this.uiSections().isEmpty()) this.uiSections = this.uiSections();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (this.mc != null && this.mc.world != null) this.drawDefaultBackground();
        else return;

        //background
        this.drawGuiContainerBackgroundLayer();

        //hovered item will be defined when iterating over the sections
        ItemStack hoveredItem = null;

        //iterate over all ui sections
        for (RiftLibUISection section : this.uiSections) {
            //draw all the sections in uiSections
            section.drawSectionContents(mouseX, mouseY, partialTicks);

            //assign hovered item as long as its originally null
            if (hoveredItem == null) hoveredItem = section.getHoveredItemStack(mouseX, mouseY);

            //create overlay text for hovered tool
            String toolOverlayString = section.getStringToHoverFromTool(mouseX, mouseY);
            if (!toolOverlayString.isEmpty()) this.drawHoveringText(toolOverlayString, mouseX, mouseY);
        }

        //show overlay info regarding hovered item
        if (hoveredItem != null) {
            List<String> tooltip = new ArrayList<>();

            tooltip.add(hoveredItem.getDisplayName());
            if (Loader.isModLoaded(RiftLibJEI.JEI_MOD_ID)) tooltip.add(I18n.format("ui.open_in_jei"));
            this.drawHoveringText(tooltip, mouseX, mouseY);
        }
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

    public abstract void onButtonClicked(RiftLibButtonElement button);

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

        for (RiftLibUISection section : this.uiSections) {
            //skip to clicked position scroll bar on sections
            section.handleClickOnScrollSection(mouseX, mouseY, mouseButton);

            //open jei for items
            section.itemElementClicked(mouseX, mouseY, mouseButton);

            //button clicking
            //all the additional logic here is for ensuring clicking out of bounds results in nothing
            int sectionTop = (section.guiHeight - section.height) / 2 + section.yPos;
            int sectionBottom = sectionTop + section.height;
            for (RiftLibButtonElement button : section.getActiveButtons()) {
                int buttonTop = button.y;
                int buttonBottom = button.y + button.height;
                boolean clickWithinVisiblePart = mouseY >= Math.max(buttonTop, sectionTop) && mouseY <= Math.min(buttonBottom, sectionBottom);
                if (clickWithinVisiblePart && button.mousePressed(this.mc, mouseX, mouseY)) {
                    button.playPressSound(this.mc.getSoundHandler());
                    this.onButtonClicked(button);
                }
            }
        }
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

    protected RiftLibButtonElement getButtonByID(String id) {
        for (RiftLibUISection section : this.uiSections) {
            for (RiftLibButtonElement button : section.getActiveButtons()) {
                if (button.buttonId.equals(id)) return button;
            }
        }
        return null;
    }

    protected void setButtonUsabilityByID(String id, boolean value) {
        for (RiftLibUISection section : this.uiSections) {
            for (RiftLibButtonElement button : section.getActiveButtons()) {
                if (button.buttonId.equals(id)) section.setButtonEnabled(id, value);
            }
        }
    }
}
