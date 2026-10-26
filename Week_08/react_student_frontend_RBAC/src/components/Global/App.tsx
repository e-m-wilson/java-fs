import Header from './Header'
import { CssBaseline, Box, ThemeProvider } from '@mui/material';
import { Outlet } from 'react-router';
import lightTheme from './lightTheme';
import darkTheme from './darkTheme';
import { useState } from 'react';

function App() {

  const [theme, setTheme] = useState(lightTheme);

  function toggleDarkMode(){
    if(theme.palette.mode === 'dark') {
      setTheme(lightTheme);
    } else {
      setTheme(darkTheme);
    }
  }

  return (
    <>
      <ThemeProvider theme={theme}>
        <CssBaseline/>
        <Header toggleDarkMode={toggleDarkMode}/>
        <Box sx={{display: 'flex', justifyContent:'center', width:'100%'}}>
          <Outlet />
        </Box> 
      </ThemeProvider>
      
    </>
  )
}

export default App