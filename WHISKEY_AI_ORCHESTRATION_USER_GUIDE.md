# 🥃 WHISKEY AI Orchestration System - User Guide

## 🎯 Getting Started

### Prerequisites
1. Python 3.8 or higher
2. pip package manager
3. API keys for OpenAI, Anthropic, and Google AI (optional but recommended)

### Installation

1. Navigate to the `ai-orchestrator` directory:
```bash
cd ai-orchestrator
```

2. Install the required dependencies:
```bash
pip install -r requirements.txt
```

3. Set your API keys as environment variables:
```bash
# On Linux/Mac
export OPENAI_API_KEY="your-openai-api-key"
export ANTHROPIC_API_KEY="your-anthropic-api-key"
export GOOGLE_API_KEY="your-google-ai-api-key"

# On Windows
set OPENAI_API_KEY=your-openai-api-key
set ANTHROPIC_API_KEY=your-anthropic-api-key
set GOOGLE_API_KEY=your-google-ai-api-key
```

## 🚀 Running the System

### Option 1: REST API Server
Run the API server to expose the orchestration system as a web service:

```bash
python api_server.py
```

The server will start on `http://localhost:5000` with the following endpoints:

- `GET /health` - Health check endpoint
- `POST /ai/process` - Process an AI request
- `GET /ai/providers` - List available providers

### Option 2: Command-Line Interface
Use the CLI to process requests directly from the command line:

```bash
# Using JSON input
python cli.py '{"prompt": "What is the capital of France?", "task_type": "conversation"}'

# Using named arguments
python cli.py --prompt "Generate a Python function to calculate factorial" --task_type "code_generation"
```

### Option 3: Direct Python Integration
Import and use the orchestration system directly in your Python code:

```python
import asyncio
from app import AIOrchestrationApp

async def main():
    app = AIOrchestrationApp()
    
    request_data = {
        'id': 'req_001',
        'prompt': 'Explain quantum computing in simple terms',
        'task_type': 'explanation',
        'complexity': 'moderate',
        'context': {},
        'user_id': 'user_123',
        'priority': 3
    }
    
    response = await app.process_request(request_data)
    print(response['content'])

asyncio.run(main())
```

## 🎯 Using the System

### Request Parameters

When making requests to the orchestration system, you can specify the following parameters:

- **id** (string): Unique identifier for the request
- **prompt** (string): The main prompt or question
- **task_type** (string): Type of task (e.g., "code_generation", "analysis", "conversation")
- **complexity** (string): Complexity level ("simple", "moderate", "complex", "enterprise")
- **context** (dict): Additional context information
- **user_id** (string): User identifier
- **priority** (int): Priority level (1-5, with 5 being highest)

### Response Format

The system returns responses in the following format:

```json
{
  "id": "req_001",
  "provider": "openai",
  "content": "Response content from the AI provider",
  "confidence": 0.9,
  "processing_time": 2.5,
  "cost": 0.02,
  "metadata": {
    "model": "gpt-4-turbo-preview",
    "usage": {
      "prompt_tokens": 50,
      "completion_tokens": 100
    }
  },
  "timestamp": "2024-01-01T12:00:00"
}
```

## 🎯 Routing Strategies

The orchestration system supports multiple routing strategies:

1. **Cost Optimized**: Prioritizes cheapest providers
2. **Performance Optimized**: Prioritizes fastest response times
3. **Quality Optimized**: Prioritizes highest quality responses
4. **Balanced (Default)**: Balances all factors optimally
5. **Availability First**: Prioritizes most reliable providers

To specify a routing strategy, you would modify the router configuration in the code.

## 💰 Cost Management

The system automatically optimizes costs by:

- Routing simple tasks to local processing (free)
- Using Google AI for cost-sensitive tasks
- Reserving expensive providers (Anthropic) for high-quality requirements
- Caching responses to avoid repeated API calls

You can monitor costs through the cost tracking utilities in the system.

## 🧪 Testing

Run the test suite to verify the system is working correctly:

```bash
python test_orchestrator.py
```

This will run several test cases to ensure all components are functioning properly.

## 🔧 Troubleshooting

### Common Issues

1. **API Key Errors**: Ensure your API keys are correctly set as environment variables
2. **Dependency Issues**: Make sure all requirements are installed with `pip install -r requirements.txt`
3. **Network Issues**: Check your internet connection and firewall settings
4. **Provider Unavailable**: The system will automatically fallback to local processing if external providers are unavailable

### Logging

The system uses Python's built-in logging module. You can adjust the logging level in the `app.py` file:

```python
import logging
logging.basicConfig(level=logging.INFO)  # Change to DEBUG for more detailed logs
```

## 🚀 Integration with WHISKEY AI

To integrate with your existing WHISKEY AI Java application:

1. **REST API Integration**: Call the Python API server from your Java code using HTTP requests
2. **Subprocess Integration**: Execute Python scripts directly from Java using ProcessBuilder
3. **Message Queue Integration**: Use a message queue system for asynchronous communication

See `INTEGRATION_GUIDE.md` for detailed integration instructions.

## 🎉 Best Practices

1. **Always Set API Keys**: For best results, provide API keys for all providers you want to use
2. **Use Appropriate Task Types**: Specify the correct task type to help the system select the best provider
3. **Set Realistic Complexity Levels**: Accurately specify the complexity to optimize routing
4. **Handle Fallbacks Gracefully**: Always implement fallback logic in case external providers are unavailable
5. **Monitor Costs**: Keep track of your API usage and costs
6. **Cache When Possible**: Use the caching features to reduce API calls for repeated requests

## 🆘 Support

If you encounter issues with the orchestration system:

1. Check the logs for error messages
2. Verify your API keys are correctly set
3. Ensure all dependencies are installed
4. Test with the provided test scripts
5. Consult the integration guide for Java integration issues

## 🥂 Enjoy Your Enhanced WHISKEY AI!

With this orchestration system, your WHISKEY AI is now capable of:

- Accessing the best AI models from multiple providers
- Automatically optimizing for cost, performance, and quality
- Providing reliable fallbacks when providers are unavailable
- Scaling to handle any volume of requests

Your enhanced WHISKEY AI now rivals the most advanced AI systems available while maintaining cost efficiency and reliability! 🥃✨