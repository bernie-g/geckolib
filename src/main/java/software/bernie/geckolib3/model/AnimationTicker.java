package software.bernie.geckolib3.model;

import net.minecraft.client.Minecraft;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import software.bernie.geckolib3.core.manager.AnimationData;

public class AnimationTicker
{
    private final AnimationData data;

    public AnimationTicker(AnimationData data)
    {
        this.data = data;
    }

    @SubscribeEvent
    public void tickEvent(TickEvent.ClientTickEvent event)
    {
        if(Minecraft.getInstance().isGamePaused() && !data.shouldPlayWhilePaused)
        {
            return;
        }

        if (event.phase == TickEvent.Phase.END)
        {
            data.tick++;
        }
    }
}
