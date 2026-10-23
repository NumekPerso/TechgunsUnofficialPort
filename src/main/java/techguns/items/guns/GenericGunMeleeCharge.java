package techguns.items.guns;

import java.util.HashMap;
import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import techguns.TGPackets;
import techguns.TGSounds;
import techguns.Techguns;
import techguns.api.damagesystem.DamageType;
import techguns.capabilities.TGExtendedPlayer;
import techguns.client.ShooterValues;
import techguns.damagesystem.TGDamageSource;
import techguns.deatheffects.EntityDeathUtils.DeathType;
import techguns.packets.PacketPlaySound;
import techguns.util.BlockUtils;
import techguns.util.ItemUtil;
import techguns.util.TGSoundCategory;
import techguns.util.TextUtil;

/**
 * Melee / mining tool gun (power hammer, chainsaw, mining drill, shishkebap).
 */
public class GenericGunMeleeCharge extends GenericGunCharge implements IGenericGunMelee<GenericGunMeleeCharge> {

	HashMap<String, Integer> mininglevels = new HashMap<>();

	/**
	 * Extra radius for minig, 0 = 1 block, 1 = 3x3, 2 = 5x5, ...
	 */
	protected int miningRadius = 0;

	protected int swingSoundDelay = 5;

	//Items instead of 1.12 ItemStacks: stacks cannot be created while items are being registered
	protected Item[] miningHeads = null;

	protected boolean hasCustomAnim = true;

	protected float melee_sound_volume = 0.65f;

	@SuppressWarnings("rawtypes")
	public GenericGunMeleeCharge(String name, ChargedProjectileSelector projectile_selector, boolean semiAuto,
			int minFiretime, int clipsize, int reloadtime, float damage, SoundEvent firesound, SoundEvent reloadsound,
			int TTL, float accuracy, float fullChargeTime, int ammoConsumedOnFullCharge) {
		super(name, projectile_selector, semiAuto, minFiretime, clipsize, reloadtime, damage, firesound, reloadsound, TTL,
				accuracy, fullChargeTime, ammoConsumedOnFullCharge);
	}

	public GenericGunMeleeCharge setMiningHeads(Item... heads) {
		this.miningHeads = heads;
		return this;
	}

	public GenericGunMeleeCharge setMiningRadius(int miningRadius) {
		this.miningRadius = miningRadius;
		return this;
	}

	public GenericGunMeleeCharge setSwingSoundDelay(int swingSoundDelay) {
		this.swingSoundDelay = swingSoundDelay;
		return this;
	}

	public GenericGunMeleeCharge setHasCustomAnim(boolean customAnim) {
		this.hasCustomAnim = customAnim;
		return this;
	}

	@Override
	public int getMiningRadius(ItemStack stack) {
		return miningRadius + getExtraMiningRadius(stack);
	}

	@Override
	public HashMap<String, Integer> getMiningLevels() {
		return mininglevels;
	}

	public int getExtraMiningRadius(ItemStack stack) {
		return 0;
	}

	@Override
	public float getDestroySpeed(ItemStack stack, BlockState state) {
		return this.getDigSpeed(stack, state);
	}

	/**
	 * 1.12 getHarvestLevel / canHarvestBlock
	 */
	@Override
	public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
		return this.canHarvestWithTool(stack, state);
	}

	@Override
	public boolean canPerformAction(ItemStack stack, ToolAction toolAction) {
		if (this.getCurrentAmmo(stack) < this.getMiningAmmoConsumption()) {
			return false;
		}
		if (toolAction == ToolActions.PICKAXE_DIG) {
			return this.mininglevels.containsKey("pickaxe");
		} else if (toolAction == ToolActions.AXE_DIG) {
			return this.mininglevels.containsKey("axe");
		} else if (toolAction == ToolActions.SHOVEL_DIG) {
			return this.mininglevels.containsKey("shovel");
		} else if (toolAction == ToolActions.HOE_DIG) {
			return this.mininglevels.containsKey("hoe");
		} else if (toolAction == ToolActions.SWORD_DIG) {
			return this.mininglevels.containsKey("sword");
		}
		return false;
	}

	@Override
	public float getEffectiveDigSpeed(ItemStack itemstack) {
		return this.digSpeed + this.getExtraDigSpeed(itemstack);
	}

	@Override
	public int getMiningAmmoConsumption() {
		return this.miningAmmoConsumption;
	}

	@Override
	public GenericGunMeleeCharge setDigSpeed(float speed) {
		this.digSpeed = speed;
		return this;
	}

	@Override
	protected TGDamageSource getMeleeDamageSource(Player player, ItemStack stack) {
		TGDamageSource src = new TGDamageSource("player", player, player, DamageType.PHYSICAL, DeathType.GORE);
		if (this.getCurrentAmmo(stack) > 0) {
			src.goreChance = 1.0f;
			src.armorPenetration = this.penetration;
			src.knockbackMultiplier = 1f;
		} else {
			src.deathType = DeathType.DEFAULT;
		}
		return src;
	}

	protected SoundEvent getSwingSound() {
		return TGSounds.POWERHAMMER_SWING;
	}

	protected SoundEvent getBlockBreakSound() {
		return TGSounds.POWERHAMMER_IMPACT;
	}

	@Override
	protected void addInitialTags(CompoundTag tags) {
		super.addInitialTags(tags);
		tags.putInt("miningHead", 0);
	}

	@Override
	public int getMiningHeadLevel(ItemStack stack) {
		CompoundTag tags = stack.getTag();
		if (tags == null) {
			this.initTags(stack);
			tags = stack.getTag();
		}
		return tags.getInt("miningHead");
	}

	public int getMiningHeadLevelForHead(ItemStack head) {
		if (this.miningHeads != null) {
			int i = 0;
			while (i < this.miningHeads.length) {
				if (head.is(this.miningHeads[i])) {
					return i + 1;
				}
				i++;
			}
		}
		return 0;
	}

	public String getCurrentMiningHeadForTooltip(ItemStack stack) {
		if (this.miningHeads != null && this.getMiningHeadLevel(stack) > 0 && this.getMiningHeadLevel(stack) <= this.miningHeads.length) {
			return this.miningHeads[this.getMiningHeadLevel(stack) - 1].getDescriptionId();
		}
		return Techguns.MODID + ".default";
	}

	@Override
	public int getExtraMiningLevel(ItemStack stack, String toolClass, Player player) {
		return getMiningHeadLevel(stack);
	}

	public float getExtraDigSpeed(ItemStack stack) {
		int headlevel = this.getMiningHeadLevel(stack);
		return 1.0f * headlevel;
	}

	@Override
	public boolean onEntitySwing(ItemStack stack, LivingEntity entityLiving) {
		boolean openingContainer = false;
		if (!entityLiving.level().isClientSide) {
			if (entityLiving instanceof Player ply) {
				if (ply.containerMenu != null && ply.containerMenu != ply.inventoryMenu) {
					openingContainer = true;
				}
			}
		}

		if (this.getCurrentAmmo(stack) > 0) {
			if (entityLiving.level().isClientSide) {
				if (ShooterValues.getRecoiltime(entityLiving, false) < System.currentTimeMillis()) {
					ShooterValues.setRecoiltime(entityLiving, false, System.currentTimeMillis() + 250, 250, (byte) 0);
				}
				return this.hasCustomAnim;
			} else {

				boolean sendSound = true;
				if (entityLiving instanceof Player) {
					TGExtendedPlayer props = TGExtendedPlayer.get((Player) entityLiving);
					if (props != null) {
						if (props.swingSoundDelay > 0) {
							sendSound = false;
						} else {
							props.swingSoundDelay = this.swingSoundDelay;
						}
					}
				}

				if (!openingContainer && sendSound) {
					TGPackets.network.sendToAllAround(new PacketPlaySound(getSwingSound(), entityLiving, melee_sound_volume, 1.0f, false, false, true, true, TGSoundCategory.GUN_FIRE), TGPackets.targetPointAroundEnt(entityLiving, 32.0f));
				}

				return this.hasCustomAnim;
			}
		}
		return false;
	}

	/**
	 * 1.12 onBlockDestroyed, creative players don't use ammo
	 */
	@Override
	public boolean mineBlock(ItemStack stack, Level worldIn, BlockState state, BlockPos pos, LivingEntity entityLiving) {
		if (this.getCurrentAmmo(stack) > 0) {
			if (!isCreative(entityLiving)) {
				this.useAmmo(stack, 1);
			}
			if (entityLiving instanceof Player) {
				SoundEvent sound = this.getBlockBreakSound();
				if (sound != null) {
					worldIn.playSound((Player) entityLiving, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, sound, SoundSource.PLAYERS, melee_sound_volume, 1.0f);
				}
			}
			return true;
		}
		return false;
	}

	@Nullable
	public Direction getSideHitMining(Level world, Player player) {
		BlockHitResult result = getPlayerPOVHitResult(world, player, ClipContext.Fluid.NONE);
		if (result != null && result.getType() == HitResult.Type.BLOCK) {
			return result.getDirection();
		}
		return null;
	}

	@Override
	public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {

		Multimap<Attribute, AttributeModifier> multimap = HashMultimap.<Attribute, AttributeModifier>create();

		if (slot == EquipmentSlot.MAINHAND) {
			double meleedmg = this.meleeDamagePwr;
			int ammoleft = this.getCurrentAmmo(stack);
			if (ammoleft <= 0) {
				meleedmg = this.meleeDamageEmpty;
			}

			multimap.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", meleedmg, AttributeModifier.Operation.ADDITION));
			multimap.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", -2.4000000953674316D, AttributeModifier.Operation.ADDITION));
		}

		return multimap;
	}

	@Override
	protected void addMiningTooltip(ItemStack stack, @Nullable Level world, List<Component> list, TooltipFlag flagIn, boolean longTooltip) {
		super.addMiningTooltip(stack, world, list, flagIn, longTooltip);

		if (longTooltip) {
			if (this.miningHeads != null) {
				addLine(list, TextUtil.trans("techguns.tooltip.mininghead") + ": " + ChatFormatting.WHITE + TextUtil.trans(this.getCurrentMiningHeadForTooltip(stack)));
			}

			addLine(list, TextUtil.trans("techguns.tooltip.toolclasses") + ":");
			for (String s : this.getMiningLevels().keySet()) {
				if (!s.equals("default")) {
					addLine(list, " " + TextUtil.transTG("toolclass." + s) + ": " + (this.getMiningLevels().get(s) + this.getExtraMiningLevel(stack, s, null)));
				}
			}
			addLine(list, TextUtil.trans("techguns.tooltip.breakspeed") + ": " + this.getEffectiveDigSpeed(stack));

			int r = this.getMiningRadius(stack) * 2 + 1;
			addLine(list, TextUtil.trans("techguns.tooltip.miningradius") + ": " + r + "x" + r);
		} else {
			String toolclasses = null;
			for (String s : this.getMiningLevels().keySet()) {
				if (!s.equals("default")) {
					if (toolclasses != null) {
						toolclasses += ", " + s;
					} else {
						toolclasses = s;
					}
				}
			}
			if (toolclasses != null) {
				addLine(list, TextUtil.trans("techguns.tooltip.toolclasses") + ": " + toolclasses);
			}
		}
	}

	/**
	 * Mines the other blocks of the mining plane (mining radius &gt; 0, not sneaking), drops are spawned at the center block like in 1.12
	 */
	@Override
	public boolean onBlockStartBreak(ItemStack itemstack, BlockPos pos, Player player) {
		if (!player.level().isClientSide && !player.isShiftKeyDown() && this.getMiningRadius(itemstack) > 0 && player instanceof ServerPlayer plyMP) {

			Direction sideHit = this.getSideHitMining(player.level(), player);
			if (sideHit != null) {

				Level w = player.level();
				List<BlockPos> otherblocks = BlockUtils.getBlockPlaneAroundAxisForMining(w, player, pos, sideHit.getAxis(), this.getMiningRadius(itemstack), false, this, itemstack);
				for (BlockPos b : otherblocks) {
					if (w.isLoaded(b)) {
						BlockState state = w.getBlockState(b);
						if (!state.isAir() && state.getDestroyProgress(player, w, b) > 0f
								&& state.canHarvestBlock(w, b, player) && this.isEffectiveToolForState(itemstack, state)) {

							int xpAmount = ForgeHooks.onBlockBreakEvent(w, plyMP.gameMode.getGameModeForPlayer(), plyMP, b);
							if (xpAmount >= 0) {
								Block block = state.getBlock();
								block.playerWillDestroy(w, b, state, player);
								BlockEntity tile = w.getBlockEntity(b);

								if (state.onDestroyedByPlayer(w, b, player, true, w.getFluidState(b))) {
									block.destroy(w, b, state);
									if (!player.getAbilities().instabuild) {
										block.playerDestroy(w, player, pos, state, tile, itemstack);
										if (xpAmount > 0 && w instanceof ServerLevel sl) {
											block.popExperience(sl, b, xpAmount);
										}
									}

									w.levelEvent(2001, b, Block.getId(state));
									plyMP.connection.send(new ClientboundBlockUpdatePacket(w, b));
								}
							}
						}
					}
				}
			}
		}
		return false;
	}

	@Override
	public boolean shouldCauseBlockBreakReset(ItemStack oldStack, ItemStack newStack) {
		//ammo NBT changes while mining must not reset the block breaking progress
		return oldStack.getItem() != newStack.getItem();
	}
}
