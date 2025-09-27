import { useQuery } from 'react-query';
import api from '../lib/api';

const mockStatus = {
  health: 'Excellent',
  uptime: '99.9%',
  security: 'Secure',
  alerts: 0,
};

export function useSystemStatus() {
  return useQuery(
    ['system-status'],
    async () => {
      try {
        // Prefer Spring Boot actuator health if available
        const { data } = await api.get('/actuator/health');
        const status = typeof data?.status === 'string' ? data.status : null;
        return {
          health: status === 'UP' ? 'Excellent' : status || mockStatus.health,
          uptime: mockStatus.uptime,
          security: mockStatus.security,
          alerts: mockStatus.alerts,
        };
      } catch (e) {
        // Fallback to mock values if backend isn’t ready
        return mockStatus;
      }
    },
    {
      staleTime: 60_000,
      retry: 1,
    }
  );
}
