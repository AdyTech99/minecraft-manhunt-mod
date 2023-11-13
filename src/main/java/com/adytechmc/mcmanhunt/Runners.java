package com.adytechmc.mcmanhunt;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;
import java.util.function.Predicate;

import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.MinecraftServer;
import net.minecraft.entity.player.PlayerEntity;
import org.apache.logging.log4j.core.jmx.Server;
import org.jetbrains.annotations.Nullable;

public class Runners {
    public static ArrayList<String> playerList = new ArrayList<>();

    public void removeRunner(String player) {
        playerList.remove(player);
    }

    public void addRunner(String player) {
        playerList.add(player);
    }

    public void clearRunners(){
        playerList.clear();
    }

    public static ArrayList<String> getRunners(World world, Boolean names) {
        if (!names) {
            return playerList;
        } else {
            ArrayList<String> nameList = new ArrayList();
            Iterator var3 = playerList.iterator();

            while(var3.hasNext()) {
                String str = (String)var3.next();
                nameList.add(world.getPlayerByUuid(UUID.fromString(str)).getEntityName().toString());
            }

            return nameList;
        }
    }

    public PlayerEntity getClosestRunnerLong(double x, double y, double z, double maxDistance, World world, ArrayList<String> playerList, @Nullable Predicate<PlayerEntity> targetPredicate) {
        double d = -1.0D;
        PlayerEntity playerEntity = null;
        for (String playerId : playerList) {
            //PlayerEntity player = world.getPlayerByUuid(UUIDFetcher.convertStringToUUID(UUIDFetcher.getUUIDFromName(playerId)));
            PlayerEntity player = world.getPlayerByUuid(UUIDFetcher.getUUIDFromNameOffline(playerId, world));
            //player.sendMessage(Text.literal("RUN!"));
            if (player != null && (targetPredicate == null || targetPredicate.test(player))) {
                double distance = player.squaredDistanceTo(x, y, z);
                if (maxDistance < 0.0D || distance < maxDistance * maxDistance) {
                    if (d == -1.0D || distance < d) {
                        d = distance;
                        playerEntity = player;
                    }
                }
            }
        }
        return playerEntity;
    }

    public static double getDistance(PlayerEntity playerOne, PlayerEntity playerTwo){
        Double X = Math.pow(playerOne.getX()-playerTwo.getX(), 2);
        Double Y = Math.pow(playerOne.getZ() - playerTwo.getZ(), 2);

        return (Double) Math.sqrt(X+Y);
    }

    public PlayerEntity getClosestRunner(PlayerEntity player, World world){
        double d = Double.MAX_VALUE;
        PlayerEntity closest = null;
        for(PlayerEntity runner : world.getPlayers()){
            if (playerList.contains(runner.getEntityName())) {
                double distance = getDistance(player, runner);
                if (distance < d){
                    d = distance;
                    closest = runner;
                }
            }
        }
        return closest;
    }

}