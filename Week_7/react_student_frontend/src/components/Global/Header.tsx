import {
  AppBar,
  Box,
  IconButton,
  List,
  ListItem,
  Toolbar,
  Typography,
} from "@mui/material";
import ListItemButtonLink from "./ListItemButtonLink";
import { LightMode, DarkMode } from "@mui/icons-material";
import { useTheme } from "@mui/material/styles";

type Props = {
  toggleDarkMode: () => void;
};

function Header({ toggleDarkMode }: Props) {
  const theme = useTheme();

  return (
    <Box sx={{ flexGrow: 1 }}>
      <AppBar position="static">
        <Toolbar>
          <IconButton size="large" edge="start" color="inherit" sx={{ mr: 2 }}>
            {theme.palette.mode === "dark" ? (
              <LightMode onClick={toggleDarkMode} />
            ) : (
              <DarkMode onClick={toggleDarkMode} />
            )}
          </IconButton>
          <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
            Student Management App
          </Typography>
          <List sx={{ flexDirection: "row", display: "flex" }}>
            <ListItem>
              <ListItemButtonLink to="/">Home</ListItemButtonLink>
            </ListItem>
            <ListItem>
              <ListItemButtonLink to="/students">Students</ListItemButtonLink>
            </ListItem>
            <ListItem>
              <ListItemButtonLink to="/students/create">
                Create Student
              </ListItemButtonLink>
            </ListItem>
          </List>
        </Toolbar>
      </AppBar>
    </Box>
  );
}

export default Header;
