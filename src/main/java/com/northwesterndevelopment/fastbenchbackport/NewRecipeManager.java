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
            if(cache.containsKey(inventoryCrafting.getClass()))
                for (int l = 0; l < cache.get(inventoryCrafting.getClass()).size(); l++) {
                    if (cache.get(inventoryCrafting.getClass()).get(l).matches(inventoryCrafting, world))
                    {
                        IRecipe recipe = cache.get(inventoryCrafting.getClass()).get(l);
                        cache.get(inventoryCrafting.getClass()).remove(l);
                        cache.get(inventoryCrafting.getClass()).add(0, recipe);
                        System.out.println("CACHE HIT");
                        return recipe.getCraftingResult(inventoryCrafting);
                    }
                }

            for (j = 0; j < super.recipes.size(); ++j)
            {
                IRecipe irecipe = (IRecipe)this.recipes.get(j);

                if (irecipe.matches(inventoryCrafting, world))
                {
                    if(!cache.containsKey(inventoryCrafting.getClass())) cache.put(inventoryCrafting.getClass(), new ArrayList<IRecipe>());
                    System.out.println("CACHE MISS, ADDING");
                    cache.get(inventoryCrafting.getClass()).add(0, irecipe);
                    if (cache.get(inventoryCrafting.getClass()).size() > 256) {
                        System.out.println("CACHE OVER 256, REMOVING LAST ENTRY");
                        cache.get(inventoryCrafting.getClass()).remove(cache.get(inventoryCrafting.getClass()).size() - 1);
                    }
                    return irecipe.getCraftingResult(inventoryCrafting);
                }
            }

            return null;
        }
    }
}
