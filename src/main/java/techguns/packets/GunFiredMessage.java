package techguns.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import techguns.client.ShooterValues;

public class GunFiredMessage extends TGPacket {
	protected int entityID;
	protected int time;
	protected byte attackType;
	protected boolean checkRecoil;
	protected boolean offhand;
	protected int muzzleflashtime;

	public GunFiredMessage(LivingEntity shooter, int firetime, int muzzleflashtime, int attackType, boolean checkRecoil, boolean offhand) {
		this.entityID = shooter.getId();
		this.time = firetime;
		this.muzzleflashtime = muzzleflashtime;
		this.attackType = (byte) attackType;
		this.checkRecoil = checkRecoil;
		this.offhand = offhand;
	}

	public GunFiredMessage(FriendlyByteBuf buf) {
		entityID = buf.readInt();
		time = buf.readInt();
		muzzleflashtime = buf.readInt();
		attackType = buf.readByte();
		checkRecoil = buf.readBoolean();
		offhand = buf.readBoolean();
	}

	@Override
	public void encode(FriendlyByteBuf buf) {
		buf.writeInt(entityID);
		buf.writeInt(time);
		buf.writeInt(muzzleflashtime);
		buf.writeByte(attackType);
		buf.writeBoolean(checkRecoil);
		buf.writeBoolean(offhand);
	}

	@Override
	protected void handle(Player ply) {
		Entity ent = ply.level().getEntity(this.entityID);
		if (ent instanceof LivingEntity shooter) {
			if (!this.checkRecoil || !ShooterValues.isStillRecoiling(shooter, this.offhand, this.attackType)) {
				ShooterValues.setRecoiltime(shooter, this.offhand, System.currentTimeMillis() + this.time, this.time, this.attackType);
				ShooterValues.setMuzzleFlashTime(shooter, this.offhand, System.currentTimeMillis() + this.muzzleflashtime, this.muzzleflashtime);
			}
		}
	}
}
