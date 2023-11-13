package com.adytechmc.mcmanhunt;

import com.adytechmc.mcmanhunt.events.UseItemHandler;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.client.session.report.ReporterEnvironment;
import net.minecraft.item.CompassItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SentMessage;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralTextContent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

import javax.swing.text.AttributeSet;
import javax.swing.text.Style;
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
							.executes(context -> {
								String playerName = StringArgumentType.getString(context, "player");
								addRunner(playerName);
								String names = String.valueOf(Runners.getRunners(context.getSource().getWorld(), false));
								context.getSource().getPlayer().sendMessage(Text.literal("Runner added"));
								//context.getSource().sendMessage(Text.literal("Runners: " + name));
								return 1;
							})));

			dispatcher.register(CommandManager.literal("track")
					.executes(context -> {
						// Implement your tracking functionality here
						ItemStack compass = new ItemStack(Items.COMPASS);
						compass.getOrCreateNbt().putBoolean("IsPlayerTracker", true);
						Objects.requireNonNull(context.getSource().getPlayer()).dropItem((compass), false, true);
						context.getSource().sendMessage(Text.literal("Compass given"));
						return 1;
					}));
			dispatcher.register(CommandManager.literal("clearrunners")
					.executes(context -> {
						// Implement your tracking functionality here
						runners.clearRunners();
						return 1;
					}));
			dispatcher.register(CommandManager.literal("showrunners")
					.executes(context -> {
						String name = String.valueOf(Runners.getRunners(context.getSource().getWorld(), false));
						context.getSource().sendMessage(Text.literal("Runners: " + name));
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
