import axios from "axios";

const baseURL = "http://localhost:8080/api";

const agent = axios.create({
  baseURL,
  withCredentials: true,
});

const csrfAgent = axios.create({
  baseURL,
  withCredentials: true,
});

let csrfToken: string | undefined;
let csrfHeaderName: string | undefined;
let csrfRequest: Promise<void> | undefined;

function ensureCsrfToken() {
  if (csrfToken && csrfHeaderName) return Promise.resolve();

  csrfRequest ??= csrfAgent
    .get<{ token: string; headerName: string }>("/auth/csrf")
    .then(({ data }) => {
      csrfToken = data.token;
      csrfHeaderName = data.headerName;
    })
    .finally(() => {
      csrfRequest = undefined;
    });

  return csrfRequest;
}

agent.interceptors.request.use(async (config) => {
  const method = config.method?.toLowerCase();

  if (method && ["post", "put", "patch", "delete"].includes(method)) {
    await ensureCsrfToken();
    config.headers.set(csrfHeaderName!, csrfToken!);
  }

  return config;
});

export default agent;
