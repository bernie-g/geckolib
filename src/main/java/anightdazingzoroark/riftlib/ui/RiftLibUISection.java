package anightdazingzoroark.riftlib.ui;

import anightdazingzoroark.riftlib.ui.uiElement.RiftLibUIElement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

import java.util.List;

import static net.minecraft.client.gui.Gui.drawModalRectWithCustomSizedTexture;
import static net.minecraft.client.gui.Gui.drawRect;

public abstract class RiftLibUISection {
    //section size related stuff
    public final int width;
    public final int height;

    //section position related stuff
    public final int xPos;
    public final int yPos;

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
    protected int scrollbarYOffset;

    //other stuff required for section to work properly
    public final int guiWidth;
    public final int guiHeight;
    protected final Minecraft minecraft;
    protected final FontRenderer fontRenderer;

    public RiftLibUISection(int guiWidth, int guiHeight, int width, int height, int xPos, int yPos, FontRenderer fontRenderer, Minecraft minecraft) {
        this.guiWidth = guiWidth;
        this.guiHeight = guiHeight;
        this.width = width;
        this.height = height;
        this.xPos = xPos;
        this.yPos = yPos;
        this.fontRenderer = fontRenderer;
        this.minecraft = minecraft;
    }

    //draw elements to be drawn
    public abstract List<RiftLibUIElement.Element> defineSectionContents();

    //draw contents as defined in defineSectionContents()
    public void drawSectionContents(int mouseX, int mouseY, float partialTicks) {
        int sectionX = (this.guiWidth - this.width) / 2 + this.xPos;
        int sectionY = (this.guiHeight - this.height) / 2 + this.yPos;

        //scissor setup
        ScaledResolution res = new ScaledResolution(this.minecraft);
        int scaleFactor = res.getScaleFactor();
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(sectionX * scaleFactor, (this.minecraft.displayHeight - (sectionY + this.height) * scaleFactor), this.width * scaleFactor, this.height * scaleFactor);

        int drawY = sectionY - this.scrollOffset;
        int totalHeight = 0;
        this.contentHeight = 0;

        //elements are drawn here
        for (int i = 0; i < this.defineSectionContents().size(); i++) {
            RiftLibUIElement.Element element = this.defineSectionContents().get(i);

            //draw the elements and add up their height
            totalHeight += this.drawElement(element, this.width, sectionX, drawY + totalHeight, mouseX, mouseY, partialTicks);

            //extra bottom height for certain elements
            if (i < this.defineSectionContents().size() - 1) totalHeight += element.getBottomSpace();
        }

        //scroll management is dealt with here
        this.contentHeight = totalHeight;
        this.maxScroll = Math.max(0, this.contentHeight - this.height);

        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        //draw scrollbar
        if (this.contentHeight > this.height) {
            float ratio = (float) this.scrollOffset / (float) this.maxScroll;
            int thumbHeight = Math.max(20, (int)((float) this.height * this.height / (float) this.contentHeight));
            int thumbY = sectionY + (int)((this.height - thumbHeight) * ratio);
            int scrollX = sectionX + this.width - this.scrollbarWidth + this.scrollbarXOffset;

            drawRect(scrollX, sectionY, scrollX + this.scrollbarWidth, sectionY + this.height, 0xFF333333);
            drawRect(scrollX, thumbY, scrollX + this.scrollbarWidth, thumbY + thumbHeight, 0xFFAAAAAA);
        }
    }

    //return value is the total height created by these elements
    private int drawElement(RiftLibUIElement.Element element, int sectionWidth, int x, int y, int mouseX, int mouseY, float partialTicks) {
        if (element instanceof RiftLibUIElement.TextElement) {
            RiftLibUIElement.TextElement textElement = (RiftLibUIElement.TextElement) element;
            float scale = textElement.getScale();
            int lines = 1;

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

            return lines * this.fontRenderer.FONT_HEIGHT;
        }
        else if (element instanceof RiftLibUIElement.ImageElement) {
            RiftLibUIElement.ImageElement imageElement = (RiftLibUIElement.ImageElement) element;
            float scale = imageElement.getScale();

            int scaledImageWidth = (int) (imageElement.getImageUVSize()[0] * scale);
            int scaledImageHeight = (int) (imageElement.getImageUVSize()[1] * scale);

            int totalImgX = imageElement.xOffsetFromAlignment(sectionWidth, scaledImageWidth, x);

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

            return scaledImageHeight;
        }
        return 0;
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
            int scrollX = sectionX + this.width - this.scrollbarWidth + this.scrollbarXOffset;

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
}
