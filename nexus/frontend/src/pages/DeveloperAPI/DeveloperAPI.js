import React from 'react';
import { motion } from 'framer-motion';
import { Code } from 'lucide-react';

const DeveloperAPI = () => {
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
                Developer API
              </span>
            </h1>
            <p className="text-quantum-text-secondary">
              Comprehensive API documentation and integration testing tools
            </p>
          </motion.div>

          <div className="quantum-card text-center py-12">
            <Code className="w-16 h-16 text-quantum-accent mx-auto mb-4 quantum-pulse" />
            <h3 className="text-xl font-semibold text-quantum-text-primary mb-2">
              API Documentation
            </h3>
            <p className="text-quantum-text-secondary">
              Developer integration interface coming soon...
            </p>
          </div>
        </div>
      </div>
    </div>
  );
};

export default DeveloperAPI;