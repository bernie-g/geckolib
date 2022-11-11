package software.bernie.example;

import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class EntityUtils {

	public static AttributeSupplier.Builder createGenericEntityAttributes() {
		return PathfinderMob.createLivingAttributes().add(Attributes.MOVEMENT_SPEED, 0.80000000298023224D)
				.add(Attributes.FOLLOW_RANGE, 16.0D).add(Attributes.MAX_HEALTH, 10.0D);
	}

}
