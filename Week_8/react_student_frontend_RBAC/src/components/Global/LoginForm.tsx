import React, { useState } from 'react'
import { useAccount } from './useAccount'
import { Alert, Box, Button, Paper, TextField, ToggleButton, ToggleButtonGroup, Typography } from '@mui/material';
import { LockOpen } from '@mui/icons-material';
import type { LoginCreds } from '../../types';
import { useLocation, useNavigate } from 'react-router';

function LoginForm() {

    const navigate = useNavigate()
    const location = useLocation();
    const { loginUser, registerUser } = useAccount();
    const [mode, setMode] = useState<'login' | 'register'>('login');
    const [registrationComplete, setRegistrationComplete] = useState(false);

    const onSubmit = async (event: React.SubmitEvent) => {
        event.preventDefault();

        const formData = new FormData(event.target);

        const data: Record<string, unknown> = {}
        formData.forEach((value, key) => {
        data[key] = value;
        });

        if (mode === 'register') {
            await registerUser.mutateAsync(data as unknown as LoginCreds);
            setRegistrationComplete(true);
            setMode('login');
            return;
        }

        await loginUser.mutateAsync(data as unknown as LoginCreds, {
            onSuccess: () => {
                navigate(location.state?.from || '/students');
            }
        });
    }

  return (
    <Paper 
    component='form'
    onSubmit={onSubmit}
    sx={{
        display: 'flex',
        flexDirection: 'column',
        p: 3,
        gap: 3,
        maxWidth: 'md',
        mx: 'auto',
        my: 5,
        borderRadius: 3
    }}
    >
        <Box
        sx={{
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            gap: 3,
            color: 'primary.main',
        }}
        >
            <LockOpen fontSize='large'/>
            <Typography variant='h4'>{mode === 'login' ? 'Sign in' : 'Create account'}</Typography>
        </Box>

        <ToggleButtonGroup
            value={mode}
            exclusive
            onChange={(_, value: 'login' | 'register' | null) => {
                if (value) {
                    setMode(value);
                    setRegistrationComplete(false);
                }
            }}
            aria-label="Authentication mode"
            fullWidth
        >
            <ToggleButton value="login">Login</ToggleButton>
            <ToggleButton value="register">Register</ToggleButton>
        </ToggleButtonGroup>

        {registrationComplete && (
            <Alert severity="success">Registration successful. Sign in with your new account.</Alert>
        )}

        <TextField label="Username" name="userName"/>

        <TextField label="Password" name="password" type='password'/>

        <Button
            type='submit'
            variant='contained'
            disabled={loginUser.isPending || registerUser.isPending}
        >
            {mode === 'login' ? 'Login' : 'Register'}
        </Button>
        
    </Paper>
  )
}

export default LoginForm