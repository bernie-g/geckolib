/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.example;

import java.util.UUID;

import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;
import org.quiltmc.qsl.block.extensions.api.client.BlockRenderLayerMap;
import org.quiltmc.qsl.networking.api.ServerPlayNetworking;
import org.quiltmc.qsl.networking.api.client.ClientPlayNetworking;

import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
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
import software.bernie.geckolib3q.GeckoLib;
import software.bernie.geckolib3q.renderers.geo.GeoArmorRenderer;
import software.bernie.geckolib3q.renderers.geo.GeoItemRenderer;

public class ClientListener implements ClientModInitializer {

	@Override
	public void onInitializeClient(ModContainer mod) {
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
					(BlockEntityRendererProvider.Context rendererDispatcherIn) -> new HabitatTileRenderer());
			BlockEntityRendererRegistry.register(TileRegistry.FERTILIZER,
					(BlockEntityRendererProvider.Context rendererDispatcherIn) -> new FertilizerTileRenderer());

			EntityRendererRegistry.register(EntityType.CREEPER, (ctx) -> new ReplacedCreeperRenderer(ctx));

			BlockRenderLayerMap.put(RenderType.cutout(), BlockRegistry.FERTILIZER_BLOCK);
			ClientPlayNetworking.registerGlobalReceiver(EntityPacket.ID, (client, handler, buf, responseSender) -> {
				EntityPacketOnClient.onPacket(client, buf);
			});
		}
	}

	public class EntityPacketOnClient {
		@Environment(EnvType.CLIENT)
		public static void onPacket(Minecraft context, FriendlyByteBuf byteBuf) {
			EntityType<?> type = Registry.ENTITY_TYPE.byId(byteBuf.readVarInt());
			UUID entityUUID = byteBuf.readUUID();
			int entityID = byteBuf.readVarInt();
			double x = byteBuf.readDouble();
			double y = byteBuf.readDouble();
			double z = byteBuf.readDouble();
			float pitch = (byteBuf.readByte() * 360) / 256.0F;
			float yaw = (byteBuf.readByte() * 360) / 256.0F;
			context.execute(() -> {
				ClientLevel world = Minecraft.getInstance().level;
				Entity entity = type.create(world);
				if (entity != null) {
					entity.absMoveTo(x, y, z);
					entity.setPacketCoordinates(x, y, z);
					entity.setXRot(pitch);
					entity.setYRot(yaw);
					entity.setId(entityID);
					entity.setUUID(entityUUID);
					world.putNonPlayerEntity(entityID, entity);
				}
			});
		}
	}

	public class EntityPacket {
		public static final ResourceLocation ID = new ResourceLocation(GeckoLib.ModID, "spawn_entity");

		public static Packet<?> createPacket(Entity entity) {
			FriendlyByteBuf buf = createBuffer();
			buf.writeVarInt(Registry.ENTITY_TYPE.getId(entity.getType()));
			buf.writeUUID(entity.getUUID());
			buf.writeVarInt(entity.getId());
			buf.writeDouble(entity.getX());
			buf.writeDouble(entity.getY());
			buf.writeDouble(entity.getZ());
			buf.writeByte(Mth.floor(entity.getXRot() * 256.0F / 360.0F));
			buf.writeByte(Mth.floor(entity.getYRot() * 256.0F / 360.0F));
			buf.writeFloat(entity.getXRot());
			buf.writeFloat(entity.getYRot());
			return ServerPlayNetworking.createS2CPacket(ID, buf);
		}

		private static FriendlyByteBuf createBuffer() {
			return new FriendlyByteBuf(Unpooled.buffer());
		}

	}
}
