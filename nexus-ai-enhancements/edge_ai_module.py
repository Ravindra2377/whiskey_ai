"""
Whiskey AI - Edge AI Module  
Enables local processing and real-time decision making
"""

import asyncio
import psutil
import threading
import queue
from datetime import datetime, timedelta
from typing import Dict, List, Any, Optional
import json
import numpy as np
from dataclasses import dataclass

@dataclass
class EdgeProcessingRequest:
    id: str
    data: Any
    processing_type: str
    priority: int  # 1-5, 5 being highest
    timestamp: datetime
    requires_real_time: bool

@dataclass
class EdgeDevice:
    id: str
    name: str
    cpu_cores: int
    memory_gb: float
    gpu_available: bool
    current_load: float
    processing_queue: queue.Queue
    status: str  # active, busy, offline

class EdgeAIProcessor:
    """
    Local AI processing for real-time decisions
    Reduces latency and enables offline operation
    """

    def __init__(self, nexus_core):
        self.nexus_core = nexus_core
        self.local_models = {}
        self.processing_queue = queue.PriorityQueue()
        self.edge_devices = {}
        self.real_time_monitor = RealTimeMonitor()

        # Initialize local processing
        self.initialize_edge_processing()

        # Start processing threads
        self.start_edge_processing_threads()

    def initialize_edge_processing(self):
        """Initialize edge AI processing capabilities"""
        # Detect available hardware
        self.detect_hardware_capabilities()

        # Load lightweight models for local processing
        self.load_local_models()

        # Setup real-time processing pipeline
        self.setup_realtime_pipeline()

    def detect_hardware_capabilities(self):
        """Detect available hardware for edge processing"""
        main_device = EdgeDevice(
            id='main_device',
            name='Primary Processing Unit',
            cpu_cores=psutil.cpu_count(),
            memory_gb=psutil.virtual_memory().total / (1024**3),
            gpu_available=self.check_gpu_availability(),
            current_load=0.0,
            processing_queue=queue.Queue(),
            status='active'
        )

        self.edge_devices['main'] = main_device

        print(f"Edge AI initialized with {main_device.cpu_cores} CPU cores, "
              f"{main_device.memory_gb:.1f}GB RAM, GPU: {main_device.gpu_available}")

    def check_gpu_availability(self) -> bool:
        """Check if GPU is available for processing"""
        try:
            import torch
            return torch.cuda.is_available()
        except ImportError:
            return False

    def load_local_models(self):
        """Load lightweight models for local processing"""
        # Lightweight models for different tasks
        self.local_models = {
            'code_analysis': LocalCodeAnalyzer(),
            'sentiment_analysis': LocalSentimentAnalyzer(),
            'anomaly_detection': LocalAnomalyDetector(),
            'performance_prediction': LocalPerformancePredictor(),
            'security_scanner': LocalSecurityScanner()
        }

        print(f"Loaded {len(self.local_models)} local AI models")

    def setup_realtime_pipeline(self):
        """Setup real-time processing pipeline"""
        self.realtime_processors = {
            'system_monitoring': SystemMonitorProcessor(),
            'code_completion': CodeCompletionProcessor(),
            'error_detection': ErrorDetectionProcessor(),
            'performance_analysis': PerformanceAnalysisProcessor()
        }

    def start_edge_processing_threads(self):
        """Start background threads for edge processing"""
        # Main processing thread
        self.processing_thread = threading.Thread(target=self.processing_worker, daemon=True)
        self.processing_thread.start()

        # Real-time monitoring thread
        self.monitoring_thread = threading.Thread(target=self.real_time_monitoring, daemon=True)
        self.monitoring_thread.start()

        print("Edge AI processing threads started")

    async def process_locally(self, request: EdgeProcessingRequest) -> Dict[str, Any]:
        """Process request using local edge AI"""
        start_time = datetime.now()

        try:
            # Route to appropriate local model
            if request.processing_type in self.local_models:
                model = self.local_models[request.processing_type]
                result = await model.process(request.data)
            else:
                result = {'error': f'No local model for {request.processing_type}'}

            processing_time = (datetime.now() - start_time).total_seconds()

            return {
                'result': result,
                'processing_time_ms': processing_time * 1000,
                'processed_locally': True,
                'device_id': 'main'
            }

        except Exception as e:
            return {
                'error': str(e),
                'processing_time_ms': (datetime.now() - start_time).total_seconds() * 1000,
                'processed_locally': False
            }

    def processing_worker(self):
        """Worker thread for processing edge requests"""
        while True:
            try:
                # Get next request (blocking)
                priority, request = self.processing_queue.get(timeout=1)

                # Process request
                loop = asyncio.new_event_loop()
                asyncio.set_event_loop(loop)
                result = loop.run_until_complete(self.process_locally(request))

                # Handle result
                self.handle_processing_result(request, result)

            except queue.Empty:
                continue
            except Exception as e:
                print(f"Edge processing error: {e}")

    def real_time_monitoring(self):
        """Real-time system monitoring thread"""
        while True:
            try:
                # Monitor system performance
                cpu_percent = psutil.cpu_percent(interval=1)
                memory_percent = psutil.virtual_memory().percent

                # Update device status
                main_device = self.edge_devices['main']
                main_device.current_load = max(cpu_percent, memory_percent) / 100

                # Trigger real-time responses if needed
                if cpu_percent > 90 or memory_percent > 90:
                    self.handle_high_resource_usage()

                # Check for anomalies
                self.real_time_monitor.check_anomalies({
                    'cpu_usage': cpu_percent,
                    'memory_usage': memory_percent,
                    'timestamp': datetime.now()
                })

            except Exception as e:
                print(f"Real-time monitoring error: {e}")

            # Wait before next check
            threading.Event().wait(5)  # Check every 5 seconds

    def handle_high_resource_usage(self):
        """Handle high resource usage situations"""
        # Reduce processing load
        main_device = self.edge_devices['main']
        if main_device.current_load > 0.9:
            main_device.status = 'busy'

            # Defer non-critical tasks
            self.defer_non_critical_tasks()

            print("🚨 High resource usage detected - reducing edge processing load")

    def defer_non_critical_tasks(self):
        """Defer non-critical tasks when resources are constrained"""
        # Move low-priority tasks back to cloud processing
        deferred_count = 0
        temp_queue = queue.PriorityQueue()

        while not self.processing_queue.empty():
            try:
                priority, request = self.processing_queue.get_nowait()
                if priority >= 3:  # Keep high-priority tasks
                    temp_queue.put((priority, request))
                else:
                    # Defer low-priority task
                    deferred_count += 1
            except queue.Empty:
                break

        # Restore high-priority tasks
        self.processing_queue = temp_queue

        if deferred_count > 0:
            print(f"Deferred {deferred_count} low-priority tasks due to high load")

    async def optimize_edge_performance(self):
        """Optimize edge processing performance"""
        # Analyze processing patterns
        performance_metrics = await self.analyze_processing_patterns()

        # Adjust model parameters
        await self.adjust_model_parameters(performance_metrics)

        # Optimize resource allocation
        await self.optimize_resource_allocation(performance_metrics)

        return performance_metrics

    async def analyze_processing_patterns(self) -> Dict[str, Any]:
        """Analyze edge processing patterns for optimization"""
        main_device = self.edge_devices['main']

        return {
            'average_load': main_device.current_load,
            'queue_length': self.processing_queue.qsize(),
            'processing_efficiency': self.calculate_processing_efficiency(),
            'bottlenecks': await self.identify_bottlenecks()
        }

    def calculate_processing_efficiency(self) -> float:
        """Calculate overall processing efficiency"""
        # Simple efficiency metric based on load vs throughput
        main_device = self.edge_devices['main']

        if main_device.current_load == 0:
            return 1.0

        # Higher efficiency when processing more with less load
        return min(1.0, (1.0 - main_device.current_load) * 1.2)

class LocalCodeAnalyzer:
    """Lightweight local code analysis"""

    async def process(self, code_data: str) -> Dict[str, Any]:
        """Analyze code locally"""
        analysis = {
            'line_count': len(code_data.split('\n')),
            'complexity_score': self.calculate_complexity(code_data),
            'potential_issues': self.find_potential_issues(code_data),
            'optimization_suggestions': self.suggest_optimizations(code_data)
        }
        return analysis

    def calculate_complexity(self, code: str) -> float:
        """Calculate code complexity score"""
        # Simple complexity calculation
        complexity_indicators = ['if ', 'for ', 'while ', 'def ', 'class ']
        score = sum(code.lower().count(indicator) for indicator in complexity_indicators)
        return min(score / 10, 1.0)  # Normalize to 0-1

    def find_potential_issues(self, code: str) -> List[str]:
        """Find potential issues in code"""
        issues = []

        # Check for common issues
        if 'print(' in code and 'debug' not in code.lower():
            issues.append('Debug print statements detected')

        if 'TODO' in code or 'FIXME' in code:
            issues.append('TODO/FIXME comments found')

        if code.count('\n') > 100 and 'def ' not in code:
            issues.append('Large function detected - consider breaking down')

        return issues

    def suggest_optimizations(self, code: str) -> List[str]:
        """Suggest code optimizations"""
        suggestions = []

        if 'for i in range(len(' in code:
            suggestions.append('Use enumerate() instead of range(len())')

        if '+ str(' in code:
            suggestions.append('Consider using f-strings for string formatting')

        return suggestions

class LocalSentimentAnalyzer:
    """Lightweight local sentiment analysis"""

    def __init__(self):
        # Simple sentiment keywords
        self.positive_words = ['good', 'great', 'excellent', 'perfect', 'awesome', 'love', 'like']
        self.negative_words = ['bad', 'terrible', 'awful', 'hate', 'dislike', 'broken', 'error']

    async def process(self, text_data: str) -> Dict[str, Any]:
        """Analyze sentiment locally"""
        text_lower = text_data.lower()

        positive_count = sum(1 for word in self.positive_words if word in text_lower)
        negative_count = sum(1 for word in self.negative_words if word in text_lower)

        if positive_count > negative_count:
            sentiment = 'positive'
            score = min(positive_count / (positive_count + negative_count + 1), 1.0)
        elif negative_count > positive_count:
            sentiment = 'negative'
            score = min(negative_count / (positive_count + negative_count + 1), 1.0)
        else:
            sentiment = 'neutral'
            score = 0.5

        return {
            'sentiment': sentiment,
            'confidence': score,
            'positive_indicators': positive_count,
            'negative_indicators': negative_count
        }

class RealTimeMonitor:
    """Real-time system monitoring and anomaly detection"""

    def __init__(self):
        self.baseline_metrics = {}
        self.anomaly_threshold = 2.0  # Standard deviations
        self.history = []

    def check_anomalies(self, metrics: Dict[str, float]):
        """Check for anomalies in real-time"""
        self.history.append(metrics)

        # Keep only recent history
        if len(self.history) > 100:
            self.history = self.history[-100:]

        # Check each metric for anomalies
        for metric_name, value in metrics.items():
            if metric_name == 'timestamp':
                continue

            if self.is_anomaly(metric_name, value):
                self.handle_anomaly(metric_name, value, metrics['timestamp'])

    def is_anomaly(self, metric_name: str, value: float) -> bool:
        """Check if a metric value is anomalous"""
        if len(self.history) < 10:
            return False  # Need more data

        # Calculate baseline statistics
        recent_values = [h[metric_name] for h in self.history[-10:] if metric_name in h]

        if len(recent_values) < 5:
            return False

        mean = np.mean(recent_values)
        std = np.std(recent_values)

        if std == 0:
            return False

        # Check if current value is anomalous
        z_score = abs(value - mean) / std
        return z_score > self.anomaly_threshold

    def handle_anomaly(self, metric_name: str, value: float, timestamp: datetime):
        """Handle detected anomaly"""
        print(f"🚨 Anomaly detected: {metric_name} = {value:.2f} at {timestamp}")

        # Take appropriate action based on metric
        if metric_name == 'cpu_usage' and value > 95:
            self.handle_high_cpu()
        elif metric_name == 'memory_usage' and value > 95:
            self.handle_high_memory()

    def handle_high_cpu(self):
        """Handle high CPU usage"""
        print("Taking action for high CPU usage")
        # Could trigger process prioritization, load balancing, etc.

    def handle_high_memory(self):
        """Handle high memory usage"""
        print("Taking action for high memory usage")
        # Could trigger garbage collection, cache clearing, etc.

# Additional processor classes would be implemented similarly
class SystemMonitorProcessor:
    async def process(self, data):
        return {"status": "monitoring"}

class CodeCompletionProcessor:
    async def process(self, data):
        return {"completion": "suggested_code"}

class ErrorDetectionProcessor:
    async def process(self, data):
        return {"errors_found": 0}

class PerformanceAnalysisProcessor:
    async def process(self, data):
        return {"performance_score": 0.85}

class LocalAnomalyDetector:
    async def process(self, data):
        return {"anomalies": []}

class LocalPerformancePredictor:
    async def process(self, data):
        return {"predicted_performance": "good"}

class LocalSecurityScanner:
    async def process(self, data):
        return {"security_issues": []}