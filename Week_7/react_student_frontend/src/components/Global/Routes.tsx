import { createBrowserRouter } from "react-router";
import App from "./App";
import Welcome from "../Welcome/Welcome";
import StudentDashboard from "../Students/StudentDashboard";
import StudentForm from "../Students/StudentForm";
import StudentDetails from "../Students/StudentDetails";
import Error from "../Global/Error";
import LoginForm from "./LoginForm";
//import RequireAuth from "./RequireAuth";

export const router = createBrowserRouter([
  {
    path: "/",
    element: <App />,
    children: [
    //   {
    //     element: <RequireAuth />,
    //     children: [
    //       { path: "students/:id", element: <StudentDetails /> },
    //       { path: "students/create", element: <StudentForm key="create" /> },
    //       { path: "students/manage/:id", element: <StudentForm /> },
    //     ],
    //   },
      { path: "students/:id", element: <StudentDetails /> },
      { path: "students/create", element: <StudentForm key="create" /> },
      { path: "students/manage/:id", element: <StudentForm /> },
      { path: "", element: <Welcome /> },
      { path: "students", element: <StudentDashboard /> },
      { path: "login", element: <LoginForm /> },
      { path: "*", element: <Error /> },
    ],
  },
]);
