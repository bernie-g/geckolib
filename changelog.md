## GeckoLib v5.5.2

### Bug Fixes
* Made improvements to the AnimationController to handle stopping and starting animations better (#868)
* Fixed non-GeckoLib armour rotating incorrectly when placed on a GeckolibEntity using `ItemArmorGeoLayer` (#843)
* Fix animations going weird speeds when things render existing objects with a partialTick of 1.0 for some reason (#848)
* Patch out GeckoLib's custom emissivity if not on windows, since linux and mac both fail gl functionality (#864)
* Fix EasingTypes being case-sensitive and then just kinda failing entirely (#861)
* Optimize out unused faces, remove auto-face expansion (#855)
* Fix inconsistency with tick retrieval for non-entity renderers (#846)
* Fix wrong frozen-state handling for animated items
* Fixed triggerable animations not stopping when told to stop if the loop type does not allow stopping (#863)
* Fix keyframes not being sorted, causing missed frames (#842)