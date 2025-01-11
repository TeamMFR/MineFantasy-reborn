package minefantasy.mfr;

import com.google.common.base.CaseFormat;
import minefantasy.mfr.api.armour.ISpecialArmourMFR;
import minefantasy.mfr.api.crafting.CustomCrafterEntry;
import minefantasy.mfr.api.farming.FarmingHelper;
import minefantasy.mfr.api.heating.IHotItem;
import minefantasy.mfr.api.heating.TongsHelper;
import minefantasy.mfr.api.stamina.CustomFoodEntry;
import minefantasy.mfr.api.tool.IHuntingItem;
import minefantasy.mfr.api.tool.ISmithTongs;
import minefantasy.mfr.api.weapon.IParryable;
import minefantasy.mfr.block.BlockComponent;
import minefantasy.mfr.client.ClientItemsMFR;
import minefantasy.mfr.config.ConfigArmour;
import minefantasy.mfr.config.ConfigClient;
import minefantasy.mfr.config.ConfigHardcore;
import minefantasy.mfr.config.ConfigMobs;
import minefantasy.mfr.config.ConfigSpecials;
import minefantasy.mfr.config.ConfigStamina;
import minefantasy.mfr.config.ConfigWeapon;
import minefantasy.mfr.constants.Constants;
import minefantasy.mfr.constants.Skill;
import minefantasy.mfr.constants.Tool;
import minefantasy.mfr.constants.WeaponClass;
import minefantasy.mfr.container.ContainerAnvil;
import minefantasy.mfr.container.ContainerForge;
import minefantasy.mfr.data.PlayerData;
import minefantasy.mfr.entity.EntityCogwork;
import minefantasy.mfr.entity.EntityItemUnbreakable;
import minefantasy.mfr.entity.mob.EntityDragon;
import minefantasy.mfr.event.LevelUpEvent;
import minefantasy.mfr.init.MineFantasyItems;
import minefantasy.mfr.init.MineFantasyOreDict;
import minefantasy.mfr.integration.CustomStone;
import minefantasy.mfr.item.ItemArmourBaseMFR;
import minefantasy.mfr.item.ItemWeaponMFR;
import minefantasy.mfr.material.CustomMaterial;
import minefantasy.mfr.material.MetalMaterial;
import minefantasy.mfr.mechanics.CombatMechanics;
import minefantasy.mfr.mechanics.PlayerTickHandler;
import minefantasy.mfr.mechanics.RPGElements;
import minefantasy.mfr.mechanics.StaminaBar;
import minefantasy.mfr.mechanics.StaminaMechanics;
import minefantasy.mfr.mechanics.knowledge.ResearchLogic;
import minefantasy.mfr.network.LevelUpPacket;
import minefantasy.mfr.network.NetworkHandler;
import minefantasy.mfr.registry.CustomMaterialRegistry;
import minefantasy.mfr.registry.types.CustomMaterialType;
import minefantasy.mfr.util.ArmourCalculator;
import minefantasy.mfr.util.ArrowEffectsMF;
import minefantasy.mfr.util.CustomToolHelper;
import minefantasy.mfr.util.MFRLogUtil;
import minefantasy.mfr.util.TacticalManager;
import minefantasy.mfr.util.ToolHelper;
import minefantasy.mfr.util.XSTRandom;
import net.minecraft.block.Block;
import net.minecraft.block.BlockGrass;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.Slot;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.event.entity.player.UseHoeEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.oredict.OreDictionary;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static minefantasy.mfr.constants.Constants.CRAFTED_BY_NAME_TAG;

/**
 * General-purpose event handler for things, as a replacement for {EventManagerMFR}
 *
 * @since MFR
 */

@Mod.EventBusSubscriber
public final class MFREventHandler {
	private static final XSTRandom random = new XSTRandom();
	public static final DecimalFormat decimal_format = new DecimalFormat("#.#");
	public static final UUID BLOCK_SPEED_MODIFIER_UUID = UUID.fromString("2bafbc0d-1832-4a58-8296-f1a0251c8fe3");

	private MFREventHandler() {} // No instances!

	/**
	 * Shows a tooltip list of the different damage reduction attributes of an armor item (like blunt, cutting, pierce). Also displays the difference between
	 * the currently worn armor's stats and the hovered item.
	 *
	 * @param armour The armor ItemStack currently being checked
	 * @param user   The user hovering over the ItemStack
	 * @param list   The tooltip list of the ItemStack
	 * @param extra  If advanced tooltips are enabled (=true), show some extra info
	 */
	public static void addArmorDamageReductionTooltip(ItemStack armour, EntityPlayer user, List<String> list, boolean extra) {
		list.add("");
		String armourClass = ArmourCalculator.getArmourClass(armour);  // Light/Medium/Heavy
		if (armourClass != null) {
			list.add(I18n.format("attribute.armour." + armourClass));
		}
		if (armour.getItem() instanceof ISpecialArmourMFR) {
			if (ConfigArmour.advancedDamageTypes) {
				list.add(TextFormatting.BLUE + I18n.format("attribute.armour.protection"));
				addSingleDamageReductionTooltip(armour, user, 0, list, true);
				addSingleDamageReductionTooltip(armour, user, 2, list, true);
				addSingleDamageReductionTooltip(armour, user, 1, list, true);
			} else {
				addSingleDamageReductionTooltip(armour, user, 0, list, false);
			}
		}
	}

	/**
	 * Shows adds a damage reduction attribute label to the ItemStack of the specified damage reduction type
	 *
	 * @param armour   The armor ItemStack currently being checked
	 * @param user     The user hovering over the ItemStack
	 * @param id       The id of the armor rating type
	 * @param list     The tooltip list of the ItemStack
	 * @param advanced If advanced tooltips are enabled or not
	 */
	public static void addSingleDamageReductionTooltip(ItemStack armour, EntityPlayer user, int id, List<String> list, boolean advanced) {
		EntityEquipmentSlot slot = ((ItemArmor) armour.getItem()).armorType;
		String attach = "";

		int rating = (int) (ArmourCalculator.getDamageReductionForDisplayPiece(armour, id) * 100F);
		int equipped = (int) (ArmourCalculator.getDamageReductionForDisplayPiece(user.getItemStackFromSlot(slot), id) * 100F);

		if (rating > 0 || equipped > 0) {
			if (equipped > 0 && rating != equipped) {
				float d = rating - equipped;

				attach += d > 0 ? TextFormatting.DARK_GREEN : TextFormatting.RED;

				String d2 = ItemWeaponMFR.decimal_format.format(d);
				attach += " (" + (d > 0 ? "+" : "") + d2 + ")";
			}
			if (advanced) {
				list.add(TextFormatting.BLUE + I18n.format("attribute.armour.rating." + id) + " "
						+ rating + attach);
			} else {
				list.add(TextFormatting.BLUE + I18n.format("attribute.armour.protection") + ": "
						+ rating + attach);
			}
		}
	}

	/**
	 * Adds a tooltip to the specified ItemStack about the crafter (if it has info about the crafter in nbt)
	 *
	 * @param tool the ItemStack
	 * @param list the tooltip of the ItemStack
	 */
	private static void showCrafterTooltip(ItemStack tool, List<String> list) {
		Tool toolStack = ToolHelper.getToolTypeFromStack(tool);
		int tier = ToolHelper.getCrafterTier(tool);
		float efficiency = ToolHelper.getCrafterEfficiency(tool);

		list.add(I18n.format("attribute.mfcrafttool.name") + ": " + toolStack.getDisplayName());
		list.add(I18n.format("attribute.mfcrafttier.name") + ": " + tier);
		list.add(I18n.format("attribute.mfcrafteff.name") + ": " + efficiency);
	}

	/**
	 * Adds a tooltip to the specified ItemStack about custom Food Stats (if it has info about the food in custom data)
	 *
	 * @param food the ItemStack
	 * @param list the tooltip of the ItemStack
	 */
	private static void showFoodTooltip(ItemStack food, List<String> list) {
		CustomFoodEntry foodEntry = CustomFoodEntry.getEntry(food);
		if (foodEntry.hasEffect && ClientItemsMFR.showSpecials(list)) {
			list.add("");
			list.add(TextFormatting.WHITE + I18n.format("food.stat.list.name"));
			if (foodEntry.eatDelay > 0) {
				list.add(I18n.format("food.stat.eatDelay.name", decimal_format.format(foodEntry.eatDelay / 20)));
			}
			if (foodEntry.staminaRestore > 0) {
				list.add(I18n.format("food.stat.staminaPlus.name", (int) foodEntry.staminaRestore));
			}
			if (foodEntry.staminaBuff > 0) {
				if (foodEntry.staminaInHours) {
					list.add(I18n.format("food.stat.staminabuffHours.name", decimal_format.format(foodEntry.staminaBuff), decimal_format.format(foodEntry.staminaSeconds / 3600F)));
				} else if (foodEntry.staminaInMinutes) {
					list.add(I18n.format("food.stat.staminabuffMinutes.name", decimal_format.format(foodEntry.staminaBuff), decimal_format.format(foodEntry.staminaSeconds / 60F)));
				} else {
					list.add(I18n.format("food.stat.staminabuffSeconds.name", decimal_format.format(foodEntry.staminaBuff), decimal_format.format(foodEntry.staminaSeconds)));
				}
			}
			if (foodEntry.staminaRegenBuff > 0) {
				if (foodEntry.staminaRegenInMinutes) {
					list.add(I18n.format("food.stat.staminabuffRegenMinutes.name", decimal_format.format(foodEntry.staminaRegenBuff), decimal_format.format(foodEntry.staminaRegenSeconds / 60F)));
				} else {
					list.add(I18n.format("food.stat.staminabuffRegenSeconds.name", decimal_format.format(foodEntry.staminaRegenBuff), decimal_format.format(foodEntry.staminaRegenSeconds)));
				}
			}
			if (foodEntry.fatAccumulation > 0) {
				list.add(I18n.format("food.stat.fatAccumulation.name", decimal_format.format(foodEntry.fatAccumulation)));
			}
		}
	}

	// ================================================ Event Handlers ================================================

	/**
	 * Attaches the dynamic tooltips to itemStacks. Called when an item is hovered with the cursor
	 */
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public static void onItemTooltip(ItemTooltipEvent event) {
		//  Excludes this from the initial search tree population for tooltips (during game initialization).
		if (event.getEntity() == null) {
			return;
		}

		if (!event.getItemStack().isEmpty()) {
			boolean saidArtefact = false;
			int[] ids = OreDictionary.getOreIDs(event.getItemStack());
			boolean hasInfo = false;
			if (ids != null && event.getEntityPlayer() != null) {
				for (int id : ids) {
					String s = OreDictionary.getOreName(id);
					if (s != null) {
						if (!hasInfo && s.startsWith("ingot")) {
							String s2 = s.substring(5, s.length());
							CustomMaterial material = CustomMaterialRegistry.getMaterial(CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, s2));
							if (material != CustomMaterialRegistry.NONE){
								hasInfo = true;
							}
							else {
								if (!s.contains("Brick")){
									ArrayList<CustomMaterial> metalMaterials = CustomMaterialRegistry.getList(CustomMaterialType.METAL_MATERIAL);
									for (CustomMaterial metal : metalMaterials){
										if (metal instanceof MetalMaterial) {
											if (metal.getMaterialIngredient().apply(event.getItemStack())){
												material = metal;
												if (material != CustomMaterialRegistry.NONE){
													break;
												}
											}
										}
									}
								}
							}

							CustomToolHelper.addComponentString(event.getToolTip(), material);
						}
						if (s.startsWith("Artefact-")) {
							if (!saidArtefact) {
								String knowledge = s.substring(9).toLowerCase();

								if (!ResearchLogic.hasInfoUnlocked(event.getEntityPlayer(), knowledge) && !ResearchLogic.alreadyUsedArtefact(event.getEntityPlayer(), ResearchLogic.getResearch(knowledge), event.getItemStack())) {
									saidArtefact = true;
									event.getToolTip().add(TextFormatting.AQUA + I18n.format("info.hasKnowledge"));
								}
							}
						} else if (ConfigClient.displayOreDict) {
							event.getToolTip().add("oreDict: " + s);
						}
					}
				}
			}

			if (event.getItemStack().hasTagCompound() && event.getItemStack().getTagCompound().hasKey("MF_Inferior")) {
				if (event.getItemStack().getTagCompound().getBoolean("MF_Inferior")) {
					event.getToolTip().add(TextFormatting.RED + I18n.format("attribute.inferior.name"));
				}
				if (!event.getItemStack().getTagCompound().getBoolean("MF_Inferior")) {
					event.getToolTip().add(TextFormatting.GREEN + I18n.format("attribute.superior.name"));
				}
			}
			if (event.getEntityPlayer() != null && event.getToolTip() != null && event.getFlags() != null) {
				if (event.getItemStack().getItem() instanceof ItemArmor
						&& (!(event.getItemStack().getItem() instanceof ItemArmourBaseMFR)
						|| ClientItemsMFR.showSpecials(event.getToolTip()))) {
					addArmorDamageReductionTooltip(event.getItemStack(), event.getEntityPlayer(), event.getToolTip(), event.getFlags().isAdvanced());
				}
			}
			if (ConfigArmour.advancedDamageTypes && ArmourCalculator.getRatioForWeapon(event.getItemStack()) != null) {
				displayWeaponTraits(ArmourCalculator.getRatioForWeapon(event.getItemStack()), event.getToolTip());
			}
			if (ToolHelper.shouldShowTooltip(event.getItemStack())) {
				showCrafterTooltip(event.getItemStack(), event.getToolTip());
			}
			if (CustomFoodEntry.getEntry(event.getItemStack()) != null) {
				showFoodTooltip(event.getItemStack(), event.getToolTip());
			}
			if (event.getItemStack().hasTagCompound() && event.getItemStack().getTagCompound().hasKey(CRAFTED_BY_NAME_TAG)) {
				String name = event.getItemStack().getTagCompound().getString(CRAFTED_BY_NAME_TAG);
				boolean special = MineFantasyReforged.isNameModder(name);// Mod creators have highlights

				event.getToolTip().add((special ? TextFormatting.GREEN : "")
						+ I18n.format("attribute.mfcraftedbyname.name")
						+ ": " + name
						+ TextFormatting.GRAY);
			}
			WeaponClass WC = WeaponClass.findClassForAny(event.getItemStack());
			if (WC != null && RPGElements.isSystemActive && WC.parentSkill != Skill.NONE) {
				event.getToolTip().add(I18n.format("weaponclass." + WC.name.toLowerCase()));
				float skillMod = RPGElements.getWeaponModifier(event.getEntityPlayer(), WC.parentSkill) * 100F;
				if (skillMod > 100)
					event.getToolTip().add(I18n.format("rpg.skillmod") + ItemWeaponMFR.decimal_format.format(skillMod - 100) + "%");
			}
		}
	}

	@SubscribeEvent
	public static void specialInteractForComponentBlock(PlayerInteractEvent.RightClickBlock event) {
		// Handle Custom Crafters block transformation
		if (CustomCrafterEntry.getEntry(event.getItemStack()) != null) {
			ToolHelper.performBlockTransformation(
					event.getEntityPlayer(), event.getEntityPlayer().world,
					event.getPos(), event.getHand(), event.getFace());
		}

		if (event.getEntityPlayer().isSneaking() && event.getWorld().getBlockState(event.getPos()).getBlock() instanceof BlockComponent) {
			event.setUseBlock(Event.Result.ALLOW);
		}
	}

	@SubscribeEvent
	public static void tryDropItems(LivingDropsEvent event) {
		EntityLivingBase dropper = event.getEntityLiving();

		if (dropper instanceof EntityChicken) {
			int dropCount = 1 + random.nextInt(event.getLootingLevel() + 4);

			for (int a = 0; a < dropCount; a++) {
				dropper.entityDropItem(new ItemStack(Items.FEATHER), 0.0F);
			}
		}
		if (dropper.getEntityData().hasKey(Constants.DROP_LOOT_TAG)) {
			int id = dropper.getEntityData().getInteger(Constants.DROP_LOOT_TAG);
			Item drop = id == 0 ? MineFantasyItems.LOOT_SACK_COMMON : id == 1 ? MineFantasyItems.LOOT_SACK_VALUABLE : MineFantasyItems.LOOT_SACK_EXQUISITE;
			dropper.entityDropItem(new ItemStack(drop), 0.0F);
		}
		if (dropper instanceof EntityAgeable && dropper.getCreatureAttribute() != EnumCreatureAttribute.UNDEAD) {
			if (random.nextFloat() * (1 + event.getLootingLevel()) < 0.05F) {
				dropper.entityDropItem(new ItemStack(MineFantasyItems.GUTS), 0.0F);
			}
		}
		if (dropper instanceof IAnimals && !(dropper instanceof IMob)) {
			if (ConfigHardcore.hunterKnife && !dropper.getEntityData().hasKey(Constants.HUNTER_KILL_TAG)) {
				event.setCanceled(true);
				return;
			}
			if (ConfigHardcore.lessHunt) {
				alterDrops(dropper, event);
			}
		}
		if (getRegisterName(dropper).contains("Horse") && !dropper.isEntityUndead()) {
			int dropCount = random.nextInt(3 + event.getLootingLevel());
			if (ConfigHardcore.lessHunt) {
				dropCount = 1 + random.nextInt(event.getLootingLevel() + 1);
			}

			Item meat = dropper.isBurning() ? MineFantasyItems.HORSE_COOKED : MineFantasyItems.HORSE_RAW;
			for (int a = 0; a < dropCount; a++) {
				dropper.entityDropItem(new ItemStack(meat), 0.0F);
			}
		}
		if (getRegisterName(dropper).contains("Wolf")) {
			int dropCount = random.nextInt(3 + event.getLootingLevel());
			if (ConfigHardcore.lessHunt) {
				dropCount = 1 + random.nextInt(event.getLootingLevel() + 1);
			}

			Item meat = dropper.isBurning() ? MineFantasyItems.WOLF_COOKED : MineFantasyItems.WOLF_RAW;
			for (int a = 0; a < dropCount; a++) {
				dropper.entityDropItem(new ItemStack(meat), 0.0F);
			}
		}
		dropLeather(event.getEntityLiving(), event);

		if (dropper instanceof EntitySkeleton) {
			EntitySkeleton skeleton = (EntitySkeleton) dropper;

			if ((skeleton.getHeldItemMainhand().isEmpty() || !(skeleton.getHeldItemMainhand().getItem() instanceof ItemBow))
					&& event.getDrops() != null && !event.getDrops().isEmpty()) {

				for (EntityItem entItem : event.getDrops()) {
					ItemStack drop = entItem.getItem();

					if (drop.getItem() == Items.ARROW) {
						entItem.setDead();
					}
				}
			}
		}
	}

	public static String getRegisterName(Entity entity) {
		String s = EntityList.getEntityString(entity);

		if (s == null) {
			s = "generic";
		}

		return s;
	}

	private static void dropLeather(EntityLivingBase mob, LivingDropsEvent event) {
		boolean dropHide = shouldAnimalDropHide(mob);
		Item hide = getHideFor(mob);

		if (event.getDrops() != null && !event.getDrops().isEmpty()) {

			for (EntityItem entItem : event.getDrops()) {
				ItemStack drop = entItem.getItem();

				if (ConfigHardcore.dropRawhide && drop.getItem() == Items.LEATHER) {
					entItem.setDead();
					dropHide = true;
				}
			}
		}
		if (ConfigHardcore.dropRawhide && dropHide && hide != null && !(ConfigHardcore.hunterKnife && !mob.getEntityData().hasKey(Constants.HUNTER_KILL_TAG))) {
			mob.entityDropItem(new ItemStack(hide), 0.0F);
		}
	}

	private static Item getHideFor(EntityLivingBase mob) {
		Item[] hide = new Item[] {MineFantasyItems.RAWHIDE_SMALL, MineFantasyItems.RAWHIDE_MEDIUM, MineFantasyItems.RAWHIDE_LARGE};
		int size = getHideSizeFor(mob);
		if (mob.isChild()) {
			size--;
		}

		if (size <= 0) {
			return null;
		}
		if (size > hide.length) {
			size = hide.length;
		}

		return hide[size - 1];
	}

	private static int getHideSizeFor(EntityLivingBase mob) {
		String mobName = mob.getClass().getName();
		if (mobName.endsWith("EntityCow") || mobName.endsWith("EntityHorse")) {
			return 3;
		}
		if (mobName.endsWith("EntitySheep")) {
			return 2;
		}
		if (mobName.endsWith("EntityPig")) {
			return 1;
		}

		int size = (int) (mob.width + mob.height + 1);
		if (size <= 1) {
			return 0;
		}
		if (size == 2) {
			return 1;
		} else if (size <= 4) {
			return 2;
		}
		return 3;
	}

	private static boolean shouldAnimalDropHide(EntityLivingBase mob) {
		return mob instanceof EntityWolf || mob instanceof EntityCow || mob instanceof EntityPig || mob instanceof EntitySheep || mob instanceof EntityHorse;
	}

	@SubscribeEvent
	public static void onDeath(LivingDeathEvent event) {
		//Hunting Mechanics and rare drops
		EntityLivingBase dead = event.getEntityLiving();
		EntityLivingBase hunter;
		ItemStack weapon = null;
		DamageSource source = event.getSource();

		//drop rare loot
		dropBook(dead);

		//get weapon mainhand
		if (source != null && source.getTrueSource() != null) {
			if (source.getTrueSource() instanceof EntityLivingBase) {
				hunter = (EntityLivingBase) source.getTrueSource();
				weapon = hunter.getHeldItemMainhand();
			}
		}

		//Add hunter kill tag to entity, it will then be used in hide dropping mechanics if the config is set to hunting items only
		if (weapon != null) {
			Tool type = ToolHelper.getToolTypeFromStack(weapon);
			if (weapon.getItem() instanceof IHuntingItem) {
				if (((IHuntingItem) weapon.getItem()).canRetrieveDrops(weapon)) {
					dead.getEntityData().setBoolean(Constants.HUNTER_KILL_TAG, true);
				}
			} else if (type != null && type.equals(Tool.KNIFE)) {
				dead.getEntityData().setBoolean(Constants.HUNTER_KILL_TAG, true);
			}
		}

		//Dragon Kill Points for figuring Dragon Spawn Chance
		if (!event.getEntityLiving().world.isRemote && event.getEntityLiving() instanceof EntityDragon && event.getSource() != null
				&& event.getSource().getTrueSource() != null && event.getSource().getTrueSource() instanceof EntityPlayer) {
			PlayerTickHandler.addDragonKill((EntityPlayer) event.getSource().getTrueSource());
		}
		if (!event.getEntityLiving().world.isRemote && event.getEntityLiving() instanceof EntityPlayer && event.getSource() != null
				&& event.getSource().getTrueSource() != null && event.getSource().getTrueSource() instanceof EntityDragon) {
			PlayerTickHandler.addDragonEnemyPts((EntityPlayer) event.getEntityLiving(), -1);
		}

		//Arrow Mechanics
		Entity dropper = event.getEntity();

		boolean useArrows = true;
		try {
			Class.forName("minefantasy.mf2.api.helpers.ArrowEffectsMF");
		}
		catch (Exception e) {
			useArrows = false;
		}
		if (dropper != null && useArrows && ConfigSpecials.stickArrows && !dropper.world.isRemote) {
			ArrayList<ItemStack> stuckArrows = (ArrayList<ItemStack>) ArrowEffectsMF.getStuckArrows(dropper);
			if (!stuckArrows.isEmpty()) {

				for (ItemStack arrow : stuckArrows) {
					if (!arrow.isEmpty()) {
						dropper.entityDropItem(arrow, 0.0F);
					}
				}
			}
		}

	}

	@SubscribeEvent
	public static void onEntityJoinWorldEvent(EntityJoinWorldEvent event) {
		if (event.getEntity().isDead) {
			return;
		}
		if (event.getEntity() instanceof EntityItem && !(event.getEntity() instanceof EntityItemUnbreakable)) {
			EntityItem entityItem = (EntityItem) event.getEntity();
			if (!entityItem.getItem().isEmpty()) {
				//noinspection ConstantConditions
				if (entityItem.getItem().hasTagCompound() && entityItem.getItem().getTagCompound().hasKey(Constants.UNBREAKABLE_TAG)) {
					EntityItem newEntity = new EntityItemUnbreakable(event.getWorld(), entityItem);
					event.getWorld().spawnEntity(newEntity);
					entityItem.setDead();
					event.setCanceled(true);
				}
				if (entityItem.getItem().getItem() == MineFantasyItems.DRAGON_HEART) {
					MFRLogUtil.logDebug("Found dragon heart");
					EntityItem newEntity = new EntityItemUnbreakable(event.getWorld(), entityItem);
					event.getWorld().spawnEntity(newEntity);
					entityItem.setDead();
					event.setCanceled(true);
				}
			}
		}
	}

	private static void dropBook(EntityLivingBase dead) {
		if (dead.world.isRemote) {
			return;
		}

		Item book = null;

		if (dead instanceof EntityWitch) {
			float chance = random.nextFloat();
			if (chance > 0.75F) {
				book = MineFantasyItems.SKILLBOOK_ENGINEERING;
			} else {
				book = MineFantasyItems.SKILLBOOK_PROVISIONING;
			}
		} else if (dead instanceof EntityVillager && random.nextInt(5) == 0) {
			float chance = random.nextFloat();
			if (chance > 0.9F) {
				book = MineFantasyItems.SKILLBOOK_ENGINEERING;
			} else if (chance > 0.6F) {
				book = MineFantasyItems.SKILLBOOK_ARTISANRY;
			} else if (chance > 0.3F) {
				book = MineFantasyItems.SKILLBOOK_CONSTRUCTION;
			} else {
				book = MineFantasyItems.SKILLBOOK_PROVISIONING;
			}
		} else if (dead instanceof EntityZombie && random.nextInt(25) == 0) {
			float chance = random.nextFloat();
			if (chance > 0.9F) {
				book = MineFantasyItems.SKILLBOOK_ENGINEERING;
			} else if (chance > 0.6F) {
				book = MineFantasyItems.SKILLBOOK_ARTISANRY;
			} else if (chance > 0.3F) {
				book = MineFantasyItems.SKILLBOOK_CONSTRUCTION;
			} else {
				book = MineFantasyItems.SKILLBOOK_PROVISIONING;
			}
		}

		if (book != null) {
			dead.entityDropItem(new ItemStack(book), 0F);
		}
	}

	public static void alterDrops(EntityLivingBase dropper, LivingDropsEvent event) {
		ArrayList<ItemStack> meats = new ArrayList<>();

		if (event.getDrops() != null && !event.getDrops().isEmpty()) {

			for (EntityItem entItem : event.getDrops()) {
				ItemStack drop = entItem.getItem();
				boolean dropItem = true;

				if (drop.getItem() instanceof ItemFood) {
					entItem.setDead();

					if (!meats.isEmpty()) {
						for (ItemStack compare : meats) {
							if (drop.isItemEqual(compare)) {
								dropItem = false;
								break;
							}
						}
					}
					if (dropItem) {
						drop.setCount(1);
						if (event.getLootingLevel() > 0) {
							drop.setCount(dropper.getRNG().nextInt(event.getLootingLevel() + 1));
						}
						meats.add(drop.copy());
					}
				}
			}

			for (ItemStack meat : meats) {
				dropper.entityDropItem(meat, 0.0F);
			}
		}
	}


	@SubscribeEvent
	public static void useHoe(UseHoeEvent event) {
		Block block = event.getWorld().getBlockState(event.getPos()).getBlock();
		if (block != Blocks.FARMLAND
				&& FarmingHelper.didHoeFail(event.getCurrent(), event.getWorld(), block == Blocks.GRASS)) {
			EntityPlayer player = event.getEntityPlayer();
			player.swingArm(player.isHandActive() ? player.getActiveHand() : (player.getHeldItemMainhand() == event.getCurrent() ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND));
			event.getWorld().playSound(player, event.getPos(), SoundEvents.ITEM_HOE_TILL, SoundCategory.AMBIENT, 12, 1F);
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public static void breakBlock(BlockEvent.BreakEvent event) {
		// Block block = event.block;
		Block base = event.getWorld().getBlockState(event.getPos().down()).getBlock();
		// int meta = event.blockMetadata;

		if (base == Blocks.FARMLAND && FarmingHelper.didHarvestRuinBlock(event.getWorld(), false)) {
			event.getWorld().setBlockState(event.getPos().add(0, -1, 0), Blocks.DIRT.getDefaultState());
		}

		EntityPlayer player = event.getPlayer();
		if (player != null && !player.capabilities.isCreativeMode && !(player instanceof FakePlayer)) {
			playerMineBlock(event);
		}
	}

	public static void playerMineBlock(BlockEvent.BreakEvent event) {
		EntityPlayer player = event.getPlayer();
		ItemStack held = player.getHeldItemMainhand();
		IBlockState broken = event.getState();
		if (broken != null){
			if (ConfigHardcore.HCCallowRocks) {
				if (held.isEmpty() && CustomStone.isStone(broken.getBlock())) {
					entityDropItem(event.getWorld(), event.getPos(),
							new ItemStack(MineFantasyItems.SHARP_ROCK, random.nextInt(3) + 1));
				}
				if (!held.isEmpty() && held.getItem() == MineFantasyItems.SHARP_ROCK && broken.getBlock() instanceof BlockLeaves) {
					if (random.nextInt(5) == 0) {
						entityDropItem(event.getWorld(), event.getPos(), new ItemStack(Items.STICK, random.nextInt(3) + 1));
					}
					if (random.nextInt(3) == 0) {
						entityDropItem(event.getWorld(), event.getPos(), new ItemStack(MineFantasyItems.VINE, random.nextInt(3) + 1));
					}
				}
			}

			if (ConfigStamina.isSystemActive && ConfigStamina.affectMining && StaminaBar.doesAffectEntity(player) && !isBlockPlant(broken.getBlock()) && !(broken == (Blocks.SNOW_LAYER).getDefaultState())) {
				float points = 2.0F * ConfigStamina.miningSpeed;
				ItemWeaponMFR.applyFatigue(player, points, 20F);

				if (points > 0 && !StaminaBar.isAnyStamina(player, false)) {
					player.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 100, 1));
				}
			}
		}
	}

	public static boolean isBlockPlant(Block block){
		return block instanceof IPlantable && !(block instanceof BlockGrass);
	}

	public static EntityItem entityDropItem(World world, BlockPos pos, ItemStack item) {
		if (item.getCount() != 0 && !item.isEmpty()) {
			EntityItem entityitem = new EntityItem(world, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, item);
			entityitem.setPickupDelay(10);
			world.spawnEntity(entityitem);
			return entityitem;
		}
		return null;
	}


	private static void displayWeaponTraits(float[] ratio, List<String> list) {
		int cutting = (int) (ratio[0] / (ratio[0] + ratio[1] + ratio[2]) * 100F);
		int piercing = (int) (ratio[2] / (ratio[0] + ratio[1] + ratio[2]) * 100F);
		int blunt = (int) (ratio[1] / (ratio[0] + ratio[1] + ratio[2]) * 100F);

		addDamageType(list, cutting, "cutting");
		addDamageType(list, piercing, "piercing");
		addDamageType(list, blunt, "blunt");
	}

	private static void addDamageType(List<String> list, int value, String name) {
		if (value > 0) {
			String s = I18n.format("attribute.weapon." + name);
			if (value < 100) {
				s += " " + value + "%";
			}
			list.add(s);
		}
	}

	@SubscribeEvent
	public static void updateEntity(LivingEvent.LivingUpdateEvent event) {
		if (event.getEntity() instanceof EntityCogwork) {
			return;
		}
		EntityLivingBase entity = event.getEntityLiving();

		int injury = getInjuredTime(entity);

		if (ConfigMobs.criticalLimp && entity.ticksExisted - entity.getLastAttackedEntityTime() > 200 && (entity instanceof EntityLiving || !(entity instanceof EntityPlayer && ((EntityPlayer) entity).capabilities.isCreativeMode))) {
			float lowHp = entity.getMaxHealth() / 5F;

			if (entity.getHealth() <= lowHp || injury > 0) {
				if (entity.getRNG().nextInt(10) == 0 && entity.onGround && !entity.isRiding()) {
					entity.motionX = 0F;
					entity.motionZ = 0F;
				}
				float x = (float) (entity.posX + (random.nextFloat() - 0.5F) / 4F);
				float y = (float) (entity.posY + entity.getEyeHeight() + (random.nextFloat() - 0.5F) / 4F);
				float z = (float) (entity.posZ + (random.nextFloat() - 0.5F) / 4F);
				entity.world.spawnParticle(EnumParticleTypes.REDSTONE, x, y, z, 0F, 0F, 0F);
			}
			if (!entity.world.isRemote && entity instanceof EntityPlayer && entity.getHealth() <= (lowHp / 2) && entity.getRNG().nextInt(200) == 0) {
				entity.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 100, 50));
			}
		}
		if (injury > 0 && !entity.world.isRemote) {
			injury--;
			entity.getEntityData().setInteger(Constants.INJURED_TAG, injury);
		}
		if (ConfigStamina.isSystemActive && StaminaBar.doesAffectEntity(entity) && event.getEntityLiving() instanceof EntityPlayer) {
			StaminaMechanics.tickEntity((EntityPlayer) event.getEntityLiving());
		}

		IAttributeInstance speedAttribute = entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
		double speedMod = ConfigWeapon.blockSpeedMod;
		if (entity.getActiveItemStack().getItem() instanceof IParryable) {
			AttributeModifier blockSpeedMod = new AttributeModifier(BLOCK_SPEED_MODIFIER_UUID, "block_speed_adjustment", speedMod, 2);
			if (!speedAttribute.hasModifier(blockSpeedMod)) {
				speedAttribute.applyModifier(blockSpeedMod);
			}
			else if (speedAttribute.getModifier(BLOCK_SPEED_MODIFIER_UUID).getAmount() != speedMod) {
				speedAttribute.removeModifier(BLOCK_SPEED_MODIFIER_UUID);
				speedAttribute.applyModifier(blockSpeedMod);
			}
		}
		else if (speedAttribute.hasModifier(new AttributeModifier(BLOCK_SPEED_MODIFIER_UUID, "block_speed_adjustment", 0, 0))){
			speedAttribute.removeModifier(BLOCK_SPEED_MODIFIER_UUID);
		}

	}

	public static int getInjuredTime(Entity entity) {
		if (entity.getEntityData().hasKey(Constants.INJURED_TAG)) {
			return entity.getEntityData().getInteger(Constants.INJURED_TAG);
		}
		return 0;
	}

	/**
	 * Handles stamina drain for holding a bowstring back
	 * @param event arrow loose event
	 */
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void applyExhaustArrow(ArrowLooseEvent event) {
		if (ConfigStamina.isSystemActive && !StaminaBar.isAnyStamina(event.getEntityPlayer(), false)) {
			if (ConfigStamina.weaponDrain < 1.0F)
				event.setCharge(event.getCharge() * (int) ConfigStamina.weaponDrain);
		}
	}

	@SubscribeEvent
	public static void startUseItem(LivingEntityUseItemEvent.Start event) {
		EntityLivingBase player = event.getEntityLiving();
		if (!event.getItem().isEmpty() && event.getItem().getItemUseAction() == EnumAction.valueOf("mfr_block")) {
			if ((ConfigStamina.isSystemActive && TacticalManager.shouldStaminaBlock && !StaminaBar.isAnyStamina(player, false)) || !CombatMechanics.isParryAvailable(player)) {
				event.setCanceled(true);
			}
		}
	}

	@SubscribeEvent
	public static void itemEvent(EntityItemPickupEvent event) {
		EntityPlayer player = event.getEntityPlayer();

		EntityItem drop = event.getItem();
		ItemStack item = drop.getItem();
		ItemStack held = player.getHeldItemMainhand();

		if (!held.isEmpty() && held.getItem() instanceof ISmithTongs) {
			if (!TongsHelper.hasHeldItem(held)) {
				if (isHotItem(item)) {
					if (TongsHelper.trySetHeldItem(held, item)) {
						drop.setDead();

						if (event.isCancelable()) {
							event.setCanceled(true);
						}
						return;
					}
				}
			}
		}
		{
			if (ConfigHardcore.HCChotBurn && !item.isEmpty() && isHotItem(item)) {
				if (event.isCancelable()) {
					event.setCanceled(true);
				}
			}
		}
	}

	@SubscribeEvent
	public static void onContainerClosed(PlayerContainerEvent.Close event){
		EntityPlayer player = event.getEntityPlayer();
		Container container = event.getContainer();
		//Handle not placing hot items in non-allowed containers
		if (!(container instanceof ContainerAnvil || container instanceof ContainerForge || container instanceof ContainerPlayer)){
			for (Slot slot : container.inventorySlots){
				int slotIndex = slot.slotNumber;
				if (slotIndex < (container.inventorySlots.size() - player.inventory.mainInventory.size())){
					ItemStack stack = slot.getStack().copy();
					if (stack.getItem() instanceof IHotItem && ConfigHardcore.HCChotBurn){
						IItemHandlerModifiable inventoryHandler = (IItemHandlerModifiable) player.getHeldItemMainhand()
								.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);

						player.dropItem(stack, false);
						if (inventoryHandler != null) {
							inventoryHandler.setStackInSlot(slotIndex, ItemStack.EMPTY);
						}
						else {
							container.putStackInSlot(slotIndex, ItemStack.EMPTY);
						}

						container.detectAndSendChanges();
					}
				}
			}
		}
	}

	@SubscribeEvent
	public static void wakeUp(PlayerWakeUpEvent event) {
		EntityPlayer player = event.getEntityPlayer();
		if (player.isPlayerFullyAsleep()){
			PlayerTickHandler.wakeUp(event.getEntityPlayer());
		}
	}

	private static boolean isHotItem(ItemStack item) {
		return !item.isEmpty() && (item.getItem() instanceof IHotItem);
	}

	@SubscribeEvent
	public static void levelup(LevelUpEvent event) {
		EntityPlayer player = event.thePlayer;
		if (player instanceof EntityPlayerMP) {
			NetworkHandler.sendToPlayer((EntityPlayerMP) player, new LevelUpPacket(player, event.theSkill, event.theLevel));
			PlayerData.get(player).sync();
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void oreDictRegistry(RegistryEvent.Register<Item> event) {
		//It is necessary to register the OreDict entries here, instead of somewhere normal, because registries get wierd
		//Essentially, this needs to happen after Item and Block registry, but before recipes. This works I guess
		MineFantasyOreDict.registerOreDictEntries();
	}
}
