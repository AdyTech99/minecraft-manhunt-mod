package com.adytechmc.mcmanhunt;

import java.util.ArrayList;
import net.minecraft.item.ItemStack;

public class PersistanceItems {
    private static ArrayList<PersistanceItems> persistentPlayers = new ArrayList<>();
    private String playerUUID;
    private ItemStack itemStack;

    public PersistanceItems(String playerUUID, ItemStack itemStack) {
        this.playerUUID = playerUUID;
        this.itemStack = itemStack;
    }

    public String getPlayerUUID() {
        return this.playerUUID;
    }

    public ItemStack getStack() {
        return this.itemStack;
    }

    public static void removeItems(PersistanceItems player) {
        persistentPlayers.remove(player);
    }

    public static void addItems(PersistanceItems player) {
        persistentPlayers.add(player);
    }

    public static ArrayList<PersistanceItems> getItems() {
        return persistentPlayers;
    }
}
