package techguns.entities.npcs;

import java.util.Random;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import techguns.TGArmors;
import techguns.TGuns;
import techguns.Techguns;
import techguns.items.armors.GenericArmorMultiCamo;

public class Outcast extends GenericNPCGearSpecificStats {

	public static final ResourceLocation LOOT = new ResourceLocation(Techguns.MODID, "entities/outcast");

	public Outcast(EntityType<? extends Outcast> type, Level world) {
		super(type, world);
	}

	public static AttributeSupplier.Builder createAttributes() {
		return createAttributes(0.2D, 30, 8, 60.0D);
	}

	@Override
	protected void addRandomArmor(int difficulty) {

		Random r = new Random();
		// Armors

		int camo = r.nextInt(2);
		this.setItemSlot(EquipmentSlot.HEAD, GenericArmorMultiCamo.getNewWithCamo(TGArmors.t3_power_Helmet, camo + 1));
		camo = r.nextInt(2);
		this.setItemSlot(EquipmentSlot.CHEST, GenericArmorMultiCamo.getNewWithCamo(TGArmors.t3_power_Chestplate, camo + 1));
		camo = r.nextInt(2);
		this.setItemSlot(EquipmentSlot.LEGS, GenericArmorMultiCamo.getNewWithCamo(TGArmors.t3_power_Leggings, camo + 1));
		camo = r.nextInt(2);
		this.setItemSlot(EquipmentSlot.FEET, GenericArmorMultiCamo.getNewWithCamo(TGArmors.t3_power_Boots, camo + 1));

		// Weapons

		Item weapon = null;
		switch (r.nextInt(7)) {
		case 0:
			weapon = TGuns.lasergun;
			break;
		case 1:
			weapon = TGuns.grimreaper;
			break;
		case 2:
			weapon = TGuns.minigun;
			break;
		case 3:
			weapon = TGuns.biogun;
			break;
		case 4:
			weapon = TGuns.flamethrower;
			break;
		case 5:
			weapon = TGuns.teslagun;
			break;
		case 6:
			weapon = TGuns.lmg;
			break;
		default:
			weapon = TGuns.lasergun;
			break;
		}
		if (weapon != null) this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(weapon));
	}

	@Override
	protected ResourceLocation getDefaultLootTable() {
		return LOOT;
	}
}
