import React, { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { fetchUser } from "../api/client";
import { useAuth } from "../context/AuthContext";

export default function Dashboard() {
  const { user } = useAuth();
  const [profile, setProfile] = useState(null);
  const [error, setError] = useState("");

  useEffect(() => {
    if (!user) return;
    fetchUser(user.id)
      .then(({ data }) => setProfile(data))
      .catch(() => setError("Could not load your profile."));
  }, [user]);

  if (error) return <div className="alert alert--error">{error}</div>;
  if (!profile) return <p className="loading-text">Loading your dashboard…</p>;

  return (
    <section>
      <div className="id-card">
        <div className="id-card__crest">AU</div>
        <div className="id-card__body">
          <p className="id-card__label">Student ID</p>
          <h2>{profile.name}</h2>
          <dl className="id-card__details">
            <div>
              <dt>Department</dt>
              <dd>{profile.department}</dd>
            </div>
            <div>
              <dt>Roll number</dt>
              <dd>{profile.rollNumber}</dd>
            </div>
            <div>
              <dt>Email</dt>
              <dd>{profile.email}</dd>
            </div>
          </dl>
        </div>
      </div>

      <div className="dashboard-actions">
        <Link to="/courses" className="btn btn--primary">Enroll for course registration</Link>
      </div>
    </section>
  );
}
