package software.bernie.geckolib.file;

import software.bernie.geckolib.core.builder.Animation;

import java.util.HashMap;

public class AnimationFile {
    private final HashMap<String, Animation> animations = new HashMap<>();

    public Animation getAnimation(String name) {
        return animations.get(name);
    }

    public void putAnimation(String name, Animation animation) {
        this.animations.put(name, animation);
    }
}
