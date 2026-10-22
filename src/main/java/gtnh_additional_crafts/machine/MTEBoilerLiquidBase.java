package gtnh_additional_crafts.machine;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidContainerItem;
import net.minecraftforge.fluids.IFluidTank;

import org.jetbrains.annotations.NotNull;

import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.utils.item.IItemHandler;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widget.Widget;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.enums.ParticleFX;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.modularui2.GTWidgetThemes;
import gregtech.api.objects.XSTR;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTUtility;
import gregtech.api.util.WorldSpawnedEventBuilder;
import gregtech.common.tileentities.boilers.MTEBoiler;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;

public abstract class MTEBoilerLiquidBase extends MTEBoiler {

    public static final Map<String, Integer> FUEL_ENERGY_MAP;

    static {
        Map<String, Integer> map = new LinkedHashMap<>();
        // Creosote Oil (Primary fuel, 160 energy = 1920 ticks / 96s on Bronze, equivalent to 1 coal)
        map.put("creosote", 160);
        map.put("creosoteoil", 160);

        // Biomass
        map.put("biomass", 120);

        // Raw Oil / Light Oil
        map.put("oil", 100);
        map.put("crudeoil", 100);
        map.put("oillight", 100);
        map.put("lightoil", 100);

        // Fish Oil
        map.put("fishoil", 80);
        map.put("fish.oil", 80);

        // Seed Oils
        map.put("seedoil", 60);
        map.put("seed.oil", 60);
        map.put("seedoilhemp", 60);
        map.put("seedoillin", 60);
        map.put("linseedoil", 60);
        map.put("hempoil", 60);

        FUEL_ENERGY_MAP = Collections.unmodifiableMap(map);
    }

    protected final FluidTank fuelTank;

    public MTEBoilerLiquidBase(int aID, String aName, String aNameRegional, String[] aDescription, int fuelCapacity,
        ITexture... aTextures) {
        super(aID, aName, aNameRegional, aDescription, aTextures);
        this.fuelTank = new FluidTank(fuelCapacity);
    }

    public MTEBoilerLiquidBase(String aName, int aTier, String[] aDescription, int fuelCapacity,
        ITexture[][][] aTextures) {
        super(aName, aTier, aDescription, aTextures);
        this.fuelTank = new FluidTank(fuelCapacity);
    }

    public static int getEnergyForFluid(FluidStack fluid) {
        if (fluid == null || fluid.getFluid() == null) {
            return 0;
        }
        String name = fluid.getFluid()
            .getName()
            .toLowerCase(Locale.ROOT);
        if (name.startsWith("fluid.")) {
            name = name.substring(6);
        }
        Integer energy = FUEL_ENERGY_MAP.get(name);
        return energy != null ? energy : 0;
    }

    public static boolean isFuelAllowed(FluidStack fluid) {
        return getEnergyForFluid(fluid) > 0;
    }

    public static boolean isFuelContainer(ItemStack stack) {
        if (stack == null) {
            return false;
        }
        FluidStack fluid = GTUtility.getFluidForFilledItem(stack, true);
        return isFuelAllowed(fluid);
    }

    public FluidTank getFuelTank() {
        return this.fuelTank;
    }

    @Override
    public boolean isFluidInputAllowed(FluidStack aFluid) {
        return GTModHandler.isWater(aFluid) || isFuelAllowed(aFluid);
    }

    @Override
    public int fill(FluidStack aFluid, boolean doFill) {
        if (GTModHandler.isWater(aFluid)) {
            return super.fill(aFluid, doFill);
        }
        if (isFuelAllowed(aFluid)) {
            if (this.fuelTank.getFluid() != null && !this.fuelTank.getFluid()
                .isFluidEqual(aFluid)) {
                return 0;
            }
            return this.fuelTank.fill(aFluid, doFill);
        }
        return 0;
    }

    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection side) {
        return new FluidTankInfo[] { super.getTankInfo(side)[0],
            new FluidTankInfo(this.fuelTank.getFluid(), this.fuelTank.getCapacity()),
            new FluidTankInfo(this.getDrainableStack(), this.getSteamCapacity()) };
    }

    @Override
    protected boolean isItemValidFuel(@NotNull ItemStack stack) {
        return isFuelContainer(stack);
    }

    @Override
    public boolean isValidFluidInputSlotItem(@NotNull ItemStack stack) {
        return super.isValidFluidInputSlotItem(stack);
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack itemStack) {
        if (index == 2) {
            return isFuelContainer(itemStack);
        }
        return super.isItemValidForSlot(index, itemStack);
    }

    @Override
    public boolean allowPutStack(IGregTechTileEntity aBaseMetaTileEntity, int aIndex, ForgeDirection side,
        ItemStack aStack) {
        if (!this.isAutomatable()) {
            return false;
        }
        if (aIndex == 0) {
            return this.isValidFluidInputSlotItem(aStack);
        }
        if (aIndex == 2) {
            return isFuelContainer(aStack);
        }
        return false;
    }

    @Override
    public boolean allowPullStack(IGregTechTileEntity aBaseMetaTileEntity, int aIndex, ForgeDirection side,
        ItemStack aStack) {
        return this.isAutomatable() && (aIndex == 1 || aIndex == 3);
    }

    @Override
    public void onPreTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        if (!aBaseMetaTileEntity.isServerSide()) {
            return;
        }
        super.onPreTick(aBaseMetaTileEntity, aTick);

        ItemStack fuelStack = this.mInventory[2];
        if (fuelStack != null && fuelStack.stackSize > 0) {
            FluidStack containedFluid = GTUtility.getFluidForFilledItem(fuelStack, true);
            if (isFuelAllowed(containedFluid)) {
                if (fuelStack.getItem() instanceof IFluidContainerItem) {
                    IFluidContainerItem containerItem = (IFluidContainerItem) fuelStack.getItem();
                    int fillable = this.fuelTank.fill(containedFluid, false);
                    if (fillable > 0) {
                        FluidStack drained = containerItem.drain(fuelStack, fillable, true);
                        if (drained != null && drained.amount > 0) {
                            this.fuelTank.fill(drained, true);
                        }
                    }
                    FluidStack remaining = containerItem.getFluid(fuelStack);
                    if (remaining == null || remaining.amount == 0) {
                        if (aBaseMetaTileEntity.addStackToSlot(3, fuelStack, 1)) {
                            aBaseMetaTileEntity.setInventorySlotContents(2, null);
                        }
                    }
                } else {
                    int fillable = this.fuelTank.fill(containedFluid, false);
                    if (fillable >= containedFluid.amount) {
                        ItemStack emptyContainer = GTUtility.getContainerForFilledItem(fuelStack, true);
                        if (emptyContainer == null || aBaseMetaTileEntity.addStackToSlot(3, emptyContainer, 1)) {
                            this.fuelTank.fill(containedFluid, true);
                            aBaseMetaTileEntity.decrStackSize(2, 1);
                        }
                    }
                }
            }
        }
    }

    @Override
    protected void updateFuel(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        if (this.fuelTank.getFluid() == null || this.fuelTank.getFluidAmount() < 100) {
            return;
        }
        int energyPer1000L = getEnergyForFluid(this.fuelTank.getFluid());
        if (energyPer1000L <= 0) {
            return;
        }
        int energyPer100L = energyPer1000L / 10;
        FluidStack drained = this.fuelTank.drain(100, true);
        if (drained != null && drained.amount > 0) {
            this.addProcessingEnergy(energyPer100L);
        }
    }

    @Override
    public boolean onRightclick(IGregTechTileEntity aBaseMetaTileEntity, EntityPlayer aPlayer) {
        if (aBaseMetaTileEntity.isClientSide() || aPlayer == null) {
            return true;
        }
        ItemStack held = aPlayer.getCurrentEquippedItem();
        if (held != null) {
            FluidStack fluid = GTUtility.getFluidForFilledItem(held, true);
            if (GTModHandler.isWater(fluid)) {
                ItemStack returned = fillFluidTankFromItemStack(this, held);
                if (returned != null) {
                    applyReturnedItem(aBaseMetaTileEntity, aPlayer, held, returned);
                    return true;
                }
            } else if (isFuelAllowed(fluid)) {
                ItemStack returned = fillFluidTankFromItemStack(this.fuelTank, held);
                if (returned != null) {
                    applyReturnedItem(aBaseMetaTileEntity, aPlayer, held, returned);
                    return true;
                }
            }
        }
        this.openGui(aPlayer);
        return true;
    }

    private static ItemStack fillFluidTankFromItemStack(IFluidTank destinationTank, ItemStack sourceStack) {
        if (destinationTank == null || sourceStack == null) {
            return null;
        }
        FluidStack containedFluid = GTUtility.getFluidForFilledItem(sourceStack, true);
        if (containedFluid == null || containedFluid.amount == 0) {
            return null;
        }
        int fillable = destinationTank.fill(containedFluid, false);
        if (fillable <= 0) {
            return null;
        }
        Item item = sourceStack.getItem();
        if (item instanceof IFluidContainerItem) {
            IFluidContainerItem container = (IFluidContainerItem) item;
            destinationTank.fill(container.drain(sourceStack, fillable, true), true);
            return null;
        }
        ItemStack empty = GTUtility.getContainerForFilledItem(sourceStack, false);
        destinationTank.fill(containedFluid, true);
        return empty;
    }

    private static void applyReturnedItem(IGregTechTileEntity baseTile, EntityPlayer player, ItemStack equipped,
        ItemStack returned) {
        if (player.capabilities.isCreativeMode) {
            return;
        }
        if (equipped.stackSize > 1) {
            equipped.stackSize--;
            if (!player.inventory.addItemStackToInventory(returned)) {
                World world = baseTile.getWorld();
                world.spawnEntityInWorld(
                    new EntityItem(
                        world,
                        baseTile.getXCoord() + 0.5,
                        baseTile.getYCoord() + 1.5,
                        baseTile.getZCoord() + 0.5,
                        returned));
            } else if (player instanceof EntityPlayerMP) {
                ((EntityPlayerMP) player).sendContainerToPlayer(player.inventoryContainer);
            }
        } else {
            player.inventory.setInventorySlotContents(player.inventory.currentItem, returned);
        }
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        if (this.fuelTank.getFluid() != null) {
            aNBT.setTag("mFuelTank", this.fuelTank.getFluid().writeToNBT(new NBTTagCompound()));
        }
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        if (aNBT.hasKey("mFuelTank")) {
            this.fuelTank.setFluid(FluidStack.loadFluidStackFromNBT(aNBT.getCompoundTag("mFuelTank")));
        }
    }

    @Override
    public ModularPanel buildUI(PosGuiData data, PanelSyncManager syncManager, UISettings uiSettings) {
        return new MTEBoilerLiquidGui(this).build(data, syncManager, uiSettings);
    }

    @Override
    public Widget<?> createFuelSlot() {
        return new ItemSlot()
            .slot(new ModularSlot((IItemHandler) this.inventoryHandler, 2).slotGroup("item_inv"))
            .widgetTheme(GTWidgetThemes.OVERLAY_ITEM_SLOT_IN);
    }

    @Override
    public Widget<?> createAshSlot() {
        return new ItemSlot()
            .slot(
                new ModularSlot((IItemHandler) this.inventoryHandler, 3).slotGroup("item_inv")
                    .accessibility(false, true))
            .widgetTheme(GTWidgetThemes.OVERLAY_ITEM_SLOT_OUT);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void onRandomDisplayTick(IGregTechTileEntity aBaseMetaTileEntity) {
        if (aBaseMetaTileEntity.isActive()) {
            ForgeDirection frontFacing = aBaseMetaTileEntity.getFrontFacing();
            if ((frontFacing.flag & (ForgeDirection.UP.flag | ForgeDirection.DOWN.flag)) == 0
                && !aBaseMetaTileEntity.hasCoverAtSide(frontFacing)
                && !aBaseMetaTileEntity.getOpacityAtSide(frontFacing)) {
                double x;
                double z;
                double oX = (double) aBaseMetaTileEntity.getOffsetX(frontFacing, 1) + 0.5;
                double oY = aBaseMetaTileEntity.getOffsetY(frontFacing, 1);
                double oZ = (double) aBaseMetaTileEntity.getOffsetZ(frontFacing, 1) + 0.5;
                double horizontal = (double) XSTR.XSTR_INSTANCE.nextFloat() * 10.0 / 16.0 - 0.3125;
                double y = oY + (double) XSTR.XSTR_INSTANCE.nextFloat() * 6.0 / 16.0;
                switch (frontFacing) {
                    case WEST:
                        x = oX - -0.48;
                        z = oZ + horizontal;
                        break;
                    case EAST:
                        x = oX + -0.48;
                        z = oZ + horizontal;
                        break;
                    case NORTH:
                        x = oX + horizontal;
                        z = oZ - -0.48;
                        break;
                    default:
                        x = oX + horizontal;
                        z = oZ + -0.48;
                        break;
                }
                WorldSpawnedEventBuilder.ParticleEventBuilder builder = (WorldSpawnedEventBuilder.ParticleEventBuilder) ((WorldSpawnedEventBuilder.ParticleEventBuilder) new WorldSpawnedEventBuilder.ParticleEventBuilder()
                    .setMotion(0.0, 0.0, 0.0)
                    .setPosition(x, y, z)).setWorld(this.getBaseMetaTileEntity().getWorld());
                ((WorldSpawnedEventBuilder.ParticleEventBuilder) builder.setIdentifier((Enum<?>) ParticleFX.SMOKE))
                    .run();
                ((WorldSpawnedEventBuilder.ParticleEventBuilder) builder.setIdentifier((Enum<?>) ParticleFX.FLAME))
                    .run();
            }
        }
    }

    @Override
    public void getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        super.getWailaBody(itemStack, currenttip, accessor, config);
        NBTTagCompound tag = accessor.getNBTData();
        if (tag.hasKey("fuelFluidName") && tag.getInteger("fuelFluidAmount") > 0) {
            currenttip.add(
                EnumChatFormatting.YELLOW + "Fuel: "
                    + EnumChatFormatting.RESET
                    + tag.getString("fuelFluidName")
                    + " ("
                    + tag.getInteger("fuelFluidAmount")
                    + " / "
                    + tag.getInteger("fuelFluidCapacity")
                    + " L)");
        }
    }

    @Override
    public void getWailaNBTData(EntityPlayerMP player, TileEntity tile, NBTTagCompound tag, World world, int x, int y,
        int z) {
        super.getWailaNBTData(player, tile, tag, world, x, y, z);
        if (this.fuelTank.getFluid() != null && this.fuelTank.getFluidAmount() > 0) {
            tag.setString("fuelFluidName", this.fuelTank.getFluid().getLocalizedName());
            tag.setInteger("fuelFluidAmount", this.fuelTank.getFluidAmount());
            tag.setInteger("fuelFluidCapacity", this.fuelTank.getCapacity());
        }
    }
}
