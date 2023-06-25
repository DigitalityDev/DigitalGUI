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

package dev.digitality.digitalgui.api;

import de.tr7zw.changeme.nbtapi.NBTItem;
import dev.digitality.digitalgui.DigitalGUI;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Getter @Setter
public class InteractiveItem extends ItemStack {
    /**
     * The slot of the item in the GUI. Optional, but recommended. Defaults to -1.
     */
    private int slot;

    private BiConsumer<Player, ClickType> clickCallback;
    private Consumer<Player> leftClickCallback;
    private Consumer<Player> rightClickCallback;

    public InteractiveItem(Material material, int slot, String displayName, String... lore) {
        super(material);

        this.slot = slot;

        ItemMeta meta = this.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(displayName);
            meta.setLore(Arrays.asList(lore));

            this.setItemMeta(meta);
        }

        createMapping();
    }

    public InteractiveItem(Material material, int slot) {
        super(material);

        this.slot = slot;

        createMapping();
    }

    public InteractiveItem(ItemStack itemStack, int slot) {
        super(itemStack);

        this.slot = slot;

        createMapping();
    }

    public InteractiveItem(Material material) {
        super(material);

        this.slot = -1;

        createMapping();
    }

    private void createMapping() {
        UUID uuid = UUID.randomUUID();
        DigitalGUI.getItemMapper().put(uuid, this);

        NBTItem nbtItem = new NBTItem(this);
        nbtItem.setUUID("digitalgui:id", uuid);
        this.setItemMeta(nbtItem.getItem().getItemMeta());
    }

    public String getDisplayName() {
        ItemMeta meta = this.getItemMeta();

        if (meta != null)
            return meta.getDisplayName();

        return null;
    }

    public void setDisplayName(String displayName) {
        ItemMeta meta = this.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(displayName);

            this.setItemMeta(meta);
        }
    }

    public List<String> getLore() {
        ItemMeta meta = this.getItemMeta();

        if (meta != null) {
            return meta.getLore();
        }

        return new ArrayList<>();
    }

    public void setLore(List<String> lore) {
        ItemMeta meta = this.getItemMeta();

        if (meta != null) {
            meta.setLore(lore);
            this.setItemMeta(meta);
        }
    }

    public void setLore(String... lore) {
        ItemMeta meta = this.getItemMeta();

        if (meta != null) {
            if (lore == null || lore.length == 0) {
                meta.setLore(null);
            } else {
                List<String> loreList = new ArrayList<>();

                for (String line : lore)
                    loreList.addAll(Arrays.asList(line.split("\n")));

                meta.setLore(loreList);
            }

            this.setItemMeta(meta);
        }
    }

    public void addItemFlags(ItemFlag... flags) {
        ItemMeta meta = this.getItemMeta();

        if (meta != null) {
            meta.addItemFlags(flags);
            this.setItemMeta(meta);
        }
    }

    public void removeItemFlags(ItemFlag... flags) {
        ItemMeta meta = this.getItemMeta();

        if (meta != null) {
            meta.removeItemFlags(flags);
            this.setItemMeta(meta);
        }
    }

    public Set<ItemFlag> getItemFlags() {
        ItemMeta meta = this.getItemMeta();

        return meta != null ? meta.getItemFlags() : new HashSet<>();
    }

    public void setGlow(boolean active) {
        if (active) {
            this.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 1);
            this.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        } else {
            this.removeEnchantment(Enchantment.DAMAGE_ALL);
            this.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
    }

    public void setSkullOwner(String owner) {
        ItemMeta meta = this.getItemMeta();

        if (!(meta instanceof SkullMeta))
            return;

        ((SkullMeta) meta).setOwner(owner);
        this.setItemMeta(meta);
    }

    //

    public InteractiveItem onClick(BiConsumer<Player, ClickType> consumer) {
        clickCallback = consumer;

        return this;
    }

    public InteractiveItem onLeftClick(Consumer<Player> consumer) {
        leftClickCallback = consumer;

        return this;
    }

    public InteractiveItem onRightClick(Consumer<Player> consumer) {
        rightClickCallback = consumer;

        return this;
    }

    // Handles InventoryClickEvent
    public void handleClick(Player player, ClickType clickType) {
        if ((clickType == ClickType.LEFT || clickType == ClickType.SHIFT_LEFT) && leftClickCallback != null)
            leftClickCallback.accept(player);
        else if ((clickType == ClickType.RIGHT || clickType == ClickType.SHIFT_RIGHT) && rightClickCallback != null)
            rightClickCallback.accept(player);

        if (clickCallback != null)
            clickCallback.accept(player, clickType);
    }

    // Handles PlayerInteractEvent
    public void handleClick(Player player, Action clickType) {
        if ((clickType == Action.LEFT_CLICK_AIR || clickType == Action.LEFT_CLICK_BLOCK) && leftClickCallback != null)
            leftClickCallback.accept(player);
        else if ((clickType == Action.RIGHT_CLICK_AIR || clickType == Action.RIGHT_CLICK_BLOCK) && rightClickCallback != null)
            rightClickCallback.accept(player);

        if (clickCallback != null)
            clickCallback.accept(player, clickType == Action.LEFT_CLICK_AIR || clickType == Action.LEFT_CLICK_BLOCK ? ClickType.LEFT : ClickType.RIGHT);
    }
}