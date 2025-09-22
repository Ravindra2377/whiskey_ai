#!/usr/bin/env python3
"""
Command-Line Interface for AI Orchestration
"""

import sys
import json
import asyncio
from app import AIOrchestrationApp

async def main():
    if len(sys.argv) < 2:
        print("Usage: python cli.py '<json_request>'")
        print("   or: python cli.py --prompt 'Your prompt here' --task_type 'task_type'")
        sys.exit(1)
    
    try:
        # Check if using named arguments
        if '--prompt' in sys.argv:
            # Parse named arguments
            request_data = parse_named_arguments(sys.argv[1:])
        else:
            # Parse the JSON request from command line argument
            request_data = json.loads(sys.argv[1])
        
        # Initialize the AI orchestration app
        ai_app = AIOrchestrationApp()
        
        # Process the request
        response = await ai_app.process_request(request_data)
        
        # Output the response as JSON
        print(json.dumps(response, default=str))
        
    except Exception as e:
        error_response = {
            "error": str(e),
            "success": False
        }
        print(json.dumps(error_response))
        sys.exit(1)

def parse_named_arguments(args):
    """Parse named arguments into a request data dictionary"""
    request_data = {
        'id': f'req_{int(asyncio.get_event_loop().time())}',
        'context': {},
        'user_id': 'cli_user',
        'priority': 3,
        'complexity': 'moderate'
    }
    
    i = 0
    while i < len(args):
        if args[i] == '--prompt' and i + 1 < len(args):
            request_data['prompt'] = args[i + 1]
            i += 2
        elif args[i] == '--task_type' and i + 1 < len(args):
            request_data['task_type'] = args[i + 1]
            i += 2
        elif args[i] == '--complexity' and i + 1 < len(args):
            request_data['complexity'] = args[i + 1]
            i += 2
        elif args[i] == '--priority' and i + 1 < len(args):
            request_data['priority'] = int(args[i + 1])
            i += 2
        else:
            i += 1
    
    # Set default task type if not provided
    if 'task_type' not in request_data:
        request_data['task_type'] = 'general'
    
    # Set default prompt if not provided
    if 'prompt' not in request_data:
        request_data['prompt'] = 'Hello, what can you help me with?'
    
    return request_data

if __name__ == "__main__":
    asyncio.run(main())