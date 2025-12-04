package me.verschuls.tren.config;

import de.exlll.configlib.Comment;
import lombok.Getter;
import me.verschuls.cbu.BaseConfig;
import me.verschuls.cbu.Header;

import java.nio.file.Path;
import java.util.concurrent.Executor;

public class Redis extends BaseConfig<Redis.Data> {


    public Redis(Path path, Executor executor) {
        super(path, "redis", Data.class, executor);
    }

    @Getter
    @Header("""
            MoggedKits Redis Configuration

            Redis provides blazing fast kit data access across multiple servers.
            Set host and port to enable - plugin auto-migrates from YAML.
            Leave host empty to use local YAML storage.
            """)
    public static class Data extends BaseConfig.Data {

        @Comment("Redis server hostname/IP (empty = use YAML storage)")
        private String host = "";

        @Comment("Redis port (default: 6379)")
        private Integer port = -1;

        @Comment("Username for Redis ACL (leave empty if not using)")
        private String user = "";

        @Comment("Redis password")
        private String password = "";

        @Comment("Database index (0-15)")
        private Integer database = 0;

        @Comment("Connection timeout in milliseconds")
        private Integer timeout = 1000;

        @Comment("Client identifier in Redis")
        private String clientName = "MoggedKits";

        @Comment("Enable SSL/TLS encryption")
        private boolean ssl = false;
    }
}
