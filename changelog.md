## GeckoLib v5.4.6

### Bug Fixes
* Fixed ClientUtil#getCurrentTick returning bad values after a world has existed for a while
* Fixed query.time_of_day being wrong after the first day
* Fixed RenderUtil#translateMatrix incorrectly translating
* Fixed `RenderUtil#fixInvertedFlatCube` not being quite correct
* Fixed GeckoLib's deduplication on ID'd stacks not applying when drag-splitting a stack
* Fixed a bug causing animations to continue progressing even after telling it to stop
* Fixed a bug causing stopped animations to not restart when starting it again
* Fixed a missing animation file throwing an exception instead of logging an error
* Add caching to missing animation error messages to remove log/console spam
* Fixed an issue caused by bones over-transitioning when mid-transition
* Fixed some other animation transition issues

### Additions
* Add query shorthand support for the non-query query types (`v.`, `c.`, `t.`)