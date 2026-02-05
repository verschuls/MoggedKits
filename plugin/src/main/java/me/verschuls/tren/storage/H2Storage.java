package me.verschuls.tren.storage;

import me.verschuls.tren.MoggedKits;
import me.verschuls.tren.utils.Logger;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.nio.file.Path;
import java.sql.*;
import java.util.UUID;
import java.util.concurrent.Executor;

public class H2Storage extends StorageHandler {

    private static final String CREATE_COOLDOWNS_TABLE = """
            CREATE TABLE IF NOT EXISTS cooldowns (
                uuid VARCHAR(36) NOT NULL,
                kit VARCHAR(64) NOT NULL,
                expires_at BIGINT NOT NULL,
                PRIMARY KEY (uuid, kit)
            )
            """;

    private static final String CREATE_ACCESS_TABLE = """
            CREATE TABLE IF NOT EXISTS access (
                uuid VARCHAR(36) NOT NULL,
                kit VARCHAR(64) NOT NULL,
                PRIMARY KEY (uuid, kit)
            )
            """;

    private Connection connection;
    private final Path dbPath;

    public H2Storage(JavaPlugin plugin, Executor executor) {
        super(plugin, executor);
        Logger.info("Initiating H2 storage...");
        this.dbPath = plugin.getDataPath().resolve("database");
        try {
            Class.forName("org.h2.Driver");
            String url = "jdbc:h2:" + dbPath.toAbsolutePath() + ";MODE=MySQL;AUTO_RECONNECT=TRUE";
            this.connection = DriverManager.getConnection(url);

            try (Statement stmt = connection.createStatement()) {
                stmt.execute(CREATE_COOLDOWNS_TABLE);
                stmt.execute(CREATE_ACCESS_TABLE);
            }

            Logger.success("H2 storage connected!");
        } catch (Exception e) {
            Logger.error("Failed to initialize H2 storage", e);
            MoggedKits.disable();
        }
    }

    private Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            String url = "jdbc:h2:" + dbPath.toAbsolutePath() + ";MODE=MySQL;AUTO_RECONNECT=TRUE";
            this.connection = DriverManager.getConnection(url);
        }
        return connection;
    }

    @Override
    public void putCooldown(Player player, String kit, int time) {
        String sql = "MERGE INTO cooldowns (uuid, kit, expires_at) KEY (uuid, kit) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, player.getUniqueId().toString());
            stmt.setString(2, kit);
            stmt.setLong(3, System.currentTimeMillis() + (time * 60_000L));
            stmt.executeUpdate();
        } catch (SQLException e) {
            Logger.error("Failed to set cooldown for {} kit {}", e, player.getName(), kit);
        }
    }

    @Override
    public Long getCooldown(Player player, String kit) {
        String sql = "SELECT expires_at FROM cooldowns WHERE uuid = ? AND kit = ?";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, player.getUniqueId().toString());
            stmt.setString(2, kit);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("expires_at");
                }
            }
        } catch (SQLException e) {
            Logger.error("Failed to get cooldown for {} kit {}", e, player.getName(), kit);
        }
        return 0L;
    }

    @Override
    public void setAccess(Player player, String kit, boolean access) {
        try {
            if (access) {
                String sql = "MERGE INTO access (uuid, kit) KEY (uuid, kit) VALUES (?, ?)";
                try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
                    stmt.setString(1, player.getUniqueId().toString());
                    stmt.setString(2, kit);
                    stmt.executeUpdate();
                }
            } else {
                String sql = "DELETE FROM access WHERE uuid = ? AND kit = ?";
                try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
                    stmt.setString(1, player.getUniqueId().toString());
                    stmt.setString(2, kit);
                    stmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            Logger.error("Failed to set access for {} kit {}", e, player.getName(), kit);
        }
    }

    @Override
    public boolean hasAccess(Player player, String kit) {
        String sql = "SELECT 1 FROM access WHERE uuid = ? AND kit = ?";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, player.getUniqueId().toString());
            stmt.setString(2, kit);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            Logger.error("Failed to check access for {} kit {}", e, player.getName(), kit);
        }
        return false;
    }

    public int getPlayerCount() {
        String sql = "SELECT COUNT(DISTINCT uuid) FROM (SELECT uuid FROM cooldowns UNION SELECT uuid FROM access)";
        try (Statement stmt = getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            Logger.error("Failed to get player count", e);
        }
        return 0;
    }

    @Override
    public Info getInfo() {
        boolean connected = false;
        try {
            connected = connection != null && !connection.isClosed();
        } catch (SQLException ignored) {}
        return new Info("H2", connected, getPlayerCount(), null);
    }

    @Override
    public void clear() {

    }

    public void shutdown() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            Logger.error("Failed to close H2 connection", e);
        }
        Logger.info("H2 storage disconnected");
    }

    // Migration support - import data directly
    void importCooldown(UUID uuid, String kit, long expiresAt) {
        String sql = "MERGE INTO cooldowns (uuid, kit, expires_at) KEY (uuid, kit) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, uuid.toString());
            stmt.setString(2, kit);
            stmt.setLong(3, expiresAt);
            stmt.executeUpdate();
        } catch (SQLException e) {
            Logger.error("Failed to import cooldown for {} kit {}", e, uuid.toString(), kit);
        }
    }
}
