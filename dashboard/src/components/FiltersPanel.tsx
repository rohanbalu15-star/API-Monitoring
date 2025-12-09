interface FiltersPanelProps {
  filters: {
    serviceName: string;
    endpoint: string;
    slowApi: boolean;
    brokenApi: boolean;
    rateLimitHit: boolean;
  };
  onFilterChange: (key: string, value: any) => void;
  onApply: () => void;
  onReset: () => void;
}

export default function FiltersPanel({
  filters,
  onFilterChange,
  onApply,
  onReset,
}: FiltersPanelProps) {
  return (
    <div className="bg-white dark:bg-gray-800 rounded-lg shadow p-6">
      <h3 className="text-lg font-medium text-gray-900 dark:text-white mb-4">Filters</h3>

      <div className="space-y-4">
        <div>
          <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
            Service Name
          </label>
          <input
            type="text"
            value={filters.serviceName}
            onChange={(e) => onFilterChange('serviceName', e.target.value)}
            className="w-full px-3 py-2 border border-gray-300 dark:border-gray-700 rounded-md shadow-sm focus:ring-primary focus:border-primary dark:bg-gray-900 dark:text-white"
            placeholder="Enter service name"
          />
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
            Endpoint
          </label>
          <input
            type="text"
            value={filters.endpoint}
            onChange={(e) => onFilterChange('endpoint', e.target.value)}
            className="w-full px-3 py-2 border border-gray-300 dark:border-gray-700 rounded-md shadow-sm focus:ring-primary focus:border-primary dark:bg-gray-900 dark:text-white"
            placeholder="Enter endpoint"
          />
        </div>

        <div className="space-y-2">
          <label className="flex items-center">
            <input
              type="checkbox"
              checked={filters.slowApi}
              onChange={(e) => onFilterChange('slowApi', e.target.checked)}
              className="h-4 w-4 text-primary focus:ring-primary border-gray-300 dark:border-gray-700 rounded"
            />
            <span className="ml-2 text-sm text-gray-700 dark:text-gray-300">
              Slow APIs (&gt; 500ms)
            </span>
          </label>

          <label className="flex items-center">
            <input
              type="checkbox"
              checked={filters.brokenApi}
              onChange={(e) => onFilterChange('brokenApi', e.target.checked)}
              className="h-4 w-4 text-primary focus:ring-primary border-gray-300 dark:border-gray-700 rounded"
            />
            <span className="ml-2 text-sm text-gray-700 dark:text-gray-300">
              Broken APIs (5xx)
            </span>
          </label>

          <label className="flex items-center">
            <input
              type="checkbox"
              checked={filters.rateLimitHit}
              onChange={(e) => onFilterChange('rateLimitHit', e.target.checked)}
              className="h-4 w-4 text-primary focus:ring-primary border-gray-300 dark:border-gray-700 rounded"
            />
            <span className="ml-2 text-sm text-gray-700 dark:text-gray-300">
              Rate Limit Hits
            </span>
          </label>
        </div>

        <div className="flex gap-2 pt-4">
          <button
            onClick={onApply}
            className="flex-1 bg-primary text-white px-4 py-2 rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-primary"
          >
            Apply Filters
          </button>
          <button
            onClick={onReset}
            className="flex-1 bg-gray-200 dark:bg-gray-700 text-gray-700 dark:text-gray-300 px-4 py-2 rounded-md hover:bg-gray-300 dark:hover:bg-gray-600 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-gray-500"
          >
            Reset
          </button>
        </div>
      </div>
    </div>
  );
}
