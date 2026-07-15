import React, { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import { loginUser } from "../api/client";
import { useAuth } from "../context/AuthContext";

export default function Login() {
  const [form, setForm] = useState({ email: "", password: "" });
  const [status, setStatus] = useState({ loading: false, error: "" });
  const { login } = useAuth();
  const navigate = useNavigate();

  const handleChange = (event) => {
    const { name, value } = event.target;
    setForm((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    setStatus({ loading: true, error: "" });

    try {
      const { data } = await loginUser(form);
      login(data);
      // Role-based domain selection comes from what the backend returned,
      // not from anything the client asserted.
      navigate(data.user.role === "ADMIN" ? "/admin" : "/dashboard");
    } catch (err) {
      const message = err.response?.data?.message || "Invalid credentials";
      setStatus({ loading: false, error: message });
    }
  };

  return (
    <section className="auth-card">
      <h2>Sign in</h2>

      {status.error && <div className="alert alert--error">{status.error}</div>}

      <form onSubmit={handleSubmit}>
        <div className="form-field">
          <label htmlFor="email">Email</label>
          <input id="email" name="email" type="email" required value={form.email} onChange={handleChange} />
        </div>

        <div className="form-field">
          <label htmlFor="password">Password</label>
          <input id="password" name="password" type="password" required value={form.password} onChange={handleChange} />
        </div>

        <button className="btn btn--primary btn--block" type="submit" disabled={status.loading}>
          {status.loading ? "Signing in…" : "Login"}
        </button>
      </form>

      <p className="auth-card__footer">
        Need an account? <Link to="/register">Register</Link>
      </p>
    </section>
  );
}
