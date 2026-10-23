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
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import techguns.TGArmors;
import techguns.TGuns;
import techguns.Techguns;

/**
 * 1.12 height 1.95: EntityType size
 */
public class SkeletonSoldier extends GenericNPCUndead {

	public static final ResourceLocation LOOT = new ResourceLocation(Techguns.MODID, "entities/skeletonsoldier");

	public SkeletonSoldier(EntityType<? extends SkeletonSoldier> type, Level world) {
		super(type, world);
		setTGArmorStats(0f, 0f);
	}

	public static AttributeSupplier.Builder createAttributes() {
		return createAttributes(0.25D, 25, 4, 50.0D);
	}

	@Override
	public float getWeaponPosX() {
		return -0.06f;
	}

	@Override
	public float getWeaponPosY() {
		return -0.06f;
	}

	@Override
	protected void addRandomArmor(int difficulty) {

		// Armors

		double chance = 0.5;
		if (Math.random() <= chance) {
			this.setItemSlot(EquipmentSlot.HEAD, new ItemStack(TGArmors.t1_scout_Helmet));
		} else {
			this.setItemSlot(EquipmentSlot.HEAD, new ItemStack(TGArmors.t1_combat_Helmet));
		}
		if (Math.random() <= chance) {
			this.setItemSlot(EquipmentSlot.FEET, new ItemStack(TGArmors.t1_scout_Boots));
		} else {
			this.setItemSlot(EquipmentSlot.FEET, new ItemStack(TGArmors.t1_combat_Boots));
		}

		// Weapons
		Random r = new Random();
		Item weapon = null;
		switch (r.nextInt(3)) {
		case 0:
			weapon = TGuns.revolver;
			break;
		case 1:
			weapon = TGuns.thompson;
			break;
		case 2:
			weapon = TGuns.handcannon;
			break;
		default:
			weapon = Items.STONE_SHOVEL;
			break;
		}
		if (weapon != null) this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(weapon));
	}

	@Override
	public SoundEvent getAmbientSound() {
		return SoundEvents.SKELETON_AMBIENT;
	}

	@Override
	public SoundEvent getHurtSound(DamageSource damageSourceIn) {
		return SoundEvents.SKELETON_HURT;
	}

	@Override
	public SoundEvent getDeathSound() {
		return SoundEvents.SKELETON_DEATH;
	}

	public SoundEvent getStepSound() {
		return SoundEvents.SKELETON_STEP;
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
