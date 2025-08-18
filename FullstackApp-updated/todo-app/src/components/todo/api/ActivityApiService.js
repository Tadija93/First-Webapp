import { apiClient } from './ApiClient'

// Get all activity logs
export const getActivityLogsApi = () => apiClient.get('/activity')

// Get activity logs for a specific user with date range
export const getUserActivityLogsApi = (username, fromDate, toDate) => 
    apiClient.get(`/activity/user/${username}`, {
        params: {
            from: fromDate,
            to: toDate
        }
    })

// Get activity logs for a specific todo/task
export const getTodoActivityLogsApi = (todoId) => 
    apiClient.get(`/activity/todo/${todoId}`)