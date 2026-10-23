package techguns.world.structures;

import java.util.Random;

import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import techguns.TGBlocks;
import techguns.blocks.BlockTGCamoNet;
import techguns.blocks.BlockTGLamp;
import techguns.blocks.EnumCamoNetType;
import techguns.blocks.EnumLampType;
import techguns.util.MBlock;
import techguns.world.EnumLootType;
import techguns.world.StructureBlockUtils;

public class CampProps extends WorldgenStructure {

	MBlock groundBlock = new MBlock("minecraft:gravel", 0);
	BlockState groundstate = groundBlock.getState();
	MBlock[] crateBlocks = MBlockRegister.crates;
	MBlock sandbags = new MBlock("techguns:sandbags", 0);
	BlockState sandbagsState = sandbags.getState();
	BlockState lampstate = TGBlocks.LAMP_0.defaultBlockState().setValue(TGBlocks.LAMP_TYPE, EnumLampType.YELLOW_LANTERN);

	int variant = -1;

	public CampProps(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
		super(minX, minY, minZ, maxX, maxY, maxZ);
		this.lootTier = EnumLootType.TIER1;
	}

	public CampProps(int minX, int minY, int minZ, int maxX, int maxY, int maxZ, int variant) {
		this(minX, minY, minZ, maxX, maxY, maxZ);
		this.variant = variant;
		this.lootTier = EnumLootType.TIER1;
	}

	@Override
	public void setBlocks(LevelAccessor world, int posX, int posY, int posZ, int sizeX, int sizeY, int sizeZ, int direction, BiomeColorType colorType, Random rnd) {

		EnumCamoNetType camoMeta;
		switch (colorType) {
		case DESERT:
			camoMeta = EnumCamoNetType.DESERT;
			break;
		case SNOW:
			camoMeta = EnumCamoNetType.SNOW;
			break;
		case WOODLAND:
		default:
			camoMeta = EnumCamoNetType.WOOD;
			break;
		}

		int variant = this.variant;
		if (variant == -1)
			variant = rnd.nextInt(2);

		switch (variant) {
		case 0: //pile of crates
		default:
			setCrateBlocks(world, posX, posY, posZ, sizeX, sizeY, sizeZ, rnd);
			break;
		case 1: //crate + roof
			setRoofBlocks(world, posX, posY, posZ, sizeX, sizeY, sizeZ, camoMeta);
			setCrateBlocks(world, posX + 1, posY, posZ + 1, sizeX - 2, sizeY - 2, sizeZ - 2, rnd);
			break;
		case 2: //Sandbags
			setSandbagBlocks(world, posX, posY, posZ, sizeX, sizeY, sizeZ, direction);
			break;
		case 3: //Sandbags + roof
			setRoofBlocks(world, posX, posY + 1, posZ, sizeX, sizeY - 1, sizeZ, camoMeta);
			setSandbagBlocks(world, posX, posY, posZ, sizeX, sizeY, sizeZ, direction);
			break;
		case 4: //row of sandbags
			setSandbagRow(world, posX, posY, posZ, sizeX, sizeY, sizeZ, direction);
			break;
		case 5: //Sandbags + roof
			setRoofBlocks(world, posX, posY + 1, posZ, sizeX, sizeY - 1, sizeZ, camoMeta);
			setSandbagBlocks(world, posX, posY, posZ, sizeX, sizeY, sizeZ, direction);
			setCrateBlocks(world, posX + 1, posY, posZ + 1, sizeX - 2, sizeY - 2, sizeZ - 2, rnd);
			break;
		}
	}

	private void setSandbagRow(LevelAccessor world, int px, int py, int pz, int sizeX, int sizeY, int sizeZ, int direction) {
		MutableBlockPos pos = new MutableBlockPos();
		int x = 0;
		int z = 0;
		int i = 1;
		for (int y = 1; y < sizeY; y++) {
			if (sizeX > sizeZ) {
				z = sizeZ / 2;
				for (x = i; x < sizeX - i; x++) {
					world.setBlock(pos.set(px + x, py + y, pz + z), sandbagsState, 2);
				}
			} else {
				x = sizeX / 2;
				for (z = i; z < sizeZ - i; z++) {
					world.setBlock(pos.set(px + x, py + y, pz + z), sandbagsState, 2);
				}
			}
			i++;
		}
	}

	private void setSandbagBlocks(LevelAccessor world, int posX, int posY, int posZ, int sizeX, int sizeY, int sizeZ, int direction) {
		MutableBlockPos p = new MutableBlockPos();
		for (int x = 0; x < sizeX; x++) {
			for (int z = 0; z < sizeZ; z++) {
				if (x == 0 || x == sizeX - 1 || z == 0 || z == sizeZ - 1) {
					world.setBlock(p.set(posX + x, posY + 1, posZ + z), sandbagsState, 2);
				}
			}
		}
		int xoffset, zoffset; //Door/center positions
		switch (direction) {
		case 0:
			xoffset = 0;
			zoffset = (sizeZ / 2);
			break;
		case 1:
			xoffset = (sizeX / 2);
			zoffset = 0;
			break;
		case 2:
			xoffset = sizeX - 1;
			zoffset = (sizeZ / 2);
			break;
		case 3:
		default:
			xoffset = (sizeX / 2);
			zoffset = sizeZ - 1;
			break;
		}
		//opening
		StructureBlockUtils.setBlockToAir(world, p.set(posX + xoffset, posY + 1, posZ + zoffset));
	}

	private void setRoofBlocks(LevelAccessor world, int posX, int posY, int posZ, int sizeX, int sizeY, int sizeZ, EnumCamoNetType camo) {
		MutableBlockPos p = new MutableBlockPos();
		StructureBlockUtils.fillBlocks(world, p.set(posX, posY + 1, posZ), 1, sizeY - 2, 1, Blocks.OAK_FENCE.defaultBlockState());
		StructureBlockUtils.fillBlocks(world, p.set(posX + sizeX - 1, posY + 1, posZ), 1, sizeY - 2, 1, Blocks.OAK_FENCE.defaultBlockState());
		StructureBlockUtils.fillBlocks(world, p.set(posX, posY + 1, posZ + sizeZ - 1), 1, sizeY - 2, 1, Blocks.OAK_FENCE.defaultBlockState());
		StructureBlockUtils.fillBlocks(world, p.set(posX + sizeX - 1, posY + 1, posZ + sizeZ - 1), 1, sizeY - 2, 1, Blocks.OAK_FENCE.defaultBlockState());
		StructureBlockUtils.fillBlocks(world, p.set(posX, posY + sizeY - 1, posZ), sizeX, 1, sizeZ, TGBlocks.CAMONET_TOP.defaultBlockState().setValue(BlockTGCamoNet.CAMO_TYPE, camo));
	}

	public void setCrateBlocks(LevelAccessor world, int posX, int posY, int posZ, int sizeX, int sizeY, int sizeZ, Random rnd) {
		MutableBlockPos p = new MutableBlockPos();
		for (int x = 0; x < sizeX; x++) {
			for (int z = 0; z < sizeZ; z++) {
				world.setBlock(p.set(posX + x, posY, posZ + z), groundstate, 2);
				//roll for special chest
				rnd.nextFloat();
				for (int y = 1; y < sizeY; y++) {
					if (!isFreeSpace(world, posX + x, posY + y - 1, posZ + z) && rnd.nextFloat() > 0.5f) {
						int index = rnd.nextInt(crateBlocks.length);
						world.setBlock(p.set(posX + x, posY + y, posZ + z), crateBlocks[index].getState(), 2);
					}
				}
			}
		}

		if (((sizeX + sizeZ) / 2.0f) / 6.0f > rnd.nextFloat()) { //place lantern
			for (int y = sizeY; y > 1; y--) {
				for (int t = 0; t < 3; t++) {
					int x = rnd.nextInt(Math.max(1, sizeX));
					int z = rnd.nextInt(Math.max(1, sizeZ));
					if (world.isEmptyBlock(p.set(posX + x, posY + y, posZ + z)) && !isFreeSpace(world, posX + x, posY + y - 1, posZ + z)) {
						world.setBlock(p, lampstate, 2);
						return;
					}
				}
			}
		}
	}

	public boolean isFreeSpace(LevelAccessor world, int x, int y, int z) {
		MutableBlockPos p = new MutableBlockPos(x, y, z);
		BlockState s = world.getBlockState(p);
		return s.isAir() || s.is(Blocks.SNOW) || s.getBlock() instanceof BlockTGLamp;
	}
}
