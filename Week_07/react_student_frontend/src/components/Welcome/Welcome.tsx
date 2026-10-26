import { Button, Stack, Typography } from "@mui/material";
import { NavLink } from "react-router";

function Welcome() {
  return (
    <Stack
      sx={{
        width: "100%",
        height: "100vh",
        bgcolor: "background.default",
        alignItems: "center",
        justifyContent: "center",
      }}
    >
      <Typography variant="h2" sx={{ color: "primary" }}>
        Student Management App
      </Typography>
      <Button
        component={NavLink}
        to={`/students`}
        variant="text"
        sx={{ color: "primary" }}
      >
        Go to student dashboard!
      </Button>
    </Stack>
  );
}

export default Welcome;
