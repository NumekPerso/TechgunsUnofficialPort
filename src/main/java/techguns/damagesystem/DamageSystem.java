package techguns.damagesystem;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.ServerLifecycleHooks;
import techguns.TGConfig;
import techguns.Techguns;
import techguns.api.damagesystem.DamageType;
import techguns.items.armors.GenericArmor;
import techguns.util.MathUtil;

/**
 * The 1.12 version replaced EntityLivingBase.attackEntityFrom with a reflection based copy. In 1.20 the same behaviour
 * is achieved with events around LivingEntity.hurt():
 * <ul>
 * <li>ignoreHurtresistTime: the hurt cooldown is cleared before vanilla checks it and restored afterwards</li>
 * <li>knockbackMultiplier: applied in LivingKnockBackEvent</li>
 * <li>wasSuccessful(): set when the damage reaches LivingHurtEvent with an amount &gt; 0</li>
 * </ul>
 */
@Mod.EventBusSubscriber(modid = Techguns.MODID)
public class DamageSystem {

	/**
	 * The TG damage source currently processed by LivingEntity.hurt, used for knockback
	 */
	protected static TGDamageSource currentSource = null;
	protected static LivingEntity currentTarget = null;

	public static float getDamageFactor(LivingEntity attacker, LivingEntity target) {
		if (attacker instanceof Player && target instanceof Player) {
			MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
			if (server != null && server.isPvpAllowed()) {
				return TGConfig.damagePvP;
			} else {
				return 0.0f;
			}
		} else if (target instanceof Player) {
			if (attacker instanceof techguns.entities.npcs.NPCTurret) {
				return TGConfig.damageTurretToPlayer;
			}
			return TGConfig.damageFactorNPC;
		} else if (attacker instanceof Player) {
			return 1.0f;
		}
		return TGConfig.damageFactorNPC;
	}

	public static float getTotalArmorAgainstType(Player ply, DamageType type) {
		float value = 0.0f;
		for (EquipmentSlot slot : new EquipmentSlot[] { EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET }) {
			ItemStack armor = ply.getItemBySlot(slot);
			if (!armor.isEmpty()) {
				Item item = armor.getItem();
				if (item instanceof GenericArmor) {
					value += ((GenericArmor) item).getArmorValue(armor, type);
				} else if (item instanceof ArmorItem) {
					if (type == DamageType.PHYSICAL) {
						value += ((ArmorItem) item).getDefense();
					}
				}
			}
		}
		return value;
	}

	/**
	 * Default behavior when unspecified
	 */
	public static float getArmorAgainstDamageTypeDefault(LivingEntity elb, float armor, DamageType damageType) {
		switch (damageType) {
		case PHYSICAL:
		case PROJECTILE:
			return armor;
		case EXPLOSION:
		case ENERGY:
		case ICE:
		case LIGHTNING:
		case DARK:
			return armor * 0.5f;
		case FIRE:
			if (elb.fireImmune()) {
				return armor * 2;
			} else {
				return armor * 0.5f;
			}
		case POISON:
		case RADIATION:
		case UNRESISTABLE:
		default:
			return 0;
		}
	}

	/**
	 * lot J: armor calculation of the Techguns NPCs (INpcTGDamageSystem), 1.12 ELB_applyArmorCalculations.
	 * Called from their LivingEntity.getDamageAfterArmorAbsorb override (replaces the vanilla armor formula).
	 */
	public static float getDamageAfterArmorAbsorbNPC(LivingEntity elb, DamageSource source, float damage) {
		if (!source.is(net.minecraft.tags.DamageTypeTags.BYPASSES_ARMOR) && elb instanceof techguns.api.npc.INpcTGDamageSystem tg) {
			TGDamageSource dmgsrc = TGDamageSource.getFromGenericDamageSource(source);
			float toughness = (float) elb.getAttributeValue(net.minecraft.world.entity.ai.attributes.Attributes.ARMOR_TOUGHNESS);
			damage = (float) getDamageAfterAbsorb_TGFormula(damage, tg.getTotalArmorAgainstType(dmgsrc), toughness, dmgsrc.armorPenetration * 4);
		}
		return damage;
	}

	/**
	 * based on old 1.7 damage formula
	 */
	public static double getDamageAfterAbsorb_TGFormula(float damage, float totalArmor, float toughnessAttribute, float penetration) {
		//use toughness to reduce penetration
		float pen = Math.max((penetration) - toughnessAttribute, 0);
		double armor = MathUtil.clamp(totalArmor - pen, 0.0, 24.0);
		return damage * (1.0 - armor / 25.0);
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onLivingAttack(LivingAttackEvent event) {
		if (event.isCanceled()) {
			return;
		}
		DamageSource source = event.getSource();
		LivingEntity ent = event.getEntity();
		if (source instanceof TGDamageSource src) {
			currentSource = src;
			currentTarget = ent;
			if (src.ignoreHurtresistTime && !ent.level().isClientSide) {
				src.cooldownEntity = ent;
				src.savedInvulnerableTime = ent.invulnerableTime;
				src.savedLastHurt = ent.lastHurt;
				ent.invulnerableTime = 0;
			}
		} else {
			currentSource = null;
			currentTarget = null;
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
	public static void onLivingHurtRestoreCooldown(LivingHurtEvent event) {
		if (event.getSource() instanceof TGDamageSource src && src.cooldownEntity == event.getEntity()) {
			LivingEntity ent = event.getEntity();
			ent.invulnerableTime = src.savedInvulnerableTime;
			ent.lastHurt = src.savedLastHurt;
			src.cooldownEntity = null;
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onLivingHurtSuccess(LivingHurtEvent event) {
		if (!event.isCanceled() && event.getAmount() > 0f && event.getSource() instanceof TGDamageSource src) {
			src.setAttackSuccessful();
		}
	}

	@SubscribeEvent
	public static void onKnockback(LivingKnockBackEvent event) {
		if (currentSource != null && currentTarget == event.getEntity()) {
			float mult = currentSource.knockbackMultiplier;
			if (mult <= 0f) {
				event.setCanceled(true);
			} else if (mult != 1.0f) {
				event.setStrength(event.getStrength() * mult);
			}
			currentSource = null;
			currentTarget = null;
		}
	}
}
