
export interface Student {

    id: number;
    email: string;
    lastName?: string;
    firstName?: string;
    enrollments?: Enrollment[];
    school?: School;
}


export interface LoginCreds {
    username: string
    password: string
}

export interface User {
    id: string
    email: string
    displayName: string
    imageUrl?: string
}