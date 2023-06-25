# DigitalGUI
### Modern GUI system for Developers

## Table of Contents
- [0. Features](#0-features)
- [1. Installation](#1-installation)
  - [Maven](#maven)
  - [Gradle](#gradle)
- [2. Shading + Relocating (Very important!)](#2-shading--relocating-very-important)
  - [Maven](#maven-1)
  - [Gradle](#gradle-1)
- [3. GUI Usage](#3-gui-usage)
  - [3.1 Creating a GUI](#31-creating-a-gui)
  - [3.2 Opening a GUI](#32-opening-a-gui)
- [4. InteractiveItem Usage](#4-interactiveitem-usage)
  - [4.1 What is an InteractiveItem?](#41-what-is-an-interactiveitem)
  - [4.2 Creating an InteractiveItem](#42-creating-an-interactiveitem)
  - [4.3 Setting the click handler](#43-setting-the-click-handler)
  - [4.4 Setting the glow](#44-setting-the-glow)
- [5. Pagination Usage](#5-pagination-usage)
- [6. Utilities](#6-utilities)
  - [6.1 Creating borders and filling](#61-creating-borders-and-filling)
- [7. Putting it all together](#7-putting-it-all-together)

## 0. Features
- Easy to use
- Capable of creating complex GUIs
- Maintainer has (unfortunately) a lot of free time
- Pagination (TODO!)

## 1. Installation
### Maven
```xml
<repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
</repository>
<dependency>
    <groupId>com.github.DigitalityDev</groupId>
    <artifactId>DigitalGUI</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Gradle
```groovy
repositories {
    maven { url "https://jitpack.io" }
}

dependencies {
    implementation "com.github.DigitalityDev:DigitalGUI:1.0.0"
}
```

## 2. Shading + Relocating (Very important!)
### Maven
```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-shade-plugin</artifactId>
            <version>3.2.4</version>
            <configuration>
                <relocations>
                    <relocation>
                        <pattern>dev.digitality.digitalgui</pattern>
                        <shadedPattern>your.package.here.digitalgui</shadedPattern>
                    </relocation>
                </relocations>
            </configuration>
        </plugin>
    </plugins>
</build>
```

### Gradle
```groovy
plugins {
    id "com.github.johnrengelman.shadow" version "8.1.1"
}

shadowJar {
    relocate "dev.digitality.digitalgui", "your.package.here.digitalgui"
}
```

## 3. GUI Usage
This isn't what exactly makes the GUI interactive, please check the section below.

### 3.1 Creating a GUI
To create a GUI you need to make a separate class implementing the `IGUI` interface and `getInventory()` method.
If you wish to pass anything, such as a player, you can do so by adding parameters to the constructor.
```java
public class ExampleGUI implements IGUI {
    private final Player player;

    public ExampleGUI(Player player) {
        this.player = player;
    }

    @Override
    public Inventory getInventory() {
        Inventory inventory = Bukkit.createInventory(this, 9, "Example GUI"); // Don't forget the "this"!
        inventory.setItem(0, new ItemStack(Material.DIAMOND));
        
        return inventory;
    }
}
```
### 3.2 Opening a GUI
Opening a GUI as simple as calling the `getInventory()` method.
```java
player.openInventory(new ExampleGUI(player).getInventory());
```

## 4. InteractiveItem Usage
### 4.1 What is an InteractiveItem?
InteractiveItem serves as a clickable **drop-in replacement for ItemStack**. This means, that everything you can do with ItemStacks works with InteractiveItems as well.
InteractiveItem also works with **both InventoryClickEvent and PlayerInteractEvent**. This means, that you can use InteractiveItems in both GUIs and in the world.

This includes, but is not limited to:
- Setting the item's name with one line
- Setting the item's lore with one line
- Setting the item's glow with one line
- Setting left/right click actions
### 4.2 Creating an InteractiveItem
Creating an InteractiveItem is as simple as creating a new instance of the class. You can optionally specify the slot, the item's name and the item's lore. See the source code for more.
```java
InteractiveItem item = new InteractiveItem(Material.DIAMOND, 0, "§aDiamond", "§7This is a diamond.");
```
### 4.3 Setting the click handler
To set the click handler, you need to call the `onClick()` method. This method takes a function as a parameter, with player and click type as parameters.
```java
item.onClick((player, clickType) -> {
    player.sendMessage("You clicked the diamond!");
});
```
You can also use onLeftClick and onRightClick methods, which take a function with player as a parameter.
```java
item.onLeftClick(player -> {
    player.sendMessage("You left clicked the diamond!");
});
```
### 4.4 Setting the glow
To set the glow, you need to call the `setGlow()` method. This method takes a boolean as a parameter.
```java
item.setGlow(true);
```

## 5. Pagination Usage
TODO!

## 6. Utilities
### 6.1 Creating borders and filling
You can use the `fillInventory()` method to fill an inventory with an item and create a frame around it.
```java
Inventory inventory = Bukkit.createInventory(this, 9, "Example GUI"); // Don't forget the "this"!
ItemStack fillerItem = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
ItemStack borderItem = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
DigitalGUI.fillInventory(inventory, fillerItem, borderItem);
```

## 7. Putting it all together
Here is a final example which you can use as a reference.
```java
public class ExampleGUI implements IGUI {
    private final Player player;

    public ExampleGUI(Player player) {
        this.player = player;
    }

    @Override
    public Inventory getInventory() {
        Inventory inventory = Bukkit.createInventory(this, 9, "Example GUI"); // Don't forget the "this"!
        DigitalGUI.fillInventory(inventory, new InteractiveItem(Material.GRAY_STAINED_GLASS_PANE, 0, "§7"), new InteractiveItem(Material.BLACK_STAINED_GLASS_PANE, 0, "§7"));
        InteractiveItem item = new InteractiveItem(Material.DIAMOND, 0, "§aDiamond", "§7This is a diamond.")
              .onClick((player, clickType) -> { // This will run for any click action
                player.sendMessage("You clicked the diamond!");
              })
              .onLeftClick(player -> { // This will run on left click, regardless of whether it was InventoryClickEvent or PlayerInteractEvent
                player.sendMessage("You left clicked the diamond!");
              })
              .onRightClick(player -> { // This will run on right click, regardless of whether it was InventoryClickEvent or PlayerInteractEvent
                player.sendMessage("You right clicked the diamond!");
              });
        inventory.setItem(item.getSlot(), item);
        
        return inventory;
    }
}
```