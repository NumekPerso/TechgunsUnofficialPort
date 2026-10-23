package techguns.entities.npcs;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import techguns.damagesystem.DamageSystem;
import techguns.damagesystem.TGDamageSource;
import techguns.items.armors.GenericArmor;

/**
 * NPC whose armor values come from the armor it wears
 */
public abstract class GenericNPCGearSpecificStats extends GenericNPC {

	public GenericNPCGearSpecificStats(EntityType<? extends GenericNPCGearSpecificStats> type, Level world) {
		super(type, world);
	}

	@Override
	public float getTotalArmorAgainstType(TGDamageSource dmgsrc) {
		Iterable<ItemStack> inv = this.getArmorSlots();
		float totalArmor = 0.0f;
		for (ItemStack stack : inv) {
			if (!stack.isEmpty() && stack.getItem() instanceof ArmorItem) {

				if (stack.getItem() instanceof GenericArmor) {
					GenericArmor genArmor = (GenericArmor) stack.getItem();
					float armorvalue = genArmor.getArmorValue(stack, dmgsrc.damageType);
					totalArmor += armorvalue;
				} else {
					ArmorItem armor = (ArmorItem) stack.getItem();
					float armorvalue = armor.getDefense();
					armorvalue = DamageSystem.getArmorAgainstDamageTypeDefault(this, armorvalue, dmgsrc.damageType);
					totalArmor += armorvalue;
				}
			}
		}

		return totalArmor;
	}

	@Override
	public float getPenetrationResistance(TGDamageSource dmgsrc) {
		Iterable<ItemStack> inv = this.getArmorSlots();
		float res = 0.0f;
		for (ItemStack stack : inv) {
			if (!stack.isEmpty() && stack.getItem() instanceof GenericArmor) {
				GenericArmor genArmor = (GenericArmor) stack.getItem();
				float antipen = genArmor.getPenetrationResistance();
				res += antipen;
			}
		}
		return res;
	}

	@Override
	public float getToughnessAfterPentration(LivingEntity elb, TGDamageSource src) {
		Iterable<ItemStack> inv = this.getArmorSlots();
		float t = 0.0f;
		for (ItemStack stack : inv) {
			if (!stack.isEmpty() && stack.getItem() instanceof GenericArmor) {
				GenericArmor genArmor = (GenericArmor) stack.getItem();
				float dt = genArmor.getTGMaterial().getTGToughness() - (Math.max(src.armorPenetration - genArmor.getPenetrationResistance(), 0f));
				t += Math.max(dt, 0f);
			} else if (!stack.isEmpty() && stack.getItem() instanceof ArmorItem) {
				ArmorItem a = (ArmorItem) stack.getItem();
				float dt = a.getToughness() - src.armorPenetration;
				t += Math.max(dt, 0f);
			}
		}
		return t;
	}

}
