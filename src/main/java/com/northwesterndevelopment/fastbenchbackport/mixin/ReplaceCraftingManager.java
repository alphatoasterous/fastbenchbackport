package com.northwesterndevelopment.fastbenchbackport.mixin;

import com.northwesterndevelopment.fastbenchbackport.NewRecipeManager;
import net.minecraft.item.crafting.CraftingManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(CraftingManager.class)
public class ReplaceCraftingManager {
    @Shadow
    private static final CraftingManager instance = new NewRecipeManager();
}
