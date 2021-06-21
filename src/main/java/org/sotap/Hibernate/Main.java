package org.sotap.Hibernate;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public final class Main extends JavaPlugin {
    public static int EmptyTime = 0;
    public static int maxEmptyTime = 43200;
    public static String instanceName;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        initConfig();
        if (maxEmptyTime < 10) {
            getLogger().info("You can't set an empty_time shorter than 10 seconds.");
        }
        if (checkValidity()) {
            BukkitScheduler sch = getServer().getScheduler();
            sch.runTaskTimerAsynchronously(this, new Runnable() {
                @Override
                public void run() {
                    int count = Bukkit.getOnlinePlayers().size();
                    if (count == 0) {
                        Main.EmptyTime += 1;
                    }
                    if (count > 0) {
                        // clear empty time
                        Main.EmptyTime = 0;
                    }
                    if (Main.maxEmptyTime - Main.EmptyTime < 60) {
                        getLogger().info("The instance will be destoried in " + (Main.maxEmptyTime - Main.EmptyTime + 1)
                                + " second(s).");
                    }
                    if (Main.EmptyTime >= Main.maxEmptyTime) {
                        DeleteInstance del = new DeleteInstance(instanceName);
                        if (!del.d()) {
                            getLogger().info("Failed to delete instance.");
                        }
                    }
                }
            }, 0L, 20L);
            getLogger().info("Player count detection task begins");
        } else {
            getLogger().info("Unable to start player count detection task. Reason:");
            getLogger().info("The necessary item in config.yml is not fulfilled.");
            getLogger().info("Please fill them all and restart the server to enable the task.");
        }
    }

    @Override
    public void onDisable() {
        this.getLogger().info("Hibernate disabled");
    }

    public void initConfig() {
        var cfg = getConfig();
        DeleteInstance.backupScriptPath = cfg.getString("backup_script_path");
        DeleteInstance.accessKeyId = cfg.getString("access_key_id");
        DeleteInstance.accessKeySecret = cfg.getString("access_key_secret");
        DeleteInstance.region = cfg.getString("region");
        maxEmptyTime = cfg.getInt("max_empty_time");
        instanceName = cfg.getString("instance_name");
    }

    public boolean checkValidity() {
        if (isStringValid(DeleteInstance.accessKeyId) && isStringValid(DeleteInstance.accessKeySecret)
                && isStringValid(DeleteInstance.region) && maxEmptyTime >= 10 && isStringValid(instanceName)) {
            return true;
        }
        return false;
    }

    public boolean isStringValid(String str) {
        if (null == str)
            return false;
        if (str.length() == 0)
            return false;
        if (str.equals(""))
            return false;
        return true;
    }
}
