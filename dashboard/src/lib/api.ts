import axios from 'axios';
import type { ApiLog, Alert, Incident, Stats, LoginRequest, LoginResponse } from '@/types';

const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export const authApi = {
  login: async (credentials: LoginRequest): Promise<LoginResponse> => {
    const response = await api.post<LoginResponse>('/auth/login', credentials);
    return response.data;
  },

  register: async (credentials: LoginRequest & { email: string }) => {
    const response = await api.post('/auth/register', credentials);
    return response.data;
  },
};

export const logsApi = {
  getLogs: async (filters?: {
    serviceName?: string;
    endpoint?: string;
    startDate?: string;
    endDate?: string;
    statusCode?: number;
    slowApi?: boolean;
    brokenApi?: boolean;
    rateLimitHit?: boolean;
  }): Promise<ApiLog[]> => {
    const response = await api.get<ApiLog[]>('/logs', { params: filters });
    return response.data;
  },

  getAlerts: async (limit: number = 50): Promise<Alert[]> => {
    const response = await api.get<Alert[]>('/alerts', { params: { limit } });
    return response.data;
  },

  getStats: async (): Promise<Stats> => {
    const response = await api.get<Stats>('/stats');
    return response.data;
  },
};

export const incidentsApi = {
  getAll: async (): Promise<Incident[]> => {
    const response = await api.get<Incident[]>('/incidents');
    return response.data;
  },

  getOpen: async (): Promise<Incident[]> => {
    const response = await api.get<Incident[]>('/incidents/open');
    return response.data;
  },

  getResolved: async (): Promise<Incident[]> => {
    const response = await api.get<Incident[]>('/incidents/resolved');
    return response.data;
  },

  resolve: async (id: string): Promise<void> => {
    await api.put(`/incidents/${id}/resolve`);
  },
};

export const analyticsApi = {
  getAvgLatency: async () => {
    const response = await api.get('/analytics/avg-latency');
    return response.data;
  },

  getTopSlowEndpoints: async (limit: number = 5) => {
    const response = await api.get('/analytics/top-slow-endpoints', { params: { limit } });
    return response.data;
  },

  getErrorRate: async () => {
    const response = await api.get('/analytics/error-rate');
    return response.data;
  },

  getTimeline: async (hours: number = 24) => {
    const response = await api.get('/analytics/timeline', { params: { hours } });
    return response.data;
  },
};

export default api;
