## GeckoLib v5.3-alpha-4

## Alpha 4 Notes:
* Removed `GeoRenderer#postRender` - it no longer has a use

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
* Added `DefaultAnimations#genericWalkFlyIdleController`
* Added a constructor overload for various GeckoLib renderers that takes the item directly and creates a defaulted instance using the object's registered ID for quick and easy handling
    * E.G. `new GeoEntityRenderer(ModEntities.MY_ENTITY);`
* Added `GeoRenderEvent#hasData`
* Added `RenderUtil#getReplacedEntityRenderer`
* Added `RenderUtil#getGeckoLibItemRenderer`
* Added `RenderUtil#getGeckoLibEntityRenderer`
* Added `RenderUtil#getGeckoLibBlockRenderer`
* Added `RenderUtil#getGeckoLibArmorRenderer`
* Added a StreamCodec implementation for `Animation.Stage`
* Added a StreamCodec implementation for `RawAnimation`
* Added `GeoBone#transformToBone`
* Added `RenderStateUtil`
* Added `PerBoneRenderTasks` for neatly handling per-bone render tasks given their added complexity
* Added `GeoRenderer#createRenderState`
* Added `#withRenderLayer` to the various `GeoRenderer`s to allow for a functional instantiation
* Added `RenderModelPositioner`
* Added the associated HumanoidModel to `GeoArmorLayer.RenderData`
* Added `GeoRenderState#getPackedLight`
* Added the `GeoModelRenderer` interface and moved the rendering-specific methods from `GeoRenderer` to this, extending it
  * This was done to move non-typical API methods out of `GeoRenderer` to reduce clutter, and additionally begin separation of actually rendering a `GeoModel` from the renderer, to potentially facilitate additional rendering options in the future.
* Added `GeoRenderer#positionModelForRender`

## Changes
* Port to 1.21.10
    * I apologise for the extra API changes - I decided to use this update as an opportunity to do some more futureproofing for Mojang's render changes that were going to be necessary sooner or later.
    * NOTE: This is an ALPHA build, and may be subject to breaking changes until the alpha tag is removed.
* The PACKED_LIGHT DataTicket is no longer filled for most GeoRenderers, as the base RenderState classes contain `lightCoords` now by default
* `GeoRenderer#defaultRender` has been renamed to `#submitRenderTasks` to better represent its function now that we're not actually rendering at the time of that call
* `GeoRenderer#actuallyRender` has been renamed to `#buildRenderTask` to better represent its function
* `GeoLayer#render` has been renamed to `#buildRenderTask` to better represent its function
* `preRender`, `scaleModelForRender`, and `adjustPositionForRender` have all been moved to _after_ the preRender event check
* Removed various DataTickets from the default setup, where they're just a blatant clone of existing vanilla properties, to eliminate multiple sources of truth and improve performance
* The various Matrix4f variables in GeckoLib renderers has been moved to DataTickets
* Rewrote `GeoArmorRenderer` - it should now be significantly easier to use and understand
* Changed DefaultedEntityGeoModel to take a customisable bone name instead of a flat boolean
* `GeoObjectRenderer` now uses a generic for its `GeoRenderState`, allowing for generic extensibility
* Directly pass the partialTick to `GeoRenderer#addRenderData`, `GeoRenderLayer#addRenderData`, `GeoModel#prepareForRenderPass`, and the various `CompileRenderState` events/hooks (#762)
* Renamed `GeoRenderer#adjustPositionForRender` to `#adjustRenderPose` to better reflect its intended usage
* Made `GeoRenderEvent` and its various sub-events multiloader compatible
* All platform-specific GeckoLib events are now split into their own individual classes to make it easier to find and manage them. E.G. `CompileBlockRenderLayersEvent`
* `ItemArmorGeoLayer.RenderData` now uses `GeoArmorRenderer.ArmorSegment`s instead of manual slots and part getters
* Moved `RenderUtil#getCurrentTick` to `ClientUtil`
* Moved `RenderUtil#arrayToVec` to `JsonUtil`
* Renamed `ItemArmorGeoLayer#prepHumanoidModelForRender` to `#positionModelPartFromBone`
* `BlockAndItemGeoLayer#renderStackForBone` and `#renderBlockForBone` renamed to `#submitItemStackRender` and `#submitBlockRender` respectively
* Moved the `OBJECT_RENDER_POSE` and `MODEL_RENDER_POSE` DataTickets to the base `GeoRenderer`, so that subclasses do not need to handle them in overrides
* Split `PerBoneRender` tasks out into `GeoRenderer#submitPerBoneRenderTasks`
* Moved `GeoRenderer#setBonesVisible` to `RenderUtil` 

## Removals
* Removed `RenderUtil#getCurrentSystemTick`
* Removed `RenderUtil#booleanToFloat`
* Removed `RenderUtil#getGeoModelForEntityType`
* Removed `RenderUtil#getGeoModelForEntity`
* Removed `RenderUtil#getGeoModelForItem`
* Removed `RenderUtil#getGeoModelForBlock`
* Removed `RenderUtil#getGeoModelForArmor`
* Removed some superfluous parameters from `GeoRenderProvider#getGeoArmorRenderer`
* Removed the `skipPerBoneTasks` in several `GeoRenderer` method calls as it is no longer needed
* Removed `GeoEntityRenderer#createBaseRenderState` - Use `#createRenderState` instead
* Removed `GeoRenderer#doPostRenderCleanup`
* `GeoRenderer#reRender` has been removed. Instead, render layers or renderers should submit another render task via `GeoRenderer#buildRenderTask`. Consequently, you no longer need to check for `isReRender` in renderer methods
* Removed `GeoRenderer#firePostRenderEvent` and the associated events, as they no longer have a use

## Bug Fixes
* Add double-depth bedrock keyframe parsing because I have no idea why Blockbench is exporting that
* Fixed triggered animations not visually working on brand-new stacks in multiplayer for other players
* Fixed some incorrect javadocs in AnimationController
* Ensure the `EntityRenderState` is properly extracted before passing to `GeoArmorRenderer` for extraction
* Fixed only handling 1 `PerBoneRenderTask` per `GeoBone`

## Internal Changes
* Reorganised GeckoLib's packets into folders
* Added javadocs to more internal methods and all of the mixins, for clarity
* Improved the Javadoc on `GeoRenderEvent#getRenderData`
* Updated the Javadoc in `GeoRenderer` to account for modern mc rendering changes
* Reorganised `GeoRenderer` to be easier to read