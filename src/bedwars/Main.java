package bedwars;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import command.AddKill;
import command.AddTeam;
import command.ArenaInfo;
import command.CreateArena;
import command.JoinArena;
import command.JoinTeam;
import command.MapResetCommand;
import command.SetArenaLobby;
import command.SetBedLocation;
import command.SetGenerator;
import command.SetShop;
import command.SetMainLobby;
import command.SetMode;
import command.SetTeamSpawn;
import command.TeleportWorld;
import config.FileManager;
import config.MySQLFile;
import database.MySQLConnection;
import database.MySQLDatabase;
import handler.Game;
import handler.GameCountdown;
import handler.GameListener;
import handler.GameManager;
import listener.AxeListener;
import manager.OreManager;
import object.TeamManager;
import scoreboard.ScoreboardManager;
import shop.ShopInventory;
import shop.ShopListener;

public class Main extends JavaPlugin {
	
	private static Main instance;
    private FileManager fileManager;
    private GameCountdown countdownManager;
    private Game game;
    private ScoreboardManager scoreboardManager;
    private OreManager goldManager;
    private MySQLConnection mysqlConnection;
    private MySQLDatabase mysqlDatabase;
    private MySQLFile sqlFile;
    private TeamManager teamManager;
    private GameManager gameManager;
    private ShopInventory shopInventory;
    
    private static final int MIN_VERSION = 8;
	private static final int MAX_VERSION = 19;
	private static int version;

    @Override
    public void onEnable() {
        instance = this;
        loadServerVersion();
        
        //Initialize the SQL file first
        this.sqlFile = new MySQLFile(this);

        //Create and start the MySQL connection using the SQL file
        //mysqlConnection = new MySQLConnection(this, sqlFile);
        //mysqlConnection.getConnection(); // This method should establish the connection
        //this.mysqlDatabase = new MySQLDatabase(this.mysqlConnection);

        // Initialize your plugin components
        fileManager = new FileManager();
        countdownManager = new GameCountdown();
        game = new Game();
        scoreboardManager = new ScoreboardManager();
        goldManager = new OreManager(Main.getInstance().getConfig());
        teamManager = new TeamManager();
        gameManager = new GameManager();
        shopInventory = new ShopInventory();

        // Register commands
        registerCommands();

        // Register listeners
        registerListeners();

        // Create lobby world and load arena configurations
        fileManager.getLobbyFile().createLobbyWorld(fileManager.getLobbyFile().getWorldName());
        fileManager.getArenaFile().createArenasFromConfig();

        getLogger().info("Your plugin has been enabled!");
    }
    
    private void loadServerVersion(){
		String versionString = Bukkit.getBukkitVersion();
		version = 0;

		for (int i = MIN_VERSION; i <= MAX_VERSION; i ++){
			if (versionString.contains("1." + i)){
				version = i;
			}
		}

		if (version == 0) {
			version = MIN_VERSION;
			Bukkit.getLogger().warning("[Bedwars] Failed to detect server version! " + versionString + "?");
		}else {
			Bukkit.getLogger().info("[Bedwars] 1." + version + " Server detected!");
		}
	}

	public int getVersion() {
		return version;
	}

    @Override
    public void onDisable() {
        if (mysqlConnection != null) {
            mysqlConnection.close();
        }

        // Clean up any other resources if needed

        getLogger().info("Your plugin has been disabled!");
    }

    public static Main getInstance() {
        return instance;
    }

    public FileManager getFileManager() {
        return fileManager;
    }

    public GameCountdown getCountdownManager() {
        return countdownManager;
    }

    public Game getGame() {
        return game;
    }

    public ScoreboardManager getScoreboardManager() {
        return scoreboardManager;
    }

    public OreManager getGoldManager() {
        return goldManager;
    }

    public MySQLDatabase getMysqlDatabase() {
        return mysqlDatabase;
    }

    public MySQLConnection getMysqlConnection() {
        return mysqlConnection;
    }

    public MySQLFile getSqlFile() {
        return sqlFile;
    }
	
	public TeamManager getTeamManager() {
		return teamManager;
	}

	private void registerCommands() {
        getCommand("createarena").setExecutor(new CreateArena());
        getCommand("setteamspawn").setExecutor(new SetTeamSpawn());
        getCommand("joinarena").setExecutor(new JoinArena());
        getCommand("leavearena").setExecutor(new JoinArena());
        getCommand("setarenalobby").setExecutor(new SetArenaLobby());
        getCommand("setmainlobby").setExecutor(new SetMainLobby());
        getCommand("arenainfo").setExecutor(new ArenaInfo());
        getCommand("tpworld").setExecutor(new TeleportWorld());
        getCommand("createworld").setExecutor(new TeleportWorld());
        getCommand("setgoldspawn").setExecutor(new SetGenerator());
        getCommand("addkill").setExecutor(new AddKill(this, mysqlDatabase));
        getCommand("setgen").setExecutor(new SetGenerator());
        getCommand("setmode").setExecutor(new SetMode());
        getCommand("addteam").setExecutor(new AddTeam());
        getCommand("jointeam").setExecutor(new JoinTeam());
        getCommand("setbedlocation").setExecutor(new SetBedLocation());
        getCommand("load").setExecutor(new MapResetCommand());    
        getCommand("unload").setExecutor(new MapResetCommand());   
        getCommand("setshop").setExecutor(new SetShop());    
    }

    private void registerListeners() {
        Listener[] listeners = {
            new AxeListener(),
            new GameListener(),
            new ShopInventory(),
            new ShopListener(),
            // Register other listeners here
        };

        PluginManager pluginManager = getServer().getPluginManager();
        for (Listener listener : listeners) {
            pluginManager.registerEvents(listener, this);
        }
    }

	public GameManager getGameManager() {
		return gameManager;
	}

	public ShopInventory getShopInventory() {
		return shopInventory;
	}
}