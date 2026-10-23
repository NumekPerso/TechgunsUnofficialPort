package techguns.items.additionalslots;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.network.PacketDistributor.TargetPoint;
import techguns.TGItems;
import techguns.TGPackets;
import techguns.TGSounds;
import techguns.api.tginventory.TGSlotType;
import techguns.capabilities.TGExtendedPlayer;
import techguns.items.armors.TGArmorBonus;
import techguns.packets.PacketPlaySound;
import techguns.packets.PacketSpawnParticle;
import techguns.packets.PacketSpawnParticleOnEntity;
import techguns.util.TGSoundCategory;

public class ItemJumpPack extends ItemTGSpecialSlotAmmo {

	private static int durPerUse = 1;

	private static final float JUMPBOOST = 0.8f;
	private static final float FREEHEIGHT = 8.0f;
	private static final float FALLDMG = 0.2f;

	public ItemJumpPack(String unlocalizedName, int camoCount, int dur) {
		super(unlocalizedName, TGSlotType.BACKSLOT, camoCount, dur, TGItems.COMPRESSED_AIR_TANK, TGItems.COMPRESSED_AIR_TANK_EMPTY);
	}

	@Override
	public void onPlayerTick(ItemStack item, PlayerTickEvent event) {
		if ((item.getDamageValue() + durPerUse > item.getMaxDamage())) {
			this.tryReloadAndRepair(item, event.player);
		}
	}

	@Override
	public float getBonus(TGArmorBonus type, ItemStack stack, boolean consume, Player player) {
		if (type == TGArmorBonus.JUMP) {

			if (consume) {
				TGExtendedPlayer props = TGExtendedPlayer.get(player);

				if (props.enableJetpack && (stack.getDamageValue() + durPerUse) <= stack.getMaxDamage()) {

					stack.setDamageValue(stack.getDamageValue() + durPerUse);
					if (!player.level().isClientSide) {
						this.doJumpEffect(player);
					}
					return JUMPBOOST;
				}
			} else {
				if ((stack.getDamageValue() + durPerUse) <= stack.getMaxDamage()) {
					return JUMPBOOST;
				}
			}
		} else if (type == TGArmorBonus.FREEHEIGHT) {
			if (stack.getDamageValue() < stack.getMaxDamage()) {
				return FREEHEIGHT;
			}
		} else if (type == TGArmorBonus.FALLDMG) {
			if (consume) {

				TGExtendedPlayer props = TGExtendedPlayer.get(player);

				if (props.enableJetpack && (stack.getDamageValue() + durPerUse) <= stack.getMaxDamage()) {
					stack.setDamageValue(stack.getDamageValue() + durPerUse);
					if (!player.level().isClientSide) {
						this.doFallEffect(player);
					}
					return FALLDMG;
				}

			} else {
				if (stack.getDamageValue() < stack.getMaxDamage()) {
					return FALLDMG;
				}
			}
		}
		return 0;
	}

	public void doJumpEffect(Player ply) {
		//send out the effects
		if (!ply.level().isClientSide) {
			TargetPoint targetPoint = TGPackets.targetPointAroundEnt(ply, 50);

			TGPackets.network.sendToAllAround(new PacketSpawnParticleOnEntity("JumpPackBoost", ply), targetPoint);
			TGPackets.network.sendToAllAround(new PacketPlaySound(TGSounds.STEAM_JUMP_2, ply, 1.0f, 1.0f, false, true, false, true, TGSoundCategory.PLAYER_EFFECT), targetPoint);
		}
	}

	public void doFallEffect(Player ply) {
		if (!ply.level().isClientSide) {
			TGPackets.network.sendToAllAround(new PacketSpawnParticle("JumpPackFall", ply.getX(), ply.getY(), ply.getZ()), TGPackets.targetPointAroundEnt(ply, 50));
		}
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
		super.appendHoverText(stack, level, tooltip, flag);
		tooltip.add(Component.translatable("techguns.armorTooltip.jumpheight").append(": +" + JUMPBOOST));
		tooltip.add(Component.translatable("techguns.armorTooltip.falldamage").append(": -" + FALLDMG * 100.0f + "%"));
		tooltip.add(Component.translatable("techguns.armorTooltip.fallheight").append(": -" + FREEHEIGHT));
	}

}
