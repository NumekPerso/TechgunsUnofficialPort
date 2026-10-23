package techguns.client.particle;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class TGParticleSystemItemAttached extends TGParticleSystem {

	protected InteractionHand hand;

	protected LivingEntity elb;
	protected Player ply;

	public TGParticleSystemItemAttached(Entity entity, InteractionHand hand, TGParticleSystemType type) {
		super(entity, type);
		this.hand=hand;
		if(this.entity instanceof Player) {
			this.ply = (Player) this.entity;
			this.elb=null;
		} else {
			this.elb= this.entity instanceof LivingEntity ? (LivingEntity) this.entity : null;
			this.ply=null;
		}
		this.itemAttached=true;
		float s = 1.0f;
		this.x= type.offset.x;
		this.y= type.offset.y;
		this.z= type.offset.z;
		this.xo = this.x;
		this.yo = this.y;
		this.zo = this.z;
		this.scale=s;
	}

	/**
	 * 1.12: players stored the particles in TGExtendedPlayerClient, NPCs in TGShooterValues; 1.20: TGFXHandler.getParticleAttachments
	 */
	@Override
	protected void addEffect(ITGParticle s) {
		List<ITGParticle> list = new ArrayList<>();
		s.setItemAttached();
		list.add(s);
		LivingEntity holder = this.ply != null ? this.ply : this.elb;
		if (holder != null) {
			ITGParticleAttachments props = TGFXHandler.getParticleAttachments(holder);
			if (props != null) {
				props.addEffectHand(hand, list);
			}
		}
	}


}
