import { ListItemButton } from '@mui/material'
import type { SxProps, Theme } from '@mui/material/styles'
import React, { type ReactNode } from 'react'
import { NavLink } from 'react-router'

export const listItemButtonSx: SxProps<Theme> = {
  fontSize: '1.2rem',
  textTransform: 'uppercase',
  fontWeight: 'bold',
  whiteSpace: 'nowrap',
  color: 'inherit',
  '&.active': {
    color: 'powderblue'
  }
}

function ListItemButtonLink({children, to} : {children: ReactNode, to: string}) {
  return (
    <ListItemButton
    component={NavLink}
    to={to}
    end
    sx={listItemButtonSx}
    >
        {children}
    </ListItemButton>    
  )
}

export default ListItemButtonLink