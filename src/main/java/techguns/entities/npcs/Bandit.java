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

public class Bandit extends GenericNPC {

	public static final ResourceLocation LOOT = new ResourceLocation(Techguns.MODID, "entities/bandit");

	public Bandit(EntityType<? extends Bandit> type, Level world) {
		super(type, world);
		setTGArmorStats(5.0f, 0f);
	}

	public static AttributeSupplier.Builder createAttributes() {
		return createAttributes(0.30D, 20, 5, 40.0D);
	}

	@Override
	protected void addRandomArmor(int difficulty) {

		// Armors
		this.setItemSlot(EquipmentSlot.CHEST, new ItemStack(TGArmors.t1_scout_Chestplate));
		this.setItemSlot(EquipmentSlot.LEGS, new ItemStack(TGArmors.t1_scout_Leggings));
		this.setItemSlot(EquipmentSlot.FEET, new ItemStack(TGArmors.t1_scout_Boots));

		double chance = 0.5;
		if (Math.random() <= chance) {
			this.setItemSlot(EquipmentSlot.HEAD, new ItemStack(TGArmors.t1_scout_Helmet));
		}

		// Weapons
		Random r = new Random();
		Item weapon = null;
		switch (r.nextInt(6)) {
		case 0:
			weapon = TGuns.pistol;
			break;
		case 1:
			weapon = TGuns.ak47;
			break;
		case 2:
			weapon = TGuns.sawedoff;
			break;
		case 3:
			weapon = TGuns.thompson;
			break;
		case 4:
			weapon = TGuns.revolver;
			break;
		default:
			weapon = TGuns.boltaction;
			break;
		}
		if (weapon != null) this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(weapon));
	}

	@Override
	protected ResourceLocation getDefaultLootTable() {
		return LOOT;
	}
}
