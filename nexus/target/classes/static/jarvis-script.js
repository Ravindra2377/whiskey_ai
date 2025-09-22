// WHISKEY-like JavaScript for Whiskey AI Interface

class WhiskeyAI {
    constructor() {
        this.websocket = null;
        this.voiceRecognition = null;
        this.isListening = false;
        this.charts = {};
        this.statusUpdateInterval = null;
        this.timeUpdateInterval = null;

        this.init();
    }

    async init() {
        console.log('🚀 Initializing WHISKEY AI Interface...');

        // Initialize components
        await this.initializeWebSocket();
        this.initializeVoiceRecognition();
        this.initializeCharts();
        this.setupEventListeners();
        this.startStatusUpdates();
        this.startTimeUpdates();

        // Welcome message
        this.speakResponse("Good evening. WHISKEY AI systems are online and ready for deployment.");

        console.log('✅ WHISKEY Interface fully operational');
    }

    // WebSocket Communication
    async initializeWebSocket() {
        try {
            const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
            const wsUrl = `${protocol}//${window.location.host}/ws`;

            this.websocket = new WebSocket(wsUrl);

            this.websocket.onopen = () => {
                console.log('🔗 WebSocket connection established');
                this.updateSystemStatus('online');
            };

            this.websocket.onmessage = (event) => {
                this.handleWebSocketMessage(JSON.parse(event.data));
            };

            this.websocket.onclose = () => {
                console.log('❌ WebSocket connection lost. Attempting reconnection...');
                this.updateSystemStatus('connecting');
                setTimeout(() => this.initializeWebSocket(), 5000);
            };

            this.websocket.onerror = (error) => {
                console.error('WebSocket error:', error);
                this.updateSystemStatus('error');
            };
        } catch (error) {
            console.error('Failed to initialize WebSocket:', error);
            // Fallback to HTTP polling
            this.initializeHttpPolling();
        }
    }

    handleWebSocketMessage(data) {
        switch (data.type) {
            case 'chat_message':
                this.displayMessage(data.message, data.user, false);
                break;
            case 'ai_response':
                this.displayMessage(data.message, 'WHISKEY AI', true);
                this.speakResponse(data.message);
                break;
            case 'status_update':
                this.updateSystemMetrics(data.metrics);
                break;
            case 'task_update':
                this.updateActiveTasks(data.tasks);
                break;
            case 'activity_log':
                this.addActivityItem(data.activity);
                break;
        }
    }

    // Voice Recognition
    initializeVoiceRecognition() {
        if ('webkitSpeechRecognition' in window) {
            this.voiceRecognition = new webkitSpeechRecognition();
            this.voiceRecognition.continuous = false;
            this.voiceRecognition.interimResults = false;
            this.voiceRecognition.lang = 'en-US';

            this.voiceRecognition.onstart = () => {
                console.log('🎤 Voice recognition started');
                this.setVoiceIndicator(true);
                document.getElementById('voiceBtn').classList.add('recording');
            };

            this.voiceRecognition.onresult = (event) => {
                const transcript = event.results[0][0].transcript;
                console.log('🗣️ Voice input:', transcript);
                document.getElementById('commandInput').value = transcript;
                this.sendCommand();
            };

            this.voiceRecognition.onend = () => {
                console.log('🎤 Voice recognition ended');
                this.setVoiceIndicator(false);
                document.getElementById('voiceBtn').classList.remove('recording');
                this.isListening = false;
            };

            this.voiceRecognition.onerror = (event) => {
                console.error('Voice recognition error:', event.error);
                this.setVoiceIndicator(false);
                document.getElementById('voiceBtn').classList.remove('recording');
                this.isListening = false;
            };
        } else {
            console.warn('Voice recognition not supported');
        }
    }

    // Text-to-Speech
    speakResponse(text, rate = 1.0, pitch = 1.0) {
        if ('speechSynthesis' in window) {
            const utterance = new SpeechSynthesisUtterance(text);
            utterance.rate = rate;
            utterance.pitch = pitch;
            utterance.voice = speechSynthesis.getVoices().find(voice => 
                voice.name.includes('Google') || voice.name.includes('Microsoft')
            );
            speechSynthesis.speak(utterance);
        }
    }

    // Chart Initialization
    initializeCharts() {
        // Response Time Chart
        const responseTimeCtx = document.getElementById('responseTimeChart');
        if (responseTimeCtx) {
            this.charts.responseTime = new Chart(responseTimeCtx, {
                type: 'line',
                data: {
                    labels: Array.from({length: 20}, (_, i) => ''),
                    datasets: [{
                        data: Array.from({length: 20}, () => Math.random() * 200 + 50),
                        borderColor: '#00d4ff',
                        backgroundColor: 'rgba(0, 212, 255, 0.1)',
                        borderWidth: 2,
                        fill: true,
                        tension: 0.4
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    plugins: {
                        legend: { display: false }
                    },
                    scales: {
                        x: { display: false },
                        y: { display: false }
                    },
                    elements: {
                        point: { radius: 0 }
                    }
                }
            });
        }

        // Success Rate Chart
        const successRateCtx = document.getElementById('successRateChart');
        if (successRateCtx) {
            this.charts.successRate = new Chart(successRateCtx, {
                type: 'line',
                data: {
                    labels: Array.from({length: 20}, (_, i) => ''),
                    datasets: [{
                        data: Array.from({length: 20}, () => Math.random() * 10 + 90),
                        borderColor: '#00ff88',
                        backgroundColor: 'rgba(0, 255, 136, 0.1)',
                        borderWidth: 2,
                        fill: true,
                        tension: 0.4
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    plugins: {
                        legend: { display: false }
                    },
                    scales: {
                        x: { display: false },
                        y: { display: false, min: 80, max: 100 }
                    },
                    elements: {
                        point: { radius: 0 }
                    }
                }
            });
        }
    }

    // Event Listeners
    setupEventListeners() {
        // Command input
        const commandInput = document.getElementById('commandInput');
        if (commandInput) {
            commandInput.addEventListener('keypress', (e) => {
                if (e.key === 'Enter') {
                    this.sendCommand();
                }
            });

            commandInput.addEventListener('input', (e) => {
                this.handleInputSuggestions(e.target.value);
            });
        }

        // Voice button
        const voiceBtn = document.getElementById('voiceBtn');
        if (voiceBtn) {
            voiceBtn.addEventListener('click', () => this.toggleVoiceInput());
        }

        // Send button
        const sendBtn = document.querySelector('.send-btn');
        if (sendBtn) {
            sendBtn.addEventListener('click', () => this.sendCommand());
        }

        // Modal events
        document.addEventListener('keydown', (e) => {
            if (e.key === 'Escape') {
                this.closeModal();
            }
        });

        // Capability items
        const capabilityItems = document.querySelectorAll('.capability-item');
        capabilityItems.forEach(item => {
            item.addEventListener('click', () => {
                this.showAdvancedFeatures();
            });
        });
    }

    // Command Processing
    async sendCommand() {
        const input = document.getElementById('commandInput');
        const command = input.value.trim();

        if (!command) return;

        // Display user message
        this.displayMessage(command, 'User', false);

        // Clear input
        input.value = '';

        // Show typing indicator
        this.showTypingIndicator();

        try {
            // Send to backend
            if (this.websocket && this.websocket.readyState === WebSocket.OPEN) {
                this.websocket.send(JSON.stringify({
                    type: 'command',
                    message: command,
                    timestamp: new Date().toISOString()
                }));
            } else {
                // Fallback to HTTP
                await this.sendHttpCommand(command);
            }

            // Add to activity log
            this.addActivityItem({
                icon: 'fas fa-terminal',
                text: `Command executed: ${command}`,
                time: 'Just now'
            });

        } catch (error) {
            console.error('Error sending command:', error);
            this.displayMessage('Sorry, there was an error processing your command.', 'WHISKEY AI', true);
        } finally {
            this.hideTypingIndicator();
        }
    }

    async sendHttpCommand(command) {
        const response = await fetch('/api/command', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ command })
        });

        const result = await response.json();

        if (result.success) {
            this.displayMessage(result.response, 'WHISKEY AI', true);
            this.speakResponse(result.response);
        } else {
            throw new Error(result.error || 'Command failed');
        }
    }

    // Message Display
    displayMessage(message, sender, isAI) {
        const messagesContainer = document.getElementById('chatMessages');

        const messageDiv = document.createElement('div');
        messageDiv.className = `message ${isAI ? 'ai-message' : 'user-message'}`;

        messageDiv.innerHTML = `
            <div class="message-avatar">
                <i class="${isAI ? 'fas fa-robot' : 'fas fa-user'}"></i>
            </div>
            <div class="message-content">
                <div class="message-header">
                    <span class="sender-name">${sender}</span>
                    <span class="message-time">${this.getCurrentTime()}</span>
                </div>
                <div class="message-text">${message}</div>
            </div>
        `;

        messagesContainer.appendChild(messageDiv);
        messagesContainer.scrollTop = messagesContainer.scrollHeight;

        // Add entrance animation
        messageDiv.style.opacity = '0';
        messageDiv.style.transform = 'translateY(20px)';
        requestAnimationFrame(() => {
            messageDiv.style.transition = 'all 0.3s ease';
            messageDiv.style.opacity = '1';
            messageDiv.style.transform = 'translateY(0)';
        });
    }

    showTypingIndicator() {
        const messagesContainer = document.getElementById('chatMessages');

        const typingDiv = document.createElement('div');
        typingDiv.id = 'typingIndicator';
        typingDiv.className = 'message ai-message';
        typingDiv.innerHTML = `
            <div class="message-avatar">
                <i class="fas fa-robot"></i>
            </div>
            <div class="message-content">
                <div class="message-header">
                    <span class="sender-name">WHISKEY AI</span>
                    <span class="message-time">${this.getCurrentTime()}</span>
                </div>
                <div class="message-text">
                    <div class="loading"></div> Processing your request...
                </div>
            </div>
        `;

        messagesContainer.appendChild(typingDiv);
        messagesContainer.scrollTop = messagesContainer.scrollHeight;
    }

    hideTypingIndicator() {
        const typingIndicator = document.getElementById('typingIndicator');
        if (typingIndicator) {
            typingIndicator.remove();
        }
    }

    // Voice Controls
    toggleVoiceInput() {
        if (!this.voiceRecognition) {
            this.displayMessage('Voice recognition is not supported in your browser.', 'WHISKEY AI', true);
            return;
        }

        if (this.isListening) {
            this.voiceRecognition.stop();
        } else {
            this.voiceRecognition.start();
            this.isListening = true;
        }
    }

    setVoiceIndicator(active) {
        const indicator = document.getElementById('voiceIndicator');
        if (indicator) {
            indicator.classList.toggle('active', active);
        }
    }

    // Status Updates
    startStatusUpdates() {
        this.statusUpdateInterval = setInterval(() => {
            this.updateSystemMetrics();
            this.updateCharts();
        }, 5000);
    }

    updateSystemMetrics(metrics = null) {
        if (!metrics) {
            // Generate mock metrics
            metrics = {
                cpu: Math.random() * 30 + 20,
                memory: Math.random() * 40 + 40,
                responseTime: Math.random() * 100 + 100,
                successRate: Math.random() * 5 + 95
            };
        }

        // Update CPU
        const cpuFill = document.querySelector('.stat-item:first-child .stat-fill');
        const cpuValue = document.querySelector('.stat-item:first-child .stat-value');
        if (cpuFill && cpuValue) {
            cpuFill.style.width = `${metrics.cpu}%`;
            cpuValue.textContent = `${Math.round(metrics.cpu)}%`;
        }

        // Update Memory
        const memFill = document.querySelector('.stat-item:last-child .stat-fill');
        const memValue = document.querySelector('.stat-item:last-child .stat-value');
        if (memFill && memValue) {
            memFill.style.width = `${metrics.memory}%`;
            memValue.textContent = `${Math.round(metrics.memory)}%`;
        }

        // Update metric displays
        const responseTimeValue = document.querySelector('.metric-item:first-child .metric-value');
        if (responseTimeValue) {
            responseTimeValue.textContent = `${Math.round(metrics.responseTime)}ms`;
        }

        const successRateValue = document.querySelector('.metric-item:last-child .metric-value');
        if (successRateValue) {
            successRateValue.textContent = `${metrics.successRate.toFixed(1)}%`;
        }
    }

    updateCharts() {
        // Update response time chart
        if (this.charts.responseTime) {
            const chart = this.charts.responseTime;
            chart.data.datasets[0].data.shift();
            chart.data.datasets[0].data.push(Math.random() * 200 + 50);
            chart.update('none');
        }

        // Update success rate chart
        if (this.charts.successRate) {
            const chart = this.charts.successRate;
            chart.data.datasets[0].data.shift();
            chart.data.datasets[0].data.push(Math.random() * 10 + 90);
            chart.update('none');
        }
    }

    updateSystemStatus(status) {
        const statusIndicator = document.querySelector('.status-indicator');
        const statusText = document.querySelector('.system-status span:last-child');

        if (statusIndicator && statusText) {
            statusIndicator.className = `status-indicator ${status}`;

            const statusMessages = {
                online: 'FULLY OPERATIONAL',
                connecting: 'CONNECTING...',
                error: 'CONNECTION ERROR'
            };

            statusText.textContent = statusMessages[status] || 'UNKNOWN';
        }
    }

    // Time Updates
    startTimeUpdates() {
        this.updateTime();
        this.timeUpdateInterval = setInterval(() => {
            this.updateTime();
        }, 1000);
    }

    updateTime() {
        const now = new Date();
        const timeElement = document.getElementById('current-time');
        const dateElement = document.getElementById('current-date');

        if (timeElement) {
            timeElement.textContent = now.toLocaleTimeString('en-US', {
                hour12: false,
                hour: '2-digit',
                minute: '2-digit',
                second: '2-digit'
            });
        }

        if (dateElement) {
            dateElement.textContent = now.toLocaleDateString('en-US', {
                weekday: 'long',
                year: 'numeric',
                month: 'long',
                day: 'numeric'
            });
        }
    }

    getCurrentTime() {
        return new Date().toLocaleTimeString('en-US', {
            hour12: true,
            hour: 'numeric',
            minute: '2-digit'
        });
    }

    // Activity Management
    addActivityItem(activity) {
        const activityList = document.getElementById('activityList');
        if (!activityList) return;

        const activityDiv = document.createElement('div');
        activityDiv.className = 'activity-item';
        activityDiv.innerHTML = `
            <div class="activity-icon">
                <i class="${activity.icon}"></i>
            </div>
            <div class="activity-info">
                <span class="activity-text">${activity.text}</span>
                <span class="activity-time">${activity.time}</span>
            </div>
        `;

        activityList.insertBefore(activityDiv, activityList.firstChild);

        // Keep only the last 5 activities
        while (activityList.children.length > 5) {
            activityList.removeChild(activityList.lastChild);
        }

        // Add entrance animation
        activityDiv.style.opacity = '0';
        activityDiv.style.transform = 'translateX(-20px)';
        requestAnimationFrame(() => {
            activityDiv.style.transition = 'all 0.3s ease';
            activityDiv.style.opacity = '1';
            activityDiv.style.transform = 'translateX(0)';
        });
    }

    updateActiveTasks(tasks) {
        const tasksContainer = document.getElementById('activeTasks');
        if (!tasksContainer) return;

        tasksContainer.innerHTML = '';

        tasks.forEach(task => {
            const taskDiv = document.createElement('div');
            taskDiv.className = 'task-item';
            taskDiv.innerHTML = `
                <div class="task-icon">
                    <i class="${task.icon}"></i>
                </div>
                <div class="task-info">
                    <span class="task-name">${task.name}</span>
                    <div class="task-progress">
                        <div class="progress-bar">
                            <div class="progress-fill" style="width: ${task.progress}%"></div>
                        </div>
                        <span class="progress-text">${task.progress}%</span>
                    </div>
                </div>
            `;
            tasksContainer.appendChild(taskDiv);
        });
    }

    // Input Suggestions
    handleInputSuggestions(value) {
        const suggestions = document.getElementById('inputSuggestions');
        if (!suggestions) return;

        // Hide suggestions if input is empty
        if (!value.trim()) {
            suggestions.style.display = 'flex';
            return;
        }

        // Hide suggestions when typing
        suggestions.style.display = 'none';
    }

    // Modal Management
    showAdvancedFeatures() {
        const modal = document.getElementById('modalOverlay');
        if (modal) {
            modal.classList.add('active');
        }
    }

    closeModal() {
        const modal = document.getElementById('modalOverlay');
        if (modal) {
            modal.classList.remove('active');
        }
    }

    // Quick Actions
    executeCommand(command) {
        document.getElementById('commandInput').value = command;
        this.sendCommand();
    }

    // Input Suggestions
    selectSuggestion(text) {
        document.getElementById('commandInput').value = text;
        this.sendCommand();
    }

    // Supermodel Personality Functions
    async setSupermodelPersonality(personality) {
        try {
            const response = await fetch('/api/whiskey/enhanced/supermodel-task', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    personality: personality,
                    task: `Activate ${personality} personality mode`,
                    type: 'CODE_MODIFICATION'
                })
            });

            const data = await response.json();
            
            if (data.status === 'SUCCESS') {
                // Display supermodel response
                this.displayMessage(data.supermodelResponsePattern, 'WHISKEY AI SUPERMODEL', true);
                this.speakResponse(data.supermodelResponsePattern);
                
                // Add to activity log
                this.addActivityItem({
                    icon: 'fas fa-theater-masks',
                    text: `Supermodel personality activated: ${personality}`,
                    time: 'Just now'
                });
            } else {
                throw new Error(data.message);
            }
        } catch (error) {
            console.error('Error setting supermodel personality:', error);
            this.displayMessage('Sorry, there was an error activating the supermodel personality.', 'WHISKEY AI', true);
        }
    }

    // Initialization Fallback
    initializeHttpPolling() {
        console.log('📡 Initializing HTTP polling fallback...');

        setInterval(async () => {
            try {
                const response = await fetch('/api/status');
                const data = await response.json();
                this.updateSystemMetrics(data.metrics);
            } catch (error) {
                console.error('HTTP polling error:', error);
            }
        }, 10000);
    }

    // AI Status Updates
    updateAIStatus(message) {
        const statusText = document.getElementById('aiStatusText');
        if (statusText) {
            statusText.textContent = message;

            // Animate text change
            statusText.style.opacity = '0.5';
            setTimeout(() => {
                statusText.style.opacity = '1';
            }, 200);
        }
    }

    // Cleanup
    destroy() {
        if (this.websocket) {
            this.websocket.close();
        }

        if (this.statusUpdateInterval) {
            clearInterval(this.statusUpdateInterval);
        }

        if (this.timeUpdateInterval) {
            clearInterval(this.timeUpdateInterval);
        }

        Object.values(this.charts).forEach(chart => chart.destroy());
    }
}

// Global Functions (for HTML onclick events)

let jarvis;

function toggleVoiceInput() {
    jarvis.toggleVoiceInput();
}

function sendCommand() {
    jarvis.sendCommand();
}

function executeCommand(command) {
    jarvis.executeCommand(command);
}

function selectSuggestion(text) {
    jarvis.selectSuggestion(text);
}

function closeModal() {
    jarvis.closeModal();
}

function setSupermodelPersonality(personality) {
    jarvis.setSupermodelPersonality(personality);
}

// Initialize WHISKEY when page loads

document.addEventListener('DOMContentLoaded', () => {
    jarvis = new WhiskeyAI();

    // Add some demo activities
    setTimeout(() => {
        jarvis.addActivityItem({
            icon: 'fas fa-code',
            text: 'Generated React component',
            time: '2m ago'
        });
    }, 2000);

    setTimeout(() => {
        jarvis.addActivityItem({
            icon: 'fas fa-check',
            text: 'Security scan completed',
            time: '5m ago'
        });
    }, 4000);
});

// Handle page unload

window.addEventListener('beforeunload', () => {
    if (jarvis) {
        jarvis.destroy();
    }
});

// Handle visibility change (pause/resume when tab is not visible)

document.addEventListener('visibilitychange', () => {
    if (jarvis) {
        if (document.hidden) {
            // Pause non-essential updates
            if (jarvis.statusUpdateInterval) {
                clearInterval(jarvis.statusUpdateInterval);
                jarvis.statusUpdateInterval = null;
            }
        } else {
            // Resume updates
            if (!jarvis.statusUpdateInterval) {
                jarvis.startStatusUpdates();
            }
        }
    }
});