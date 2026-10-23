package techguns.client.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Replacement for the 1.12 ModelBase used by Techguns models: holds the default texture size for new TGModelRenderers.
 * Models set textureWidth/textureHeight in their constructor BEFORE creating their parts, exactly like in 1.12.
 */
public abstract class TGModelBase {

	public int textureWidth = 64;
	public int textureHeight = 32;

	public List<TGModelRenderer> boxList = new ArrayList<>();

	protected void setRotation(TGModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}

	/**
	 * Copies the rotation angles and rotation point of one renderer to another (1.12 ModelBase.copyModelAngles)
	 */
	public static void copyModelAngles(TGModelRenderer source, TGModelRenderer dest) {
		dest.rotateAngleX = source.rotateAngleX;
		dest.rotateAngleY = source.rotateAngleY;
		dest.rotateAngleZ = source.rotateAngleZ;
		dest.rotationPointX = source.rotationPointX;
		dest.rotationPointY = source.rotationPointY;
		dest.rotationPointZ = source.rotationPointZ;
	}
}
