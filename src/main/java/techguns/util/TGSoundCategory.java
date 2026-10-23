package techguns.util;

import java.util.ArrayList;

import net.minecraft.sounds.SoundSource;

public class TGSoundCategory {

	protected static ArrayList<TGSoundCategory> CATEGORIES = new ArrayList<>();

	public static TGSoundCategory GUN_FIRE = new TGSoundCategory("gunfire", SoundSource.PLAYERS);
	public static TGSoundCategory PLAYER_EFFECT = new TGSoundCategory("playereffect", SoundSource.PLAYERS);
	public static TGSoundCategory RELOAD = new TGSoundCategory("reload", SoundSource.PLAYERS);
	public static TGSoundCategory EXPLOISON = new TGSoundCategory("explosion", SoundSource.MASTER);
	public static TGSoundCategory MACHINE = new TGSoundCategory("machine", SoundSource.BLOCKS);
	public static TGSoundCategory DEATHEFFECT = new TGSoundCategory("deathFX", SoundSource.HOSTILE);
	public static TGSoundCategory HOSTILE = new TGSoundCategory("hostile", SoundSource.HOSTILE);

	protected int id;
	protected String name;
	protected SoundSource vanillaCategory;

	public TGSoundCategory(String name, SoundSource vanillaCategory) {
		this.id = CATEGORIES.size();
		this.name = name;
		this.vanillaCategory = vanillaCategory;
		CATEGORIES.add(this);
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public SoundSource getVanillaCategory() {
		return vanillaCategory;
	}

	public static TGSoundCategory get(int index) {
		if (index >= 0 && index < CATEGORIES.size()) {
			return CATEGORIES.get(index);
		}
		return GUN_FIRE;
	}
}
