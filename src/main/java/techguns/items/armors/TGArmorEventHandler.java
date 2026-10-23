package techguns.items.armors;

import java.util.UUID;

import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.ShieldBlockEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import techguns.TGPackets;
import techguns.TGSounds;
import techguns.Techguns;
import techguns.capabilities.TGExtendedPlayer;
import techguns.damagesystem.ShieldStats;
import techguns.damagesystem.TGDamageSource;
import techguns.packets.PacketPlaySound;
import techguns.util.TGSoundCategory;

/**
 * Armor related event handling, port of:
 * <ul>
 * <li>Forge 1.12 ISpecialArmor (GenericArmor.getProperties / damageArmor) -> {@link #onLivingHurt}</li>
 * <li>the shield part of the 1.12 DamageSystem.attackEntityFrom -> {@link #onShieldBlock}</li>
 * <li>the armor part of 1.12 TGTickHandler.onPlayerTick -> {@link #onPlayerTick}</li>
 * <li>the armor part of 1.12 TGEventHandler (jump, fall, break speed, cooling system)</li>
 * </ul>
 *
 * <b>Damage reduction / armor bar:</b> TG armor pieces put their 1.12 display value (rounded PHYSICAL armor) in the vanilla ARMOR
 * attribute, so the vanilla armor bar (and everything else reading the armor attribute) shows the same value as 1.12 getArmorDisplay.
 * In LivingHurtEvent the 1.12 ISpecialArmor calculation is done (per piece absorb ratio with the TG formula against the
 * Techguns damage type of the source, durability damage with damageArmor). Then, for the vanilla armor calculation that follows
 * LivingHurtEvent (LivingEntity.getDamageAfterArmorAbsorb), a transient ARMOR modifier removes the TG part of the armor attribute,
 * so the vanilla formula only uses the armor of vanilla pieces and the toughness of all pieces, exactly like 1.12 where the vanilla
 * armor of TG pieces was 0. The modifier is removed again in LivingDamageEvent (fired right after the armor calculation).
 */
@Mod.EventBusSubscriber(modid = Techguns.MODID)
public class TGArmorEventHandler {

	private static final UUID UUID_SPEED = UUID.fromString("5D8E53EB-DCFA-4121-B4DB-99BCAFA6B70B");
	private static final UUID UUID_HEALTH = UUID.fromString("4CFA49EB-D215-498B-9CC9-4BD0D1350B1F");
	private static final UUID UUID_KNOCKBACK_RESISTANCE = UUID.fromString("3441FC5D-F0B6-47F4-AFBB-DC5005670254");

	/**
	 * Transient modifier removing the armor of TG pieces during the vanilla armor calculation
	 */
	private static final UUID UUID_TG_ARMOR_COMPENSATION = UUID.fromString("B7C2D5F1-6A1E-4C55-9D2B-7E3A1F0C4D21");

	private static final EquipmentSlot[] ARMOR_SLOTS = { EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD };

	private static final float DEFAULT_FLYSPEED = 0.05f;

	/*
	 * ---------------- DAMAGE REDUCTION ----------------
	 */

	/**
	 * Safety: remove a compensation modifier left over from an interrupted damage calculation
	 */
	@SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
	public static void onLivingHurtFirst(LivingHurtEvent event) {
		removeArmorCompensation(event.getEntity());
	}

	@SubscribeEvent(priority = EventPriority.LOW)
	public static void onLivingHurt(LivingHurtEvent event) {
		if (event.isCanceled()) {
			return;
		}
		LivingEntity ent = event.getEntity();
		DamageSource source = event.getSource();
		//1.12 ISpecialArmor.handleUnblockableDamage was false: TG armor doesn't protect against damage bypassing armor
		if (source.is(DamageTypeTags.BYPASSES_ARMOR)) {
			return;
		}
		//lot J: Techguns NPCs use their own armor calculation (1.12 TGEventHandler.onLivingHurt replaced ISpecialArmor for them)
		if (ent instanceof techguns.api.npc.INpcTGDamageSystem) {
			return;
		}

		ItemStack[] stacks = new ItemStack[4];
		double[] ratios = new double[4];
		boolean hasTGArmor = false;
		for (int i = 0; i < 4; i++) {
			ItemStack stack = ent.getItemBySlot(ARMOR_SLOTS[i]);
			if (GenericArmor.isTechgunArmor(stack)) {
				stacks[i] = stack;
				hasTGArmor = true;
			}
		}
		if (!hasTGArmor) {
			return;
		}

		TGDamageSource src = TGDamageSource.getFromGenericDamageSource(source);
		double damage = event.getAmount();

		double totalRatio = 0;
		int displayedArmor = 0;
		for (int i = 0; i < 4; i++) {
			if (stacks[i] != null) {
				GenericArmor armor = (GenericArmor) stacks[i].getItem();
				ratios[i] = Math.max(0.0, armor.getAbsorbRatio(ent, stacks[i], src));
				totalRatio += ratios[i];
				displayedArmor += armor.getArmorDisplay(stacks[i]);
			}
		}

		//1.12 ISpecialArmor.StandardizeList: the ratios of one priority can't absorb more than the damage
		if (totalRatio > 1.0) {
			for (int i = 0; i < 4; i++) {
				ratios[i] /= totalRatio;
			}
			totalRatio = 1.0;
		}

		for (int i = 0; i < 4; i++) {
			if (stacks[i] != null) {
				double absorb = damage * ratios[i];
				if (absorb > 0) {
					int itemDamage = (int) Math.max(1, absorb);
					((GenericArmor) stacks[i].getItem()).damageArmor(ent, stacks[i], itemDamage);
				}
			}
		}

		damage -= damage * totalRatio;
		event.setAmount((float) damage);

		if (damage > 0 && displayedArmor > 0) {
			addArmorCompensation(ent, displayedArmor);
		}
	}

	/**
	 * Fired after the vanilla armor calculation
	 */
	@SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
	public static void onLivingDamage(LivingDamageEvent event) {
		removeArmorCompensation(event.getEntity());
	}

	@SubscribeEvent
	public static void onLivingTick(LivingEvent.LivingTickEvent event) {
		removeArmorCompensation(event.getEntity());
	}

	protected static void addArmorCompensation(LivingEntity ent, double tgArmor) {
		AttributeInstance armorAttr = ent.getAttribute(Attributes.ARMOR);
		if (armorAttr != null) {
			armorAttr.removeModifier(UUID_TG_ARMOR_COMPENSATION);
			armorAttr.addTransientModifier(new AttributeModifier(UUID_TG_ARMOR_COMPENSATION, "techguns_armor_compensation", -tgArmor, AttributeModifier.Operation.ADDITION));
		}
	}

	protected static void removeArmorCompensation(LivingEntity ent) {
		AttributeInstance armorAttr = ent.getAttribute(Attributes.ARMOR);
		if (armorAttr != null && armorAttr.getModifier(UUID_TG_ARMOR_COMPENSATION) != null) {
			armorAttr.removeModifier(UUID_TG_ARMOR_COMPENSATION);
		}
	}

	/*
	 * ---------------- SHIELDS ----------------
	 */

	/**
	 * Techguns damage sources: the blocked damage depends on the shield and the damage type (1.12 DamageSystem.calculateShieldDamage),
	 * other damage sources are fully blocked like in vanilla.
	 */
	@SubscribeEvent
	public static void onShieldBlock(ShieldBlockEvent event) {
		if (event.getDamageSource() instanceof TGDamageSource src) {
			LivingEntity ent = event.getEntity();
			ShieldStats s = ShieldStats.getStats(ent.getUseItem(), ent);
			if (s != null) {
				float amount = event.getOriginalBlockedDamage();
				float through = s.getAmount(amount, src);
				event.setBlockedDamage(Math.max(0f, amount - through));
			}
			ShieldStats.playBlockSound(ent, src);
		}
	}

	/*
	 * ---------------- ARMOR BONI ----------------
	 */

	@SubscribeEvent
	public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
		if (event.phase != TickEvent.Phase.END) {
			return;
		}
		Player ply = event.player;
		TGExtendedPlayer props = TGExtendedPlayer.get(ply);
		if (props == null) {
			return;
		}
		boolean client = ply.level().isClientSide;

		boolean wearingTechgunsArmor = false;
		for (int i = 0; i < 4; i++) {
			ItemStack istack = ply.getInventory().armor.get(i);
			if (GenericArmor.isTechgunArmor(istack)) {
				wearingTechgunsArmor = true;
				break;
			}
		}

		float speed = 0.0f;
		float healthBonus = 0.0f;
		float knockbackresistance = 0.0f;
		if (wearingTechgunsArmor) {

			PoweredArmor.calculateConsumptionTick(ply);

			if (ply.isOnFire()) {
				float cooling = GenericArmor.getArmorBonusForPlayer(ply, TGArmorBonus.COOLING_SYSTEM, false);
				if (cooling >= 1.0f) {
					ply.clearFire();
				}
			}

			//Check for stats
			speed = GenericArmor.getArmorBonusForPlayer(ply, TGArmorBonus.SPEED, false);

			if (ply.isEyeInFluid(FluidTags.WATER) || ply.isEyeInFluid(FluidTags.LAVA)) {
				speed += GenericArmor.getArmorBonusForPlayer(ply, TGArmorBonus.SPEED_WATER, false);
			}

			if (speed > 0.0f && ply.isSprinting()) {
				speed = speed * 2.0f;
			}

			healthBonus = GenericArmor.getArmorBonusForPlayer(ply, TGArmorBonus.EXTRA_HEART, false);
			knockbackresistance = GenericArmor.getArmorBonusForPlayer(ply, TGArmorBonus.KNOCKBACK_RESISTANCE, false);
		}
		//1.12 removed and added the modifiers every tick, only update them when the value changes (no attribute sync every tick)
		updateModifier(ply.getAttribute(Attributes.MOVEMENT_SPEED), UUID_SPEED, "TechgunsSpeedboost", speed, AttributeModifier.Operation.MULTIPLY_TOTAL);
		updateModifier(ply.getAttribute(Attributes.MAX_HEALTH), UUID_HEALTH, "TechgunsHealthbonus", healthBonus, AttributeModifier.Operation.ADDITION);
		updateModifier(ply.getAttribute(Attributes.KNOCKBACK_RESISTANCE), UUID_KNOCKBACK_RESISTANCE, "TechgunsKnockbackresistbonus", knockbackresistance, AttributeModifier.Operation.ADDITION);
		if (!client && ply.getHealth() > ply.getMaxHealth()) {
			ply.setHealth(ply.getMaxHealth());
		}

		//Step height is client only
		if (client) {
			//ONLY DO FOR OWN PLAYER!
			if (ply == Techguns.proxy.getPlayerClient()) {
				float stepassist = GenericArmor.getArmorBonusForPlayer(ply, TGArmorBonus.STEPASSIST, false);

				Techguns.proxy.setHasStepassist(stepassist > 0.0f);

				if (props.enableStepAssist && (stepassist > 0)) {
					if (ply.maxUpStep() < 1.1f) {
						ply.setMaxUpStep(1.1f);
					}
				} else {
					if (ply.maxUpStep() > 0.6f) {
						ply.setMaxUpStep(0.6f);
					}
				}
			}
		}

		boolean enabled = props.enableNightVision;
		float nightvision = GenericArmor.getArmorBonusForPlayer(ply, TGArmorBonus.NIGHTVISION, enabled);
		if (client && ply == Techguns.proxy.getPlayerClient()) {
			Techguns.proxy.setHasNightvision(nightvision > 0.0f);
		}
		if (nightvision > 0.0f && enabled) {
			if (!client) {
				ply.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 220, 0, false, false));
			}
		}

		//flying
		float flightbonus = GenericArmor.getArmorBonusForPlayer(ply, TGArmorBonus.CREATIVE_FLIGHT, false);
		if (flightbonus > 0) {
			ply.getAbilities().mayfly = true;
			props.gotCreativeFlightLastTick = true;

			if (ply.getAbilities().flying) {
				if (!props.wasFlying) {
					if (client) {
						Techguns.proxy.createFXOnEntity("AntiGravRing", ply);
					} else {
						TGPackets.network.sendToAllAround(new PacketPlaySound(TGSounds.ANTI_GRAV_START, ply, 1.0f, 1.0f, false, true, false, true, TGSoundCategory.PLAYER_EFFECT), TGPackets.targetPointAroundEnt(ply, 50.0f));
					}
				}
				props.wasFlying = true;
			} else {
				props.wasFlying = false;
			}

		} else {
			if (props.gotCreativeFlightLastTick && !ply.getAbilities().instabuild && !ply.isSpectator()) {
				ply.getAbilities().mayfly = false;
				ply.getAbilities().flying = false;
			}
			props.gotCreativeFlightLastTick = false;
			props.wasFlying = false;
		}

		//Fly speed is client only
		if (client) {
			//ONLY DO FOR OWN PLAYER!
			if (ply == Techguns.proxy.getPlayerClient()) {
				float flyspeedBonus = GenericArmor.getArmorBonusForPlayer(ply, TGArmorBonus.FLYSPEED, false);
				if (flyspeedBonus > 0) {
					if (props.enableJetpack) {
						Techguns.proxy.setFlySpeed((1.0f + flyspeedBonus) * DEFAULT_FLYSPEED);
					} else {
						Techguns.proxy.setFlySpeed(DEFAULT_FLYSPEED);
					}
				} else {
					if (props.gotCreativeFlightLastTick) {
						Techguns.proxy.setFlySpeed(DEFAULT_FLYSPEED);
					}
				}
			}
		}
		//OXYGEN_GEAR: used by the scuba tanks (additional slot item) to refill the air, see ItemScubaTanks
	}

	protected static void updateModifier(AttributeInstance attr, UUID uuid, String name, double value, AttributeModifier.Operation op) {
		if (attr == null) {
			return;
		}
		AttributeModifier mod = attr.getModifier(uuid);
		if (value > 0.0) {
			if (mod != null) {
				if (mod.getAmount() == value && mod.getOperation() == op) {
					return;
				}
				attr.removeModifier(uuid);
			}
			attr.addTransientModifier(new AttributeModifier(uuid, name, value, op));
		} else if (mod != null) {
			attr.removeModifier(uuid);
		}
	}

	/*
	 * ---------------- 1.12 TGEventHandler ----------------
	 */

	/**
	 * Cooling system: immunity against fire damage
	 */
	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void onLivingAttack(LivingAttackEvent event) {
		DamageSource src = event.getSource();
		if ((src.is(DamageTypes.LAVA) || src.is(DamageTypes.ON_FIRE) || src.is(DamageTypes.IN_FIRE)) && event.getEntity() instanceof Player ply) {
			float bonus = GenericArmor.getArmorBonusForPlayer(ply, TGArmorBonus.COOLING_SYSTEM, ply.level().getGameTime() % 5 == 0);

			if (bonus >= 1.0f) {
				event.setCanceled(true);
			}
		}
	}

	@SubscribeEvent
	public static void onLivingJumpEvent(LivingEvent.LivingJumpEvent event) {
		if (event.getEntity() instanceof Player ply) {
			float jumpbonus = GenericArmor.getArmorBonusForPlayer(ply, TGArmorBonus.JUMP, true);
			if (jumpbonus != 0f) {
				ply.setDeltaMovement(ply.getDeltaMovement().add(0, jumpbonus, 0));
			}
		}
	}

	@SubscribeEvent
	public static void onLivingFallEvent(LivingFallEvent event) {
		if (event.getEntity() instanceof Player ply) {
			boolean consume = event.getDistance() > 3.0f;

			float fallbonus = GenericArmor.getArmorBonusForPlayer(ply, TGArmorBonus.FALLDMG, consume);
			float reduction = (fallbonus < 1 ? 1 - fallbonus : 0.0f);
			float freeheight = GenericArmor.getArmorBonusForPlayer(ply, TGArmorBonus.FREEHEIGHT, false);
			if (freeheight < event.getDistance()) {
				event.setDistance(event.getDistance() - freeheight);
			} else {
				event.setDistance(0.0f);
			}

			event.setDistance(event.getDistance() * reduction);
		}
	}

	@SubscribeEvent
	public static void onBreakEvent(PlayerEvent.BreakSpeed event) {
		Player ply = event.getEntity();
		float bonus = 1.0f + GenericArmor.getArmorBonusForPlayer(ply, TGArmorBonus.BREAKSPEED, true);
		float waterbonus = 1.0f;
		if (ply.isEyeInFluid(FluidTags.WATER) || ply.isEyeInFluid(FluidTags.LAVA)) {
			waterbonus += GenericArmor.getArmorBonusForPlayer(ply, TGArmorBonus.BREAKSPEED_WATER, true);
		}

		event.setNewSpeed(event.getNewSpeed() * bonus * waterbonus);
	}
}
