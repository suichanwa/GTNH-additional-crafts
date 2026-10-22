package gtnh_additional_crafts.machine;

import net.minecraftforge.fluids.IFluidTank;

import com.cleanroommc.modularui.api.value.IDoubleValue;
import com.cleanroommc.modularui.api.widget.IWidget;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.utils.Alignment;
import com.cleanroommc.modularui.utils.item.IItemHandler;
import com.cleanroommc.modularui.value.sync.BooleanSyncValue;
import com.cleanroommc.modularui.value.sync.DoubleSyncValue;
import com.cleanroommc.modularui.value.sync.FloatSyncValue;
import com.cleanroommc.modularui.value.sync.FluidSlotSyncHandler;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.widget.Widget;
import com.cleanroommc.modularui.widgets.ProgressWidget;
import com.cleanroommc.modularui.widgets.ToggleButton;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.cleanroommc.modularui.widgets.slot.FluidSlot;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;
import com.cleanroommc.modularui.widgets.slot.ModularSlot;
import com.gtnewhorizon.gtnhlib.util.numberformatting.NumberFormatUtil;

import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.modularui2.GTGuis;
import gregtech.api.modularui2.GTWidgetThemes;
import gregtech.api.modularui2.common.CommonButtons;
import gregtech.common.modularui2.widget.GTProgressWidget;

public class MTEBoilerLiquidGui {

    private final MTEBoilerLiquidBase base;
    private final IGregTechTileEntity baseMetaTileEntity;

    public MTEBoilerLiquidGui(MTEBoilerLiquidBase base) {
        this.base = base;
        this.baseMetaTileEntity = base.getBaseMetaTileEntity();
    }

    public ModularPanel build(PosGuiData data, PanelSyncManager syncManager, UISettings uiSettings) {
        syncManager.registerSlotGroup("item_inv", 0);
        FloatSyncValue heat = new FloatSyncValue(
            () -> (float) this.base.mTemperature / (float) this.base.maxProgresstime());
        syncManager.syncValue("heat", heat);

        BooleanSyncValue mufflerSyncer = (BooleanSyncValue) new BooleanSyncValue(
            this.baseMetaTileEntity::isMuffled,
            this.baseMetaTileEntity::setMuffler).allowC2S();
        syncManager.syncValue("mufflerSyncer", mufflerSyncer);

        IWidget waterSlots = Flow.column()
            .coverChildren()
            .child(
                new ItemSlot()
                    .slot(new ModularSlot((IItemHandler) this.base.inventoryHandler, 0).slotGroup("item_inv"))
                    .widgetTheme(GTWidgetThemes.OVERLAY_ITEM_SLOT_IN))
            .child((IWidget) new Widget<>().widgetTheme(GTWidgetThemes.PICTURE_CANISTER).size(18))
            .child(
                new ItemSlot()
                    .slot(
                        new ModularSlot((IItemHandler) this.base.inventoryHandler, 1).slotGroup("item_inv")
                            .accessibility(false, true))
                    .widgetTheme(GTWidgetThemes.OVERLAY_ITEM_SLOT_OUT));

        IWidget indicators = Flow.row()
            .coverChildren()
            .crossAxisAlignment(Alignment.CrossAxis.CENTER)
            .childPadding(3)
            .child(
                new FluidSlot()
                    .syncHandler(
                        new FluidSlotSyncHandler((IFluidTank) this.base.getSteamTank()).canDrainSlot(false)
                            .canFillSlot(false)
                            .controlsAmount(false))
                    .alwaysShowFull(false)
                    .size(10, 54))
            .child(
                new FluidSlot()
                    .syncHandler(
                        new FluidSlotSyncHandler((IFluidTank) this.base.getFluidStackTank()).canDrainSlot(false)
                            .canFillSlot(false)
                            .controlsAmount(false))
                    .alwaysShowFull(false)
                    .size(10, 54))
            .child(
                new FluidSlot()
                    .syncHandler(
                        new FluidSlotSyncHandler((IFluidTank) this.base.getFuelTank()).canDrainSlot(false)
                            .canFillSlot(false)
                            .controlsAmount(false))
                    .alwaysShowFull(false)
                    .size(10, 54))
            .child(
                ((ProgressWidget) ((ProgressWidget) ((ProgressWidget) new GTProgressWidget().syncHandler("heat"))
                    .tooltipDynamic(
                        a -> a.add(
                            NumberFormatUtil.formatNumber(
                                (Number) ((int) (heat.getFloatValue() * (float) this.base.maxProgresstime())))
                                + "°C")))
                                    .direction(ProgressWidget.Direction.UP)
                                    .widgetTheme(GTWidgetThemes.PROGRESSBAR_BOILER_HEAT)).size(10, 54));

        IWidget fuelSlots = Flow.column()
            .coverChildren()
            .childIf(this.base.doesAddAshSlot(), this.base::createAshSlot)
            .child(
                ((ProgressWidget) ((ProgressWidget) new GTProgressWidget().value((IDoubleValue) new DoubleSyncValue(() -> {
                    if (this.base.mProcessingEnergy <= 0 || this.base.fuelMaxEnergy <= 0) {
                        return 0.0;
                    }
                    return Math.max(0.14285715f, (float) this.base.mProcessingEnergy / (float) this.base.fuelMaxEnergy);
                }))).direction(ProgressWidget.Direction.UP)
                    .widgetTheme(GTWidgetThemes.PROGRESSBAR_FUEL)).size(14)
                        .margin(2))
            .childIf(this.base.doesAddFuelSlot(), this.base::createFuelSlot);

        IWidget mufflerSlot = Flow.column()
            .coverChildren()
            .topRel(0.0f)
            .rightRel(0.0f)
            .child(
                ((ToggleButton) CommonButtons.createMuffleButton("mufflerSyncer").disableThemeBackground(true))
                    .disableHoverThemeBackground(true));

        return (ModularPanel) ((ModularPanel) GTGuis
            .mteTemplatePanelBuilder((IMetaTileEntity) this.base, data, syncManager, uiSettings)
            .build()
            .child(
                Flow.row()
                    .horizontalCenter()
                    .top(25)
                    .coverChildren()
                    .childPadding(9)
                    .child(waterSlots)
                    .child(indicators)
                    .child(fuelSlots)))
                        .child(Flow.row().full().padding(4).child(mufflerSlot));
    }
}
