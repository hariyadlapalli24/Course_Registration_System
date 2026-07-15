import React from "react";
import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

export default function Header() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate("/");
  };

  return (
    <header className="site-header">
      <Link to="/" className="site-header__brand">
        <span className="site-header__crest">MRU</span>
        <span>Malla Reddy University</span>
      </Link>

      <nav className="site-header__actions">
        {!user && (
          <>
            <Link to="/login" className="btn btn--ghost">Login</Link>
            <Link to="/register" className="btn btn--primary">Register</Link>
          </>
        )}

        {user && user.role === "USER" && (
          <>
            <Link to="/dashboard" className="btn btn--ghost">Dashboard</Link>
            <button className="btn btn--primary" onClick={handleLogout}>Log out</button>
          </>
        )}

        {user && user.role === "ADMIN" && (
          <>
            <Link to="/admin" className="btn btn--ghost">Admin</Link>
            <button className="btn btn--primary" onClick={handleLogout}>Log out</button>
          </>
        )}
      </nav>
    </header>
  );
}
