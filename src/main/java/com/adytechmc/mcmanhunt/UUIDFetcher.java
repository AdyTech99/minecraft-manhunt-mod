package com.adytechmc.mcmanhunt;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.world.World;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;
import java.util.UUID;

public class UUIDFetcher {

    public static String getUUIDFromName(String playerName) {
        try {
            String url = "https://api.mojang.com/users/profiles/minecraft/" + playerName;
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            // Parse JSON response to get the UUID
            String responseString = response.toString();
            String uuid = responseString.substring(7, 39); // Extracting the UUID from the JSON response
            return uuid;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public static UUID convertStringToUUID(String uuidString) {
        return UUID.fromString(uuidString);
    }


    public static UUID getUUIDFromNameOffline(String name, World world){
        UUID uuid = null;
        for(PlayerEntity player : world.getPlayers()){
            if(player.getName().toString().equalsIgnoreCase(name)){
                player.sendMessage(Text.literal("THIS WORKS!!"));
                return player.getUuid();
            }
        }
        return uuid;
    }

    /*public static void main(String[] args) {
        String playerName = "YourPlayerName"; // Replace with the actual player name
        String uuid = getUUIDFromName(playerName);
        if (uuid != null) {
            System.out.println("UUID for player " + playerName + " is " + uuid);
        } else {
            System.out.println("Failed to retrieve UUID for player " + playerName);
        }
    }
    */
}
