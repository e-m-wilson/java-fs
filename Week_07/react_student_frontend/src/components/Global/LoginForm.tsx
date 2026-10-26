import React from 'react'
import { useAccount } from './useAccount'
import { Box, Button, Paper, TextField, Typography } from '@mui/material';
import { LockOpen } from '@mui/icons-material';
import type { LoginCreds } from '../../types';
import { useLocation, useNavigate } from 'react-router';

function LoginForm() {

    const navigate = useNavigate()
    const location = useLocation();
    const { loginUser } = useAccount();

    const onSubmit = async (event: React.SubmitEvent) => {
        event.preventDefault();

        const formData = new FormData(event.target);

        const data: Record<string, unknown> = {}
        formData.forEach((value, key) => {
        data[key] = value;
        });

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
            <Typography variant='h4'>Sign in</Typography>
        </Box>

        <TextField label="Email" name="email"/>

        <TextField label="Password" name="password" type='password'/>

        <Button type='submit' variant='contained'>
            Login
        </Button>
    </Paper>
  )
}

export default LoginForm