"""
Whiskey AI - Multi-Modal Enhancement Module
Adds computer vision, voice recognition, and multi-modal capabilities
"""

import cv2
import numpy as np
import speech_recognition as sr
import pyttsx3
import base64
import io
from PIL import Image
import pytesseract
from typing import Dict, List, Any, Optional, Tuple
import asyncio
import aiofiles
from datetime import datetime
import json

class MultiModalProcessor:
    """
    Advanced multi-modal processing for Whiskey AI
    Handles text, voice, images, and video inputs simultaneously
    """

    def __init__(self, config: Dict[str, Any]):
        self.config = config
        self.voice_recognizer = sr.Recognizer()
        self.tts_engine = pyttsx3.init()
        self.setup_voice_settings()

    def setup_voice_settings(self):
        """Configure voice recognition and text-to-speech"""
        # Configure TTS
        self.tts_engine.setProperty('rate', 150)
        self.tts_engine.setProperty('volume', 0.8)

        # Configure voice recognition
        self.voice_recognizer.energy_threshold = 4000
        self.voice_recognizer.dynamic_energy_threshold = True

    async def process_multi_modal_input(self, inputs: Dict[str, Any]) -> Dict[str, Any]:
        """Process multiple input modalities simultaneously"""
        results = {
            'text_analysis': None,
            'voice_analysis': None,
            'image_analysis': None,
            'video_analysis': None,
            'combined_insights': None
        }

        # Process text input
        if 'text' in inputs:
            results['text_analysis'] = await self.analyze_text(inputs['text'])

        # Process voice input
        if 'audio' in inputs:
            results['voice_analysis'] = await self.analyze_voice(inputs['audio'])

        # Process image input
        if 'image' in inputs:
            results['image_analysis'] = await self.analyze_image(inputs['image'])

        # Process video input
        if 'video' in inputs:
            results['video_analysis'] = await self.analyze_video(inputs['video'])

        # Combine insights from all modalities
        results['combined_insights'] = await self.synthesize_multi_modal_insights(results)

        return results

    async def analyze_voice(self, audio_data: bytes) -> Dict[str, Any]:
        """Analyze voice input for commands and sentiment"""
        try:
            # Convert audio to text
            audio_file = sr.AudioFile(io.BytesIO(audio_data))
            with audio_file as source:
                audio = self.voice_recognizer.record(source)

            text = self.voice_recognizer.recognize_google(audio)

            # Analyze voice characteristics
            voice_analysis = {
                'transcribed_text': text,
                'confidence': 0.95,  # Would come from actual recognition
                'sentiment': await self.analyze_voice_sentiment(audio_data),
                'urgency_level': await self.detect_urgency(text),
                'commands_detected': await self.extract_voice_commands(text)
            }

            return voice_analysis

        except Exception as e:
            return {'error': str(e), 'transcribed_text': ''}

    async def analyze_image(self, image_data: bytes) -> Dict[str, Any]:
        """Analyze images for code, diagrams, screenshots, etc."""
        try:
            # Convert bytes to OpenCV image
            nparr = np.frombuffer(image_data, np.uint8)
            image = cv2.imdecode(nparr, cv2.IMREAD_COLOR)

            analysis = {
                'type': await self.classify_image_type(image),
                'text_extracted': await self.extract_text_from_image(image),
                'code_detected': await self.detect_code_in_image(image),
                'ui_elements': await self.detect_ui_elements(image),
                'diagrams': await self.detect_diagrams(image),
                'quality_metrics': await self.analyze_image_quality(image)
            }

            return analysis

        except Exception as e:
            return {'error': str(e)}

    async def classify_image_type(self, image: np.ndarray) -> str:
        """Classify what type of image this is"""
        # Use basic computer vision techniques
        gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)

        # Detect if it's a screenshot (high contrast, rectangular elements)
        edges = cv2.Canny(gray, 50, 150)
        lines = cv2.HoughLines(edges, 1, np.pi/180, threshold=100)

        if lines is not None and len(lines) > 20:
            return "screenshot_or_ui"

        # Detect if it contains text (OCR confidence check)
        text = pytesseract.image_to_string(gray)
        if len(text.strip()) > 50:
            return "document_or_code"

        # Check for diagrams (circular/curved elements)
        circles = cv2.HoughCircles(gray, cv2.HOUGH_GRADIENT, 1, 20)
        if circles is not None:
            return "diagram_or_flowchart"

        return "general_image"

    async def extract_text_from_image(self, image: np.ndarray) -> Dict[str, Any]:
        """Extract text using OCR"""
        try:
            # Convert to PIL Image for pytesseract
            pil_image = Image.fromarray(cv2.cvtColor(image, cv2.COLOR_BGR2RGB))

            # Extract text with confidence
            data = pytesseract.image_to_data(pil_image, output_type=pytesseract.Output.DICT)

            extracted_text = []
            confidences = []

            for i, text in enumerate(data['text']):
                if int(data['conf'][i]) > 30:  # Only high-confidence text
                    extracted_text.append(text)
                    confidences.append(data['conf'][i])

            return {
                'text': ' '.join(extracted_text),
                'average_confidence': np.mean(confidences) if confidences else 0,
                'word_count': len(extracted_text)
            }

        except Exception as e:
            return {'error': str(e), 'text': ''}

    async def detect_code_in_image(self, image: np.ndarray) -> Dict[str, Any]:
        """Detect if image contains code and analyze it"""
        text_data = await self.extract_text_from_image(image)
        text = text_data.get('text', '')

        # Code detection patterns
        code_indicators = [
            'def ', 'function ', 'class ', 'import ', 'from ',
            '{', '}', '()', '=>', 'console.log', 'print(',
            'if (', 'for (', 'while (', 'async ', 'await '
        ]

        code_score = sum(1 for indicator in code_indicators if indicator in text)

        if code_score >= 3:
            return {
                'contains_code': True,
                'confidence': min(code_score / 10, 1.0),
                'detected_language': await self.detect_programming_language(text),
                'code_snippet': text,
                'analysis': await self.analyze_code_from_image(text)
            }

        return {'contains_code': False}

    async def detect_programming_language(self, code: str) -> str:
        """Detect programming language from code text"""
        language_indicators = {
            'python': ['def ', 'import ', 'print(', '__init__'],
            'javascript': ['function ', 'const ', 'let ', 'console.log'],
            'java': ['public class', 'System.out', 'public static'],
            'cpp': ['#include', 'cout <<', 'int main'],
            'html': ['<html>', '<div>', '<script>'],
            'css': ['{', '}', 'color:', 'font-size:']
        }

        scores = {}
        for lang, indicators in language_indicators.items():
            scores[lang] = sum(1 for indicator in indicators if indicator in code)

        return max(scores, key=scores.get) if max(scores.values()) > 0 else 'unknown'

    async def analyze_code_from_image(self, code: str) -> Dict[str, Any]:
        """Analyze code extracted from image"""
        return {
            'line_count': len(code.split('\n')),
            'complexity': 'medium',  # Would use actual analysis
            'potential_issues': [],  # Would scan for common issues
            'suggestions': ['Code extracted from image - verify accuracy']
        }

    async def detect_ui_elements(self, image: np.ndarray) -> List[Dict[str, Any]]:
        """Detect UI elements like buttons, forms, etc."""
        gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)

        # Detect rectangular UI elements
        edges = cv2.Canny(gray, 50, 150)
        contours, _ = cv2.findContours(edges, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)

        ui_elements = []
        for contour in contours:
            area = cv2.contourArea(contour)
            if area > 500:  # Filter small elements
                x, y, w, h = cv2.boundingRect(contour)
                aspect_ratio = w / h

                element_type = "unknown"
                if 0.8 <= aspect_ratio <= 1.2 and area < 5000:
                    element_type = "button"
                elif aspect_ratio > 3 and h < 50:
                    element_type = "input_field"
                elif area > 10000:
                    element_type = "container"

                ui_elements.append({
                    'type': element_type,
                    'position': {'x': int(x), 'y': int(y), 'width': int(w), 'height': int(h)},
                    'area': int(area),
                    'aspect_ratio': round(aspect_ratio, 2)
                })

        return ui_elements

    async def speak_response(self, text: str):
        """Convert text to speech"""
        try:
            self.tts_engine.say(text)
            self.tts_engine.runAndWait()
        except Exception as e:
            print(f"TTS Error: {e}")

    async def synthesize_multi_modal_insights(self, analysis_results: Dict) -> Dict[str, Any]:
        """Combine insights from all modalities"""
        insights = {
            'primary_intent': None,
            'confidence': 0,
            'recommended_actions': [],
            'context_understanding': {},
            'response_modality': 'text'  # Default response type
        }

        # Analyze combined context
        if analysis_results.get('voice_analysis') and analysis_results.get('image_analysis'):
            # User is showing code and speaking about it
            if (analysis_results['image_analysis'].get('code_detected', {}).get('contains_code') and 
                'review' in analysis_results['voice_analysis'].get('transcribed_text', '').lower()):

                insights['primary_intent'] = 'code_review_with_visual'
                insights['confidence'] = 0.9
                insights['recommended_actions'] = [
                    'analyze_code_from_image',
                    'provide_voice_feedback',
                    'suggest_improvements'
                ]
                insights['response_modality'] = 'voice_and_text'

        elif analysis_results.get('voice_analysis'):
            urgency = analysis_results['voice_analysis'].get('urgency_level', 'low')
            if urgency == 'high':
                insights['response_modality'] = 'voice'
                insights['recommended_actions'].append('immediate_voice_response')

        return insights

# Voice command processor for development tasks
class VoiceCommandProcessor:
    """Process voice commands for development tasks"""

    def __init__(self, nexus_core):
        self.nexus_core = nexus_core
        self.command_patterns = {
            'generate_code': ['generate', 'create', 'write code', 'make'],
            'review_code': ['review', 'check', 'analyze', 'look at'],
            'deploy': ['deploy', 'push', 'release', 'launch'],
            'test': ['test', 'run tests', 'check tests'],
            'debug': ['debug', 'fix bug', 'find error', 'troubleshoot'],
            'explain': ['explain', 'what does', 'how does', 'tell me about']
        }

    async def process_voice_command(self, voice_input: str, context: Dict = None) -> Dict:
        """Process natural language voice commands"""
        command_type = await self.classify_command(voice_input)

        if command_type == 'generate_code':
            return await self.handle_code_generation_command(voice_input, context)
        elif command_type == 'review_code':
            return await self.handle_code_review_command(voice_input, context)
        elif command_type == 'deploy':
            return await self.handle_deployment_command(voice_input, context)
        elif command_type == 'explain':
            return await self.handle_explanation_command(voice_input, context)
        else:
            return await self.handle_general_command(voice_input, context)

    async def classify_command(self, voice_input: str) -> str:
        """Classify the type of command from voice input"""
        voice_lower = voice_input.lower()

        for command_type, patterns in self.command_patterns.items():
            if any(pattern in voice_lower for pattern in patterns):
                return command_type

        return 'general'

    async def handle_code_generation_command(self, voice_input: str, context: Dict) -> Dict:
        """Handle code generation from voice"""
        # Extract programming language and requirements
        language_hints = {
            'python': ['python', 'py'],
            'javascript': ['javascript', 'js', 'node'],
            'react': ['react', 'component'],
            'html': ['html', 'webpage'],
            'css': ['css', 'style']
        }

        detected_language = 'python'  # default
        for lang, hints in language_hints.items():
            if any(hint in voice_input.lower() for hint in hints):
                detected_language = lang
                break

        # Create code generation task
        task_description = f"Generate {detected_language} code: {voice_input}"

        return {
            'task_type': 'code_generation',
            'language': detected_language,
            'description': task_description,
            'voice_response': True
        }