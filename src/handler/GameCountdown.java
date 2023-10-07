package handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import bedwars.Main;
import config.LobbyFile;
import config.Messages;
import mapreset.MapReset;
import object.TeamManager;
import utils.ActionBar;

public class GameCountdown {
	
	private final Map<String, Integer> arenaCountdowns = new HashMap<>();
    private final Map<String, BukkitRunnable> countdownTasks = new HashMap<>();

    public void startCountdown(String arenaName, int seconds, GameState nextState) {
        if (!arenaCountdowns.containsKey(arenaName)) {
            arenaCountdowns.put(arenaName, seconds);
            createCountdownTask(arenaName, seconds, () -> completeCountdown(arenaName, nextState));
            Bukkit.getLogger().info("Started countdown for " + arenaName + " with " + seconds + " seconds");
        }
    }

    public void cancelCountdown(String arenaName) {
        arenaCountdowns.remove(arenaName);
        BukkitRunnable countdownTask = countdownTasks.remove(arenaName);
        if (countdownTask != null) {
            countdownTask.cancel();
        }
    }

    public int getRemainingSeconds(String arenaName) {
        if (arenaCountdowns.containsKey(arenaName)) {
            int remainingSeconds = arenaCountdowns.get(arenaName);
            return remainingSeconds;
        }
        return 0; // Return 0 if the arena name is not found
    }

    private void completeCountdown(String arenaName, GameState nextState) {
        arenaCountdowns.remove(arenaName);
        Game game = Main.getInstance().getGame();

        Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
            switch (nextState) {
                case INGAME:
                    TeamManager teamManager = Main.getInstance().getTeamManager();
                    game.startDiamondGenerators(arenaName, 1, 10);
                    game.startEmeraldGenerators(arenaName, 1, 10);
                    teamManager.checkForEmptyTeams(arenaName);
                    teamManager.destroyBedForEmptyTeams(arenaName);
                    game.setGameState(arenaName, GameState.INGAME);
                    Main.getInstance().getGame().teleportPlayersToTeamSpawns(arenaName);
                    startCountdown(arenaName, 120, GameState.ENDGAME);
                    break;

                case ENDGAME:
                	game.setGameState(arenaName, GameState.ENDGAME);
                    startCountdown(arenaName, 15, GameState.MAPRESET);
                    break;

                case MAPRESET:
                	game.setGameState(arenaName, GameState.MAPRESET);
                    startCountdown(arenaName, 15, GameState.WAITING);
                    LobbyFile lobbyFile = Main.getInstance().getFileManager().getLobbyFile();
                    game.removeAllPlayersFromArena(arenaName, lobbyFile.getLocation());
                    MapReset.resetMap("lighthouse");
                    break;

                case WAITING:
                	game.setGameState(arenaName, GameState.WAITING);
                    break;

                default:
                    break;
            }
        }, 1L);
    }

    private void createCountdownTask(String arenaName, int seconds, Runnable onComplete) {
        BukkitRunnable countdownTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (!arenaCountdowns.containsKey(arenaName)) {
                    cancelCountdown(arenaName);
                    return;
                }

                int remainingSeconds = arenaCountdowns.get(arenaName);
                if (remainingSeconds <= 0) {
                    onComplete.run();
                    cancelCountdown(arenaName);
                    return;
                }

                List<Player> playersInArena = getPlayersInArena(arenaName);
                for (Player player : playersInArena) {
                    if (remainingSeconds % 60 == 0 || remainingSeconds == 30 || remainingSeconds == 15 || remainingSeconds <= 5) {
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1F, 1F);
                        ActionBar actionBar = new ActionBar();
                        actionBar.sendActionbar(player, Messages.getActionBarMessage(arenaName).replace("%timer%", String.valueOf(remainingSeconds)));
                    }
                }

                remainingSeconds--;
                arenaCountdowns.put(arenaName, remainingSeconds);
            }
        };

        countdownTasks.put(arenaName, countdownTask);
        countdownTask.runTaskTimer(Main.getInstance(), 0L, 20L);
    }

    private List<Player> getPlayersInArena(String arenaName) {
        return new ArrayList<>(Main.getInstance().getGame().getPlayersInArena(arenaName));
    }
}