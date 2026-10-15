import {
  Button,
  Card,
  CardActions,
  CardContent,
  Typography,
} from "@mui/material";
import { useStudents } from "./useStudents";
import { NavLink, useNavigate, useParams } from "react-router";
import { useEffect } from "react";

function StudentDetails() {
  const navigate = useNavigate();
  const { id } = useParams();
  const { student, isLoadingStudent } = useStudents(id);

  useEffect(() => {
    document.title = `Viewing Student: ${id}`;
  }, [id]);

  if (isLoadingStudent) return <Typography>Loading Student...</Typography>;

  return (
    <Card sx={{ mx: "20%", mt: 3 }}>
      <CardContent>
        <Typography variant="h4">
          {student?.firstName} {student?.lastName}
        </Typography>
        <Typography variant="subtitle1">ID: {student?.id}</Typography>
        <Typography>Email: {student?.email}</Typography>
      </CardContent>
      <CardActions>
        <Button
          component={NavLink}
          to={`/students/manage/${student?.id}`}
          color="primary"
        >
          Edit
        </Button>
        <Button onClick={() => navigate("/students")}>Cancel</Button>
      </CardActions>
    </Card>
  );
}

export default StudentDetails;
