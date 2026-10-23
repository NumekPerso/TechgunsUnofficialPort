package techguns.entities.npcs;

import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import techguns.TGArmors;
import techguns.TGuns;
import techguns.Techguns;
import techguns.items.armors.GenericArmorMultiCamo;

/**
 * Fire immunity: EntityType builder (fireImmune). 1.12 zombie pigman sounds = 1.20 zombified piglin sounds.
 */
public class ZombiePigmanSoldier extends GenericNPCUndead {

	public static final ResourceLocation LOOT = new ResourceLocation(Techguns.MODID, "entities/zombiepigmansoldier");

	public ZombiePigmanSoldier(EntityType<? extends ZombiePigmanSoldier> type, Level world) {
		super(type, world);
		setTGArmorStats(10.0f, 0f);
	}

	public static AttributeSupplier.Builder createAttributes() {
		return createAttributes(0.30D, 25, 5, 50.0D);
	}

	@Override
	protected void addRandomArmor(int difficulty) {

		// Armors
		double chance = 0.5;
		int camo = 3;
		//1.12: the helmet is always set (missing braces after the if)
		this.setItemSlot(EquipmentSlot.HEAD, GenericArmorMultiCamo.getNewWithCamo(TGArmors.t2_combat_Helmet, camo));
		if (Math.random() <= chance)
			this.setItemSlot(EquipmentSlot.CHEST, GenericArmorMultiCamo.getNewWithCamo(TGArmors.t2_combat_Chestplate, camo));
		if (Math.random() <= chance)
			this.setItemSlot(EquipmentSlot.LEGS, GenericArmorMultiCamo.getNewWithCamo(TGArmors.t2_combat_Leggings, camo));
		if (Math.random() <= chance)
			this.setItemSlot(EquipmentSlot.FEET, GenericArmorMultiCamo.getNewWithCamo(TGArmors.t2_combat_Boots, camo));

		// Weapons
		Random r = new Random();
		Item weapon = null;
		int bound = 9;

		switch (r.nextInt(bound)) {
		case 0:
		case 1:
		case 2:
			weapon = TGuns.thompson;
			break;
		case 3:
		case 4:
			weapon = TGuns.revolver;
			break;
		case 5:
		case 6:
			weapon = TGuns.ak47;
			break;
		case 7:
		case 8:
			weapon = TGuns.pistol;
			break;
		case 9:
		case 10:
			weapon = TGuns.flamethrower;
			break;
		case 11:
		case 12:
			weapon = TGuns.combatshotgun;
			break;
		case 13:
			weapon = TGuns.lmg;
			break;
		case 14:
		default:
			weapon = TGuns.rocketlauncher;
			break;
		}
		if (weapon != null) this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(weapon));
	}

	@Override
	protected boolean shouldBurnInDay() {
		return false;
	}

	@Override
	public SoundEvent getAmbientSound() {
		return SoundEvents.ZOMBIFIED_PIGLIN_AMBIENT;
	}

	@Override
	public SoundEvent getHurtSound(DamageSource damageSourceIn) {
		return SoundEvents.ZOMBIFIED_PIGLIN_HURT;
	}

	@Override
	public SoundEvent getDeathSound() {
		return SoundEvents.ZOMBIFIED_PIGLIN_DEATH;
	}

	public SoundEvent getStepSound() {
		return SoundEvents.ZOMBIE_STEP;
	}

	@Override
	protected void playStepSound(BlockPos pos, BlockState blockIn) {
		this.playSound(this.getStepSound(), 0.15F, 1.0F);
	}

	@Override
	protected ResourceLocation getDefaultLootTable() {
		return LOOT;
	}
}
