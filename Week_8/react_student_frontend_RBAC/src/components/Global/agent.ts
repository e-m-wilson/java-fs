import axios from "axios";

const agent = axios.create({
  baseURL: "http://localhost:8080/api",
  //withCredentials: true,
});

agent.interceptors.response.use(async (response) => {
  try {
    return response;
  } catch (err) {
    console.error(err);
    return Promise.reject(err);
  }
});

export default agent;
