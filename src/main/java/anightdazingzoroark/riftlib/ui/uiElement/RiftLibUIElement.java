package anightdazingzoroark.riftlib.ui.uiElement;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

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
            else if (this.getAlignment() == ALIGN_CENTER) return x + (sectionWidth - elementWidth) / 2;
            else if (this.getAlignment() == ALIGN_RIGHT) return x + sectionWidth - elementWidth;
            return 0;
        }
    }

    //container elements start here
    public static class ContainerElement extends Element {
        private final List<Element> elements = new ArrayList<>();

        public void addElement(Element element) {
            this.elements.add(element);
        }

        public List<Element> getElements() {
            return this.elements;
        }
    }

    //elements sorted horizontally
    public static class HorizontalContainerElement extends ContainerElement {}

    //elements sorted into a table
    public static class TableContainerElement extends ContainerElement {
        private TextElement textLabel;
        private int rowCount;
        private boolean hasBorders;

        public void addTextLabel(TextElement textLabel) {
            this.textLabel = textLabel;
        }

        public TextElement getTextLabel() {
            return this.textLabel;
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
    }

    //a special type of container element that has tabs, each tab opens a different type of element list
    public static class TabElement extends ContainerElement {
        private int width; //no width means that it will inherit the width of the section
        private final Map<String, List<Element>> tabContents = new HashMap<>();
        private final List<String> tabOrder = new ArrayList<>();

        public void setWidth(int value) {
            this.width = value;
        }

        public int getWidth() {
            return this.width;
        }

        public void addTab(String name, List<Element> contents) {
            this.tabContents.put(name, contents);
            this.tabOrder.add(name);
        }

        public Map<String, List<Element>> getTabContents() {
            return this.tabContents;
        }

        public List<String> getTabOrder() {
            return this.tabOrder;
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
    public static class ProgressBarElement extends Element {}

    //render an entity
    public static class RenderedEntityElement extends Element {
        private Entity entity;

        public void setEntity(Entity value) {
            this.entity = value;
        }

        public Entity getEntity() {
            return this.entity;
        }
    }
    //normal elements end here
}
