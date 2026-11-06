import { useEffect, useState } from "react";
import { LockKeyhole, ShieldCheck, Store } from "lucide-react";
import { Link, useLocation, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

export default function AdminLogin() {
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState("");
    const [loading, setLoading] = useState(false);
    const { login, user, logout } = useAuth();
    const navigate = useNavigate();
    const location = useLocation();

    useEffect(() => {
        if (user?.role === "ADMIN") {
            navigate("/admin", { replace: true });
        }
    }, [navigate, user?.role]);

    const handleSubmit = async (event) => {
        event.preventDefault();
        setError("");
        setLoading(true);

        try {
            const loggedInUser = await login(email, password);

            if (loggedInUser.role !== "ADMIN") {
                await logout();
                setError("Tài khoản này không có quyền truy cập khu vực quản trị.");
                return;
            }

            navigate("/admin", { replace: true });
        } catch (requestError) {
            setError(
                requestError.response?.data?.message ||
                requestError.message ||
                "Đăng nhập quản trị thất bại."
            );
        } finally {
            setLoading(false);
        }
    };

    return (
        <main className="flex min-h-screen items-center justify-center bg-slate-950 px-4 py-12">
            <div className="grid w-full max-w-5xl overflow-hidden rounded-3xl border border-slate-800 bg-slate-900 shadow-2xl lg:grid-cols-[1.05fr_0.95fr]">
                <section className="hidden flex-col justify-between bg-gradient-to-br from-indigo-700 via-indigo-800 to-slate-950 p-10 text-white lg:flex">
                    <Link to="/" className="flex items-center gap-3 text-2xl font-black">
                        <Store size={30} /> ShopEase
                    </Link>
                    <div>
                        <div className="flex h-14 w-14 items-center justify-center rounded-2xl bg-white/15">
                            <ShieldCheck size={30} />
                        </div>
                        <p className="mt-8 text-sm font-bold uppercase tracking-[0.25em] text-indigo-200">
                            Khu vực bảo mật
                        </p>
                        <h1 className="mt-4 text-4xl font-black leading-tight">
                            Đăng nhập quản trị hệ thống.
                        </h1>
                        <p className="mt-4 max-w-md leading-7 text-indigo-100">
                            Chỉ tài khoản có quyền Admin mới được truy cập trang quản lý.
                        </p>
                    </div>
                    <p className="text-xs text-indigo-300">ShopEase Admin Panel</p>
                </section>

                <section className="bg-white p-6 sm:p-10">
                    <Link to="/" className="text-sm font-semibold text-indigo-600 hover:text-indigo-800">
                        ← Về cửa hàng
                    </Link>
                    <div className="mt-10 flex h-12 w-12 items-center justify-center rounded-2xl bg-indigo-50 text-indigo-600">
                        <LockKeyhole size={24} />
                    </div>
                    <h2 className="mt-6 text-3xl font-black text-slate-900">Admin Login</h2>
                    <p className="mt-2 text-sm text-slate-500">
                        Đăng nhập bằng tài khoản quản trị viên.
                    </p>

                    {(error || location.state?.message) && (
                        <p className="mt-5 rounded-xl bg-red-50 px-4 py-3 text-sm text-red-700">
                            {error || location.state.message}
                        </p>
                    )}

                    <form onSubmit={handleSubmit} className="mt-8 space-y-5">
                        <div>
                            <label htmlFor="admin-email" className="mb-2 block text-sm font-semibold text-slate-700">
                                Email quản trị
                            </label>
                            <input
                                id="admin-email"
                                type="email"
                                value={email}
                                onChange={(event) => setEmail(event.target.value)}
                                className="w-full rounded-xl border border-slate-200 px-4 py-3.5 outline-none focus:border-indigo-500 focus:ring-4 focus:ring-indigo-50"
                                placeholder="admin@example.com"
                                autoComplete="username"
                                required
                            />
                        </div>
                        <div>
                            <label htmlFor="admin-password" className="mb-2 block text-sm font-semibold text-slate-700">
                                Mật khẩu
                            </label>
                            <input
                                id="admin-password"
                                type="password"
                                value={password}
                                onChange={(event) => setPassword(event.target.value)}
                                className="w-full rounded-xl border border-slate-200 px-4 py-3.5 outline-none focus:border-indigo-500 focus:ring-4 focus:ring-indigo-50"
                                placeholder="Nhập mật khẩu"
                                autoComplete="current-password"
                                required
                            />
                        </div>
                        <button
                            type="submit"
                            disabled={loading}
                            className="w-full rounded-xl bg-indigo-700 px-5 py-3.5 font-bold text-white transition hover:bg-indigo-800 disabled:cursor-not-allowed disabled:opacity-60"
                        >
                            {loading ? "Đang xác thực..." : "Đăng nhập Admin"}
                        </button>
                    </form>
                </section>
            </div>
        </main>
    );
}
