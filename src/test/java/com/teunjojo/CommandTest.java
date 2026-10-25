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
    public void sarCommand() {
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
}
