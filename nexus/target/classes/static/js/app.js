// WHISKEY-like JavaScript for Whiskey AI Interface

class WhiskeyAI {
    constructor() {
        this.websocket = null;
        this.voiceRecognition = null;
        this.isListening = false;
        this.charts = {};
        this.statusUpdateInterval = null;
        this.timeUpdateInterval = null;
        this.currentMetrics = {
            cpu: 45,
            memory: 62,
            responseTime: 145,
            successRate: 96.8
        };
        
        this.init();
    }

    async init() {
        console.log('🚀 Initializing WHISKEY AI Interface...');
        
        // Initialize components
        this.initializeWebSocket();
        this.initializeVoiceRecognition();
        this.initializeCharts();
        this.setupEventListeners();
        this.startStatusUpdates();
        this.startTimeUpdates();
        
        // Welcome message
        this.displayMessage("Good evening. WHISKEY AI systems are online and ready for deployment. How may I assist you today?", "WHISKEY AI", true);
        
        console.log('✅ WHISKEY Interface fully operational');
    }

    // WebSocket Communication
    initializeWebSocket() {
        try {
            // In a real implementation, this would connect to your backend
            console.log('🔗 WebSocket connection established');
            this.updateSystemStatus('online');
            
            // Simulate receiving messages
            setInterval(() => {
                if (Math.random() > 0.7) {
                    const activities = [
                        { icon: 'fas fa-code', text: 'Code analysis completed', time: 'Just now' },
                        { icon: 'fas fa-check', text: 'Security scan passed', time: 'Just now' },
                        { icon: 'fas fa-rocket', text: 'Deployment successful', time: 'Just now' },
                        { icon: 'fas fa-bug', text: 'Bug fix implemented', time: 'Just now' }
                    ];
                    const randomActivity = activities[Math.floor(Math.random() * activities.length)];
                    this.addActivityItem(randomActivity);
                }
            }, 10000);
        } catch (error) {
            console.error('Failed to initialize WebSocket:', error);
            this.updateSystemStatus('error');
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
                document.getElementById('voice-btn').classList.add('active');
            };

            this.voiceRecognition.onresult = (event) => {
                const transcript = event.results[0][0].transcript;
                console.log('🗣️ Voice input:', transcript);
                document.getElementById('command-input').value = transcript;
                this.sendCommand();
            };

            this.voiceRecognition.onend = () => {
                console.log('🎤 Voice recognition ended');
                this.setVoiceIndicator(false);
                document.getElementById('voice-btn').classList.remove('active');
                this.isListening = false;
            };

            this.voiceRecognition.onerror = (event) => {
                console.error('Voice recognition error:', event.error);
                this.setVoiceIndicator(false);
                document.getElementById('voice-btn').classList.remove('active');
                this.isListening = false;
            };
        } else {
            console.warn('Voice recognition not supported in this browser');
        }
    }

    // Chart Initialization
    initializeCharts() {
        // Response Time Chart
        const responseTimeCtx = document.getElementById('performance-chart');
        if (responseTimeCtx) {
            this.charts.performance = new Chart(responseTimeCtx, {
                type: 'line',
                data: {
                    labels: Array.from({length: 20}, (_, i) => ''),
                    datasets: [{
                        label: 'Response Time (ms)',
                        data: Array.from({length: 20}, () => Math.random() * 200 + 50),
                        borderColor: '#00d4ff',
                        backgroundColor: 'rgba(0, 212, 255, 0.1)',
                        borderWidth: 2,
                        fill: true,
                        tension: 0.4,
                        pointRadius: 0
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    plugins: {
                        legend: { display: false }
                    },
                    scales: {
                        x: { 
                            display: false,
                            grid: { display: false }
                        },
                        y: { 
                            display: false,
                            min: 0,
                            max: 300,
                            grid: { display: false }
                        }
                    },
                    elements: {
                        point: { radius: 0 }
                    },
                    animation: { duration: 0 }
                }
            });
        }
    }

    // Event Listeners
    setupEventListeners() {
        // Command input
        const commandInput = document.getElementById('command-input');
        if (commandInput) {
            commandInput.addEventListener('keypress', (e) => {
                if (e.key === 'Enter') {
                    this.sendCommand();
                }
            });
        }

        // Voice button
        const voiceBtn = document.getElementById('voice-btn');
        if (voiceBtn) {
            voiceBtn.addEventListener('click', () => this.toggleVoiceInput());
        }

        // Send button
        const sendBtn = document.getElementById('send-btn');
        if (sendBtn) {
            sendBtn.addEventListener('click', () => this.sendCommand());
        }

        // Modal events
        const modalClose = document.getElementById('modal-close');
        const modalBackdrop = document.getElementById('modal-backdrop');
        const advancedFeaturesBtn = document.getElementById('advanced-features-btn');
        
        if (modalClose) {
            modalClose.addEventListener('click', () => this.closeModal());
        }
        
        if (modalBackdrop) {
            modalBackdrop.addEventListener('click', () => this.closeModal());
        }
        
        if (advancedFeaturesBtn) {
            advancedFeaturesBtn.addEventListener('click', () => this.showAdvancedFeatures());
        }

        // Quick action buttons
        const actionButtons = document.querySelectorAll('.action-btn');
        actionButtons.forEach(button => {
            button.addEventListener('click', (e) => {
                const command = e.currentTarget.getAttribute('data-command');
                this.executeCommand(command);
            });
        });

        // Suggestions
        const suggestions = document.querySelectorAll('.suggestion');
        suggestions.forEach(suggestion => {
            suggestion.addEventListener('click', (e) => {
                const command = e.currentTarget.getAttribute('data-command');
                document.getElementById('command-input').value = command;
                this.sendCommand();
            });
        });

        // Close modal on escape key
        document.addEventListener('keydown', (e) => {
            if (e.key === 'Escape') {
                this.closeModal();
            }
        });
    }

    // Command Processing
    sendCommand() {
        const input = document.getElementById('command-input');
        const command = input.value.trim();
        
        if (!command) return;

        // Display user message
        this.displayMessage(command, 'You', false);
        
        // Clear input
        input.value = '';

        // Show typing indicator
        this.showTypingIndicator();

        // Simulate AI processing
        setTimeout(() => {
            this.hideTypingIndicator();
            
            // Generate a response based on the command
            let response = "I've processed your request. ";
            
            if (command.toLowerCase().includes('hello') || command.toLowerCase().includes('hi')) {
                response = "Hello there! WHISKEY AI is at your service. How can I assist you today?";
            } else if (command.toLowerCase().includes('generate')) {
                response = "Code generation initiated. I'm creating the requested components now. This will be ready shortly.";
            } else if (command.toLowerCase().includes('deploy')) {
                response = "Deployment sequence initiated. I'm preparing your application for deployment. This should take just a moment.";
            } else if (command.toLowerCase().includes('test')) {
                response = "Running test suite now. I'll notify you when the results are available.";
            } else if (command.toLowerCase().includes('security')) {
                response = "Security scan in progress. I'm checking for vulnerabilities and potential issues.";
            } else if (command.toLowerCase().includes('status')) {
                response = "All systems are operational. Current metrics: CPU " + this.currentMetrics.cpu + "%, Memory " + this.currentMetrics.memory + "%. Response time is " + this.currentMetrics.responseTime + "ms with a success rate of " + this.currentMetrics.successRate + "%.";
            } else {
                const responses = [
                    "I've analyzed your request and taken appropriate action.",
                    "Task completed successfully. Is there anything else you'd like me to help with?",
                    "Processing finished. The results have been logged for your review.",
                    "Operation complete. I'm standing by for your next command.",
                    "Request fulfilled. Please let me know if you need any further assistance."
                ];
                response = responses[Math.floor(Math.random() * responses.length)];
            }
            
            // Display AI response
            this.displayMessage(response, 'WHISKEY AI', true);
            
            // Add to activity log
            this.addActivityItem({
                icon: 'fas fa-terminal',
                text: `Command executed: ${command.substring(0, 30)}${command.length > 30 ? '...' : ''}`,
                time: 'Just now'
            });
        }, 1000 + Math.random() * 2000);
    }

    // Message Display
    displayMessage(message, sender, isAI) {
        const chatArea = document.getElementById('chat-area');
        
        const messageDiv = document.createElement('div');
        messageDiv.className = `message ${isAI ? 'ai-message' : 'user-message'}`;
        
        messageDiv.innerHTML = `
            <div class="message-avatar">
                <i class="${isAI ? 'fas fa-robot' : 'fas fa-user'}"></i>
            </div>
            <div class="message-content">
                <p>${message}</p>
            </div>
        `;

        chatArea.appendChild(messageDiv);
        chatArea.scrollTop = chatArea.scrollHeight;
    }

    showTypingIndicator() {
        const chatArea = document.getElementById('chat-area');
        
        const typingDiv = document.createElement('div');
        typingDiv.id = 'typing-indicator';
        typingDiv.className = 'message ai-message';
        typingDiv.innerHTML = `
            <div class="message-avatar">
                <i class="fas fa-robot"></i>
            </div>
            <div class="message-content">
                <p><span class="typing-dot"></span><span class="typing-dot"></span><span class="typing-dot"></span></p>
            </div>
        `;

        chatArea.appendChild(typingDiv);
        chatArea.scrollTop = chatArea.scrollHeight;
    }

    hideTypingIndicator() {
        const typingIndicator = document.getElementById('typing-indicator');
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
        const indicator = document.getElementById('voice-indicator');
        if (indicator) {
            indicator.classList.toggle('active', active);
        }
    }

    // Status Updates
    startStatusUpdates() {
        this.statusUpdateInterval = setInterval(() => {
            this.updateSystemMetrics();
            this.updateCharts();
        }, 3000);
    }

    updateSystemMetrics() {
        // Generate realistic metrics with some variation
        this.currentMetrics.cpu = Math.max(10, Math.min(90, this.currentMetrics.cpu + (Math.random() * 10 - 5)));
        this.currentMetrics.memory = Math.max(20, Math.min(95, this.currentMetrics.memory + (Math.random() * 8 - 4)));
        this.currentMetrics.responseTime = Math.max(50, Math.min(500, this.currentMetrics.responseTime + (Math.random() * 40 - 20)));
        this.currentMetrics.successRate = Math.max(85, Math.min(99.9, this.currentMetrics.successRate + (Math.random() * 2 - 1)));

        // Update CPU
        const cpuFill = document.querySelector('.metric-bar:first-child .metric-fill');
        const cpuValue = document.getElementById('cpu-value');
        if (cpuFill && cpuValue) {
            cpuFill.style.width = `${this.currentMetrics.cpu}%`;
            cpuValue.textContent = `${Math.round(this.currentMetrics.cpu)}%`;
        }

        // Update Memory
        const memFill = document.querySelector('.metric-bar:last-child .metric-fill');
        const memValue = document.getElementById('memory-value');
        if (memFill && memValue) {
            memFill.style.width = `${this.currentMetrics.memory}%`;
            memValue.textContent = `${Math.round(this.currentMetrics.memory)}%`;
        }

        // Update metric displays
        const responseTimeValue = document.getElementById('response-time');
        if (responseTimeValue) {
            responseTimeValue.textContent = `${Math.round(this.currentMetrics.responseTime)}ms`;
        }

        const successRateValue = document.getElementById('success-rate');
        if (successRateValue) {
            successRateValue.textContent = `${this.currentMetrics.successRate.toFixed(1)}%`;
        }
    }

    updateCharts() {
        // Update performance chart
        if (this.charts.performance) {
            const chart = this.charts.performance;
            chart.data.datasets[0].data.shift();
            chart.data.datasets[0].data.push(this.currentMetrics.responseTime);
            chart.update();
        }
    }

    updateSystemStatus(status) {
        const statusIndicator = document.querySelector('.status-indicator');
        const statusText = document.querySelector('.system-status span:last-child');
        
        if (statusIndicator && statusText) {
            statusIndicator.className = `status-indicator ${status}`;
            
            const statusMessages = {
                online: 'ONLINE',
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
                weekday: 'short',
                year: 'numeric',
                month: 'short',
                day: 'numeric'
            }).toUpperCase();
        }
    }

    // Activity Management
    addActivityItem(activity) {
        const activityList = document.querySelector('.activity-list');
        if (!activityList) return;

        // Remove the advanced features button temporarily
        const advancedBtn = document.getElementById('advanced-features-btn');
        const advancedBtnClone = advancedBtn.cloneNode(true);
        
        // Remove the original button
        advancedBtn.remove();

        const activityDiv = document.createElement('div');
        activityDiv.className = 'activity-item';
        activityDiv.innerHTML = `
            <div class="activity-icon">
                <i class="${activity.icon}"></i>
            </div>
            <div class="activity-content">
                <div class="activity-text">${activity.text}</div>
                <div class="activity-time">${activity.time}</div>
            </div>
        `;

        // Add the activity item
        activityList.prepend(activityDiv);

        // Keep only the last 5 activities
        while (activityList.children.length > 5) {
            activityList.removeChild(activityList.children[4]); // Remove the fifth item (0-indexed)
        }

        // Re-add the advanced features button at the end
        activityList.parentElement.appendChild(advancedBtnClone);
        
        // Reattach event listener
        advancedBtnClone.addEventListener('click', () => this.showAdvancedFeatures());
        
        // Update the reference
        document.getElementById('advanced-features-btn').id = 'advanced-features-btn-old';
        advancedBtnClone.id = 'advanced-features-btn';
    }

    // Modal Management
    showAdvancedFeatures() {
        const modal = document.getElementById('advanced-modal');
        if (modal) {
            modal.classList.remove('hidden');
        }
    }

    closeModal() {
        const modal = document.getElementById('advanced-modal');
        if (modal) {
            modal.classList.add('hidden');
        }
    }

    // Quick Actions
    executeCommand(command) {
        document.getElementById('command-input').value = command;
        this.sendCommand();
    }

    // Cleanup
    destroy() {
        if (this.statusUpdateInterval) {
            clearInterval(this.statusUpdateInterval);
        }
        
        if (this.timeUpdateInterval) {
            clearInterval(this.timeUpdateInterval);
        }

        Object.values(this.charts).forEach(chart => {
            if (chart) chart.destroy();
        });
    }
}

// Global Functions (for HTML onclick events)
let jarvis;

// Initialize WHISKEY when page loads
document.addEventListener('DOMContentLoaded', () => {
    jarvis = new WhiskeyAI();
    
    // Matrix background effect
    initMatrix();
});

// Handle page unload
window.addEventListener('beforeunload', () => {
    if (jarvis) {
        jarvis.destroy();
    }
});

// Handle visibility change (pause/resume when tab is not visible)
document.addEventListener('visibilitychange', () => {
    // In a real implementation, you might want to pause/resume updates here
});

// Matrix background effect
function initMatrix() {
    const canvas = document.getElementById('matrix-canvas');
    const ctx = canvas.getContext('2d');
    
    canvas.width = window.innerWidth;
    canvas.height = window.innerHeight;
    
    const characters = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789$#@%&*';
    const fontSize = 14;
    const columns = canvas.width / fontSize;
    
    // Create drops for each column
    const drops = [];
    for (let i = 0; i < columns; i++) {
        drops[i] = Math.floor(Math.random() * canvas.height / fontSize);
    }
    
    function draw() {
        // Semi-transparent black to create fade effect
        ctx.fillStyle = 'rgba(0, 0, 0, 0.05)';
        ctx.fillRect(0, 0, canvas.width, canvas.height);
        
        ctx.fillStyle = '#00ccff';
        ctx.font = `${fontSize}px monospace`;
        
        for (let i = 0; i < drops.length; i++) {
            const text = characters.charAt(Math.floor(Math.random() * characters.length));
            ctx.fillText(text, i * fontSize, drops[i] * fontSize);
            
            // Reset drop if it reaches the bottom or randomly
            if (drops[i] * fontSize > canvas.height && Math.random() > 0.975) {
                drops[i] = 0;
            }
            
            drops[i]++;
        }
    }
    
    setInterval(draw, 50);
    
    // Handle window resize
    window.addEventListener('resize', () => {
        canvas.width = window.innerWidth;
        canvas.height = window.innerHeight;
    });
}