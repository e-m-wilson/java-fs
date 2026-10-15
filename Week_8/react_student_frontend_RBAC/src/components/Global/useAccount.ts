import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query"
import agent from "./agent"
import type { User, LoginCreds } from "../../types";

export const useAccount = () => {

    const queryClient = useQueryClient();

    const loginUser = useMutation({
        mutationFn: async (creds: LoginCreds) => {
            await agent.post('/auth/login', creds);
        },
        onSuccess: async () => {
            await queryClient.invalidateQueries({
                queryKey: ['user']
            });
        }
    });

    // don't just invalidate the user here
    // we need to set user to null so the UI refreshes 
    // asap, otherwise it will try and hold the previous
    // cached value and the UI won't update right away 
    const logoutUser = useMutation({
        mutationFn: async () => {
            await agent.post('/auth/logout');
        },
        onSuccess: () => {
            queryClient.setQueryData(['user'], null);
        }
    });

    const registerUser = useMutation({
        mutationFn: async (creds: LoginCreds) => {
            await agent.post('/auth/register', creds);
        }
    });

    const {data: currentUser, isLoading: isLoadingUser} = useQuery({
        queryKey: ['user'],
        queryFn: async () => {
            const response = await agent.get<User>('/auth/me');
            return response.data;
        }
    });

    return {
        loginUser,
        logoutUser,
        registerUser,
        currentUser,
        isLoadingUser
    }
}