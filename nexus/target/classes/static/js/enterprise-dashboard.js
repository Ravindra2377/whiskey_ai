// Enterprise Dashboard JavaScript for NEXUS AI Universal Enterprise Integration Engine

class EnterpriseDashboard {
    constructor() {
        this.apiBaseUrl = '/api/nexus/enterprise';
        this.init();
    }

    init() {
        console.log('🚀 Initializing Enterprise Dashboard...');
        this.setupEventListeners();
        this.loadDashboardData();
    }

    setupEventListeners() {
        // Integration form submission
        const integrateForm = document.getElementById('integrate-form');
        if (integrateForm) {
            integrateForm.addEventListener('submit', (e) => this.handleIntegration(e));
        }

        // Discovery form submission
        const discoveryForm = document.getElementById('discovery-form');
        if (discoveryForm) {
            discoveryForm.addEventListener('submit', (e) => this.handleDiscovery(e));
        }

        // Configuration form submission
        const configForm = document.getElementById('config-form');
        if (configForm) {
            configForm.addEventListener('submit', (e) => this.handleConfiguration(e));
        }

        // Deployment form submission
        const deployForm = document.getElementById('deploy-form');
        if (deployForm) {
            deployForm.addEventListener('submit', (e) => this.handleDeployment(e));
        }

        // Support form submission
        const supportForm = document.getElementById('support-form');
        if (supportForm) {
            supportForm.addEventListener('submit', (e) => this.handleSupport(e));
        }

        // Tab navigation
        const tabs = document.querySelectorAll('.tab');
        tabs.forEach(tab => {
            tab.addEventListener('click', () => this.switchTab(tab));
        });

        // Button click handlers for the HTML-based forms
        const integrateBtn = document.getElementById('integrate-btn');
        if (integrateBtn) {
            integrateBtn.addEventListener('click', () => this.handleIntegrationFromHTML());
        }

        const discoverBtn = document.getElementById('discover-btn');
        if (discoverBtn) {
            discoverBtn.addEventListener('click', () => this.handleDiscoveryFromHTML());
        }

        const configureBtn = document.getElementById('configure-btn');
        if (configureBtn) {
            configureBtn.addEventListener('click', () => this.handleConfigurationFromHTML());
        }

        const deployBtn = document.getElementById('deploy-btn');
        if (deployBtn) {
            deployBtn.addEventListener('click', () => this.handleDeploymentFromHTML());
        }

        const supportBtn = document.getElementById('support-btn');
        if (supportBtn) {
            supportBtn.addEventListener('click', () => this.handleSupportFromHTML());
        }
    }

    // Handle integration from form submission
    async handleIntegration(event) {
        event.preventDefault();
        
        const formData = new FormData(event.target);
        const clientData = {
            clientName: formData.get('clientName'),
            tier: formData.get('tier'),
            configuration: {
                industry: formData.get('industry'),
                employees: parseInt(formData.get('employees')) || 0
            }
        };

        this.showLoading('integration');
        
        try {
            const response = await this.apiRequest('/integrate', 'POST', clientData);
            this.displayResult('Integration', response);
            this.updateDashboardStats(response);
        } catch (error) {
            this.displayError('Integration', error);
        } finally {
            this.hideLoading('integration');
        }
    }

    // Handle integration from HTML button click
    async handleIntegrationFromHTML() {
        const clientName = document.getElementById('client-name').value;
        const clientTier = document.getElementById('client-tier').value;
        const industry = document.getElementById('industry').value;
        const employees = document.getElementById('employees').value;
        
        if (!clientName) {
            alert('Please enter an enterprise name');
            return;
        }
        
        const clientData = {
            clientName: clientName,
            tier: clientTier,
            configuration: {
                industry: industry,
                employees: employees ? parseInt(employees) : 0
            },
            discoveryParams: {
                scan_network: true,
                scan_apis: true,
                scan_databases: true
            },
            configParams: {
                auto_configure: true
            },
            deploymentParams: {
                region: 'us-west-2'
            }
        };

        this.showLoading('integration');
        
        try {
            const response = await this.apiRequest('/integrate', 'POST', clientData);
            this.displayResult('Integration', response);
            this.updateDashboardStats(response);
        } catch (error) {
            this.displayError('Integration', error);
        } finally {
            this.hideLoading('integration');
        }
    }

    // Handle discovery from form submission
    async handleDiscovery(event) {
        event.preventDefault();
        
        const formData = new FormData(event.target);
        const discoveryParams = {
            scan_network: formData.get('scanNetwork') === 'on',
            scan_apis: formData.get('scanApis') === 'on',
            scan_databases: formData.get('scanDatabases') === 'on'
        };

        this.showLoading('discovery');
        
        try {
            const response = await this.apiRequest('/discover-systems', 'POST', discoveryParams);
            this.displayResult('Discovery', response);
            this.updateStat('systems-discovered', response.discoveredSystems?.length || 0);
        } catch (error) {
            this.displayError('Discovery', error);
        } finally {
            this.hideLoading('discovery');
        }
    }

    // Handle discovery from HTML button click
    async handleDiscoveryFromHTML() {
        const scanNetwork = document.getElementById('scan-network').value === 'true';
        const scanApis = document.getElementById('scan-apis').value === 'true';
        const scanDatabases = document.getElementById('scan-databases').value === 'true';
        
        const discoveryParams = {
            scan_network: scanNetwork,
            scan_apis: scanApis,
            scan_databases: scanDatabases
        };

        this.showLoading('discovery');
        
        try {
            const response = await this.apiRequest('/discover-systems', 'POST', discoveryParams);
            this.displayResult('Discovery', response);
            this.updateStat('systems-discovered', response.discoveredSystems?.length || 0);
        } catch (error) {
            this.displayError('Discovery', error);
        } finally {
            this.hideLoading('discovery');
        }
    }

    // Handle configuration from form submission
    async handleConfiguration(event) {
        event.preventDefault();
        
        // First get discovered systems
        try {
            const discoveryResponse = await this.apiRequest('/discover-systems', 'POST', {
                scan_network: true,
                scan_apis: true,
                scan_databases: true
            });
            
            const configParams = {
                systems: discoveryResponse.discoveredSystems,
                configParams: {
                    auto_configure: true
                }
            };

            this.showLoading('configuration');
            
            const response = await this.apiRequest('/configure-connections', 'POST', configParams);
            this.displayResult('Configuration', response);
            this.updateStat('connections-configured', response.length || 0);
        } catch (error) {
            this.displayError('Configuration', error);
        } finally {
            this.hideLoading('configuration');
        }
    }

    // Handle configuration from HTML button click
    async handleConfigurationFromHTML() {
        const autoConfigure = document.getElementById('auto-configure').value === 'true';
        const securityProtocol = document.getElementById('security-protocol').value;
        
        // First get discovered systems
        try {
            const discoveryResponse = await this.apiRequest('/discover-systems', 'POST', {
                scan_network: true,
                scan_apis: true,
                scan_databases: true
            });
            
            const configParams = {
                systems: discoveryResponse.discoveredSystems,
                configParams: {
                    auto_configure: autoConfigure,
                    security_protocol: securityProtocol
                }
            };

            this.showLoading('configuration');
            
            const response = await this.apiRequest('/configure-connections', 'POST', configParams);
            this.displayResult('Configuration', response);
            this.updateStat('connections-configured', response.length || 0);
        } catch (error) {
            this.displayError('Configuration', error);
        } finally {
            this.hideLoading('configuration');
        }
    }

    // Handle deployment from form submission
    async handleDeployment(event) {
        event.preventDefault();
        
        // First get discovered systems
        try {
            const discoveryResponse = await this.apiRequest('/discover-systems', 'POST', {
                scan_network: true,
                scan_apis: true,
                scan_databases: true
            });
            
            const deploymentParams = {
                systems: discoveryResponse.discoveredSystems,
                deploymentParams: {
                    region: document.getElementById('deployment-region').value,
                    monitoring_enabled: document.getElementById('monitoring-enabled').value === 'true'
                }
            };

            this.showLoading('deployment');
            
            const response = await this.apiRequest('/deploy-agents', 'POST', deploymentParams);
            this.displayResult('Deployment', response);
            this.updateStat('agents-deployed', response.length || 0);
        } catch (error) {
            this.displayError('Deployment', error);
        } finally {
            this.hideLoading('deployment');
        }
    }

    // Handle deployment from HTML button click
    async handleDeploymentFromHTML() {
        const deploymentRegion = document.getElementById('deployment-region').value;
        const monitoringEnabled = document.getElementById('monitoring-enabled').value === 'true';
        
        // First get discovered systems
        try {
            const discoveryResponse = await this.apiRequest('/discover-systems', 'POST', {
                scan_network: true,
                scan_apis: true,
                scan_databases: true
            });
            
            const deploymentParams = {
                systems: discoveryResponse.discoveredSystems,
                deploymentParams: {
                    region: deploymentRegion,
                    monitoring_enabled: monitoringEnabled
                }
            };

            this.showLoading('deployment');
            
            const response = await this.apiRequest('/deploy-agents', 'POST', deploymentParams);
            this.displayResult('Deployment', response);
            this.updateStat('agents-deployed', response.length || 0);
        } catch (error) {
            this.displayError('Deployment', error);
        } finally {
            this.hideLoading('deployment');
        }
    }

    // Handle support from form submission
    async handleSupport(event) {
        event.preventDefault();
        
        const formData = new FormData(event.target);
        const supportRequest = {
            requestId: 'SUPPORT_' + Date.now(),
            systemId: formData.get('systemId'),
            supportType: formData.get('supportType'),
            description: formData.get('description')
        };

        this.showLoading('support');
        
        try {
            const response = await this.apiRequest('/technical-support', 'POST', supportRequest);
            this.displayResult('Support', response);
        } catch (error) {
            this.displayError('Support', error);
        } finally {
            this.hideLoading('support');
        }
    }

    // Handle support from HTML button click
    async handleSupportFromHTML() {
        const supportType = document.getElementById('support-type').value;
        const systemId = document.getElementById('system-id').value;
        const description = document.getElementById('support-description').value;
        
        if (!systemId) {
            alert('Please enter a system ID');
            return;
        }
        
        const supportRequest = {
            requestId: 'SUPPORT_' + Date.now(),
            systemId: systemId,
            supportType: supportType,
            description: description
        };

        this.showLoading('support');
        
        try {
            const response = await this.apiRequest('/technical-support', 'POST', supportRequest);
            this.displayResult('Support', response);
        } catch (error) {
            this.displayError('Support', error);
        } finally {
            this.hideLoading('support');
        }
    }

    switchTab(tabElement) {
        // Remove active class from all tabs and content
        document.querySelectorAll('.tab').forEach(tab => tab.classList.remove('active'));
        document.querySelectorAll('.tab-content').forEach(content => content.classList.remove('active'));
        
        // Add active class to clicked tab
        tabElement.classList.add('active');
        
        // Show corresponding content
        const tabName = tabElement.getAttribute('data-tab');
        document.getElementById(`${tabName}-tab`).classList.add('active');
    }

    async apiRequest(endpoint, method = 'GET', data = null) {
        const url = this.apiBaseUrl + endpoint;
        const options = {
            method: method,
            headers: {
                'Content-Type': 'application/json'
            }
        };

        if (data) {
            options.body = JSON.stringify(data);
        }

        const response = await fetch(url, options);
        
        if (!response.ok) {
            throw new Error(`API request failed with status ${response.status}`);
        }
        
        return await response.json();
    }

    showLoading(section) {
        const loadingElement = document.getElementById(`${section}-loading`);
        if (loadingElement) {
            loadingElement.style.display = 'block';
        }
        
        const submitButton = document.querySelector(`#${section}-form button[type="submit"]`);
        const htmlButton = document.getElementById(`${section}-btn`);
        if (submitButton) {
            submitButton.disabled = true;
        }
        if (htmlButton) {
            htmlButton.disabled = true;
        }
    }

    hideLoading(section) {
        const loadingElement = document.getElementById(`${section}-loading`);
        if (loadingElement) {
            loadingElement.style.display = 'none';
        }
        
        const submitButton = document.querySelector(`#${section}-form button[type="submit"]`);
        const htmlButton = document.getElementById(`${section}-btn`);
        if (submitButton) {
            submitButton.disabled = false;
        }
        if (htmlButton) {
            htmlButton.disabled = false;
        }
    }

    displayResult(operation, data) {
        const resultsContent = document.getElementById('results-content');
        const resultItem = document.createElement('div');
        resultItem.className = 'result-item';
        resultItem.innerHTML = `
            <h4>${operation} Results</h4>
            <pre>${JSON.stringify(data, null, 2)}</pre>
        `;
        resultsContent.prepend(resultItem);
        
        // Scroll to results
        resultsContent.parentElement.scrollTop = 0;
    }

    displayError(operation, error) {
        const resultsContent = document.getElementById('results-content');
        const resultItem = document.createElement('div');
        resultItem.className = 'result-item';
        resultItem.style.borderLeftColor = '#ff6666';
        resultItem.innerHTML = `
            <h4>${operation} Error</h4>
            <pre style="color: #ff6666;">${error.message || error}</pre>
        `;
        resultsContent.prepend(resultItem);
        
        // Scroll to results
        resultsContent.parentElement.scrollTop = 0;
    }

    updateDashboardStats(data) {
        if (data.discoveryResult) {
            this.updateStat('systems-discovered', data.discoveryResult.discoveredSystems?.length || 0);
        }
        if (data.configurations) {
            this.updateStat('connections-configured', data.configurations);
        }
        if (data.agentsDeployed) {
            this.updateStat('agents-deployed', data.agentsDeployed);
        }
        // Update revenue potential based on tier
        if (data.client && data.client.tier) {
            const revenue = this.calculateRevenuePotential(data.client.tier);
            this.updateStat('revenue-potential', revenue);
        }
    }

    updateStat(statId, value) {
        const statElement = document.getElementById(statId);
        if (statElement) {
            statElement.textContent = value;
        }
    }

    calculateRevenuePotential(tier) {
        const tierValues = {
            'Startup': '$2.4B',
            'Scale-Up': '$1.9B',
            'Enterprise': '$1.5B',
            'Global Enterprise': '$900M'
        };
        return tierValues[tier] || '$0B';
    }

    async loadDashboardData() {
        // Load initial dashboard data
        try {
            // This would typically fetch current stats from the backend
            console.log('Dashboard data loaded');
        } catch (error) {
            console.error('Failed to load dashboard data:', error);
        }
    }
}

// Initialize the dashboard when the page loads
document.addEventListener('DOMContentLoaded', () => {
    window.enterpriseDashboard = new EnterpriseDashboard();
});