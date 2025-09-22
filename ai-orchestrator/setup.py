#!/usr/bin/env python3
"""
Setup Script for AI Orchestration System
"""

import os
import sys
import subprocess
import venv

def create_virtual_environment():
    """Create a virtual environment for the AI orchestration system"""
    print("Creating virtual environment...")
    venv.create('venv', with_pip=True)
    print("Virtual environment created successfully!")

def install_dependencies():
    """Install required dependencies"""
    print("Installing dependencies...")
    
    # Determine the path to the Python executable in the virtual environment
    if os.name == 'nt':  # Windows
        python_executable = os.path.join('venv', 'Scripts', 'python.exe')
    else:  # Unix/Linux/Mac
        python_executable = os.path.join('venv', 'bin', 'python')
    
    # Install requirements
    subprocess.check_call([python_executable, '-m', 'pip', 'install', '-r', 'requirements.txt'])
    print("Dependencies installed successfully!")

def setup_environment():
    """Setup the AI orchestration system"""
    print("🚀 Setting up WHISKEY AI Orchestration System")
    print("=" * 50)
    
    try:
        # Create virtual environment
        create_virtual_environment()
        
        # Install dependencies
        install_dependencies()
        
        print("\n✅ Setup completed successfully!")
        print("\nNext steps:")
        print("1. Set your API keys as environment variables:")
        print("   export OPENAI_API_KEY='your-openai-api-key'")
        print("   export ANTHROPIC_API_KEY='your-anthropic-api-key'")
        print("   export GOOGLE_API_KEY='your-google-ai-api-key'")
        print("\n2. Run the API server:")
        print("   python api_server.py")
        print("\n3. Or run the command-line interface:")
        print("   python cli.py --prompt 'Your question here'")
        
    except Exception as e:
        print(f"❌ Setup failed with error: {e}")
        sys.exit(1)

if __name__ == "__main__":
    setup_environment()