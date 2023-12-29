package com.adytechmc.mcmanhunt;

import com.adytechmc.mcmanhunt.events.UseItemHandler;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.session.report.ReporterEnvironment;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.CompassItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SentMessage;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralTextContent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

import javax.swing.text.AttributeSet;
import java.awt.*;
import java.util.Objects;

import static net.minecraft.server.command.CommandManager.literal;
import static net.minecraft.util.Formatting.GREEN;

public class MinecraftManhunt implements ModInitializer {

	public static Runners runners = new Runners();

	@Override
	public void onInitialize() {
		System.out.println("ManHunt mod initialized.");
		registerCommands();
		UseItemCallback.EVENT.register(new UseItemHandler());
	}

	private void registerCommands() {
		CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) -> {
			dispatcher.register(CommandManager.literal("runner")
					.then(CommandManager.argument("player", StringArgumentType.string())
							.suggests((context, builder) -> {
								// Provide player name suggestions here
								for (PlayerEntity playerEntity : context.getSource().getServer().getPlayerManager().getPlayerList()) {
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
								context.getSource().getPlayer().sendMessage(Text.literal("Runner added"));
								//context.getSource().sendMessage(Text.literal("Runners: " + name));
								return 1;
							})));

			dispatcher.register(CommandManager.literal("track")
					.executes(context -> {
						// Implement your tracking functionality here
						ItemStack compass = new ItemStack(Items.COMPASS);
						compass.getOrCreateNbt().putBoolean("IsPlayerTracker", true);
						Objects.requireNonNull(context.getSource().getPlayer()).giveItemStack((compass));
						context.getSource().sendMessage(Text.literal("Compass given"));
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
						context.getSource().sendMessage(Text.literal("Runners: " + name).setStyle(Style.EMPTY.withColor(Formatting.AQUA)));
						return 1;
					}));
			dispatcher.register(CommandManager.literal("startmanhunt")
					.executes(context -> {
						for(PlayerEntity player : context.getSource().getServer().getPlayerManager().getPlayerList()){
							player.addStatusEffect(new StatusEffectInstance(StatusEffects.SATURATION, 40, 0));
							player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 40, 255));
							context.getSource().getWorld().setTimeOfDay(0);
							player.sendMessage(Text.literal("Manhunt has begun. Good luck!").setStyle(Style.EMPTY.withItalic(true).withColor(Formatting.GOLD)));
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
