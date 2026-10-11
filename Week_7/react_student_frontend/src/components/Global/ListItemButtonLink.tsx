import { ListItemButton } from '@mui/material'
import React, { type ReactNode } from 'react'
import { NavLink } from 'react-router'

function ListItemButtonLink({children, to} : {children: ReactNode, to: string}) {
  return (
    <ListItemButton
    component={NavLink}
    to={to}
    sx={{
        fontSize: '1.2rem',
        textTransform: 'uppercase',
        fontWeight: 'bold',
        whiteSpace: 'nowrap',
        color: 'inherit',
        '&.active' : {
            color: 'powderblue'
        }
    }}
    >
        {children}
    </ListItemButton>    
  )
}

export default ListItemButtonLink