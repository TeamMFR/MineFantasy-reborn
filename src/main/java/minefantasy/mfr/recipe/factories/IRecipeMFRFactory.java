package minefantasy.mfr.recipe.factories;

import com.google.gson.JsonObject;
import minefantasy.mfr.recipe.IRecipeMFR;
import net.minecraftforge.common.crafting.JsonContext;

public interface IRecipeMFRFactory<T extends IRecipeMFR> {

	T parse(JsonContext context, JsonObject json);
}
