import React from 'react'
import { useAccount } from './useAccount'
import { Navigate, Outlet, useLocation } from 'react-router';
import { CircularProgress } from '@mui/material';

function RequireAuth() {

    const {currentUser, isLoadingUser} = useAccount();
    const location = useLocation();

    if(isLoadingUser) return <CircularProgress sx={{ display: 'block', m: 4, mx: 'auto' }} />


    if(!currentUser) return <Navigate to='/login' state={{from: location}}/>

  return (
    <Outlet/>
  )
}

export default RequireAuth