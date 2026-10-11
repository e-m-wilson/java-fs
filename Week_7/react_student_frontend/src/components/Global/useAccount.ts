import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query"
import agent from "./agent"
import type { User, LoginCreds } from "../../types";

export const useAccount = () => {

    const queryClient = useQueryClient();

    const loginUser = useMutation({
        mutationFn: async (creds: LoginCreds) => {
            await agent.post('/login?useCookies=true', creds);
        },
        onSuccess: async () => {
            await queryClient.invalidateQueries({
                queryKey: ['user']
            });
        }
    });

    const {data: currentUser, isLoading: isLoadingUser} = useQuery({
        queryKey: ['user'],
        queryFn: async () => {
            const response = await agent.get<User>('/account/user-info');
            return response.data;
        }
    });

    return {
        loginUser,
        currentUser,
        isLoadingUser
    }
}