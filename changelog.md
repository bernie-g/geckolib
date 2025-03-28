## GeckoLib v4.9

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

## API Changes
* All GeckoLib assets now go in /assets/geckolib/
  * Animations go in `/assets/<modid>/geckolib/animations/`
  * Models go in `/assets/<modid>/geckolib/models/`
* Renamed `GeckoLibCache` to `GeckoLibResources`
* Removed `FileLoader`
* Converted the FormatVersion to a string and converted it to a more flexible system
* Introduced an identity-based lookup for synced singleton animatables, to hopefully eliminate class-duplication collisions
* Marked AnimationState#getData as nullable to avoid confusion
* Marked the easingType argument in EasingType as nullable to avoid confusion
* Cleaned up the javadoc for EasingType

## New Stuff
* Added `stopTriggeredAnimation` to `SingletonGeoAnimatable`
* Added `triggerArmorAnim` for triggering armor animations (#433)
* Add `query.controller_speed` Molang query
* Added native support for catmull-rom (smooth) easings for bedrock-style animation jsons (Thanks for the initial work Zigy)
* Animated textures now support glowmasks (#456)