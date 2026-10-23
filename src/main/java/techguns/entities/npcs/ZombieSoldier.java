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

public class ZombieSoldier extends GenericNPCUndead {

	public static final ResourceLocation LOOT = new ResourceLocation(Techguns.MODID, "entities/zombiesoldier");

	public ZombieSoldier(EntityType<? extends ZombieSoldier> type, Level world) {
		super(type, world);
		setTGArmorStats(5.0f, 0f);
	}

	public static AttributeSupplier.Builder createAttributes() {
		return createAttributes(0.25D, 25, 4, 50.0D);
	}

	@Override
	protected void addRandomArmor(int difficulty) {

		// Armors
		double chance = 0.5;
		if (Math.random() <= chance)
			this.setItemSlot(EquipmentSlot.HEAD, new ItemStack(TGArmors.t1_combat_Helmet));
		if (Math.random() <= chance)
			this.setItemSlot(EquipmentSlot.CHEST, new ItemStack(TGArmors.t1_combat_Chestplate));
		if (Math.random() <= chance)
			this.setItemSlot(EquipmentSlot.LEGS, new ItemStack(TGArmors.t1_combat_Leggings));
		if (Math.random() <= chance)
			this.setItemSlot(EquipmentSlot.FEET, new ItemStack(TGArmors.t1_combat_Boots));

		// Weapons
		Random r = new Random();
		Item weapon = null;
		switch (r.nextInt(4)) {
		case 0:
			weapon = TGuns.revolver;
			break;
		case 1:
			weapon = TGuns.thompson;
			break;
		case 2:
			weapon = Items.IRON_SHOVEL;
			break;
		case 3:
		default:
			weapon = Items.STONE_SHOVEL;
			break;
		}
		if (weapon != null) this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(weapon));
	}

	@Override
	public SoundEvent getAmbientSound() {
		return SoundEvents.ZOMBIE_AMBIENT;
	}

	@Override
	public SoundEvent getHurtSound(DamageSource damageSourceIn) {
		return SoundEvents.ZOMBIE_HURT;
	}

	@Override
	public SoundEvent getDeathSound() {
		return SoundEvents.ZOMBIE_DEATH;
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

	@Override
	protected boolean shouldBurnInDay() {
		return false;
	}
}
