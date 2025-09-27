import React, { useState, useEffect } from 'react';
import { motion } from 'framer-motion';
import {
  Brain,
  Cpu,
  Network,
  Activity,
  Users,
  Server,
  Clock,
  Star,
  Sparkles,
  Target,
  BarChart3
} from 'lucide-react';

// Import chart components
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, PieChart, Pie, Cell } from 'recharts';

const Dashboard = () => {
  const [systemMetrics] = useState({
    quantumEfficiency: 95.7,
    consciousnessAccuracy: 99.2,
    neuromorphicPower: 98.5,
    aiOrchestrationSuccess: 97.8,
    totalProcessedTasks: 15420,
    activeConnections: 1247,
    uptime: '99.9%'
  });

  const [realtimeData, setRealtimeData] = useState([]);
  // const [performanceData, setPerformanceData] = useState([]);

  // Simulate real-time data updates
  useEffect(() => {
    const interval = setInterval(() => {
      const timestamp = new Date().toLocaleTimeString();
      const newDataPoint = {
        time: timestamp,
        quantum: 95 + Math.random() * 5,
        consciousness: 98 + Math.random() * 2,
        neuromorphic: 97 + Math.random() * 3,
        orchestration: 96 + Math.random() * 4
      };

      setRealtimeData(prev => {
        const updated = [...prev, newDataPoint];
        return updated.slice(-20); // Keep last 20 data points
      });
    }, 3000);

    return () => clearInterval(interval);
  }, []);

  // (Optional) Performance data generator removed; using realtimeData for live chart

  const systemComponents = [
    {
      name: 'Quantum Intelligence',
      status: 'Active',
      efficiency: systemMetrics.quantumEfficiency,
  icon: Brain,
      color: 'text-quantum-primary',
      bgColor: 'bg-quantum-primary/10',
      description: 'Quantum-inspired optimization running'
    },
    {
      name: 'Consciousness Engine',
      status: 'Processing',
      efficiency: systemMetrics.consciousnessAccuracy,
      icon: Brain,
      color: 'text-quantum-secondary',
      bgColor: 'bg-quantum-secondary/10',
      description: 'Self-awareness models active'
    },
    {
      name: 'Neuromorphic Computing',
      status: 'Optimized',
      efficiency: systemMetrics.neuromorphicPower,
      icon: Cpu,
      color: 'text-quantum-accent',
      bgColor: 'bg-quantum-accent/10',
      description: 'Spike-based processing engaged'
    },
    {
      name: 'AI Orchestration',
      status: 'Coordinating',
      efficiency: systemMetrics.aiOrchestrationSuccess,
      icon: Network,
      color: 'text-green-400',
      bgColor: 'bg-green-400/10',
      description: 'Multi-provider integration online'
    }
  ];

  const revolutionaryFeatures = [
    { name: 'AI Swarm Intelligence', status: 'Active', usage: '1,247 agents' },
    { name: 'Natural Language Programming', status: 'Ready', usage: '892 requests' },
    { name: 'Autonomous Code Healing', status: 'Monitoring', usage: '15 fixes today' },
    { name: 'Predictive Vulnerability Scanner', status: 'Scanning', usage: '0 threats detected' },
    { name: 'Dream Coding', status: 'Sleeping', usage: 'Next: 22:00' },
    { name: 'Quantum Reality Manipulation', status: 'Standby', usage: 'Research mode' }
  ];

  const pieData = [
    { name: 'Quantum Processing', value: 35, color: '#7877c6' },
    { name: 'Consciousness Simulation', value: 25, color: '#ff77c6' },
    { name: 'Neuromorphic Computing', value: 20, color: '#78dbff' },
    { name: 'AI Orchestration', value: 20, color: '#4ade80' }
  ];

  return (
    <div className="lg:ml-64 min-h-screen bg-quantum-bg-primary">
      {/* Mobile padding for header */}
      <div className="pt-16 lg:pt-0">
        <div className="p-6 space-y-8">
          {/* Header Section */}
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            className="flex flex-col lg:flex-row lg:items-center lg:justify-between"
          >
            <div>
              <h1 className="text-4xl font-bold mb-2">
                <span className="quantum-gradient bg-clip-text text-transparent">
                  NEXUS AI Dashboard
                </span>
              </h1>
              <p className="text-quantum-text-secondary">
                Revolutionary Quantum Consciousness Platform - All Systems Operational
              </p>
            </div>
            
            <motion.div
              className="mt-4 lg:mt-0 flex items-center space-x-4"
              initial={{ opacity: 0, x: 20 }}
              animate={{ opacity: 1, x: 0 }}
              transition={{ delay: 0.2 }}
            >
              <div className="flex items-center space-x-2 px-4 py-2 bg-green-400/10 rounded-lg border border-green-400/20">
                <div className="w-2 h-2 bg-green-400 rounded-full animate-pulse"></div>
                <span className="text-green-400 text-sm font-medium">Production Ready</span>
              </div>
              <div className="text-quantum-text-muted text-sm">
                Uptime: {systemMetrics.uptime}
              </div>
            </motion.div>
          </motion.div>

          {/* Key Metrics Grid */}
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
            {systemComponents.map((component, index) => {
              const Icon = component.icon;
              return (
                <motion.div
                  key={component.name}
                  initial={{ opacity: 0, y: 20 }}
                  animate={{ opacity: 1, y: 0 }}
                  transition={{ delay: index * 0.1 }}
                  className="quantum-card consciousness-float"
                >
                  <div className="flex items-center justify-between mb-4">
                    <div className={`p-3 rounded-lg ${component.bgColor}`}>
                      <Icon className={`w-6 h-6 ${component.color}`} />
                    </div>
                    <div className="text-right">
                      <div className="text-2xl font-bold text-quantum-text-primary">
                        {component.efficiency}%
                      </div>
                      <div className="text-xs text-quantum-text-muted">Efficiency</div>
                    </div>
                  </div>
                  
                  <h3 className="font-semibold text-quantum-text-primary mb-1">
                    {component.name}
                  </h3>
                  <p className="text-sm text-quantum-text-secondary mb-2">
                    {component.description}
                  </p>
                  
                  <div className="flex items-center justify-between">
                    <span className={`text-xs px-2 py-1 rounded ${component.bgColor} ${component.color}`}>
                      {component.status}
                    </span>
                    <Sparkles className={`w-4 h-4 ${component.color} animate-pulse`} />
                  </div>
                </motion.div>
              );
            })}
          </div>

          {/* Charts Section */}
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
            {/* Real-time Performance Chart */}
            <motion.div
              initial={{ opacity: 0, x: -20 }}
              animate={{ opacity: 1, x: 0 }}
              transition={{ delay: 0.3 }}
              className="quantum-card"
            >
              <div className="flex items-center justify-between mb-6">
                <h3 className="text-xl font-semibold text-quantum-text-primary">
                  Real-time System Performance
                </h3>
                <Activity className="w-5 h-5 text-quantum-accent" />
              </div>
              
              <div className="h-64">
                <ResponsiveContainer width="100%" height="100%">
                  <LineChart data={realtimeData}>
                    <CartesianGrid strokeDasharray="3 3" stroke="#333366" />
                    <XAxis dataKey="time" stroke="#b0b0d0" fontSize={12} />
                    <YAxis stroke="#b0b0d0" fontSize={12} />
                    <Tooltip
                      contentStyle={{
                        backgroundColor: 'rgba(26, 26, 46, 0.9)',
                        border: '1px solid #333366',
                        borderRadius: '8px',
                        color: '#ffffff'
                      }}
                    />
                    <Line
                      type="monotone"
                      dataKey="quantum"
                      stroke="#7877c6"
                      strokeWidth={2}
                      dot={false}
                    />
                    <Line
                      type="monotone"
                      dataKey="consciousness"
                      stroke="#ff77c6"
                      strokeWidth={2}
                      dot={false}
                    />
                    <Line
                      type="monotone"
                      dataKey="neuromorphic"
                      stroke="#78dbff"
                      strokeWidth={2}
                      dot={false}
                    />
                  </LineChart>
                </ResponsiveContainer>
              </div>
            </motion.div>

            {/* Resource Distribution */}
            <motion.div
              initial={{ opacity: 0, x: 20 }}
              animate={{ opacity: 1, x: 0 }}
              transition={{ delay: 0.4 }}
              className="quantum-card"
            >
              <div className="flex items-center justify-between mb-6">
                <h3 className="text-xl font-semibold text-quantum-text-primary">
                  Processing Distribution
                </h3>
                <BarChart3 className="w-5 h-5 text-quantum-accent" />
              </div>
              
              <div className="h-64">
                <ResponsiveContainer width="100%" height="100%">
                  <PieChart>
                    <Pie
                      data={pieData}
                      cx="50%"
                      cy="50%"
                      innerRadius={60}
                      outerRadius={100}
                      paddingAngle={5}
                      dataKey="value"
                    >
                      {pieData.map((entry, index) => (
                        <Cell key={`cell-${index}`} fill={entry.color} />
                      ))}
                    </Pie>
                    <Tooltip
                      contentStyle={{
                        backgroundColor: 'rgba(26, 26, 46, 0.9)',
                        border: '1px solid #333366',
                        borderRadius: '8px',
                        color: '#ffffff'
                      }}
                    />
                  </PieChart>
                </ResponsiveContainer>
              </div>
              
              <div className="mt-4 grid grid-cols-2 gap-4">
                {pieData.map((item, index) => (
                  <div key={index} className="flex items-center space-x-2">
                    <div
                      className="w-3 h-3 rounded-full"
                      style={{ backgroundColor: item.color }}
                    ></div>
                    <span className="text-xs text-quantum-text-secondary">{item.name}</span>
                  </div>
                ))}
              </div>
            </motion.div>
          </div>

          {/* Revolutionary Features Status */}
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.5 }}
            className="quantum-card"
          >
            <div className="flex items-center justify-between mb-6">
              <h3 className="text-xl font-semibold text-quantum-text-primary">
                Revolutionary Features Status
              </h3>
              <Star className="w-5 h-5 text-quantum-accent" />
            </div>
            
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
              {revolutionaryFeatures.map((feature, index) => (
                <motion.div
                  key={feature.name}
                  initial={{ opacity: 0, scale: 0.9 }}
                  animate={{ opacity: 1, scale: 1 }}
                  transition={{ delay: 0.6 + index * 0.1 }}
                  className="consciousness-border rounded-lg p-4 hover:border-quantum-primary/30 transition-all duration-300"
                >
                  <div className="flex items-center justify-between mb-2">
                    <h4 className="font-medium text-quantum-text-primary text-sm">
                      {feature.name}
                    </h4>
                    <div className="flex items-center space-x-1">
                      <div className={`w-2 h-2 rounded-full ${
                        feature.status === 'Active' ? 'bg-green-400' :
                        feature.status === 'Ready' ? 'bg-blue-400' :
                        feature.status === 'Monitoring' ? 'bg-yellow-400' :
                        feature.status === 'Scanning' ? 'bg-purple-400' :
                        feature.status === 'Sleeping' ? 'bg-gray-400' :
                        'bg-quantum-primary'
                      } animate-pulse`}></div>
                      <span className="text-xs text-quantum-text-muted">{feature.status}</span>
                    </div>
                  </div>
                  <div className="text-xs text-quantum-text-secondary">
                    {feature.usage}
                  </div>
                </motion.div>
              ))}
            </div>
          </motion.div>

          {/* System Statistics */}
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.7 }}
            className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6"
          >
            <div className="quantum-card text-center">
              <Target className="w-8 h-8 text-quantum-primary mx-auto mb-2" />
              <div className="text-2xl font-bold text-quantum-text-primary">
                {systemMetrics.totalProcessedTasks.toLocaleString()}
              </div>
              <div className="text-sm text-quantum-text-muted">Tasks Processed</div>
            </div>
            
            <div className="quantum-card text-center">
              <Users className="w-8 h-8 text-quantum-secondary mx-auto mb-2" />
              <div className="text-2xl font-bold text-quantum-text-primary">
                {systemMetrics.activeConnections.toLocaleString()}
              </div>
              <div className="text-sm text-quantum-text-muted">Active Connections</div>
            </div>
            
            <div className="quantum-card text-center">
              <Server className="w-8 h-8 text-quantum-accent mx-auto mb-2" />
              <div className="text-2xl font-bold text-quantum-text-primary">65+</div>
              <div className="text-sm text-quantum-text-muted">Automated Tests</div>
            </div>
            
            <div className="quantum-card text-center">
              <Clock className="w-8 h-8 text-green-400 mx-auto mb-2" />
              <div className="text-2xl font-bold text-quantum-text-primary">{'<5s'}</div>
              <div className="text-sm text-quantum-text-muted">Response Time</div>
            </div>
          </motion.div>
        </div>
      </div>
    </div>
  );
};

export default Dashboard;