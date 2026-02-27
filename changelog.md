## GeckoLib v5.4.4

### Changes
#### API
* Added `RenderPassInfo#getPreRenderMatrixPose` and `RenderPassInfo#getModelRenderMatrixPose`, capturing the entire PoseStack pose instead of just the matrix pose itself
* Added `OverridingDataTicket` - A `DataTicket` implementation that defers to an existing RenderState field first before applying itself.
  * This allows for capturing RenderState field values that may be modified by vanilla or other mods after extraction by the renderer (Fixes #818)

### Bug Fixes
* Fixed looping animations sometimes causing vanishing entities
* Fixed GeckoLib failing to load an animation with an empty string as the keyframe
* Fixed `RenderUtil#transformToBone` manipulating the `PoseStack` in reverse (#819)
* Fixed `RenderUtil#transformToBone` not accounting for back-translating to the pivot point (#822)
* Fixed `MathParser` sometimes parsing mathmematical expressions incorrectly (#820)
* Fixed `GeoItem`s sometimes having their id collide if not registered as a synced animatable
* Fixed some animations replaying the last few frames of their animation occasionally under very specific circumstances