package anightdazingzoroark.riftlib.ui.uiElement;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;

public class RiftLibClickableSection {
    //width and height of the section
    protected final int width;
    protected final int height;

    //x and y offsets of the section
    public final int xOffset;
    public final int yOffset;

    //width and height of the screen the section will be put in
    public int guiWidth;
    public int guiHeight;

    protected boolean isHovered;
    protected boolean isSelected;

    //other important shite
    protected Minecraft minecraft;
    protected final FontRenderer fontRenderer;

    //string related stuff
    private String stringToRender;
    private boolean stringHasShadow;
    private int stringColor = 0x000000;
    private int stringHoveredColor = 0xFFFFFF;
    private int stringSelectedColor = 0xFFFF00;
    private int stringXOffset;
    private int stringYOffset;
    private float textScale = 1f;

    //image related stuff
    protected ResourceLocation textureLocation;
    protected int textureWidth, textureHeight, uvWidth, uvHeight;
    protected int xUV, yUV, hoveredXUV, hoveredYUV;
    protected int selectedXUV = -1, selectedYUV = -1;

    //scale related stuff
    protected float scale = 1f;

    //additional offsets
    protected int xAddOffset, yAddOffset;

    protected String stringID = "";

    //for boundary related stuff
    public int scrollTop = Integer.MIN_VALUE;
    public int scrollBottom = Integer.MAX_VALUE;

    //hover effects
    public boolean doHoverEffects;

    public RiftLibClickableSection(int width, int height, int guiWidth, int guiHeight, int xOffset, int yOffset, FontRenderer fontRenderer, Minecraft minecraft) {
        this.width = width;
        this.height = height;
        this.guiWidth = guiWidth;
        this.guiHeight = guiHeight;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.fontRenderer = fontRenderer;
        this.minecraft = minecraft;
    }

    public void addString(String value, boolean withShadow, int color, int stringXOffset, int stringYOffset, float textScale) {
        this.stringToRender = value;
        this.stringHasShadow = withShadow;
        this.stringColor = color;
        this.stringXOffset = stringXOffset;
        this.stringYOffset = stringYOffset;
        this.textScale = textScale;
    }

    public void setStringHoveredColor(int value) {
        this.stringHoveredColor = value;
    }

    public int getStringHoveredColor() {
        return this.stringHoveredColor;
    }

    public void setStringSelectedColor(int value) {
        this.stringSelectedColor = value;
    }

    public int getStringSelectedColor() {
        return this.stringSelectedColor;
    }

    public void addImage(ResourceLocation texture, int uvWidth, int uvHeight, int textureWidth, int textureHeight, int xUV, int yUV, int hoveredXUV, int hoveredYUV) {
        this.textureLocation = texture;
        this.uvWidth = uvWidth;
        this.uvHeight = uvHeight;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
        this.xUV = xUV;
        this.yUV = yUV;
        this.hoveredXUV = hoveredXUV;
        this.hoveredYUV = hoveredYUV;
    }

    public void setSelectedUV(int xUV, int yUV) {
        this.selectedXUV = xUV;
        this.selectedYUV = yUV;
    }

    public void setID(String value) {
        this.stringID = value;
    }

    public String getStringID() {
        return this.stringID;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public void setAdditionalOffset(int xAddOffset, int yAddOffset) {
        this.xAddOffset = xAddOffset;
        this.yAddOffset = yAddOffset;
    }

    public void drawSection(int mouseX, int mouseY) {
        //deal with hovering
        this.isHovered = this.isHovered(mouseX, mouseY);

        //draw image
        if (this.textureLocation != null) {
            //scaling start
            if (this.scale < 1) {
                GlStateManager.pushMatrix();
                GlStateManager.scale(this.scale, this.scale, this.scale);
            }
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.minecraft.getTextureManager().bindTexture(this.textureLocation);
            int k = (int) (((this.guiWidth - (this.width * this.scale)) / 2f + this.xOffset + this.xAddOffset) / this.scale);
            int l = (int) (((this.guiHeight - (this.height * this.scale)) / 2f + this.yOffset + this.yAddOffset) / this.scale);
            boolean isSelected = this.selectedXUV >= 0 && this.selectedYUV >= 0 && this.isSelected;
            int xUVTexture = this.isHovered ? this.hoveredXUV : (isSelected ? this.selectedXUV : this.xUV);
            int yUVTexture = this.isHovered ? this.hoveredYUV : (isSelected ? this.selectedYUV : this.yUV);
            Gui.drawModalRectWithCustomSizedTexture(k, l, xUVTexture, yUVTexture, this.uvWidth, this.uvHeight, this.textureWidth, this.textureHeight);
            //end scaling
            if (this.scale < 1) GlStateManager.popMatrix();
        }

        //draw string
        if (this.stringToRender != null) {
            int stringWidth = this.fontRenderer.getStringWidth(this.stringToRender);
            //scaling start
            if (this.scale < 1) {
                GlStateManager.pushMatrix();
                GlStateManager.scale(this.scale, this.scale, this.scale);
            }
            if (this.textScale < 1) {
                GlStateManager.pushMatrix();
                GlStateManager.scale(this.textScale, this.textScale, this.textScale);
            }
            if (this.stringHasShadow) {
                this.fontRenderer.drawStringWithShadow(
                        this.stringToRender,
                        (int)(((this.guiWidth - (stringWidth * this.scale * this.textScale)) / 2f + this.xOffset + this.stringXOffset + this.xAddOffset) / (this.scale * this.textScale)),
                        (int)(((this.guiHeight - (this.fontRenderer.FONT_HEIGHT * this.scale * this.textScale)) / 2f + this.yOffset + this.stringYOffset + this.yAddOffset) / (this.scale * this.textScale)),
                        this.getTextColor()
                );
            }
            else {
                this.fontRenderer.drawString(
                        this.stringToRender,
                        (int)(((this.guiWidth - (stringWidth * this.scale * this.textScale)) / 2f + this.xOffset + this.stringXOffset + this.xAddOffset) / (this.scale * this.textScale)),
                        (int)(((this.guiHeight - (this.fontRenderer.FONT_HEIGHT * this.scale * this.textScale)) / 2f + this.yOffset + this.stringYOffset + this.yAddOffset) / (this.scale * this.textScale)),
                        this.getTextColor()
                );
            }
            //end scaling
            if (this.textScale < 1) GlStateManager.popMatrix();
            if (this.scale < 1) GlStateManager.popMatrix();
        }
    }

    public boolean isHovered(int mouseX, int mouseY) {
        if (!this.doHoverEffects) return false;
        return mouseX >= this.minClickableArea()[0] && mouseX <= this.maxClickableArea()[0]
                && mouseY >= this.minClickableArea()[1] && mouseY <= this.maxClickableArea()[1]
                && mouseY >= this.scrollTop && mouseY <= this.scrollBottom;
    }

    public int[] minClickableArea() {
        int x = (int) ((this.guiWidth - (this.width * this.scale)) / 2f) + this.xOffset + this.xAddOffset;
        int y = (int) ((this.guiHeight - (this.height * this.scale)) / 2f) + this.yOffset + this.yAddOffset;
        return new int[]{x, y};
    }

    public int[] maxClickableArea() {
        int x = (int) ((this.guiWidth - (this.width * this.scale)) / 2f) + this.xOffset + this.xAddOffset;
        int y = (int) ((this.guiHeight - (this.height * this.scale)) / 2f) + this.yOffset + this.yAddOffset;
        return new int[]{x + (int) (this.width * this.scale), y + (int) (this.height * this.scale)};
    }

    public boolean isHovered() {
        return this.isHovered;
    }

    public void setSelected(boolean value) {
        this.isSelected = value;
    }

    public boolean isSelected() {
        return this.isSelected;
    }

    private int getTextColor() {
        if (this.isHovered) return this.stringHoveredColor;
        else if (this.isSelected) return this.stringSelectedColor;
        return this.stringColor;
    }

    public void playPressSound(SoundHandler soundHandlerIn) {
        soundHandlerIn.playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }
}
