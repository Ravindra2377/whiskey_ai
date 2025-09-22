import React from 'react';
import { motion } from 'framer-motion';
import { Network, Layers, Zap, GitBranch, Users, Activity } from 'lucide-react';

const AIOrchestration = () => {
  const providers = [
    { name: 'OpenAI GPT-4', status: 'Active', latency: '120ms', success: '99.8%' },
    { name: 'Anthropic Claude', status: 'Active', latency: '95ms', success: '99.9%' },
    { name: 'Google Gemini', status: 'Active', latency: '140ms', success: '99.7%' },
    { name: 'AWS Bedrock', status: 'Standby', latency: '200ms', success: '99.5%' }
  ];

  return (
    <div className="lg:ml-64 min-h-screen bg-quantum-bg-primary">
      <div className="pt-16 lg:pt-0">
        <div className="p-6 space-y-8">
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
          >
            <h1 className="text-4xl font-bold mb-2">
              <span className="quantum-gradient bg-clip-text text-transparent">
                AI Orchestration
              </span>
            </h1>
            <p className="text-quantum-text-secondary">
              Multi-provider AI integration with intelligent routing and optimization
            </p>
          </motion.div>

          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
            {[
              { icon: Network, label: 'Active Providers', value: '4', color: 'text-quantum-primary' },
              { icon: Users, label: 'Requests/min', value: '2.1K', color: 'text-quantum-secondary' },
              { icon: Activity, label: 'Success Rate', value: '99.8%', color: 'text-green-400' },
              { icon: Zap, label: 'Avg Latency', value: '115ms', color: 'text-quantum-accent' }
            ].map((metric, index) => {
              const Icon = metric.icon;
              return (
                <motion.div
                  key={metric.label}
                  initial={{ opacity: 0, y: 20 }}
                  animate={{ opacity: 1, y: 0 }}
                  transition={{ delay: index * 0.1 }}
                  className="quantum-card text-center"
                >
                  <Icon className={`w-8 h-8 ${metric.color} mx-auto mb-3`} />
                  <div className="text-2xl font-bold text-quantum-text-primary mb-1">
                    {metric.value}
                  </div>
                  <div className="text-sm text-quantum-text-muted">{metric.label}</div>
                </motion.div>
              );
            })}
          </div>

          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.3 }}
            className="quantum-card"
          >
            <h3 className="text-xl font-semibold text-quantum-text-primary mb-4">
              Provider Status
            </h3>
            <div className="space-y-4">
              {providers.map((provider, index) => (
                <motion.div
                  key={provider.name}
                  initial={{ opacity: 0, x: -20 }}
                  animate={{ opacity: 1, x: 0 }}
                  transition={{ delay: 0.4 + index * 0.1 }}
                  className="consciousness-border rounded-lg p-4"
                >
                  <div className="flex items-center justify-between">
                    <div className="flex items-center space-x-3">
                      <div className={`w-3 h-3 rounded-full ${
                        provider.status === 'Active' ? 'bg-green-400' : 'bg-yellow-400'
                      } animate-pulse`}></div>
                      <span className="font-medium text-quantum-text-primary">
                        {provider.name}
                      </span>
                    </div>
                    <div className="flex items-center space-x-6 text-sm text-quantum-text-muted">
                      <span>Latency: {provider.latency}</span>
                      <span>Success: {provider.success}</span>
                    </div>
                  </div>
                </motion.div>
              ))}
            </div>
          </motion.div>
        </div>
      </div>
    </div>
  );
};

export default AIOrchestration;