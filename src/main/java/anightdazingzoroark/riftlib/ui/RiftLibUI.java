package anightdazingzoroark.riftlib.ui;

import anightdazingzoroark.riftlib.RiftLib;
import anightdazingzoroark.riftlib.RiftLibJEI;
import anightdazingzoroark.riftlib.ui.uiElement.RiftLibButton;
import anightdazingzoroark.riftlib.ui.uiElement.RiftLibClickableSection;
import anightdazingzoroark.riftlib.ui.uiElement.RiftLibTextField;
import anightdazingzoroark.riftlib.ui.uiElement.RiftLibUIElement;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class RiftLibUI extends GuiScreen {
    private final ResourceLocation popupBackground = new ResourceLocation(RiftLib.ModID, "textures/ui/popup.png");
    private List<RiftLibUISection> uiSections = new ArrayList<>();
    private RiftLibPopupUI popup;

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
        //set contents of sections when opening screen
        if (this.uiSections.isEmpty()) this.uiSections = this.uiSections();
        //when screen resizes, make sure that the section and all changes to it are preserved
        //only thing that changes is what it believes to be the ui size
        else {
            for (RiftLibUISection section : this.uiSections) {
                section.resizeGUISizes(this.width, this.height);
            }
        }

        //if popup exists, make sure that the section and all changes to it are preserved
        //when resizing the screen
        if (this.popup != null) this.popup.resizeGUISizes(this.width, this.height);
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
            if (!this.hiddenUISections.contains(section.id)) {
                //when there's a popup, make sure that hover related effects cannot happen
                section.setCanDoHoverEffects(this.popup == null);

                section.drawSectionContents(mouseX, mouseY, partialTicks);
            }

            //if theres a popup, skip the hoverlay related stuff
            if (this.popup != null) continue;

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

        //create popup and a black gradient over the ui for when a popup has been opened
        if (this.popup != null) {
            this.drawVerticalBlackGradientOverlay(
                    (this.width - this.backgroundSize()[0]) / 2,
                    (this.height - this.backgroundSize()[1]) / 2,
                    this.backgroundSize()[0],
                    this.backgroundSize()[1],
                    128
            );
            this.drawPopupBackgroundLayer();

            //draw popup elements
            this.popup.getSection().drawSectionContents(mouseX, mouseY, partialTicks);
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

    private void drawPopupBackgroundLayer() {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(this.popupBackground);
        int k = (this.width - 176) / 2;
        int l = (this.height - 96) / 2;
        drawModalRectWithCustomSizedTexture(k, l,
                0,
                0,
                176,
                96,
                176,
                96
        );
    }

    public abstract void onButtonClicked(RiftLibButton button);

    public abstract void onClickableSectionClicked(RiftLibClickableSection clickableSection);

    protected void createPopup(List<RiftLibUIElement.Element> elements) {
        this.popup = new RiftLibPopupUI(elements, this.width, this.height, this.fontRenderer, this.mc);
    }

    protected void clearPopup() {
        this.popup = null;
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        //if theres a popup, don't do anything
        if (this.popup != null) return;
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

        //if theres a popup, don't do anything
        if (this.popup != null) return;

        for (RiftLibUISection section : this.uiSections) {
            //skip if this section is hidden
            if (this.hiddenUISections.contains(section.id)) continue;

            //skip to clicked position scroll bar on sections
            section.handleClickOnScrollSection(mouseX, mouseY, mouseButton);

            //open jei for items
            section.itemElementClicked(mouseX, mouseY, mouseButton);

            //all the additional logic here is for ensuring clicking smth out of bounds results in nothing
            int sectionTop = (this.height - section.height) / 2 + section.yPos;
            int sectionBottom = sectionTop + section.height;

            //button clicking
            for (RiftLibButton button : section.getActiveButtons()) {
                int buttonTop = button.y;
                int buttonBottom = button.y + button.height;
                boolean clickWithinVisiblePart = mouseY >= Math.max(buttonTop, sectionTop) && mouseY <= Math.min(buttonBottom, sectionBottom);
                if (clickWithinVisiblePart && button.mousePressed(this.mc, mouseX, mouseY)) {
                    this.playPressSound();
                    this.onButtonClicked(button);
                }
            }

            //clickable section clicking
            for (RiftLibClickableSection clickableSection : section.getClickableSections()) {
                int clickableSectionTop = clickableSection.minClickableArea()[1];
                int clickableSectionBottom = clickableSection.maxClickableArea()[1];
                boolean clickWithinVisiblePart = mouseY >= Math.max(clickableSectionTop, sectionTop) && mouseY <= Math.min(clickableSectionBottom, sectionBottom);
                if (clickWithinVisiblePart && clickableSection.isHovered(mouseX, mouseY)) {
                    this.playPressSound();
                    this.onClickableSectionClicked(clickableSection);
                }
            }

            //tab selector clicking
            for (RiftLibUISection.TabSelectorClickRegion tabSelectorClickRegion : section.getTabSelectorClickRegions()) {
                if (tabSelectorClickRegion.isHovered(mouseX, mouseY)) {
                    this.playPressSound();
                    section.getOpenedTabs().replace(tabSelectorClickRegion.tabID, tabSelectorClickRegion.tabContentsID);
                }
            }
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);

        //if theres a popup, don't do anything
        if (this.popup != null) return;

        for (RiftLibUISection section : this.uiSections) {
            //skip if this section is hidden
            if (this.hiddenUISections.contains(section.id)) continue;

            section.handleReleaseClickOnScrollbar(mouseX, mouseY, state);
        }
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);

        //if theres a popup, don't do anything
        if (this.popup != null) return;

        for (RiftLibUISection section : this.uiSections) {
            //skip if this section is hidden
            if (this.hiddenUISections.contains(section.id)) continue;

            section.handleScrollWithClick(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        //make sure that pressing escape when theres a popup removes the popup
        if (this.popup != null) {
            if (keyCode == 1) this.clearPopup();
        }
        //for exiting gui in general when pressing escape, as well as other functions involving keyboard
        else {
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
    }

    @Override
    public void updateScreen() {
        //if theres a popup, don't do anything
        if (this.popup != null) return;

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

    protected void playPressSound() {
        this.mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }

    private void drawVerticalBlackGradientOverlay(int x, int y, int width, int height, int alpha) {
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);

        // Top (more transparent)
        buffer.pos(x, y + height, 0).color(0f, 0f, 0f, alpha / 255f).endVertex();
        buffer.pos(x + width, y + height, 0).color(0f, 0f, 0f, alpha / 255f).endVertex();

        // Bottom (more opaque)
        buffer.pos(x + width, y, 0).color(0f, 0f, 0f, alpha / 255f).endVertex();
        buffer.pos(x, y, 0).color(0f, 0f, 0f, alpha / 255f).endVertex();

        tessellator.draw();

        GlStateManager.enableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
    }
}
