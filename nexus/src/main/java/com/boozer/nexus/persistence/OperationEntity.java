package com.boozer.nexus.persistence;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "operations")
public class OperationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workspace_id", nullable = false)
    private WorkspaceEntity workspace;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "type", nullable = false, length = 30)
    private String type;

    @Column(name = "path", nullable = false, length = 1024)
    private String path;

    @Column(name = "summary", length = 1024)
    private String summary;

    @Column(name = "tags", length = 512)
    private String tags; // pipe-delimited list

    @Column(name = "ingested_at", nullable = false)
    private OffsetDateTime ingestedAt;

    protected OperationEntity() {
    }

    public OperationEntity(WorkspaceEntity workspace, String name, String type, String path, String summary, String tags, OffsetDateTime ingestedAt) {
        this.workspace = workspace;
        this.name = name;
        this.type = type;
        this.path = path;
        this.summary = summary;
        this.tags = tags;
        this.ingestedAt = ingestedAt;
    }

    public UUID getId() {
        return id;
    }

    public WorkspaceEntity getWorkspace() {
        return workspace;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getPath() {
        return path;
    }

    public String getSummary() {
        return summary;
    }

    public String getTags() {
        return tags;
    }

    public OffsetDateTime getIngestedAt() {
        return ingestedAt;
    }
}
