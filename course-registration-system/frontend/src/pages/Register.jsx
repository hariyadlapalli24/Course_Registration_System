import React, { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import { registerUser } from "../api/client";

const initialForm = {
  name: "",
  department: "",
  rollNumber: "",
  email: "",
  password: "",
};

export default function Register() {
  const [form, setForm] = useState(initialForm);
  const [errors, setErrors] = useState({});
  const [status, setStatus] = useState({ loading: false, error: "", success: false });
  const navigate = useNavigate();

  const handleChange = (event) => {
    const { name, value } = event.target;
    setForm((prev) => ({ ...prev, [name]: value }));
  };

  const validate = () => {
    const nextErrors = {};
    Object.entries(form).forEach(([key, value]) => {
      if (!value.trim()) {
        nextErrors[key] = "This field is required.";
      }
    });
    if (form.password && form.password.length < 6) {
      nextErrors.password = "Password must be at least 6 characters.";
    }
    if (form.email && !/^\S+@\S+\.\S+$/.test(form.email)) {
      nextErrors.email = "Enter a valid email address.";
    }
    setErrors(nextErrors);
    return Object.keys(nextErrors).length === 0;
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    if (!validate()) return;

    setStatus({ loading: true, error: "", success: false });
    try {
      await registerUser(form);
      setStatus({ loading: false, error: "", success: true });
      setForm(initialForm);
      setTimeout(() => navigate("/login"), 1200);
    } catch (err) {
      const message = err.response?.data?.message || "Registration failed. Please try again.";
      setStatus({ loading: false, error: message, success: false });
    }
  };

  return (
    <section className="auth-card">
      <h2>Create your student account</h2>
      <p className="auth-card__hint">Every account created here is a standard student account.</p>

      {status.success && (
        <div className="alert alert--success">Registration successful, please login.</div>
      )}
      {status.error && <div className="alert alert--error">{status.error}</div>}

      <form onSubmit={handleSubmit} noValidate>
        <div className="form-field">
          <label htmlFor="name">Name</label>
          <input id="name" name="name" value={form.name} onChange={handleChange} />
          {errors.name && <span className="form-field__error">{errors.name}</span>}
        </div>

        <div className="form-field">
          <label htmlFor="department">Department</label>
          <input id="department" name="department" value={form.department} onChange={handleChange} />
          {errors.department && <span className="form-field__error">{errors.department}</span>}
        </div>

        <div className="form-field">
          <label htmlFor="rollNumber">Roll number</label>
          <input id="rollNumber" name="rollNumber" value={form.rollNumber} onChange={handleChange} />
          {errors.rollNumber && <span className="form-field__error">{errors.rollNumber}</span>}
        </div>

        <div className="form-field">
          <label htmlFor="email">Email</label>
          <input id="email" name="email" type="email" value={form.email} onChange={handleChange} />
          {errors.email && <span className="form-field__error">{errors.email}</span>}
        </div>

        <div className="form-field">
          <label htmlFor="password">Password</label>
          <input id="password" name="password" type="password" value={form.password} onChange={handleChange} />
          {errors.password && <span className="form-field__error">{errors.password}</span>}
        </div>

        <button className="btn btn--primary btn--block" type="submit" disabled={status.loading}>
          {status.loading ? "Creating account…" : "Register"}
        </button>
      </form>

      <p className="auth-card__footer">
        Already have an account? <Link to="/login">Login</Link>
      </p>
    </section>
  );
}
