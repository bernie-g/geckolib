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
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.impl.blockrenderlayer.BlockRenderLayerMapImpl;
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
import software.bernie.example.client.renderer.armor.GeckoArmorRenderer;
import software.bernie.example.client.renderer.entity.BikeGeoRenderer;
import software.bernie.example.client.renderer.entity.CarGeoRenderer;
import software.bernie.example.client.renderer.entity.ExampleExtendedRendererEntityRenderer;
import software.bernie.example.client.renderer.entity.ExampleGeoRenderer;
import software.bernie.example.client.renderer.entity.LERenderer;
import software.bernie.example.client.renderer.entity.ReplacedCreeperRenderer;
import software.bernie.example.client.renderer.entity.RocketRender;
import software.bernie.example.client.renderer.entity.TexturePerBoneTestEntityRenderer;
import software.bernie.example.client.renderer.item.JackInTheBoxRenderer;
import software.bernie.example.client.renderer.item.PistolRender;
import software.bernie.example.client.renderer.tile.FertilizerTileRenderer;
import software.bernie.example.client.renderer.tile.HabitatTileRenderer;
import software.bernie.example.registry.BlockRegistry;
import software.bernie.example.registry.EntityRegistry;
import software.bernie.example.registry.ItemRegistry;
import software.bernie.example.registry.TileRegistry;
import software.bernie.geckolib3.GeckoLib;
import software.bernie.geckolib3.renderers.geo.GeoArmorRenderer;
import software.bernie.geckolib3.renderers.geo.GeoItemRenderer;

public class ClientListener implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		if (GeckoLibMod.shouldRegisterExamples()) {
			EntityRendererRegistry.register(EntityRegistry.GEO_EXAMPLE_ENTITY, ExampleGeoRenderer::new);
			EntityRendererRegistry.register(EntityRegistry.GEOLAYERENTITY, LERenderer::new);
			EntityRendererRegistry.register(EntityRegistry.BIKE_ENTITY, BikeGeoRenderer::new);
			EntityRendererRegistry.register(EntityRegistry.CAR_ENTITY, CarGeoRenderer::new);
			EntityRendererRegistry.register(EntityRegistry.EXTENDED_RENDERER_EXAMPLE,
					ExampleExtendedRendererEntityRenderer::new);
			EntityRendererRegistry.register(EntityRegistry.TEXTURE_PER_BONE_EXAMPLE,
					TexturePerBoneTestEntityRenderer::new);
			GeoItemRenderer.registerItemRenderer(ItemRegistry.JACK_IN_THE_BOX, new JackInTheBoxRenderer());
			GeoItemRenderer.registerItemRenderer(ItemRegistry.PISTOL, new PistolRender());
			GeoArmorRenderer.registerArmorRenderer(new GeckoArmorRenderer(), ItemRegistry.GECKOARMOR_HEAD,
					ItemRegistry.GECKOARMOR_CHEST, ItemRegistry.GECKOARMOR_LEGGINGS, ItemRegistry.GECKOARMOR_BOOTS);
			EntityRendererRegistry.register(EntityRegistry.ROCKET, (ctx) -> new RocketRender(ctx));
			BlockEntityRendererRegistry.register(TileRegistry.HABITAT_TILE,
					(BlockEntityRendererFactory.Context rendererDispatcherIn) -> new HabitatTileRenderer());
			BlockEntityRendererRegistry.register(TileRegistry.FERTILIZER,
					(BlockEntityRendererFactory.Context rendererDispatcherIn) -> new FertilizerTileRenderer());

			EntityRendererRegistry.register(EntityType.CREEPER, (ctx) -> new ReplacedCreeperRenderer(ctx));

			BlockRenderLayerMapImpl.INSTANCE.putBlock(BlockRegistry.HABITAT_BLOCK, RenderLayer.getCutout());
			ClientPlayNetworking.registerGlobalReceiver(EntityPacket.ID, (client, handler, buf, responseSender) -> {
				EntityPacketOnClient.onPacket(client, buf);
			});
		}
	}

	public class EntityPacketOnClient {
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
			buf.writeByte(MathHelper.floor(entity.getPitch() * 256.0F / 360.0F));
			buf.writeByte(MathHelper.floor(entity.getYaw() * 256.0F / 360.0F));
			buf.writeFloat(entity.getPitch());
			buf.writeFloat(entity.getYaw());
			return ServerPlayNetworking.createS2CPacket(ID, buf);
		}

		private static PacketByteBuf createBuffer() {
			return new PacketByteBuf(Unpooled.buffer());
		}

	}
}
