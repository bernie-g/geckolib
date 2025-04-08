## GeckoLib v5.0-alpha1

## NOTE
* As of `alpha1` glowmasks and animated textures are not functional. This will be resolved soon :)
* Additionally, I'm looking into alternate solutions for per-bone render handling since Mojang has made the previous Dynamic renderers not practical.
* If you use any of these functionalities, wait until the 5.0 full release, thanks.

## General Changes
* Updated to Minecraft 1.21.5
* Converted the changelog to Markdown format

## Internal Changes
* Rewrote the resource loading system to properly account for parallelised resource handling without hammering I/O
* Made GeoArmorRenderer inherit bone visibility from HumanoidArmorLayer#setPartVisibility to better work with third-party mods
* Default boneResetTime to 5-ticks, and auto-complete the reset the tick time is 0. This prevents unavoidable partial-tick bone resets
* Fixed middle-clicking on synced animatable items causing them to conflict when using triggered animations or synced data (#681)
* Bones that now use animations to do a full rotation should now no longer counter-rotate when resetting, allowing for cleaner rotation animations
* Synced animatable items no longer prevent stacking with each other
* Added memory compression to animation data. Larger modpacks should receive a reduction in memory usage
* Added a safety check for invalid use of Actor Variables in molang
* GeckoLib Entity rendering is now automatically compatible with NeoForge EntityRenderState modifiers
* Overrode vanilla's default hard-cap on entity nameplate rendering of 64 blocks. You can now go up to 256 blocks
* Fixed world-space positioning on GeoBones not accounting for partial-tick lerping
* Fixed non-living entities not having their Y-rotation accounted for in rendering
* Fixed death rotation doubling up on partialTick
* Fixed the BufferSource not being properly non-nulled in `GeoObjectRenderer`
* Fixed `GeoEntityRenderer` re-applying other PoseStack manipulations when reRendering
* Moved a lot of the render value creation back to vanilla to allow for better compatibility with vanilla and other mods
* Removed a number of superfluous method parameters throughout `AnimationProcessor`
* Renamed `AnimationController#process` to `AnimationController#startTick`
* Fixed the `DefaultAnimations#genericFlyController` not stopping if not moving
* Moved the AnimationState to prior to rendering to allow for optimised Molang Query population
* Fixed `AnimationController`'s animation speed potentially being re-computed multiple times per render pass, resulting in inconsistent animation handling
* Renamed `Keyframes` to `KeyframeMarkers`
* GeckoLib now pre-determines which Molang Queries will be used for any given render frame, and pre-compute all relevant values and cache them. This allows for complex animation
* Moved `AnimatableManager` and any derivatives to a different package
* Moved `AnimationController`, `AnimationProcessor`, `AnimationState` to a different package
* Moved `AutoPlayingSoundKeyframeHandler` to a different package
* Removed `AnimationPointQueue`
* Moved per-bone render layer rendering to _after_ the rest of rendering is complete
* Changed the way `DyeableGeoArmorRenderer` works to be a bit more accurate
* Fixed some javadoc mistakes in the various GeoRenderEvent classes

## API Changes
* All GeckoLib assets now go in /assets/geckolib/
  * Animations go in `/assets/<modid>/geckolib/animations/`
  * Models go in `/assets/<modid>/geckolib/models/`
* All GeoRenderers now run on GeoRenderStates, rather than passing the animatable around. Mojang has made it clear this is the way we have to go :(
  * Removed `GeoRenderer#getAnimatable`
  * Removed various parameters from GeoRenderer's methods, moved them to `GeoRenderState` by default
  * Removed `GeoEntityRenderer#isShaking`
* `AnimationState` is now an internal class used for carrying the animation state through processing. Users should now use `AnimationTest`. It works functionally the same
* GeoRenderers extensions now take a second generic type; that of the RenderState type (and GeoRenderState)
* GeoModel model/animation references no longer expect the file path prefix or suffix (I.E. You do not need to include `geo/` or `.geo.json` in your returned model resources)
* `DataTicket`s now require creation through a factory method to ensure identity-parity
* `SerializableDataTicket`s no longer require registration, but must still be created during mod construct
* All render methods in `GeoRenderer` has had the PoseStack and RenderState (previously Animatable) swap places in the args
* Molang variables are now all functions that operate on the current `AnimationState` for the render pass
* `AnimationController`s no longer need the animatable passed to it
* Split the texture-retrieval and usage in `AutoGlowingGeoLayer` so that mods can override the texture as needed
* All `DefaultAnimations` helpers now default to 0-tick transition time for consistency and clarity
* Converted the FormatVersion to a string and converted it to a more flexible system
* Introduced an identity-based lookup for synced singleton animatables, to hopefully eliminate class-duplication collisions
* Marked `AnimationState#getData` as nullable to avoid confusion
* Marked the easingType argument in `EasingType` as nullable to avoid confusion
* Cleaned up the javadoc for EasingType
* `GeoReplacedEntityRenderer` now explicitly crashes upon trying to insert an entity as the animatable to prevent memory leaks
* Brought `GeoReplacedEntityRenderer` much more in line with `GeoEntityRenderer` to ensure accuracy
* `CustomInstructionKeyframeEvent`, `SoundKeyframeEvent`, and `ParticleKeyframeEvent` have all been consolidated into `KeyFrameEvent`
* `CustomInstructionKeyframeHandler`, `SoundKeyframeHandler`, and `ParticleKeyframeHandler` have all been consolidated into `KeyframeEventHandler`
* Converted `KeyFrameEvent` to a record
* Converted `Calculation` to a record
* `MolangQueries` is once again side-agnostic
* Various parts of the GeoRenderers are additionally provided an associated object for handling (ItemStack, replaced entity, etc.)
* Moved `prepLivingEntityRenderState` into `GeoEntityRenderer` so it can be overridden if needed
* Removed `EntityModelData`
* Removed `DeferredGeoRenderProvider`
* Removed `InternalUtil`
* Removed `FileLoader`
* Removed `Color`. Has been fully superceded by `net.minecraft.util.ARGB`
* Removed the `GeoRenderer` parameter from the various `GeoModel` getters as it is no longer needed
* Renamed the bone variables in `GeoArmorRenderer` to prevent collisions with `HumanoidArmorModel`'s field names
* Renamed `GeckoLibCache` to `GeckoLibResources`
* Renamed `AnimatableManager#setData` to `AnimatableManager#setAnimatableData` (and its associated getter)
* Renamed `AutoGlowingTexture#getEmissiveResource` to `#getOrCreateEmissiveTexture` for clarity
* Moved `GeoEntityRenderState` to `GeoRenderState`
* The various Singleton registration helpers in `GeckoLibUtil` have been moved to the new `SyncedSingletonAnimatableCache`, with associated methods having their hierarchical scope reduced for clarity and safety
* Renamed `GeoRenderLayer#renderForBone` to `#createPerBoneRender`, which now requires that you return your operation as a runnable to be run later
* Removed `DynamicGeoBlockRenderer`, `DynamicGeoEntityRenderer`, and `DynamicGeoItemRenderer`. Use render layers instead for bone-specific handling. I will be looking at creating a helper class or alternate option for this
* Improved the generic typing for the various `GeoRenderEvent`s to be more useful

## New Stuff
* Added `stopTriggeredAnimation` to `SingletonGeoAnimatable`
* Added `triggerArmorAnim` for triggering armor animations (#433)
* Added `query.controller_speed` Molang query
* Added `query.limb_swing` Molang query
* Added `query.limb_swing_amount` Molang query
* Added native support for catmull-rom (smooth) easings for bedrock-style animation jsons (Thanks for the initial work Zigy)
* Animated textures now support glowmasks (#456)
* Added helper methods in `GeoRenderer` `#setBonesVisible` for ease of use
* Added `AnimationController#isTriggeredAnimation` to check for the currently triggered animation
* Added `AnimationController#setAnimationState` to directly set the state of the controller, if necessary
* Added `CompileRenderState` event hooks for each renderer type