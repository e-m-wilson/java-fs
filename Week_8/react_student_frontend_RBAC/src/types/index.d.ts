
export interface Student {

    id: number;
    email: string;
    lastName?: string;
    firstName?: string;
    enrollments?: Enrollment[];
    school?: School;
}


export interface LoginCreds {
    userName: string
    password: string
}

export interface User {
    userName: string
    roles: string[]
}