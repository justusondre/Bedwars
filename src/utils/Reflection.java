package utils;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Reflection {
	
	public static final String NMS_VERSION;

	public static final int MINOR_NUMBER;

	public static final int PATCH_NUMBER;

	static {
		String found = null;
		for (Package pack : Package.getPackages()) {
			String name = pack.getName();
			if (name.startsWith("org.bukkit.craftbukkit.v")) {
				found = pack.getName().split("\\.")[3];
				try {
					Class.forName("org.bukkit.craftbukkit." + found + ".entity.CraftPlayer");
					break;
				} catch (ClassNotFoundException e) {
					found = null;
				}
			}
		}
		if (found == null)
			throw new IllegalArgumentException(
					"Failed to parse server version. Could not find any package starting with name: 'org.bukkit.craftbukkit.v'");
		NMS_VERSION = found;
		String[] split = NMS_VERSION.substring(1).split("_");
		if (split.length < 1)
			throw new IllegalStateException(
					"Version number division error: " + Arrays.toString(split) + ' ' + getVersionInformation());
		String minorVer = split[1];
		try {
			MINOR_NUMBER = Integer.parseInt(minorVer);
			if (MINOR_NUMBER < 0)
				throw new IllegalStateException("Negative minor number? " + minorVer + ' ' + getVersionInformation());
		} catch (Throwable ex) {
			throw new RuntimeException("Failed to parse minor number: " + minorVer + ' ' + getVersionInformation(), ex);
		}
		Matcher bukkitVer = Pattern.compile("^\\d+\\.\\d+\\.(\\d+)").matcher(Bukkit.getBukkitVersion());
		if (bukkitVer.find()) {
			try {
				PATCH_NUMBER = Integer.parseInt(bukkitVer.group(1));
			} catch (Throwable ex) {
				throw new RuntimeException("Failed to parse minor number: " + bukkitVer + ' ' + getVersionInformation(),
						ex);
			}
		} else {
			PATCH_NUMBER = 0;
		}
	}

	public static String getVersionInformation() {
		return "(NMS: " + NMS_VERSION + " | Minecraft: " + Bukkit.getVersion() + " | Bukkit: "
				+ Bukkit.getBukkitVersion() + ')';
	}

	public static Integer getLatestPatchNumberOf(int minorVersion) {
		if (minorVersion <= 0)
			throw new IllegalArgumentException("Minor version must be positive: " + minorVersion);
		int[] patches = { 1, 5, 2, 7, 2, 4, 10, 8, 4, 2, 2, 2, 2, 4, 2, 5, 1, 2, 4, 0 };
		if (minorVersion > patches.length)
			return null;
		return Integer.valueOf(patches[minorVersion - 1]);
	}

	public static final String CRAFTBUKKIT_PACKAGE = "org.bukkit.craftbukkit." + NMS_VERSION + '.';

	public static final String NMS_PACKAGE = v(17, "net.minecraft.")
			.orElse("net.minecraft.server." + NMS_VERSION + '.');

	private static final MethodHandle PLAYER_CONNECTION;

	private static final MethodHandle GET_HANDLE;

	private static final MethodHandle SEND_PACKET;

	static {
		Class<?> entityPlayer = getNMSClass("server.level", "EntityPlayer");
		Class<?> craftPlayer = getCraftClass("entity.CraftPlayer");
		Class<?> playerConnection = getNMSClass("server.network", "PlayerConnection");
		MethodHandles.Lookup lookup = MethodHandles.lookup();
		MethodHandle sendPacket = null, getHandle = null, connection = null;
		try {
			connection = lookup.findGetter(entityPlayer, v(20, "c").v(17, "b").orElse("playerConnection"),
					playerConnection);
			getHandle = lookup.findVirtual(craftPlayer, "getHandle", MethodType.methodType(entityPlayer));
			sendPacket = lookup.findVirtual(playerConnection, v(18, "a").orElse("sendPacket"),
					MethodType.methodType(void.class, getNMSClass("network.protocol", "Packet")));
		} catch (NoSuchMethodException | NoSuchFieldException | IllegalAccessException ex) {
			ex.printStackTrace();
		}
		PLAYER_CONNECTION = connection;
		SEND_PACKET = sendPacket;
		GET_HANDLE = getHandle;
	}

	public static <T> VersionHandler<T> v(int version, T handle) {
		return new VersionHandler<>(version, handle);
	}

	public static <T> CallableVersionHandler<T> v(int version, Callable<T> handle) {
		return new CallableVersionHandler<>(version, handle);
	}

	public static boolean supports(int minorNumber) {
		return (MINOR_NUMBER >= minorNumber);
	}

	public static boolean supportsPatch(int patchNumber) {
		return (PATCH_NUMBER >= patchNumber);
	}

	@Nullable
	public static Class<?> getNMSClass(@Nonnull String newPackage, @Nonnull String name) {
		if (supports(17))
			name = newPackage + '.' + name;
		return getNMSClass(name);
	}

	@Nullable
	public static Class<?> getNMSClass(@Nonnull String name) {
		try {
			return Class.forName(NMS_PACKAGE + name);
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	@Nonnull
	public static CompletableFuture<Void> sendPacket(@Nonnull Player player, @Nonnull Object... packets) {
		return CompletableFuture.runAsync(() -> sendPacketSync(player, packets)).exceptionally(ex -> {
			ex.printStackTrace();
			return null;
		});
	}

	public static void sendPacketSync(@Nonnull Player player, @Nonnull Object... packets) {
		try {
			Object handle = GET_HANDLE.invoke(player);
			Object connection = PLAYER_CONNECTION.invoke(handle);
			if (connection != null)
				for (Object packet : packets)
					SEND_PACKET.invoke(connection, packet);
		} catch (Throwable throwable) {
			throwable.printStackTrace();
		}
	}

	@Nullable
	public static Object getHandle(@Nonnull Player player) {
		Objects.requireNonNull(player, "Cannot get handle of null player");
		try {
			return GET_HANDLE.invoke(player);
		} catch (Throwable throwable) {
			throwable.printStackTrace();
			return null;
		}
	}

	@Nullable
	public static Object getConnection(@Nonnull Player player) {
		Objects.requireNonNull(player, "Cannot get connection of null player");
		try {
			Object handle = GET_HANDLE.invoke(player);
			return PLAYER_CONNECTION.invoke(handle);
		} catch (Throwable throwable) {
			throwable.printStackTrace();
			return null;
		}
	}

	@Nullable
	public static Class<?> getCraftClass(@Nonnull String name) {
		try {
			return Class.forName(CRAFTBUKKIT_PACKAGE + name);
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public static final class VersionHandler<T> {
		private int version;

		private T handle;

		private VersionHandler(int version, T handle) {
			if (Reflection.supports(version)) {
				this.version = version;
				this.handle = handle;
			}
		}

		public VersionHandler<T> v(int version, T handle) {
			if (version == this.version)
				throw new IllegalArgumentException("Cannot have duplicate version handles for version: " + version);
			if (version > this.version && Reflection.supports(version)) {
				this.version = version;
				this.handle = handle;
			}
			return this;
		}

		public T orElse(T handle) {
			return (this.version == 0) ? handle : this.handle;
		}
	}

	public static final class CallableVersionHandler<T> {
		private int version;

		private Callable<T> handle;

		private CallableVersionHandler(int version, Callable<T> handle) {
			if (Reflection.supports(version)) {
				this.version = version;
				this.handle = handle;
			}
		}

		public CallableVersionHandler<T> v(int version, Callable<T> handle) {
			if (version == this.version)
				throw new IllegalArgumentException("Cannot have duplicate version handles for version: " + version);
			if (version > this.version && Reflection.supports(version)) {
				this.version = version;
				this.handle = handle;
			}
			return this;
		}

		public T orElse(Callable<T> handle) {
			try {
				return ((this.version == 0) ? handle : this.handle).call();
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
	}
}