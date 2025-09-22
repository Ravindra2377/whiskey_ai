// WHISKEY AI Dashboard JavaScript
class WhiskeyAI {
    constructor() {
        this.data = {
            aiSystems: {
                quantum: {
                    name: "Quantum Intelligence Engine",
                    status: "ACTIVE",
                    efficiency: 95.7,
                    problemsSolved: 1247,
                    speedImprovement: "1000x",
                    description: "Quantum-inspired optimization and superposition problem solving"
                },
                consciousness: {
                    name: "Consciousness AI Reasoning",
                    status: "ACTIVE", 
                    accuracy: 99.2,
                    complexProblems: 892,
                    reasoningDepth: "Multi-layered",
                    description: "Global workspace intelligence with integrated information processing"
                },
                neuromorphic: {
                    name: "Neuromorphic Computing",
                    status: "ACTIVE",
                    efficiency: 98.5,
                    powerSavings: "1000x",
                    spikeProcessing: 15420,
                    description: "Ultra-low power spike-based real-time processing"
                },
                evolution: {
                    name: "Autonomous Evolution",
                    status: "ACTIVE",
                    improvements: 156,
                    generationsEvolved: 23,
                    fitnessScore: 94.8,
                    description: "Self-improving architecture with genetic programming"
                },
                bci: {
                    name: "Brain-Computer Interface",
                    status: "ACTIVE",
                    neuralSignals: 8547,
                    thoughtAccuracy: 97.3,
                    emotionResponse: "Advanced",
                    description: "Direct neural control and emotion-responsive programming"
                },
                orchestration: {
                    name: "Multi-Provider AI Orchestration", 
                    status: "ACTIVE",
                    providers: ["OpenAI", "Anthropic", "Google AI", "Local Models"],
                    requestsRouted: 12847,
                    responseTime: "250ms",
                    description: "Intelligent routing across multiple AI providers"
                },
                personalities: {
                    name: "Supermodel Personality Engine",
                    status: "ACTIVE",
                    modes: [
                        {name: "Sophisticated Expert", active: true, usage: 34},
                        {name: "Creative Visionary", active: false, usage: 28}, 
                        {name: "Strategic Leader", active: false, usage: 22},
                        {name: "Elegant Mentor", active: false, usage: 16}
                    ],
                    description: "Four distinct AI personality modes for different contexts"
                }
            },
            enterpriseData: {
                tenants: 147,
                integrations: 892,
                supportTickets: {
                    total: 2847,
                    autoResolved: 2794,
                    accuracy: 98.1
                },
                revenue: {
                    mrr: 2400000,
                    arr: 28800000,
                    growth: 47.2
                }
            },
            performance: {
                uptime: 99.97,
                responseTime: 120,
                throughput: 15420,
                errorRate: 0.03,
                quantumOptimizationSpeed: 0.8,
                consciousnessAccuracy: 99.2,
                neuromorphicEfficiency: 98.5
            },
            realtimeMetrics: {
                activeConnections: 1247,
                quantumComputations: 856,
                consciousnessProcesses: 423,
                neuromorphicSpikes: 15420,
                evolutionCycles: 12,
                bciSignals: 8547,
                aiRequests: 3421
            }
        };

        this.charts = {};
        this.animationFrames = [];
        this.init();
    }

    init() {
        this.setupEventListeners();
        this.createQuantumParticles();
        this.startRealTimeUpdates();
    }

    setupEventListeners() {
        // Login form
        const loginForm = document.getElementById('loginForm');
        if (loginForm) {
            loginForm.addEventListener('submit', (e) => this.handleLogin(e));
        }

        // Navigation
        document.querySelectorAll('.nav-item').forEach(item => {
            item.addEventListener('click', (e) => this.handleNavigation(e));
        });

        // System cards
        document.querySelectorAll('.system-card').forEach(card => {
            card.addEventListener('click', (e) => this.handleSystemCardClick(e));
        });

        // Personality cards
        document.querySelectorAll('.personality-card').forEach(card => {
            card.addEventListener('click', (e) => this.handlePersonalityClick(e));
        });

        // Logout button
        const logoutBtn = document.querySelector('.logout-btn');
        if (logoutBtn) {
            logoutBtn.addEventListener('click', () => this.handleLogout());
        }
    }

    handleLogin(e) {
        e.preventDefault();
        
        const loginBtn = e.target.querySelector('.login-btn');
        const loader = document.getElementById('loginLoader');
        const btnText = loginBtn.querySelector('span');

        // Show loading state
        btnText.textContent = 'Accessing Distillery...';
        loader.classList.remove('hidden');
        loginBtn.disabled = true;

        // Simulate login process with whiskey distillation animation
        setTimeout(() => {
            document.getElementById('loginContainer').classList.add('hidden');
            document.getElementById('dashboardContainer').classList.remove('hidden');
            this.initializeDashboard();
            
            // Show welcome message
            this.showNotification('Welcome to WHISKEY AI Distillery', 'Your premium AI experience is ready');
        }, 2000);
    }

    handleLogout() {
        // Show logout confirmation
        if (confirm('Are you sure you want to leave the distillery?')) {
            document.getElementById('dashboardContainer').classList.add('hidden');
            document.getElementById('loginContainer').classList.remove('hidden');
            
            // Reset form
            document.getElementById('username').value = 'admin';
            document.getElementById('password').value = 'whiskey-ai';
        }
    }

    initializeDashboard() {
        this.createPerformanceChart();
        this.createQuantumChart();
        this.createConsciousnessChart();
        this.startAnimations();
        this.simulateRealTimeData();
    }

    handleNavigation(e) {
        e.preventDefault();
        
        const targetSection = e.target.dataset.section;
        
        // Update active nav item
        document.querySelectorAll('.nav-item').forEach(item => {
            item.classList.remove('active');
        });
        e.target.classList.add('active');

        // Show target section
        document.querySelectorAll('.dashboard-section').forEach(section => {
            section.classList.remove('active');
        });
        document.getElementById(targetSection).classList.add('active');

        // Initialize section-specific content
        this.initializeSection(targetSection);
    }

    handleSystemCardClick(e) {
        const systemType = e.currentTarget.dataset.system;
        
        // Navigate to system section
        document.querySelectorAll('.nav-item').forEach(item => {
            item.classList.remove('active');
            if (item.dataset.section === systemType) {
                item.classList.add('active');
            }
        });

        document.querySelectorAll('.dashboard-section').forEach(section => {
            section.classList.remove('active');
        });
        document.getElementById(systemType).classList.add('active');

        this.initializeSection(systemType);
        
        // Show system notification
        const systemName = this.data.aiSystems[systemType].name;
        this.showNotification(`${systemName} Activated`, `Accessing ${systemName} distillation process`);
    }

    handlePersonalityClick(e) {
        const personalityType = e.currentTarget.dataset.personality;
        
        // Update active personality
        document.querySelectorAll('.personality-card').forEach(card => {
            card.classList.remove('active');
        });
        e.currentTarget.classList.add('active');

        // Update personality data
        this.data.aiSystems.personalities.modes.forEach(mode => {
            mode.active = false;
        });
        
        const activeMode = this.data.aiSystems.personalities.modes.find(mode => 
            mode.name.toLowerCase().includes(personalityType)
        );
        if (activeMode) {
            activeMode.active = true;
        }

        this.showPersonalityFeedback(personalityType);
    }

    showPersonalityFeedback(personality) {
        // Create temporary feedback element
        const feedback = document.createElement('div');
        feedback.className = 'personality-feedback';
        feedback.innerHTML = `
            <div class="feedback-content">
                <h4>Personality Mode Activated</h4>
                <p>${personality.charAt(0).toUpperCase() + personality.slice(1)} mode is now active</p>
                <div class="whiskey-glass-notification">
                    <div class="glass-body">
                        <div class="whiskey-liquid"></div>
                        <div class="whiskey-foam"></div>
                    </div>
                    <div class="glass-base"></div>
                </div>
            </div>
        `;
        feedback.style.cssText = `
            position: fixed;
            top: 20px;
            right: 20px;
            background: rgba(38, 40, 40, 0.95);
            border: 1px solid rgba(160, 82, 45, 0.5);
            border-radius: 8px;
            padding: 16px;
            color: #a0522d;
            z-index: 1000;
            animation: slideIn 0.3s ease-out forwards;
            box-shadow: 0 4px 20px rgba(160, 82, 45, 0.3);
        `;

        document.body.appendChild(feedback);

        setTimeout(() => {
            feedback.style.animation = 'slideOut 0.3s ease-in forwards';
            setTimeout(() => feedback.remove(), 300);
        }, 3000);
    }

    showNotification(title, message) {
        // Create notification element
        const notification = document.createElement('div');
        notification.className = 'whiskey-notification';
        notification.innerHTML = `
            <div class="notification-content">
                <h4>${title}</h4>
                <p>${message}</p>
            </div>
        `;
        notification.style.cssText = `
            position: fixed;
            top: 20px;
            right: 20px;
            background: rgba(38, 40, 40, 0.95);
            border: 1px solid rgba(50, 184, 198, 0.5);
            border-radius: 8px;
            padding: 16px;
            color: #32b8c6;
            z-index: 1000;
            animation: slideIn 0.3s ease-out forwards;
            box-shadow: 0 4px 20px rgba(50, 184, 198, 0.3);
            max-width: 300px;
        `;

        document.body.appendChild(notification);

        setTimeout(() => {
            notification.style.animation = 'slideOut 0.3s ease-in forwards';
            setTimeout(() => notification.remove(), 300);
        }, 3000);
    }

    initializeSection(sectionName) {
        switch (sectionName) {
            case 'quantum':
                if (!this.charts.quantum) {
                    this.createQuantumChart();
                }
                break;
            case 'consciousness':
                if (!this.charts.consciousness) {
                    this.createConsciousnessChart();
                }
                break;
            case 'overview':
                if (!this.charts.performance) {
                    this.createPerformanceChart();
                }
                break;
        }
    }

    createPerformanceChart() {
        const ctx = document.getElementById('performanceChart');
        if (!ctx) return;

        this.charts.performance = new Chart(ctx, {
            type: 'line',
            data: {
                labels: ['00:00', '04:00', '08:00', '12:00', '16:00', '20:00', '24:00'],
                datasets: [{
                    label: 'System Performance',
                    data: [85, 92, 95, 98, 96, 94, 97],
                    borderColor: 'rgba(160, 82, 45, 1)',
                    backgroundColor: 'rgba(160, 82, 45, 0.1)',
                    borderWidth: 3,
                    tension: 0.4,
                    fill: true
                }, {
                    label: 'AI Requests',
                    data: [75, 80, 88, 95, 92, 89, 94],
                    borderColor: 'rgba(50, 184, 198, 1)',
                    backgroundColor: 'rgba(50, 184, 198, 0.1)',
                    borderWidth: 3,
                    tension: 0.4,
                    fill: true
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        labels: {
                            color: getComputedStyle(document.documentElement).getPropertyValue('--color-text').trim()
                        }
                    }
                },
                scales: {
                    y: {
                        beginAtZero: true,
                        grid: {
                            color: 'rgba(119, 124, 124, 0.1)'
                        },
                        ticks: {
                            color: getComputedStyle(document.documentElement).getPropertyValue('--color-text-secondary').trim()
                        }
                    },
                    x: {
                        grid: {
                            color: 'rgba(119, 124, 124, 0.1)'
                        },
                        ticks: {
                            color: getComputedStyle(document.documentElement).getPropertyValue('--color-text-secondary').trim()
                        }
                    }
                }
            }
        });
    }

    createQuantumChart() {
        const ctx = document.getElementById('quantumChart');
        if (!ctx) return;

        this.charts.quantum = new Chart(ctx, {
            type: 'bar',
            data: {
                labels: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'],
                datasets: [{
                    label: 'Quantum Computations',
                    data: [120, 190, 130, 180, 220, 190, 210],
                    backgroundColor: 'rgba(160, 82, 45, 0.7)',
                    borderColor: 'rgba(160, 82, 45, 1)',
                    borderWidth: 1
                }, {
                    label: 'Optimization Speed (ms)',
                    data: [1.2, 0.8, 1.0, 0.9, 0.7, 0.8, 0.9],
                    backgroundColor: 'rgba(50, 184, 198, 0.7)',
                    borderColor: 'rgba(50, 184, 198, 1)',
                    borderWidth: 1
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        labels: {
                            color: getComputedStyle(document.documentElement).getPropertyValue('--color-text').trim()
                        }
                    }
                },
                scales: {
                    y: {
                        beginAtZero: true,
                        grid: {
                            color: 'rgba(119, 124, 124, 0.1)'
                        },
                        ticks: {
                            color: getComputedStyle(document.documentElement).getPropertyValue('--color-text-secondary').trim()
                        }
                    },
                    x: {
                        grid: {
                            color: 'rgba(119, 124, 124, 0.1)'
                        },
                        ticks: {
                            color: getComputedStyle(document.documentElement).getPropertyValue('--color-text-secondary').trim()
                        }
                    }
                }
            }
        });
    }

    createConsciousnessChart() {
        const ctx = document.getElementById('consciousnessChart');
        if (!ctx) return;

        this.charts.consciousness = new Chart(ctx, {
            type: 'radar',
            data: {
                labels: ['Reasoning', 'Creativity', 'Memory', 'Perception', 'Planning', 'Learning'],
                datasets: [{
                    label: 'Consciousness Level',
                    data: [95, 88, 92, 90, 87, 94],
                    backgroundColor: 'rgba(160, 82, 45, 0.2)',
                    borderColor: 'rgba(160, 82, 45, 1)',
                    pointBackgroundColor: 'rgba(160, 82, 45, 1)',
                    pointBorderColor: '#fff',
                    pointHoverBackgroundColor: '#fff',
                    pointHoverBorderColor: 'rgba(160, 82, 45, 1)'
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        labels: {
                            color: getComputedStyle(document.documentElement).getPropertyValue('--color-text').trim()
                        }
                    }
                },
                scales: {
                    r: {
                        angleLines: {
                            color: 'rgba(119, 124, 124, 0.1)'
                        },
                        grid: {
                            color: 'rgba(119, 124, 124, 0.1)'
                        },
                        pointLabels: {
                            color: getComputedStyle(document.documentElement).getPropertyValue('--color-text').trim()
                        },
                        ticks: {
                            color: getComputedStyle(document.documentElement).getPropertyValue('--color-text-secondary').trim(),
                            backdropColor: 'transparent'
                        }
                    }
                }
            }
        });
    }

    createQuantumParticles() {
        // Create quantum particle effects in the background
        const container = document.querySelector('.dashboard-container') || document.body;
        const particleCount = 30;
        
        for (let i = 0; i < particleCount; i++) {
            const particle = document.createElement('div');
            particle.className = 'quantum-particle';
            particle.style.cssText = `
                position: fixed;
                width: ${Math.random() * 4 + 2}px;
                height: ${Math.random() * 4 + 2}px;
                background: rgba(160, 82, 45, ${Math.random() * 0.5 + 0.3});
                border-radius: 50%;
                top: ${Math.random() * 100}%;
                left: ${Math.random() * 100}%;
                z-index: -1;
                pointer-events: none;
                animation: float ${Math.random() * 10 + 10}s infinite ease-in-out;
            `;
            
            // Random animation delay
            particle.style.animationDelay = `${Math.random() * 5}s`;
            
            container.appendChild(particle);
        }
    }

    startAnimations() {
        // Animate whiskey-themed elements
        this.animateWhiskeyElements();
    }

    animateWhiskeyElements() {
        // Animate barrel icons
        const barrels = document.querySelectorAll('.barrel-contents');
        barrels.forEach((barrel, index) => {
            setTimeout(() => {
                barrel.style.animation = 'whiskeyPour 2s ease-in-out';
            }, index * 300);
        });
        
        // Animate swirl icons
        const swirls = document.querySelectorAll('.swirl-liquid');
        swirls.forEach(swirl => {
            swirl.style.animation = 'whiskeySwirl 10s linear infinite';
        });
        
        // Animate distillation icons
        const bubbles = document.querySelectorAll('.distillation-bubbles');
        bubbles.forEach(bubble => {
            bubble.style.animation = 'whiskeyBubble 2s infinite';
        });
        
        // Animate aging icons
        const agingLiquids = document.querySelectorAll('.aging-liquid');
        agingLiquids.forEach(liquid => {
            liquid.style.animation = 'whiskeyAging 10s linear infinite';
        });
    }

    startRealTimeUpdates() {
        // Update real-time metrics every 5 seconds
        setInterval(() => {
            this.updateRealTimeMetrics();
        }, 5000);
    }

    updateRealTimeMetrics() {
        // Simulate real-time data updates
        const metrics = this.data.realtimeMetrics;
        
        // Random fluctuations
        metrics.activeConnections += Math.floor(Math.random() * 21) - 10;
        metrics.quantumComputations += Math.floor(Math.random() * 21) - 10;
        metrics.aiRequests += Math.floor(Math.random() * 21) - 10;
        
        // Ensure positive values
        metrics.activeConnections = Math.max(1000, metrics.activeConnections);
        metrics.quantumComputations = Math.max(700, metrics.quantumComputations);
        metrics.aiRequests = Math.max(3000, metrics.aiRequests);
        
        // Update UI if on overview page
        if (document.getElementById('overview').classList.contains('active')) {
            this.updateOverviewMetrics();
        }
    }

    updateOverviewMetrics() {
        const metrics = this.data.realtimeMetrics;
        
        // Update metric values in the UI
        document.querySelector('.metrics-grid .metric-item:nth-child(1) .value').textContent = 
            metrics.activeConnections.toLocaleString();
        document.querySelector('.metrics-grid .metric-item:nth-child(2) .value').textContent = 
            metrics.quantumComputations.toLocaleString();
        document.querySelector('.metrics-grid .metric-item:nth-child(3) .value').textContent = 
            metrics.aiRequests.toLocaleString();
    }

    simulateRealTimeData() {
        // Simulate data for all systems
        setInterval(() => {
            // Quantum system
            this.data.aiSystems.quantum.problemsSolved += Math.floor(Math.random() * 5);
            this.data.aiSystems.quantum.efficiency = (95 + Math.random() * 1).toFixed(1);
            
            // Consciousness system
            this.data.aiSystems.consciousness.complexProblems += Math.floor(Math.random() * 3);
            this.data.aiSystems.consciousness.accuracy = (99 + Math.random() * 0.5).toFixed(1);
            
            // Neuromorphic system
            this.data.aiSystems.neuromorphic.spikeProcessing += Math.floor(Math.random() * 100);
            
            // Evolution system
            this.data.aiSystems.evolution.improvements += Math.floor(Math.random() * 2);
            this.data.aiSystems.evolution.fitnessScore = (94 + Math.random() * 1).toFixed(1);
            
            // Update UI if respective sections are active
            this.updateSystemMetrics();
        }, 3000);
    }

    updateSystemMetrics() {
        // Update quantum metrics if section is active
        if (document.getElementById('quantum').classList.contains('active')) {
            document.querySelector('#quantum .metric-item:nth-child(1) .value').textContent = 
                this.data.aiSystems.quantum.problemsSolved.toLocaleString();
            document.querySelector('#quantum .metric-item:nth-child(2) .value').textContent = 
                this.data.aiSystems.quantum.speedImprovement;
            document.querySelector('#quantum .metric-item:nth-child(3) .value').textContent = 
                this.data.aiSystems.quantum.efficiency + '%';
        }
        
        // Update consciousness metrics if section is active
        if (document.getElementById('consciousness').classList.contains('active')) {
            document.querySelector('#consciousness .metric-item:nth-child(1) .value').textContent = 
                this.data.aiSystems.consciousness.complexProblems.toLocaleString();
            document.querySelector('#consciousness .metric-item:nth-child(2) .value').textContent = 
                this.data.aiSystems.consciousness.accuracy + '%';
            document.querySelector('#consciousness .metric-item:nth-child(3) .value').textContent = 
                this.data.aiSystems.consciousness.reasoningDepth;
        }
        
        // Update neuromorphic metrics if section is active
        if (document.getElementById('neuromorphic').classList.contains('active')) {
            document.querySelector('#neuromorphic .metric-item:nth-child(1) .value').textContent = 
                this.data.aiSystems.neuromorphic.powerSavings;
            document.querySelector('#neuromorphic .metric-item:nth-child(2) .value').textContent = 
                this.data.aiSystems.neuromorphic.spikeProcessing.toLocaleString() + '/s';
            document.querySelector('#neuromorphic .metric-item:nth-child(3) .value').textContent = 
                this.data.aiSystems.neuromorphic.efficiency + '%';
        }
        
        // Update evolution metrics if section is active
        if (document.getElementById('evolution').classList.contains('active')) {
            document.querySelector('#evolution .metric-item:nth-child(1) .value').textContent = 
                this.data.aiSystems.evolution.generationsEvolved;
            document.querySelector('#evolution .metric-item:nth-child(2) .value').textContent = 
                this.data.aiSystems.evolution.fitnessScore;
            document.querySelector('#evolution .metric-item:nth-child(3) .value').textContent = 
                this.data.aiSystems.evolution.improvements;
        }
    }
}

// Initialize the application when the DOM is loaded
document.addEventListener('DOMContentLoaded', () => {
    window.whiskeyAI = new WhiskeyAI();
});

// Add CSS for quantum particles
const particleStyles = `
    @keyframes float {
        0% {
            transform: translate(0, 0) rotate(0deg);
            opacity: 0.3;
        }
        50% {
            transform: translate(${Math.random() * 100 - 50}px, ${Math.random() * 100 - 50}px) rotate(180deg);
            opacity: 0.7;
        }
        100% {
            transform: translate(0, 0) rotate(360deg);
            opacity: 0.3;
        }
    }
    
    .quantum-particle {
        position: fixed;
        z-index: -1;
        pointer-events: none;
    }
`;

// Add the styles to the document
const styleSheet = document.createElement('style');
styleSheet.textContent = particleStyles;
document.head.appendChild(styleSheet);