package com.boozer.nexus;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@TestConfiguration
@ComponentScan(
    basePackages = "com.boozer.nexus",
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = {
            BoozerFileController.class,
            com.boozer.nexus.service.BoozerFileService.class,
            com.boozer.nexus.repository.BoozerFileRepository.class
        }
    )
)
public class TestConfig {
}
