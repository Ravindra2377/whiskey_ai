import React from 'react';
import { motion } from 'framer-motion';
import { Activity, Shield, CheckCircle, AlertTriangle } from 'lucide-react';
import { useSystemStatus } from '../../hooks/useSystemStatus';

const SystemStatus = () => {
  const { data: status, isLoading } = useSystemStatus();

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
                System Status
              </span>
            </h1>
            <p className="text-quantum-text-secondary">
              Real-time system health monitoring and performance analytics
            </p>
          </motion.div>

          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
            {[
              { icon: CheckCircle, label: 'System Health', value: isLoading ? '…' : status.health, color: 'text-green-400' },
              { icon: Activity, label: 'Uptime', value: isLoading ? '…' : status.uptime, color: 'text-quantum-primary' },
              { icon: Shield, label: 'Security', value: isLoading ? '…' : status.security, color: 'text-quantum-accent' },
              { icon: AlertTriangle, label: 'Alerts', value: isLoading ? '…' : String(status.alerts), color: 'text-yellow-400' }
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
            className="quantum-card text-center py-12"
          >
            <Activity className="w-16 h-16 text-green-400 mx-auto mb-4 quantum-pulse" />
            <h3 className="text-xl font-semibold text-quantum-text-primary mb-2">
              {isLoading ? 'Loading Status…' : 'All Systems Operational'}
            </h3>
            <p className="text-quantum-text-secondary">
              {isLoading ? 'Fetching live metrics.' : 'Advanced monitoring dashboard coming soon...'}
            </p>
          </motion.div>
        </div>
      </div>
    </div>
  );
};

export default SystemStatus;