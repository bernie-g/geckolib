## GeckoLib v5.5.7

### Changes
- Added `1.21.20` to the known geometry format versions
- Fixed the `relative_to` section in animations json throwing an error on parsing
- Fixed `AnimationController`'s easing override not being changeable mid-animation
- Fixed reverse-animations skipping keyframes
- Some animation performance improvements
- Animations no longer transition in when it's the first animation the controller ever plays
- Ensure loaded JSON files preserve their entry order for reliable molang operation
- Fixed animations with keyframes not at the start of the timeline sometimes animating incorrectly
- Fixed looping/held controllers still progressing even when the `PlayState` is `STOP`
- Fixed one more animation pass occurring when a controller is just stopped, even when there's no transition ticks
- Fixed stopped animations not restarting when starting up again
- Fixed some logical issues with additive animations and resets