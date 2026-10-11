import { Event, FilterList } from '@mui/icons-material'
import { Box, ListItemText, MenuItem, MenuList, Paper, Typography } from '@mui/material'
import React from 'react'
import Calendar from 'react-calendar'
import 'react-calendar/dist/Calendar.css'
import styles from './StudentFilters.module.css'

function StudentFilters() {
  return (
    <Box sx={{display: 'flex', flexDirection: 'column', borderRadius:3, gap: 3}}>
        <Paper sx={{ p: 3, borderRadius:3}}>
            <Box sx={{width: '100%'}}>
                <Typography color="primary" variant='h6' sx={{display: 'flex', alignItems:'center', mb:1}}>
                    <FilterList sx={{mr: 1}}/>
                    Filters
                </Typography>
                <MenuList>
                    <MenuItem>
                        <ListItemText primary='Test' />
                    </MenuItem>
                    <MenuItem>
                        <ListItemText primary="Test" />
                    </MenuItem>
                    <MenuItem>
                        <ListItemText primary="Test" />
                    </MenuItem>
                </MenuList>
            </Box>
        </Paper>
        <Paper sx={{ width: '100%', p: 3, borderRadius: 3}}>
            <Typography color="primary" variant='h6' sx={{display: 'flex', alignItems:'center', mb: 1}}>
                <Event sx={{mr: 1}}/>
                Select Date
            </Typography>
            <Calendar className={styles.cal}/>
        </Paper>
    </Box>
  )
}

export default StudentFilters