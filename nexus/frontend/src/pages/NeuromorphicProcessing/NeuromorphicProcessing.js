import React from 'react';
import { motion } from 'framer-motion';
import { Cpu, Zap, Activity, Brain, TrendingUp } from 'lucide-react';

const NeuromorphicProcessing = () => {
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
                Neuromorphic Processing
              </span>
            </h1>
            <p className="text-quantum-text-secondary">
              Brain-inspired ultra-efficient computing with spike-based neural networks
            </p>
          </motion.div>

          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
            {[
              { icon: Cpu, label: 'Processing Cores', value: '1,024', color: 'text-quantum-primary' },
              { icon: Zap, label: 'Power Efficiency', value: '98.5%', color: 'text-quantum-secondary' },
              { icon: Activity, label: 'Spike Rate', value: '15.4K/s', color: 'text-quantum-accent' },
              { icon: Brain, label: 'Neural Density', value: '2.1M', color: 'text-green-400' }
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
              Neuromorphic Architecture Status
            </h3>
            <div className="text-center py-8">
              <TrendingUp className="w-16 h-16 text-quantum-accent mx-auto mb-4 quantum-pulse" />
              <p className="text-quantum-text-secondary">
                Advanced neuromorphic processing interface coming soon...
              </p>
            </div>
          </motion.div>
        </div>
      </div>
    </div>
  );
};

export default NeuromorphicProcessing;