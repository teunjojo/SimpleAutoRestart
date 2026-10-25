package com.teunjojo;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.command.CommandResult;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

class CommandTest {

    private ServerMock server;
    private SimpleAutoRestart plugin;

    @BeforeEach
    public void setUp() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(SimpleAutoRestart.class);
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    public void sarCommandPermission() {
        // Arrange
        PlayerMock player = server.addPlayer();
        String expectedMessage = "§cI'm sorry, but you do not have permission to perform this command. Please contact the server administrators if you believe that this is in error.";

        // Act
        CommandResult result = server.executePlayer("sar");
        String actualMessage = player.nextMessage();
        if (actualMessage == null) {
            actualMessage = "";
        }

        // Assert
        assertEquals(true, result.hasSucceeded());
        assertEquals(expectedMessage, actualMessage.trim(), "should return no permission error");
    }

    @Test
    public void simpleautorestartCommand() {
        // Arrange
        PlayerMock player = server.addPlayer();
        player.addAttachment(plugin, "simpleautorestart.use", true);
        String expectedMessage = "Usage: /simpleautorestart set <hour> <minute> [day]";

        // Act
        CommandResult result = server.executePlayer("simpleautorestart");
        String actualMessage = player.nextMessage();
        if (actualMessage == null) {
            actualMessage = "";
        }

        // Assert
        assertEquals(false, result.hasSucceeded());
        assertEquals(expectedMessage, actualMessage.trim(), "should return usage");
    }

    @Test
    public void autorestartCommand() {
        // Arrange
        PlayerMock player = server.addPlayer();
        player.addAttachment(plugin, "simpleautorestart.use", true);
        String expectedMessage = "Usage: /simpleautorestart set <hour> <minute> [day]";

        // Act
        CommandResult result = server.executePlayer("autorestart");
        String actualMessage = player.nextMessage();
        if (actualMessage == null) {
            actualMessage = "";
        }

        // Assert
        assertEquals(false, result.hasSucceeded());
        assertEquals(expectedMessage, actualMessage.trim(), "should return usage");
    }

    @Test
    public void sarCommand() {
        // Arrange
        PlayerMock player = server.addPlayer();
        player.addAttachment(plugin, "simpleautorestart.use", true);
        String expectedMessage = "Usage: /simpleautorestart set <hour> <minute> [day]";

        // Act
        CommandResult result = server.executePlayer("sar");
        String actualMessage = player.nextMessage();
        if (actualMessage == null) {
            actualMessage = "";
        }

        // Assert
        assertEquals(false, result.hasSucceeded());
        assertEquals(expectedMessage, actualMessage.trim(), "should return usage");
    }

}
