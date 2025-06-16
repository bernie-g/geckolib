## GeckoLib v5.1.0

## Additions
* Added `AutoGlowingGeoLayer#shouldAddZOffset` - A toggle to add better compatibility for specific render types or special circumstances
* Added `AutoGlowingGeoLayer#getBrightness` - A configurable value to adjust the brightness of the emissive layer

## Changes
* Added the RenderState to the method parameters of `AutoGlowingGeoLayer#shouldRespectWorldLighting`
* Minor code cleanup

## Bug Fixes
* Fix GeoArmorRenderer not supporting Glowmasks. Required a small rewrite. Is slightly breaking. Sorry.
* Fixed GeoArmorRenderer sharing animations between worn instances when not registered as a syncable GeoAnimatable (#730)