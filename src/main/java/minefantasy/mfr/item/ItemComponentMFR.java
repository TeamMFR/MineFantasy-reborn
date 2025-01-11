package minefantasy.mfr.item;

import minefantasy.mfr.api.crafting.ITieredComponent;
import minefantasy.mfr.block.BlockComponent;
import minefantasy.mfr.constants.Constants;
import minefantasy.mfr.constants.Rarity;
import minefantasy.mfr.init.MineFantasyTabs;
import minefantasy.mfr.registry.CustomMaterialRegistry;
import minefantasy.mfr.registry.types.CustomMaterialType;
import minefantasy.mfr.tile.TileEntityComponent;
import minefantasy.mfr.util.CustomToolHelper;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.IRarity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

/**
 * @author Anonymous Productions
 */
public class ItemComponentMFR extends ItemBaseMFR implements ITieredComponent {
	protected String name;
	protected Rarity itemRarity;
	// STORAGE
	String blockTexture;
	String storageType;

	private float unitCount = 1;
	private boolean isCustom = false;
	CustomMaterialType materialType = CustomMaterialRegistry.NONE.getType();

	public ItemComponentMFR(String name) {
		this(name, Rarity.COMMON);
	}

	public ItemComponentMFR(String name, Rarity rarity) {
		super(name);
		itemRarity = rarity;
		this.name = name;
		this.setCreativeTab(MineFantasyTabs.tabMaterials);
	}

	private void add(List<ItemStack> list, Item item) {
		list.add(new ItemStack(item));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack item, World world, List<String> list, ITooltipFlag flag) {

		super.addInformation(item, world, list, flag);
		if (isCustom) {
			CustomToolHelper.addComponentString(list, CustomMaterialRegistry.getMaterialFor(item, CustomToolHelper.slot_main), this.unitCount);
		}
	}

	public ItemComponentMFR setCustom(float units, CustomMaterialType type) {
		canRepair = false;
		this.unitCount = units;
		isCustom = true;
		this.materialType = type;
		return this;
	}

	public ItemComponentMFR setStoragePlacement(String type, String texture) {
		this.blockTexture = texture;
		this.storageType = type;
		return this;
	}

	protected float getWeightModifier(ItemStack stack) {
		return CustomToolHelper.getWeightModifier(stack, 1.0F);
	}

	@Override
	public IRarity getForgeRarity(ItemStack item) {
		return CustomToolHelper.getRarity(item, itemRarity);
	}

	public ItemStack construct(String main) {
		return construct(main, 1);
	}

	public ItemStack construct(String main, int stackSize) {
		return CustomToolHelper.constructSingleColoredLayer(this, main, stackSize);
	}

	@Override
	public String getItemStackDisplayName(ItemStack item) {
		if (isCustom) {
			return CustomToolHelper.getLocalisedName(item, "item.commodity_" + name + ".name");
		}
		String unlocalizedName = this.getUnlocalizedNameInefficiently(item) + ".name";
		return CustomToolHelper.getLocalisedName(item, unlocalizedName);
	}

	@Override
	public CustomMaterialType getMaterialType(ItemStack item) {
		return materialType;
	}

	@Override
	public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);
		if (storageType == null) {
			return EnumActionResult.PASS;
		}
		if (world.getTileEntity(pos) != null && !(world.getTileEntity(pos) instanceof TileEntityComponent)){
			return EnumActionResult.PASS;
		}

		//If the component item is being placed on an existing component, and that existing component is Persistence:
		//If it is an empty Persistent Component, swap it to whatever component is being placed
		TileEntity tile = world.getTileEntity(pos);
		if (tile instanceof TileEntityComponent) {
			TileEntityComponent tileComponent = (TileEntityComponent) tile;
			if (tileComponent.type.equalsIgnoreCase(Constants.StorageTextures.PERSIST_FLAG)) {
				ItemStack newItem = stack.copy();

				int max = BlockComponent.getStorageSize(storageType);
				int size = player.isSneaking() ? Math.min(newItem.getCount(), max) : 1;
				newItem.setCount(size);
				tileComponent.setItem(newItem, storageType, blockTexture, max);
				stack.shrink(size);
				return EnumActionResult.SUCCESS;
			}
		}

		EnumFacing facingForPlacement = EnumFacing.getDirectionFromEntityLiving(pos, player);

		if (facingForPlacement != EnumFacing.UP && world.getBlockState(pos).getBlock() instanceof BlockComponent){
			return EnumActionResult.FAIL;
		}

		if (!(world.getBlockState(pos).getBlock() instanceof BlockComponent) && !world.isSideSolid(pos, facingForPlacement)) {
			return EnumActionResult.FAIL;
		}

		if (player.canPlayerEdit(pos.offset(facingForPlacement), facing, stack)) {
			int size = BlockComponent.placeComponent(player, stack, world, pos.offset(facingForPlacement),
					facing, hitX, hitY, hitZ, player, hand, storageType, blockTexture);
			if (!player.capabilities.isCreativeMode){
				stack.shrink(size);
				return EnumActionResult.SUCCESS;
			}
		}
		return EnumActionResult.FAIL;
	}

	public String getBlockTexture() {
		return blockTexture;
	}

	public String getStorageType() {
		return storageType;
	}
}
