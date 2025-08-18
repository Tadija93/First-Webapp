import { useEffect, useState } from 'react';
import {
  getTodosForUserApi,
  retreiveAllTodosForUsernameApi,
  deleteTodoApi,
  updateTodoApi,
  getAllUsersApi,
  updateTodoAssignmentApi
} from './api/TodoApiService';
import { useAuth } from './security/AuthContext';
import { useNavigate } from 'react-router-dom';
import { format } from 'date-fns';

// Map each status to a Bootstrap border color
const statusColors = {
  TODO: 'info',
  IN_PROGRESS: 'warning',
  READY_TO_TEST: 'primary',
  DONE: 'success'
};

function ListTodosComponent() {
  const authContext = useAuth();
  const username = authContext.username;
  const role = authContext.role;
  const navigate = useNavigate();

  const [todos, setTodos] = useState([]);
  const [message, setMessage] = useState(null);
  const [filter, setFilter] = useState('ALL');
  const [users, setUsers] = useState([]);

  // Fetch users on component mount
  useEffect(() => {
    fetchUsers();
  }, []);

  // Fetch todos whenever role or username changes
  useEffect(() => {
    if (role === 'admin') {
      fetchAllTodos();
    } else {
      fetchUserTodos();
    }
  }, [role, username]);

  function fetchUsers() {
    getAllUsersApi()
      .then((response) => setUsers(response.data))
      .catch((error) => console.log(error));
  }

  function fetchAllTodos() {
    retreiveAllTodosForUsernameApi(username)
      .then((response) => setTodos(response.data))
      .catch((error) => console.log(error));
  }

  function fetchUserTodos() {
    getTodosForUserApi(username)
      .then((response) => setTodos(response.data))
      .catch((error) => console.log(error));
  }

  function refreshTodos() {
    if (role === 'admin') {
      fetchAllTodos();
    } else {
      fetchUserTodos();
    }
  }

  // Update todo status
  function updateTodoStatus(todo, newStatus) {
    const updatedTodo = { ...todo, status: newStatus };
    updateTodoApi(username, todo.id, updatedTodo)
      .then(() => refreshTodos())
      .catch((error) => console.log(error));
  }

  // Update todo assignment
  function updateTodoAssignment(todo, newAssignedTo) {
    updateTodoAssignmentApi(username, todo.id, newAssignedTo)
      .then(() => refreshTodos())
      .catch((error) => console.log(error));
  }

  function deleteTodo(id) {
    deleteTodoApi(username, id)
      .then(() => {
        setMessage(`The todo with id = ${id} was deleted successfully`);
        refreshTodos();
      })
      .catch((error) => console.log(error));
  }

  function updateTodo(id) {
    navigate(`/todos/${id}`);
  }

  function addNewTodo() {
    navigate(`/todos/-1`);
  }

  // Filter todos based on selected filter
  const filteredTodos = todos.filter((todo) => {
    if (filter === 'ALL') return true;
    return todo.status === filter;
  });

  return (
    <div className="container">
      <h1>Your Todos</h1>
      {message && <div className="alert alert-warning">{message}</div>}

      {/* Filter */}
      <div className="filter-options mb-3">
        <label>Filter Todos: </label>
        <select
          value={filter}
          onChange={(e) => setFilter(e.target.value)}
          className="form-select w-auto d-inline-block ms-2"
        >
          <option value="ALL">All</option>
          <option value="TODO">To Do</option>
          <option value="IN_PROGRESS">In Progress</option>
          <option value="READY_TO_TEST">Ready to Test</option>
          <option value="DONE">Done</option>
        </select>
      </div>

      {/* Cards in a responsive grid */}
      <div className="row row-cols-1 row-cols-md-3 g-4">
        {filteredTodos.map((todo) => {
          // Determine the Bootstrap border color for this todo's status
          const borderColorClass = `border-${statusColors[todo.status] || 'secondary'}`;

          return (
            <div key={todo.id} className="col d-flex">
              <div className={`card h-100 d-flex flex-column ${borderColorClass}`}>
                <div className="card-body flex-grow-1">
                  {/* Title */}
                  <h6 className="card-title">{todo.description}</h6>
                  {/* Status */}
                  <p className="card-text mb-1">
                    <strong>Status: </strong>
                    <select
                      value={todo.status}
                      onChange={(e) => updateTodoStatus(todo, e.target.value)}
                      className="form-select form-select-sm d-inline-block w-auto"
                    >
                      <option value="TODO">To Do</option>
                      <option value="IN_PROGRESS">In Progress</option>
                      <option value="READY_TO_TEST">Ready to Test</option>
                      <option value="DONE">Done</option>
                    </select>
                  </p>
                  {/* Target Date */}
                  <p className="card-text mb-1">
                    <strong>Target Date: </strong>
                    {format(new Date(todo.targetDate), 'dd.MM.yyyy')}
                  </p>
                  {/* Comments */}
                  <p className="card-text mb-1">
                    <strong>Comments: </strong>
                    {todo.comments || 'None'}
                  </p>
                  {/* Assignee */}
                  <p className="card-text">
                    <strong>Assignee: </strong>
                    <select
                      value={todo.assignedTo || ''}
                      onChange={(e) => updateTodoAssignment(todo, e.target.value)}
                      className="form-select form-select-sm d-inline-block w-auto"
                    >
                      <option value="">Unassigned</option>
                      {users.map((user) => (
                        <option key={user.username} value={user.username}>
                          {user.username}
                        </option>
                      ))}
                    </select>
                  </p>
                </div>
                {/* Footer with actions */}
                <div className="card-footer d-flex justify-content-between">
                  <button
                    className="btn btn-success btn-sm"
                    onClick={() => updateTodo(todo.id)}
                  >
                    Update
                  </button>
                  {role === 'admin' && (
                    <button
                      className="btn btn-warning btn-sm"
                      onClick={() => deleteTodo(todo.id)}
                    >
                      Delete
                    </button>
                  )}
                </div>
              </div>
            </div>
          );
        })}
      </div>

      {/* Add New Todo (admin only) */}
      {role === 'admin' && (
        <button className="btn btn-success mt-3" onClick={addNewTodo}>
          Add New Todo
        </button>
      )}
    </div>
  );
}

export default ListTodosComponent;
