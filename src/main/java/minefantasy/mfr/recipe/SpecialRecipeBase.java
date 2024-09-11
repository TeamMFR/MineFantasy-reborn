package minefantasy.mfr.recipe;

import minefantasy.mfr.constants.Skill;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.registries.IForgeRegistryEntry;

public  class SpecialRecipeBase extends IForgeRegistryEntry.Impl<SpecialRecipeBase> implements IRecipeMFR {
	protected Ingredient input;
	protected Ingredient specialInput;
	protected ItemStack output;
	protected String research;
	protected String design;

	public SpecialRecipeBase(Ingredient input, Ingredient specialInput, ItemStack output, String research, String design) {
		this.input = input;
		this.specialInput = specialInput;
		this.output = output;
		this.research = research;
		this.design = design;
	}

	public boolean matches(ItemStack recipeInput, ItemStack specialInput) {
		return input.apply(recipeInput) && this.specialInput.apply(specialInput);
	}

	public Ingredient getInput() {
		return input;
	}

	public Ingredient getSpecialInput() {
		return specialInput;
	}

	public ItemStack getOutput() {
		return output;
	}

	public String getRequiredResearch() {
		return research;
	}

	public String getDesign() {
		return design;
	}

	@Override
	public String getResourceLocation() {
		return this.getRegistryName() != null ? this.getRegistryName().toString() : "";
	}

	@Override
	public Skill getSkill() {
		return null;
	}

	@Override
	public int getSkillXp() {
		return 0;
	}

	@Override
	public boolean shouldSlotGiveSkillXp() {
		return false;
	}

	@Override
	public float getVanillaXp() {
		return 0;
	}
}
