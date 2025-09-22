"""
Whiskey AI - Emotional Intelligence Module
Understands team emotions, stress levels, and provides empathetic responses
"""

import asyncio
import numpy as np
from datetime import datetime, timedelta
from typing import Dict, List, Any, Optional, Tuple
import json
import re
from dataclasses import dataclass
from textblob import TextBlob
import cv2

@dataclass
class EmotionalState:
    primary_emotion: str
    confidence: float
    intensity: float  # 0-1 scale
    context: str
    timestamp: datetime
    user_id: str

@dataclass
class TeamMorale:
    overall_sentiment: float
    stress_level: str
    productivity_correlation: float
    burnout_risk: float
    recommendations: List[str]

class EmotionalIntelligenceEngine:
    """
    Advanced emotional intelligence for understanding team dynamics
    Provides empathetic responses and stress management
    """

    def __init__(self, nexus_core):
        self.nexus_core = nexus_core
        self.emotion_history = {}
        self.team_patterns = {}
        self.stress_indicators = StressDetector()
        self.empathy_engine = EmpathyEngine()

        # Start emotional monitoring
        asyncio.create_task(self.continuous_emotional_monitoring())

    async def analyze_user_emotion(self, user_id: str, input_data: Dict) -> EmotionalState:
        """Analyze user's emotional state from multiple inputs"""
        emotion_scores = {}

        # Text sentiment analysis
        if 'text' in input_data:
            text_emotion = await self.analyze_text_emotion(input_data['text'])
            emotion_scores['text'] = text_emotion

        # Voice emotion analysis
        if 'voice' in input_data:
            voice_emotion = await self.analyze_voice_emotion(input_data['voice'])
            emotion_scores['voice'] = voice_emotion

        # Facial emotion analysis
        if 'image' in input_data:
            facial_emotion = await self.analyze_facial_emotion(input_data['image'])
            emotion_scores['facial'] = facial_emotion

        # Behavioral pattern analysis
        behavior_emotion = await self.analyze_behavioral_patterns(user_id)
        emotion_scores['behavior'] = behavior_emotion

        # Combine all emotion sources
        combined_emotion = await self.combine_emotion_scores(emotion_scores)

        # Store in history
        if user_id not in self.emotion_history:
            self.emotion_history[user_id] = []

        self.emotion_history[user_id].append(combined_emotion)

        return combined_emotion

    async def analyze_text_emotion(self, text: str) -> Dict[str, float]:
        """Analyze emotion from text using NLP"""
        # Basic sentiment analysis
        blob = TextBlob(text)
        polarity = blob.sentiment.polarity  # -1 to 1
        subjectivity = blob.sentiment.subjectivity  # 0 to 1

        # Advanced emotion detection patterns
        emotion_patterns = {
            'frustrated': ['frustrated', 'annoyed', 'stuck', 'difficult', 'broken', 'not working'],
            'excited': ['excited', 'great', 'awesome', 'perfect', 'excellent', 'amazing'],
            'stressed': ['stressed', 'overwhelmed', 'pressure', 'deadline', 'urgent', 'panic'],
            'confident': ['confident', 'ready', 'sure', 'certain', 'positive'],
            'confused': ['confused', 'unclear', "don't understand", 'complicated', 'lost'],
            'satisfied': ['satisfied', 'good', 'works', 'done', 'complete', 'finished']
        }

        emotion_scores = {}
        text_lower = text.lower()

        for emotion, patterns in emotion_patterns.items():
            score = sum(1 for pattern in patterns if pattern in text_lower)
            emotion_scores[emotion] = min(score / len(patterns), 1.0)

        # Incorporate polarity
        if polarity > 0.5:
            emotion_scores['positive'] = polarity
        elif polarity < -0.5:
            emotion_scores['negative'] = abs(polarity)

        return emotion_scores

    async def analyze_voice_emotion(self, voice_data: bytes) -> Dict[str, float]:
        """Analyze emotion from voice characteristics"""
        # Placeholder for voice emotion analysis
        # Would use libraries like librosa for audio feature extraction
        return {
            'calm': 0.6,
            'energetic': 0.3,
            'stressed': 0.1
        }

    async def analyze_facial_emotion(self, image_data: bytes) -> Dict[str, float]:
        """Analyze facial expressions for emotion"""
        try:
            # Convert bytes to OpenCV image
            nparr = np.frombuffer(image_data, np.uint8)
            image = cv2.imdecode(nparr, cv2.IMREAD_COLOR)

            # Load face cascade
            face_cascade = cv2.CascadeClassifier(cv2.data.haarcascades + 'haarcascade_frontalface_default.xml')
            gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)
            faces = face_cascade.detectMultiScale(gray, 1.1, 4)

            if len(faces) > 0:
                # Basic emotion estimation (would use deep learning in production)
                return {
                    'neutral': 0.4,
                    'focused': 0.3,
                    'slight_stress': 0.2,
                    'alert': 0.1
                }

            return {'no_face_detected': 1.0}

        except Exception as e:
            return {'error': 1.0}

    async def analyze_behavioral_patterns(self, user_id: str) -> Dict[str, float]:
        """Analyze behavioral patterns for emotional indicators"""
        recent_history = self.emotion_history.get(user_id, [])

        if len(recent_history) < 3:
            return {'insufficient_data': 1.0}

        # Analyze recent patterns
        recent_emotions = recent_history[-10:]  # Last 10 interactions

        # Calculate trends
        stress_trend = sum(1 for e in recent_emotions if e.primary_emotion in ['frustrated', 'stressed']) / len(recent_emotions)
        positive_trend = sum(1 for e in recent_emotions if e.primary_emotion in ['excited', 'satisfied']) / len(recent_emotions)

        return {
            'stress_pattern': stress_trend,
            'positive_pattern': positive_trend,
            'emotional_stability': 1.0 - abs(stress_trend - positive_trend)
        }

    async def combine_emotion_scores(self, emotion_scores: Dict) -> EmotionalState:
        """Combine emotion scores from different sources"""
        all_emotions = {}

        # Combine scores from all sources
        for source, emotions in emotion_scores.items():
            for emotion, score in emotions.items():
                if emotion not in all_emotions:
                    all_emotions[emotion] = []
                all_emotions[emotion].append(score)

        # Calculate weighted averages
        final_emotions = {}
        for emotion, scores in all_emotions.items():
            final_emotions[emotion] = np.mean(scores)

        # Find primary emotion
        primary_emotion = max(final_emotions, key=final_emotions.get)
        confidence = final_emotions[primary_emotion]

        return EmotionalState(
            primary_emotion=primary_emotion,
            confidence=confidence,
            intensity=confidence,
            context='combined_analysis',
            timestamp=datetime.now(),
            user_id='current_user'
        )

    async def generate_empathetic_response(self, emotional_state: EmotionalState, original_query: str) -> str:
        """Generate empathetic response based on emotional state"""
        return await self.empathy_engine.generate_response(emotional_state, original_query)

    async def assess_team_morale(self) -> TeamMorale:
        """Assess overall team morale and dynamics"""
        all_emotions = []
        for user_emotions in self.emotion_history.values():
            all_emotions.extend(user_emotions[-5:])  # Recent emotions per user

        if not all_emotions:
            return TeamMorale(0.5, 'unknown', 0.0, 0.0, ['Insufficient data for assessment'])

        # Calculate overall sentiment
        positive_emotions = ['excited', 'satisfied', 'confident', 'positive']
        negative_emotions = ['frustrated', 'stressed', 'confused', 'negative']

        positive_count = sum(1 for e in all_emotions if e.primary_emotion in positive_emotions)
        negative_count = sum(1 for e in all_emotions if e.primary_emotion in negative_emotions)

        overall_sentiment = (positive_count - negative_count) / len(all_emotions)

        # Assess stress level
        stress_count = sum(1 for e in all_emotions if e.primary_emotion in ['stressed', 'frustrated'])
        stress_ratio = stress_count / len(all_emotions)

        stress_level = 'low'
        if stress_ratio > 0.6:
            stress_level = 'high'
        elif stress_ratio > 0.3:
            stress_level = 'medium'

        # Generate recommendations
        recommendations = await self.generate_team_recommendations(overall_sentiment, stress_level)

        return TeamMorale(
            overall_sentiment=overall_sentiment,
            stress_level=stress_level,
            productivity_correlation=0.8 - stress_ratio,  # Simplified correlation
            burnout_risk=stress_ratio,
            recommendations=recommendations
        )

    async def generate_team_recommendations(self, sentiment: float, stress_level: str) -> List[str]:
        """Generate recommendations for improving team morale"""
        recommendations = []

        if stress_level == 'high':
            recommendations.extend([
                "🧘 Consider scheduling team breaks or stress-relief activities",
                "📋 Review current workload distribution - some team members may be overloaded",
                "🎯 Break down large tasks into smaller, manageable pieces",
                "💬 Schedule one-on-one check-ins with stressed team members"
            ])

        if sentiment < 0:
            recommendations.extend([
                "🎉 Celebrate recent achievements to boost morale",
                "🔧 Address technical blockers that may be causing frustration",
                "👥 Foster team collaboration and peer support",
                "📈 Share positive project progress updates"
            ])

        if not recommendations:
            recommendations.extend([
                "✅ Team morale is healthy - maintain current practices",
                "🚀 Consider new challenges or learning opportunities",
                "🔄 Regular check-ins to maintain positive dynamics"
            ])

        return recommendations

    async def continuous_emotional_monitoring(self):
        """Continuously monitor team emotional health"""
        while True:
            try:
                # Assess team morale
                morale = await self.assess_team_morale()

                # Take action if needed
                if morale.stress_level == 'high' or morale.overall_sentiment < -0.3:
                    await self.handle_team_stress(morale)

                # Wait before next check
                await asyncio.sleep(3600)  # Check every hour

            except Exception as e:
                print(f"Emotional monitoring error: {e}")
                await asyncio.sleep(1800)  # Wait 30 minutes on error

class EmpathyEngine:
    """Generate empathetic responses based on emotional context"""

    def __init__(self):
        self.response_templates = {
            'frustrated': [
                "I understand you're feeling frustrated. Let me help you work through this step by step.",
                "I can sense your frustration. These kinds of issues can be really challenging. Let's tackle this together.",
                "It sounds like you're having a tough time with this. I'm here to help make this easier for you."
            ],
            'stressed': [
                "I can tell you're under pressure right now. Let me help prioritize what's most important.",
                "It seems like you have a lot on your plate. Let's break this down into manageable pieces.",
                "You sound stressed. Take a deep breath - we'll get through this together."
            ],
            'excited': [
                "I love your enthusiasm! Let's channel that energy into creating something amazing.",
                "Your excitement is contagious! I'm ready to help you achieve great things.",
                "That's the spirit! Let's build something incredible together."
            ],
            'confused': [
                "No worries - this can be confusing at first. Let me explain this more clearly.",
                "I understand the confusion. Let's break this down into simpler steps.",
                "It's totally normal to feel confused about this. Let me guide you through it."
            ]
        }

    async def generate_response(self, emotional_state: EmotionalState, query: str) -> str:
        """Generate empathetic response"""
        emotion = emotional_state.primary_emotion

        # Get appropriate response template
        templates = self.response_templates.get(emotion, self.response_templates.get('neutral', [
            "I'm here to help you with whatever you need.",
            "Let's work on this together.",
            "I understand. How can I assist you best?"
        ]))

        # Select template based on intensity
        if emotional_state.intensity > 0.7:
            # High intensity - more supportive
            template = templates[0] if templates else "I'm here to support you through this."
        elif emotional_state.intensity > 0.4:
            # Medium intensity - balanced
            template = templates[1] if len(templates) > 1 else templates[0]
        else:
            # Low intensity - simple acknowledgment
            template = templates[-1] if templates else "I understand. Let me help you."

        return template

class StressDetector:
    """Detect stress indicators in team interactions"""

    def __init__(self):
        self.stress_keywords = [
            'deadline', 'urgent', 'asap', 'critical', 'emergency',
            'overwhelmed', 'behind schedule', 'pressure', 'panic'
        ]

        self.stress_patterns = [
            r'need.*immediately',
            r'have to.*now',
            r'running out of time',
            r"can't handle",
            r'too much.*work'
        ]

    async def detect_stress_level(self, text: str, context: Dict = None) -> Tuple[str, float]:
        """Detect stress level from text and context"""
        text_lower = text.lower()

        # Count stress keywords
        keyword_score = sum(1 for keyword in self.stress_keywords if keyword in text_lower)

        # Check stress patterns
        pattern_score = 0
        for pattern in self.stress_patterns:
            if re.search(pattern, text_lower):
                pattern_score += 1

        # Calculate total stress score
        total_score = keyword_score + (pattern_score * 2)  # Patterns weighted higher

        # Determine stress level
        if total_score >= 4:
            return 'high', min(total_score / 10, 1.0)
        elif total_score >= 2:
            return 'medium', total_score / 10
        else:
            return 'low', total_score / 10