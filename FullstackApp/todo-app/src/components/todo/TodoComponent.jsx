import { useParams, useNavigate } from "react-router-dom";
import { retrieveTodoApi, updateTodoApi, createTodoApi } from "./api/TodoApiService";
import { useAuth } from "./security/AuthContext";
import { useEffect, useState } from "react";
import { Field, Formik, Form, ErrorMessage } from "formik";
import moment from "moment";

function TodoComponent() {
    const { id } = useParams()

    const authContext = useAuth()
    const navigate = useNavigate();

    const username = authContext.username

    const [description, setDescription] = useState('')
    const [targetDate, setTargetDate] = useState('')

    useEffect(() => retreiveTodos(), [id]);
    

    function retreiveTodos() {
        if (id != -1) {
            retrieveTodoApi(username, id)
                .then(response => {
                    setDescription(response.data.description)
                    setTargetDate(response.data.targetDate)
                })
                .catch((error) => console.log(error))
        }
    }

    function onSubmit(values) {
        const todo = {
            id: id,
            username: username,
            targetDate: values.targetDate,
            description: values.description,
            done:false
        }

        if (id == -1) {
            createTodoApi(username, todo)
            .then(response => {
                navigate('/todos')
            })
            .catch((error) => console.log(error)) }
        else {
            updateTodoApi(username, id, todo)
            .then(response => {
                navigate('/todos')
            })
            .catch((error) => console.log(error))
        }
    }

    function validate(values) {
        let errors ={ }
        if (values.description.length<5)
            errors.description = 'Enter at least 5 characthers'
        if(values.targetDate == null || values.targetDate === '' || !moment(values.targetDate).isValid() )
            errors.targetDate = 'Enter target date'
        return errors
    }
    return (
        <div className="container">
            <h1>Enter Todo Details</h1>
            <div>
                <Formik initialValues={{ description, targetDate }}
                    enableReinitialize={true}
                    onSubmit={onSubmit}
                    validate={validate}
                    validateOnBlur={false}
                    validateOnChange={false}
                >
                    {
                        (props) => (
                            <Form>
                                <ErrorMessage
                                    name="description"
                                    component='div'
                                    className="alert alert-warning"
                                />
                                <ErrorMessage
                                    name="targetDate"
                                    component='div'
                                    className="alert alert-warning"
                                />
                                <fieldset className="form-group">
                                    <label>Description</label>
                                    <Field className="form-control" type="text" name="description"/>
                                </fieldset>
                                <fieldset className="form-group">
                                    <label>Target Date</label>
                                    <Field className="form-control" type="date" name="targetDate"/>
                                </fieldset>
                                <div>
                                    <button className="btn btn-success m-5" type="submit">Save</button>
                                </div>
                            </Form>
                        )
                    }
              </Formik>
            </div>
        </div>
    )
}
export default TodoComponent;