package software.bernie.geckolib.renderer.internal;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.renderer.base.GeoRenderer;
import software.bernie.geckolib.renderer.base.PerBoneRender;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Wrapper class for handling the {@link PerBoneRender} task system.
 * <p>
 * Because of the dynamic nature of GeckoLib, there can be multiple renderers involved in a single render pass.<br>
 * Each renderer then can have its own per-bone render tasks, all in the same {@link GeoRenderState}.
 * <p>
 * This container separates render task lists by renderer, then groups them by bone for more efficient handling,
 * and offers convenience methods for ease of use.
 */
@ApiStatus.Internal
public record PerBoneRenderTasks(Map<GeoRenderer<?, ?, ?>, ForRenderer<?>> map) {
    public static PerBoneRenderTasks create() {
        return new PerBoneRenderTasks(new Reference2ObjectOpenHashMap<>(0));
    }

    /**
     * Get the {@link PerBoneRenderTasks.ForRenderer} collection for the given renderer, or a new empty one if non exists
     */
    public static <R extends GeoRenderState> PerBoneRenderTasks.ForRenderer<R> get(GeoRenderState renderState, GeoRenderer<?, ?, R> renderer) {
        PerBoneRenderTasks tasks = renderState.getOrDefaultGeckolibData(DataTickets.PER_BONE_TASKS, null);

        if (tasks == null)
            tasks = create();

        return (ForRenderer<R>)tasks.map.computeIfAbsent(renderer, ForRenderer::new);
    }

    /**
     * Add a new {@link PerBoneRender} task
     *
     * @param renderer The renderer this task belongs to (or the renderer the layer belongs to in the case of {@link GeoRenderLayer} usage
     * @param bone The bone this task is for
     * @param task The task to add
     */
    public <R extends GeoRenderState> void addTask(GeoRenderer<?, ?, R> renderer, GeoBone bone, PerBoneRender<R> task) {
        this.map.computeIfAbsent(renderer, ForRenderer::new).addTask(bone, (PerBoneRender)task);
    }

    /**
     * @return Whether this collection has any applied tasks for any renderers
     *
     * @see ForRenderer#isEmpty()
     */
    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    /**
     * Sub-record containing only the {@link PerBoneRender} tasks for a single renderer
     */
    public record ForRenderer<R extends GeoRenderState>(Map<GeoBone, List<PerBoneRender<R>>> tasks) implements Iterable<Map.Entry<GeoBone, List<PerBoneRender<R>>>> {
        ForRenderer(GeoRenderer<?, ?, R> renderer) {
            this(new Reference2ObjectArrayMap<>(0));
        }

        /**
         * Add a new {@link PerBoneRender} task
         *
         * @param bone The bone this task is for
         * @param task The task to add
         */
        public void addTask(GeoBone bone, PerBoneRender<R> task) {
            this.tasks.computeIfAbsent(bone, k -> new ObjectArrayList<>())
                    .add(task);
        }

        /**
         * @return Whether this collection has any applied tasks
         */
        public boolean isEmpty() {
            return this.tasks.isEmpty();
        }

        @NotNull
        @Override
        public Iterator<Map.Entry<GeoBone, List<PerBoneRender<R>>>> iterator() {
            return this.tasks.entrySet().iterator();
        }
    }
}
