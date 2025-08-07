package anightdazingzoroark.example.ui;

import anightdazingzoroark.riftlib.RiftLib;
import anightdazingzoroark.riftlib.ui.RiftLibUI;
import anightdazingzoroark.riftlib.ui.RiftLibUISection;
import anightdazingzoroark.riftlib.ui.uiElement.RiftLibButton;
import anightdazingzoroark.riftlib.ui.uiElement.RiftLibClickableSection;
import anightdazingzoroark.riftlib.ui.uiElement.RiftLibUIElement;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class HelloWorldUI extends RiftLibUI {
    public HelloWorldUI(int x, int y, int z) {
        super(x, y, z);
    }

    @Override
    public List<RiftLibUISection> uiSections() {
        List<RiftLibUISection> toReturn = new ArrayList<>();

        toReturn.add(new RiftLibUISection("mainSection", this.width, this.height, 166, 156, 0, 0, this.fontRenderer, this.mc) {
            @Override
            public List<RiftLibUIElement.Element> defineSectionContents() {
                List<RiftLibUIElement.Element> elementsToReturn = new ArrayList<>();

                //hello world text element
                RiftLibUIElement.TextElement helloWorldText = new RiftLibUIElement.TextElement();
                helloWorldText.setText("Hello world!");
                helloWorldText.setAlignment(RiftLibUIElement.ALIGN_CENTER);
                //helloWorldText.setScale(0.75f);
                helloWorldText.setSingleLine();
                elementsToReturn.add(helloWorldText);

                //rift lib intro text element
                RiftLibUIElement.TextElement introText = new RiftLibUIElement.TextElement();
                introText.setText("This text has been opened by hitting the sky with a piece of paper!");
                introText.setAlignment(RiftLibUIElement.ALIGN_LEFT);
                //introText.setScale(0.75f);
                //introText.setSingleLine();
                elementsToReturn.add(introText);

                RiftLibUIElement.TextElement one = new RiftLibUIElement.TextElement();
                one.setText("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam tincidunt, turpis auctor tincidunt sollicitudin, nunc est dictum mi, eget vehicula nulla arcu a velit. Proin odio nibh, lacinia non blandit id, faucibus nec massa. Nulla vehicula convallis nisl. Maecenas ultrices ultricies quam sit amet tempor. Maecenas vestibulum, ligula eu suscipit tincidunt, lorem augue congue odio, a faucibus augue erat ut ante. Cras imperdiet neque nec purus mollis maximus. Vivamus congue quam quis erat varius, sed iaculis nibh auctor. Praesent tempor vitae felis vel rutrum. Nunc vitae risus lorem. Nulla venenatis molestie hendrerit. Sed mollis risus non fringilla ornare.");
                one.setAlignment(RiftLibUIElement.ALIGN_CENTER);
                //introText.setScale(0.75f);
                //introText.setSingleLine();
                elementsToReturn.add(one);

                RiftLibUIElement.TextElement two = new RiftLibUIElement.TextElement();
                two.setText("Maecenas at varius felis. Aenean malesuada est in tellus porttitor aliquam. Nulla facilisi. Vestibulum commodo nec ipsum sit amet pretium. Nullam sit amet felis et nulla imperdiet fermentum. Aliquam lacus turpis, dictum vitae nisl ac, facilisis tristique dolor. Maecenas laoreet iaculis urna quis dapibus. Phasellus non pellentesque nulla. Aliquam congue libero vel ex elementum condimentum. Sed ac dapibus turpis.");
                two.setAlignment(RiftLibUIElement.ALIGN_CENTER);
                //introText.setScale(0.75f);
                //introText.setSingleLine();
                elementsToReturn.add(two);

                RiftLibUIElement.TextElement three = new RiftLibUIElement.TextElement();
                three.setText("Morbi at ultricies enim. Praesent est enim, ornare et vestibulum molestie, convallis pharetra sapien. Nullam sem neque, hendrerit sit amet turpis ut, commodo euismod mauris. Suspendisse auctor lobortis elit. Cras feugiat egestas felis in tincidunt. Integer varius elit non eleifend consequat. Maecenas feugiat lacus egestas, rutrum neque ac, vulputate velit. Mauris ac blandit magna. Maecenas finibus diam ac ornare lacinia. Morbi nibh lorem, vestibulum quis feugiat sed, pellentesque non sem. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec hendrerit placerat magna, a sodales ex rutrum eget.");
                three.setAlignment(RiftLibUIElement.ALIGN_CENTER);
                //introText.setScale(0.75f);
                //introText.setSingleLine();
                elementsToReturn.add(three);

                //image test
                RiftLibUIElement.ImageElement imageElement = new RiftLibUIElement.ImageElement();
                imageElement.setImage(
                        new ResourceLocation(RiftLib.ModID, "textures/ui/controls.png"),
                        64,
                        48,
                        16,
                        16,
                        0,
                        0
                );
                imageElement.setAlignment(RiftLibUIElement.ALIGN_CENTER);
                elementsToReturn.add(imageElement);

                //item test
                RiftLibUIElement.ItemElement itemElement = new RiftLibUIElement.ItemElement();
                itemElement.setAlignment(RiftLibUIElement.ALIGN_CENTER);
                Item stickItem = Item.getByNameOrId("minecraft:stick");
                if (stickItem != null) {
                    itemElement.setItemStack(new ItemStack(stickItem));
                    elementsToReturn.add(itemElement);
                }

                //tool  test
                RiftLibUIElement.ToolElement toolElement = new RiftLibUIElement.ToolElement();
                toolElement.setAlignment(RiftLibUIElement.ALIGN_CENTER);
                toolElement.setToolType("pickaxe");
                toolElement.setMiningLevel(2);
                toolElement.setOverlayText("ui.hovered_tool");
                toolElement.setScale(1f);
                elementsToReturn.add(toolElement);

                //button test
                RiftLibUIElement.ButtonElement buttonElement = new RiftLibUIElement.ButtonElement();
                buttonElement.setText("My Button");
                buttonElement.setID("myButton");
                buttonElement.setSize(80, 20);
                buttonElement.setAlignment(RiftLibUIElement.ALIGN_CENTER);
                elementsToReturn.add(buttonElement);

                //deactivatable button test
                RiftLibUIElement.ButtonElement deactivatableButton = new RiftLibUIElement.ButtonElement();
                deactivatableButton.setText("Deactivatable Button");
                deactivatableButton.setID("deactivatable");
                deactivatableButton.setSize(100, 20);
                deactivatableButton.setAlignment(RiftLibUIElement.ALIGN_CENTER);
                elementsToReturn.add(deactivatableButton);

                //clickable section test
                RiftLibUIElement.ClickableSectionElement clickableSection = new RiftLibUIElement.ClickableSectionElement();
                clickableSection.setSize(60, 20);
                clickableSection.setID("selectableSection");
                clickableSection.setTextContent("Click Me!");
                clickableSection.setTextOffsets(0, 1);
                clickableSection.setTextScale(0.75f);
                clickableSection.setImage(
                        new ResourceLocation(RiftLib.ModID, "textures/ui/example_clickable_section.png"),
                        60,
                        60,
                        60,
                        20,
                        0,
                        0,
                        0,
                        20
                );
                clickableSection.setImageSelectedUV(0, 40);
                clickableSection.setImageScale(0.5f);
                clickableSection.setAlignment(RiftLibUIElement.ALIGN_RIGHT);
                elementsToReturn.add(clickableSection);

                //text box test
                RiftLibUIElement.TextBoxElement textBoxSection = new RiftLibUIElement.TextBoxElement();
                textBoxSection.setWidth(80);
                textBoxSection.setID("myTextBox");
                textBoxSection.setDefaultText("Hello");
                textBoxSection.setAlignment(RiftLibUIElement.ALIGN_CENTER);
                elementsToReturn.add(textBoxSection);

                //progress bar
                RiftLibUIElement.ProgressBarElement progressBarElement = new RiftLibUIElement.ProgressBarElement();
                progressBarElement.setPercentage(0.75f);
                progressBarElement.setColors(0xff0000, 0x868686);
                progressBarElement.setAlignment(RiftLibUIElement.ALIGN_CENTER);
                progressBarElement.setScale(1f);
                progressBarElement.setWidth(80);
                elementsToReturn.add(progressBarElement);

                //entity
                RiftLibUIElement.RenderedEntityElement renderedEntityElement = new RiftLibUIElement.RenderedEntityElement();
                renderedEntityElement.setScale(40f);
                renderedEntityElement.setAlignment(RiftLibUIElement.ALIGN_CENTER);
                renderedEntityElement.setEntity(new EntityChicken(this.minecraft.world));
                elementsToReturn.add(renderedEntityElement);

                //table
                RiftLibUIElement.TableContainerElement tableContainerElement = new RiftLibUIElement.TableContainerElement();
                tableContainerElement.setRowCount(2);
                tableContainerElement.setAlignment(RiftLibUIElement.ALIGN_CENTER);
                tableContainerElement.setCellSize(70, 30);

                //table contents
                RiftLibUIElement.ButtonElement buttonOneElement = new RiftLibUIElement.ButtonElement();
                buttonOneElement.setSize(60, 20);
                tableContainerElement.addElement(buttonOneElement);
                RiftLibUIElement.ButtonElement buttonTwoElement = new RiftLibUIElement.ButtonElement();
                buttonTwoElement.setSize(60, 20);
                tableContainerElement.addElement(buttonTwoElement);
                RiftLibUIElement.ButtonElement buttonThreeElement = new RiftLibUIElement.ButtonElement();
                buttonThreeElement.setSize(60, 20);
                tableContainerElement.addElement(buttonThreeElement);

                elementsToReturn.add(tableContainerElement);

                //tab contents
                RiftLibUIElement.TabElement tabElement = new RiftLibUIElement.TabElement();
                tabElement.setWidth(150);

                //tab one
                List<RiftLibUIElement.Element> tabOne = new ArrayList<>();
                RiftLibUIElement.TextElement tabOneTextLineOne = new RiftLibUIElement.TextElement();
                tabOneTextLineOne.setText("This is the first tab!");
                RiftLibUIElement.TextElement tabOneTextLineTwo = new RiftLibUIElement.TextElement();
                tabOneTextLineTwo.setText("There are many more tabs like this one!");
                tabOne.add(tabOneTextLineOne);
                tabOne.add(tabOneTextLineTwo);
                tabElement.addTab("tabOne", "One", tabOne);

                //tab two
                List<RiftLibUIElement.Element> tabTwo = new ArrayList<>();
                RiftLibUIElement.TextElement tabTwoTextLineOne = new RiftLibUIElement.TextElement();
                tabTwoTextLineOne.setText("This is the second tab!");
                RiftLibUIElement.TextElement tabTwoTextLineTwo = new RiftLibUIElement.TextElement();
                tabTwoTextLineTwo.setText("This is another tab that you can select!");
                tabTwo.add(tabTwoTextLineOne);
                tabTwo.add(tabTwoTextLineTwo);
                tabElement.addTab("tabTwo", "Two", tabTwo);

                elementsToReturn.add(tabElement);

                return elementsToReturn;
            }
        });

        return toReturn;
    }

    @Override
    public ResourceLocation drawBackground() {
        return new ResourceLocation(RiftLib.ModID, "textures/ui/generic_screen.png");
    }

    @Override
    public int[] backgroundTextureSize() {
        return new int[]{176, 166};
    }

    @Override
    public int[] backgroundUV() {
        return new int[]{0, 0};
    }

    @Override
    public int[] backgroundSize() {
        return new int[]{176, 166};
    }

    @Override
    public void onButtonClicked(RiftLibButton button) {
        if (button.buttonId.equals("myButton")) {
            //get deactivatable button
            RiftLibButton deactivatableButton = this.getButtonByID("deactivatable");

            if (deactivatableButton != null) {
                this.setButtonUsabilityByID("deactivatable", !deactivatableButton.enabled);
            }
        }
    }

    @Override
    public void onClickableSectionClicked(RiftLibClickableSection clickableSection) {
        if (clickableSection.getStringID().equals("selectableSection")) {
            RiftLibClickableSection sectionToSelect = this.getClickableSectionByID("selectableSection");

            if (sectionToSelect != null) {
                this.setSelectClickableSectionByID("selectableSection", !sectionToSelect.isSelected());
            }
        }
    }
}
