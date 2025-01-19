package com.northwesterndevelopment.fastbenchbackport.mixin;

import java.lang.reflect.Method;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.northwesterndevelopment.fastbenchbackport.IFixShiftClick;
import com.northwesterndevelopment.fastbenchbackport.IMoreShiftClickFixes;
import com.northwesterndevelopment.fastbenchbackport.NewWorkbenchInventoryCrafting;

import cpw.mods.fml.common.FMLCommonHandler;

@Mixin(value = SlotCrafting.class, priority = 20000)
public class MoreShiftClickFixes implements IMoreShiftClickFixes {

    @Shadow
    private IInventory craftMatrix;
    @Shadow
    private EntityPlayer thePlayer;
    @Shadow
    private int amountCrafted;

    private static Class<?> IDamagable;

    @Inject(method = "<init>", at = @At("HEAD"))
    private static void constructorHead(EntityPlayer p_i1823_1_, IInventory p_i1823_2_, IInventory p_i1823_3_,
        int p_i1823_4_, int p_i1823_5_, int p_i1823_6_, CallbackInfo ci) {
        try {
            IDamagable = Class.forName("gregtech.api.items.MetaGeneratedTool");
        } catch (Exception e) {
            System.out.println("ERROR " + e);
        }
    }

    /**
     * @author Jackson Abney
     * @reason Rewrite shift click handling
     */
    @Overwrite
    public void onPickupFromSlot(EntityPlayer p_82870_1_, ItemStack p_82870_2_) {
        FMLCommonHandler.instance()
            .firePlayerCraftingEvent(p_82870_1_, p_82870_2_, craftMatrix);
        this.ths()
            .onCrafting(p_82870_2_);

        for (int i = 0; i < this.craftMatrix.getSizeInventory(); i++) {
            ItemStack itemstack1 = this.craftMatrix.getStackInSlot(i);

            if (itemstack1 != null) {
                this.craftMatrix.decrStackSize(i, 1);

                if (itemstack1.getItem()
                    .hasContainerItem(itemstack1)) {
                    ItemStack itemstack2 = itemstack1.getItem()
                        .getContainerItem(itemstack1);

                    if (itemstack2 != null && itemstack2.isItemStackDamageable()
                        && itemstack2.getItemDamage() > itemstack2.getMaxDamage()) {
                        MinecraftForge.EVENT_BUS.post(new PlayerDestroyItemEvent(thePlayer, itemstack2));
                        continue;
                    }

                    if (!itemstack1.getItem()
                        .doesContainerItemLeaveCraftingGrid(itemstack1)
                        || !this.thePlayer.inventory.addItemStackToInventory(itemstack2)) {
                        if (this.craftMatrix.getStackInSlot(i) == null) {
                            this.craftMatrix.setInventorySlotContents(i, itemstack2);
                        } else {
                            this.thePlayer.dropPlayerItemWithRandomChoice(itemstack2, false);
                        }
                    }
                }
            }
        }
    }

    @Unique
    public SlotCrafting ths() {
        return (SlotCrafting) (Object) this;
    }

    @Override
    public int CalculateCraftableCount() {
        int smallestValue = Integer.MAX_VALUE;
        for (int i = 0; i < this.craftMatrix.getSizeInventory(); i++) {
            ItemStack itemstack1 = this.craftMatrix.getStackInSlot(i);

            if (itemstack1 != null) {
                boolean damage = false;
                if (itemstack1.getItem()
                    .hasContainerItem(itemstack1)) {
                    ItemStack itemStack2 = itemstack1.getItem()
                        .getContainerItem(itemstack1);
                    if (itemStack2 != null && itemStack2.isItemStackDamageable()) {
                        System.out.println(itemStack2.getItemDamage() + " " + itemStack2.getMaxDamage());
                        damage = true;
                        if (itemStack2.getMaxDamage() - itemStack2.getItemDamage() < smallestValue) {
                            smallestValue = itemStack2.getMaxDamage() - itemStack2.getItemDamage();
                        }
                    }
                    if (IDamagable.isAssignableFrom(
                        itemStack2.getItem()
                            .getClass())) {
                        try {
                            Method getMaxToolDamage = itemStack2.getItem()
                                .getClass()
                                .getMethod("getToolMaxDamage", ItemStack.class);
                            Method getToolDamage = itemStack2.getItem()
                                .getClass()
                                .getMethod("getToolDamage", ItemStack.class);
                            Method getToolStats = itemStack2.getItem()
                                .getClass()
                                .getMethod("getToolStats", ItemStack.class);
                            Object stats = getToolStats.invoke(itemStack2.getItem(), itemStack2);
                            Method getUsageAmount = stats.getClass()
                                .getMethod("getToolDamagePerContainerCraft");
                            long maxDamage = (long) getMaxToolDamage.invoke(itemStack2.getItem(), itemStack2);
                            long currentDamage = (long) getToolDamage.invoke(itemStack2.getItem(), itemStack2);
                            int perUse = (int) getUsageAmount.invoke(stats);

                            long temp = maxDamage - currentDamage;
                            int maxCount = (int) temp / perUse;
                            if (maxCount < smallestValue) smallestValue = maxCount;
                            damage = true;
                        } catch (Exception e) {
                            System.out.println("ERROR " + e);
                        }
                    }
                }
                if (itemstack1.stackSize < smallestValue && !damage) {
                    System.out.println(itemstack1.getDisplayName() + " NO DAMAGE");
                    smallestValue = itemstack1.stackSize;
                }
            }
        }
        return Math.min(smallestValue, 64);
    }

    @Override
    public void ShiftCraft(EntityPlayer p_82870_1_, ItemStack p_82870_2_) {
        int recipeCount = ((IFixShiftClick) ((NewWorkbenchInventoryCrafting) craftMatrix).eventHandler)
            .getLastResult().stackSize;

        FMLCommonHandler.instance()
            .firePlayerCraftingEvent(p_82870_1_, p_82870_2_, craftMatrix);
        int craftCount = p_82870_2_.stackSize / recipeCount;
        this.ths()
            .onCrafting(p_82870_2_, craftCount);

        for (int i = 0; i < this.craftMatrix.getSizeInventory(); ++i) {
            ItemStack itemstack1 = this.craftMatrix.getStackInSlot(i);

            if (itemstack1 != null) {
                this.craftMatrix.decrStackSize(i, craftCount);

                if (itemstack1.getItem()
                    .hasContainerItem(itemstack1)) {
                    ItemStack itemstack2;
                    itemstack2 = itemstack1.getItem()
                        .getContainerItem(itemstack1);
                    for (int k = 1; k < craftCount; k++) itemstack2 = itemstack2.getItem()
                        .getContainerItem(itemstack2);

                    if (itemstack2 != null && itemstack2.isItemStackDamageable()
                        && itemstack2.getItemDamage() > itemstack2.getMaxDamage()) {
                        MinecraftForge.EVENT_BUS.post(new PlayerDestroyItemEvent(thePlayer, itemstack2));
                        continue;
                    }

                    if (!itemstack1.getItem()
                        .doesContainerItemLeaveCraftingGrid(itemstack1)
                        || !this.thePlayer.inventory.addItemStackToInventory(itemstack2)) {
                        if (this.craftMatrix.getStackInSlot(i) == null) {
                            this.craftMatrix.setInventorySlotContents(i, itemstack2);
                        } else {
                            this.thePlayer.dropPlayerItemWithRandomChoice(itemstack2, false);
                        }
                    }
                }
            }
        }
    }
}
