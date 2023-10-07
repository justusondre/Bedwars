package utils;

import java.util.HashMap;
import java.util.Map;

public class Cooldown {
	
	private final Map<String, Long> cooldowns = new HashMap<>();
    private final long cooldownMillis;

    public Cooldown(long cooldownSeconds) {
        this.cooldownMillis = cooldownSeconds * 1000;
    }

    public boolean isCooldownActive(String playerName) {
        if (!cooldowns.containsKey(playerName)) return false;
        long currentTime = System.currentTimeMillis();
        long cooldownTime = cooldowns.get(playerName);
        return (currentTime - cooldownTime) < cooldownMillis;
    }

    public void setCooldown(String playerName) {
        cooldowns.put(playerName, System.currentTimeMillis());
    }

    public void clearCooldown(String playerName) {
        cooldowns.remove(playerName);
    }

    public long getCooldownMillis() {
        return cooldownMillis;
    }

    public long getCooldownTime(String playerName) {
        if (!cooldowns.containsKey(playerName)) return 0;
        return cooldowns.get(playerName);
    }
}