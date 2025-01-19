package com.northwesterndevelopment.fastbenchbackport;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface IMoreShiftClickFixes {

    int CalculateCraftableCount();

    void ShiftCraft(EntityPlayer p_82870_1_, ItemStack p_82870_2_);
}
