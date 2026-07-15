import React, { useEffect, useState } from "react";
import client, { updateUser, deleteUser, deleteEnrollment } from "../api/client";

export default function AdminDashboard() {
  const [enrollments, setEnrollments] = useState([]);
  const [users, setUsers] = useState([]);
  const [error, setError] = useState("");
  const [actionError, setActionError] = useState("");

  const [editingUserId, setEditingUserId] = useState(null);
  const [editForm, setEditForm] = useState({ name: "", department: "", rollNumber: "", email: "" });
  const [savingEdit, setSavingEdit] = useState(false);

  useEffect(() => {
    loadData();
  }, []);

  function loadData() {
    Promise.all([client.get("/admin/enrollments"), client.get("/admin/users")])
      .then(([enrollmentsRes, usersRes]) => {
        setEnrollments(enrollmentsRes.data);
        setUsers(usersRes.data);
      })
      .catch(() => setError("Could not load admin data."));
  }

  function startEdit(user) {
    setActionError("");
    setEditingUserId(user.id);
    setEditForm({
      name: user.name,
      department: user.department,
      rollNumber: user.rollNumber,
      email: user.email,
    });
  }

  function cancelEdit() {
    setEditingUserId(null);
  }

  function handleEditFieldChange(field, value) {
    setEditForm((prev) => ({ ...prev, [field]: value }));
  }

  async function saveEdit(id) {
    setSavingEdit(true);
    setActionError("");
    try {
      const res = await updateUser(id, editForm);
      setUsers((prev) => prev.map((u) => (u.id === id ? res.data : u)));
      setEditingUserId(null);
    } catch (err) {
      setActionError(err.response?.data?.message || "Could not save changes.");
    } finally {
      setSavingEdit(false);
    }
  }

  async function handleDeleteUser(user) {
    if (!window.confirm(`Delete ${user.name}? This also removes their enrollments.`)) {
      return;
    }
    setActionError("");
    try {
      await deleteUser(user.id);
      setUsers((prev) => prev.filter((u) => u.id !== user.id));
      setEnrollments((prev) => prev.filter((e) => e.rollNumber !== user.rollNumber));
      if (editingUserId === user.id) setEditingUserId(null);
    } catch (err) {
      setActionError(err.response?.data?.message || "Could not delete user.");
    }
  }

  async function handleDeleteEnrollment(enrollment) {
    if (!window.confirm(`Remove ${enrollment.studentName} from ${enrollment.courseCode}?`)) {
      return;
    }
    setActionError("");
    try {
      await deleteEnrollment(enrollment.enrollmentId);
      setEnrollments((prev) => prev.filter((e) => e.enrollmentId !== enrollment.enrollmentId));
    } catch (err) {
      setActionError(err.response?.data?.message || "Could not remove enrollment.");
    }
  }

  if (error) return <div className="alert alert--error">{error}</div>;

  return (
    <section>
      <h2>Admin dashboard</h2>
      <p className="page-subtitle">Visible only to accounts with the ADMIN role.</p>

      {actionError && <div className="alert alert--error">{actionError}</div>}

      <h3>Registered students ({users.length})</h3>
      <table className="admin-table">
        <thead>
          <tr>
            <th>Name</th>
            <th>Department</th>
            <th>Roll number</th>
            <th>Email</th>
            <th>Role</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {users.map((u) => {
            const isEditing = editingUserId === u.id;
            return (
              <tr key={u.id}>
                {isEditing ? (
                  <>
                    <td>
                      <input
                        className="admin-table__input"
                        value={editForm.name}
                        onChange={(e) => handleEditFieldChange("name", e.target.value)}
                      />
                    </td>
                    <td>
                      <input
                        className="admin-table__input"
                        value={editForm.department}
                        onChange={(e) => handleEditFieldChange("department", e.target.value)}
                      />
                    </td>
                    <td>
                      <input
                        className="admin-table__input"
                        value={editForm.rollNumber}
                        onChange={(e) => handleEditFieldChange("rollNumber", e.target.value)}
                      />
                    </td>
                    <td>
                      <input
                        className="admin-table__input"
                        value={editForm.email}
                        onChange={(e) => handleEditFieldChange("email", e.target.value)}
                      />
                    </td>
                    <td>{u.role}</td>
                    <td className="admin-table__actions">
                      <button
                        type="button"
                        className="btn btn--primary btn--small"
                        onClick={() => saveEdit(u.id)}
                        disabled={savingEdit}
                      >
                        {savingEdit ? "Saving…" : "Save"}
                      </button>
                      <button
                        type="button"
                        className="btn btn--ghost btn--small"
                        onClick={cancelEdit}
                        disabled={savingEdit}
                      >
                        Cancel
                      </button>
                    </td>
                  </>
                ) : (
                  <>
                    <td>{u.name}</td>
                    <td>{u.department}</td>
                    <td>{u.rollNumber}</td>
                    <td>{u.email}</td>
                    <td>{u.role}</td>
                    <td className="admin-table__actions">
                      <button type="button" className="btn btn--ghost btn--small" onClick={() => startEdit(u)}>
                        Edit
                      </button>
                      <button
                        type="button"
                        className="btn btn--danger btn--small"
                        onClick={() => handleDeleteUser(u)}
                      >
                        Delete
                      </button>
                    </td>
                  </>
                )}
              </tr>
            );
          })}
        </tbody>
      </table>

      <h3>Enrollments ({enrollments.length})</h3>
      <table className="admin-table">
        <thead>
          <tr>
            <th>Student</th>
            <th>Roll number</th>
            <th>Course</th>
            <th>Enrolled at</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {enrollments.map((e) => (
            <tr key={e.enrollmentId}>
              <td>{e.studentName}</td>
              <td>{e.rollNumber}</td>
              <td>{e.courseCode} — {e.courseName}</td>
              <td>{e.enrolledAt}</td>
              <td className="admin-table__actions">
                <button
                  type="button"
                  className="btn btn--danger btn--small"
                  onClick={() => handleDeleteEnrollment(e)}
                >
                  Delete
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </section>
  );
}
