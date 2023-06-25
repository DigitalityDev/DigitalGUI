/*
 * MIT License
 *
 * Copyright (c) 2023 Róbert Hanečák - Digitality
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package dev.digitality.digitalgui;

import dev.digitality.digitalgui.api.InteractiveItem;
import dev.digitality.digitalgui.listeners.GUIClickListener;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DigitalGUI {
    private static final Logger LOGGER = Logger.getLogger("DigitalGUI");

    /**
     * The item mapper, which is used to recognize which item was clicked based on NBT tag.
     */
    @Getter
    public static final HashMap<UUID, InteractiveItem> itemMapper = new HashMap<>();

    /**
     * Important to call in onEnable to register the GUI listener.
     *
     * @param plugin The plugin instance.
     */
    public static void register(Plugin plugin) {
        if (DigitalGUI.class.getPackage().getName().equals("dev.digitality" + ".digitalgui.DigitalGUI")) // Bypass relocation of strings
            LOGGER.log(Level.SEVERE, "DigitalGUI was shaded but not transformed! This is prone to errors! Please nag the author of " + plugin.getName() + " to use the relocation according to README!");

        Bukkit.getPluginManager().registerEvents(new GUIClickListener(), plugin);
    }

    /**
     * Fills the inventory with items, while also creating a border around it, with option to disable sides and keep only the top and bottom frame.
     *
     * @param inventory   The inventory to create the border in.
     * @param fillerPanel The material to use for the inner fill, if null it won't replace the original contents.
     * @param borderPanel The material to use for the border, if null it won't replace the original contents. If you want the borders to be the same as filler, you have to specify the same item as in fillerPanel.
     * @param full        Whether to create a full border or just a frame.
     */
    public static void fillInventory(Inventory inventory, @Nullable ItemStack fillerPanel, @Nullable ItemStack borderPanel, boolean full) {
        for (int i = 0; i < inventory.getSize(); i++) {
            if ((i % 9 == 0 || (i - 8) % 9 == 0) && borderPanel != null)
                inventory.setItem(i, borderPanel);
            else if (full && (i < 9 || i >= inventory.getSize() - 9) && borderPanel != null)
                inventory.setItem(i, borderPanel);
            else if (fillerPanel != null)
                inventory.setItem(i, fillerPanel);
        }
    }

    /**
     * Fills the inventory with items, while also creating a border around it.
     *
     * @param inventory   The inventory to create the border in.
     * @param fillerPanel The material to use for the inner fill, if null it won't replace the original contents.
     * @param borderPanel The material to use for the border, if null it won't replace the original contents. If you want the borders to be the same as filler, you have to specify the same item as in fillerPanel.
     */
    public static void fillInventory(Inventory inventory, @Nullable ItemStack fillerPanel, @Nullable ItemStack borderPanel) {
        fillInventory(inventory, fillerPanel, borderPanel, true);
    }
}
