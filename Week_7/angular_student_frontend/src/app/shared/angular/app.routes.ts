import { Routes } from '@angular/router';
import { Home } from '../components/home/home';
import { userAuthGuard } from '../guards/user-auth-guard';
import { Login } from '../components/login/login';
import { NotFound } from '../components/not-found/not-found';

export const routes: Routes = [
    {
        path: '',
        component: Home,
        title: 'Home',
        canActivate: [userAuthGuard]
    },
    {
        path: 'about',
        loadComponent: () =>
            import('../components/about/about').then(m => m.About),
        title: 'About'
    },
    {
        path: 'students/:id',
        loadComponent: () => 
            import('../../features/students/student-details/student-details').then(m => m.StudentDetails),
        title: 'Student Details',
        canActivate: [userAuthGuard]
    },
    {
        path: 'login',
        title: 'Login',
        component: Login
    },
    {
        path: '**',
        title: "Not Found",
        component: NotFound
    }
];
