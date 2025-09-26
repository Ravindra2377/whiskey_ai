package com.boozer.nexus.cli.commands;

public class VersionCommand implements Command {
    private final String version;

    public VersionCommand(String version) {
        this.version = version;
    }

    @Override
    public String name() { return "version"; }

    @Override
    public String description() { return "Print version and exit"; }

    @Override
    public int run(String[] args) {
        System.out.println(version);
        return 0;
    }
}
