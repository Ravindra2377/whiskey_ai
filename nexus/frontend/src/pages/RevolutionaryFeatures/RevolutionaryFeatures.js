import React from 'react';
import { motion } from 'framer-motion';
import { Stars, Sparkles, Zap, Brain, Code, Shield } from 'lucide-react';

const RevolutionaryFeatures = () => {
  const features = [
    { name: 'AI Swarm Intelligence', status: 'Active', icon: Stars },
    { name: 'Natural Language Programming', status: 'Ready', icon: Code },
    { name: 'Autonomous Code Healing', status: 'Monitoring', icon: Shield },
    { name: 'Predictive Vulnerability Scanner', status: 'Scanning', icon: Brain },
    { name: 'Dream Coding', status: 'Sleeping', icon: Sparkles },
    { name: 'Quantum Reality Manipulation', status: 'Research', icon: Zap }
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
                Revolutionary Features
              </span>
            </h1>
            <p className="text-quantum-text-secondary">
              20+ cutting-edge AI capabilities that redefine what's possible
            </p>
          </motion.div>

          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {features.map((feature, index) => {
              const Icon = feature.icon;
              return (
                <motion.div
                  key={feature.name}
                  initial={{ opacity: 0, y: 20 }}
                  animate={{ opacity: 1, y: 0 }}
                  transition={{ delay: index * 0.1 }}
                  className="quantum-card consciousness-float"
                >
                  <Icon className="w-8 h-8 text-quantum-primary mb-3" />
                  <h3 className="font-semibold text-quantum-text-primary mb-2">
                    {feature.name}
                  </h3>
                  <span className={`text-xs px-2 py-1 rounded ${
                    feature.status === 'Active' ? 'bg-green-400/10 text-green-400' :
                    feature.status === 'Ready' ? 'bg-blue-400/10 text-blue-400' :
                    'bg-quantum-primary/10 text-quantum-primary'
                  }`}>
                    {feature.status}
                  </span>
                </motion.div>
              );
            })}
          </div>
        </div>
      </div>
    </div>
  );
};

export default RevolutionaryFeatures;