package anightdazingzoroark.riftlib.ui;

import anightdazingzoroark.riftlib.RiftLibJEI;
import anightdazingzoroark.riftlib.ui.uiElement.RiftLibButton;
import anightdazingzoroark.riftlib.ui.uiElement.RiftLibClickableSection;
import anightdazingzoroark.riftlib.ui.uiElement.RiftLibTextField;
import anightdazingzoroark.riftlib.ui.uiElement.RiftLibUIElement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.Loader;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.minecraft.client.gui.Gui.drawModalRectWithCustomSizedTexture;
import static net.minecraft.client.gui.Gui.drawRect;

public abstract class RiftLibUISection {
    //section size related stuff
    private final int width;
    private final int height;

    //section position related stuff
    private int xPos;
    private int yPos;

    //section management stuff
    public final String id;

    //scrolling related stuff
    private boolean scrollable;
    protected int contentHeight;
    protected int scrollOffset = 0;
    protected int maxScroll = 0;
    protected int scrollStep = 10;
    protected final int scrollbarWidth = 2;
    private boolean draggingScrollbar = false;
    private int dragOffsetY = 0;
    protected int scrollbarXOffset;

    //other stuff required for section to work properly
    private int guiWidth;
    private int guiHeight;
    protected final Minecraft minecraft;
    protected final FontRenderer fontRenderer;

    //for being able hover over items and see their jei recipes
    private final List<ItemClickRegion> itemClickRegions = new ArrayList<>();

    //for being able to hover over tools and see hover text
    private final List<ToolHoverRegion> toolHoverRegions = new ArrayList<>();

    //for dealing with active buttons
    private final List<RiftLibButton> activeButtons = new ArrayList<>();

    //for dealing with clickable sections
    private final List<RiftLibClickableSection> clickableSections = new ArrayList<>();
    private final List<String> selectedClickableSections = new ArrayList<>();

    //logic involved in disabling buttons
    private final List<String> disabledButtonIds = new ArrayList<>();

    //text box stuff
    private final List<RiftLibTextField> textFields = new ArrayList<>();
    private final Map<String, String> textFieldContents = new HashMap<>();

    //tab stuff
    private final Map<String, String> openedTabs = new HashMap<>();
    private final List<TabSelectorClickRegion> tabSelectorClickRegions = new ArrayList<>();

    //hover related stuff
    private boolean doHoverEffects = true;

    public RiftLibUISection(String id, int guiWidth, int guiHeight, int width, int height, int xPos, int yPos, FontRenderer fontRenderer, Minecraft minecraft) {
        this.id = id;
        this.guiWidth = guiWidth;
        this.guiHeight = guiHeight;
        this.width = width;
        this.height = height;
        this.xPos = xPos;
        this.yPos = yPos;
        this.fontRenderer = fontRenderer;
        this.minecraft = minecraft;
    }

    public void resizeGUISizes(int guiWidth, int guiHeight) {
        this.guiWidth = guiWidth;
        this.guiHeight = guiHeight;
    }

    public void repositionSection(int xPos, int yPos) {
        this.xPos = xPos;
        this.yPos = yPos;
    }

    public int[] sectionPos() {
        return new int[]{this.xPos, this.yPos};
    }

    public int[] sectionSize() {
        return new int[]{this.width, this.height};
    }

    public void setCanDoHoverEffects(boolean value) {
        this.doHoverEffects = value;
    }

    //draw elements to be drawn
    public abstract List<RiftLibUIElement.Element> defineSectionContents();

    //draw contents as defined in defineSectionContents()
    public void drawSectionContents(int mouseX, int mouseY, float partialTicks) {
        //preemptively clear lists
        this.itemClickRegions.clear();
        this.toolHoverRegions.clear();
        this.activeButtons.clear();
        this.clickableSections.clear();
        this.textFields.clear();
        this.tabSelectorClickRegions.clear();

        int sectionX = (this.guiWidth - this.getWidthMinusScrollbar()) / 2 + this.xPos;
        int sectionY = (this.guiHeight - this.height) / 2 + this.yPos;

        //scissor setup
        ScaledResolution res = new ScaledResolution(this.minecraft);
        int scaleFactor = res.getScaleFactor();
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(sectionX * scaleFactor, (this.minecraft.displayHeight - (sectionY + this.height) * scaleFactor), this.getWidthMinusScrollbar() * scaleFactor, this.height * scaleFactor);
        /*
        int drawY = sectionY - this.scrollOffset;
        int totalHeight = 0;
        this.contentHeight = 0;

        //elements are drawn here
        for (int i = 0; i < this.defineSectionContents().size(); i++) {
            RiftLibUIElement.Element element = this.defineSectionContents().get(i);

            //draw the elements and add up their height
            totalHeight += this.drawElement(element, true, this.getWidthMinusScrollbar(), sectionX, drawY + totalHeight, mouseX, mouseY, partialTicks);

            //extra bottom height for certain elements
            if (i < this.defineSectionContents().size() - 1) totalHeight += element.getBottomSpace();
        }

        //scroll management is dealt with here
        this.contentHeight = totalHeight;
        this.maxScroll = Math.max(0, this.contentHeight - this.height);
         */

        //measure total height
        int totalHeight = 0;
        for (int i = 0; i < this.defineSectionContents().size(); i++) {
            RiftLibUIElement.Element element = this.defineSectionContents().get(i);
            int elementHeight = this.drawElement(element, false, this.getWidthMinusScrollbar(), sectionX, 0, mouseX, mouseY, partialTicks);
            totalHeight += elementHeight;

            if (i < this.defineSectionContents().size() - 1) {
                totalHeight += element.getBottomSpace();
            }
        }

        //store height and compute scroll values
        this.contentHeight = totalHeight;
        this.maxScroll = Math.max(0, this.contentHeight - this.height);

        //adjust drawY based on scroll and vertical centering
        int drawY = sectionY - this.scrollOffset;
        if (this.elementsCenteredVertically() && this.contentHeight < this.height) {
            int verticalOffset = (this.height - this.contentHeight) / 2;
            drawY += verticalOffset;
        }

        //draw the elements
        int accumulatedHeight = 0;
        for (int i = 0; i < this.defineSectionContents().size(); i++) {
            RiftLibUIElement.Element element = this.defineSectionContents().get(i);

            accumulatedHeight += this.drawElement(
                    element,
                    true,
                    this.getWidthMinusScrollbar(),
                    sectionX,
                    drawY + accumulatedHeight,
                    mouseX,
                    mouseY,
                    partialTicks
            );

            if (i < this.defineSectionContents().size() - 1) {
                accumulatedHeight += element.getBottomSpace();
            }
        }

        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        //draw scrollbar
        if (this.contentHeight > this.height) {
            float ratio = (float) this.scrollOffset / (float) this.maxScroll;
            int thumbHeight = Math.max(20, (int)((float) this.height * this.height / (float) this.contentHeight));
            int thumbY = sectionY + (int)((this.height - thumbHeight) * ratio);
            int scrollX = sectionX + this.getWidthMinusScrollbar() + this.scrollbarXOffset;

            drawRect(scrollX, sectionY, scrollX + this.scrollbarWidth, sectionY + this.height, 0xFF333333);
            drawRect(scrollX, thumbY, scrollX + this.scrollbarWidth, thumbY + thumbHeight, 0xFFAAAAAA);
        }
    }

    //return value is the total height created by these elements
    private int drawElement(RiftLibUIElement.Element element, boolean draw, int sectionWidth, int x, int y, int mouseX, int mouseY, float partialTicks) {
        //for items that have select, hover, or click functionalities
        //this is to ensure that items out of bounds dont get interacted with ever
        int sectionTop = (this.guiHeight - this.height) / 2 + this.yPos;
        int sectionBottom = sectionTop + this.height;

        //regular elements
        if (element instanceof RiftLibUIElement.TextElement) {
            RiftLibUIElement.TextElement textElement = (RiftLibUIElement.TextElement) element;
            float scale = textElement.getScale();
            int lines = 1;

            if (draw) {
                if (scale != 1f) {
                    GlStateManager.pushMatrix();
                    GlStateManager.scale(scale, scale, scale);
                }
                if (textElement.getSingleLine()) {
                    int stringWidth = (int) (this.fontRenderer.getStringWidth(textElement.getText()) * scale);
                    int totalTextX = textElement.xOffsetFromAlignment(sectionWidth, stringWidth, x);
                    this.fontRenderer.drawString(
                            textElement.getText(),
                            (int) (totalTextX / scale),
                            (int) (y / scale),
                            textElement.getTextColor()
                    );
                }
                else {
                    List<String> stringList = this.fontRenderer.listFormattedStringToWidth(textElement.getText(), (int) (sectionWidth * scale));
                    this.drawImprovedSplitString(
                            textElement,
                            x,
                            y,
                            sectionWidth,
                            sectionWidth,
                            scale,
                            textElement.getTextColor()
                    );
                    lines = stringList.size();
                }
                if (scale != 1f) GlStateManager.popMatrix();
            }
            else {
                if (!textElement.getSingleLine()) {
                    List<String> stringList = this.fontRenderer.listFormattedStringToWidth(textElement.getText(), (int) (sectionWidth * scale));
                    lines = stringList.size();
                }
            }

            return lines * this.fontRenderer.FONT_HEIGHT;
        }
        else if (element instanceof RiftLibUIElement.ImageElement) {
            RiftLibUIElement.ImageElement imageElement = (RiftLibUIElement.ImageElement) element;
            float scale = imageElement.getScale();

            int scaledImageWidth = (int) (imageElement.getImageUVSize()[0] * scale);
            int scaledImageHeight = (int) (imageElement.getImageUVSize()[1] * scale);

            int totalImgX = imageElement.xOffsetFromAlignment(sectionWidth, scaledImageWidth, x);

            if (draw) {
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                GlStateManager.pushMatrix();
                if (scale != 1f) GlStateManager.scale(scale, scale, scale);
                this.minecraft.getTextureManager().bindTexture(imageElement.getImage());
                GlStateManager.translate(
                        totalImgX / scale,
                        y / scale,
                        0
                );
                drawModalRectWithCustomSizedTexture(
                        0,
                        0,
                        imageElement.getImageUV()[0],
                        imageElement.getImageUV()[1],
                        imageElement.getImageUVSize()[0],
                        imageElement.getImageUVSize()[1],
                        imageElement.getTextureSize()[0],
                        imageElement.getTextureSize()[1]
                );
                GlStateManager.popMatrix();
            }

            return scaledImageHeight;
        }
        else if (element instanceof RiftLibUIElement.ItemElement) {
            RiftLibUIElement.ItemElement itemElement = (RiftLibUIElement.ItemElement) element;
            float scale = itemElement.getScale();

            int scaledItemSize = (int) (16 * scale);

            if (draw) {
                ItemStack itemStack = itemElement.getItemStack();
                int totalItemX = itemElement.xOffsetFromAlignment(sectionWidth, scaledItemSize, x);

                int scaledItemX = (int) (totalItemX / scale);
                int scaledItemY = (int) (y / scale);

                if (scale != 1f) {
                    GlStateManager.pushMatrix();
                    GlStateManager.scale(scale, scale, scale);
                }
                this.renderItem(
                        itemStack,
                        scaledItemX,
                        scaledItemY
                );
                if (scale != 1f) GlStateManager.popMatrix();

                //for being able hover over items and see their jei recipes
                this.itemClickRegions.add(new ItemClickRegion(itemStack, totalItemX, y, scaledItemSize, sectionTop, sectionBottom));
            }

            return scaledItemSize;
        }
        else if (element instanceof RiftLibUIElement.ToolElement) {
            RiftLibUIElement.ToolElement toolElement = (RiftLibUIElement.ToolElement) element;
            float scale = toolElement.getScale();

            int scaledItemSize = (int) (16 * scale);

            if (draw) {
                int totalItemX = toolElement.xOffsetFromAlignment(sectionWidth, scaledItemSize, x);

                int scaledItemX = (int) (totalItemX / scale);
                int scaledItemY = (int) (y / scale);

                //now visualize the mining levels using wooden tools and a number
                //why wooden tools? well theres mods that let u modify mining levels of tools and am not wanna deal with
                //that shit, so representing them all with just 1 tool type would make it easier
                //anyways, tool first
                Item toolItem = Item.getByNameOrId("minecraft:wooden_"+toolElement.getToolType());
                if (toolItem != null) {
                    ItemStack itemStack = new ItemStack(toolItem);
                    if (scale != 1f) {
                        GlStateManager.pushMatrix();
                        GlStateManager.scale(scale, scale, scale);
                    }
                    this.renderItem(
                            itemStack,
                            scaledItemX,
                            scaledItemY
                    );
                    if (scale != 1f) GlStateManager.popMatrix();
                }

                //render mining level
                String level = String.valueOf(toolElement.getMiningLevel());
                int totalLevelX = totalItemX + (int) (9 * scale);
                int totalLevelY = y + (int) (12 * scale);
                if (scale != 1f) {
                    GlStateManager.pushMatrix();
                    GlStateManager.scale(scale, scale, scale);
                }
                this.fontRenderer.drawString(level, totalLevelX / scale, totalLevelY / scale, 0, false);
                if (scale != 1f) GlStateManager.popMatrix();

                //it tool has hover text, add it
                if (toolElement.getOverlayText() != null && !toolElement.getOverlayText().isEmpty()) {
                    this.toolHoverRegions.add(new ToolHoverRegion(
                            toolElement.getOverlayText(),
                            toolElement.getToolType(),
                            toolElement.getMiningLevel(),
                            totalItemX,
                            y,
                            scaledItemSize,
                            sectionTop,
                            sectionBottom
                    ));
                }
            }
            return scaledItemSize + (int) (4 * scale);
        }
        else if (element instanceof RiftLibUIElement.ButtonElement) {
            RiftLibUIElement.ButtonElement buttonElement = (RiftLibUIElement.ButtonElement) element;

            int buttonW = buttonElement.getSize()[0];
            int buttonH = buttonElement.getSize()[1];

            if (draw) {
                int buttonX = buttonElement.xOffsetFromAlignment(sectionWidth, buttonW, x);

                RiftLibButton button = new RiftLibButton(buttonElement.getID(), buttonX, y, buttonW, buttonH, buttonElement.getName());
                button.doHoverEffects = this.doHoverEffects;
                button.scrollTop = sectionTop;
                button.scrollBottom = sectionBottom;
                if (this.disabledButtonIds.contains(buttonElement.getID())) button.enabled = false;

                //only render and register button if its within visible bounds
                if ((y + buttonH) > sectionTop && y < sectionBottom) {
                    button.drawButton(this.minecraft, mouseX, mouseY, partialTicks);
                }
                this.activeButtons.add(button);
            }

            return buttonH;
        }
        else if (element instanceof RiftLibUIElement.ClickableSectionElement) {
            RiftLibUIElement.ClickableSectionElement clickableSectionElement = (RiftLibUIElement.ClickableSectionElement) element;

            int sectionW = clickableSectionElement.getSize()[0];
            int sectionH = clickableSectionElement.getSize()[1];

            if (draw) {
                int sectionX = clickableSectionElement.xOffsetFromAlignment(sectionWidth, sectionW, x);

                //make section
                RiftLibClickableSection clickableSection = new RiftLibClickableSection(
                        sectionW,
                        sectionH,
                        sectionW,
                        sectionH,
                        sectionX,
                        y,
                        this.fontRenderer,
                        this.minecraft
                );
                clickableSection.setID(clickableSectionElement.getID());
                clickableSection.scrollTop = sectionTop;
                clickableSection.scrollBottom = sectionBottom;
                clickableSection.doHoverEffects = this.doHoverEffects;

                //text rendering
                if (!clickableSectionElement.getTextContent().isEmpty()) {
                    clickableSection.addString(
                            clickableSectionElement.getTextContent(),
                            false,
                            clickableSectionElement.getTextColor(),
                            clickableSectionElement.getTextOffsets()[0],
                            clickableSectionElement.getTextOffsets()[1],
                            clickableSectionElement.getTextScale()
                    );
                    clickableSection.setStringHoveredColor(clickableSectionElement.getTextHoveredColor());
                    clickableSection.setStringSelectedColor(clickableSectionElement.getTextSelectedColor());
                }

                //image rendering
                if (clickableSectionElement.getImage() != null) {
                    clickableSection.addImage(
                            clickableSectionElement.getImage(),
                            clickableSectionElement.getUVSize()[0],
                            clickableSectionElement.getUVSize()[1],
                            clickableSectionElement.getImageSize()[0],
                            clickableSectionElement.getImageSize()[1],
                            clickableSectionElement.getImageUV()[0],
                            clickableSectionElement.getImageUV()[1],
                            clickableSectionElement.getImageHoveredUV()[0],
                            clickableSectionElement.getImageHoveredUV()[1]
                    );
                    clickableSection.setScale(clickableSectionElement.getImageScale());
                    if (clickableSectionElement.getImageSelectedUV() != null) clickableSection.setSelectedUV(clickableSectionElement.getImageSelectedUV()[0], clickableSectionElement.getImageSelectedUV()[1]);
                }

                if (this.selectedClickableSections.contains(clickableSectionElement.getID())) clickableSection.setSelected(true);

                //only render and register section if its within visible bounds
                if ((y + sectionH) > sectionTop && y < sectionBottom) {
                    clickableSection.drawSection(mouseX, mouseY);
                }
                this.clickableSections.add(clickableSection);
            }

            return sectionH;
        }
        else if (element instanceof RiftLibUIElement.TextBoxElement) {
            RiftLibUIElement.TextBoxElement textBoxElement = (RiftLibUIElement.TextBoxElement) element;
            float scale = textBoxElement.getScale();

            int textBoxWidth = (int) (textBoxElement.getWidth() * scale);
            int textBoxHeight = (int) (20 * scale);

            if (draw) {
                int textBoxX = textBoxElement.xOffsetFromAlignment(sectionWidth, textBoxWidth, x);

                int scaledTextBoxX = (int) (textBoxX / scale);
                int scaledTextBoxY = (int) (y / scale);

                //create text field
                RiftLibTextField textField = new RiftLibTextField(
                        textBoxElement.getID(),
                        this.fontRenderer,
                        scaledTextBoxX,
                        scaledTextBoxY,
                        textBoxWidth,
                        textBoxHeight
                );
                textField.setFocused(true);
                textField.setText(textBoxElement.getDefaultText());
                //if theres already text in the textFieldContents map, set the text of textField to its stored text
                if (!this.textFieldContents.isEmpty() && this.textFieldContents.containsKey(textBoxElement.getID())) {
                    textField.setText(this.textFieldContents.get(textBoxElement.getID()));
                }
                //otherwise, put it inside
                else this.textFieldContents.put(textBoxElement.getID(), textField.getText());

                textField.drawTextBox();
                this.textFields.add(textField);
            }

            return textBoxHeight;
        }
        else if (element instanceof RiftLibUIElement.ProgressBarElement) {
            RiftLibUIElement.ProgressBarElement progressBarElement = (RiftLibUIElement.ProgressBarElement) element;
            float scale = progressBarElement.getScale();

            int progressBarWidth = (int) Math.ceil(progressBarElement.getWidth() * scale);
            int progressBarHeight = (int) Math.ceil(3 * scale);

            if (draw) {
                int progressBarX = progressBarElement.xOffsetFromAlignment(sectionWidth, progressBarWidth, x);

                int progressBarContentWidth = progressBarWidth - (int) Math.ceil(2 * scale);
                int progressBarContentHeight = progressBarHeight - (int) Math.ceil(2 * scale);
                int progressBarContentX = progressBarElement.contentXOffsetFromAlignment(sectionWidth, progressBarContentWidth, x, scale);
                int progressBarContentY = (int) Math.ceil(y + scale);

                //draw frame
                Gui.drawRect(progressBarX, y, progressBarX + progressBarWidth, y + progressBarHeight, 0xFF000000);

                //draw background of progress bar
                this.drawRectOutline(progressBarContentX, progressBarContentY, progressBarContentWidth, progressBarContentHeight, (0xFF000000 | progressBarElement.getBackgroundColor()));

                //draw progress of progress bar
                if (progressBarElement.getPercentage() > 0)
                    this.drawRectOutline(progressBarContentX, progressBarContentY, (int) (progressBarContentWidth * progressBarElement.getPercentage()), progressBarContentHeight, (0xFF000000 | progressBarElement.getOverlayColor()));
            }

            return progressBarHeight;
        }
        else if (element instanceof RiftLibUIElement.RenderedEntityElement) {
            RiftLibUIElement.RenderedEntityElement renderedEntityElement = (RiftLibUIElement.RenderedEntityElement) element;
            float scale = renderedEntityElement.getScale();

            int renderWidth = (int) Math.ceil(renderedEntityElement.getEntity().width) + renderedEntityElement.getAdditionalSize()[0];
            int renderHeight = (int) Math.ceil(renderedEntityElement.getEntity().height) + renderedEntityElement.getAdditionalSize()[1];
            int scaledRenderWidth = (int) (scale * (renderWidth - renderedEntityElement.getAdditionalSize()[0])) + renderedEntityElement.getAdditionalSize()[0];
            int scaledRenderHeight = (int) (scale * (renderHeight - renderedEntityElement.getAdditionalSize()[1])) + renderedEntityElement.getAdditionalSize()[1];

            if (draw) {
                int renderedEntityX = renderedEntityElement.xOffsetFromAlignment(sectionWidth, scaledRenderWidth, scale, x);

                //entity rendering
                if ((y + scaledRenderHeight) > sectionTop && y < sectionBottom) {
                    GlStateManager.color(1f, 1f, 1f, 1f);
                    GlStateManager.pushMatrix();
                    GlStateManager.pushMatrix();
                    GlStateManager.enableDepth();
                    GlStateManager.translate(renderedEntityX, y + scaledRenderHeight, 210f);
                    GlStateManager.rotate(180, 1f, 0f, 0f);
                    GlStateManager.rotate(renderedEntityElement.getRotationAngle(), 0f, 1f, 0f);
                    GlStateManager.scale(scale, scale, scale);
                    this.minecraft.getRenderManager().renderEntity(renderedEntityElement.getEntity(), 0.0D, 0.0D, 0.0D, 0.0F, 0F, false);
                    GlStateManager.disableDepth();
                    GlStateManager.popMatrix();
                    GlStateManager.popMatrix();
                }
            }

            return scaledRenderHeight;
        }
        //container elements
        else if (element instanceof RiftLibUIElement.TableContainerElement) {
            RiftLibUIElement.TableContainerElement tableContainerElement = (RiftLibUIElement.TableContainerElement) element;

            int rowCount = (int) Math.ceil(tableContainerElement.getElements().size() / (double) tableContainerElement.getRowCount());

            int cellWidth = tableContainerElement.getCellSize()[0];
            int cellHeight = tableContainerElement.getCellSize()[1];

            int totalTableWidth = cellWidth * tableContainerElement.getRowCount();
            int totalTableHeight = cellHeight * rowCount;

            if (draw) {
                int tableX = tableContainerElement.xOffsetFromAlignment(sectionWidth, totalTableWidth, x);

                for (int i = 0; i < tableContainerElement.getElements().size(); i++) {
                    RiftLibUIElement.Element elementForCell = tableContainerElement.getElements().get(i);

                    int column = i % tableContainerElement.getRowCount();
                    int row = i / tableContainerElement.getRowCount();

                    int xCellPos = tableX + column * cellWidth;
                    int yCellPos = y + row * cellHeight;

                    //all elements in each cell must be centered
                    elementForCell.setAlignment(RiftLibUIElement.ALIGN_CENTER);
                    this.drawElement(elementForCell, true, cellWidth, xCellPos, yCellPos, mouseX, mouseY, partialTicks);
                }
            }

            return totalTableHeight;
        }
        else if (element instanceof RiftLibUIElement.TabElement) {
            RiftLibUIElement.TabElement tabElement = (RiftLibUIElement.TabElement) element;

            //draw tab selectors
            int tabSelectorX = x;
            int tabSelectorY = y;
            int tabSelectorHeight = 18;
            int tabSelectorPadding = 6;
            for (RiftLibUIElement.TabContents tabContents : tabElement.getTabContents()) {
                String tabName = tabContents.tabName;
                int tabWidth = this.fontRenderer.getStringWidth(tabName) + tabSelectorPadding * 2;

                //save region for hover and click
                TabSelectorClickRegion region = new TabSelectorClickRegion(tabSelectorX, tabSelectorY, tabWidth, tabSelectorHeight, tabElement.getID(), tabContents.tabContentID);
                if (!this.tabSelectorClickRegions.contains(region)) this.tabSelectorClickRegions.add(region);
                if (!this.openedTabs.containsKey(tabElement.getID())) this.openedTabs.put(tabElement.getID(), "");

                //detect hover
                boolean isHovered = region.isHovered(mouseX, mouseY);
                boolean isActive = region.isActive();

                //text color logic
                int textColor = tabElement.getTabSelectorTextColor();
                if (isActive) textColor = tabElement.getTabSelectorSelectedColor();
                else if (isHovered) textColor = tabElement.getTabSelectorHoverColor();

                //draw outline
                if (draw) this.drawRectOutline(tabSelectorX, tabSelectorY, tabWidth, tabSelectorHeight, 0xFF000000);

                //draw text
                if (draw) this.fontRenderer.drawStringWithShadow(tabName, tabSelectorX + tabSelectorPadding, tabSelectorY + 5, textColor);

                tabSelectorX += tabWidth + 4;
            }

            //draw tab contents
            int contentInnerHeight = 0;
            int contentPadding = 4;
            int contentBoxY = tabSelectorY + tabSelectorHeight + contentPadding;
            String idOfActiveTab = this.openedTabs.get(tabElement.getID());
            if (idOfActiveTab.isEmpty()) {
                this.openedTabs.replace(tabElement.getID(), tabElement.getTabContents().get(0).tabContentID);
                idOfActiveTab = tabElement.getTabContents().get(0).tabContentID;
            }
            List<RiftLibUIElement.Element> tabContentElements = tabElement.getTabContentsByID(idOfActiveTab).getTabContents();
            if (tabContentElements != null && !tabContentElements.isEmpty()) {
                int contentInnerX = x + contentPadding;
                int contentInnerY = contentBoxY + contentPadding;

                for (int i = 0; i < tabContentElements.size(); i++) {
                    RiftLibUIElement.Element elementInTab = tabContentElements.get(i);

                    //draw all the elements in the tab
                    int usedHeight = this.drawElement(elementInTab, draw, this.getTabContentWidth(tabElement), contentInnerX, contentInnerY, mouseX, mouseY, partialTicks);
                    contentInnerY += usedHeight;
                    contentInnerHeight += usedHeight;

                    //extra bottom height for certain elements
                    if (i < this.defineSectionContents().size() - 1) {
                        int bottomSpace = element.getBottomSpace();
                        contentInnerY += bottomSpace;
                        contentInnerHeight += bottomSpace;
                    }
                }

                //draw content outline box after measuring
                if (draw) this.drawRectOutline(x, contentBoxY, this.getTabContentWidth(tabElement) + contentPadding * 2, contentInnerHeight + contentPadding * 2, 0xFF000000);
            }

            return tabSelectorHeight + 4 + contentInnerHeight + contentPadding * 2;
        }
        return 0;
    }

    private int getTabContentWidth(RiftLibUIElement.TabElement tabElement) {
        if (tabElement.getWidth() > 0) return tabElement.getWidth();
        return this.width;
    }

    private int getWidthMinusScrollbar() {
        return this.width - this.scrollbarWidth;
    }

    private void drawImprovedSplitString(RiftLibUIElement.TextElement textElement, int x, int y, int wrapWidth, int sectionWidth, float scale, int color) {
        String string = textElement.getText();
        List<String> stringList = this.fontRenderer.listFormattedStringToWidth(string, wrapWidth);
        for (int i = 0; i < stringList.size(); i++) {
            String stringToRender = stringList.get(i);
            int stringWidth = (int) (Math.min(sectionWidth, this.fontRenderer.getStringWidth(stringToRender)) * scale);
            int totalTextX = textElement.xOffsetFromAlignment(sectionWidth, stringWidth, x);
            this.fontRenderer.drawString(
                    stringToRender,
                    (int) (totalTextX / scale),
                    (int)((y + this.fontRenderer.FONT_HEIGHT * i) / scale),
                    color
            );
        }
    }

    private void drawRectOutline(int x, int y, int w, int h, int color) {
        drawRect(x, y, x + w, y + 1, color);             // top
        drawRect(x, y + h - 1, x + w, y + h, color);     // bottom
        drawRect(x, y, x + 1, y + h, color);             // left
        drawRect(x + w - 1, y, x + w, y + h, color);     // right
    }

    private void renderItem(ItemStack stack, int x, int y) {
        RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
        renderItem.renderItemAndEffectIntoGUI(stack, x, y);
        renderItem.renderItemOverlayIntoGUI(this.fontRenderer, stack, x, y, null);
    }

    protected boolean elementsCenteredVertically() {
        return false;
    }

    //scroll management starts here
    //this changes scroll offset based on scrolling w the scroll button
    public void handleScrollWithScrollbar(int mouseX, int mouseY, int delta) {
        int x = (this.guiWidth - this.width) / 2 + this.xPos;
        int y = (this.guiHeight - this.height) / 2 + this.yPos;

        //check if mouse is within scrollable section
        if (mouseX >= x && mouseX <= x + this.width && mouseY >= y && mouseY <= y + this.height) {
            this.maxScroll = Math.max(0, this.contentHeight - this.height);
            this.scrollOffset = MathHelper.clamp(this.scrollOffset - Integer.signum(delta) * this.scrollStep, 0, this.maxScroll);
        }
    }

    //this changes scroll offset based on where the user clicked on scroll section
    public void handleClickOnScrollSection(int mouseX, int mouseY, int button) {
        if (button == 0 && this.contentHeight > this.height) {
            int sectionX = (this.guiWidth - this.width) / 2 + this.xPos;
            int sectionY = (this.guiHeight - this.height) / 2 + this.yPos;
            int scrollX = sectionX + this.getWidthMinusScrollbar() + this.scrollbarXOffset;

            float ratio = (float) this.scrollOffset / this.maxScroll;
            int thumbHeight = Math.max(20, (int)((float) this.height * this.height / this.contentHeight));
            int thumbY = sectionY + (int)((this.height - thumbHeight) * ratio);

            if (mouseX >= scrollX && mouseX <= scrollX + this.scrollbarWidth &&
                    mouseY >= sectionY && mouseY <= sectionY + height) {
                if (mouseY >= thumbY && mouseY <= thumbY + thumbHeight) {
                    //start dragging
                    this.draggingScrollbar = true;
                    this.dragOffsetY = mouseY - thumbY;
                }
                else {
                    int clickedY = mouseY - sectionY - thumbHeight / 2;
                    float scrollRatio = (float) clickedY / (float)(this.height - thumbHeight);
                    this.scrollOffset = MathHelper.clamp((int)(scrollRatio * this.maxScroll), 0, this.maxScroll);
                }
            }
        }
    }

    public void handleReleaseClickOnScrollbar(int mouseX, int mouseY, int button) {
        if (button == 0) this.draggingScrollbar = false;
    }

    //this changes scroll offset based on clicking and dragging the scroll guide
    public void handleScrollWithClick(int mouseX, int mouseY, int button, long timeSinceLastClick) {
        if (!this.draggingScrollbar || this.contentHeight <= this.height || button != 0) return;

        int sectionY = (this.guiHeight - this.height) / 2 + this.yPos;
        int thumbHeight = Math.max(20, (int)((float) this.height * this.height / this.contentHeight));

        int dragY = mouseY - sectionY - this.dragOffsetY;
        float scrollRatio = (float) dragY / (float)(this.height - thumbHeight);
        this.scrollOffset = MathHelper.clamp((int)(scrollRatio * this.maxScroll), 0, this.maxScroll);
    }

    public void resetScrollProgress() {
        this.scrollOffset = 0;
    }

    public int getScrollOffset() {
        return this.scrollOffset;
    }

    public void setScrollOffset(int value) {
        this.scrollOffset = value;
    }
    //scroll management stops here

    //item element related stuff starts here
    private class ItemClickRegion {
        private final ItemStack stack;
        private final int x, y, size;
        private final int sectionTop, sectionBottom;

        private ItemClickRegion(ItemStack stack, int x, int y, int size, int sectionTop, int sectionBottom) {
            this.stack = stack;
            this.x = x;
            this.y = y;
            this.size = size;
            this.sectionTop = sectionTop;
            this.sectionBottom = sectionBottom;
        }

        private boolean isHovered(int mouseX, int mouseY) {
            if (!doHoverEffects) return false;
            return mouseX >= this.x && mouseX < this.x + this.size && mouseY >= this.y && mouseY < this.y + this.size
                    && mouseY > this.sectionTop && mouseY < this.sectionBottom;
        }
    }

    public void itemElementClicked(int mouseX, int mouseY, int button) {
        //for item clicking
        for (ItemClickRegion region : this.itemClickRegions) {
            if (region.isHovered(mouseX, mouseY)) {
                if (Loader.isModLoaded(RiftLibJEI.JEI_MOD_ID)) {
                    RiftLibJEI.showRecipesForItemStack(region.stack, false);
                }
                break;
            }
        }
    }

    public ItemStack getHoveredItemStack(int mouseX, int mouseY) {
        for (ItemClickRegion region : this.itemClickRegions) {
            if (region.isHovered(mouseX, mouseY)) {
                return region.stack;
            }
        }
        return null;
    }
    //item element related stuff ends here

    //tool element related stuff starts here
    private class ToolHoverRegion {
        private final String stringOverlay;
        private final String toolType;
        private final int miningLevel;
        private final int x, y, size;
        private final int sectionTop, sectionBottom;

        private ToolHoverRegion(String stringOverlay, String toolType, int miningLevel, int x, int y, int size, int sectionTop, int sectionBottom) {
            this.stringOverlay = stringOverlay;
            this.toolType = toolType;
            this.miningLevel = miningLevel;

            this.x = x;
            this.y = y;
            this.size = size;
            this.sectionTop = sectionTop;
            this.sectionBottom = sectionBottom;
        }

        public String renderStringOverlay() {
            return I18n.format(this.stringOverlay, this.toolType, this.miningLevel);
        }

        private boolean isHovered(int mouseX, int mouseY) {
            if (!doHoverEffects) return false;
            return mouseX >= this.x && mouseX < this.x + this.size && mouseY >= this.y && mouseY < this.y + this.size
                    && mouseY > this.sectionTop && mouseY < this.sectionBottom;
        }
    }

    public String getStringToHoverFromTool(int mouseX, int mouseY) {
        for (ToolHoverRegion region : this.toolHoverRegions) {
            if (region.isHovered(mouseX, mouseY)) {
                return region.renderStringOverlay();
            }
        }
        return "";
    }
    //tool element related stuff ends here

    //button related stuff starts here
    public List<RiftLibButton> getActiveButtons() {
        return this.activeButtons;
    }

    public void setButtonEnabled(String id, boolean value) {
        if (value) this.disabledButtonIds.remove(id);
        else this.disabledButtonIds.add(id);
    }
    //button related stuff ends here

    //clickable section stuff starts here
    public List<RiftLibClickableSection> getClickableSections() {
        return this.clickableSections;
    }

    public void setClickableSectionSelected(String id, boolean value) {
        if (value) this.selectedClickableSections.add(id);
        else this.selectedClickableSections.remove(id);
    }
    //clickable section stuff ends here

    //text box stuff starts here
    public List<RiftLibTextField> getTextFields() {
        return this.textFields;
    }

    public Map<String, String> getTextFieldContents() {
        return this.textFieldContents;
    }

    public String getTextBoxContentsByID(String id) {
        for (RiftLibTextField textField : this.textFields) {
            if (textField.id.equals(id)) return textField.getText();
        }
        return "";
    }

    public void setTextBoxContentsByID(String id, String contents) {
        for (RiftLibTextField textField : this.textFields) {
            if (textField.id.equals(id)) {
                textField.setText(contents);
                if (this.textFieldContents.containsKey(id)) this.textFieldContents.replace(id, contents);
                else this.textFieldContents.put(id, contents);
            }
        }
    }
    //text box stuff ends here

    //tab related stuff starts here
    public class TabSelectorClickRegion {
        private final int x, y, w, h;
        public final String tabID, tabContentsID;

        public TabSelectorClickRegion(int x, int y, int w, int h, String tabID, String tabContentsID) {
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
            this.tabID = tabID;
            this.tabContentsID = tabContentsID;
        }

        public boolean isHovered(int mouseX, int mouseY) {
            if (!doHoverEffects) return false;
            return mouseX >= this.x && mouseX < this.x + this.w && mouseY >= y && mouseY < this.y + this.h;
        }

        public boolean isActive() {
            return openedTabs.containsKey(this.tabID) && openedTabs.get(this.tabID).equals(this.tabContentsID);
        }
    }

    public List<TabSelectorClickRegion> getTabSelectorClickRegions() {
        return this.tabSelectorClickRegions;
    }

    public Map<String, String> getOpenedTabs() {
        return this.openedTabs;
    }
    //tab related stuff ends here
}
