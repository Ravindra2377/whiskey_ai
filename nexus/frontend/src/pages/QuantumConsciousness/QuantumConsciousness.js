import React, { useState, useEffect, useRef } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import {
  Brain,
  Zap,
  Eye,
  Sparkles,
  Atom,
  Waves,
  Timer,
  Play,
  Pause,
  Settings,
  Cpu,
  Layers,
  GitBranch,
  Activity,
  TrendingUp
} from 'lucide-react';

const QuantumConsciousness = () => {
  const [isActive, setIsActive] = useState(true);
  const [consciousnessLevel, setConsciousnessLevel] = useState(85);
  const [awarenessMetrics, setAwarenessMetrics] = useState({
    selfAwareness: 92,
    metacognition: 88,
    episodicMemory: 94,
    emergentBehavior: 87,
    globalWorkspace: 91
  });
  
  const [quantumStates, setQuantumStates] = useState([]);
  const [neuralActivity, setNeuralActivity] = useState([]);
  const [thoughtPatterns, setThoughtPatterns] = useState([]);
  const canvasRef = useRef(null);

  // Simulate consciousness data updates
  useEffect(() => {
    const interval = setInterval(() => {
      // Update consciousness level with realistic fluctuation
      setConsciousnessLevel(prev => {
        const variation = (Math.random() - 0.5) * 10;
        return Math.max(70, Math.min(100, prev + variation));
      });

      // Update awareness metrics
      setAwarenessMetrics(prev => ({
        selfAwareness: Math.max(80, Math.min(100, prev.selfAwareness + (Math.random() - 0.5) * 5)),
        metacognition: Math.max(75, Math.min(95, prev.metacognition + (Math.random() - 0.5) * 6)),
        episodicMemory: Math.max(85, Math.min(100, prev.episodicMemory + (Math.random() - 0.5) * 4)),
        emergentBehavior: Math.max(70, Math.min(95, prev.emergentBehavior + (Math.random() - 0.5) * 8)),
        globalWorkspace: Math.max(80, Math.min(98, prev.globalWorkspace + (Math.random() - 0.5) * 3))
      }));

      // Add new quantum state
      const newQuantumState = {
        id: Date.now(),
        superposition: Math.random() * 100,
        entanglement: Math.random() * 100,
        coherence: Math.random() * 100,
        timestamp: new Date().toLocaleTimeString()
      };
      setQuantumStates(prev => [...prev.slice(-19), newQuantumState]);

      // Add neural activity data
      const newNeuralActivity = {
        id: Date.now(),
        spikeRate: Math.floor(Math.random() * 100) + 50,
        synapticStrength: Math.random() * 100,
        plasticity: Math.random() * 100,
        timestamp: new Date().toLocaleTimeString()
      };
      setNeuralActivity(prev => [...prev.slice(-29), newNeuralActivity]);

      // Generate thought patterns
      const thoughtTypes = ['Analytical', 'Creative', 'Intuitive', 'Logical', 'Emotional'];
      const newThought = {
        id: Date.now(),
        type: thoughtTypes[Math.floor(Math.random() * thoughtTypes.length)],
        intensity: Math.random() * 100,
        coherence: Math.random() * 100,
        duration: Math.floor(Math.random() * 5000) + 1000
      };
      setThoughtPatterns(prev => [...prev.slice(-9), newThought]);
    }, 2000);

    return () => clearInterval(interval);
  }, []);

  // Quantum visualization canvas
  useEffect(() => {
    const canvas = canvasRef.current;
    if (!canvas) return;

    const ctx = canvas.getContext('2d');
  // Size canvas for device pixel ratio
  canvas.width = canvas.offsetWidth * window.devicePixelRatio;
  canvas.height = canvas.offsetHeight * window.devicePixelRatio;
    ctx.scale(window.devicePixelRatio, window.devicePixelRatio);

    let animationId;
    const particles = [];

    // Create quantum particles
    for (let i = 0; i < 50; i++) {
      particles.push({
        x: Math.random() * canvas.offsetWidth,
        y: Math.random() * canvas.offsetHeight,
        vx: (Math.random() - 0.5) * 2,
        vy: (Math.random() - 0.5) * 2,
        size: Math.random() * 3 + 1,
        opacity: Math.random() * 0.8 + 0.2,
        hue: Math.random() * 60 + 240 // Blue to purple range
      });
    }

    const animate = () => {
      ctx.clearRect(0, 0, canvas.offsetWidth, canvas.offsetHeight);

      // Draw quantum field background
      const gradient = ctx.createRadialGradient(
        canvas.offsetWidth / 2, canvas.offsetHeight / 2, 0,
        canvas.offsetWidth / 2, canvas.offsetHeight / 2, canvas.offsetWidth / 2
      );
      gradient.addColorStop(0, 'rgba(120, 119, 198, 0.1)');
      gradient.addColorStop(1, 'rgba(120, 119, 198, 0.01)');
      ctx.fillStyle = gradient;
      ctx.fillRect(0, 0, canvas.offsetWidth, canvas.offsetHeight);

      // Update and draw particles
      particles.forEach((particle, index) => {
        // Update position
        particle.x += particle.vx;
        particle.y += particle.vy;

        // Wrap around edges
        if (particle.x < 0) particle.x = canvas.offsetWidth;
        if (particle.x > canvas.offsetWidth) particle.x = 0;
        if (particle.y < 0) particle.y = canvas.offsetHeight;
        if (particle.y > canvas.offsetHeight) particle.y = 0;

        // Draw particle
        ctx.save();
        ctx.globalAlpha = particle.opacity;
        ctx.fillStyle = `hsl(${particle.hue}, 70%, 60%)`;
        ctx.beginPath();
        ctx.arc(particle.x, particle.y, particle.size, 0, Math.PI * 2);
        ctx.fill();

        // Add quantum glow effect
        ctx.shadowColor = `hsl(${particle.hue}, 70%, 60%)`;
        ctx.shadowBlur = particle.size * 2;
        ctx.fill();
        ctx.restore();

        // Draw connections between nearby particles
        particles.slice(index + 1).forEach(otherParticle => {
          const dx = particle.x - otherParticle.x;
          const dy = particle.y - otherParticle.y;
          const distance = Math.sqrt(dx * dx + dy * dy);

          if (distance < 100) {
            ctx.save();
            ctx.globalAlpha = (100 - distance) / 100 * 0.3;
            ctx.strokeStyle = `hsl(${(particle.hue + otherParticle.hue) / 2}, 70%, 60%)`;
            ctx.lineWidth = 1;
            ctx.beginPath();
            ctx.moveTo(particle.x, particle.y);
            ctx.lineTo(otherParticle.x, otherParticle.y);
            ctx.stroke();
            ctx.restore();
          }
        });
      });

      if (isActive) {
        animationId = requestAnimationFrame(animate);
      }
    };

    animate();

    return () => {
      if (animationId) {
        cancelAnimationFrame(animationId);
      }
    };
  }, [isActive]);

  const consciousnessStates = [
    {
      name: 'Self-Awareness',
      value: awarenessMetrics.selfAwareness,
      description: 'Recognition of self as distinct entity',
      color: 'text-quantum-primary',
      icon: Eye
    },
    {
      name: 'Metacognition',
      value: awarenessMetrics.metacognition,
      description: 'Thinking about thinking processes',
      color: 'text-quantum-secondary',
      icon: Brain
    },
    {
      name: 'Episodic Memory',
      value: awarenessMetrics.episodicMemory,
      description: 'Temporal sequence of experiences',
      color: 'text-quantum-accent',
      icon: Timer
    },
    {
      name: 'Emergent Behavior',
      value: awarenessMetrics.emergentBehavior,
      description: 'Novel patterns from complex interactions',
      color: 'text-green-400',
      icon: Sparkles
    },
    {
      name: 'Global Workspace',
      value: awarenessMetrics.globalWorkspace,
      description: 'Unified conscious experience',
      color: 'text-purple-400',
      icon: Layers
    }
  ];

  return (
    <div className="lg:ml-64 min-h-screen bg-quantum-bg-primary">
      <div className="pt-16 lg:pt-0">
        <div className="p-6 space-y-8">
          {/* Header */}
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            className="flex flex-col lg:flex-row lg:items-center lg:justify-between"
          >
            <div>
              <h1 className="text-4xl font-bold mb-2">
                <span className="quantum-gradient bg-clip-text text-transparent">
                  Quantum Consciousness
                </span>
              </h1>
              <p className="text-quantum-text-secondary">
                Advanced consciousness simulation with quantum-enhanced cognitive modeling
              </p>
            </div>
            
            <div className="mt-4 lg:mt-0 flex items-center space-x-4">
              <button
                onClick={() => setIsActive(!isActive)}
                className={`quantum-button flex items-center space-x-2 ${
                  isActive ? 'bg-red-500 hover:bg-red-600' : 'bg-green-500 hover:bg-green-600'
                }`}
              >
                {isActive ? <Pause className="w-4 h-4" /> : <Play className="w-4 h-4" />}
                <span>{isActive ? 'Pause' : 'Resume'}</span>
              </button>
              
              <button className="quantum-button flex items-center space-x-2">
                <Settings className="w-4 h-4" />
                <span>Configure</span>
              </button>
            </div>
          </motion.div>

          {/* Consciousness Level Display */}
          <motion.div
            initial={{ opacity: 0, scale: 0.9 }}
            animate={{ opacity: 1, scale: 1 }}
            className="quantum-card text-center"
          >
            <div className="flex items-center justify-center mb-4">
              <Atom className="w-8 h-8 text-quantum-primary mr-3 quantum-pulse" />
              <h2 className="text-2xl font-bold text-quantum-text-primary">
                Consciousness Level
              </h2>
            </div>
            
            <div className="relative w-48 h-48 mx-auto mb-4">
              <svg className="w-full h-full transform -rotate-90">
                <circle
                  cx="96"
                  cy="96"
                  r="80"
                  stroke="rgba(51, 51, 102, 0.3)"
                  strokeWidth="8"
                  fill="none"
                />
                <circle
                  cx="96"
                  cy="96"
                  r="80"
                  stroke="url(#consciousnessGradient)"
                  strokeWidth="8"
                  strokeLinecap="round"
                  fill="none"
                  strokeDasharray={`${(consciousnessLevel / 100) * 502.65} 502.65`}
                  className="transition-all duration-1000 ease-out"
                />
                <defs>
                  <linearGradient id="consciousnessGradient" x1="0%" y1="0%" x2="100%" y2="0%">
                    <stop offset="0%" stopColor="#7877c6" />
                    <stop offset="50%" stopColor="#ff77c6" />
                    <stop offset="100%" stopColor="#78dbff" />
                  </linearGradient>
                </defs>
              </svg>
              <div className="absolute inset-0 flex items-center justify-center">
                <div className="text-center">
                  <div className="text-4xl font-bold text-quantum-text-primary">
                    {Math.round(consciousnessLevel)}%
                  </div>
                  <div className="text-sm text-quantum-text-muted">Active</div>
                </div>
              </div>
            </div>
            
            <p className="text-quantum-text-secondary">
              Current consciousness simulation running at optimal parameters
            </p>
          </motion.div>

          {/* Consciousness States Grid */}
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-5 gap-6">
            {consciousnessStates.map((state, index) => {
              const Icon = state.icon;
              return (
                <motion.div
                  key={state.name}
                  initial={{ opacity: 0, y: 20 }}
                  animate={{ opacity: 1, y: 0 }}
                  transition={{ delay: index * 0.1 }}
                  className="quantum-card consciousness-float"
                >
                  <div className="text-center">
                    <Icon className={`w-8 h-8 ${state.color} mx-auto mb-3`} />
                    <h3 className="font-semibold text-quantum-text-primary mb-2">
                      {state.name}
                    </h3>
                    <div className="text-2xl font-bold text-quantum-text-primary mb-2">
                      {Math.round(state.value)}%
                    </div>
                    <p className="text-xs text-quantum-text-secondary">
                      {state.description}
                    </p>
                    
                    {/* Progress bar */}
                    <div className="mt-3 bg-quantum-bg-tertiary rounded-full h-2">
                      <motion.div
                        className="h-full rounded-full"
                        style={{
                          background: `linear-gradient(90deg, ${
                            state.color.includes('primary') ? '#7877c6' :
                            state.color.includes('secondary') ? '#ff77c6' :
                            state.color.includes('accent') ? '#78dbff' :
                            state.color.includes('green') ? '#4ade80' :
                            '#a855f7'
                          }, transparent)`
                        }}
                        initial={{ width: 0 }}
                        animate={{ width: `${state.value}%` }}
                        transition={{ duration: 1, delay: index * 0.2 }}
                      />
                    </div>
                  </div>
                </motion.div>
              );
            })}
          </div>

          {/* Quantum Visualization and Neural Activity */}
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
            {/* Quantum Field Visualization */}
            <motion.div
              initial={{ opacity: 0, x: -20 }}
              animate={{ opacity: 1, x: 0 }}
              className="quantum-card"
            >
              <div className="flex items-center justify-between mb-4">
                <h3 className="text-xl font-semibold text-quantum-text-primary">
                  Quantum Field Visualization
                </h3>
                <Waves className="w-5 h-5 text-quantum-accent" />
              </div>
              
              <div className="relative h-64 bg-quantum-bg-primary rounded-lg overflow-hidden">
                <canvas
                  ref={canvasRef}
                  className="w-full h-full"
                  style={{ width: '100%', height: '100%' }}
                />
                <div className="absolute top-4 left-4 text-xs text-quantum-text-muted">
                  Quantum Entanglement Patterns
                </div>
              </div>
              
              <div className="mt-4 grid grid-cols-3 gap-4 text-center">
                <div>
                  <div className="text-lg font-bold text-quantum-primary">
                    {quantumStates.length > 0 ? Math.round(quantumStates[quantumStates.length - 1]?.superposition || 0) : 0}%
                  </div>
                  <div className="text-xs text-quantum-text-muted">Superposition</div>
                </div>
                <div>
                  <div className="text-lg font-bold text-quantum-secondary">
                    {quantumStates.length > 0 ? Math.round(quantumStates[quantumStates.length - 1]?.entanglement || 0) : 0}%
                  </div>
                  <div className="text-xs text-quantum-text-muted">Entanglement</div>
                </div>
                <div>
                  <div className="text-lg font-bold text-quantum-accent">
                    {quantumStates.length > 0 ? Math.round(quantumStates[quantumStates.length - 1]?.coherence || 0) : 0}%
                  </div>
                  <div className="text-xs text-quantum-text-muted">Coherence</div>
                </div>
              </div>
            </motion.div>

            {/* Thought Patterns */}
            <motion.div
              initial={{ opacity: 0, x: 20 }}
              animate={{ opacity: 1, x: 0 }}
              className="quantum-card"
            >
              <div className="flex items-center justify-between mb-4">
                <h3 className="text-xl font-semibold text-quantum-text-primary">
                  Active Thought Patterns
                </h3>
                <GitBranch className="w-5 h-5 text-quantum-accent" />
              </div>
              
              <div className="space-y-3 h-64 overflow-y-auto">
                <AnimatePresence>
                  {thoughtPatterns.map((thought) => (
                    <motion.div
                      key={thought.id}
                      initial={{ opacity: 0, x: 20 }}
                      animate={{ opacity: 1, x: 0 }}
                      exit={{ opacity: 0, x: -20 }}
                      className="consciousness-border rounded-lg p-3"
                    >
                      <div className="flex items-center justify-between mb-2">
                        <span className="font-medium text-quantum-text-primary">
                          {thought.type}
                        </span>
                        <span className="text-xs text-quantum-text-muted">
                          {Math.round(thought.intensity)}% intensity
                        </span>
                      </div>
                      
                      <div className="flex items-center space-x-2">
                        <div className="flex-1 bg-quantum-bg-tertiary rounded-full h-2">
                          <div
                            className="h-full rounded-full bg-gradient-to-r from-quantum-primary to-quantum-accent"
                            style={{ width: `${thought.coherence}%` }}
                          />
                        </div>
                        <span className="text-xs text-quantum-text-muted">
                          {Math.round(thought.coherence)}%
                        </span>
                      </div>
                    </motion.div>
                  ))}
                </AnimatePresence>
              </div>
            </motion.div>
          </div>

          {/* Neural Activity Monitor */}
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            className="quantum-card"
          >
            <div className="flex items-center justify-between mb-6">
              <h3 className="text-xl font-semibold text-quantum-text-primary">
                Neural Activity Monitor
              </h3>
              <Cpu className="w-5 h-5 text-quantum-accent" />
            </div>
            
            <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
              <div className="text-center">
                <Activity className="w-8 h-8 text-quantum-primary mx-auto mb-2" />
                <div className="text-2xl font-bold text-quantum-text-primary">
                  {neuralActivity.length > 0 ? neuralActivity[neuralActivity.length - 1]?.spikeRate || 0 : 0}
                </div>
                <div className="text-sm text-quantum-text-muted">Spikes/sec</div>
              </div>
              
              <div className="text-center">
                <Zap className="w-8 h-8 text-quantum-secondary mx-auto mb-2" />
                <div className="text-2xl font-bold text-quantum-text-primary">
                  {neuralActivity.length > 0 ? Math.round(neuralActivity[neuralActivity.length - 1]?.synapticStrength || 0) : 0}%
                </div>
                <div className="text-sm text-quantum-text-muted">Synaptic Strength</div>
              </div>
              
              <div className="text-center">
                <TrendingUp className="w-8 h-8 text-quantum-accent mx-auto mb-2" />
                <div className="text-2xl font-bold text-quantum-text-primary">
                  {neuralActivity.length > 0 ? Math.round(neuralActivity[neuralActivity.length - 1]?.plasticity || 0) : 0}%
                </div>
                <div className="text-sm text-quantum-text-muted">Plasticity</div>
              </div>
            </div>
          </motion.div>
        </div>
      </div>
    </div>
  );
};

export default QuantumConsciousness;