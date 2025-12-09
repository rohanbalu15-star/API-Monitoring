import { format } from 'date-fns';
import type { Alert } from '@/types';

interface AlertsListProps {
  alerts: Alert[];
}

export default function AlertsList({ alerts }: AlertsListProps) {
  const getSeverityColor = (severity: string) => {
    switch (severity) {
      case 'critical':
        return 'bg-red-100 dark:bg-red-900/20 border-red-500 text-red-800 dark:text-red-400';
      case 'warning':
        return 'bg-yellow-100 dark:bg-yellow-900/20 border-yellow-500 text-yellow-800 dark:text-yellow-400';
      default:
        return 'bg-blue-100 dark:bg-blue-900/20 border-blue-500 text-blue-800 dark:text-blue-400';
    }
  };

  const getAlertTypeIcon = (type: string) => {
    switch (type) {
      case 'SLOW_API':
        return '🐌';
      case 'BROKEN_API':
        return '❌';
      case 'RATE_LIMIT_HIT':
        return '⚠️';
      default:
        return '📊';
    }
  };

  return (
    <div className="space-y-4">
      {alerts.length === 0 ? (
        <p className="text-center text-gray-500 dark:text-gray-400 py-8">No alerts found</p>
      ) : (
        alerts.map((alert) => (
          <div
            key={alert.id}
            className={`border-l-4 p-4 rounded-r-lg ${getSeverityColor(alert.severity)}`}
          >
            <div className="flex items-start">
              <div className="flex-shrink-0 text-2xl mr-3">
                {getAlertTypeIcon(alert.alertType)}
              </div>
              <div className="flex-1">
                <h3 className="text-sm font-medium">
                  {alert.alertType.replace(/_/g, ' ')}
                </h3>
                <p className="mt-1 text-sm">{alert.message}</p>
                <div className="mt-2 text-xs opacity-75">
                  <span className="font-medium">{alert.serviceName}</span> - {alert.endpoint}
                </div>
                <div className="mt-1 text-xs opacity-75">
                  {format(new Date(alert.timestamp), 'MMM dd, yyyy HH:mm:ss')}
                </div>
              </div>
            </div>
          </div>
        ))
      )}
    </div>
  );
}
