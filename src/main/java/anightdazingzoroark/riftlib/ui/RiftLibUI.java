package anightdazingzoroark.riftlib.ui;

import anightdazingzoroark.riftlib.RiftLibJEI;
import anightdazingzoroark.riftlib.ui.uiElement.RiftLibButton;
import anightdazingzoroark.riftlib.ui.uiElement.RiftLibClickableSection;
import anightdazingzoroark.riftlib.ui.uiElement.RiftLibTextField;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class RiftLibUI extends GuiScreen {
    private List<RiftLibUISection> uiSections = new ArrayList<>();

    //for managing sections
    private final List<String> hiddenUISections = new ArrayList<>();

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
        //todo: make it so content can be restored when resizing screen
        this.uiSections = this.uiSections();
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
            //draw all the sections in uiSections as long as its id is not hidden
            if (!this.hiddenUISections.contains(section.id)) section.drawSectionContents(mouseX, mouseY, partialTicks);

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

    public abstract void onButtonClicked(RiftLibButton button);

    public abstract void onClickableSectionClicked(RiftLibClickableSection clickableSection);

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int delta = Mouse.getEventDWheel();
        if (delta != 0) {
            int mouseX = Mouse.getEventX() * this.width / this.mc.displayWidth;
            int mouseY = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
            for (RiftLibUISection section : this.uiSections) {
                //skip if this section is hidden
                if (this.hiddenUISections.contains(section.id)) continue;

                //handle scrolling with scrollbar on sections
                section.handleScrollWithScrollbar(mouseX, mouseY, delta);
            }
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        for (RiftLibUISection section : this.uiSections) {
            //skip if this section is hidden
            if (this.hiddenUISections.contains(section.id)) continue;

            //skip to clicked position scroll bar on sections
            section.handleClickOnScrollSection(mouseX, mouseY, mouseButton);

            //open jei for items
            section.itemElementClicked(mouseX, mouseY, mouseButton);

            //all the additional logic here is for ensuring clicking smth out of bounds results in nothing
            int sectionTop = (section.guiHeight - section.height) / 2 + section.yPos;
            int sectionBottom = sectionTop + section.height;

            //button clicking
            for (RiftLibButton button : section.getActiveButtons()) {
                int buttonTop = button.y;
                int buttonBottom = button.y + button.height;
                boolean clickWithinVisiblePart = mouseY >= Math.max(buttonTop, sectionTop) && mouseY <= Math.min(buttonBottom, sectionBottom);
                if (clickWithinVisiblePart && button.mousePressed(this.mc, mouseX, mouseY)) {
                    button.playPressSound(this.mc.getSoundHandler());
                    this.onButtonClicked(button);
                }
            }

            //clickable section clicking
            for (RiftLibClickableSection clickableSection : section.getClickableSections()) {
                int clickableSectionTop = clickableSection.minClickableArea()[1];
                int clickableSectionBottom = clickableSection.maxClickableArea()[1];
                boolean clickWithinVisiblePart = mouseY >= Math.max(clickableSectionTop, sectionTop) && mouseY <= Math.min(clickableSectionBottom, sectionBottom);
                if (clickWithinVisiblePart && clickableSection.isHovered(mouseX, mouseY)) {
                    clickableSection.playPressSound(this.mc.getSoundHandler());
                    this.onClickableSectionClicked(clickableSection);
                }
            }
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        for (RiftLibUISection section : this.uiSections) {
            //skip if this section is hidden
            if (this.hiddenUISections.contains(section.id)) continue;

            section.handleReleaseClickOnScrollbar(mouseX, mouseY, state);
        }
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
        for (RiftLibUISection section : this.uiSections) {
            //skip if this section is hidden
            if (this.hiddenUISections.contains(section.id)) continue;

            section.handleScrollWithClick(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        //for exiting
        super.keyTyped(typedChar, keyCode);

        //deal with input for text boxes in each section
        for (RiftLibUISection section : this.uiSections) {
            //skip if this section is hidden
            if (this.hiddenUISections.contains(section.id)) continue;

            for (RiftLibTextField textField : section.getTextFields()) {
                //get contents
                Map<String, String> contents = section.getTextFieldContents();

                //add to contents map if it exists
                if (contents.containsKey(textField.id)) {
                    textField.textboxKeyTyped(typedChar, keyCode);
                    contents.replace(textField.id, textField.getText());
                }
                //just put it inside otherwise
                else contents.put(textField.id, String.valueOf(typedChar));
            }
        }
    }

    @Override
    public void updateScreen() {
        //update text boxes in each section
        for (RiftLibUISection section : this.uiSections) {
            //skip if this section is hidden
            if (this.hiddenUISections.contains(section.id)) continue;

            for (RiftLibTextField textField : section.getTextFields()) {
                textField.updateCursorCounter();
            }
        }
    }

    protected RiftLibButton getButtonByID(String id) {
        for (RiftLibUISection section : this.uiSections) {
            for (RiftLibButton button : section.getActiveButtons()) {
                if (button.buttonId.equals(id)) return button;
            }
        }
        return null;
    }

    protected void setButtonUsabilityByID(String id, boolean value) {
        for (RiftLibUISection section : this.uiSections) {
            for (RiftLibButton button : section.getActiveButtons()) {
                if (button.buttonId.equals(id)) section.setButtonEnabled(id, value);
            }
        }
    }

    protected RiftLibClickableSection getClickableSectionByID(String id) {
        for (RiftLibUISection section : this.uiSections) {
            for (RiftLibClickableSection clickableSection : section.getClickableSections()) {
                if (clickableSection.getStringID().equals(id)) return clickableSection;
            }
        }
        return null;
    }

    protected void setSelectClickableSectionByID(String id, boolean value) {
        for (RiftLibUISection section : this.uiSections) {
            for (RiftLibClickableSection clickableSection : section.getClickableSections()) {
                if (clickableSection.getStringID().equals(id)) section.setClickableSectionSelected(id, value);
            }
        }
    }

    protected void setUISectionVisibility(String sectionID, boolean value) {
        if (value) this.hiddenUISections.remove(sectionID);
        else this.hiddenUISections.add(sectionID);
    }
}
