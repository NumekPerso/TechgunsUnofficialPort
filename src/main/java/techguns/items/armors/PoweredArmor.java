package techguns.items.armors;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import techguns.TGSounds;
import techguns.Techguns;
import techguns.util.InventoryUtil;
import techguns.util.TextUtil;

/**
 * Port of the 1.12 PoweredArmor. Power is stored in the "power" NBT int of each piece. The chestplate is refilled by consuming a
 * battery item from the player inventory when it's empty, the other pieces take their power from the chestplate.
 */
public class PoweredArmor extends GenericArmorMultiCamo {

	protected ArmorPowerType powerType;
	@Nullable
	protected Item battery = null;
	@Nullable
	protected Item battery_empty = null;

	public int maxpower;

	/**
	 * position (x,z) of the players in the last consumption tick, used to detect movement
	 */
	protected static final Map<Player, double[]> LAST_POSITIONS = Collections.synchronizedMap(new WeakHashMap<>());
	/**
	 * Separate map for the client: the keys compare entity ids (Entity.equals), in singleplayer the client and server
	 * player have the same id and overwrote each other's position, the server saw no movement and consumed no power
	 */
	protected static final Map<Player, double[]> LAST_POSITIONS_CLIENT = Collections.synchronizedMap(new WeakHashMap<>());

	//
	protected float SpeedBonusUnpowered = 0.0f;
	protected float JumpBonusUnpowered = 0.0f;
	protected float FallDMGUnpowered = 0.0f;
	protected float FallFreeHeightUnpowered = 0.0f;
	protected float MiningSpeedBonusUnpowered = 0.0f;
	protected float WaterMiningBonusUnpowered = 0.0f;
	protected int armorValueUnpowered = 0;
	protected float GunAccuracyUnpowered = 0.0f;
	protected float extraHeartsUnpowered = 0.0f;
	protected float nightvisionUnpowered = 0.0f;
	protected float knockbackresistanceUnpowered = 0.0f;

	protected float stepassistUnpowerd = 0.0f;

	protected float oxygen_gearUnpowered = 0.0f;
	protected float water_electrolyzerUnpowered = 0.0f;
	protected float coolingsystemUnpowered = 0.0f;

	protected float waterspeedbonusUnpowered = 0.0f;

	public PoweredArmor(String unlocalizedName, TGArmorMaterial material, String[] textureNames, EquipmentSlot type, ArmorPowerType powerType, int maxpower) {
		this(Techguns.MODID, unlocalizedName, material, textureNames, type, powerType, maxpower);
	}

	public PoweredArmor(String modid, String unlocalizedName, TGArmorMaterial material, String[] textureNames, EquipmentSlot type, ArmorPowerType powerType, int maxpower) {
		super(modid, unlocalizedName, material, textureNames, type);
		this.powerType = powerType;
		this.maxpower = maxpower;
	}

	public PoweredArmor setBattery(Item battery) {
		this.battery = battery;
		return this;
	}

	public PoweredArmor setEmptyBattery(Item emptyBattery) {
		this.battery_empty = emptyBattery;
		return this;
	}

	public ItemStack getBattery() {
		return battery != null ? new ItemStack(battery) : ItemStack.EMPTY;
	}

	public ItemStack getBattery_empty() {
		return battery_empty != null ? new ItemStack(battery_empty) : ItemStack.EMPTY;
	}

	public ArmorPowerType getPowerType() {
		return powerType;
	}

	public float getBonusUnpowered(TGArmorBonus type) {
		switch (type) {
		case SPEED:
			return this.SpeedBonusUnpowered;
		case JUMP:
			return this.JumpBonusUnpowered;
		case FALLDMG:
			return this.FallDMGUnpowered;
		case FREEHEIGHT:
			return this.FallFreeHeightUnpowered;
		case BREAKSPEED:
			return this.MiningSpeedBonusUnpowered;
		case BREAKSPEED_WATER:
			return this.WaterMiningBonusUnpowered;
		case GUN_ACCURACY:
			return this.GunAccuracyUnpowered;
		case EXTRA_HEART:
			return this.extraHeartsUnpowered;
		case NIGHTVISION:
			return this.nightvisionUnpowered;
		case KNOCKBACK_RESISTANCE:
			return this.knockbackresistanceUnpowered;
		case STEPASSIST:
			return this.stepassistUnpowerd;
		case OXYGEN_GEAR:
			return this.oxygen_gearUnpowered;
		case WATER_ELECTROLYZER:
			return this.water_electrolyzerUnpowered;
		case COOLING_SYSTEM:
			return this.coolingsystemUnpowered;
		case SPEED_WATER:
			return this.waterspeedbonusUnpowered;
		case RAD_RESISTANCE:
			return this.radresistance;
		default:
			break;
		}
		return 0.0f;
	}

	public PoweredArmor setSpeedBoni(float speed, float jump, float speed_unpowered, float jump_unpowered) {
		this.SpeedBonus = speed;
		this.JumpBonus = jump;
		this.SpeedBonusUnpowered = speed_unpowered;
		this.JumpBonusUnpowered = jump_unpowered;
		return this;
	}

	public PoweredArmor setFallProtection(float multiplier, float freeheight, float multiplier_unpowered, float freeheight_unpowered) {
		this.FallDMG = multiplier;
		this.FallFreeHeight = freeheight;
		this.FallDMGUnpowered = multiplier_unpowered;
		this.FallFreeHeightUnpowered = freeheight_unpowered;
		return this;
	}

	public PoweredArmor setMiningBoni(float bonus, float bonus_unpowered) {
		this.MiningSpeedBonus = bonus;
		this.MiningSpeedBonusUnpowered = bonus_unpowered;
		return this;
	}

	public PoweredArmor setMiningBoniWater(float bonus, float bonus_unpowered) {
		this.WaterMiningBonus = bonus;
		this.WaterMiningBonusUnpowered = bonus_unpowered;
		return this;
	}

	public PoweredArmor setGunBonus(float acc, float acc_unpowered) {
		this.GunAccuracy = acc;
		this.GunAccuracyUnpowered = acc_unpowered;
		return this;
	}

	public PoweredArmor setHealthBonus(int bonusHearts, int bonusHeartsUnpowered) {
		this.extraHearts = bonusHearts;
		this.extraHeartsUnpowered = bonusHeartsUnpowered;
		return this;
	}

	public PoweredArmor setKnockbackResistance(float resistpercent, float resistpercent_unpowered) {
		this.knockbackresistance = resistpercent;
		this.knockbackresistanceUnpowered = resistpercent_unpowered;
		return this;
	}

	public PoweredArmor setStepAssist(float stepassist, float stepassist_unpowered) {
		this.stepassist = stepassist;
		this.stepassistUnpowerd = stepassist_unpowered;
		return this;
	}

	public PoweredArmor setOxygenGear(float value, float unpowered_value) {
		this.oxygen_gear = value;
		this.oxygen_gearUnpowered = unpowered_value;
		return this;
	}

	public PoweredArmor setCoolingSystem(float value, float unpowered_value) {
		this.coolingsystem = value;
		this.coolingsystemUnpowered = unpowered_value;
		return this;
	}

	public PoweredArmor setWaterspeedBonus(float value, float unpowered_value) {
		this.waterspeedbonus = value;
		this.waterspeedbonusUnpowered = unpowered_value;
		return this;
	}

	public static void consumePower(ItemStack armor, int amount) {
		if (amount == 0) {
			return;
		}
		CompoundTag tags = armor.getOrCreateTag();
		int power = tags.getInt("power");
		power -= amount;
		if (power < 0) {
			power = 0;
		}
		tags.putInt("power", power);
	}

	public int setPowered(ItemStack armor, int max) {
		CompoundTag tags = armor.getOrCreateTag();
		int power = tags.getInt("power");

		int missing = maxpower - power;

		if (missing > max) {
			tags.putInt("power", power + max);
			return max;
		} else {
			if (power != maxpower) {
				tags.putInt("power", maxpower);
			}
			return missing;
		}
	}

	protected void setFullyPowered(ItemStack armor) {
		armor.getOrCreateTag().putInt("power", maxpower);
	}

	protected static boolean hasPower(ItemStack armor) {
		return getPower(armor) > 0;
	}

	public static int getPower(ItemStack armor) {
		CompoundTag tags = armor.getTag();
		if (tags == null) {
			return 0;
		}
		return tags.getInt("power");
	}

	/**
	 * Armor inventory index 2 is the chestplate (0 boots, 1 leggings, 2 chestplate, 3 helmet)
	 */
	protected static void powerSlots(Player player, ArmorPowerType power, ItemStack chest, boolean consumeTick) {
		for (int i = 0; i < 4; i++) {
			ItemStack armor = player.getInventory().armor.get(i);
			if (!armor.isEmpty() && armor.getItem() instanceof PoweredArmor) {
				if (i != 2) {
					if (((PoweredArmor) armor.getItem()).powerType == power) {
						int max = getPower(chest);
						int dmg = ((PoweredArmor) armor.getItem()).setPowered(armor, max);
						consumePower(chest, dmg);
					}
					if (consumeTick) {
						consumePower(armor, 1);
					}
				} else if (consumeTick) {
					consumePower(armor, 1);
				}
			}
		}
	}

	public int applyPowerConsumptionOnAction(TGArmorBonus type, Player player) {

		if (type == TGArmorBonus.JUMP) {
			if (this.getArmorSlot() == EquipmentSlot.LEGS) { //Legs
				if (!player.level().isClientSide) {
					player.level().playSound(null, player.getX(), player.getY(), player.getZ(), TGSounds.STEAM_JUMP, SoundSource.PLAYERS, 1f, 1f);
				}
				return 5;
			}
		} else if (type == TGArmorBonus.FALLDMG) {
			if (this.getArmorSlot() == EquipmentSlot.FEET) {
				return 5;
			}
		} else if (type == TGArmorBonus.COOLING_SYSTEM) {
			if (this.getArmorSlot() == EquipmentSlot.CHEST) {
				return 1;
			}
		}
		return 0;
	}

	/**
	 * Called from tick handler to reduce durability
	 */
	public static void calculateConsumptionTick(Player player) {
		boolean moving = false;
		float f = 0.001f;
		//1.12 used motionX/Z, the movement of a player is not in its delta movement on the server in 1.20: compare with the position of the last tick
		Map<Player, double[]> positions = player.level().isClientSide ? LAST_POSITIONS_CLIENT : LAST_POSITIONS;
		double[] last = positions.get(player);
		if (last != null) {
			double mx = player.getX() - last[0];
			double mz = player.getZ() - last[1];
			if (mx > f || mz > f || mx < -f || mz < -f) {
				moving = true;
			}
		}
		positions.put(player, new double[] { player.getX(), player.getZ() });

		ArmorPowerType power = null;

		ItemStack chest = player.getInventory().armor.get(2);
		if (!chest.isEmpty() && chest.getItem() instanceof PoweredArmor) {
			power = ((PoweredArmor) chest.getItem()).powerType;
		}

		int modTick = (int) (player.level().getGameTime() % 20);
		powerSlots(player, power, chest, (modTick == 0) && moving);

		if (!chest.isEmpty() && chest.getItem() instanceof PoweredArmor) {
			PoweredArmor powerChest = (PoweredArmor) chest.getItem();
			if (!hasPower(chest)) {
				//chest is empty, try reload;
				if (powerChest.battery != null && InventoryUtil.consumeAmmoPlayer(player, powerChest.getBattery())) {

					if (powerChest.battery_empty != null) {
						int amount = InventoryUtil.addAmmoToPlayerInventory(player, powerChest.getBattery_empty());
						if (amount > 0 && !player.level().isClientSide) {
							player.level().addFreshEntity(new ItemEntity(player.level(), player.getX(), player.getY(), player.getZ(), new ItemStack(powerChest.battery_empty, amount)));
						}
					}

					powerChest.setFullyPowered(chest);

					powerSlots(player, power, chest, false);
				}
			}
		}
	}

	@Override
	public void appendHoverText(ItemStack item, @Nullable Level worldIn, List<Component> list, TooltipFlag flagIn) {
		int power = getPower(item);
		addLine(list, ChatFormatting.AQUA + TextUtil.transTG("tooltip.power") + ": " + power + "/" + maxpower);

		if (this.getArmorSlot() == EquipmentSlot.CHEST && this.battery != null) {
			list.add(Component.literal(ChatFormatting.AQUA + TextUtil.transTG("tooltip.powerType") + ": " + this.powerType.toString() + " (")
					.append(Component.translatable(this.battery.getDescriptionId())).append(")").withStyle(ChatFormatting.AQUA));
		} else {
			addLine(list, ChatFormatting.AQUA + TextUtil.transTG("tooltip.powerType") + ": " + this.powerType.toString());
		}

		super.appendHoverText(item, worldIn, list, flagIn);
	}

	@Override
	public void onCraftedBy(ItemStack stack, Level world, Player player) {
		super.onCraftedBy(stack, world, player);
		stack.getOrCreateTag().putInt("power", 0);
	}

	public boolean setArmorStat(EnumArmorStat stat, float powered, float unpowered) {
		super.setArmorStat(stat, powered);
		switch (stat) {
		case COOLING_SYSTEM:
			this.coolingsystemUnpowered = unpowered;
			return true;
		case EXTRA_HEARTS:
			this.extraHeartsUnpowered = unpowered;
			return true;
		case FALL_DAMAGE:
			this.FallDMGUnpowered = unpowered;
			return true;
		case FALL_HEIGHT:
			this.FallFreeHeightUnpowered = unpowered;
			return true;
		case GUN_ACCURACY:
			this.GunAccuracyUnpowered = unpowered;
			return true;
		case JUMP:
			this.JumpBonusUnpowered = unpowered;
			return true;
		case KNOCKBACK_RESITANCE:
			this.knockbackresistanceUnpowered = unpowered;
			return true;
		case MINING_SPEED:
			this.MiningSpeedBonusUnpowered = unpowered;
			return true;
		case NIGHTVISION:
			this.nightvisionUnpowered = unpowered;
			return true;
		case OXYGEN_GEAR:
			this.oxygen_gearUnpowered = unpowered;
			return true;
		case SPEED:
			this.SpeedBonusUnpowered = unpowered;
			return true;
		case STEP_ASSIST:
			this.stepassistUnpowerd = unpowered;
			return true;
		case WATER_ELECTROLYZER:
			this.water_electrolyzerUnpowered = unpowered;
			return true;
		case WATER_MINING_SPEED:
			this.WaterMiningBonusUnpowered = unpowered;
			return true;
		case WATER_SPEED:
			this.waterspeedbonusUnpowered = unpowered;
			return true;
		default:
			return false;
		}
	}
}
