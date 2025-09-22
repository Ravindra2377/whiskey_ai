# WHISKEY AI Enhancement Suite Installation Guide

## Overview
This guide explains how to install and set up the advanced enhancement suite for WHISKEY AI.

## Prerequisites
- Python 3.8 or higher
- pip package manager
- Git (for cloning repositories if needed)

## Installation Steps

### 1. Install Required Python Packages
Run the following command to install all required dependencies:

```bash
pip install -r requirements_advanced.txt
```

### 2. Install System Dependencies

#### For Computer Vision Capabilities (OpenCV)
On Windows:
```bash
pip install opencv-python
```

On macOS:
```bash
brew install opencv
pip install opencv-python
```

On Ubuntu/Debian:
```bash
sudo apt-get update
sudo apt-get install python3-opencv
pip install opencv-python
```

#### For OCR Capabilities (Tesseract)
On Windows:
1. Download Tesseract installer from: https://github.com/UB-Mannheim/tesseract/wiki
2. Install Tesseract
3. Add Tesseract to your PATH environment variable
4. Install Python wrapper:
```bash
pip install pytesseract
```

On macOS:
```bash
brew install tesseract
pip install pytesseract
```

On Ubuntu/Debian:
```bash
sudo apt-get install tesseract-ocr
pip install pytesseract
```

#### For Audio Processing
On Windows:
```bash
pip install pyaudio
```

On macOS:
```bash
brew install portaudio
pip install pyaudio
```

On Ubuntu/Debian:
```bash
sudo apt-get install portaudio19-dev
pip install pyaudio
```

### 3. Install Scientific Computing Libraries
```bash
pip install numpy pandas scikit-learn
```

### 4. Install Security Libraries
```bash
pip install cryptography pyjwt
```

### 5. Install System Monitoring Libraries
```bash
pip install psutil
```

### 6. Install Natural Language Processing Libraries
```bash
pip install textblob
python -m textblob.download_corpora
```

## Alternative Installation Method

If you prefer to install all dependencies at once:

```bash
pip install opencv-python pytesseract pyaudio numpy pandas scikit-learn cryptography pyjwt psutil textblob speechrecognition pyttsx3
python -m textblob.download_corpora
```

## Verification

After installation, you can verify that all dependencies are correctly installed by running:

```bash
python test_enhancements.py
```

## Troubleshooting

### Common Issues

1. **Import Errors**: If you see "No module named 'X'" errors, make sure you've installed all dependencies.

2. **Tesseract Not Found**: If pytesseract can't find Tesseract, you may need to specify the path manually:
   ```python
   import pytesseract
   pytesseract.pytesseract.tesseract_cmd = r'C:\Program Files\Tesseract-OCR\tesseract.exe'  # Windows example
   ```

3. **OpenCV Installation Issues**: On some systems, you might need to install additional dependencies:
   ```bash
   pip install opencv-contrib-python
   ```

4. **Permission Errors**: On Linux/macOS, you might need to use `sudo` for system package installations.

### Testing Installation

Run the test script to verify all modules work correctly:
```bash
python test_enhancements.py
```

All tests should pass if the installation was successful.

## Usage

After successful installation, you can use the enhancement modules in your WHISKEY AI system by importing them:

```python
from enhancement_integration import EnhancementIntegrator

# Initialize the enhancement integrator
enhancer = EnhancementIntegrator(nexus_core)

# Process enhanced requests
result = await enhancer.process_enhanced_request({
    'text': 'Your request here',
    'user_id': 'user_123'
})
```

## Updating Dependencies

To update all dependencies to their latest versions:

```bash
pip install --upgrade -r requirements_advanced.txt
```

## Support

If you encounter any issues during installation, please check:
1. Python version (3.8+ required)
2. pip version (upgrade with `pip install --upgrade pip`)
3. System requirements for each dependency
4. Firewall/antivirus software that might block installations

For further assistance, consult the documentation for each individual library or reach out to the NEXUS AI support team.