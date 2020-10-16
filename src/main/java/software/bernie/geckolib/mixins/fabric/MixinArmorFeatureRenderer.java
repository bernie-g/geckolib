package software.bernie.geckolib.mixins.fabric;

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
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import software.bernie.geckolib.ArmorRenderingRegistry;

import java.util.Map;

@Mixin(ArmorFeatureRenderer.class)
@Environment(EnvType.CLIENT)
public abstract class MixinArmorFeatureRenderer extends FeatureRenderer {
	@Shadow
	@Final
	private static Map<String, Identifier> ARMOR_TEXTURE_CACHE;

	public MixinArmorFeatureRenderer(FeatureRendererContext context) {
		super(context);
	}

	@Unique
	private LivingEntity storedEntity;
	@Unique
	private EquipmentSlot storedSlot;

	@Inject(method = "render", at = @At("HEAD"))
	private void storeEntity(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, LivingEntity livingEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci) {
		// We store the living entity wearing the armor before we render
		this.storedEntity = livingEntity;
	}

	@Inject(method = "renderArmor", at = @At("HEAD"))
	private void storeSlot(MatrixStack matrices, VertexConsumerProvider vertexConsumers, LivingEntity livingEntity, EquipmentSlot slot, int i, BipedEntityModel bipedEntityModel, CallbackInfo ci) {
		// We store the current armor slot that is rendering before we render each armor piece
		this.storedSlot = slot;
	}

	@Inject(method = "render", at = @At("RETURN"))
	private void removeStored(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, LivingEntity livingEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci) {
		// We remove the stored data after we render
		this.storedEntity = null;
		this.storedSlot = null;
	}

	@Inject(method = "getArmor", at = @At("RETURN"), cancellable = true)
	private void selectArmorModel(EquipmentSlot slot, CallbackInfoReturnable<BipedEntityModel<LivingEntity>> cir) {
		ItemStack stack = storedEntity.getEquippedStack(slot);

		BipedEntityModel<LivingEntity> defaultModel = cir.getReturnValue();
		BipedEntityModel<LivingEntity> model = ArmorRenderingRegistry.getArmorModel(storedEntity, stack, slot, defaultModel);

		if (model != defaultModel) {
			cir.setReturnValue(model);
		}
	}

	@Inject(method = "getArmorTexture", at = @At(value = "INVOKE", target = "Ljava/util/Map;computeIfAbsent(Ljava/lang/Object;Ljava/util/function/Function;)Ljava/lang/Object;"), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
	private void getArmorTexture(ArmorItem armorItem, boolean secondLayer, /* @Nullable */ String suffix, CallbackInfoReturnable<Identifier> cir, String vanillaIdentifier) {
		String model = ArmorRenderingRegistry.getArmorTexture(storedEntity, storedEntity.getEquippedStack(storedSlot), storedSlot, vanillaIdentifier);

		if (model != null) {
			cir.setReturnValue(ARMOR_TEXTURE_CACHE.computeIfAbsent(model, Identifier::new));
		}
	}
}