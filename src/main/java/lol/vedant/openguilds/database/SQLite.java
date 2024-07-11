package lol.vedant.openguilds.database;

import lol.vedant.openguilds.OpenGuilds;
import lol.vedant.openguilds.guild.Guild;
import lol.vedant.openguilds.guild.GuildPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SQLite implements Database {

    private OpenGuilds plugin;
    private Connection connection;
    private final String url;

    public SQLite(OpenGuilds plugin) {
        this.plugin = plugin;

        File file = new File(plugin.getDataFolder(), "database.db");
        if(!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        this.url = "jdbc:sqlite:" + file.getAbsolutePath();

        try {
            Class.forName("org.sqlite.JDBC");
            this.connection = DriverManager.getConnection(url);
        } catch (ClassNotFoundException | SQLException e) {
            plugin.getLogger().severe("SQLite driver not found on your system!");
            plugin.getLogger().severe("Disabling Open Guilds Plugin...");
            plugin.getPluginLoader().disablePlugin(plugin);
            e.printStackTrace();
        }
    }

    @Override
    public void init() {
        String sql = "CREATE TABLE IF NOT EXISTS open_guilds (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "display_name VARCHAR(255) NOT NULL," +
                "description VARCHAR(255)," +
                "tag VARCHAR(32));";

        String sql2 = "CREATE TABLE IF NOT EXISTS open_guilds_members (" +
                "uuid VARCHAR(255) PRIMARY KEY," +
                "name VARCHAR(255)," +
                "is_leader INT," +
                "guild_id INT)";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.executeUpdate();

            PreparedStatement ps2 = connection.prepareStatement(sql2);
            ps2.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isInGuild(UUID player) {
        String sql = "SELECT * FROM open_guilds_members WHERE uuid=?";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, player.toString());
            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Guild getGuild(int id) {
        String sql = "SELECT * FROM open_guilds WHERE id=?";
        String sql2 = "SELECT * FROM open_guilds_members WHERE guild_id=?";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            Guild guild;
            if(!rs.next()) {
                return null;
            } else {
                guild = new Guild(rs.getInt(id));
                guild.setDescription(rs.getString("description"));
                guild.setDisplayName(rs.getString("display_name"));
                guild.setTag(rs.getString("tag"));
            }

            PreparedStatement ps2 = connection.prepareStatement(sql2);
            ps2.setInt(1, id);

            ResultSet rs2 = ps2.executeQuery();
            List<GuildPlayer> members = new ArrayList<>();

            while (rs2.next()) {
                Player player = (Player) Bukkit.getOfflinePlayer(UUID.fromString(rs.getString("uuid")));
                members.add(new GuildPlayer(player));
            }

            guild.setMembers(members);
            return guild;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Guild getGuildByPlayer(UUID player) {
        String sql = "SELECT guild_id FROM open_guilds_members WHERE uuid=?";
        String sql2 = "SELECT * FROM open_guilds WHERE id=?";
        String sql3 = "SELECT * FROM open_guilds_members WHERE guild_id=?";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, player.toString());
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                return null;
            }

            int guildId = rs.getInt("guild_id");

            PreparedStatement ps2 = connection.prepareStatement(sql2);
            ps2.setInt(1, guildId);
            ResultSet rs2 = ps2.executeQuery();
            if (!rs2.next()) {
                return null;
            }

            Guild guild = new Guild(rs2.getInt("id"));
            guild.setDescription(rs2.getString("description"));
            guild.setDisplayName(rs2.getString("display_name"));
            guild.setTag(rs2.getString("tag"));

            // Get the members of the guild
            PreparedStatement ps3 = connection.prepareStatement(sql3);
            ps3.setInt(1, guildId);
            ResultSet rs3 = ps3.executeQuery();
            List<GuildPlayer> members = new ArrayList<>();

            while (rs3.next()) {
                UUID memberUUID = UUID.fromString(rs3.getString("uuid"));
                Player memberPlayer = Bukkit.getOfflinePlayer(memberUUID).getPlayer();
                GuildPlayer guildPlayer = new GuildPlayer(memberPlayer);
                members.add(guildPlayer);
            }

            guild.setMembers(members);
            return guild;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
