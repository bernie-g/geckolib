/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.geckolib;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = GeckoLib.ModID, name = GeckoLib.NAME, version = GeckoLib.VERSION)
public class GeckoLib
{
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String ModID = "geckolib";
    public static final String NAME = "GeckoLib";
    public static final String VERSION = "2.0.0";

    public GeckoLib()
    {
        MinecraftForge.EVENT_BUS.register(this);
    }
}