package com.adytechmc.mcmanhunt.events;

import com.adytechmc.mcmanhunt.MinecraftManhunt;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.minecraft.world.poi.PointOfInterestTypes;
import org.slf4j.Logger;

import java.util.Objects;

public class UseItemHandler implements UseItemCallback {
    @Override
    public TypedActionResult<ItemStack> interact(PlayerEntity player, World world, Hand hand) {
        if(player.getStackInHand(hand).hasNbt()) {
            if (player.getStackInHand(hand).getItem() == Items.COMPASS && player.getStackInHand(hand).getNbt().contains("IsPlayerTracker") && !world.isClient()) {
                PlayerEntity runner;
                runner = MinecraftManhunt.runners.getClosestRunner(player, world);
                player.sendMessage(Text.of("Now tracking " + runner.getName()));
                if (runner == null) {
                    player.sendMessage(Text.literal("This guy is null, abort"));
                }
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
        World world = targetPlayer.getWorld();
        NbtCompound tag = compass.getOrCreateNbt();
        tag.put("LodestonePos", NbtHelper.fromBlockPos(targetPlayer.getBlockPos())); // Should also work.
        DataResult<NbtElement> var10000 = World.CODEC.encodeStart(NbtOps.INSTANCE, world.getRegistryKey());
        Logger var10001 = LogUtils.getLogger();
        Objects.requireNonNull(var10001);
        var10000.resultOrPartial(var10001::error).ifPresent((nbtElement) -> {
            tag.put("LodestoneDimension", nbtElement);
            //targetPlayer.sendMessage(Text.literal(String.valueOf(nbtElement))); // For Debugging. Correct Dimension is put :).
        });
        tag.putBoolean("LodestoneTracked", true); //This should work. It's a literal boolean.
        //targetPlayer.sendMessage(Text.literal(String.valueOf(compass.getNbt())));
        return compass;


    }

}
