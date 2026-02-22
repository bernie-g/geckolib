package com.geckolib.animatable.stateless;

import com.geckolib.animatable.GeoAnimatable;
import com.geckolib.animation.RawAnimation;

/// Extension of [StatelessAnimatable] for custom [GeoAnimatable]s
///
/// Implementation of [StatelessAnimatable#playAnimation(RawAnimation)] and [StatelessAnimatable#stopAnimation(String)] is left up
/// to the implementers of this interface, as well as any additional handling required
public non-sealed interface StatelessGeoObject extends StatelessAnimatable, GeoAnimatable {}
