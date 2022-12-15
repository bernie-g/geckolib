package software.bernie.geckolib3q.mixins.fabric;

import java.util.Map;
import java.util.Objects;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.mojang.blaze3d.vertex.PoseStack;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib3q.ArmorRenderingRegistryImpl;

@Mixin(value = HumanoidArmorLayer.class, priority = 700)
@Environment(EnvType.CLIENT)
public abstract class MixinArmorFeatureRenderer extends RenderLayer {
	@Shadow
	@Final
	private static Map<String, ResourceLocation> ARMOR_LOCATION_CACHE;
	@Unique
	private LivingEntity gl_storedEntity;
	@Unique
	private EquipmentSlot gl_storedSlot;

	public MixinArmorFeatureRenderer(RenderLayerParent context) {
		super(context);
	}

	@Inject(method = { "render" }, at = { @At("HEAD") })
	private void storeEntity(PoseStack PoseStack, MultiBufferSource MultiBufferSource, int i,
			LivingEntity livingEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci) {
		this.gl_storedEntity = livingEntity;
	}

	@Inject(method = { "renderArmorPiece" }, at = { @At("HEAD") })
	private void storeSlot(PoseStack matrices, MultiBufferSource vertexConsumers, LivingEntity livingEntity,
			EquipmentSlot slot, int i, HumanoidModel HumanoidModel, CallbackInfo ci) {
		this.gl_storedSlot = slot;
	}

	@Inject(method = { "render" }, at = { @At("RETURN") })
	private void removeStored(PoseStack PoseStack, MultiBufferSource MultiBufferSource, int i,
			LivingEntity livingEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci) {
		this.gl_storedEntity = null;
		this.gl_storedSlot = null;
	}

	@Inject(method = { "getArmorModel" }, at = { @At("RETURN") }, cancellable = true)
	private void selectArmorModel(EquipmentSlot slot, CallbackInfoReturnable<HumanoidModel<LivingEntity>> cir) {
		ItemStack stack = this.gl_storedEntity.getItemBySlot(slot);
		HumanoidModel<LivingEntity> defaultModel = cir.getReturnValue();
		HumanoidModel<LivingEntity> model = ArmorRenderingRegistryImpl.getArmorModel(this.gl_storedEntity, stack,
				slot, defaultModel);
		if (model != defaultModel) {
			cir.setReturnValue(model);
		}
	}

	@Inject(method = { "getArmorLocation" }, at = {
			@At(value = "INVOKE", target = "Ljava/util/Map;computeIfAbsent(Ljava/lang/Object;Ljava/util/function/Function;)Ljava/lang/Object;") }, cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
	private void getArmorTexture(ArmorItem armorItem, boolean secondLayer, String suffix,
			CallbackInfoReturnable<ResourceLocation> cir, String vanillaResourceLocation) {
		String texture = ArmorRenderingRegistryImpl
				.getArmorTexture(this.gl_storedEntity, this.gl_storedEntity.getItemBySlot(this.gl_storedSlot),
						this.gl_storedSlot, secondLayer, suffix, new ResourceLocation(vanillaResourceLocation))
				.toString();
		if (!Objects.equals(texture, vanillaResourceLocation)) {
			cir.setReturnValue(ARMOR_LOCATION_CACHE.computeIfAbsent(texture, ResourceLocation::new));
		}

	}
}