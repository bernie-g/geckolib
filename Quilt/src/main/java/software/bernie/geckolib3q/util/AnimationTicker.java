package software.bernie.geckolib3q.util;

import org.quiltmc.qsl.lifecycle.api.client.event.ClientTickEvents;

import net.minecraft.client.Minecraft;
import software.bernie.geckolib3.core.manager.AnimationData;

public class AnimationTicker {

    private AnimationData manager;

    public AnimationTicker(AnimationData manager) {
        this.manager = manager;
        ClientTickEvents.START.register(this::onTick);
    }

    private void onTick(Minecraft minecraftClient) {
        manager.tick++;
    }
}
