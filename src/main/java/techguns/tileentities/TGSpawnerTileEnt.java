package techguns.tileentities;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.UUID;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import techguns.TGBlocks;
import techguns.Techguns;

/**
 * NPC spawner of the structures (1.12 TGSpawnerTileEnt).
 * 1.20: mob types are entity registry names (techguns namespace when none is given), resolved when spawning, unknown
 * types are skipped. The spawned NPCs are tracked by UUID; the spawner position is stored in the NPC's persistent
 * data (TGSpawnerNPCData, lot J) and the NPC notifies the spawner when it dies, despawns or is loaded again (ITGNpcSpawner).
 */
public class TGSpawnerTileEnt extends BasicTGTileEntity implements techguns.api.npc.ITGNpcSpawner {

	public static final String SPAWNER_TAG = "techguns_spawner";

	protected RandomSource rand = RandomSource.create();
	protected int delay = 200;
	protected int spawndelay = 200;
	protected int mobsLeft = 5;
	protected int maxActive = 3;

	protected int spawnHeightOffset = 0;

	protected double spawnrange = 2d;

	protected ArrayList<MobEntry> mobtypes = new ArrayList<>();

	protected LinkedList<UUID> activeMobs = new LinkedList<>();

	protected ItemStack weaponOverride = ItemStack.EMPTY;

	public static class MobEntry {
		public final String id;
		public final int weight;

		public MobEntry(String id, int weight) {
			this.id = id;
			this.weight = weight;
		}
	}

	public TGSpawnerTileEnt(BlockPos pos, BlockState state) {
		super(TGBlocks.MONSTER_SPAWNER_TILEENT, pos, state, false);
	}

	/**
	 * @param name entity registry name, "techguns:" is added when there is no namespace
	 */
	public void addMobType(String name, int weight) {
		if (!name.contains(":")) {
			name = Techguns.MODID + ":" + name;
		}
		this.mobtypes.add(new MobEntry(name, weight));
		this.setChanged();
	}

	public void killedEntity(UUID ent) {
		if (this.activeMobs.remove(ent)) {
			this.mobsLeft--;
			this.setChanged();
		}
	}

	public void despawnedEntity(UUID ent) {
		if (this.activeMobs.remove(ent)) {
			this.setChanged();
		}
	}

	public void relinkNPC(UUID ent) {
		if (!this.activeMobs.contains(ent)) {
			this.activeMobs.add(ent);
		}
	}

	@Override
	public void killedEntity(techguns.entities.npcs.ITGSpawnerNPC ent) {
		if (ent instanceof Entity e) {
			killedEntity(e.getUUID());
		}
	}

	@Override
	public void despawnedEntity(techguns.entities.npcs.ITGSpawnerNPC ent) {
		if (ent instanceof Entity e) {
			despawnedEntity(e.getUUID());
		}
	}

	@Override
	public void relinkNPC(techguns.entities.npcs.ITGSpawnerNPC ent) {
		if (ent instanceof Entity e) {
			relinkNPC(e.getUUID());
		}
	}

	@Override
	public boolean canBeWrenchRotated() {
		return false;
	}

	public void setParams(int mobsleft, int maxactive, int spawndelay, int spawnrange) {
		this.mobsLeft = mobsleft;
		this.maxActive = maxactive;
		this.spawndelay = spawndelay;
		this.delay = spawndelay;
		this.spawnrange = spawnrange;
		this.setChanged();
	}

	public TGSpawnerTileEnt setWeaponOverride(ItemStack weapon) {
		this.weaponOverride = weapon;
		return this;
	}

	public void setSpawnHeightOffset(int offset) {
		this.spawnHeightOffset = offset;
	}

	@Override
	protected void saveAdditional(CompoundTag compound) {
		super.saveAdditional(compound);
		compound.putByte("mobsLeft", (byte) this.mobsLeft);
		compound.putShort("delay", (short) this.delay);
		compound.putByte("maxActive", (byte) this.maxActive);
		compound.putShort("spawnDelay", (short) this.spawndelay);
		compound.putByte("spawnRange", (byte) this.spawnrange);
		compound.putShort("spawnHeightOffset", (short) this.spawnHeightOffset);

		ListTag list = new ListTag();
		for (MobEntry type : this.mobtypes) {
			CompoundTag t = new CompoundTag();
			t.putString("id", type.id);
			t.putInt("weight", type.weight);
			list.add(t);
		}
		compound.put("mobtypes", list);

		ListTag active = new ListTag();
		for (UUID u : this.activeMobs) {
			active.add(NbtUtils.createUUID(u));
		}
		compound.put("active", active);

		if (!weaponOverride.isEmpty()) {
			compound.put("weapon", this.weaponOverride.save(new CompoundTag()));
		}
	}

	@Override
	public void load(CompoundTag compound) {
		super.load(compound);
		this.mobsLeft = compound.getByte("mobsLeft");
		this.delay = compound.getShort("delay");
		this.spawndelay = compound.getShort("spawnDelay");
		if (spawndelay < 1) {
			spawndelay = 200;
		}
		this.maxActive = compound.getByte("maxActive");
		if (maxActive < 1) {
			maxActive = 1;
		}
		this.spawnrange = compound.getByte("spawnRange");
		this.spawnHeightOffset = compound.getShort("spawnHeightOffset");

		if (compound.contains("mobtypes", Tag.TAG_LIST)) {
			ListTag list = compound.getList("mobtypes", Tag.TAG_COMPOUND);
			this.mobtypes.clear();
			for (int i = 0; i < list.size(); i++) {
				CompoundTag t = list.getCompound(i);
				this.mobtypes.add(new MobEntry(t.getString("id"), Math.max(1, t.getInt("weight"))));
			}
		}
		this.activeMobs.clear();
		if (compound.contains("active", Tag.TAG_LIST)) {
			ListTag active = compound.getList("active", Tag.TAG_INT_ARRAY);
			for (Tag t : active) {
				this.activeMobs.add(NbtUtils.loadUUID(t));
			}
		}

		if (compound.contains("weapon", Tag.TAG_COMPOUND)) {
			this.weaponOverride = ItemStack.of(compound.getCompound("weapon"));
		}
	}

	protected boolean hasMobTypes() {
		return this.mobtypes.size() > 0;
	}

	protected MobEntry getRandomMob() {
		int total = 0;
		for (MobEntry e : mobtypes) {
			total += e.weight;
		}
		int roll = rand.nextInt(Math.max(1, total));
		for (MobEntry e : mobtypes) {
			roll -= e.weight;
			if (roll < 0) {
				return e;
			}
		}
		return mobtypes.get(0);
	}

	@Override
	public void update() {
		if (this.level == null || this.level.isClientSide) {
			return;
		}
		ServerLevel world = (ServerLevel) this.level;
		this.delay--;

		if (this.delay <= 0 && activeMobs.size() < Math.min(maxActive, mobsLeft) && this.hasMobTypes()) {

			if (world.getDifficulty() != Difficulty.PEACEFUL) {

				MobEntry entdata = getRandomMob();
				ResourceLocation id = ResourceLocation.tryParse(entdata.id);
				EntityType<?> type = id != null && ForgeRegistries.ENTITY_TYPES.containsKey(id) ? ForgeRegistries.ENTITY_TYPES.getValue(id) : null;

				BlockPos blockpos = this.getBlockPos();

				double d0 = (double) blockpos.getX() + (rand.nextDouble() - rand.nextDouble()) * this.spawnrange + 0.5D;
				double d1 = (double) (blockpos.getY() + 1 + this.spawnHeightOffset);
				double d2 = (double) blockpos.getZ() + (rand.nextDouble() - rand.nextDouble()) * this.spawnrange + 0.5D;
				Entity entity = type != null ? type.create(world) : null;

				if (entity instanceof Mob elb) {
					elb.moveTo(d0, d1, d2, rand.nextFloat() * 360.0F, 0.0F);
					net.minecraftforge.event.ForgeEventFactory.onFinalizeSpawn(elb, world, world.getCurrentDifficultyAt(elb.blockPosition()), MobSpawnType.SPAWNER, null, null);
					if (elb instanceof PathfinderMob pm) {
						pm.restrictTo(blockpos, 10);
					}
					if (elb instanceof techguns.entities.npcs.ITGSpawnerNPC npc) {
						techguns.entities.npcs.TGSpawnerNPCData.get(npc).setSpawnerPos(blockpos);
					}
					elb.setPersistenceRequired();

					if (!weaponOverride.isEmpty()) {
						elb.setItemSlot(EquipmentSlot.MAINHAND, this.weaponOverride.copy());
					}

					world.addFreshEntityWithPassengers(elb);
					world.levelEvent(2004, blockpos, 0);
					elb.spawnAnim();

					this.activeMobs.add(elb.getUUID());
					this.setChanged();
				}
				this.delay = this.spawndelay;
			} else {
				this.delay = this.spawndelay;
			}
		} else {
			if (this.delay <= 0) {
				this.delay = this.spawndelay;

				Iterator<UUID> it = this.activeMobs.iterator();
				while (it.hasNext()) {
					Entity ent = world.getEntity(it.next());
					if (!(ent instanceof LivingEntity) || !ent.isAlive()) {
						it.remove();
					}
				}
			}
		}

		if (this.mobsLeft <= 0) {
			world.setBlock(this.getBlockPos(), Blocks.AIR.defaultBlockState(), 3);
		}
	}

}
