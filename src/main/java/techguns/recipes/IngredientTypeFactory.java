package techguns.recipes;

import net.minecraftforge.common.crafting.IIngredientSerializer;

/**
 * Name of a custom ingredient type and its serializer class (1.12 recipe writer helper).
 */
public class IngredientTypeFactory {
	protected String factoryname;
	protected String typename;

	public IngredientTypeFactory(String typename, Class<? extends IIngredientSerializer<?>> clazz) {
		this.factoryname = clazz.getName();
		this.typename = typename;
	}

	public String getFactoryname() {
		return factoryname;
	}

	public String getTypename() {
		return typename;
	}

}
