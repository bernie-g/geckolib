package software.bernie.geckolib.services;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.network.GeckoLibNetwork;
import software.bernie.geckolib.constant.dataticket.SerializableDataTicket;
import software.bernie.geckolib.network.packet.*;

public class FabricGeckoLibNetworking implements GeckoLibNetworking {
    @Override
    public <D> void syncBlockEntityAnimData(BlockPos pos, SerializableDataTicket<D> dataTicket, D data, Level serverLevel){
        BlockEntityAnimDataSyncPacket<D> blockEntityAnimDataSyncPacket = new BlockEntityAnimDataSyncPacket<>(pos, dataTicket, data);
        GeckoLibNetwork.sendToEntitiesTrackingChunk(blockEntityAnimDataSyncPacket, (ServerLevel) serverLevel, pos);
    }

    @Override
    public void blockEntityAnimTrigger(BlockPos pos, @Nullable String controllerName, String animName, Level serverLevel){
        BlockEntityAnimTriggerPacket blockEntityAnimTriggerPacket = new BlockEntityAnimTriggerPacket(pos, controllerName, animName);
        GeckoLibNetwork.sendToEntitiesTrackingChunk(blockEntityAnimTriggerPacket, (ServerLevel) serverLevel, pos);
    }

    @Override
    public <D> void syncEntityAnimData(Entity entity, SerializableDataTicket<D> dataTicket, D data){
        EntityAnimDataSyncPacket<D> entityAnimDataSyncPacket = new EntityAnimDataSyncPacket<>(entity.getId(), dataTicket, data);
        GeckoLibNetwork.sendToTrackingEntityAndSelf(entityAnimDataSyncPacket, entity);
    }

    @Override
    public void entityAnimTrigger(Entity entity, @Nullable String controllerName, String animName){
        EntityAnimTriggerPacket entityAnimTriggerPacket = new EntityAnimTriggerPacket(entity.getId(), controllerName, animName);
        GeckoLibNetwork.sendToTrackingEntityAndSelf(entityAnimTriggerPacket, entity);
    }

    @Override
    public <D> void syncSingletonAnimData(long instanceId, SerializableDataTicket<D> dataTicket, D data, Entity entityToTrack){
        GeckoLibNetwork.sendToTrackingEntityAndSelf(new AnimDataSyncPacket<>(getClass().toString(), instanceId, dataTicket, data), entityToTrack);
    }

    @Override
    public void singletonTriggerAnim(String animatableClassName, long instanceId, @Nullable String controllerName, String animName, Entity entityToTrack){
        GeckoLibNetwork.sendToTrackingEntityAndSelf(new AnimTriggerPacket(animatableClassName, instanceId, controllerName, animName), entityToTrack);
    }
}
