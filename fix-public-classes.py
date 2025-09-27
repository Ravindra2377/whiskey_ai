#!/usr/bin/env python3
"""
Script to fix public class declarations in Java files.
Removes 'public' modifier from classes that should be package-private.
"""

import os
import re
import glob

def fix_public_classes_in_file(file_path):
    """Fix public class declarations in a single file."""
    try:
        with open(file_path, 'r', encoding='utf-8') as f:
            content = f.read()
        
        original_content = content
        
        # Fix public class declarations (but keep the main application class public)
        # More comprehensive patterns to fix all cases
        patterns_to_fix = [
            # Fix any public class/enum declarations with proper indentation
            (r'(\s+)public\s+(class|enum)\s+(?!.*Application)', r'\1\2 '),
            # Fix standalone public classes/enums 
            (r'^public\s+(class|enum)\s+(?!.*Application)', r'\1 '),
            # Fix annotated public classes
            (r'(@\w+\s*\n\s*)public\s+(class|enum)\s+(?!.*Application)', r'\1\2 '),
        ]
        
        for pattern, replacement in patterns_to_fix:
            content = re.sub(pattern, replacement, content, flags=re.MULTILINE)
        
        # Only write if content changed
        if content != original_content:
            with open(file_path, 'w', encoding='utf-8') as f:
                f.write(content)
            print(f"Fixed: {file_path}")
            return True
        else:
            print(f"No changes needed: {file_path}")
            return False
            
    except Exception as e:
        print(f"Error processing {file_path}: {e}")
        return False

def main():
    """Main function to fix all Java files."""
    base_dir = r"d:\OneDrive\Desktop\Boozer_App_Main\nexus\src\main\java\com\boozer\nexus"
    
    # Expanded list of problematic files
    problem_files = [
        "learning/LearningComponents.java",
        "learning/models/LearningModels.java", 
        "bci/models/BCIModels.java",
        "agents/AgentComponents.java",
        "agents/models/AgentMemoryModels.java",
        "agents/models/AgentModels.java",
        "neuromorphic/network/NetworkModels.java",
        "neuromorphic/models/NeuromorphicModels.java",
        "neuromorphic/learning/NeuromorphicLearning.java",
        "consciousness/emergence/EmergenceModels.java",
        "ai/integration/models/AIModels.java",
        "consciousness/models/ConsciousnessModels.java",
        "multimodal/models/MultimodalModels.java"
    ]
    
    fixed_count = 0
    
    for file_path in problem_files:
        full_path = os.path.join(base_dir, file_path)
        if os.path.exists(full_path):
            if fix_public_classes_in_file(full_path):
                fixed_count += 1
        else:
            print(f"File not found: {full_path}")
    
    print(f"\nFixed {fixed_count} files")

if __name__ == "__main__":
    main()