import React, { useEffect, useState } from "react";
import { fetchCourses, submitEnrollment } from "../api/client";
import { useAuth } from "../context/AuthContext";

export default function CourseRegistration() {
  const { user } = useAuth();
  const [courses, setCourses] = useState([]);
  const [selected, setSelected] = useState([]);
  const [status, setStatus] = useState({ loading: true, error: "", submitting: false, success: false });

  useEffect(() => {
    fetchCourses()
      .then(({ data }) => {
        setCourses(data);
        setStatus((prev) => ({ ...prev, loading: false }));
      })
      .catch(() =>
        setStatus({ loading: false, error: "Could not load courses.", submitting: false, success: false })
      );
  }, []);

  const toggleCourse = (id) => {
    setSelected((prev) => (prev.includes(id) ? prev.filter((c) => c !== id) : [...prev, id]));
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    if (selected.length === 0) return;

    setStatus((prev) => ({ ...prev, submitting: true, error: "", success: false }));
    try {
      await submitEnrollment({ userId: user.id, courseIds: selected });
      setStatus({ loading: false, submitting: false, error: "", success: true });
      setSelected([]);
    } catch (err) {
      const message = err.response?.data?.message || "Enrollment failed. Please try again.";
      setStatus({ loading: false, submitting: false, error: message, success: false });
    }
  };

  if (status.loading) return <p className="loading-text">Loading the course catalog…</p>;

  return (
    <section>
      <h2>Course catalog</h2>
      <p className="page-subtitle">Select every course you'd like to register for this term.</p>

      {status.success && <div className="alert alert--success">Registration successful.</div>}
      {status.error && <div className="alert alert--error">{status.error}</div>}

      <form onSubmit={handleSubmit}>
        <ul className="course-list">
          {courses.map((course) => (
            <li key={course.id} className="course-list__item">
              <label>
                <input
                  type="checkbox"
                  checked={selected.includes(course.id)}
                  onChange={() => toggleCourse(course.id)}
                />
                <span className="course-list__code">{course.courseCode}</span>
                <span className="course-list__name">{course.courseName}</span>
                {course.description && (
                  <span className="course-list__description">{course.description}</span>
                )}
              </label>
            </li>
          ))}
        </ul>

        <button className="btn btn--primary" type="submit" disabled={selected.length === 0 || status.submitting}>
          {status.submitting ? "Submitting…" : "Register"}
        </button>
      </form>
    </section>
  );
}
