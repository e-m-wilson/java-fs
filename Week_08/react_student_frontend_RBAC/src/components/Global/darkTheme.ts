import { createTheme } from '@mui/material/styles';

const darkTheme = () => createTheme({
  palette: {
    mode: 'dark',

    primary: {
        main: '#60a5fa',
        contrastText: '#0b1220',
    },

    secondary: {
        main: '#c084fc',
        contrastText: '#0b0b0b',
    },

    background: {
      default: '#0f1115',
        paper: '#1a1d23',
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

export default darkTheme;