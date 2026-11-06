
import { createContext, useContext, useState } from "react";
import api from "../services/api";

const AuthContext = createContext(null);

// Đọc thông tin trong JWT để hiển thị tài khoản và vai trò.
// Backend vẫn là nơi xác thực token và quyết định quyền truy cập.
function getUserFromToken(token, email) {
    try {
        const payload = token.split(".")[1];
        const base64 = payload
            .replace(/-/g, "+")
            .replace(/_/g, "/");

        const decoded = atob(base64);
        const bytes = Uint8Array.from(
            decoded,
            (char) => char.charCodeAt(0)
        );

        const data = JSON.parse(
            new TextDecoder().decode(bytes)
        );

        return {
            id: data.userId,
            email,
            username: email,
            name: email,
            role: data.role || "CUSTOMER",
        };
    } catch {
        return {
            id: null,
            email,
            username: email,
            name: email,
            role: "CUSTOMER",
        };
    }
}

export function AuthProvider({ children }) {
    const [user, setUser] = useState(() => {
        try {
            return JSON.parse(localStorage.getItem("user")) || null;
        } catch {
            return null;
        }
    });

    const login = async (username, password) => {
        const email = username.trim();

        if (!email || !password.trim()) {
            throw new Error("Vui lòng nhập email và mật khẩu.");
        }

        // Không sử dụng token demo cũ khi đăng nhập.
        localStorage.removeItem("token");

        const response = await api.post("/auth/login", {
            email,
            password,
        });

        // Backend trả về ApiResponse có trường data.
        const result = response.data;

        if (
            !result ||
            result.authenticated !== true ||
            !result.token
        ) {
            throw new Error("Email hoặc mật khẩu không chính xác.");
        }

        const token = result.token;
        const userData = getUserFromToken(token, email);

        localStorage.setItem("token", token);
        localStorage.setItem("user", JSON.stringify(userData));

        setUser(userData);

        return userData;
    };

    const logout = async () => {
        const token = localStorage.getItem("token");

        try {
            if (token) {
                await api.post("/auth/logout");
            }
        } catch (error) {
            console.error("Không thể gọi API đăng xuất:", error);
        } finally {
            localStorage.removeItem("token");
            localStorage.removeItem("user");
            setUser(null);
        }
    };

    return (
        <AuthContext.Provider
            value={{
                user,
                login,
                logout,
                isLoggedIn: !!user,
            }}
        >
            {children}
        </AuthContext.Provider>
    );
}

export function useAuth() {
    return useContext(AuthContext);
}
