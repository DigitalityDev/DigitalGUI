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

package dev.digitality.digitalgui.listeners;

import de.tr7zw.changeme.nbtapi.NBT;
import dev.digitality.digitalgui.DigitalGUI;
import dev.digitality.digitalgui.api.IGUI;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerAnimationType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

public class GUIClickListener implements Listener {
    private static final HashMap<UUID, Long> interactTimeout = new HashMap<>();

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        UUID uuid = getUUID(e.getCurrentItem());

        boolean isGUIInv = e.getClickedInventory() != null && e.getClickedInventory().getHolder() != null && e.getClickedInventory().getHolder() instanceof IGUI;

        // Number keys also move the hotbar item when the hovered slot is empty.
        boolean isInteractiveHotbarItem = e.getClick() == ClickType.NUMBER_KEY && getUUID(player.getInventory().getItem(e.getHotbarButton())) != null;

        if (isGUIInv || uuid != null || isInteractiveHotbarItem) {
            e.setCancelled(true);
            player.updateInventory();

            if (DigitalGUI.getItemMapper().containsKey(uuid))
                DigitalGUI.getItemMapper().get(uuid).handleClick(player, e.getClick());
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        UUID uuid = getUUID(e.getItem());

        if (uuid == null)
            return;

        if (DigitalGUI.getItemMapper().containsKey(uuid) && System.currentTimeMillis() >= interactTimeout.getOrDefault(e.getPlayer().getUniqueId(), -1L)) {
            DigitalGUI.getItemMapper().get(uuid).handleClick(e.getPlayer(), e.getAction());

            interactTimeout.put(e.getPlayer().getUniqueId(), System.currentTimeMillis() + 100L); // Timeout prevents the GUI opening twice
        }

        e.setCancelled(true);
    }

    @EventHandler
    public void onAnimation(PlayerAnimationEvent e) { // This compensates for the fact that PlayerInteractEvent is not called when the player is in Adventure mode
        if (e.getAnimationType() != PlayerAnimationType.ARM_SWING || e.getPlayer().getTargetBlock(new HashSet<Material>(), 5).getType() == Material.AIR || e.getPlayer().getGameMode() != GameMode.ADVENTURE)
            return;

        UUID uuid = getUUID(e.getPlayer().getInventory().getItemInHand());

        if (uuid == null)
            return;

        if (DigitalGUI.getItemMapper().containsKey(uuid) && System.currentTimeMillis() >= interactTimeout.getOrDefault(e.getPlayer().getUniqueId(), -1L)) {
            DigitalGUI.getItemMapper().get(uuid).handleClick(e.getPlayer(), Action.RIGHT_CLICK_BLOCK);

            interactTimeout.put(e.getPlayer().getUniqueId(), System.currentTimeMillis() + 100L); // Timeout prevents the GUI opening twice
        }

        e.setCancelled(true);
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        UUID uuid = getUUID(e.getItemDrop().getItemStack());

        if (e.getItemDrop().getItemStack().getType() != Material.AIR && uuid != null)
            e.setCancelled(true);
    }

    private UUID getUUID(ItemStack item) {
        if (item == null || item.getType() == Material.AIR)
            return null;

        return NBT.get(item, nbt -> {
            if (!nbt.hasTag("digitalgui:id"))
                return null;

            return UUID.fromString(nbt.getString("digitalgui:id"));
        });
    }
}