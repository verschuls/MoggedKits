package me.verschuls.tren.storage;

import io.lettuce.core.RedisClient;
import io.lettuce.core.pubsub.RedisPubSubAdapter;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.lettuce.core.pubsub.api.async.RedisPubSubAsyncCommands;
import me.verschuls.tren.utils.Logger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class RedisPubSub {

    private final String instanceId;
    private static final String CHANNEL_PREFIX = "moggedkits:";

    private final StatefulRedisPubSubConnection<String, String> subConnection;
    private final StatefulRedisPubSubConnection<String, String> pubConnection;
    private final RedisPubSubAsyncCommands<String, String> subCommands;
    private final RedisPubSubAsyncCommands<String, String> pubCommands;

    private final Map<String, Consumer<String>> handlers = new ConcurrentHashMap<>();

    RedisPubSub(RedisClient client, String instanceId) {
        this.instanceId = instanceId;
        Logger.debug("Creating PubSub connections...");
        this.subConnection = client.connectPubSub();
        Logger.debug("Sub connection created");
        this.pubConnection = client.connectPubSub();
        Logger.debug("Pub connection created");
        this.subCommands = subConnection.async();
        this.pubCommands = pubConnection.async();
        Logger.debug("Async commands initialized");

        Logger.debug("Adding message listener...");
        subConnection.addListener(new RedisPubSubAdapter<>() {
            @Override
            public void message(String channel, String message) {
                Logger.debug("Raw message received on channel: {}, length: {}", channel, String.valueOf(message.length()));
                handleMessage(channel, message);
            }
        });

        Logger.info("PubSub initialized, instance: {}", instanceId);
    }

    // subscribe to channel, handler gets payload string only (self filtered)
    public void subscribe(String channel, Consumer<String> handler) {
        String fullChannel = CHANNEL_PREFIX + channel;
        Logger.debug("Subscribing to channel: {}", fullChannel);
        handlers.put(fullChannel, handler);
        subCommands.subscribe(fullChannel).thenRun(() -> {
            Logger.debug("Successfully subscribed to: {}", fullChannel);
        }).exceptionally(e -> {
            Logger.error("Failed to subscribe to: {}", (Exception) e, fullChannel);
            return null;
        });
    }

    public void unsubscribe(String channel) {
        String fullChannel = CHANNEL_PREFIX + channel;
        Logger.debug("Unsubscribing from channel: {}", fullChannel);
        handlers.remove(fullChannel);
        subCommands.unsubscribe(fullChannel);
    }

    public void broadcast(String channel, String payload) {
        String fullChannel = CHANNEL_PREFIX + channel;
        // message format: instanceId|payload
        String message = instanceId + "|" + payload;
        Logger.debug("Broadcasting to {}, message length: {}", fullChannel, String.valueOf(message.length()));
        pubCommands.publish(fullChannel, message).thenAccept(receivers -> {
            Logger.debug("Message delivered to {} subscribers", String.valueOf(receivers));
        }).exceptionally(e -> {
            Logger.error("Failed to broadcast to: {}", (Exception) e, fullChannel);
            return null;
        });
    }

    private void handleMessage(String channel, String message) {
        Logger.debug("handleMessage called for channel: {}", channel);
        Consumer<String> handler = handlers.get(channel);
        if (handler == null) {
            Logger.debug("No handler registered for channel: {}", channel);
            return;
        }
        int sep = message.indexOf('|');
        if (sep == -1) {
            Logger.warn("Malformed pubsub message on {}", channel);
            return;
        }
        String sender = message.substring(0, sep);
        String payload = message.substring(sep + 1);
        Logger.debug("Message from instance: {}, self: {}, skipping: {}", sender, instanceId, String.valueOf(sender.equals(instanceId)));
        if (sender.equals(instanceId)) return;
        Logger.debug("Processing payload from other instance...");
        try {
            handler.accept(payload);
            Logger.debug("Handler completed successfully");
        } catch (Exception e) {
            Logger.error("Error handling pubsub on {}", e, channel);
        }
    }

    public void shutdown() {
        if (subConnection != null) subConnection.close();
        if (pubConnection != null) pubConnection.close();
        Logger.info("PubSub closed");
    }
}
