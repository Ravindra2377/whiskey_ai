package com.boozer.nexus.cli.commands;

public class HealthCommand implements Command {
    @Override
    public String name() { return "health"; }

    @Override
    public String description() { return "Print OK and exit"; }

    @Override
    public int run(String[] args) {
        System.out.println("OK");
        return 0;
    }
}
