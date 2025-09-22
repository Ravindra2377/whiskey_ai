"""
REST API Server for AI Orchestration
"""

from flask import Flask, request, jsonify
import asyncio
import threading
from app import AIOrchestrationApp

app = Flask(__name__)
ai_app = AIOrchestrationApp()

@app.route('/health', methods=['GET'])
def health_check():
    """Health check endpoint"""
    return jsonify({
        'status': 'healthy',
        'providers': list(ai_app.orchestrator.providers.keys())
    })

@app.route('/ai/process', methods=['POST'])
def process_request():
    """Process an AI request"""
    try:
        request_data = request.json
        # Run the async function in a new event loop
        loop = asyncio.new_event_loop()
        asyncio.set_event_loop(loop)
        response = loop.run_until_complete(ai_app.process_request(request_data))
        return jsonify(response)
    except Exception as e:
        return jsonify({'error': str(e)}), 500

@app.route('/ai/providers', methods=['GET'])
def list_providers():
    """List available providers"""
    providers = [provider.value for provider in ai_app.orchestrator.providers.keys()]
    return jsonify({'providers': providers})

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000, debug=True)