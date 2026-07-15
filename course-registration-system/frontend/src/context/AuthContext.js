import React, { createContext, useContext, useEffect, useState } from "react";

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [initializing, setInitializing] = useState(true);

  useEffect(() => {
    const storedToken = localStorage.getItem("abc_token");
    const storedUser = localStorage.getItem("abc_user");
    if (storedToken && storedUser) {
      setUser(JSON.parse(storedUser));
    }
    setInitializing(false);
  }, []);

  const login = (loginResponse) => {
    localStorage.setItem("abc_token", loginResponse.token);
    localStorage.setItem("abc_user", JSON.stringify(loginResponse.user));
    setUser(loginResponse.user);
  };

  const logout = () => {
    localStorage.removeItem("abc_token");
    localStorage.removeItem("abc_user");
    setUser(null);
  };

  return (
    <AuthContext.Provider value={{ user, login, logout, initializing }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error("useAuth must be used within an AuthProvider");
  }
  return context;
}
