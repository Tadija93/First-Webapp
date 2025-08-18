import React, { useEffect, useState } from "react";
import { getActivityLogsApi } from "./api/ActivityApiService";
import { useAuth } from "./security/AuthContext";

const ActivityLogList = () => {
  const [logs, setLogs] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  
  const authContext = useAuth();

  useEffect(() => {
    fetchActivityLogs();
  }, []);

  const fetchActivityLogs = async () => {
    try {
      setLoading(true);
      setError(null);
      
      console.log('Fetching activity logs...');
      console.log('Auth context:', { 
        isAuthenticated: authContext.isAuthenticated, 
        username: authContext.username 
      });
      console.log('Token in localStorage:', !!localStorage.getItem('token'));
      
      const response = await getActivityLogsApi();
      
      console.log('Success! Received data:', response.data);
      setLogs(Array.isArray(response.data) ? response.data : []);
      
    } catch (error) {
      console.error("Error fetching activity logs:", error);
      
      if (error.response) {
        const status = error.response.status;
        const errorData = error.response.data;
        
        if (status === 401) {
          setError(`Unauthorized: Please login again`);
        } else if (status === 403) {
          setError(`Forbidden: You don't have permission to access activity logs`);
        } else if (status === 404) {
          setError('Activity logs endpoint not found');
        } else if (status >= 500) {
          setError(`Server error: ${errorData?.message || 'Please try again later'}`);
        } else {
          setError(`Error ${status}: ${errorData?.message || 'Something went wrong'}`);
        }
      } else if (error.request) {
        setError('Network error: Could not connect to server. Is the backend running?');
      } else {
        setError(error.message || "Failed to fetch activity logs");
      }
    } finally {
      setLoading(false);
    }
  };

  const handleLogin = () => {
    window.location.href = '/login';
  };

  if (loading) {
    return (
      <div className="p-4">
        <h2 className="text-xl font-bold mb-4">Activity Logs</h2>
        <div className="flex items-center space-x-2">
          <div className="animate-spin rounded-full h-5 w-5 border-b-2 border-blue-600"></div>
          <p>Loading activity logs...</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="p-4">
        <h2 className="text-xl font-bold mb-4">Activity Logs</h2>
        <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded mb-4">
          <strong>Error: </strong>{error}
        </div>
        <div className="space-x-3 mb-4">
          <button 
            onClick={fetchActivityLogs}
            className="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600 transition-colors"
          >
            Retry
          </button>
          {error.includes('Unauthorized') && (
            <button 
              onClick={handleLogin}
              className="bg-green-500 text-white px-4 py-2 rounded hover:bg-green-600 transition-colors"
            >
              Login
            </button>
          )}
        </div>
        
        {/* Debug information */}
        <div className="mt-4 p-3 bg-gray-100 rounded text-sm">
          <details>
            <summary className="cursor-pointer font-medium">Debug Info (click to expand)</summary>
            <div className="mt-2 space-y-1">
              <p><strong>Authenticated:</strong> {authContext.isAuthenticated ? 'Yes' : 'No'}</p>
              <p><strong>Username:</strong> {authContext.username || 'Not set'}</p>
              <p><strong>Token found:</strong> {localStorage.getItem('token') ? 'Yes' : 'No'}</p>
              <p><strong>API URL:</strong> http://localhost:8082/activity</p>
              <p><strong>Current URL:</strong> {window.location.href}</p>
            </div>
          </details>
        </div>
      </div>
    );
  }

  return (
    <div className="p-4">
      <div className="flex justify-between items-center mb-4">
        <h2 className="text-xl font-bold">Activity Logs</h2>
        <button 
          onClick={fetchActivityLogs}
          className="bg-gray-500 text-white px-3 py-1 rounded text-sm hover:bg-gray-600 transition-colors"
        >
          Refresh
        </button>
      </div>
      
      {logs.length === 0 ? (
        <div className="text-center py-8">
          <p className="text-gray-500 mb-4">No activity logs found.</p>
          <p className="text-sm text-gray-400">
            Activity logs will appear here when users create, update, or complete tasks.
          </p>
        </div>
      ) : (
        <div className="overflow-x-auto">
          <table className="min-w-full border border-gray-300 bg-white rounded-lg shadow-sm">
            <thead className="bg-gray-100">
              <tr>
                <th className="border px-4 py-3 text-left font-medium text-gray-700">ID</th>
                <th className="border px-4 py-3 text-left font-medium text-gray-700">Action</th>
                <th className="border px-4 py-3 text-left font-medium text-gray-700">User</th>
                <th className="border px-4 py-3 text-left font-medium text-gray-700">Todo</th>
                <th className="border px-4 py-3 text-left font-medium text-gray-700">Time</th>
              </tr>
            </thead>
            <tbody>
              {logs.map((log, index) => (
                <tr key={log.id || index} className="hover:bg-gray-50 transition-colors">
                  <td className="border px-4 py-3 text-sm">{log.id || 'N/A'}</td>
                  <td className="border px-4 py-3">
                    <span className={`inline-flex px-2 py-1 text-xs font-semibold rounded-full ${
                      log.action?.toLowerCase().includes('created') ? 'bg-green-100 text-green-800' :
                      log.action?.toLowerCase().includes('completed') ? 'bg-blue-100 text-blue-800' :
                      log.action?.toLowerCase().includes('updated') ? 'bg-yellow-100 text-yellow-800' :
                      log.action?.toLowerCase().includes('deleted') ? 'bg-red-100 text-red-800' :
                      'bg-gray-100 text-gray-800'
                    }`}>
                      {log.action || 'Unknown Action'}
                    </span>
                  </td>
                  <td className="border px-4 py-3 text-sm font-medium">
                    {log.user?.username || log.username || 'Unknown User'}
                  </td>
                  <td className="border px-4 py-3 text-sm">
                    {log.todo?.description || log.todoDescription || 'N/A'}
                  </td>
                  <td className="border px-4 py-3 text-sm text-gray-600">
                    {log.timestamp ? new Date(log.timestamp).toLocaleString() : 'N/A'}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
      
      <div className="mt-4 flex justify-between items-center text-sm text-gray-600">
        <span>Total: {logs.length} activities</span>
        <span>Last updated: {new Date().toLocaleString()}</span>
      </div>
    </div>
  );
};

export default ActivityLogList;