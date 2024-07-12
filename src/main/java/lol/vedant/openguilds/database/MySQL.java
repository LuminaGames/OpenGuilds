package lol.vedant.openguilds.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lol.vedant.openguilds.OpenGuilds;
import lol.vedant.openguilds.guild.Guild;
import lol.vedant.openguilds.guild.GuildPlayer;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class MySQL implements Database {

    private HikariDataSource dataSource;
    private final OpenGuilds plugin;
    private final String host;
    private final String database;
    private final String user;
    private final String pass;
    private final int port;
    private final boolean ssl;
    private final boolean certificateVerification;
    private final int poolSize;
    private final int maxLifetime;
    private final FileConfiguration config;

    public MySQL(OpenGuilds plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfiguration();
        this.host = config.getString("database.host");
        this.database = config.getString("database.database");
        this.user = config.getString("database.user");
        this.pass = config.getString("database.pass");
        this.port = config.getInt("database.port");
        this.ssl = config.getBoolean("database.ssl");
        this.certificateVerification = config.getBoolean("database.verify-certificate", true);
        this.poolSize = config.getInt("database.pool-size", 10);
        this.maxLifetime = config.getInt("database.max-lifetime", 1800);

        init();
    }


    @Override
    public void init() {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setPoolName("OpenGuilds-pool");
        hikariConfig.setMaximumPoolSize(poolSize);
        hikariConfig.setMaxLifetime(maxLifetime);
        hikariConfig.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database);
        hikariConfig.setUsername(user);
        hikariConfig.setPassword(pass);
        hikariConfig.addDataSourceProperty("useSSL", String.valueOf(ssl));

        if (!certificateVerification) {
            hikariConfig.addDataSourceProperty("verifyServerCertificate", String.valueOf(false));
        }

        hikariConfig.addDataSourceProperty("characterEncoding", "utf8");
        hikariConfig.addDataSourceProperty("encoding", "UTF-8");
        hikariConfig.addDataSourceProperty("useUnicode", "true");
        hikariConfig.addDataSourceProperty("rewriteBatchedStatements", "true");
        hikariConfig.addDataSourceProperty("jdbcCompliantTruncation", "false");
        hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", "275");
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        hikariConfig.addDataSourceProperty("socketTimeout", String.valueOf(TimeUnit.SECONDS.toMillis(30)));

        dataSource = new HikariDataSource(hikariConfig);

        try {
            dataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        createTables();
    }

    private void createTables() {
        String sql = "CREATE TABLE IF NOT EXISTS open_guilds (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "display_name VARCHAR(255) NOT NULL," +
                "description VARCHAR(255)," +
                "tag VARCHAR(32));";

        String sql2 = "CREATE TABLE IF NOT EXISTS open_guilds_members (" +
                "uuid VARCHAR(255) PRIMARY KEY," +
                "name VARCHAR(255)," +
                "is_leader INT," +
                "guild_id INT)";
        try (Connection connection = dataSource.getConnection()) {
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
        try(Connection connection = dataSource.getConnection()) {
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

        try(Connection connection = dataSource.getConnection()) {
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

        try (Connection connection = dataSource.getConnection()) {
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
