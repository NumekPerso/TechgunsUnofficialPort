package techguns.world;

import java.util.Random;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import techguns.world.structures.WorldgenStructure;

/**
 * The single piece of a Techguns structure: runs the 1.12 generation code for each chunk of its bounding box, with a
 * TGStructureLevel that only writes the current chunk.
 */
public class TGStructurePiece extends StructurePiece {
	private static final Logger LOGGER = LogUtils.getLogger();

	protected final String structureName;
	protected final TGSpawnData data;

	public TGStructurePiece(String structureName, TGSpawnData data, BoundingBox box) {
		super(TGWorldgen.TG_STRUCTURE_PIECE, 0, box);
		this.structureName = structureName;
		this.data = data;
	}

	public TGStructurePiece(StructurePieceSerializationContext ctx, CompoundTag tag) {
		super(TGWorldgen.TG_STRUCTURE_PIECE, tag);
		this.structureName = tag.getString("structure");
		this.data = TGSpawnData.load(tag.getCompound("data"));
	}

	@Override
	protected void addAdditionalSaveData(StructurePieceSerializationContext ctx, CompoundTag tag) {
		tag.putString("structure", structureName);
		tag.put("data", data.save());
	}

	@Override
	public void postProcess(WorldGenLevel level, StructureManager structureManager, ChunkGenerator generator, RandomSource random, BoundingBox chunkBox, ChunkPos chunkPos, BlockPos pivot) {
		TGStructureSpawn spawn = TGStructureSpawnRegister.get(structureName);
		if (spawn == null) {
			return;
		}
		BlockState defaultBlock = Blocks.STONE.defaultBlockState();
		BlockState defaultFluid = Blocks.WATER.defaultBlockState();
		if (generator instanceof NoiseBasedChunkGenerator noise) {
			defaultBlock = noise.generatorSettings().value().defaultBlock();
			defaultFluid = noise.generatorSettings().value().defaultFluid();
		}
		try {
			TGStructureLevel w = new TGStructureLevel(level, chunkBox, data.seed, data.heights, defaultBlock, defaultFluid);
			WorldgenStructure s = spawn.create();
			s.generate(w, data, new Random(data.seed));
		} catch (Exception e) {
			LOGGER.error("Techguns: error while generating structure {} in chunk {}", structureName, chunkPos, e);
		}
	}
}
