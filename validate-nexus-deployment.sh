#!/bin/bash
# NEXUS AI Platform Deployment Validation Script
# Comprehensive validation of the complete NEXUS AI system

echo "=========================================="
echo "NEXUS AI Platform Deployment Validation"
echo "=========================================="
echo ""

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Validation counters
TOTAL_CHECKS=0
PASSED_CHECKS=0
FAILED_CHECKS=0

# Function to print status
print_status() {
    local status=$1
    local message=$2
    TOTAL_CHECKS=$((TOTAL_CHECKS + 1))
    
    if [ "$status" = "PASS" ]; then
        echo -e "${GREEN}✓ PASS${NC} - $message"
        PASSED_CHECKS=$((PASSED_CHECKS + 1))
    elif [ "$status" = "FAIL" ]; then
        echo -e "${RED}✗ FAIL${NC} - $message"
        FAILED_CHECKS=$((FAILED_CHECKS + 1))
    elif [ "$status" = "WARN" ]; then
        echo -e "${YELLOW}⚠ WARN${NC} - $message"
    else
        echo -e "${BLUE}ℹ INFO${NC} - $message"
    fi
}

# Function to check file exists
check_file() {
    local file=$1
    local description=$2
    
    if [ -f "$file" ]; then
        print_status "PASS" "$description exists: $file"
        return 0
    else
        print_status "FAIL" "$description missing: $file"
        return 1
    fi
}

# Function to check directory exists
check_directory() {
    local dir=$1
    local description=$2
    
    if [ -d "$dir" ]; then
        print_status "PASS" "$description exists: $dir"
        return 0
    else
        print_status "FAIL" "$description missing: $dir"
        return 1
    fi
}

echo "1. NEXUS Application Structure Validation"
echo "========================================="

# Check main application structure
check_file "nexus/src/main/java/com/boozer/nexus/NexusApplication.java" "Main Application Class"
check_file "nexus/pom.xml" "Maven Configuration"
check_file "nexus/src/main/resources/application.properties" "Application Properties"

echo ""
echo "2. AI Integration Components"
echo "============================"

# AI Integration files
check_file "nexus/src/main/java/com/boozer/nexus/ai/ExternalAIIntegrationService.java" "AI Integration Service"
check_file "nexus/src/main/java/com/boozer/nexus/ai/IntelligentAIRouter.java" "Intelligent AI Router"
check_file "nexus/src/main/java/com/boozer/nexus/ai/providers/OpenAIProvider.java" "OpenAI Provider"
check_file "nexus/src/main/java/com/boozer/nexus/ai/providers/AnthropicProvider.java" "Anthropic Provider"
check_file "nexus/src/main/java/com/boozer/nexus/ai/providers/GoogleAIProvider.java" "Google AI Provider"
check_file "nexus/src/main/java/com/boozer/nexus/ai/models/AIModels.java" "AI Models"

echo ""
echo "3. Consciousness Engine Components"
echo "=================================="

# Consciousness Engine files
check_file "nexus/src/main/java/com/boozer/nexus/consciousness/ConsciousnessEngine.java" "Consciousness Engine"
check_file "nexus/src/main/java/com/boozer/nexus/consciousness/models/ConsciousnessModels.java" "Consciousness Models"
check_file "nexus/src/main/java/com/boozer/nexus/consciousness/emergence/EmergenceModels.java" "Emergence Models"

echo ""
echo "4. Quantum Computing Components"
echo "==============================="

# Quantum Computing files
check_file "nexus/src/main/java/com/boozer/nexus/quantum/QuantumProcessor.java" "Quantum Processor"
check_file "nexus/src/main/java/com/boozer/nexus/quantum/QuantumSimulator.java" "Quantum Simulator"
check_file "nexus/src/main/java/com/boozer/nexus/quantum/QuantumCircuitBuilder.java" "Quantum Circuit Builder"
check_file "nexus/src/main/java/com/boozer/nexus/quantum/models/QuantumModels.java" "Quantum Models"

echo ""
echo "5. Neuromorphic Processing Components"
echo "====================================="

# Neuromorphic Processing files
check_file "nexus/src/main/java/com/boozer/nexus/neuromorphic/NeuromorphicProcessor.java" "Neuromorphic Processor"
check_file "nexus/src/main/java/com/boozer/nexus/neuromorphic/models/NeuromorphicModels.java" "Neuromorphic Models"
check_file "nexus/src/main/java/com/boozer/nexus/neuromorphic/models/NetworkModels.java" "Network Models"
check_file "nexus/src/main/java/com/boozer/nexus/neuromorphic/learning/NeuromorphicLearning.java" "Neuromorphic Learning"

echo ""
echo "6. Security Framework Components"
echo "================================"

# Security files
check_file "nexus/src/main/java/com/boozer/nexus/security/SecurityConfig.java" "Security Configuration"
check_file "nexus/src/main/java/com/boozer/nexus/security/JWTAuthenticationFilter.java" "JWT Authentication Filter"
check_file "nexus/src/main/java/com/boozer/nexus/security/JWTUtil.java" "JWT Utility"
check_file "nexus/src/main/java/com/boozer/nexus/entities/User.java" "User Entity"
check_file "nexus/src/main/java/com/boozer/nexus/entities/Role.java" "Role Entity"
check_file "nexus/src/main/java/com/boozer/nexus/controllers/AuthController.java" "Authentication Controller"

echo ""
echo "7. Testing Framework Components"
echo "==============================="

# Test files
check_file "nexus/src/test/java/com/boozer/nexus/integration/NexusIntegrationTest.java" "Integration Tests"
check_file "nexus/src/test/java/com/boozer/nexus/performance/NexusPerformanceTest.java" "Performance Tests"
check_file "nexus/src/test/java/com/boozer/nexus/security/NexusSecurityTest.java" "Security Tests"
check_file "nexus/src/test/java/com/boozer/nexus/ai/NexusAIProviderTest.java" "AI Provider Tests"
check_file "nexus/src/test/java/com/boozer/nexus/config/TestConfig.java" "Test Configuration"
check_file "nexus/src/test/java/com/boozer/nexus/suite/NexusTestSuite.java" "Test Suite"
check_file "nexus/src/test/resources/application-test.properties" "Test Properties"

echo ""
echo "8. Directory Structure Validation"
echo "================================="

# Check directory structure
check_directory "nexus/src/main/java/com/boozer/nexus" "Main Java Source"
check_directory "nexus/src/main/java/com/boozer/nexus/ai" "AI Package"
check_directory "nexus/src/main/java/com/boozer/nexus/consciousness" "Consciousness Package"
check_directory "nexus/src/main/java/com/boozer/nexus/quantum" "Quantum Package"
check_directory "nexus/src/main/java/com/boozer/nexus/neuromorphic" "Neuromorphic Package"
check_directory "nexus/src/main/java/com/boozer/nexus/security" "Security Package"
check_directory "nexus/src/test/java/com/boozer/nexus" "Test Source"

echo ""
echo "9. Configuration Files Validation"
echo "================================="

# Check configuration files
if [ -f "nexus/src/main/resources/application.properties" ]; then
    if grep -q "spring.application.name=nexus" nexus/src/main/resources/application.properties; then
        print_status "PASS" "Application name configured correctly"
    else
        print_status "FAIL" "Application name not configured"
    fi
    
    if grep -q "server.port" nexus/src/main/resources/application.properties; then
        print_status "PASS" "Server port configured"
    else
        print_status "WARN" "Server port not explicitly configured (will use default)"
    fi
fi

echo ""
echo "10. Maven Dependencies Validation"
echo "================================="

# Check Maven configuration
if [ -f "nexus/pom.xml" ]; then
    if grep -q "spring-boot-starter" nexus/pom.xml; then
        print_status "PASS" "Spring Boot dependencies configured"
    else
        print_status "FAIL" "Spring Boot dependencies missing"
    fi
    
    if grep -q "junit" nexus/pom.xml; then
        print_status "PASS" "Testing dependencies configured"
    else
        print_status "WARN" "Testing dependencies may be missing"
    fi
fi

echo ""
echo "11. Code Quality Checks"
echo "======================="

# Basic code quality checks
java_files=$(find nexus/src -name "*.java" 2>/dev/null | wc -l)
if [ "$java_files" -gt 0 ]; then
    print_status "PASS" "Found $java_files Java source files"
else
    print_status "FAIL" "No Java source files found"
fi

test_files=$(find nexus/src/test -name "*Test.java" 2>/dev/null | wc -l)
if [ "$test_files" -gt 0 ]; then
    print_status "PASS" "Found $test_files test files"
else
    print_status "WARN" "No test files found"
fi

echo ""
echo "12. Database Configuration"
echo "========================="

# Check database configuration
if [ -f "nexus/src/main/resources/application.properties" ]; then
    if grep -q "spring.datasource" nexus/src/main/resources/application.properties; then
        print_status "PASS" "Database configuration found"
    else
        print_status "WARN" "Database configuration not found (may use defaults)"
    fi
fi

echo ""
echo "13. Security Configuration Validation"
echo "====================================="

# Check security configuration
if [ -f "nexus/src/main/java/com/boozer/nexus/security/SecurityConfig.java" ]; then
    if grep -q "@EnableWebSecurity" nexus/src/main/java/com/boozer/nexus/security/SecurityConfig.java; then
        print_status "PASS" "Web security enabled"
    else
        print_status "FAIL" "Web security not properly configured"
    fi
fi

echo ""
echo "14. API Endpoints Validation"
echo "============================"

# Check for controller classes
controller_files=$(find nexus/src/main/java -name "*Controller.java" 2>/dev/null | wc -l)
if [ "$controller_files" -gt 0 ]; then
    print_status "PASS" "Found $controller_files controller classes"
else
    print_status "WARN" "No controller classes found"
fi

echo ""
echo "15. Deployment Readiness"
echo "========================"

# Check deployment files
if [ -f "docker-compose.yml" ]; then
    print_status "PASS" "Docker Compose configuration found"
else
    print_status "WARN" "Docker Compose configuration not found"
fi

if [ -f "README.md" ]; then
    print_status "PASS" "README documentation found"
else
    print_status "WARN" "README documentation not found"
fi

echo ""
echo "=========================================="
echo "NEXUS AI Platform Validation Summary"
echo "=========================================="
echo -e "Total Checks: ${BLUE}$TOTAL_CHECKS${NC}"
echo -e "Passed: ${GREEN}$PASSED_CHECKS${NC}"
echo -e "Failed: ${RED}$FAILED_CHECKS${NC}"
echo -e "Success Rate: ${BLUE}$(( (PASSED_CHECKS * 100) / TOTAL_CHECKS ))%${NC}"
echo ""

if [ "$FAILED_CHECKS" -eq 0 ]; then
    echo -e "${GREEN}🎉 NEXUS AI Platform validation completed successfully!${NC}"
    echo -e "${GREEN}✅ The platform is ready for deployment.${NC}"
    exit 0
elif [ "$FAILED_CHECKS" -lt 5 ]; then
    echo -e "${YELLOW}⚠️ NEXUS AI Platform validation completed with minor issues.${NC}"
    echo -e "${YELLOW}🔧 Please review and address the failed checks before deployment.${NC}"
    exit 1
else
    echo -e "${RED}❌ NEXUS AI Platform validation failed.${NC}"
    echo -e "${RED}🚨 Critical issues found. Platform is not ready for deployment.${NC}"
    exit 1
fi