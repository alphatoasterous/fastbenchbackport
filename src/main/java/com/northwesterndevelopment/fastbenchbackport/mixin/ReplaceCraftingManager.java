package com.northwesterndevelopment.fastbenchbackport.mixin;

import net.minecraft.item.crafting.CraftingManager;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import com.northwesterndevelopment.fastbenchbackport.NewRecipeManager;

@Mixin(value = CraftingManager.class, priority = 20000)
public class ReplaceCraftingManager {

    @Shadow
    private static final CraftingManager instance = new NewRecipeManager();
}
