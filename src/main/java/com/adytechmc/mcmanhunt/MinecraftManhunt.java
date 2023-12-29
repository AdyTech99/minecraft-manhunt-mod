package com.adytechmc.mcmanhunt;

import com.adytechmc.mcmanhunt.events.UseItemHandler;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.world.World;

import java.util.Objects;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class MinecraftManhunt implements ModInitializer {

	public static Runners runners = new Runners();

	public static final org.apache.logging.log4j.Logger ApacheLogger = org.apache.logging.log4j.LogManager.getLogger();
	@Override
	public void onInitialize() {
		System.out.println("ManHunt mod initialized.");
		registerCommands();
		UseItemCallback.EVENT.register(new UseItemHandler());
	}

	private void registerCommands() {
		CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess) -> {
			dispatcher.register(CommandManager.literal("runner")
					.then(CommandManager.argument("player", StringArgumentType.string())
							.suggests((context, builder) -> {
								// Provide player name suggestions here
								for (PlayerEntity playerEntity : context.getSource().getMinecraftServer().getPlayerManager().getPlayerList()) {
									String playerName = playerEntity.getEntityName();
									if (playerName.startsWith(builder.getRemaining())) {
										builder.suggest(playerName);
									}
								}
								return builder.buildFuture();
							})
							.executes(context -> {
								String playerName = StringArgumentType.getString(context, "player");
								addRunner(playerName);
								context.getSource().getPlayer().sendMessage(Text.of("Runner added"), true);
								//context.getSource().sendMessage(Text.literal("Runners: " + name));
								return 1;
							})));

			dispatcher.register(CommandManager.literal("track")
					.executes(context -> {
						// Implement your tracking functionality here
						ItemStack compass = new ItemStack(Items.COMPASS);
						compass.getOrCreateTag().putBoolean("IsPlayerTracker", true);
						Objects.requireNonNull(context.getSource().getPlayer()).giveItemStack((compass));
						context.getSource().getPlayer().sendMessage(Text.of("Compass given"), true);
						return 1;
					}));
			dispatcher.register(CommandManager.literal("clearrunners")
					.executes(context -> {
						runners.clearRunners();
						return 1;
					}));
			dispatcher.register(CommandManager.literal("runnerlist")
					.executes(context -> {
						String name = String.valueOf(Runners.getRunners(context.getSource().getWorld(), false));
						context.getSource().getPlayer().sendMessage(Text.of("Runners: " + name), false);
						return 1;
					}));
			dispatcher.register(CommandManager.literal("startmanhunt")
					.executes(context -> {
						for(PlayerEntity player : context.getSource().getMinecraftServer().getPlayerManager().getPlayerList()){
							player.addStatusEffect(new StatusEffectInstance(StatusEffects.SATURATION, 40, 0));
							player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 40, 255));
							context.getSource().getWorld().setTimeOfDay(0);
							player.sendMessage(Text.of("Manhunt has begun. Good luck!"), false);
						}
						return 1;
					}));

		}));
	}

	public static void addRunner(String player) {
		runners.addRunner(player);
	}

	public static void removeRunner(String player) {
		MinecraftManhunt.runners.removeRunner(player);
	}

	public static ServerWorld getServerWorldFromWorld(World world) {
		if (world instanceof ServerWorld) {
			return (ServerWorld) world;
		} else {
			// Handle the case where the world is not a ServerWorld
			return null; // or throw an exception, or handle it in a different way
		}
	}
}
