'use client';

import { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import { isAuthenticated, removeToken } from '@/lib/auth';
import { logsApi, incidentsApi, analyticsApi } from '@/lib/api';
import StatsCard from '@/components/StatsCard';
import LogsTable from '@/components/LogsTable';
import AlertsList from '@/components/AlertsList';
import IncidentsList from '@/components/IncidentsList';
import FiltersPanel from '@/components/FiltersPanel';
import type { ApiLog, Alert, Incident, Stats } from '@/types';

export default function DashboardPage() {
  const router = useRouter();
  const [activeTab, setActiveTab] = useState<'overview' | 'logs' | 'alerts' | 'incidents'>('overview');
  const [logs, setLogs] = useState<ApiLog[]>([]);
  const [alerts, setAlerts] = useState<Alert[]>([]);
  const [incidents, setIncidents] = useState<Incident[]>([]);
  const [stats, setStats] = useState<Stats>({ slowApiCount: 0, brokenApiCount: 0, rateLimitViolations: 0 });
  const [topSlowEndpoints, setTopSlowEndpoints] = useState<any[]>([]);
  const [loading, setLoading] = useState(false);
  const [filters, setFilters] = useState({
    serviceName: '',
    endpoint: '',
    slowApi: false,
    brokenApi: false,
    rateLimitHit: false,
  });

  useEffect(() => {
    if (!isAuthenticated()) {
      router.push('/login');
      return;
    }

    loadData();
  }, [router]);

  const loadData = async () => {
    setLoading(true);
    try {
      const [logsData, alertsData, incidentsData, statsData, topSlow] = await Promise.all([
        logsApi.getLogs(),
        logsApi.getAlerts(),
        incidentsApi.getAll(),
        logsApi.getStats(),
        analyticsApi.getTopSlowEndpoints(5),
      ]);

      setLogs(logsData);
      setAlerts(alertsData);
      setIncidents(incidentsData);
      setStats(statsData);
      setTopSlowEndpoints(topSlow);
    } catch (error) {
      console.error('Error loading data:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleFilterChange = (key: string, value: any) => {
    setFilters((prev) => ({ ...prev, [key]: value }));
  };

  const handleApplyFilters = async () => {
    setLoading(true);
    try {
      const filteredLogs = await logsApi.getLogs({
        serviceName: filters.serviceName || undefined,
        endpoint: filters.endpoint || undefined,
        slowApi: filters.slowApi,
        brokenApi: filters.brokenApi,
        rateLimitHit: filters.rateLimitHit,
      });
      setLogs(filteredLogs);
    } catch (error) {
      console.error('Error applying filters:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleResetFilters = () => {
    setFilters({
      serviceName: '',
      endpoint: '',
      slowApi: false,
      brokenApi: false,
      rateLimitHit: false,
    });
    loadData();
  };

  const handleResolveIncident = async (id: string) => {
    setLoading(true);
    try {
      await incidentsApi.resolve(id);
      const updatedIncidents = await incidentsApi.getAll();
      setIncidents(updatedIncidents);
    } catch (error) {
      console.error('Error resolving incident:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleLogout = () => {
    removeToken();
    router.push('/login');
  };

  if (!isAuthenticated()) {
    return null;
  }

  return (
    <div className="min-h-screen bg-gray-50 dark:bg-gray-900">
      <nav className="bg-white dark:bg-gray-800 shadow-sm">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between h-16">
            <div className="flex items-center">
              <h1 className="text-xl font-bold text-gray-900 dark:text-white">
                API Monitoring Dashboard
              </h1>
            </div>
            <div className="flex items-center space-x-4">
              <button
                onClick={loadData}
                disabled={loading}
                className="text-sm text-gray-700 dark:text-gray-300 hover:text-primary disabled:opacity-50"
              >
                {loading ? 'Refreshing...' : 'Refresh'}
              </button>
              <button
                onClick={handleLogout}
                className="text-sm text-gray-700 dark:text-gray-300 hover:text-danger"
              >
                Logout
              </button>
            </div>
          </div>
        </div>
      </nav>

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="mb-6">
          <div className="border-b border-gray-200 dark:border-gray-700">
            <nav className="-mb-px flex space-x-8">
              {(['overview', 'logs', 'alerts', 'incidents'] as const).map((tab) => (
                <button
                  key={tab}
                  onClick={() => setActiveTab(tab)}
                  className={`${
                    activeTab === tab
                      ? 'border-primary text-primary'
                      : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300 dark:text-gray-400 dark:hover:text-gray-300'
                  } whitespace-nowrap py-4 px-1 border-b-2 font-medium text-sm capitalize`}
                >
                  {tab}
                </button>
              ))}
            </nav>
          </div>
        </div>

        {activeTab === 'overview' && (
          <div className="space-y-6">
            <div className="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-3">
              <StatsCard
                title="Slow APIs"
                value={stats.slowApiCount}
                icon={<span className="text-2xl">🐌</span>}
                color="yellow"
              />
              <StatsCard
                title="Broken APIs"
                value={stats.brokenApiCount}
                icon={<span className="text-2xl">❌</span>}
                color="red"
              />
              <StatsCard
                title="Rate Limit Violations"
                value={stats.rateLimitViolations}
                icon={<span className="text-2xl">⚠️</span>}
                color="blue"
              />
            </div>

            <div className="bg-white dark:bg-gray-800 rounded-lg shadow p-6">
              <h2 className="text-lg font-medium text-gray-900 dark:text-white mb-4">
                Top 5 Slow Endpoints
              </h2>
              <div className="space-y-3">
                {topSlowEndpoints.length === 0 ? (
                  <p className="text-gray-500 dark:text-gray-400">No slow endpoints found</p>
                ) : (
                  topSlowEndpoints.map((endpoint, index) => (
                    <div
                      key={index}
                      className="flex justify-between items-center p-3 bg-gray-50 dark:bg-gray-900 rounded"
                    >
                      <div>
                        <p className="text-sm font-medium text-gray-900 dark:text-white">
                          {endpoint._id?.endpoint || 'Unknown'}
                        </p>
                        <p className="text-xs text-gray-500 dark:text-gray-400">
                          {endpoint._id?.serviceName || 'Unknown Service'}
                        </p>
                      </div>
                      <div className="text-right">
                        <p className="text-sm font-semibold text-yellow-600 dark:text-yellow-400">
                          {Math.round(endpoint.avgLatency)}ms avg
                        </p>
                        <p className="text-xs text-gray-500 dark:text-gray-400">
                          {endpoint.count} calls
                        </p>
                      </div>
                    </div>
                  ))
                )}
              </div>
            </div>

            <div className="bg-white dark:bg-gray-800 rounded-lg shadow p-6">
              <h2 className="text-lg font-medium text-gray-900 dark:text-white mb-4">
                Recent Alerts
              </h2>
              <AlertsList alerts={alerts.slice(0, 5)} />
            </div>
          </div>
        )}

        {activeTab === 'logs' && (
          <div className="grid grid-cols-1 lg:grid-cols-4 gap-6">
            <div className="lg:col-span-1">
              <FiltersPanel
                filters={filters}
                onFilterChange={handleFilterChange}
                onApply={handleApplyFilters}
                onReset={handleResetFilters}
              />
            </div>
            <div className="lg:col-span-3 bg-white dark:bg-gray-800 rounded-lg shadow">
              <div className="p-6">
                <h2 className="text-lg font-medium text-gray-900 dark:text-white mb-4">
                  API Request Logs
                </h2>
                <LogsTable logs={logs} />
              </div>
            </div>
          </div>
        )}

        {activeTab === 'alerts' && (
          <div className="bg-white dark:bg-gray-800 rounded-lg shadow p-6">
            <h2 className="text-lg font-medium text-gray-900 dark:text-white mb-4">
              All Alerts
            </h2>
            <AlertsList alerts={alerts} />
          </div>
        )}

        {activeTab === 'incidents' && (
          <div className="bg-white dark:bg-gray-800 rounded-lg shadow p-6">
            <h2 className="text-lg font-medium text-gray-900 dark:text-white mb-4">
              Incidents Management
            </h2>
            <IncidentsList
              incidents={incidents}
              onResolve={handleResolveIncident}
              loading={loading}
            />
          </div>
        )}
      </div>
    </div>
  );
}
