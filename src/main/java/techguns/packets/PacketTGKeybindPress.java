package techguns.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import techguns.TGPackets;
import techguns.TGSounds;
import techguns.capabilities.TGExtendedPlayer;
import techguns.items.armors.GenericArmor;
import techguns.items.armors.TGArmorBonus;
import techguns.items.guns.GenericGun;
import techguns.keybind.TGKeybindsID;

public class PacketTGKeybindPress extends TGPacket {
	public byte buttonID;
	public InteractionHand hand = InteractionHand.MAIN_HAND;
	public boolean showMsg = false;

	public PacketTGKeybindPress(byte buttonID) {
		this.buttonID = buttonID;
	}

	public PacketTGKeybindPress(byte buttonID, InteractionHand hand) {
		this(buttonID);
		this.hand = hand;
	}

	public PacketTGKeybindPress(byte buttonID, boolean showMsg) {
		this(buttonID);
		this.showMsg = showMsg;
	}

	public PacketTGKeybindPress(FriendlyByteBuf buf) {
		this.buttonID = buf.readByte();
		this.hand = InteractionHand.values()[buf.readByte()];
		this.showMsg = buf.readBoolean();
	}

	@Override
	public void encode(FriendlyByteBuf buf) {
		buf.writeByte(buttonID);
		buf.writeByte((byte) hand.ordinal());
		buf.writeBoolean(showMsg);
	}

	@Override
	protected void handle(Player ply) {
		TGExtendedPlayer props = TGExtendedPlayer.get(ply);
		if (props == null || !(ply instanceof ServerPlayer sply)) {
			return;
		}

		if (this.buttonID == TGKeybindsID.TOGGLE_NIGHTVISION) {
			props.enableNightVision = !props.enableNightVision;
			if (!props.enableNightVision) {
				if (ply.getEffect(MobEffects.NIGHT_VISION) != null) {
					ply.removeEffect(MobEffects.NIGHT_VISION);
					ply.level().playSound(null, ply.getX(), ply.getEyeY(), ply.getZ(), TGSounds.NIGHTVISION_OFF, SoundSource.PLAYERS, 1f, 1f);
				}
			} else if (GenericArmor.getArmorBonusForPlayer(ply, TGArmorBonus.NIGHTVISION, false) > 0.0f) {
				ply.level().playSound(null, ply.getX(), ply.getEyeY(), ply.getZ(), TGSounds.NIGHTVISION_ON, SoundSource.PLAYERS, 1f, 1f);
			}
			if (this.showMsg) {
				TGKeybindsID.showConfirmMessage(sply, this.buttonID, props.enableNightVision);
			}
			TGPackets.network.sendTo(new PacketTGExtendedPlayerSync(ply, props), sply);

		} else if (this.buttonID == TGKeybindsID.TOGGLE_SAFEMODE) {
			props.enableSafemode = !props.enableSafemode;
			if (!props.enableSafemode && !TGExtendedPlayer.canUseUnsafeMode(ply)) {
				props.enableSafemode = true;
			}
			if (this.showMsg) {
				TGKeybindsID.showConfirmMessage(sply, this.buttonID, props.enableSafemode);
			}
			TGPackets.network.sendTo(new PacketTGExtendedPlayerSync(ply, props), sply);

		} else if (this.buttonID == TGKeybindsID.TOGGLE_JETPACK) {
			if (!props.isJumpkeyPressed()) {
				props.enableJetpack = !props.enableJetpack;
				TGPackets.network.sendTo(new PacketTGPlayerFieldSync(ply, PacketTGPlayerFieldSync.FIELD_ENABLEJETPACK, props.enableJetpack), sply);
				if (this.showMsg) {
					TGKeybindsID.showConfirmMessage(sply, this.buttonID, props.enableJetpack);
				}

				if (!props.enableJetpack && props.isJumpkeyPressed()) {
					props.setJumpkeyPressed(false);
					TGPackets.network.sendToDimension(new PacketTGPlayerFieldSync(ply, PacketTGPlayerFieldSync.FIELD_JUMPBUTTONPRESSED, false), ply.level().dimension());
				}
			}

		} else if (this.buttonID == TGKeybindsID.JETPACK_BOOST_START) {
			props.setJumpkeyPressed(true);
			//send change to other player
			TGPackets.network.sendToDimension(new PacketTGPlayerFieldSync(ply, PacketTGPlayerFieldSync.FIELD_JUMPBUTTONPRESSED, true), ply.level().dimension());

		} else if (this.buttonID == TGKeybindsID.JETPACK_BOOST_STOP) {
			props.setJumpkeyPressed(false);
			TGPackets.network.sendToDimension(new PacketTGPlayerFieldSync(ply, PacketTGPlayerFieldSync.FIELD_JUMPBUTTONPRESSED, false), ply.level().dimension());

		} else if (this.buttonID == TGKeybindsID.JETPACK_FORWARD_START) {
			props.isForwardKeyPressed = true;

		} else if (this.buttonID == TGKeybindsID.JETPACK_FORWARD_STOP) {
			props.isForwardKeyPressed = false;

		} else if (this.buttonID == TGKeybindsID.FORCE_RELOAD) {
			ItemStack item = ply.getItemInHand(this.hand);
			if (!item.isEmpty() && item.getItem() instanceof GenericGun gun) {
				gun.tryForcedReload(item, ply.level(), ply, this.hand);
			}

		} else if (this.buttonID == TGKeybindsID.TOGGLE_HUD) {
			props.showTGHudElements = !props.showTGHudElements;
			TGPackets.network.sendTo(new PacketTGExtendedPlayerSync(ply, props), sply);

		} else if (this.buttonID == TGKeybindsID.TOGGLE_STEP_ASSIST) {
			props.enableStepAssist = !props.enableStepAssist;
			if (this.showMsg) {
				TGKeybindsID.showConfirmMessage(sply, this.buttonID, props.enableStepAssist);
			}
			TGPackets.network.sendTo(new PacketTGExtendedPlayerSync(ply, props), sply);
		}
	}
}
