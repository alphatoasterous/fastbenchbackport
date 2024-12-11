package com.northwesterndevelopment.fastbenchbackport;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewRecipeManager extends CraftingManager {
    private Map<Class, List<IRecipe>> cache = new HashMap<Class, List<IRecipe>>();

    @Override
    public ItemStack findMatchingRecipe(InventoryCrafting inventoryCrafting, World world) {
        int i = 0;
        ItemStack itemstack = null;
        ItemStack itemstack1 = null;
        int j;

        for (j = 0; j < inventoryCrafting.getSizeInventory(); ++j)
        {
            ItemStack itemstack2 = inventoryCrafting.getStackInSlot(j);

            if (itemstack2 != null)
            {
                if (i == 0)
                {
                    itemstack = itemstack2;
                }

                if (i == 1)
                {
                    itemstack1 = itemstack2;
                }

                ++i;
            }
        }

        if (i == 2 && itemstack.getItem() == itemstack1.getItem() && itemstack.stackSize == 1 && itemstack1.stackSize == 1 && itemstack.getItem().isRepairable())
        {
            Item item = itemstack.getItem();
            int j1 = item.getMaxDamage() - itemstack.getItemDamageForDisplay();
            int k = item.getMaxDamage() - itemstack1.getItemDamageForDisplay();
            int l = j1 + k + item.getMaxDamage() * 5 / 100;
            int i1 = item.getMaxDamage() - l;

            if (i1 < 0)
            {
                i1 = 0;
            }

            return new ItemStack(itemstack.getItem(), 1, i1);
        }
        else
        {
            if(cache.containsKey(inventoryCrafting.getClass())) {
                List<IRecipe> cacheList = cache.get(inventoryCrafting.getClass());
                for (int l = 0; l < cacheList.size(); l++) {
                    IRecipe recipe = cacheList.get(l);
                    if (recipe.matches(inventoryCrafting, world)) {
                        if (l != 0) {
                            cacheList.remove(l);
                            cacheList.add(0, recipe);
                        }
                        return recipe.getCraftingResult(inventoryCrafting);
                    }
                }
            }

            for (j = 0; j < super.recipes.size(); ++j)
            {
                IRecipe irecipe = (IRecipe)this.recipes.get(j);

                if (irecipe.matches(inventoryCrafting, world))
                {
                    if(!cache.containsKey(inventoryCrafting.getClass())) cache.put(inventoryCrafting.getClass(), new ArrayList<IRecipe>());
                    List<IRecipe> cacheList = cache.get(inventoryCrafting.getClass());
                    cacheList.add(0, irecipe);
                    if (cacheList.size() > 256) {
                        cacheList.remove(cache.get(inventoryCrafting.getClass()).size() - 1);
                    }
                    return irecipe.getCraftingResult(inventoryCrafting);
                }
            }

            return null;
        }
    }
}
