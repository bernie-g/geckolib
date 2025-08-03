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

        toReturn.add(new RiftLibUISection(this.width, this.height, 176, 166, 0, 0, this.fontRenderer, this.mc) {
            @Override
            public List<RiftLibUIElement.Element> defineSectionContents() {
                List<RiftLibUIElement.Element> elementsToReturn = new ArrayList<>();

                //hello world text element
                RiftLibUIElement.TextElement helloWorldText = new RiftLibUIElement.TextElement();
                helloWorldText.setText("Hello world!");
                //helloWorldText.setAlignment(RiftLibUIElement.ALIGN_CENTER);
                helloWorldText.setScale(0.75f);
                //helloWorldText.setSingleLine();
                elementsToReturn.add(helloWorldText);

                //rift lib intro text element
                RiftLibUIElement.TextElement introText = new RiftLibUIElement.TextElement();
                introText.setText("This text has been opened by hitting the sky with a piece of paper!");
                //introText.setAlignment(RiftLibUIElement.ALIGN_CENTER);
                introText.setScale(0.75f);
                //introText.setSingleLine();
                elementsToReturn.add(introText);

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
