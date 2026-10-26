import { createTheme } from '@mui/material/styles';

const lightTheme = () => createTheme({
  palette: {
    mode: 'light',

    primary: {
      main: '#2563eb',
      contrastText: '#ffffff',
    },

    secondary: {
      main: '#9c27b0',
    },

    background: {
      default: '#f7f8fa',
      paper: '#ffffff',
    },

    success: {
      main: '#2e7d32',
    },

    error: {
      main: '#d32f2f',
    },
    warning: {
      main: '#ed6c02',
    },
    info: {
      main: '#0288d1',
    },
  },

  typography: {
    fontFamily: '"Roboto", sans-serif',

    h1: {
      fontSize: '2.5rem',
      fontWeight: 700,
    },
  },
});

export default lightTheme;