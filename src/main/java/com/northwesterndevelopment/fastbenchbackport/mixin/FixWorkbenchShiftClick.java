package com.northwesterndevelopment.fastbenchbackport.mixin;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import com.northwesterndevelopment.fastbenchbackport.IFixShiftClick;
import com.northwesterndevelopment.fastbenchbackport.IMoreShiftClickFixes;
import com.northwesterndevelopment.fastbenchbackport.NewWorkbenchInventoryCrafting;

@Mixin(value = ContainerWorkbench.class, priority = 20000)
public class FixWorkbenchShiftClick extends Container implements IFixShiftClick {

    @Shadow
    public InventoryCrafting craftMatrix = new NewWorkbenchInventoryCrafting(this.ths(), 3, 3);
    @Shadow
    public IInventory craftResult = new InventoryCraftResult();
    @Shadow
    private World worldObj;

    @Unique
    private ItemStack lastResult;

    /**
     * @author Jackson Abney
     * @reason Fix look up
     */
    @Overwrite
    public void onCraftMatrixChanged(IInventory inv) {
        this.craftResult.setInventorySlotContents(
            0,
            CraftingManager.getInstance()
                .findMatchingRecipe(this.craftMatrix, this.worldObj));
        this.lastResult = this.craftResult.getStackInSlot(0) != null ? this.craftResult.getStackInSlot(0)
            .copy() : null;
    }

    /**
     * @author Jackson Abney
     * @reason Rewrite shift click handling
     */
    @Overwrite
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        ItemStack itemstack = null;
        Slot slot = (Slot) this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (index == 0) {
                int count = ((IMoreShiftClickFixes) slot).CalculateCraftableCount();

                itemstack1.stackSize = count;

                if (!this.mergeItemStack(itemstack1, 10, 46, true)) {
                    return null;
                }

                slot.onSlotChange(itemstack1, itemstack);

                itemstack1.stackSize = count;

                ((IMoreShiftClickFixes) slot).ShiftCraft(player, itemstack1);

                return itemstack;
            } else if (index >= 10 && index < 37) {
                if (!this.mergeItemStack(itemstack1, 37, 46, false)) {
                    return null;
                }
            } else if (index >= 37 && index < 46) {
                if (!this.mergeItemStack(itemstack1, 10, 37, false)) {
                    return null;
                }
            } else if (!this.mergeItemStack(itemstack1, 10, 46, false)) {
                return null;
            }

            if (itemstack1.stackSize == 0) {
                slot.putStack((ItemStack) null);
            } else {
                slot.onSlotChanged();
            }

            if (itemstack1.stackSize == itemstack.stackSize) {
                return null;
            }

            slot.onPickupFromSlot(player, itemstack1);
        }

        return itemstack;
    }

    @Override
    public ItemStack slotClick(int slotId, int clickedButton, int mode, EntityPlayer player) {
        if (mode == 1 && (clickedButton == 0 || clickedButton == 1) && slotId != -999) {
            if (slotId < 0) {
                return null;
            }

            Slot slot2 = (Slot) this.inventorySlots.get(slotId);

            if (slot2 != null && slot2.canTakeStack(player)) {
                ItemStack itemstack3 = this.transferStackInSlot(player, slotId);

                if (itemstack3 != null) {
                    Item item = itemstack3.getItem();
                    ItemStack itemstack = itemstack3.copy();

                    return itemstack;
                }
            }
        }
        return super.slotClick(slotId, clickedButton, mode, player);
    }

    @Shadow
    public boolean canInteractWith(EntityPlayer player) {
        return false;
    }

    @Unique
    public ItemStack getLastResult() {
        return lastResult;
    }

    @Unique
    public ContainerWorkbench ths() {
        return (ContainerWorkbench) (Object) this;
    }
}
