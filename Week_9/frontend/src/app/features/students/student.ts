import { Enrollment } from "../enrollments/enrollment";
import { School } from "../schools/school";

export interface Student {

    id: number;
    email: string;
    lastName?: string;
    firstName?: string;
    enrollments?: Enrollment[];
    school?: School;
}
