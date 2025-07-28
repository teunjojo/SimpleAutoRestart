package com.teunjojo;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;
import java.util.function.Consumer;

public class UpdateChecker {

    private final JavaPlugin plugin;

    public UpdateChecker(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void getVersion(final Consumer<String> consumer) {
        new Thread(() -> {
            // Get the latest release from the GitHub API
            try (InputStream is = new URL("https://api.github.com/repos/teunjojo/SimpleAutoRestart/releases/latest")
                    .openStream();
                    Scanner scanner = new Scanner(is)) {
                // Get tag_name from the JSON response
                if (scanner.hasNext()) {

                    // Get the JSON response
                    String response = scanner.useDelimiter("\\A").next();

                    // Parse the JSON response
                    String tagName = response.split("\"tag_name\":\"")[1].split("\",")[0];

                    // Return the tag name
                    consumer.accept(tagName);
                }
            } catch (IOException exception) {
                this.plugin.getLogger().info("Cannot look for updates: " + exception.getMessage());
            }
        }).start();
    }
}