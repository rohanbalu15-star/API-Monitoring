export interface ApiLog {
  id: string;
  serviceName: string;
  endpoint: string;
  method: string;
  requestSize: number;
  responseSize: number;
  statusCode: number;
  timestamp: string;
  latencyMs: number;
  eventType: string;
}

export interface Alert {
  id: string;
  serviceName: string;
  endpoint: string;
  alertType: 'SLOW_API' | 'BROKEN_API' | 'RATE_LIMIT_HIT';
  message: string;
  timestamp: string;
  severity: string;
  metadata: Record<string, any>;
}

export interface Incident {
  id: string;
  serviceName: string;
  endpoint: string;
  incidentType: string;
  description: string;
  status: 'OPEN' | 'RESOLVED';
  createdAt: string;
  resolvedAt?: string;
  resolvedBy?: string;
}

export interface Stats {
  slowApiCount: number;
  brokenApiCount: number;
  rateLimitViolations: number;
}

export interface LoginRequest {
  username: string;
  password: string;
}

export interface LoginResponse {
  accessToken: string;
  tokenType: string;
  username: string;
}
