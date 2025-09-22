package com.boozer.nexus.test.suite;

import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.IncludeClassNamePatterns;

/**
 * Comprehensive Test Suite for NEXUS AI Platform
 * 
 * Orchestrates all test categories including integration tests,
 * performance benchmarks, security tests, and AI provider tests.
 */
@Suite
@SuiteDisplayName("NEXUS AI Platform Complete Test Suite")
@SelectPackages({
    "com.boozer.nexus.test.integration",
    "com.boozer.nexus.test.performance", 
    "com.boozer.nexus.test.security",
    "com.boozer.nexus.test.ai"
})
@IncludeClassNamePatterns(".*Test")
public class NexusTestSuite {
    // Test suite configuration - no implementation needed
    // JUnit 5 Platform will automatically discover and run all tests
    // in the specified packages matching the class name patterns
}

/**
 * Integration Test Suite
 */
@Suite
@SuiteDisplayName("NEXUS Integration Tests")
@SelectPackages("com.boozer.nexus.test.integration")
@IncludeClassNamePatterns(".*IntegrationTest")
public class NexusIntegrationTestSuite {
}

/**
 * Performance Test Suite  
 */
@Suite
@SuiteDisplayName("NEXUS Performance Tests")
@SelectPackages("com.boozer.nexus.test.performance")
@IncludeClassNamePatterns(".*PerformanceTest")
public class NexusPerformanceTestSuite {
}

/**
 * Security Test Suite
 */
@Suite
@SuiteDisplayName("NEXUS Security Tests") 
@SelectPackages("com.boozer.nexus.test.security")
@IncludeClassNamePatterns(".*SecurityTest")
public class NexusSecurityTestSuite {
}

/**
 * AI Provider Test Suite
 */
@Suite
@SuiteDisplayName("NEXUS AI Provider Tests")
@SelectPackages("com.boozer.nexus.test.ai")
@IncludeClassNamePatterns(".*ProviderTest")
public class NexusAITestSuite {
}