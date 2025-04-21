## GeckoLib v5.0

## New Things
* Added `ItemInHandGeoLayer`
* Added `PerBoneRender`
* Added `DataTickets#IS_LEFT_HANDED`
* Added `DataTickets#IS_CROUCHING`
* Added `GeoRender#adjustPositionForRender`
* Added `DirectionalProjectileRenderer`
* Added `TextureLayerGeoLayer`
* Added `CustomBoneTextureGeoLayer`

## Internal Changes
* Removed some unnecessary warnings when loading animation or model files without their suffixes
* Fixed the javadocs in `DefaultedGeoModel` using the old format
* Moved the scale attribute handling into `scaleModelForRender` for `GeoEntityRenderer`, and moved sleeping pose translation to better account for scaling
* Moved `scaleModelForRender` out of `preRender` and into its own call
* Optimised `RenderUtil#getTextureDimensions` and allowed it to account for post-loading modifications
* Optimised `BakedGeoModel#getBone`

## API Changes
* Removed `GeoRenderer#applyRenderLayersForBone`. Per-bone renders are now added in `#preApplyRenderLayers`
* Added `GeoRenderLayer#addPerBoneRender`
* Changed how `ItemArmorGeoLayer` works to be more efficient, and support Elytras (although they're not 100% correct yet)
* Changed how `BlockAndItemGeoLayer` works to be more efficient and cleaner
* Changed `ItemArmorGeoLayer#prepModelPartForRender` to `prepHumanoidModelForRender`
* Changed `GeckoLibClient#getArmorModelForItem` to return a `HumanoidModel` instead of a base `Model`, since non-humanoid models never get used
* Added the `packedLight`, `packedOverlay`, and `renderColor` to `GeoRenderer#renderFinal`
* Removed `final` from `GeoEntityRenderer#calculateYRot`
* Removed `BoneFilterGeoLayer` and `FastBoneFilterGeoLayer`

## Bug Fixes
* Fixed `GeckoLibAnimatedTexture` crashing when failing to load a texture
* Fixed Per-bone render tasks messing with query values
* Fixed PoseStack manipulations in render layers messing with bone-position getters
* Fixed `GeoItemRenderer` and `GeoObjectRenderer` positioning incorrectly when scaled
* Fixed `GeoReplacedEntityRenderer` not propagating PoseStack manipulations
* Fixed `ItemArmorGeoLayer` colliding with other layers that use the same DataTicket