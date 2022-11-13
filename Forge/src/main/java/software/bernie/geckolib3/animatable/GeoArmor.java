package software.bernie.geckolib3.animatable;

import software.bernie.geckolib3.core.animatable.GeoAnimatable;

/**
 * The {@link GeoAnimatable} interface specific to {@link net.minecraft.world.item.ArmorItem Armor}.<br>
 * This is specifically for the rendering when worn. For their item rendering, see {@link GeoItem}
 * @see <a href="https://github.com/bernie-g/geckolib/wiki/Armor-Animations">GeckoLib Wiki - Armor Animations</a>
 */
public interface GeoArmor extends GeoItem {
	static final String ID_NBT_KEY = "GeckoLibID";
}
