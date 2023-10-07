package utils;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.function.Function;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public final class Title implements Cloneable {
	
	private static final Object TITLE = new Object();

	private static final Object SUBTITLE = new Object();

	private static final Object TIMES = new Object();

	private static final Object CLEAR = new Object();

	private static final MethodHandle PACKET_PLAY_OUT_TITLE = null;

	private static final MethodHandle CHAT_COMPONENT_TEXT = null;

	private String title;

	private String subtitle;

	private final int fadeIn;

	private final int stay;

	private final int fadeOut;

	public Title(String title, String subtitle, int fadeIn, int stay, int fadeOut) {
		this.title = title;
		this.subtitle = subtitle;
		this.fadeIn = fadeIn*20;
		this.stay = stay*20;
		this.fadeOut = fadeOut*20;
	}

	public Title clone() {
		return new Title(this.title, this.subtitle, this.fadeIn, this.stay, this.fadeOut);
	}

	public void send(Player player) {
		sendTitle(player, this.fadeIn, this.stay, this.fadeOut, this.title, this.subtitle);
	}

	public static void sendTitle(Player player, int fadeIn, int stay, int fadeOut, String title, String subtitle) {
		Objects.requireNonNull(player, "Cannot send title to null player");
		if (title == null && subtitle == null)
			return;
		if (Reflection.supports(11)) {
			player.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
			return;
		}
		try {
			Object timesPacket = PACKET_PLAY_OUT_TITLE.invoke(TIMES, CHAT_COMPONENT_TEXT.invoke(title), fadeIn, stay,
					fadeOut);
			Reflection.sendPacket(player, new Object[] { timesPacket });
			if (title != null) {
				Object titlePacket = PACKET_PLAY_OUT_TITLE.invoke(TITLE, CHAT_COMPONENT_TEXT.invoke(title), fadeIn,
						stay, fadeOut);
				Reflection.sendPacket(player, new Object[] { titlePacket });
			}
			if (subtitle != null) {
				Object subtitlePacket = PACKET_PLAY_OUT_TITLE.invoke(SUBTITLE, CHAT_COMPONENT_TEXT.invoke(subtitle),
						fadeIn, stay, fadeOut);
				Reflection.sendPacket(player, new Object[] { subtitlePacket });
			}
		} catch (Throwable throwable) {
			throwable.printStackTrace();
		}
	}

	public static void sendTitle(Player player, String title, String subtitle) {
		sendTitle(player, 10, 20, 10, title, subtitle);
	}

	public static Title parseTitle(ConfigurationSection config) {
		return parseTitle(config, null);
	}

	public static Title parseTitle(ConfigurationSection config, Function<String, String> transformers) {
		String title = config.getString("title");
		String subtitle = config.getString("subtitle");
		if (transformers != null) {
			title = transformers.apply(title);
			subtitle = transformers.apply(subtitle);
		}
		int fadeIn = config.getInt("fade-in");
		int stay = config.getInt("stay");
		int fadeOut = config.getInt("fade-out");
		if (fadeIn < 1)
			fadeIn = 10;
		if (stay < 1)
			stay = 20;
		if (fadeOut < 1)
			fadeOut = 10;
		return new Title(title, subtitle, fadeIn, stay, fadeOut);
	}

	public String getTitle() {
		return this.title;
	}

	public String getSubtitle() {
		return this.subtitle;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
	}

	public static Title sendTitle(Player player, ConfigurationSection config) {
		Title titles = parseTitle(config, null);
		titles.send(player);
		return titles;
	}

	public static void clearTitle(Player player) {
		Object clearPacket;
		Objects.requireNonNull(player, "Cannot clear title from null player");
		if (Reflection.supports(11)) {
			player.resetTitle();
			return;
		}
		try {
			clearPacket = PACKET_PLAY_OUT_TITLE.invoke(CLEAR, null, -1, -1, -1);
		} catch (Throwable throwable) {
			throwable.printStackTrace();
			return;
		}
		Reflection.sendPacket(player, new Object[] { clearPacket });
	}

	public static void sendTabList(String header, String footer, Player... players) {
		Objects.requireNonNull(players, "Cannot send tab title to null players");
		Objects.requireNonNull(header, "Tab title header cannot be null");
		Objects.requireNonNull(footer, "Tab title footer cannot be null");
		if (Reflection.supports(13)) {
			for (Player player : players)
				player.setPlayerListHeaderFooter(header, footer);
			return;
		}
		try {
			Class<?> IChatBaseComponent = Reflection.getNMSClass("network.chat", "IChatBaseComponent");
			Class<?> PacketPlayOutPlayerListHeaderFooter = Reflection.getNMSClass("network.protocol.game",
					"PacketPlayOutPlayerListHeaderFooter");
			Method chatComponentBuilderMethod = IChatBaseComponent.getDeclaredClasses()[0].getMethod("a",
					new Class[] { String.class });
			Object tabHeader = chatComponentBuilderMethod.invoke(null,
					new Object[] { "{\"text\":\"" + header + "\"}" });
			Object tabFooter = chatComponentBuilderMethod.invoke(null,
					new Object[] { "{\"text\":\"" + footer + "\"}" });
			Object packet = PacketPlayOutPlayerListHeaderFooter.getConstructor(new Class[0]).newInstance(new Object[0]);
			Field headerField = PacketPlayOutPlayerListHeaderFooter.getDeclaredField("a");
			Field footerField = PacketPlayOutPlayerListHeaderFooter.getDeclaredField("b");
			headerField.setAccessible(true);
			headerField.set(packet, tabHeader);
			footerField.setAccessible(true);
			footerField.set(packet, tabFooter);
			for (Player player : players) {
				Reflection.sendPacket(player, new Object[] { packet });
			}
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
}