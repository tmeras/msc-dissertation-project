import axios from "../api/axiosConfig"
import { createContext, useContext, useEffect, useMemo, useState } from "react";

const AuthContext = createContext();

const AuthProvider = ({ children }) => {
    // States for holding the authentication token and the authenticated user
    const [token, setToken_] = useState(localStorage.getItem("token"));
    const [user, setUser_] = useState(null);

    // Function to set the authentication token
    const setToken = (newToken) => {
        setToken_(newToken);
    };

    // Function to set the authenticated user
    const setUser = (newUser) => {
        setUser_(newUser);
    };

    useEffect(() => {
        if (token) {
            axios.defaults.headers.common["Authorization"] = "Bearer " + token;

            // Get user data
            axios.get(`/auth/me`)
                .then(res => {
                    setUser_(res.data)
                    localStorage.setItem('token', token);
                })
                .catch(error => {
                    setUser_(null)
                    setToken_(null)
                })

        } else {
            delete axios.defaults.headers.common["Authorization"];
            localStorage.removeItem('token')
            setUser_(null)
        }
    }, [token]);

    // Memoized value of the authentication context
    const contextValue = useMemo(() => ({
        token,
        setToken,
        user,
        setUser,
    }), [token, user]);


    // Provide the authentication context to the children components
    return (
        <AuthContext.Provider value={contextValue}>
            {children}
        </AuthContext.Provider>
    );
};

// Custom hook for accesssing authentication context
export const useAuth = () => {
    return useContext(AuthContext);
};

export default AuthProvider;