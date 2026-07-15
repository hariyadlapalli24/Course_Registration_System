import React from "react";
import { Navigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

/**
 * Gate for any route that requires a logged-in user.
 *
 * requireRole, if set, additionally hides the page from the wrong role.
 * This only controls what the UI *shows* -- the backend independently
 * re-checks the role (from its own token store) on every admin API call,
 * regardless of what this component decides to render.
 */
export default function ProtectedRoute({ children, requireRole }) {
  const { user, initializing } = useAuth();

  if (initializing) {
    return null;
  }

  if (!user) {
    return <Navigate to="/login" replace />;
  }

  if (requireRole && user.role !== requireRole) {
    return <Navigate to="/dashboard" replace />;
  }

  return children;
}
