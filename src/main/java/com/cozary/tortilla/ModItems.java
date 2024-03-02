/*
 *
 *  * Copyright (c) 2024 Cozary
 *  *
 *  * This file is part of Tortilla, a mod made for Minecraft.
 *  *
 *  * Tortilla is free software: you can redistribute it and/or modify it
 *  * under the terms of the GNU General Public License as published
 *  * by the Free Software Foundation, either version 3 of the License, or
 *  * (at your option) any later version.
 *  *
 *  * Tortilla is distributed in the hope that it will be useful, but
 *  * WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE.  See the
 *  * GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU General Public License
 *  * License along with Tortilla.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package com.cozary.tortilla;

import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Tortilla.MOD_ID);

    public static final RegistryObject<Item> TORTILLA_ITEM = ITEMS.register("tortilla", () -> new BlockItemBase(ModBlocks.TORTILLA.get()));

}
