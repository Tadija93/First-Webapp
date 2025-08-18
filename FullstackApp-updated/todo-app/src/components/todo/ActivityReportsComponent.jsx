import React, { useState, useEffect } from "react";
import { getTodoActivityLogsApi, getUserActivityLogsApi } from "./api/ActivityApiService";
import { getAllUsersApi, retreiveAllTodosForUsernameApi } from "./api/TodoApiService";
import { useAuth } from "./security/AuthContext";

const ActivityReports = () => {
  const [activeReport, setActiveReport] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [reportData, setReportData] = useState([]);

  const [selectedTaskId, setSelectedTaskId] = useState('');
  const [tasks, setTasks] = useState([]);

  const [selectedUsername, setSelectedUsername] = useState('');
  const [users, setUsers] = useState([]);
  const [fromDate, setFromDate] = useState('');
  const [toDate, setToDate] = useState('');

  const authContext = useAuth();

  useEffect(() => {
    if (authContext.role !== 'admin') {
      setError('Access denied. Admin privileges required.');
      return;
    }
    fetchInitialData();
  }, [authContext.role]);

  const fetchInitialData = async () => {
    try {
      const [usersResponse, tasksResponse] = await Promise.all([
        getAllUsersApi(),
        retreiveAllTodosForUsernameApi(authContext.username)
      ]);
      setUsers(usersResponse.data);
      setTasks(tasksResponse.data);
    } catch (error) {
      setError('Failed to load data for reports');
    }
  };

  const generateTaskReport = async () => {
    if (!selectedTaskId) {
      setError('Please select a task');
      return;
    }
    try {
      setLoading(true);
      setError(null);
      setActiveReport('task');
      const response = await getTodoActivityLogsApi(selectedTaskId);
      setReportData(response.data);
    } catch (error) {
      setError('Failed to generate task activity report');
    } finally {
      setLoading(false);
    }
  };

  const generateUserReport = async () => {
    if (!selectedUsername || !fromDate || !toDate) {
      setError('Please select user and date range');
      return;
    }
    try {
      setLoading(true);
      setError(null);
      setActiveReport('user');
      const response = await getUserActivityLogsApi(selectedUsername, fromDate, toDate);
      setReportData(response.data);
    } catch (error) {
      setError('Failed to generate user activity report');
    } finally {
      setLoading(false);
    }
  };

  const clearReport = () => {
    setActiveReport(null);
    setReportData([]);
    setError(null);
  };

  const exportToCSV = () => {
    if (reportData.length === 0) return;
    const headers = ['ID', 'Action', 'User', 'Todo', 'Timestamp'];
    const csvContent = [
      headers.join(','),
      ...reportData.map(log => [
        log.id || '',
        `"${log.action || ''}"`,
        `"${log.user?.username || log.username || ''}"`,
        `"${log.todo?.description || log.todoDescription || ''}"`,
        log.timestamp ? new Date(log.timestamp).toLocaleString() : ''
      ].join(','))
    ].join('\n');

    const blob = new Blob([csvContent], { type: 'text/csv' });
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `activity-report-${activeReport}-${Date.now()}.csv`;
    a.click();
    window.URL.revokeObjectURL(url);
  };

  if (authContext.role !== 'admin') {
    return (
      <div className="container mt-5">
        <div className="alert alert-danger" role="alert">
          Access denied. Admin privileges required to view activity reports.
        </div>
      </div>
    );
  }

  return (
    <div className="container mt-4">
      <h2 className="mb-3">Activity Reports</h2>
      <p className="mb-4 text-muted">Monitor and analyze system activities</p>

      {error && <div className="alert alert-danger">{error}</div>}

      <div className="row">
        {/* Task Activity Report */}
        <div className="col-md-6 mb-4">
          <div className="card">
            <div className="card-header">
              <h5 className="mb-0">Task Activity Report</h5>
              <small className="text-muted">View activities for a specific task</small>
            </div>
            <div className="card-body">
              <label className="form-label">Select Task:</label>
              <select
                value={selectedTaskId}
                onChange={(e) => setSelectedTaskId(e.target.value)}
                className="form-select mb-3"
              >
                <option value="">Choose a task...</option>
                {tasks.map(task => (
                  <option key={task.id} value={task.id}>
                    #{task.id} - {task.description}
                  </option>
                ))}
              </select>
              <button
                onClick={generateTaskReport}
                disabled={!selectedTaskId || loading}
                className="btn btn-primary w-100"
              >
                {loading && activeReport === 'task' ? 'Generating...' : 'Generate Task Report'}
              </button>
            </div>
          </div>
        </div>

        {/* User Activity Report */}
        <div className="col-md-6 mb-4">
          <div className="card">
            <div className="card-header">
              <h5 className="mb-0">User Activity Report</h5>
              <small className="text-muted">View activities for a specific user</small>
            </div>
            <div className="card-body">
              <label className="form-label">Select User:</label>
              <select
                value={selectedUsername}
                onChange={(e) => setSelectedUsername(e.target.value)}
                className="form-select mb-3"
              >
                <option value="">Choose a user...</option>
                {users.map(user => (
                  <option key={user.username} value={user.username}>
                    {user.username}
                  </option>
                ))}
              </select>

              <div className="mb-3">
                <label className="form-label">From Date:</label>
                <input
                  type="date"
                  value={fromDate}
                  onChange={(e) => setFromDate(e.target.value)}
                  className="form-control"
                />
              </div>
              <div className="mb-3">
                <label className="form-label">To Date:</label>
                <input
                  type="date"
                  value={toDate}
                  onChange={(e) => setToDate(e.target.value)}
                  className="form-control"
                />
              </div>

              <button
                onClick={generateUserReport}
                disabled={!selectedUsername || !fromDate || !toDate || loading}
                className="btn btn-success w-100"
              >
                {loading && activeReport === 'user' ? 'Generating...' : 'Generate User Report'}
              </button>
            </div>
          </div>
        </div>
      </div>

      {/* Report Section */}
      {activeReport && (
        <div className="card mt-4">
          <div className="card-header d-flex justify-content-between align-items-center">
            <div>
              <h5 className="mb-0">
                {activeReport === 'task'
                  ? `Task Activity Report - #${selectedTaskId}`
                  : `User Activity Report - ${selectedUsername}`}
              </h5>
              {activeReport === 'user' && (
                <small className="text-muted">
                  From {fromDate} to {toDate}
                </small>
              )}
            </div>
            <div>
              {reportData.length > 0 && (
                <button onClick={exportToCSV} className="btn btn-secondary btn-sm me-2">
                  Export CSV
                </button>
              )}
              <button onClick={clearReport} className="btn btn-danger btn-sm">
                Clear
              </button>
            </div>
          </div>
          <div className="card-body">
            {loading ? (
              <p>Loading report...</p>
            ) : reportData.length === 0 ? (
              <p>No activity data found.</p>
            ) : (
              <div className="table-responsive">
                <table className="table table-striped">
                  <thead>
                    <tr>
                      <th>ID</th>
                      <th>Action</th>
                      <th>User</th>
                      <th>Todo</th>
                      <th>Timestamp</th>
                    </tr>
                  </thead>
                  <tbody>
                    {reportData.map(log => (
                      <tr key={log.id}>
                        <td>{log.id}</td>
                        <td>{log.action}</td>
                        <td>{log.user?.username || log.username}</td>
                        <td>{log.todo?.description || log.todoDescription}</td>
                        <td>{new Date(log.timestamp).toLocaleString()}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </div>
        </div>
      )}
    </div>
  );
};

export default ActivityReports;
