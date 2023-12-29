package com.adytechmc.mcmanhunt.events;

import com.adytechmc.mcmanhunt.MinecraftManhunt;
import com.mojang.serialization.DataResult;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtOps;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.apache.logging.log4j.Logger;

public class UseItemHandler implements UseItemCallback {
    @Override
    public TypedActionResult<ItemStack> interact(PlayerEntity player, World world, Hand hand) {
        if(player.getStackInHand(hand).hasTag()) {
            if (player.getStackInHand(hand).getItem() == Items.COMPASS && player.getStackInHand(hand).getTag().contains("IsPlayerTracker") && !world.isClient()) {
                PlayerEntity runner;
                runner = MinecraftManhunt.runners.getClosestRunner(player, world);
                String messageString  = "Now tracking " + runner.getEntityName();
                player.sendMessage(Text.of((messageString)), false);
                if (runner == null) player.sendMessage(Text.of("This guy is null, abort"), false);
                //KnifeMod.getServerWorldFromWorld(world).setSpawnPos(runner.getBlockPos(), runner.bodyYaw);
                ItemStack compass = player.getStackInHand(hand);
                player.setStackInHand(hand, pointCompassToPlayer(runner, compass));
            } else {
                //doNothing
            }
        }
        return TypedActionResult.pass(player.getStackInHand(hand));

    }

    public static ItemStack pointCompassToPlayer(PlayerEntity targetPlayer, ItemStack compass) {
        World world = targetPlayer.getEntityWorld();
        NbtCompound tag = compass.getOrCreateTag();
        tag.put("LodestonePos", NbtHelper.fromBlockPos(targetPlayer.getBlockPos())); // Should also work
        Logger var10001 = MinecraftManhunt.ApacheLogger;
        DataResult<NbtElement> var10000 = World.CODEC.encodeStart(NbtOps.INSTANCE, world.getRegistryKey());
        var10000.resultOrPartial(var10001::error).ifPresent((nbtElement) -> {
            tag.put("LodestoneDimension", nbtElement);
        });
        tag.putBoolean("LodestoneTracked", true); //This should work. It's a literal boolean.
        //targetPlayer.sendMessage(Text.literal(String.valueOf(compass.getNbt())));
        return compass;


    }

}
