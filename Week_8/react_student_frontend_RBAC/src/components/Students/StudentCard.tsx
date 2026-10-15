import React from "react";
import type { Student } from "../../types";
import {
  Button,
  Card,
  CardActions,
  CardContent,
  Chip,
  Typography,
} from "@mui/material";
import { useStudents } from "./useStudents";
import { NavLink } from "react-router";
import { useAccount } from "../Global/useAccount";

type Props = {
  student: Student;
};

function StudentCard({ student }: Props) {
  const { deleteStudent } = useStudents();
  const { currentUser, isLoadingUser } = useAccount();

  return (
    <Card sx={{ width: 250 }}>
      <CardContent>
        <Typography>{student.firstName} {student.lastName}</Typography>
        <Typography>{student.id}</Typography>
      </CardContent>
      <CardActions>
        {student.school && <Chip label={student.school} variant="outlined" />}
        <Button component={NavLink} to={`/students/${student.id}`}>
          View
        </Button>
        {(currentUser?.roles.includes("ROLE_ADMIN") || 
        currentUser?.roles.includes("ROLE_MANAGER")) && (
        <Button
          color="error"
          onClick={() => deleteStudent.mutateAsync(student.id)}
        >
          Delete
        </Button>
        )}
      </CardActions>
    </Card>
  );
}

export default StudentCard;
