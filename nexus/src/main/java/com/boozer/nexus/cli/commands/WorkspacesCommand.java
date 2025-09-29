package com.boozer.nexus.cli.commands;

import com.boozer.nexus.persistence.WorkspaceRepository;
import com.boozer.nexus.persistence.WorkspaceEntity;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class WorkspacesCommand implements Command {
    @Autowired
    private WorkspaceRepository workspaceRepository;

    @Override
    public String name() { return "workspaces"; }

    @Override
    public String description() { return "List all available workspaces/projects"; }

    @Override
    public int run(String[] args) {
        List<WorkspaceEntity> workspaces = workspaceRepository.findAll();
        System.out.println("Available workspaces:");
        for (WorkspaceEntity ws : workspaces) {
            System.out.println(ws.getCode() + "\t" + ws.getDisplayName());
        }
        return 0;
    }
}
