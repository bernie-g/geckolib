/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.example;

import java.util.UUID;

import io.netty.buffer.Unpooled;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.PacketContext;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.impl.blockrenderlayer.BlockRenderLayerMapImpl;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;

import software.bernie.example.client.renderer.armor.PotatoArmorRenderer;
import software.bernie.example.client.renderer.entity.BikeGeoRenderer;
import software.bernie.example.client.renderer.entity.ExampleGeoRenderer;
import software.bernie.example.client.renderer.entity.ReplacedCreeperRenderer;
import software.bernie.example.client.renderer.entity.RocketRender;
import software.bernie.example.client.renderer.item.JackInTheBoxRenderer;
import software.bernie.example.client.renderer.item.PistolRender;
import software.bernie.example.client.renderer.tile.BotariumTileRenderer;
import software.bernie.example.client.renderer.tile.FertilizerTileRenderer;
import software.bernie.example.item.PotatoArmorItem;
import software.bernie.example.registry.BlockRegistry;
import software.bernie.example.registry.EntityRegistry;
import software.bernie.example.registry.ItemRegistry;
import software.bernie.example.registry.TileRegistry;
import software.bernie.geckolib3.GeckoLib;
import software.bernie.geckolib3.renderers.geo.GeoArmorRenderer;
import software.bernie.geckolib3.renderers.geo.GeoItemRenderer;
import software.bernie.geckolib3.util.GeoArmorRendererRegistry;

@SuppressWarnings("deprecation")
public class ClientListener implements ClientModInitializer {

	@SuppressWarnings({ "unchecked" })
	@Override
	public void onInitializeClient() {
		if (FabricLoader.getInstance().isDevelopmentEnvironment() && !GeckoLibMod.DISABLE_IN_DEV) {
			EntityRendererRegistry.INSTANCE.register(EntityRegistry.GEO_EXAMPLE_ENTITY, ExampleGeoRenderer::new);
			EntityRendererRegistry.INSTANCE.register(EntityRegistry.BIKE_ENTITY,
					(context) -> new BikeGeoRenderer(context));
			GeoItemRenderer.registerItemRenderer(ItemRegistry.JACK_IN_THE_BOX, new JackInTheBoxRenderer());
			GeoItemRenderer.registerItemRenderer(ItemRegistry.PISTOL, new PistolRender());

			GeoArmorRenderer.registerArmorRenderer(new PotatoArmorRenderer(),
					ItemRegistry.POTATO_HEAD, ItemRegistry.POTATO_CHEST,
					ItemRegistry.POTATO_LEGGINGS, ItemRegistry.POTATO_BOOTS);
			EntityRendererRegistry.INSTANCE.register(EntityRegistry.ROCKET, (ctx) -> new RocketRender(ctx));
			BlockEntityRendererRegistry.INSTANCE.register(TileRegistry.BOTARIUM_TILE,
					(BlockEntityRendererFactory.Context rendererDispatcherIn) -> new BotariumTileRenderer());
			BlockEntityRendererRegistry.INSTANCE.register(TileRegistry.FERTILIZER,
					(BlockEntityRendererFactory.Context rendererDispatcherIn) -> new FertilizerTileRenderer());

			EntityRendererRegistry.INSTANCE.register(EntityType.CREEPER, (ctx) -> new ReplacedCreeperRenderer(ctx));

			BlockRenderLayerMapImpl.INSTANCE.putBlock(BlockRegistry.BOTARIUM_BLOCK, RenderLayer.getCutout());
			ClientSidePacketRegistry.INSTANCE.register(EntityPacket.ID, (ctx, buf) -> {
				EntityPacketOnClient.onPacket(ctx, buf);
			});
		}
	}

	public class EntityPacketOnClient {
		@Environment(EnvType.CLIENT)
		public static void onPacket(PacketContext context, PacketByteBuf byteBuf) {
			EntityType<?> type = Registry.ENTITY_TYPE.get(byteBuf.readVarInt());
			UUID entityUUID = byteBuf.readUuid();
			int entityID = byteBuf.readVarInt();
			double x = byteBuf.readDouble();
			double y = byteBuf.readDouble();
			double z = byteBuf.readDouble();
			float pitch = (byteBuf.readByte() * 360) / 256.0F;
			float yaw = (byteBuf.readByte() * 360) / 256.0F;
			context.getTaskQueue().execute(() -> {
				@SuppressWarnings("resource")
				ClientWorld world = MinecraftClient.getInstance().world;
				Entity entity = type.create(world);
				if (entity != null) {
					entity.updatePosition(x, y, z);
					entity.updateTrackedPosition(x, y, z);
					entity.setPitch(pitch);
					entity.setYaw(yaw);
					entity.setId(entityID);
					entity.setUuid(entityUUID);
					world.addEntity(entityID, entity);
				}
			});
		}
	}

	public class EntityPacket {
		public static final Identifier ID = new Identifier(GeckoLib.ModID, "spawn_entity");

		public static Packet<?> createPacket(Entity entity) {
			PacketByteBuf buf = createBuffer();
			buf.writeVarInt(Registry.ENTITY_TYPE.getRawId(entity.getType()));
			buf.writeUuid(entity.getUuid());
			buf.writeVarInt(entity.getId());
			buf.writeDouble(entity.getX());
			buf.writeDouble(entity.getY());
			buf.writeDouble(entity.getZ());
			buf.writeByte(MathHelper.floor(entity.pitch * 256.0F / 360.0F));
			buf.writeByte(MathHelper.floor(entity.yaw * 256.0F / 360.0F));
			buf.writeFloat(entity.pitch);
			buf.writeFloat(entity.yaw);
			return ServerPlayNetworking.createS2CPacket(ID, buf);
		}

		private static PacketByteBuf createBuffer() {
			return new PacketByteBuf(Unpooled.buffer());
		}

	}
}
