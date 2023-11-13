package com.adytechmc.mcmanhunt.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.item.CompassItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.poi.PointOfInterestTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

import static net.minecraft.item.CompassItem.hasLodestone;

@Mixin(CompassItem.class)
public class ExampleMixin {

    @Inject(method = "inventoryTick", at = @At("HEAD"), cancellable = true)
    private void onInventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected, CallbackInfo ci) {
        ci.cancel();
        if (!world.isClient) {
            if (hasLodestone(stack)) {
                NbtCompound nbtCompound = stack.getOrCreateNbt();
                if (nbtCompound.contains("LodestoneTracked") && !nbtCompound.getBoolean("LodestoneTracked")) {
                    return;
                }

                Optional<RegistryKey<World>> optional = getLodestoneDimension(nbtCompound);
                if (optional.isPresent() && optional.get() == world.getRegistryKey() && nbtCompound.contains("LodestonePos")) {
                    BlockPos blockPos = NbtHelper.toBlockPos(nbtCompound.getCompound("LodestonePos"));
                    // Remove the code
                    if (!world.isInBuildLimit(blockPos) || !((ServerWorld)world).getPointOfInterestStorage().hasTypeAt(PointOfInterestTypes.LODESTONE, blockPos)) {
                        if(stack.hasNbt()) {
                            if (!stack.getNbt().contains("IsPlayerTracker")) {
                                nbtCompound.remove("LodestonePos");
                            }
                        }
                    }
                }
            }
        }
    }

    private static Optional<RegistryKey<World>> getLodestoneDimension(NbtCompound nbt) {
        return World.CODEC.parse(NbtOps.INSTANCE, nbt.get("LodestoneDimension")).result();
    }
}
