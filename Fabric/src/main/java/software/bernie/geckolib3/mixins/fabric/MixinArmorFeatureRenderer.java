package software.bernie.geckolib3.mixins.fabric;

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

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.ArmorRenderingRegistryImpl;

@Environment(EnvType.CLIENT)
@Mixin(value = ArmorFeatureRenderer.class, priority = 700)
public abstract class MixinArmorFeatureRenderer extends FeatureRenderer {
	@Shadow
	@Final
	private static Map<String, Identifier> ARMOR_TEXTURE_CACHE;
	@Unique
	private LivingEntity gl_storedEntity;
	@Unique
	private EquipmentSlot gl_storedSlot;

	public MixinArmorFeatureRenderer(FeatureRendererContext context) {
		super(context);
	}

	@Inject(method = { "render" }, at = { @At("HEAD") })
	private void storeEntity(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i,
			LivingEntity livingEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci) {
		this.gl_storedEntity = livingEntity;
	}

	@Inject(method = { "renderArmor" }, at = { @At("HEAD") })
	private void storeSlot(MatrixStack matrices, VertexConsumerProvider vertexConsumers, LivingEntity livingEntity,
			EquipmentSlot slot, int i, BipedEntityModel bipedEntityModel, CallbackInfo ci) {
		this.gl_storedSlot = slot;
	}

	@Inject(method = { "render" }, at = { @At("RETURN") })
	private void removeStored(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i,
			LivingEntity livingEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci) {
		this.gl_storedEntity = null;
		this.gl_storedSlot = null;
	}

	@Inject(method = { "getArmor" }, at = { @At("RETURN") }, cancellable = true)
	private void selectArmorModel(EquipmentSlot slot, CallbackInfoReturnable<BipedEntityModel<LivingEntity>> cir) {
		ItemStack stack = this.gl_storedEntity.getEquippedStack(slot);
		BipedEntityModel<LivingEntity> defaultModel = cir.getReturnValue();
		BipedEntityModel<LivingEntity> model = ArmorRenderingRegistryImpl.getArmorModel(this.gl_storedEntity, stack,
				slot, defaultModel);
		if (model != defaultModel) {
			cir.setReturnValue(model);
		}
	}

	@Inject(method = { "getArmorTexture" }, at = {
			@At(value = "INVOKE", target = "Ljava/util/Map;computeIfAbsent(Ljava/lang/Object;Ljava/util/function/Function;)Ljava/lang/Object;") }, cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
	private void getArmorTexture(ArmorItem armorItem, boolean secondLayer, String suffix,
			CallbackInfoReturnable<Identifier> cir, String vanillaIdentifier) {
		String texture = ArmorRenderingRegistryImpl
				.getArmorTexture(this.gl_storedEntity, this.gl_storedEntity.getEquippedStack(this.gl_storedSlot),
						this.gl_storedSlot, secondLayer, suffix, new Identifier(vanillaIdentifier))
				.toString();
		if (!Objects.equals(texture, vanillaIdentifier)) {
			cir.setReturnValue(ARMOR_TEXTURE_CACHE.computeIfAbsent(texture, Identifier::new));
		}

	}
}