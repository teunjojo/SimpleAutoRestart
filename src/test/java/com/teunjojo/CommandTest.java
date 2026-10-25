package com.teunjojo;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;

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
}
