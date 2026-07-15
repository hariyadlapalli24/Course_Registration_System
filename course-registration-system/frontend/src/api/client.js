import axios from "axios";

const API_BASE_URL = process.env.REACT_APP_API_BASE_URL || "http://localhost:8080/api";

const client = axios.create({
  baseURL: API_BASE_URL,
});

// Attach the session token (if we have one) to every outgoing request.
client.interceptors.request.use((config) => {
  const token = localStorage.getItem("abc_token");
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export const registerUser = (payload) => client.post("/users/register", payload);
export const loginUser = (payload) => client.post("/users/login", payload);
export const fetchUser = (id) => client.get(`/users/${id}`);
export const fetchCourses = () => client.get("/courses");
export const submitEnrollment = (payload) => client.post("/enrollments", payload);

export const updateUser = (id, payload) => client.put(`/admin/users/${id}`, payload);
export const deleteUser = (id) => client.delete(`/admin/users/${id}`);
export const deleteEnrollment = (id) => client.delete(`/admin/enrollments/${id}`);

export default client;
