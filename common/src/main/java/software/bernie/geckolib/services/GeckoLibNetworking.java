package software.bernie.geckolib.services;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.network.SerializableDataTicket;

import javax.annotation.Nullable;

public interface GeckoLibNetworking {

    <D> void syncBlockEntityAnimData(BlockPos pos, SerializableDataTicket<D> dataTicket, D data, Level serverLevel);
    void blockEntityAnimTrigger(BlockPos pos, @Nullable String controllerName, String animName, Level serverLevel);

    <D> void syncEntityAnimData(Entity entity, SerializableDataTicket<D> dataTicket, D data);

    void entityAnimTrigger(Entity entity, @Nullable String controllerName, String animName);

    <D> void syncSingletonAnimData(long instanceId, SerializableDataTicket<D> dataTicket, D data, Entity entityToTrack);

    void singletonTriggerAnim(String animatableClassName, long instanceId, @Nullable String controllerName, String animName, Entity entityToTrack);
}
