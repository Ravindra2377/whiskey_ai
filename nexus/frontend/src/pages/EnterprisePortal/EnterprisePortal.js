import React from 'react';
import { motion } from 'framer-motion';
import { Building } from 'lucide-react';

const EnterprisePortal = () => {
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
                Enterprise Portal
              </span>
            </h1>
            <p className="text-quantum-text-secondary">
              Enterprise client management and multi-tenant administration
            </p>
          </motion.div>

          <div className="quantum-card text-center py-12">
            <Building className="w-16 h-16 text-quantum-secondary mx-auto mb-4 quantum-pulse" />
            <h3 className="text-xl font-semibold text-quantum-text-primary mb-2">
              Enterprise Management
            </h3>
            <p className="text-quantum-text-secondary">
              Enterprise portal interface coming soon...
            </p>
          </div>
        </div>
      </div>
    </div>
  );
};

export default EnterprisePortal;