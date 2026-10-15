import { Box, Button, CircularProgress, Stack, TextField, Typography } from "@mui/material";
import React from "react";
import { useStudents } from "./useStudents";
import { NavLink, useNavigate, useParams } from "react-router";
import { useEffect } from "react";

function StudentForm() {
  const { id } = useParams();
  const { updateStudent, createStudent, student, isLoadingStudent } =
    useStudents(id);
  const navigate = useNavigate();

  useEffect(() => {
    document.title = `Student Form`;
  }, []);

  async function handleSubmit(event: React.SubmitEvent) {
    event.preventDefault();

    const formData = new FormData(event.currentTarget as HTMLFormElement);
    const data = {
      email: String(formData.get("email") ?? ""),
      firstName: String(formData.get("firstName") ?? ""),
      lastName: String(formData.get("lastName") ?? ""),
    };

    if (student) {
      await updateStudent.mutateAsync({ ...data, id: student.id });
      navigate(`/students/${student.id}`);
    } else {
      createStudent.mutate(data, {
        onSuccess: (student) => {
          navigate(`/students/${student.id}`);
        },
      });
    }
  }
 
  if (isLoadingStudent) {
      return <CircularProgress sx={{ display: "block", m: 4, mx: "auto" }} />;
  }

  return (
    <Box
      sx={{
        flexDirection: "column",
        display: "flex",
        justifyContent: "space-around",
        m: 4,
        maxWidth: "500px",
        width: "100%",
      }}
      component="form"
      onSubmit={handleSubmit}
    >
  
      <Stack spacing={1}>
        <Typography variant="h5">
          {student ? "Edit Student" : "Create Student"}
        </Typography>

        <TextField
          label="Email"
          name="email"
          type="email"
          required
          defaultValue={student?.email}
        />
        <TextField
          label="First name"
          name="firstName"
          defaultValue={student?.firstName}
        />
        <TextField
          label="Last name"
          name="lastName"
          defaultValue={student?.lastName}
        />

        <Button
          type="submit"
          variant="contained"
          disabled={updateStudent.isPending || createStudent.isPending}
        >
          Save
        </Button>
        {student && (
          <Button component={NavLink} to={`/students/${student.id}`}>
            Cancel
          </Button>
        )}
      </Stack>
    </Box>
  );
}

export default StudentForm;
