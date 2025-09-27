package com.boozer.nexus.persistence;

import com.boozer.nexus.cli.model.OperationDescriptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Path;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Component
@ConditionalOnBean(WorkspaceRepository.class)
public class OperationCatalogPersistenceService {
    private final WorkspaceRepository workspaceRepository;
    private final OperationRepository operationRepository;

    public OperationCatalogPersistenceService(WorkspaceRepository workspaceRepository, OperationRepository operationRepository) {
        this.workspaceRepository = workspaceRepository;
        this.operationRepository = operationRepository;
    }

    @Transactional
    public void storeCatalog(Path root, List<OperationDescriptor> operations) {
        WorkspaceEntity workspace = workspaceRepository
                .findByCode(codeFromPath(root))
                .orElseGet(() -> workspaceRepository.save(new WorkspaceEntity(codeFromPath(root), displayNameFromPath(root))));

        operationRepository.deleteAllByWorkspace(workspace);

        OffsetDateTime now = OffsetDateTime.now();
        for (OperationDescriptor descriptor : operations) {
            String tags = descriptor.tags == null ? "" : String.join("|", descriptor.tags);
            OperationEntity entity = new OperationEntity(
                    workspace,
                    descriptor.name,
                    descriptor.type,
                    descriptor.path,
                    descriptor.summary,
                    tags,
                    now
            );
            operationRepository.save(entity);
        }
    }

    private String codeFromPath(Path root) {
        return root.normalize().toAbsolutePath().toString().replaceAll("[^a-zA-Z0-9]", "_").toLowerCase(Locale.ROOT);
    }

    private String displayNameFromPath(Path root) {
        String fileName = Optional.ofNullable(root.getFileName()).map(Path::toString).orElse("workspace");
        return fileName.isBlank() ? root.toAbsolutePath().toString() : fileName;
    }
}
