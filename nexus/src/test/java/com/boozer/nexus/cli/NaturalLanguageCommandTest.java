package com.boozer.nexus.cli;

import com.boozer.nexus.cli.commands.Command;
import com.boozer.nexus.cli.commands.NaturalLanguageCommand;
import com.boozer.nexus.nl.ConversationContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class NaturalLanguageCommandTest {
    private ConversationContext conversationContext;
    private NaturalLanguageCommand command;
    private CountingCommand catalogCommand;

    @BeforeEach
    void setUp() {
        conversationContext = new ConversationContext();
        command = new NaturalLanguageCommand(conversationContext, null, null);

        catalogCommand = new CountingCommand();
        Map<String, Command> registry = new HashMap<>();
        registry.put("catalog", catalogCommand);
        registry.put("nl", command);
        command.setCommandRegistry(registry);
    }

    @Test
    void shouldReplayLastCommandWhenAsked() throws Exception {
        var first = command.process("list python scripts", false);
        assertEquals(0, first.getExitCode());
        assertTrue(first.isExecuted());
        assertEquals(1, catalogCommand.invocationCount());

        var repeat = command.process("repeat that", false);
        assertEquals(0, repeat.getExitCode());
        assertTrue(repeat.isExecuted());
        assertEquals(2, catalogCommand.invocationCount());

        assertArrayEquals(catalogCommand.invocations().get(0), catalogCommand.invocations().get(1));
    }

    private static class CountingCommand implements Command {
        private final List<String[]> invocations = new ArrayList<>();

        @Override
        public String name() {
            return "catalog";
        }

        @Override
        public String description() {
            return "Mock command";
        }

        @Override
        public int run(String[] args) {
            invocations.add(args);
            return 0;
        }

        int invocationCount() {
            return invocations.size();
        }

        List<String[]> invocations() {
            return invocations;
        }
    }
}
