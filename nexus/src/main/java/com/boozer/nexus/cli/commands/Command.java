package com.boozer.nexus.cli.commands;

public interface Command {
    String name();
    String description();
    int run(String[] args) throws Exception;
}
