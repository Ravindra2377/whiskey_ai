import requests
import json

# Test the supermodel personality endpoint
url = "http://localhost:8089/api/whiskey/enhanced/supermodel-task"

# Test data for sophisticated personality
payload = {
    "personality": "sophisticated",
    "task": "Explain how to implement a REST API in Spring Boot",
    "type": "CODE_MODIFICATION"
}

headers = {
    "Content-Type": "application/json"
}

try:
    response = requests.post(url, data=json.dumps(payload), headers=headers)
    print("Status Code:", response.status_code)
    print("Response:", response.json())
except Exception as e:
    print("Error:", str(e))

print("\n" + "="*50 + "\n")

# Test data for creative personality
payload2 = {
    "personality": "creative",
    "task": "Design a modern UI for a dashboard",
    "type": "CODE_MODIFICATION"
}

try:
    response = requests.post(url, data=json.dumps(payload2), headers=headers)
    print("Status Code:", response.status_code)
    print("Response:", response.json())
except Exception as e:
    print("Error:", str(e))