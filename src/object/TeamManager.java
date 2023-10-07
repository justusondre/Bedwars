package object;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import bedwars.Main;
import bedwars.Mode;
import config.ArenaFile;

public class TeamManager {
	
	private Map<ChatColor, Team> teams;
    private Map<String, Map<Player, Team>> arenaPlayerTeams = new HashMap<>();
    
    ArenaFile arenaFile = Main.getInstance().getFileManager().getArenaFile();

    public TeamManager() {
        teams = new HashMap<>();
        initializeTeams();
    }

    private void initializeTeams() {
        for (TeamColor color : TeamColor.values()) {
            teams.put(color.getChatColor(), new Team(color));
        }
    }

    public Team getTeam(ChatColor teamColor) {
        return teams.get(teamColor);
    }

    public List<Team> getAllTeams() {
        return new ArrayList<>(teams.values());
    }

    public void addPlayerToTeam(Player player, Team team, String arenaName) {
        Map<Player, Team> playerTeams = arenaPlayerTeams.computeIfAbsent(arenaName, k -> new HashMap<>());

        ArenaFile arenaFile = Main.getInstance().getFileManager().getArenaFile();
        Mode gameMode = arenaFile.getMode(arenaName);

        removeFromTeam(player, arenaName);

        if (isTeamFull(team, gameMode)) {
            player.sendMessage(ChatColor.RED + "The " + team.getTeamColor() + team.getTeamName() + ChatColor.RED + " team is full.");
            player.sendMessage(ChatColor.RED + "Current team size: " + team.getSize() + "/" + gameMode.getTeamSize());

            return;
        }

        playerTeams.put(player, team);
        team.addPlayer(player);

        player.sendMessage(ChatColor.GREEN + "You have joined the " + team.getTeamName() + ChatColor.GREEN + " team.");
        player.sendMessage(ChatColor.GREEN + "Current team size: " + team.getSize() + "/" + gameMode.getTeamSize());
        Bukkit.broadcastMessage("YOUR TEAM SIZE IS NOW: " + getTeamSize(arenaName, team));

        // You may want to implement additional logic here, such as updating player inventories or scores
    }

    public void assignPlayerToRandomTeam(Player player, String arenaName) {
        TeamManager teamManager = Main.getInstance().getTeamManager();
        List<Team> availableTeams = teamManager.getAllTeams();
        List<Team> eligibleTeams = new ArrayList<>();

        ArenaFile arenaFile = Main.getInstance().getFileManager().getArenaFile();
        Mode gameMode = arenaFile.getMode(arenaName);

        // Find teams that are not full based on the game mode and are listed in the config
        for (Team team : availableTeams) {
            TeamColor teamColor = team.getTeamColor();
            if (!isTeamFull(team, gameMode) && arenaFile.isTeamListedInArena(arenaName, teamColor.name())) {
                eligibleTeams.add(team);
            }
        }

        // If there are eligible teams, choose a random one and add the player
        if (!eligibleTeams.isEmpty()) {
            Team randomTeam = eligibleTeams.get(new Random().nextInt(eligibleTeams.size()));
            addPlayerToTeam(player, randomTeam, arenaName);
        } else {
            player.sendMessage(ChatColor.RED + "All eligible teams are full or not listed in the " + arenaName + " arena.");
        }
    }

    private boolean isTeamFull(Team team, Mode gameMode) {
        int maxTeamSize = gameMode.getTeamSize();
        return team.getSize() >= maxTeamSize;
    }

    public void removeFromTeam(Player player, String arenaName) {
        Map<Player, Team> playerTeams = arenaPlayerTeams.get(arenaName);
        if (playerTeams != null) {
            Team team = playerTeams.remove(player);
            if (team != null) {
                team.removePlayer(player);
            }
        }
    }

    public Team getPlayerTeam(Player player, String arenaName) {
        Map<Player, Team> playerTeams = arenaPlayerTeams.get(arenaName);
        return playerTeams != null ? playerTeams.get(player) : null;
    }

    public List<Team> getTeamsRemaining(String arenaName) {
        List<Team> remainingTeams = new ArrayList<>();
        ArenaFile arenaFile = Main.getInstance().getFileManager().getArenaFile();

        for (Team team : getAllTeams()) {
            // Check if the team is listed in the arena's configuration and has players
            TeamColor teamColor = team.getTeamColor();
            if (arenaFile.isTeamListedInArena(arenaName, teamColor.name()) && team.getSize() > 0) {
                remainingTeams.add(team);
            }
        }

        return remainingTeams;
    }

    public List<Player> getPlayersInTeam(String arenaName, TeamColor teamColor) {
        Map<Player, Team> playerTeams = arenaPlayerTeams.get(arenaName);
        List<Player> playersInTeam = new ArrayList<>();

        if (playerTeams != null) {
            for (Map.Entry<Player, Team> entry : playerTeams.entrySet()) {
                if (entry.getValue().getTeamColor() == teamColor) {
                    playersInTeam.add(entry.getKey());
                }
            }
        }

        return playersInTeam;
    }

    public String getTeamsRemainingMessage(String arenaName) {
        StringBuilder message = new StringBuilder("Teams Remaining:\n");
        ArenaFile arenaFile = Main.getInstance().getFileManager().getArenaFile();

        for (Team team : getAllTeams()) {
            TeamColor teamColor = team.getTeamColor();
            String teamName = team.getTeamName();
            boolean isListedInConfig = arenaFile.isTeamListedInArena(arenaName, teamColor.name());

            if (isListedInConfig) {
                if (team.getSize() > 0) {
                    message.append("- ").append(teamColor).append(" ").append(teamName).append(" : Active\n");
                } else {
                    message.append("- ").append(teamColor).append(" ").append(teamName).append(" : Eliminated\n");
                }
            }
        }

        return message.toString();
    }

    public boolean doesBedExist(String arenaName, TeamColor teamColor) {
        ArenaFile arenaFile = Main.getInstance().getFileManager().getArenaFile();
        Location bedLocation = arenaFile.getBedLocation(arenaName, teamColor);

        // Check if the bedLocation is not null and the bed block type is not Material.AIR
        return bedLocation != null && bedLocation.getBlock().getType() != Material.AIR;
    }

    public Map<TeamColor, String> getTeamsWithStatus(String arenaName) {
        Map<TeamColor, String> teamsWithStatus = new HashMap<>();
        ArenaFile arenaFile = Main.getInstance().getFileManager().getArenaFile();
        TeamManager teamManager = Main.getInstance().getTeamManager();

        for (TeamColor teamColor : TeamColor.values()) {
            String status = "Active"; // Default status

            // Check if the team is listed in the arena's configuration and has players
            if (!arenaFile.isTeamListedInArena(arenaName, teamColor.name()) || teamManager.getPlayersInTeam(arenaName, teamColor).isEmpty()) {
                status = "Eliminated";
            }

            teamsWithStatus.put(teamColor, status);
        }

        return teamsWithStatus;
    }

    public void addTeamToArena(String arenaName, Team team) {
        // Assuming you have a method to get the ArenaFile instance
        ArenaFile arenaFile = Main.getInstance().getFileManager().getArenaFile();

        // Store the team in the arena configuration
        arenaFile.addTeam(arenaName, team.getTeamColor().name());

    }

    public boolean isPlayerOnTeam(Player player, TeamColor teamColor, String arenaName) {
        Map<Player, Team> playerTeams = arenaPlayerTeams.get(arenaName);
        if (playerTeams != null) {
            Team team = playerTeams.get(player);
            if (team != null) {
                return team.getTeamColor() == teamColor;
            }
        }
        return false;
    }

    public TeamColor getPlayerTeamColor(Player player, String arenaName) {
        Map<Player, Team> playerTeams = arenaPlayerTeams.get(arenaName);
        if (playerTeams != null) {
            Team team = playerTeams.get(player);
            if (team != null) {
                return team.getTeamColor();
            }
        }
        return null;
    }

    public int getTeamSize(String arenaName, Team team) {
        Map<Player, Team> playerTeams = arenaPlayerTeams.get(arenaName);
        if (playerTeams != null) {
            int size = 0;
            for (Map.Entry<Player, Team> entry : playerTeams.entrySet()) {
                if (entry.getValue().equals(team)) {
                    size++;
                }
            }
            return size;
        }
        return 0;
    }

    public void checkForEmptyTeams(String arenaName) {
        ArenaFile arenaFile = Main.getInstance().getFileManager().getArenaFile();
        List<TeamColor> listedTeams = arenaFile.getTeamColorsInArena(arenaName);

        if (listedTeams != null) {
            StringBuilder emptyTeamsMessage = new StringBuilder("Empty Teams in Arena '" + arenaName + "': ");

            boolean emptyTeamsFound = false; // Track if any empty teams were found

            for (TeamColor teamColor : listedTeams) {
                Team team = teams.get(teamColor.getChatColor()); // Use teamColor.getChatColor() to get ChatColor

                if (team != null) {
                    int teamSize = getTeamSize(arenaName, team);
                    if (teamSize == 0) {
                        emptyTeamsMessage.append(team.getTeamName()).append(", "); // Use team.getTeamName()
                        emptyTeamsFound = true; // Mark that an empty team was found
                    }
                }
            }

            if (emptyTeamsFound) { // Check if any empty teams were found
                emptyTeamsMessage.delete(emptyTeamsMessage.length() - 2, emptyTeamsMessage.length()); // Remove trailing comma and space
                Bukkit.broadcastMessage(emptyTeamsMessage.toString());
            }
        }
    }

    public void destroyBedForEmptyTeams(String arenaName) {
        ArenaFile arenaFile = Main.getInstance().getFileManager().getArenaFile();
        List<TeamColor> listedTeams = arenaFile.getTeamColorsInArena(arenaName);

        if (listedTeams != null) {
            for (TeamColor teamColor : listedTeams) {
                Team team = teams.get(teamColor.getChatColor());

                if (team != null) {
                    int teamSize = getTeamSize(arenaName, team);
                    if (teamSize == 0) {
                        Location bedLocation = arenaFile.getBedLocation(arenaName, teamColor);

                        // Check if the bedLocation is not null and the bed block type is not Material.AIR
                        if (bedLocation != null && bedLocation.getBlock().getType() != Material.AIR) {
                            // Destroy the bed blocks by setting them to Material.AIR
                            bedLocation.getBlock().setType(Material.AIR);
                        }
                    }
                }
            }
        }
    }
}