package software.bernie.geckolib.listener;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import software.bernie.geckolib.GeckoLib;
import software.bernie.geckolib.example.entity.RobotEntity;
import software.bernie.geckolib.example.registry.Entities;
import software.bernie.geckolib.registry.CommandRegistry;

@Mod.EventBusSubscriber(modid = GeckoLib.ModID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ServerListener
{
	@SubscribeEvent
	public static void onServerStartingEvent(RegisterCommandsEvent event)
	{
		CommandRegistry.registerCommands(event.getDispatcher());
	}

	public static void onSetup(FMLCommonSetupEvent event)
	{
		for(RegistryObject<EntityType<?>> entity : Entities.ENTITIES.getEntries())
		{
			GlobalEntityTypeAttributes.put((EntityType<? extends LivingEntity>) entity.get(), getAttributes().func_233813_a_());
		}
	}

	public static AttributeModifierMap.MutableAttribute getAttributes()
	{
		AttributeModifierMap.MutableAttribute map = AttributeModifierMap.func_233803_a_();
		map.func_233814_a_(Attributes.field_233818_a_);
		map.func_233814_a_(Attributes.field_233819_b_);
		map.func_233814_a_(Attributes.field_233820_c_);
		map.func_233814_a_(Attributes.field_233821_d_);
		map.func_233814_a_(Attributes.field_233822_e_);
		map.func_233814_a_(Attributes.field_233823_f_);
		map.func_233814_a_(Attributes.field_233824_g_);
		map.func_233814_a_(Attributes.field_233825_h_);
		map.func_233814_a_(Attributes.field_233826_i_);
		map.func_233814_a_(Attributes.field_233827_j_);
		map.func_233814_a_(Attributes.field_233828_k_);
		map.func_233814_a_(Attributes.field_233829_l_);
		map.func_233814_a_(Attributes.field_233830_m_);

		map.func_233814_a_(ForgeMod.ENTITY_GRAVITY.get());
		map.func_233814_a_(ForgeMod.SWIM_SPEED.get());
		map.func_233814_a_(ForgeMod.REACH_DISTANCE.get());
		map.func_233814_a_(ForgeMod.NAMETAG_DISTANCE.get());


		return map;
	}
}
