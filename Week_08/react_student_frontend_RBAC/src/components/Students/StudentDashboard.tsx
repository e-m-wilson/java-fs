import { CircularProgress, Grid } from "@mui/material";
import StudentList from "./StudentList";
import { useStudents } from "./useStudents";
import StudentFilters from "./StudentFilters";
import { useEffect } from "react";

function StudentDashboard() {
  // custom hook
  const { students } = useStudents();

  useEffect(() => {
    document.title = `Students`;
  }, []);

  if (!students) {
    return <CircularProgress sx={{ display: "block", m: 4, mx: "auto" }} />;
  }

  return (
    <Grid container spacing={2} sx={{ m: 4 }}>
      <Grid size={8}>
        <StudentList />
      </Grid>
      <Grid size={4}>
        <StudentFilters />
      </Grid>
    </Grid>
  );
}

export default StudentDashboard;
