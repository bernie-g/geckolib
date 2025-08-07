package anightdazingzoroark.riftlib.ui.uiElement;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RiftLibUIElement {
    //for alignment
    public static final int ALIGN_LEFT = 0;
    public static final int ALIGN_CENTER = 1;
    public static final int ALIGN_RIGHT = 2;

    public static class Element {
        private String id = "";
        private int alignment;
        private int bottomSpace = 9;
        private float scale = 1f;

        public void setID(String value) {
            this.id = value;
        }

        public String getID() {
            return this.id;
        }

        public void setAlignment(int value) {
            this.alignment = value;
        }

        public int getAlignment() {
            return this.alignment;
        }

        public void setBottomSpace(int value) {
            this.bottomSpace = value;
        }

        public int getBottomSpace() {
            return this.bottomSpace;
        }

        public void setScale(float value) {
            this.scale = value;
        }

        public float getScale() {
            return this.scale;
        }

        public int xOffsetFromAlignment(int sectionWidth, int elementWidth, int x) {
            if (this.getAlignment() == ALIGN_LEFT) return x;
            else if (this.getAlignment() == ALIGN_CENTER) return x + (int) Math.ceil((sectionWidth - elementWidth) / 2f);
            else if (this.getAlignment() == ALIGN_RIGHT) return x + sectionWidth - elementWidth;
            return 0;
        }
    }

    //container elements start here
    //elements sorted into a table
    public static class TableContainerElement extends Element {
        private final List<Element> elements = new ArrayList<>();
        private int rowCount;
        private boolean hasBorders;
        private int[] cellSize = new int[]{0, 0};

        public void addElement(Element element) {
            this.elements.add(element);
        }

        public List<Element> getElements() {
            return this.elements;
        }

        public void setRowCount(int value) {
            this.rowCount = value;
        }

        public int getRowCount() {
            return this.rowCount;
        }

        public void setHasBorders() {
            this.hasBorders = true;
        }

        public boolean getHasBorders() {
            return this.hasBorders;
        }

        public void setCellSize(int width, int height) {
            this.cellSize = new int[]{width, height};
        }

        public int[] getCellSize() {
            return this.cellSize;
        }
    }

    //a special type of container element that has tabs, each tab opens a different type of element list
    public static class TabElement extends Element {
        private int width; //no width means that it will inherit the width of the section
        private final List<TabContents> tabContents = new ArrayList<>();
        private int tabSelectorTextColor = 0xFFFFFF;
        private int tabSelectorHoverColor = 0xFFFF00;
        private int tabSelectorSelectedColor = 0x5A3B1A;

        public void setWidth(int value) {
            this.width = value;
        }

        public int getWidth() {
            return this.width;
        }

        public void addTab(String id, String name, List<Element> contents) {
            this.tabContents.add(new TabContents(id, name, contents));
        }

        public List<TabContents> getTabContents() {
            return this.tabContents;
        }

        public TabContents getTabContentsByID(String idToSearch) {
            for (TabContents contents : this.tabContents) {
                if (contents.tabContentID.equals(idToSearch)) return contents;
            }
            return null;
        }

        public void setTabSelectorTextColor(int value) {
            this.tabSelectorTextColor = value;
        }

        public int getTabSelectorTextColor() {
            return this.tabSelectorTextColor;
        }

        public void setTabSelectorHoverColor(int value) {
            this.tabSelectorHoverColor = value;
        }

        public int getTabSelectorHoverColor() {
            return this.tabSelectorHoverColor;
        }

        public void setTabSelectorSelectedColor(int value) {
            this.tabSelectorSelectedColor = value;
        }

        public int getTabSelectorSelectedColor() {
            return this.tabSelectorSelectedColor;
        }
    }

    public static class TabContents {
        public final String tabContentID;
        public final String tabName;
        private final List<Element> tabContents;

        public TabContents(String id, String name, List<Element> tabContents) {
            this.tabContentID = id;
            this.tabName = name;
            this.tabContents = tabContents;
        }

        public List<Element> getTabContents() {
            return this.tabContents;
        }
    }
    //container elements end here

    //normal elements start here
    public static class TextElement extends Element {
        private String text = "";
        private int textColor = 0x000000;
        private boolean singleLine = false;

        public void setText(String value) {
            this.text = value;
        }

        public String getText() {
            return this.text;
        }

        public void setTextColor(int value) {
            this.textColor = value;
        }

        public int getTextColor() {
            return this.textColor;
        }

        public void setSingleLine() {
            this.singleLine = true;
        }

        public boolean getSingleLine() {
            return this.singleLine;
        }
    }

    public static class ImageElement extends Element {
        private ResourceLocation image;
        private int[] textureSize; //size of the entire texture
        private int[] imageUV; //uv start pos of texture
        private int[] uvSize; //size of the portion of the texture to be used

        public void setImage(ResourceLocation image, int textureWidth, int textureHeight, int uvWidth, int uvHeight, int uvX, int uvY) {
            this.image = image;
            this.textureSize = new int[]{textureWidth, textureHeight};
            this.imageUV = new int[]{uvX, uvY};
            this.uvSize = new int[]{uvWidth, uvHeight};
        }

        public ResourceLocation getImage() {
            return this.image;
        }

        public int[] getTextureSize() {
            return this.textureSize;
        }

        public int[] getImageUV() {
            return this.imageUV;
        }

        public int[] getImageUVSize() {
            return this.uvSize;
        }
    }

    //for in-game items
    public static class ItemElement extends Element {
        private ItemStack itemStack;

        public void setItemStack(ItemStack itemStack) {
            this.itemStack = itemStack;
        }

        public ItemStack getItemStack() {
            return this.itemStack;
        }
    }

    //for representation of a tool
    public static class ToolElement extends Element {
        private String overlayText = "";
        private String toolType;
        private int miningLevel = -1;

        public void setOverlayText(String value) {
            this.overlayText = value;
        }

        public String getOverlayText() {
            return this.overlayText;
        }

        public void setToolType(String value) {
            this.toolType = value;
        }

        public String getToolType() {
            return this.toolType;
        }

        public void setMiningLevel(int value) {
            this.miningLevel = value;
        }

        public int getMiningLevel() {
            return this.miningLevel;
        }
    }

    public static class ButtonElement extends Element {
        private String buttonText;
        private int[] size = {60, 20};

        public void setText(String name) {
            this.buttonText = name;
        }

        public String getName() {
            return this.buttonText;
        }

        public void setSize(int width, int height) {
            this.size = new int[]{width, height};
        }

        public int[] getSize() {
            return this.size;
        }
    }

    //a clickable section is a button element but with a custom texture
    public static class ClickableSectionElement extends Element {
        private int[] size; //size of clickable element

        private String textContent = "";
        private int textColor = 0x000000;
        private int textHoveredColor = 0xFFFFFF;
        private int textSelectedColor = 0xFFFF00;
        private float textScale = 1f;
        private int[] textOffsets = {0, 0};

        private ResourceLocation imageContent;
        private int[] textureSize; //size of the entire texture
        private int[] imageUV; //uv start pos of texture
        private int[] imageHoveredUV; //uv start pos of texture when hovered
        private int[] imageSelectedUV;
        private int[] uvSize; //size of the portion of the texture to be used
        private float imageScale = 1f;

        public void setSize(int width, int height) {
            this.size = new int[]{width, height};
        }

        public int[] getSize() {
            return this.size;
        }

        public void setTextContent(String value) {
            this.textContent = value;
        }

        public String getTextContent() {
            return this.textContent;
        }

        public void setTextColor(int value) {
            this.textColor = value;
        }

        public int getTextColor() {
            return this.textColor;
        }

        public void setTextHoveredColor(int value) {
            this.textHoveredColor = value;
        }

        public int getTextHoveredColor() {
            return this.textHoveredColor;
        }

        public void setTextSelectedColor(int value) {
            this.textSelectedColor = value;
        }

        public int getTextSelectedColor() {
            return this.textSelectedColor;
        }

        public void setTextScale(float value) {
            this.textScale = value;
        }

        public float getTextScale() {
            return this.textScale;
        }

        public void setTextOffsets(int xOffset, int yOffset) {
            this.textOffsets = new int[]{xOffset, yOffset};
        }

        public int[] getTextOffsets() {
            return this.textOffsets;
        }

        public void setImage(ResourceLocation location, int textureWidth, int textureHeight, int uvWidth, int uvHeight, int uvX, int uvY, int uvHoveredX, int uvHoveredY) {
            this.imageContent = location;
            this.textureSize = new int[]{textureWidth, textureHeight};
            this.imageUV = new int[]{uvX, uvY};
            this.imageHoveredUV = new int[]{uvHoveredX, uvHoveredY};
            this.uvSize = new int[]{uvWidth, uvHeight};
        }

        public ResourceLocation getImage() {
            return this.imageContent;
        }

        public int[] getImageSize() {
            return this.textureSize;
        }

        public int[] getImageUV() {
            return this.imageUV;
        }

        public int[] getImageHoveredUV() {
            return this.imageHoveredUV;
        }

        public int[] getUVSize() {
            return this.uvSize;
        }

        public void setImageSelectedUV(int uvX, int uvY) {
            this.imageSelectedUV = new int[]{uvX, uvY};
        }

        public int[] getImageSelectedUV() {
            return this.imageSelectedUV;
        }

        public void setImageScale(float value) {
            this.imageScale = value;
        }

        public float getImageScale() {
            return this.imageScale;
        }
    }

    //for special boxes where you enter text
    public static class TextBoxElement extends Element {
        private int width = 0;
        private String defaultText = "";

        public void setWidth(int value) {
            this.width = value;
        }

        public int getWidth() {
            return this.width;
        }

        public void setDefaultText(String value) {
            this.defaultText = value;
        }

        public String getDefaultText() {
            return this.defaultText;
        }
    }

    //for progress bars that fill up based on a certain percentage
    public static class ProgressBarElement extends Element {
        private int width;
        private int overlayColor;
        private float percentage = 1f;
        private int backgroundColor;
        private float factor = 0; //change in progress bar

        public void setColors(int overlayColor, int backgroundColor) {
            this.overlayColor = overlayColor;
            this.backgroundColor = backgroundColor;
        }

        public int getOverlayColor() {
            return this.overlayColor;
        }

        public int getBackgroundColor() {
            return this.backgroundColor;
        }

        public void setWidth(int value) {
            this.width = value;
        }

        public int getWidth() {
            return this.width;
        }

        public void setPercentage(float value) {
            this.percentage = MathHelper.clamp(value, 0, 1f);
        }

        public float getPercentage() {
            return this.percentage;
        }

        public void setFactor(float value) {
            this.factor = value;
        }

        public float getFactor() {
            return this.factor;
        }

        public int contentXOffsetFromAlignment(int sectionWidth, int elementWidth, int x, float scale) {
            int contentXOffset = 0;
            if (this.getAlignment() == ALIGN_LEFT) contentXOffset = (int) Math.ceil(scale);
            else if (this.getAlignment() == ALIGN_CENTER) contentXOffset = 0;
            else if (this.getAlignment() == ALIGN_RIGHT) contentXOffset = - (int) Math.ceil(scale);
            return this.xOffsetFromAlignment(sectionWidth, elementWidth, x) + contentXOffset;
        }
    }

    //render an entity
    public static class RenderedEntityElement extends Element {
        private Entity entity;
        private int[] additionalSize = {0, 0};
        private int rotationAngle = 150;

        public void setEntity(Entity value) {
            this.entity = value;
        }

        public Entity getEntity() {
            return this.entity;
        }

        public void setAdditionalSize(int width, int height) {
            this.additionalSize = new int[]{width, height};
        }

        public int[] getAdditionalSize() {
            return this.additionalSize;
        }

        public void setRotationAngle(int value) {
            this.rotationAngle = value;
        }

        public int getRotationAngle() {
            return this.rotationAngle;
        }

        public int xOffsetFromAlignment(int sectionWidth, int elementWidth, float scale, int x) {
            return super.xOffsetFromAlignment(sectionWidth, elementWidth, x) + (int) Math.ceil(scale / 2f);
        }
    }
    //normal elements end here
}
