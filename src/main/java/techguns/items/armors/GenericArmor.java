package techguns.items.armors;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.registries.ForgeRegistries;
import techguns.TGArmors;
import techguns.TGItems;
import techguns.Techguns;
import techguns.api.damagesystem.DamageType;
import techguns.api.tginventory.ITGSpecialSlot;
import techguns.gui.player.TGPlayerInventory;
import techguns.capabilities.TGExtendedPlayer;
import techguns.damagesystem.DamageSystem;
import techguns.damagesystem.TGDamageSource;
import techguns.util.TextUtil;

/**
 * Port of the 1.12 GenericArmor (ItemArmor + ISpecialArmor).
 * <ul>
 * <li>The ISpecialArmor damage reduction is done by TGArmorEventHandler (LivingHurtEvent), using {@link #getAbsorbRatio}</li>
 * <li>The ARMOR attribute of the item is the 1.12 getArmorDisplay value (armor bar), TGArmorEventHandler removes it during the
 * vanilla armor calculation so TG pieces only protect with the TG formula (like in 1.12 where their vanilla armor was 0)</li>
 * <li>Vanilla armor durability damage is disabled ({@link #damageItem}), TG pieces lose durability with {@link #damageArmor}</li>
 * </ul>
 */
public class GenericArmor extends ArmorItem implements TGItems.ITGRegistryNamed {

	/**
	 * same UUIDs as the (private) vanilla ArmorItem.ARMOR_MODIFIER_UUID_PER_TYPE, so TG armor replaces the vanilla modifier of the slot
	 */
	protected static final EnumMap<ArmorItem.Type, UUID> ARMOR_MODIFIER_UUIDS = new EnumMap<>(ArmorItem.Type.class);
	static {
		ARMOR_MODIFIER_UUIDS.put(ArmorItem.Type.BOOTS, UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"));
		ARMOR_MODIFIER_UUIDS.put(ArmorItem.Type.LEGGINGS, UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"));
		ARMOR_MODIFIER_UUIDS.put(ArmorItem.Type.CHESTPLATE, UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"));
		ARMOR_MODIFIER_UUIDS.put(ArmorItem.Type.HELMET, UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150"));
	}

	//radiation resistance modifiers (lot I), index = EquipmentSlot.getIndex() (FEET, LEGS, CHEST, HEAD)
	protected static final UUID[] RAD_RESIST_MODIFIER = {
			UUID.fromString("47E9813E-3FB7-415C-A6CD-F9A4A3C55F82"),
			UUID.fromString("AD509C77-02FC-40E8-A811-52B29842CE56"),
			UUID.fromString("CB3BF616-549C-47EF-ADC8-2ED60C331ABD"),
			UUID.fromString("052C821F-1FA9-4D67-A660-D885FAE22EED")
	};

	//For vanilla Anvil (1.12 ItemStack, now the registry name of the item)
	@Nullable
	protected ResourceLocation repairItem = null;

	//Values for RepairBench
	@Nullable
	protected ResourceLocation repairMatMetal = null;
	@Nullable
	protected ResourceLocation repairMatCloth = null;
	protected float repairMatRatioMetal = 0.5f;
	protected int repairMatCount = 0;

	protected String textureName;

	protected ResourceLocation armorModel = null; //default vanilla model
	protected boolean doubleTex = true;

	//item boni
	protected float SpeedBonus = 0.0f;
	protected float JumpBonus = 0.0f;
	protected float FallDMG = 0.0f;
	protected float FallFreeHeight = 0.0f;
	protected float MiningSpeedBonus = 0.0f;
	protected float WaterMiningBonus = 0.0f;
	protected int armorValue = 0;
	protected float GunAccuracy = 0.0f;
	protected float extraHearts = 0.0f;
	protected float nightvision = 0.0f;

	protected float knockbackresistance = 0.0f;
	protected float stepassist = 0.0f;

	protected float oxygen_gear = 0.0f;
	protected float water_electrolyzer = 0.0f;
	protected float coolingsystem = 0.0f;

	protected float waterspeedbonus = 0.0f;

	protected float radresistance = 0.0f;

	protected boolean hideFaceslot = false;
	protected boolean hideBackslot = false;
	protected boolean hideGloveslot = false;

	protected TGArmorMaterial material;

	protected static DecimalFormat formatArmor = new DecimalFormat("#.##", DecimalFormatSymbols.getInstance(Locale.ENGLISH));

	protected boolean use3dRenderHack = false;

	protected String modid = Techguns.MODID;
	protected final String name;

	public GenericArmor(String unlocalizedName, TGArmorMaterial material, String textureName, EquipmentSlot type) {
		this(Techguns.MODID, unlocalizedName, material, textureName, type);
	}

	public GenericArmor(String modid, String unlocalizedName, TGArmorMaterial material, String textureName, EquipmentSlot type) {
		super(material, getTypeForSlot(type), new Item.Properties());
		this.material = material;
		this.textureName = textureName; //Armor Texture
		this.modid = modid;
		this.name = unlocalizedName;
		//for tooltip
		this.armorValue = Math.round(material.getArmorValueSlot(type, DamageType.PHYSICAL));

		TGArmors.armors.add(this);
		TGItems.ITEMLIST.add(this);
	}

	public static ArmorItem.Type getTypeForSlot(EquipmentSlot slot) {
		switch (slot) {
		case HEAD:
			return ArmorItem.Type.HELMET;
		case CHEST:
			return ArmorItem.Type.CHESTPLATE;
		case LEGS:
			return ArmorItem.Type.LEGGINGS;
		case FEET:
		default:
			return ArmorItem.Type.BOOTS;
		}
	}

	@Override
	public ResourceLocation getTGRegistryName() {
		return new ResourceLocation(this.modid, this.name);
	}

	public String getTGName() {
		return this.name;
	}

	/**
	 * Armor slot of this item (1.12 armorType)
	 */
	public EquipmentSlot getArmorSlot() {
		return this.type.getSlot();
	}

	public TGArmorMaterial getTGMaterial() {
		return this.material;
	}

	@Override
	public void initializeClient(Consumer<IClientItemExtensions> consumer) {
		consumer.accept(techguns.client.armor.TGArmorClientExtensions.INSTANCE);
	}

	/**
	 * SteamArmor (0,1) PowerArmor (2,3) ExoSuit (4,5,6) Beret (7)
	 */
	public GenericArmor setArmorModel(ResourceLocation key, boolean doubleTex, ResourceLocation backupTex) {
		this.armorModel = key;
		this.doubleTex = doubleTex;
		return this;
	}

	public GenericArmor setArmorModel(ResourceLocation key, boolean doubleTex) {
		this.armorModel = key;
		this.doubleTex = doubleTex;
		return this;
	}

	@Nullable
	public ResourceLocation getArmorModelKey() {
		return this.armorModel;
	}

	public static boolean isTechgunArmor(ItemStack i) {
		return !i.isEmpty() && i.getItem() instanceof GenericArmor;
	}

	public GenericArmor setSpeedBoni(float speed, float jump) {
		this.SpeedBonus = speed;
		this.JumpBonus = jump;
		return this;
	}

	public GenericArmor setFallProtection(float multiplier, float freeheight) {
		this.FallDMG = multiplier;
		this.FallFreeHeight = freeheight;
		return this;
	}

	public GenericArmor setMiningBoni(float bonus) {
		this.MiningSpeedBonus = bonus;
		return this;
	}

	public GenericArmor setMiningBoniWater(float bonus) {
		this.WaterMiningBonus = bonus;
		return this;
	}

	public GenericArmor setGunBonus(float acc) {
		this.GunAccuracy = acc;
		return this;
	}

	public GenericArmor setHealthBonus(int bonusHearts) {
		this.extraHearts = (float) bonusHearts;
		return this;
	}

	public GenericArmor setKnockbackResistance(float resistpercent) {
		this.knockbackresistance = resistpercent;
		return this;
	}

	public GenericArmor setRADResistance(float radresistance) {
		this.radresistance = radresistance;
		return this;
	}

	public GenericArmor setStepAssist(float stepassist) {
		this.stepassist = stepassist;
		return this;
	}

	public GenericArmor setOxygenGear(float value) {
		this.oxygen_gear = value;
		return this;
	}

	public GenericArmor setCoolingSystem(float value) {
		this.coolingsystem = value;
		return this;
	}

	public GenericArmor setWaterspeedBonus(float value) {
		this.waterspeedbonus = value;
		return this;
	}

	public GenericArmor setUseRenderHack() {
		this.use3dRenderHack = true;
		return this;
	}

	public boolean shouldUseRenderHack(ItemStack stack) {
		return this.use3dRenderHack;
	}

	public float getBonus(TGArmorBonus type) {
		switch (type) {
		case SPEED:
			return this.SpeedBonus;
		case JUMP:
			return this.JumpBonus;
		case FALLDMG:
			return this.FallDMG;
		case FREEHEIGHT:
			return this.FallFreeHeight;
		case BREAKSPEED:
			return this.MiningSpeedBonus;
		case BREAKSPEED_WATER:
			return this.WaterMiningBonus;
		case GUN_ACCURACY:
			return this.GunAccuracy;
		case EXTRA_HEART:
			return this.extraHearts;
		case NIGHTVISION:
			return this.nightvision;
		case KNOCKBACK_RESISTANCE:
			return this.knockbackresistance;
		case STEPASSIST:
			return this.stepassist;
		case OXYGEN_GEAR:
			return this.oxygen_gear;
		case WATER_ELECTROLYZER:
			return this.water_electrolyzer;
		case COOLING_SYSTEM:
			return this.coolingsystem;
		case SPEED_WATER:
			return this.waterspeedbonus;
		case RAD_RESISTANCE:
			return this.radresistance;
		default:
			break;
		}
		return 0.0f;
	}

	/**
	 * 1.12: armor is kept at 1 durability and is "broken" then (no bonus, no displayed armor)
	 */
	public static boolean isBroken(ItemStack armor) {
		return armor.isDamageableItem() && armor.getDamageValue() >= armor.getMaxDamage() - 1;
	}

	public static float getArmorBonusForPlayer(Player ply, TGArmorBonus type, boolean consumePower) {
		float bonus = 0.0f;
		TGExtendedPlayer props = TGExtendedPlayer.get(ply);
		if (props == null) {
			return 0.0f;
		}
		for (int i = 0; i < 4; i++) {
			ItemStack armor = ply.getInventory().armor.get(i);
			if (GenericArmor.isTechgunArmor(armor)) {

				if (!(armor.getItem() instanceof PoweredArmor)) {
					if (!isBroken(armor)) {
						float bonusVal = ((GenericArmor) armor.getItem()).getBonus(type);
						bonus += bonusVal;
					}
				} else {
					//Powered Armor
					PoweredArmor pwrarmor = (PoweredArmor) armor.getItem();
					boolean power = PoweredArmor.hasPower(armor);
					float bonusVal = 0;

					if (!isBroken(armor)) {
						if (power) {
							bonusVal = pwrarmor.getBonus(type);
							bonus += bonusVal;
							if (consumePower && bonusVal > 0.0f) {
								PoweredArmor.consumePower(armor, pwrarmor.applyPowerConsumptionOnAction(type, ply));
							}
						} else {
							bonusVal = pwrarmor.getBonusUnpowered(type);
							bonus += bonusVal;
						}
					}
				}
			}
		}
		bonus += getBonusForSlot(props.tg_inventory.inventory.get(TGPlayerInventory.SLOT_FACE), type, consumePower, ply);
		bonus += getBonusForSlot(props.tg_inventory.inventory.get(TGPlayerInventory.SLOT_BACK), type, consumePower, ply);
		bonus += getBonusForSlot(props.tg_inventory.inventory.get(TGPlayerInventory.SLOT_HAND), type, consumePower, ply);
		return bonus;
	}

	private static float getBonusForSlot(ItemStack stack, TGArmorBonus type, boolean consumePower, Player ply) {
		float bonus = 0;
		if (!stack.isEmpty() && stack.getItem() instanceof ITGSpecialSlot item) {
			//1.12 always used the bonus of shared items (crafting materials like the oxygen mask), they are not damageable
			if ((stack.getDamageValue() < stack.getMaxDamage()) || !stack.isDamageableItem()) {
				bonus += item.getBonus(type, stack, consumePower, ply);
			}
		}
		return bonus;
	}

	/**
	 * 1.12 GenericArmor didn't override getArmorTexture: material texture, see HumanoidArmorLayer.getArmorResource
	 */
	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
		return null;
	}

	protected boolean hasDoubleTexture() {
		return this.doubleTex;
	}

	protected static String trans(String text) {
		return TextUtil.trans(Techguns.MODID + "." + text);
	}

	public static String formatAV(float armorValue, DamageType damageType) {
		return formatArmor.format(armorValue);
	}

	protected static void addLine(List<Component> list, String text) {
		list.add(Component.literal(text));
	}

	protected void addArmorvaluesInformation(ItemStack item, List<Component> list) {
		EquipmentSlot slot = this.getArmorSlot();

		addLine(list, ChatFormatting.DARK_GRAY + " AR: " + formatAV(this.material.getArmorValueSlot(slot, DamageType.PHYSICAL), DamageType.PHYSICAL));
		addLine(list, ChatFormatting.GRAY + " PR: " + formatAV(this.material.getArmorValueSlot(slot, DamageType.PROJECTILE), DamageType.PROJECTILE));
		addLine(list, ChatFormatting.DARK_RED + " EX: " + formatAV(this.material.getArmorValueSlot(slot, DamageType.EXPLOSION), DamageType.EXPLOSION));
		addLine(list, ChatFormatting.DARK_AQUA + " E: " + formatAV(this.material.getArmorValueSlot(slot, DamageType.ENERGY), DamageType.ENERGY));

		addLine(list, ChatFormatting.RED + " F: " + formatAV(this.material.getArmorValueSlot(slot, DamageType.FIRE), DamageType.FIRE));
		addLine(list, ChatFormatting.AQUA + " I: " + formatAV(this.material.getArmorValueSlot(slot, DamageType.ICE), DamageType.ICE));
		addLine(list, ChatFormatting.YELLOW + " L: " + formatAV(this.material.getArmorValueSlot(slot, DamageType.LIGHTNING), DamageType.LIGHTNING));
		addLine(list, ChatFormatting.DARK_GREEN + " P: " + formatAV(this.material.getArmorValueSlot(slot, DamageType.POISON), DamageType.POISON));
		addLine(list, ChatFormatting.DARK_GRAY + " D: " + formatAV(this.material.getArmorValueSlot(slot, DamageType.DARK), DamageType.DARK));
		addLine(list, ChatFormatting.GREEN + " RAD: " + formatAV(this.material.getArmorValueSlot(slot, DamageType.RADIATION), DamageType.RADIATION));
	}

	@Override
	public void appendHoverText(ItemStack item, @Nullable Level worldIn, List<Component> list, TooltipFlag flagIn) {
		super.appendHoverText(item, worldIn, list, flagIn);

		if (Techguns.proxy.isShiftKeyDown()) {

			addLine(list, trans("armorTooltip.durability") + ": " + (item.getMaxDamage() - item.getDamageValue()) + "/" + (item.getMaxDamage()));

			if (this.material.getTGToughness() > 0) {
				addLine(list, ChatFormatting.GRAY + trans("armorTooltip.toughness") + ": " + this.material.getTGToughness());
			}

			addLine(list, ChatFormatting.BLUE + trans("armorTooltip.resistances") + ":");
			this.addArmorvaluesInformation(item, list);

			if (this.getBonus(TGArmorBonus.EXTRA_HEART) > 0.0f) {
				addLine(list, trans("armorTooltip.healthbonus") + ": +" + (int) this.getBonus(TGArmorBonus.EXTRA_HEART) + " " + trans("armorTooltip.hearts"));
			} else if (this.getBonus(TGArmorBonus.SPEED) > 0.0f) {
				addLine(list, trans("armorTooltip.movespeed") + ": +" + this.getBonus(TGArmorBonus.SPEED) * 100.0f + "%");
			} else if (this.getBonus(TGArmorBonus.JUMP) > 0.0f) {
				addLine(list, trans("armorTooltip.jumpheight") + ": +" + this.getBonus(TGArmorBonus.JUMP));
			}
			if (this.getBonus(TGArmorBonus.FALLDMG) > 0.0f) {
				addLine(list, trans("armorTooltip.falldamage") + ": -" + this.getBonus(TGArmorBonus.FALLDMG) * 100.0f + "%");
			}
			if (this.getBonus(TGArmorBonus.FREEHEIGHT) > 0.0f) {
				addLine(list, trans("armorTooltip.fallheight") + ": -" + this.getBonus(TGArmorBonus.FREEHEIGHT));
			}
			if (this.getBonus(TGArmorBonus.BREAKSPEED) > 0.0f) {
				addLine(list, trans("armorTooltip.miningspeed") + ": +" + this.getBonus(TGArmorBonus.BREAKSPEED) * 100.0f + "%");
			}
			if (this.getBonus(TGArmorBonus.BREAKSPEED_WATER) > 0.0f) {
				addLine(list, trans("armorTooltip.underwatermining") + ": +" + this.getBonus(TGArmorBonus.BREAKSPEED_WATER) * 100.0f + "%");
			}
			if (this.getBonus(TGArmorBonus.KNOCKBACK_RESISTANCE) > 0.0f) {
				addLine(list, trans("armorTooltip.knockbackresistance") + ": +" + this.getBonus(TGArmorBonus.KNOCKBACK_RESISTANCE) * 100.0f + "%");
			}
			if (this.getBonus(TGArmorBonus.NIGHTVISION) > 0.0f) {
				addLine(list, trans("armorTooltip.nightvision"));
			}
			if (this.getBonus(TGArmorBonus.STEPASSIST) > 0.0f) {
				addLine(list, trans("armorTooltip.stepassist"));
			}
			if (this.getBonus(TGArmorBonus.OXYGEN_GEAR) > 0.0f) {
				addLine(list, trans("armorTooltip.oxygengear"));
			}
			if (this.getBonus(TGArmorBonus.COOLING_SYSTEM) > 0.0f) {
				addLine(list, trans("armorTooltip.coolingsystem"));
			}
		} else {
			this.addMinimalInformation(item, list);
			addLine(list, TextUtil.trans("techguns.gun.tooltip.shift1") + " " + ChatFormatting.GREEN + TextUtil.trans("techguns.gun.tooltip.shift2") + " " + ChatFormatting.GRAY + TextUtil.trans("techguns.gun.tooltip.shift3"));
		}
	}

	/**
	 * to override in subclasses
	 */
	protected void addMinimalInformation(ItemStack item, List<Component> list) {
		addLine(list, trans("armorTooltip.durability") + ": " + (item.getMaxDamage() - item.getDamageValue()) + "/" + (item.getMaxDamage()));
	}

	public float getArmorValue(ItemStack armor, DamageType type) {
		return this.material.getArmorValueSlot(this.getArmorSlot(), type);
	}

	@Deprecated
	public float getPenetrationResistance() {
		return this.material.getPenetrationResistance();
	}

	/**
	 * 1.12 ISpecialArmor.getProperties: AbsorbRatio of this piece against the damage source
	 */
	public double getAbsorbRatio(LivingEntity entity, ItemStack armor, TGDamageSource src) {
		return 1.0 - DamageSystem.getDamageAfterAbsorb_TGFormula(1.0f, this.getArmorValue(armor, src.damageType), this.material.getTGToughness(), src.armorPenetration);
	}

	/**
	 * 1.12 getArmorDisplay
	 */
	public int getArmorDisplay(ItemStack armor) {
		if (isBroken(armor)) {
			return 0;
		} else {
			return this.armorValue;
		}
	}

	/**
	 * ARMOR = displayed armor (0 when broken), ARMOR_TOUGHNESS = TG toughness
	 */
	@Override
	public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
		if (slot != this.getArmorSlot()) {
			return super.getAttributeModifiers(slot, stack);
		}
		ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
		UUID uuid = ARMOR_MODIFIER_UUIDS.get(this.type);
		builder.put(Attributes.ARMOR, new AttributeModifier(uuid, "Armor modifier", (double) this.getArmorDisplay(stack), AttributeModifier.Operation.ADDITION));
		builder.put(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(uuid, "Armor toughness", (double) this.material.getTGToughness(), AttributeModifier.Operation.ADDITION));
		//lot I (radiation)
		if (this.radresistance > 0) {
			builder.put(techguns.TGRadiationSystem.RADIATION_RESISTANCE, new AttributeModifier(RAD_RESIST_MODIFIER[slot.getIndex()], "techguns.radresistance." + slot.getName(), this.radresistance, AttributeModifier.Operation.ADDITION));
		}
		return builder.build();
	}

	@Override
	public boolean isValidRepairItem(ItemStack toRepair, ItemStack mat) {
		if (this.repairItem != null && !mat.isEmpty()) {
			ResourceLocation key = ForgeRegistries.ITEMS.getKey(mat.getItem());
			return this.repairItem.equals(key);
		}
		return false;
	}

	/**
	 * Vanilla armor damage (Inventory.hurtArmor) is disabled for TG armor, durability is handled by damageArmor like in 1.12
	 */
	@Override
	public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<T> onBroken) {
		return 0;
	}

	/**
	 * 1.12 ISpecialArmor.damageArmor: keeps armor at 1 durability
	 */
	public void damageArmor(LivingEntity entity, ItemStack stack, int damage) {
		if (!(entity instanceof Player) || !((Player) entity).getAbilities().instabuild) {
			if (stack.isDamageableItem()) {

				//Keep Armours at 1 durability.
				int maxDmg = stack.getMaxDamage() - 1 - stack.getDamageValue();
				if (damage > maxDmg) {
					damage = maxDmg;
				}
				if (damage <= 0) {
					return;
				}

				if (stack.hurt(damage, entity.getRandom(), entity instanceof ServerPlayer ? (ServerPlayer) entity : null)) {
					if (stack.getDamageValue() > stack.getMaxDamage()) {
						stack.setDamageValue(stack.getMaxDamage());
					}
				}
			}
		}
	}

	public boolean isHideFaceslot() {
		return hideFaceslot;
	}

	public GenericArmor setHideFaceslot(boolean hideFaceslot) {
		this.hideFaceslot = hideFaceslot;
		return this;
	}

	public boolean isHideBackslot() {
		return hideBackslot;
	}

	public GenericArmor setHideBackslot(boolean hideBackslot) {
		this.hideBackslot = hideBackslot;
		return this;
	}

	public boolean isHideGloveslot() {
		return hideGloveslot;
	}

	public GenericArmor setHideGloveslot(boolean hideGloveslot) {
		this.hideGloveslot = hideGloveslot;
		return this;
	}

	protected static ResourceLocation tgItem(String name) {
		return new ResourceLocation(Techguns.MODID, name);
	}

	/**
	 * Repair materials, given as registry names
	 */
	public GenericArmor setRepairMats(@Nullable ResourceLocation metal, @Nullable ResourceLocation cloth, float metalpercent, int totalmats) {
		if (metal != null) {
			this.repairItem = metal;
		} else if (cloth != null) {
			this.repairItem = cloth;
		}
		this.repairMatMetal = metal;
		this.repairMatCloth = cloth;
		this.repairMatCount = totalmats;
		this.repairMatRatioMetal = metalpercent;
		return this;
	}

	public boolean canRepairOnRepairBench(ItemStack item) {
		GenericArmor armor = (GenericArmor) item.getItem();
		return armor.repairMatCount > 0;
	}

	public ArrayList<ItemStack> getRepairMats(ItemStack item) {
		ArrayList<ItemStack> mats = new ArrayList<ItemStack>();

		if (item.getDamageValue() > 0) {
			GenericArmor armor = (GenericArmor) item.getItem();

			float dmgpercent = (item.getDamageValue() * 1.0f) / ((item.getMaxDamage() - 1) * 1.0f);

			int count = (int) Math.ceil(armor.repairMatCount * dmgpercent);

			int metalcount = (int) Math.ceil(count * armor.repairMatRatioMetal);
			int clothcount = count - metalcount;

			if (armor.repairMatMetal != null && metalcount > 0) {
				Item it = ForgeRegistries.ITEMS.getValue(armor.repairMatMetal);
				if (it != null) {
					mats.add(new ItemStack(it, metalcount));
				}
			}
			if (armor.repairMatCloth != null && clothcount > 0) {
				Item it = ForgeRegistries.ITEMS.getValue(armor.repairMatCloth);
				if (it != null) {
					mats.add(new ItemStack(it, clothcount));
				}
			}
		}
		return mats;
	}

	@Override
	public boolean isFoil(ItemStack stack) {
		return false;
	}

	public boolean setArmorStat(EnumArmorStat stat, float value) {
		switch (stat) {
		case COOLING_SYSTEM:
			this.coolingsystem = value;
			return true;
		case EXTRA_HEARTS:
			this.extraHearts = value;
			return true;
		case FALL_DAMAGE:
			this.FallDMG = value;
			return true;
		case FALL_HEIGHT:
			this.FallFreeHeight = value;
			return true;
		case GUN_ACCURACY:
			this.GunAccuracy = value;
			return true;
		case JUMP:
			this.JumpBonus = value;
			return true;
		case KNOCKBACK_RESITANCE:
			this.knockbackresistance = value;
			return true;
		case MINING_SPEED:
			this.MiningSpeedBonus = value;
			return true;
		case NIGHTVISION:
			this.nightvision = value;
			return true;
		case OXYGEN_GEAR:
			this.oxygen_gear = value;
			return true;
		case SPEED:
			this.SpeedBonus = value;
			return true;
		case STEP_ASSIST:
			this.stepassist = value;
			return true;
		case WATER_ELECTROLYZER:
			this.water_electrolyzer = value;
			return true;
		case WATER_MINING_SPEED:
			this.WaterMiningBonus = value;
			return true;
		case WATER_SPEED:
			this.waterspeedbonus = value;
			return true;
		case RAD_RESISTANCE:
			this.radresistance = value;
			return true;
		default:
			return false;
		}
	}
}
