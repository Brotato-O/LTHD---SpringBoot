
import { useState } from "react";
import { Link, useLocation, useNavigate } from "react-router-dom";
import { LockKeyhole, Store, UserRound } from "lucide-react";
import { useAuth } from "../context/AuthContext";

export default function Login() {
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState("");
    const [loading, setLoading] = useState(false);

    const { login } = useAuth();
    const navigate = useNavigate();
    const location = useLocation();

    const handleSubmit = async (event) => {
        event.preventDefault();
        setError("");
        setLoading(true);

        try {
            const loggedInUser = await login(email, password);

            if (loggedInUser.role === "ADMIN") {
                navigate("/admin");
            } else {
                navigate("/");
            }
        } catch (err) {
            setError(
                err.response?.data?.message ||
                err.message ||
                "Đăng nhập thất bại."
            );
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="flex min-h-[70vh] items-center justify-center px-4 py-12">
            <div className="grid w-full max-w-5xl overflow-hidden rounded-3xl border border-slate-200 bg-white shadow-xl md:grid-cols-2">
                <div className="hidden flex-col justify-between bg-gradient-to-br from-indigo-700 to-violet-700 p-10 text-white md:flex">
                    <Link to="/" className="flex items-center gap-3 text-2xl font-extrabold">
                        <Store size={30} /> ShopEase
                    </Link>
                    <div>
                        <p className="text-sm font-bold uppercase tracking-widest text-indigo-200">
                            Chào mừng trở lại
                        </p>
                        <h1 className="mt-4 text-4xl font-black leading-tight">
                            Mua sắm thuận tiện bắt đầu từ đây.
                        </h1>
                        <p className="mt-4 leading-7 text-indigo-100">
                            Đăng nhập để trải nghiệm giao diện hệ thống bán hàng.
                        </p>
                    </div>
                    <p className="text-xs text-indigo-200">Giao diện demo đồ án</p>
                </div>

                <div className="p-6 sm:p-10">
                    <Link to="/" className="text-sm font-semibold text-indigo-600 hover:text-indigo-800">
                        ← Quay về trang chủ
                    </Link>

                    <h2 className="mt-8 text-3xl font-extrabold text-slate-900">
                        Đăng nhập
                    </h2>
                    <p className="mt-2 text-sm text-slate-500">
                        Nhập tài khoản để tiếp tục.
                    </p>
                    {location.state?.message && (
                        <p className="mt-4 rounded-xl bg-emerald-50 px-4 py-3 text-sm text-emerald-700">
                            {location.state.message}
                        </p>
                    )}

                    <form onSubmit={handleSubmit} className="mt-8 space-y-5">
                        <div>
                            <label htmlFor="email" className="mb-2 block text-sm font-semibold text-slate-700">
                                Email
                            </label>
                            <div className="flex items-center gap-3 rounded-xl border border-slate-200 px-4 focus-within:border-indigo-500 focus-within:ring-4 focus-within:ring-indigo-50">
                                <UserRound size={18} className="text-slate-400" />
                                <input
                                    id="email"
                                    type="email"
                                    value={email}
                                    onChange={(e) => setEmail(e.target.value)}
                                    placeholder="Nhập email đăng ký"
                                    autoComplete="email"
                                    className="w-full py-3.5 outline-none"
                                    required
                                />
                            </div>
                        </div>

                        <div>
                            <label htmlFor="password" className="mb-2 block text-sm font-semibold text-slate-700">
                                Mật khẩu
                            </label>
                            <div className="flex items-center gap-3 rounded-xl border border-slate-200 px-4 focus-within:border-indigo-500 focus-within:ring-4 focus-within:ring-indigo-50">
                                <LockKeyhole size={18} className="text-slate-400" />
                                <input
                                    id="password"
                                    type="password"
                                    value={password}
                                    onChange={(e) => setPassword(e.target.value)}
                                    placeholder="Nhập mật khẩu"
                                    autoComplete="current-password"
                                    className="w-full py-3.5 outline-none"
                                    required
                                />
                            </div>
                        </div>

                        {error && (
                            <p role="alert" className="rounded-xl bg-red-50 px-4 py-3 text-sm text-red-600">
                                {error}
                            </p>
                        )}

                        <button
                            type="submit"
                            disabled={loading}
                            className="w-full rounded-xl bg-indigo-600 py-3.5 font-bold text-white transition hover:bg-indigo-700 disabled:opacity-60"
                        >
                            {loading ? "Đang xử lý..." : "Đăng nhập"}
                        </button>
                    </form>

                    <p className="mt-5 text-center text-sm text-slate-500">
                        Chưa có tài khoản?{" "}
                        <Link to="/register" className="font-bold text-indigo-600 hover:text-indigo-800">
                            Đăng ký ngay
                        </Link>
                    </p>
                </div>
            </div>
        </div>
    );
}
