/*
 * Copyright (c) 2020.
 * Author: Bernie G. (Gecko)
 */

package software.bernie.geckolib;


import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.bernie.geckolib.example.registry.Entities;
import software.bernie.geckolib.listener.ServerListener;
import software.bernie.geckolib.registry.CommandRegistry;

public class GeckoLib implements ModInitializer
{
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String ModID = "geckolib";


    @Override
    public void onInitialize()
    {
        CommandRegistrationCallback.EVENT.register(ServerListener::onServerStartingEvent);
    }
}
