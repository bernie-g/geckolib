package software.bernie.example;

import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.PathAwareEntity;

public class EntityUtils {

	public static DefaultAttributeContainer.Builder createGenericEntityAttributes() {
		return PathAwareEntity.createMobAttributes().add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.80000000298023224D)
				.add(EntityAttributes.GENERIC_MAX_HEALTH, 10.0D);
	}

}
