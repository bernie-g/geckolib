package software.bernie.geckolib.services;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.network.GeckoLibNetwork;
import software.bernie.geckolib.constant.dataticket.SerializableDataTicket;
import software.bernie.geckolib.network.packet.*;

public class NeoForgeGeckoLibNetworking implements GeckoLibNetworking {
    @Override
    public <D> void syncBlockEntityAnimData(BlockPos pos, SerializableDataTicket<D> dataTicket, D data, Level serverLevel){
        GeckoLibNetwork.send(new BlockEntityAnimDataSyncPacket<>(pos, dataTicket, data),
                PacketDistributor.TRACKING_CHUNK.with(serverLevel.getChunkAt(pos)));
    }

    @Override
    public void blockEntityAnimTrigger(BlockPos pos, @Nullable String controllerName, String animName, Level serverLevel){
        GeckoLibNetwork.send(new BlockEntityAnimTriggerPacket<>(pos, controllerName, animName),
                PacketDistributor.TRACKING_CHUNK.with(serverLevel.getChunkAt(pos)));
    }

    @Override
    public <D> void syncEntityAnimData(Entity entity, SerializableDataTicket<D> dataTicket, D data){
        GeckoLibNetwork.send(new EntityAnimDataSyncPacket<>(entity.getId(), dataTicket, data), PacketDistributor.TRACKING_ENTITY_AND_SELF.with(entity));
    }

    @Override
    public void entityAnimTrigger(Entity entity, @Nullable String controllerName, String animName){
        GeckoLibNetwork.send(new EntityAnimTriggerPacket<>(entity.getId(), controllerName, animName), PacketDistributor.TRACKING_ENTITY_AND_SELF.with(entity));
    }

    @Override
    public <D> void syncSingletonAnimData(long instanceId, SerializableDataTicket<D> dataTicket, D data, Entity entityToTrack){
        GeckoLibNetwork.send(new AnimDataSyncPacket<>(getClass().toString(), instanceId, dataTicket, data),
                PacketDistributor.TRACKING_ENTITY_AND_SELF.with(entityToTrack));
    }

    @Override
    public void singletonTriggerAnim(String animatableClassName, long instanceId, @Nullable String controllerName, String animName, Entity entityToTrack){
        GeckoLibNetwork.send(new AnimTriggerPacket<>(animatableClassName, instanceId, controllerName, animName),
                PacketDistributor.TRACKING_ENTITY_AND_SELF.with(entityToTrack));
    }
}
