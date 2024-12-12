package com.northwesterndevelopment.fastbenchbackport;

import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;

public class NewWorkbenchInventoryCrafting extends InventoryCrafting {
    public NewWorkbenchInventoryCrafting(ContainerWorkbench p_i1807_1_, int p_i1807_2_, int p_i1807_3_) {
        super(p_i1807_1_, p_i1807_2_, p_i1807_3_);
    }

    @Override
    public ItemStack decrStackSize(int index, int count)
    {
        if (this.stackList[index] != null)
        {
            ItemStack itemstack;

            if (this.stackList[index].stackSize <= count)
            {
                itemstack = this.stackList[index];
                this.stackList[index] = null;
                this.eventHandler.onCraftMatrixChanged(this);
                return itemstack;
            }
            else
            {
                itemstack = this.stackList[index].splitStack(count);

                if (this.stackList[index].stackSize == 0)
                {
                    this.stackList[index] = null;
                    this.eventHandler.onCraftMatrixChanged(this);
                }

                ItemStack lastResult = ((IFixShiftClick)this.eventHandler).getLastResult();
                ((ContainerWorkbench) this.eventHandler).craftResult.setInventorySlotContents(0, lastResult == null ? null : lastResult.copy());

                return itemstack;
            }
        }
        else
        {
            return null;
        }
    }
}
