package techguns.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import techguns.Techguns;

/**
 * 1.12 "military_crate", flattened: one block per EnumMilitaryCrateType (military_crate_ammo, ...).
 * 1.12 replaced the drops by the crate's loot table in a HarvestDropsEvent (when harvested by a player without silk touch),
 * the port does it in the block loot table (data/techguns/loot_tables/blocks/military_crate_*.json), which uses the
 * content loot tables of 1.12 (data/techguns/loot_tables/crates/military_crate_*.json, see getLootableForState()).
 */
public class BlockMilitaryCrate extends GenericBlock {

	protected static final ResourceLocation loottable_ammo = new ResourceLocation(Techguns.MODID, "crates/military_crate_ammo");
	protected static final ResourceLocation loottable_gun = new ResourceLocation(Techguns.MODID, "crates/military_crate_gun");
	protected static final ResourceLocation loottable_armor = new ResourceLocation(Techguns.MODID, "crates/military_crate_armor");
	protected static final ResourceLocation loottable_medical = new ResourceLocation(Techguns.MODID, "crates/military_crate_medical");
	protected static final ResourceLocation loottable_explosives = new ResourceLocation(Techguns.MODID, "crates/military_crate_explosives");
	protected static final ResourceLocation loottable_generic = new ResourceLocation(Techguns.MODID, "crates/military_crate_generic");

	protected static final VoxelShape boundingbox = Shapes.box(0.03125, 0, 0.03125, 0.96875, 1, 0.96875);

	protected final EnumMilitaryCrateType crateType;

	public BlockMilitaryCrate(EnumMilitaryCrateType type) {
		super(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).instrument(NoteBlockInstrument.BASS).sound(SoundType.WOOD).strength(4.0f).noOcclusion().ignitedByLava());
		this.crateType = type;
	}

	public EnumMilitaryCrateType getCrateType() {
		return crateType;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return boundingbox;
	}

	public ResourceLocation getLootableForState(BlockState state) {
		switch (this.crateType) {
		case AMMO:
			return loottable_ammo;
		case ARMOR:
			return loottable_armor;
		case EXPLOSIVE:
			return loottable_explosives;
		case GUN:
			return loottable_gun;
		case MEDICAL:
			return loottable_medical;
		default:
			return loottable_generic;
		}
	}
}
