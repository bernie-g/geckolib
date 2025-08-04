package anightdazingzoroark.example.ui;

import anightdazingzoroark.riftlib.RiftLib;
import anightdazingzoroark.riftlib.ui.RiftLibUI;
import anightdazingzoroark.riftlib.ui.RiftLibUISection;
import anightdazingzoroark.riftlib.ui.uiElement.RiftLibUIElement;
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

        toReturn.add(new RiftLibUISection(this.width, this.height, 166, 156, 0, 0, this.fontRenderer, this.mc) {
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
                //imageElement.setScale(2f);
                elementsToReturn.add(imageElement);

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
}
