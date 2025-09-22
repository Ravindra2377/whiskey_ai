/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./src/**/*.{js,jsx,ts,tsx}",
    "./public/index.html"
  ],
  theme: {
    extend: {
      colors: {
        quantum: {
          primary: '#7877c6',
          secondary: '#ff77c6',
          accent: '#78dbff',
          bg: {
            primary: '#0f0f23',
            secondary: '#1a1a2e',
            tertiary: '#16213e'
          },
          text: {
            primary: '#ffffff',
            secondary: '#b0b0d0',
            muted: '#808080'
          },
          border: '#333366'
        }
      },
      fontFamily: {
        sans: ['Inter', 'system-ui', 'sans-serif'],
        mono: ['JetBrains Mono', 'monospace']
      },
      animation: {
        'quantum-pulse': 'quantumPulse 2s ease-in-out infinite',
        'consciousness-float': 'consciousnessFloat 6s ease-in-out infinite',
        'neuromorphic-rotate': 'neuromorphicRotate 20s linear infinite',
        'quantum-shimmer': 'shimmer 2s infinite',
        'quantum-spin': 'quantumSpin 1s linear infinite',
        'ripple': 'ripple 2s infinite'
      },
      keyframes: {
        quantumPulse: {
          '0%, 100%': { opacity: '1', transform: 'scale(1)' },
          '50%': { opacity: '0.7', transform: 'scale(1.05)' }
        },
        consciousnessFloat: {
          '0%, 100%': { transform: 'translateY(0px)' },
          '50%': { transform: 'translateY(-10px)' }
        },
        neuromorphicRotate: {
          'from': { transform: 'rotate(0deg)' },
          'to': { transform: 'rotate(360deg)' }
        },
        shimmer: {
          '0%': { backgroundPosition: '-200% 0' },
          '100%': { backgroundPosition: '200% 0' }
        },
        quantumSpin: {
          '0%': { transform: 'rotate(0deg)' },
          '100%': { transform: 'rotate(360deg)' }
        },
        ripple: {
          '0%': { width: '0', height: '0', opacity: '1' },
          '100%': { width: '300px', height: '300px', opacity: '0' }
        }
      },
      backdropBlur: {
        xs: '2px'
      },
      boxShadow: {
        'quantum': '0 0 20px rgba(120, 119, 198, 0.3)',
        'neuromorphic': '0 4px 8px rgba(0, 0, 0, 0.3), 0 2px 4px rgba(120, 119, 198, 0.1)',
        'consciousness': '0 12px 30px rgba(120, 119, 198, 0.2)'
      }
    },
  },
  plugins: [],
}