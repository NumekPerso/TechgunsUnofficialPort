package techguns.radiation;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import techguns.TGPackets;
import techguns.TGRadiationSystem;
import techguns.Techguns;
import techguns.capabilities.TGExtendedPlayer;
import techguns.damagesystem.TGDamageSource;
import techguns.deatheffects.EntityDeathUtils.DeathType;
import techguns.gui.player.TGPlayerInventory;
import techguns.items.additionalslots.ItemGasMask;

/**
 * Radiation parts of the 1.12 TGTickHandler (radioactive items in the inventory, radiation sickness, face slot radiation
 * resistance) and TGEventHandler (RAD lost on death). The port also syncs the RAD level to the client from here.
 */
@Mod.EventBusSubscriber(modid = Techguns.MODID)
public class RadiationEventHandler {

	@SubscribeEvent
	public static void onPlayerTick(PlayerTickEvent event) {
		Player ply = event.player;
		if (ply.level().isClientSide) {
			return;
		}
		TGExtendedPlayer props = TGExtendedPlayer.get(ply);
		if (props == null) {
			return;
		}

		if (event.phase == TickEvent.Phase.START) {
			updateFaceSlotRadResistance(ply, props);
			return;
		}

		long time = ply.level().getGameTime();

		//check inventory for radioactive items
		if (TGRadiationSystem.isEnabled() && time % TGRadiationSystem.INVENTORY_RADIATION_INVERVALL == 0) {
			int maxrad = 0;
			for (ItemStack stack : ply.getInventory().items) {
				if (!stack.isEmpty()) {
					ItemRadiationData data = ItemRadiationRegistry.getRadiationDataFor(stack);
					if (data != null) {
						if (data.radamount > maxrad) {
							maxrad = data.radamount;
						}
					}
				}
			}

			if (maxrad > 0) {
				ply.addEffect(new MobEffectInstance(TGRadiationSystem.radiation_effect, TGRadiationSystem.INVENTORY_RADIATION_INVERVALL, maxrad - 1, false, false));
			}
		}

		//TICK Radiation
		if (time % 20 == 0) {
			if (props.radlevel >= TGRadiationSystem.MINOR_POISONING && props.radlevel < TGRadiationSystem.SEVERE_POISONING) {
				ply.addEffect(new MobEffectInstance(MobEffects.HUNGER, 30, 0, false, false));

			} else if (props.radlevel >= TGRadiationSystem.SEVERE_POISONING) {
				ply.addEffect(new MobEffectInstance(MobEffects.HUNGER, 30, 1, false, false));
				ply.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 30, 1, false, false));
				ply.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 30, 1, false, false));
			}

			if (props.radlevel >= TGRadiationSystem.LETHAL_POISONING) {
				ply.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 30, 1, false, false));
				ply.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 30, 1, false, false));
				ply.hurt(TGDamageSource.causeLethalRadPoisoningDamage(null, null, DeathType.LASER), 2f);
			}
		}

		//sync the RAD level to the client (1.12 computed it on both sides)
		if (ply instanceof ServerPlayer sply && props.radlevel != props.lastSyncedRadlevel) {
			props.lastSyncedRadlevel = props.radlevel;
			TGPackets.network.sendTo(new PacketRadiationSync(props.radlevel), sply);
		}
	}

	/**
	 * 1.12: TGTickHandler removed the face slot modifier each tick, ItemGasMask.onPlayerTick added it again.
	 * 1.20 attribute instances reject duplicate modifiers, so it is added / removed here when the face slot changes.
	 */
	protected static void updateFaceSlotRadResistance(Player ply, TGExtendedPlayer props) {
		AttributeInstance attributeRadresistance = ply.getAttribute(TGRadiationSystem.RADIATION_RESISTANCE);
		if (attributeRadresistance == null) {
			return;
		}
		ItemStack face = props.tg_inventory.inventory.get(TGPlayerInventory.SLOT_FACE);
		float radresist = 0f;
		if (!face.isEmpty() && face.getItem() instanceof ItemGasMask mask) {
			radresist = mask.getRadresist();
		}

		AttributeModifier current = attributeRadresistance.getModifier(ItemGasMask.UUID_RAD_RESIST_FACE);
		if (current != null && current.getAmount() != radresist) {
			attributeRadresistance.removeModifier(ItemGasMask.UUID_RAD_RESIST_FACE);
			current = null;
		}
		if (current == null && radresist != 0f) {
			attributeRadresistance.addTransientModifier(new AttributeModifier(ItemGasMask.UUID_RAD_RESIST_FACE, "techguns.radresist.faceslot", radresist, AttributeModifier.Operation.ADDITION));
		}
	}

	@SubscribeEvent
	public static void onLivingDeath(LivingDeathEvent event) {
		if (!event.getEntity().level().isClientSide && event.getEntity() instanceof Player ply) {
			TGExtendedPlayer tgplayer = TGExtendedPlayer.get(ply);
			if (tgplayer != null) {
				tgplayer.addRadiation(-TGRadiationSystem.RADLOST_ON_DEATH);
			}
		}
	}
}
