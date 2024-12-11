package com.northwesterndevelopment.fastbenchbackport.mixin;

import com.northwesterndevelopment.fastbenchbackport.IFixShiftClick;
import com.northwesterndevelopment.fastbenchbackport.NewWorkbenchInventoryCrafting;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ContainerWorkbench.class)
public class FixWorkbenchShiftClick implements IFixShiftClick {
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
    public void onCraftMatrixChanged(IInventory inv)
    {
        this.craftResult.setInventorySlotContents(0, CraftingManager.getInstance().findMatchingRecipe(this.craftMatrix, this.worldObj));
        this.lastResult = this.craftResult.getStackInSlot(0) != null ? this.craftResult.getStackInSlot(0).copy() : null;
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
