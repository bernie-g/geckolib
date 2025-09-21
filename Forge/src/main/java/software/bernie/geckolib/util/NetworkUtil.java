package software.bernie.geckolib.util;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.network.FriendlyByteBuf;
import software.bernie.geckolib.core.animation.Animation;
import software.bernie.geckolib.core.animation.RawAnimation;

/**
 * Helper class for networking-related functionality GeckoLib uses.
 */
public final class NetworkUtil {
    /**
     * Write a {@link RawAnimation} to a network buffer
     */
    public static void writeRawAnimationToBuffer(RawAnimation rawAnimation, FriendlyByteBuf buffer) {
        buffer.writeCollection(rawAnimation.getAnimationStages(), NetworkUtil::writeAnimationStageToBuffer);
    }

    /**
     * Read a {@link RawAnimation} from a network buffer
     */
    public static RawAnimation readRawAnimationFromBuffer(FriendlyByteBuf buffer) {
        return new RawAnimation(buffer.readCollection(ObjectArrayList::new, NetworkUtil::readAnimationStageFromBuffer));
    }

    /**
     * Write a {@link RawAnimation.Stage} to a network buffer
     */
    public static void writeAnimationStageToBuffer(FriendlyByteBuf buffer, RawAnimation.Stage stage) {
        buffer.writeUtf(stage.animationName());
        buffer.writeUtf(stage.loopType().getId());
        buffer.writeVarInt(stage.additionalTicks());
    }

    /**
     * Read a {@link RawAnimation.Stage} from a network buffer
     */
    public static RawAnimation.Stage readAnimationStageFromBuffer(FriendlyByteBuf buffer) {
        final String name = buffer.readUtf();
        final Animation.LoopType loopType = Animation.LoopType.fromString(buffer.readUtf());
        final int additionalTicks = buffer.readVarInt();

        return new RawAnimation.Stage(name, loopType, additionalTicks);
    }
}
