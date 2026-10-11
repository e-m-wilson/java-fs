import { Grid } from "@mui/material";
import StudentCard from "./StudentCard";
import { useStudents } from "./useStudents";

function StudentList() {
  const { students } = useStudents();

  return (
    <>
      <Grid container spacing={2}>
        {students?.map((s) => {
          return <StudentCard key={s.id} student={s} />;
        })}
      </Grid>
    </>
  );
}

export default StudentList;
