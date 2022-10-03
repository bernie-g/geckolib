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
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.impl.blockrenderlayer.BlockRenderLayerMapImpl;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import software.bernie.example.client.renderer.armor.GeckoArmorRenderer;
import software.bernie.example.client.renderer.entity.BikeGeoRenderer;
import software.bernie.example.client.renderer.entity.ExampleExtendedRendererEntityRenderer;
import software.bernie.example.client.renderer.entity.ExampleGeoRenderer;
import software.bernie.example.client.renderer.entity.LERenderer;
import software.bernie.example.client.renderer.entity.ReplacedCreeperRenderer;
import software.bernie.example.client.renderer.entity.TexturePerBoneTestEntityRenderer;
import software.bernie.example.client.renderer.item.JackInTheBoxRenderer;
import software.bernie.example.client.renderer.item.PistolRender;
import software.bernie.example.client.renderer.tile.FertilizerTileRenderer;
import software.bernie.example.client.renderer.tile.HabitatTileRenderer;
import software.bernie.example.item.GeckoArmorItem;
import software.bernie.example.registry.BlockRegistry;
import software.bernie.example.registry.EntityRegistry;
import software.bernie.example.registry.ItemRegistry;
import software.bernie.example.registry.TileRegistry;
import software.bernie.geckolib3.GeckoLib;
import software.bernie.geckolib3.renderer.geo.GeoArmorRenderer;
import software.bernie.geckolib3.renderer.geo.GeoItemRenderer;

public class ClientListener implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		if (GeckoLibMod.shouldRegisterExamples()) {
			EntityRendererRegistry.INSTANCE.register(EntityRegistry.GEO_EXAMPLE_ENTITY,
					(entityRenderDispatcher, context) -> new ExampleGeoRenderer(entityRenderDispatcher));
			EntityRendererRegistry.INSTANCE.register(EntityRegistry.BIKE_ENTITY,
					(entityRenderDispatcher, context) -> new BikeGeoRenderer(entityRenderDispatcher));
			EntityRendererRegistry.INSTANCE.register(EntityRegistry.GEOLAYERENTITY,
					(entityRenderDispatcher, context) -> new LERenderer(entityRenderDispatcher));
			EntityRendererRegistry.INSTANCE.register(EntityRegistry.EXTENDED_RENDERER_EXAMPLE, (entityRenderDispatcher,
					context) -> new ExampleExtendedRendererEntityRenderer(entityRenderDispatcher));
			EntityRendererRegistry.INSTANCE.register(EntityRegistry.TEXTURE_PER_BONE_EXAMPLE, (entityRenderDispatcher,
					context) -> new TexturePerBoneTestEntityRenderer(entityRenderDispatcher));
			GeoItemRenderer.registerItemRenderer(ItemRegistry.JACK_IN_THE_BOX, new JackInTheBoxRenderer());
			GeoItemRenderer.registerItemRenderer(ItemRegistry.PISTOL, new PistolRender());
			GeoArmorRenderer.registerArmorRenderer(GeckoArmorItem.class, new GeckoArmorRenderer());
			BlockEntityRendererRegistry.INSTANCE.register(TileRegistry.HABITAT_TILE, HabitatTileRenderer::new);
			BlockEntityRendererRegistry.INSTANCE.register(TileRegistry.FERTILIZER, FertilizerTileRenderer::new);

			EntityRendererRegistry.INSTANCE.register(EntityType.CREEPER,
					(entityRenderDispatcher, context) -> new ReplacedCreeperRenderer(entityRenderDispatcher));

			BlockRenderLayerMapImpl.INSTANCE.putBlock(BlockRegistry.HABITAT_BLOCK, RenderLayer.getCutout());
			ClientPlayNetworking.registerGlobalReceiver(EntityPacket.ID, (client, handler, buf, responseSender) -> {
				EntityPacketOnClient.onPacket(client, buf);
			});
		}
	}

	public static class EntityPacketOnClient {
		@Environment(EnvType.CLIENT)
		public static void onPacket(MinecraftClient context, PacketByteBuf byteBuf) {
			EntityType<?> type = Registry.ENTITY_TYPE.get(byteBuf.readVarInt());
			UUID entityUUID = byteBuf.readUuid();
			int entityID = byteBuf.readVarInt();
			double x = byteBuf.readDouble();
			double y = byteBuf.readDouble();
			double z = byteBuf.readDouble();
			float pitch = (byteBuf.readByte() * 360) / 256.0F;
			float yaw = (byteBuf.readByte() * 360) / 256.0F;
			context.execute(() -> {
				ClientWorld world = MinecraftClient.getInstance().world;
				Entity entity = type.create(world);
				if (entity != null) {
					entity.updatePosition(x, y, z);
					entity.updateTrackedPosition(x, y, z);
					entity.pitch = pitch;
					entity.yaw = yaw;
					entity.setEntityId(entityID);
					entity.setUuid(entityUUID);
					world.addEntity(entityID, entity);
				}
			});
		}
	}

	public static class EntityPacket {
		public final static Identifier ID = new Identifier(GeckoLib.ModID, "spawn_entity");

		public static Packet<?> createPacket(Entity entity) {
			PacketByteBuf buf = createBuffer();
			buf.writeVarInt(Registry.ENTITY_TYPE.getRawId(entity.getType()));
			buf.writeUuid(entity.getUuid());
			buf.writeVarInt(entity.getEntityId());
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
