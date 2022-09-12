package software.bernie.geckolib3.util;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import software.bernie.geckolib3.core.manager.AnimationData;

public class AnimationTicker {

    private AnimationData manager;

    public AnimationTicker(AnimationData manager) {
        this.manager = manager;
        ClientTickEvents.START_CLIENT_TICK.register(this::onTick);
    }

    private void onTick(MinecraftClient minecraftClient) {
        manager.tick++;
    }
}
