import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import type { Student } from "../../types";
import agent from "../Global/agent";
import { useLocation } from "react-router";
//import { useAccount } from "../Global/useAccount";

type StudentInput = Omit<Student, "id">;

// you could also do this to create a quick type that excludes multiple fields
//type StudentInput = Omit<Student, "id" | "createdAt" | "updatedAt">;

// and this example excludes id + createdAt, but all other fields would be optional 
//type StudentUpdateInput = Partial<Omit<Student, "id" | "createdAt">>;

export const useStudents = (id?: string) => {
  //const { currentUser } = useAccount();
  const queryClient = useQueryClient();
  const location = useLocation();

  const { data: students, isPending } = useQuery({
    queryKey: ["students"],
    queryFn: async () => {
      const response = await agent.get<Student[]>("/students");
      return response.data;
    },
    staleTime: 1000 * 60 * 5,
    enabled: !id && location.pathname === "/students",
  });

  const { data: student, isLoading: isLoadingStudent } = useQuery({
    queryKey: ["students", id],
    queryFn: async () => {
      const response = await agent.get<Student>(`/students/${id}`);
      return response.data;
    },
    enabled: !!id //&& !!currentUser,
  });

  const updateStudent = useMutation({
    mutationFn: async (student: Student) => {
      await agent.put(`/students/${student.id}`, student);
    },
    onSuccess: async () => {
      await queryClient.invalidateQueries({
        queryKey: ["students"],
      });
    },
  });

  const createStudent = useMutation({
    mutationFn: async (student: StudentInput) => {
      const response = await agent.post<Student>(`/students`, student);
      return response.data;
    },
    onSuccess: async () => {
      await queryClient.invalidateQueries({
        queryKey: ["students"],
      });
    },
  });

  const deleteStudent = useMutation({
    mutationFn: async (id: number) => {
      await agent.delete(`/students/${id}`);
    },
    onSuccess: async () => {
      await queryClient.invalidateQueries({
        queryKey: ["students"],
      });
    },
  });

  return {
    students,
    isPending,
    updateStudent,
    createStudent,
    deleteStudent,
    student,
    isLoadingStudent,
  };
};
