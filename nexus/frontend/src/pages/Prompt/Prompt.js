import React, { useState } from 'react';
import { motion } from 'framer-motion';
import { Send, Settings, Activity } from 'lucide-react';
import api from '../../lib/api';

const Prompt = () => {
  const [prompt, setPrompt] = useState('');
  const [messages, setMessages] = useState([]);
  const [isLoading, setIsLoading] = useState(false);

  // Curated, maintenance-friendly prompt suggestions
  const suggestions = [
    {
      label: 'Build an AI named Whiskey (MVP plan)',
      text:
        'I want to build an AI named Whiskey. Draft core features, personality, and a minimal MVP roadmap with milestones, risks, and success metrics.'
    },
    {
      label: 'Check frontend↔backend↔DB links',
      text:
        'Check the frontend-backend-database connections for Nexus. Identify issues and list concrete steps to fix them.'
    },
    {
      label: 'Fix Docker context & services',
      text:
        'Diagnose Docker context/compose issues for this project and provide exact steps to fix and restart all services.'
    },
    {
      label: 'PostgreSQL setup (dev profile)',
      text:
        'Verify local PostgreSQL for the dev profile. Create the required schema/tables and seed minimal data based on database_schema.sql.'
    },
    {
      label: 'Create demo Spring endpoint',
      text:
        'Generate a Spring Boot endpoint at /api/v1/demo/echo that returns JSON {"message":"<input>","timestamp":<iso>}, plus a quick unit test.'
    },
    {
      label: 'Add System Logs page (React)',
      text:
        'Add a System Logs page in the React app using React Query to fetch /api/logs (mock if missing) with filters and pagination.'
    },
    {
      label: 'Security hardening checklist',
      text:
        'Review security across backend and frontend. Provide a prioritized hardening checklist (auth, CORS, headers, secrets, logging, rate limits).'
    },
    {
      label: 'Performance tuning plan',
      text:
        'Create a performance optimization plan for the app (build sizes, caching, DB pool tuning, JVM flags, React profiling, CI/CD gates).'
    }
  ];

  const sendPrompt = async (userText) => {
    if (!userText || !userText.trim()) return;
    const text = userText.trim();
    const userMsg = { role: 'user', content: text, ts: Date.now() };
    setMessages((prev) => [...prev, userMsg]);
    setIsLoading(true);

    try {
      // Use public demo endpoint so backend connectivity works without auth in dev
      const { data } = await api.post('/api/v1/demo/echo', { message: text });
      const botMsg = { role: 'assistant', content: data?.message || 'Processed by backend.', ts: Date.now() };
      setMessages((prev) => [...prev, botMsg]);
    } catch (err) {
      const botMsg = {
        role: 'assistant',
        content: 'Backend not reachable right now. This is a local placeholder response for your prompt.',
        ts: Date.now(),
      };
      setMessages((prev) => [...prev, botMsg]);
    } finally {
      setIsLoading(false);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!prompt.trim()) return;
    setPrompt('');
    sendPrompt(prompt);
  };

  const handleSuggestionClick = (e, text) => {
    if (isLoading) return;
    if (e.shiftKey) {
      // Shift+Click to send immediately
      setPrompt('');
      sendPrompt(text);
    } else {
      // Click to insert into input
      setPrompt(text);
    }
  };

  return (
    <div className="lg:ml-64 min-h-screen bg-quantum-bg-primary">
      <div className="pt-16 lg:pt-0">
        <div className="p-6 space-y-8">
          <motion.div initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }}>
            <h1 className="text-4xl font-bold mb-2 text-quantum-text-primary">NEXUS AI Prompt</h1>
            <p className="text-quantum-text-secondary">Ask anything. Minimalist prompt console with maintenance-friendly design.</p>
          </motion.div>

          {/* Prompt Console */}
          <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
            <div className="lg:col-span-2 quantum-card">
              <div className="space-y-4">
                {/* Suggested Prompts */}
                <div>
                  <div className="flex items-center justify-between mb-2">
                    <div className="text-xs text-quantum-text-muted">Suggested prompts</div>
                    <div className="text-[10px] text-quantum-text-muted">Tip: Shift+Click to send</div>
                  </div>
                  <div className="flex flex-wrap gap-2">
                    {suggestions.map((s, idx) => (
                      <button
                        key={idx}
                        type="button"
                        onClick={(e) => handleSuggestionClick(e, s.text)}
                        className="px-3 py-2 rounded-lg border border-quantum-border bg-quantum-bg-tertiary/50 text-quantum-text-primary hover:bg-quantum-bg-tertiary/70 transition-colors text-xs"
                        title={s.text}
                      >
                        {s.label}
                      </button>
                    ))}
                  </div>
                </div>
                <div className="h-80 overflow-y-auto pr-2">
                  {messages.length === 0 ? (
                    <div className="h-full flex items-center justify-center text-quantum-text-muted text-sm">
                      No messages yet. Type a prompt below to get started.
                    </div>
                  ) : (
                    <div className="space-y-3">
                      {messages.map((m, idx) => (
                        <div key={idx} className={`p-3 rounded-lg ${m.role === 'user' ? 'bg-quantum-bg-tertiary/60' : 'bg-quantum-primary/10'}`}>
                          <div className="text-xs text-quantum-text-muted mb-1">{m.role === 'user' ? 'You' : 'NEXUS AI'}</div>
                          <div className="text-sm text-quantum-text-primary whitespace-pre-wrap">{m.content}</div>
                        </div>
                      ))}
                    </div>
                  )}
                </div>

                <form onSubmit={handleSubmit} className="flex items-center space-x-2">
                  <input
                    type="text"
                    className="flex-1 px-4 py-3 rounded-lg bg-quantum-bg-tertiary/60 border border-quantum-border text-quantum-text-primary focus:outline-none focus:ring-2 focus:ring-quantum-primary/40"
                    placeholder="Type your prompt..."
                    value={prompt}
                    onChange={(e) => setPrompt(e.target.value)}
                    disabled={isLoading}
                  />
                  <button type="submit" disabled={isLoading} className="quantum-button flex items-center space-x-2">
                    <Send className="w-4 h-4" />
                    <span>{isLoading ? 'Sending…' : 'Send'}</span>
                  </button>
                </form>
              </div>
            </div>

            {/* Maintenance panel */}
            <div className="quantum-card">
              <h3 className="text-lg font-semibold text-quantum-text-primary mb-4">Maintenance</h3>
              <div className="space-y-3 text-sm">
                <div className="flex items-center justify-between p-3 consciousness-border rounded-lg">
                  <span className="text-quantum-text-secondary">Status</span>
                  <span className="flex items-center space-x-2 text-green-400"><Activity className="w-4 h-4" /> <span>Operational</span></span>
                </div>
                <button type="button" className="w-full p-3 rounded-lg border border-quantum-border text-quantum-text-primary hover:bg-quantum-bg-tertiary/60 flex items-center justify-center space-x-2">
                  <Settings className="w-4 h-4" />
                  <span>Clear cache (coming soon)</span>
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Prompt;
