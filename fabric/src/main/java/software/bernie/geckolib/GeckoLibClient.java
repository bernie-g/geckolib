package software.bernie.geckolib;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.ApiStatus;
import software.bernie.geckolib.cache.GeckoLibCache;
import software.bernie.geckolib.network.packet.MultiloaderPacket;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * Main GeckoLib client entrypoint
 */
public class GeckoLibClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES)
                .registerReloadListener(new IdentifiableResourceReloadListener() {
                    @Override
                    public ResourceLocation getFabricId() {
                        return GeckoLibConstants.id("models_animations");
                    }

                    @Override
                    public CompletableFuture<Void> reload(PreparationBarrier synchronizer, ResourceManager manager,
                                                          ProfilerFiller prepareProfiler, ProfilerFiller applyProfiler, Executor prepareExecutor,
                                                          Executor applyExecutor) {
                        return GeckoLibCache.reload(synchronizer, manager, prepareProfiler, applyProfiler, prepareExecutor, applyExecutor);
                    }
                });
    }

    @ApiStatus.Internal
    public static <P extends MultiloaderPacket> void registerPacket(CustomPacketPayload.Type<P> packetType) {
        ClientPlayNetworking.registerGlobalReceiver(packetType, (packet, context) -> packet.receiveMessage(context.player(), context.client()::execute));
    }
}
