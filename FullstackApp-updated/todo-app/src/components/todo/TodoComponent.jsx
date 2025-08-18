import { useParams, useNavigate } from "react-router-dom";
import { retrieveTodoApi, updateTodoApi, createTodoApi } from "./api/TodoApiService";
import { useAuth } from "./security/AuthContext";
import { useEffect, useState } from "react";
import { Field, Formik, Form, ErrorMessage } from "formik";
import moment from "moment";

function TodoComponent() {
    const { id } = useParams();
    const authContext = useAuth();
    const navigate = useNavigate();
    const username = authContext.username;

    const [description, setDescription] = useState('');
    const [targetDate, setTargetDate] = useState('');
    const [status, setStatus] = useState('TODO');
    const [comments, setComments] = useState(''); // New state for comments
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        retreiveTodos();
    }, [id]);

    function retreiveTodos() {
        if (id !== '-1') { // Ensure id is compared as a string
            setLoading(true);
            retrieveTodoApi(username, id)
                .then(response => {
                    setDescription(response.data.description);
                    setTargetDate(response.data.targetDate);
                    setStatus(response.data.status);
                    setComments(response.data.comments); // Update comments state
                })
                .catch(error => console.error("Error retrieving todo:", error))
                .finally(() => setLoading(false));
        }
    }

    function onSubmit(values) {
        const todoId = id !== undefined && parseInt(id) !== -1 ? parseInt(id) : null;

        const todo = {
            id: todoId,
            username: username,
            targetDate: values.targetDate,
            description: values.description,
            status: status || "TODO",
            comments: values.comments, // Include comments field
        };

        if (todo.id === null) {
            createTodoApi(username, todo)
                .then(() => navigate('/todos'))
                .catch(error => console.error("Error creating todo:", error));
        } else {
            updateTodoApi(username, todo.id, todo)
                .then(() => navigate('/todos'))
                .catch(error => console.error("Error updating todo:", error));
        }
    }

    function validate(values) {
        const errors = {};
        if (!values.description || values.description.trim().length < 5) {
            errors.description = 'Enter at least 5 characters for the description';
        }
        if (!values.targetDate || !moment(values.targetDate, 'YYYY-MM-DD', true).isValid()) {
            errors.targetDate = 'Enter a valid target date';
        }
        // Optionally add validation for comments if required
        return errors;
    }

    return (
        <div className="container">
            <h1>{id === '-1' ? 'Add Todo' : 'Update Todo'}</h1>
            {loading && <div>Loading...</div>}
            <div>
                <Formik
                    initialValues={{
                        description: description || '',
                        targetDate: targetDate || '',
                        comments: comments || '' 
                    }}
                    enableReinitialize={true}
                    onSubmit={onSubmit}
                    validate={validate}
                    validateOnBlur={false}
                    validateOnChange={false}
                >
                    {props => (
                        <Form>
                            <ErrorMessage
                                name="description"
                                component="div"
                                className="alert alert-warning"
                            />
                            <ErrorMessage
                                name="targetDate"
                                component="div"
                                className="alert alert-warning"
                            />
                            <fieldset className="form-group">
                                <label>Description</label>
                                <Field className="form-control" type="text" name="description" />
                            </fieldset>
                            <fieldset className="form-group">
                                <label>Target Date</label>
                                <Field className="form-control" type="date" name="targetDate" />
                            </fieldset>
                            <fieldset className="form-group">
                                <label>Comments</label>
                                <Field 
                                    className="form-control" 
                                    as="textarea" 
                                    name="comments" 
                                    placeholder="Enter comments" 
                                />
                            </fieldset>
                            {id !== '-1' && (
                                <fieldset className="form-group">
                                    <label>Status</label>
                                    <select
                                        className="form-control"
                                        value={status}
                                        onChange={(e) => setStatus(e.target.value)}
                                    >
                                        <option value="TODO">To Do</option>
                                        <option value="IN_PROGRESS">In Progress</option>
                                        <option value="READY_TO_TEST">Ready to Test</option>
                                        <option value="DONE">Done</option>
                                    </select>
                                </fieldset>
                            )}
                            <div>
                                <button
                                    className="btn btn-success m-5"
                                    type="submit"
                                    disabled={props.isSubmitting || loading}
                                >
                                    Save
                                </button>
                            </div>
                        </Form>
                    )}
                </Formik>
            </div>
        </div>
    );
}

export default TodoComponent;
