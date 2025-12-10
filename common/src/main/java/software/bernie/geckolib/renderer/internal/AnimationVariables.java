package software.bernie.geckolib.renderer.internal;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import it.unimi.dsi.fastutil.objects.Reference2DoubleMap;
import it.unimi.dsi.fastutil.objects.Reference2DoubleOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.loading.math.value.Variable;

/**
 * Container object for holding compiled {@link Variable} values for an upcoming render pass
 */
public record AnimationVariables<T extends GeoAnimatable>(Supplier<Reference2ObjectMap<AnimationController<T>, Reference2DoubleMap<Variable>>> variables) {
    public AnimationVariables() {
        this(Suppliers.memoize(Reference2ObjectOpenHashMap::new));
    }

    /**
     * Get the variable map for a given controller, creating it anew if required
     */
    public Reference2DoubleMap<Variable> forController(AnimationController<T> controller) {
        return this.variables.get().computeIfAbsent(controller, key -> new Reference2DoubleOpenHashMap<>());
    }
}
