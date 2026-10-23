package techguns.radiation;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import techguns.TGRadiationSystem;
import techguns.Techguns;

/**
 * Mod bus events of the radiation system
 */
@Mod.EventBusSubscriber(modid = Techguns.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class RadiationModEvents {

	/**
	 * 1.12 EntityConstructing: every EntityLivingBase gets the radiation resistance attribute (base 0)
	 */
	@SubscribeEvent
	public static void onAttributeModification(EntityAttributeModificationEvent event) {
		for (EntityType<? extends LivingEntity> type : event.getTypes()) {
			if (!event.has(type, TGRadiationSystem.RADIATION_RESISTANCE)) {
				event.add(type, TGRadiationSystem.RADIATION_RESISTANCE);
			}
		}
	}

	@SubscribeEvent
	public static void onCommonSetup(FMLCommonSetupEvent event) {
		event.enqueueWork(TGRadiationSystem::postInit);
	}
}
