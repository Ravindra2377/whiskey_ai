import React, { useState } from 'react';
import { Link, useLocation } from 'react-router-dom';
import { motion, AnimatePresence } from 'framer-motion';
import {
  Brain,
  Cpu,
  Zap,
  Stars,
  Shield,
  Code,
  Building,
  Activity,
  Menu,
  X,
  Atom,
  Network,
  Sparkles
} from 'lucide-react';

const Navigation = () => {
  const [isOpen, setIsOpen] = useState(false);
  const location = useLocation();

  const navigationItems = [
    {
      name: 'Dashboard',
      path: '/dashboard',
      icon: Activity,
      description: 'System Overview'
    },
    {
      name: 'Quantum Consciousness',
      path: '/quantum-consciousness',
      icon: Brain,
      description: 'Consciousness Simulation'
    },
    {
      name: 'Neuromorphic Processing',
      path: '/neuromorphic-processing',
      icon: Cpu,
      description: 'Brain-Inspired Computing'
    },
    {
      name: 'AI Orchestration',
      path: '/ai-orchestration',
      icon: Network,
      description: 'Multi-Provider Integration'
    },
    {
      name: 'Revolutionary Features',
      path: '/revolutionary-features',
      icon: Stars,
      description: '20+ Advanced Capabilities'
    },
    {
      name: 'Developer API',
      path: '/developer-api',
      icon: Code,
      description: 'Integration & Testing'
    },
    {
      name: 'Enterprise Portal',
      path: '/enterprise-portal',
      icon: Building,
      description: 'Enterprise Management'
    },
    {
      name: 'System Status',
      path: '/system-status',
      icon: Shield,
      description: 'Health & Monitoring'
    }
  ];

  const isActiveRoute = (path) => {
    return location.pathname === path || (path === '/dashboard' && location.pathname === '/');
  };

  return (
    <>
      {/* Desktop Navigation */}
      <nav className="hidden lg:flex fixed top-0 left-0 h-full w-64 bg-quantum-bg-secondary/80 backdrop-blur-xl border-r border-quantum-border z-50">
        <div className="flex flex-col w-full">
          {/* Logo Section */}
          <div className="p-6 border-b border-quantum-border">
            <Link to="/dashboard" className="flex items-center space-x-3 group">
              <div className="relative">
                <Atom className="w-8 h-8 text-quantum-primary quantum-pulse" />
                <Sparkles className="w-4 h-4 text-quantum-accent absolute -top-1 -right-1 animate-pulse" />
              </div>
              <div>
                <h1 className="text-xl font-bold quantum-gradient bg-clip-text text-transparent">
                  NEXUS AI
                </h1>
                <p className="text-xs text-quantum-text-muted">Quantum Platform</p>
              </div>
            </Link>
          </div>

          {/* Navigation Items */}
          <div className="flex-1 p-4 space-y-2">
            {navigationItems.map((item) => {
              const Icon = item.icon;
              const isActive = isActiveRoute(item.path);
              
              return (
                <motion.div
                  key={item.path}
                  whileHover={{ scale: 1.02 }}
                  whileTap={{ scale: 0.98 }}
                >
                  <Link
                    to={item.path}
                    className={`
                      flex items-center space-x-3 px-4 py-3 rounded-lg transition-all duration-300
                      ${isActive 
                        ? 'bg-quantum-primary/20 text-quantum-primary border border-quantum-primary/30' 
                        : 'text-quantum-text-secondary hover:bg-quantum-bg-tertiary/50 hover:text-quantum-text-primary'
                      }
                    `}
                  >
                    <Icon className={`w-5 h-5 ${isActive ? 'text-quantum-primary' : ''}`} />
                    <div className="flex-1">
                      <div className="text-sm font-medium">{item.name}</div>
                      <div className="text-xs opacity-70">{item.description}</div>
                    </div>
                    {isActive && (
                      <motion.div
                        layoutId="activeIndicator"
                        className="w-2 h-2 bg-quantum-primary rounded-full"
                      />
                    )}
                  </Link>
                </motion.div>
              );
            })}
          </div>

          {/* Status Footer */}
          <div className="p-4 border-t border-quantum-border">
            <div className="flex items-center justify-between text-xs">
              <div className="flex items-center space-x-2">
                <div className="w-2 h-2 bg-green-400 rounded-full animate-pulse"></div>
                <span className="text-quantum-text-muted">All Systems Operational</span>
              </div>
              <Zap className="w-4 h-4 text-quantum-accent" />
            </div>
            <div className="mt-2 text-xs text-quantum-text-muted">
              Version 1.0.0 | Quantum Ready
            </div>
          </div>
        </div>
      </nav>

      {/* Mobile Navigation */}
      <div className="lg:hidden">
        {/* Mobile Header */}
        <header className="fixed top-0 left-0 right-0 h-16 bg-quantum-bg-secondary/90 backdrop-blur-xl border-b border-quantum-border z-50">
          <div className="flex items-center justify-between h-full px-4">
            <Link to="/dashboard" className="flex items-center space-x-2">
              <Atom className="w-6 h-6 text-quantum-primary" />
              <span className="text-lg font-bold text-quantum-text-primary">NEXUS AI</span>
            </Link>
            
            <button
              onClick={() => setIsOpen(!isOpen)}
              className="p-2 rounded-lg text-quantum-text-primary hover:bg-quantum-bg-tertiary/50"
            >
              {isOpen ? <X className="w-6 h-6" /> : <Menu className="w-6 h-6" />}
            </button>
          </div>
        </header>

        {/* Mobile Menu */}
        <AnimatePresence>
          {isOpen && (
            <motion.div
              initial={{ opacity: 0, y: -20 }}
              animate={{ opacity: 1, y: 0 }}
              exit={{ opacity: 0, y: -20 }}
              className="fixed top-16 left-0 right-0 bg-quantum-bg-secondary/95 backdrop-blur-xl border-b border-quantum-border z-40"
            >
              <div className="p-4 space-y-2">
                {navigationItems.map((item) => {
                  const Icon = item.icon;
                  const isActive = isActiveRoute(item.path);
                  
                  return (
                    <Link
                      key={item.path}
                      to={item.path}
                      onClick={() => setIsOpen(false)}
                      className={`
                        flex items-center space-x-3 px-4 py-3 rounded-lg transition-all duration-300
                        ${isActive 
                          ? 'bg-quantum-primary/20 text-quantum-primary' 
                          : 'text-quantum-text-secondary hover:bg-quantum-bg-tertiary/50'
                        }
                      `}
                    >
                      <Icon className="w-5 h-5" />
                      <div>
                        <div className="text-sm font-medium">{item.name}</div>
                        <div className="text-xs opacity-70">{item.description}</div>
                      </div>
                    </Link>
                  );
                })}
              </div>
            </motion.div>
          )}
        </AnimatePresence>
      </div>

      {/* Mobile overlay */}
      {isOpen && (
        <div
          className="lg:hidden fixed inset-0 bg-black/50 z-30"
          onClick={() => setIsOpen(false)}
        />
      )}
    </>
  );
};

export default Navigation;