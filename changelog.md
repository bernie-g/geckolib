## GeckoLib v5.3

## Additions
* Added "Stateless" animatables. These are an alternate way of handling animations for GeckoLib animatables.
  * See: https://github.com/bernie-g/geckolib/wiki/Stateless-Animatable-Handling-(Geckolib5)
  * Added:
    * `StatelessGeoBlockEntity`
    * `StatelessGeoEntity`
    * `StatelessGeoObject`
    * `StatelessGeoReplacedEntity`
    * `StatelessGeoSingletonAnimatable`
    * `StatelessAnimationController`
* Added `attack.punch` DefaultAnimation constant
* Added `misc.idle.flying` DefaultAnimation constant
* Added `move.dive` DefaultAnimation constant
* Added `DefaultAnimations#triggerOnlyController` for creating a controller specifically for arbitrary triggered animations
* Added `RawAnimation#getStageCount`
* Added `AnimationController#getStateHandler`
* Directly pass the partialTick to `GeoRenderer#addRenderData`, `GeoRenderLayer#addRenderData`, `GeoModel#prepareForRenderPass`, and the various `CompileRenderState` events/hooks (#762)
* Added `DefaultAnimations#genericWalkFlyIdleController`
* Added a constructor overload for various GeckoLib renderers that takes the item directly and creates a defaulted instance using the object's registered ID for quick and easy handling
    * E.G. `new GeoEntityRenderer(ModEntities.MY_ENTITY);`

## Changes
* Port to 1.21.9
* Change DefaultedEntityGeoModel to take a customisable bone name instead of a flat boolean

## Bug Fixes
* Add double-depth bedrock keyframe parsing because I have no idea why Blockbench is exporting that
* Fixed triggered animations not visually working on brand-new stacks in multiplayer for other players

## Internal Changes
* Reorganised GeckoLib's packets into folders
* Added a StreamCodec implementation for `Animation.Stage`
* Added a StreamCodec implementation for `RawAnimation`