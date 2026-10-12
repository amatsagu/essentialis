package amatsagu.essentialis.common;

import amatsagu.essentialis.Essentialis;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.LevelResource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class WaybackCooldowns {

	public static final int COOLDOWN_TICKS = 15 * 60 * 20;

	private static final long COOLDOWN_MILLIS = COOLDOWN_TICKS * 50L;

	private static final String FILE_NAME = "essentialis_wayback_cooldowns.json";

	private static final Map<UUID, Long> ENDS = new HashMap<>();

	private static Path file;

	private WaybackCooldowns() {
	}

	public static void init() {
		ServerLifecycleEvents.SERVER_STARTED.register(WaybackCooldowns::load);
		ServerLifecycleEvents.BEFORE_SAVE.register((server, flush, force) -> save());
		ServerLifecycleEvents.SERVER_STOPPING.register(server -> save());
	}

	public static boolean isOnCooldown(final ServerPlayer player) {
		final Long end = ENDS.get(player.getUUID());

		if (end == null) {
			return false;
		}

		final long remaining = end - System.currentTimeMillis();

		if (remaining <= 0L) {
			ENDS.remove(player.getUUID());

			return false;
		}

		player.getCooldowns().addCooldown(Essentialis.id("wayback_stone"), (int) Math.max(1L, remaining / 50L));

		return true;
	}

	public static void start(final ServerPlayer player) {
		ENDS.put(player.getUUID(), System.currentTimeMillis() + COOLDOWN_MILLIS);
		player.getCooldowns().addCooldown(Essentialis.id("wayback_stone"), COOLDOWN_TICKS);
		save();
	}

	private static void load(final MinecraftServer server) {
		ENDS.clear();
		file = server.getWorldPath(LevelResource.ROOT).resolve(FILE_NAME);

		if (!Files.isRegularFile(file)) {
			return;
		}

		try {
			final JsonObject root = new Gson().fromJson(Files.readString(file), JsonObject.class);
			final long now = System.currentTimeMillis();

			for (final var entry : root.entrySet()) {
				final long end = entry.getValue().getAsLong();

				if (end > now) {
					ENDS.put(UUID.fromString(entry.getKey()), end);
				}
			}
		} catch (final IOException | IllegalArgumentException e) {
			Essentialis.LOGGER.warn("Could not load wayback stone cooldowns, starting fresh.", e);
		}
	}

	private static void save() {
		if (file == null) {
			return;
		}

		final JsonObject root = new JsonObject();
		final long now = System.currentTimeMillis();

		for (final var entry : ENDS.entrySet()) {
			if (entry.getValue() > now) {
				root.addProperty(entry.getKey().toString(), entry.getValue());
			}
		}

		try {
			Files.writeString(file, new Gson().toJson(root));
		} catch (final IOException e) {
			Essentialis.LOGGER.warn("Could not save wayback stone cooldowns.", e);
		}
	}
}
