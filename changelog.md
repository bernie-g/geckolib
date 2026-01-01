## GeckoLib v5.4.1

### Changes:
* Cleaned up some outdated tooltips
* Added some additional context to DeferredCache errors from `RenderPassInfo#addBoneUpdater`

### Bug Fixes:
* Fixed `AnimationController` sometimes thinking time has gone backwards (thanks Mojang?)
* Fixed `AnimationController` handling triggered animations in ticks instead of seconds
* Fixed keyframe markers not being triggered properly (or sometimes at all)
* Fixed in-code loop type usage not being respected