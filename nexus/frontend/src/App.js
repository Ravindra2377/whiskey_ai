import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { QueryClient, QueryClientProvider } from 'react-query';
import { Toaster } from 'react-hot-toast';
import Navigation from './components/Navigation/Navigation';
import Prompt from './pages/Prompt/Prompt';
import SystemStatus from './pages/SystemStatus/SystemStatus';
import './index.css';

// Create a client for React Query
const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      retry: 3,
      staleTime: 5 * 60 * 1000, // 5 minutes
      cacheTime: 10 * 60 * 1000, // 10 minutes
    },
  },
});

function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <Router>
        <div className="min-h-screen bg-quantum-bg-primary">
          {/* Quantum Background Effects */}
          <div className="fixed inset-0 pointer-events-none z-0">
            <div className="absolute inset-0 opacity-30">
              <div className="quantum-particle particle-1"></div>
              <div className="quantum-particle particle-2"></div>
              <div className="quantum-particle particle-3"></div>
              <div className="quantum-particle particle-4"></div>
              <div className="quantum-particle particle-5"></div>
            </div>
          </div>

          {/* Main Application */}
          <div className="relative z-10">
            <Navigation />
            
            <main className="min-h-screen">
              <Routes>
                <Route path="/" element={<Prompt />} />
                <Route path="/prompt" element={<Prompt />} />
                <Route path="/system-status" element={<SystemStatus />} />
              </Routes>
            </main>
          </div>

          {/* Toast Notifications */}
          <Toaster
            position="top-right"
            toastOptions={{
              duration: 4000,
              style: {
                background: 'rgba(26, 26, 46, 0.9)',
                color: '#ffffff',
                border: '1px solid #333366',
                backdropFilter: 'blur(10px)',
              },
              success: {
                iconTheme: {
                  primary: '#78dbff',
                  secondary: '#ffffff',
                },
              },
              error: {
                iconTheme: {
                  primary: '#ff6b6b',
                  secondary: '#ffffff',
                },
              },
            }}
          />
        </div>
      </Router>
    </QueryClientProvider>
  );
}

export default App;