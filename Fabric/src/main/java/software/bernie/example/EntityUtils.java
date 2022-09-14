package software.bernie.example;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class EntityUtils {

	public static AttributeSupplier.Builder createGenericEntityAttributes() {
		return LivingEntity.createLivingAttributes().add(Attributes.MOVEMENT_SPEED, 0.80000000298023224D)
				.add(Attributes.MAX_HEALTH, 10.0D);
	}

}
