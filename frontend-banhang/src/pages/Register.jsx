import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { LockKeyhole, Mail, Store, UserRound } from "lucide-react";
import api from "../services/api";

function getErrorMessage(error) {
    const responseData = error.response?.data;

    if (!responseData) {
        return "Không thể kết nối đến backend. Hãy kiểm tra Spring Boot đang chạy.";
    }

    const validationErrors = responseData.data;
    if (
        validationErrors &&
        typeof validationErrors === "object" &&
        !Array.isArray(validationErrors)
    ) {
        const messages = Object.values(validationErrors).filter(Boolean);
        if (messages.length > 0) {
            return messages.join(" ");
        }
    }

    return (
        responseData.message ||
        responseData.error ||
        error.message ||
        "Đăng ký thất bại."
    );
}

export default function Register() {
    const [form, setForm] = useState({
        username: "",
        email: "",
        fullName: "",
        password: "",
        confirmPassword: "",
    });
    const [error, setError] = useState("");
    const [loading, setLoading] = useState(false);
    const navigate = useNavigate();

    const handleChange = (event) => {
        const { name, value } = event.target;
        setForm((current) => ({ ...current, [name]: value }));
    };

    const handleSubmit = async (event) => {
        event.preventDefault();
        setError("");

        if (form.password !== form.confirmPassword) {
            setError("Mật khẩu xác nhận không khớp.");
            return;
        }

        try {
            setLoading(true);
            await api.post("/api/v1/users", {
                username: form.username.trim(),
                password: form.password,
                email: form.email.trim(),
                fullName: form.fullName.trim(),
            });
            navigate("/login", {
                replace: true,
                state: { message: "Đăng ký thành công. Vui lòng đăng nhập." },
            });
        } catch (err) {
            console.error("Lỗi đăng ký:", err.response?.data || err);
            setError(getErrorMessage(err));
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
                            Tạo tài khoản mới
                        </p>
                        <h1 className="mt-4 text-4xl font-black leading-tight">
                            Bắt đầu mua sắm dễ dàng hơn.
                        </h1>
                    </div>
                    <p className="text-xs text-indigo-200">Tài khoản mới mặc định là khách hàng</p>
                </div>

                <div className="p-6 sm:p-10">
                    <Link to="/login" className="text-sm font-semibold text-indigo-600 hover:text-indigo-800">
                        ← Đã có tài khoản? Đăng nhập
                    </Link>
                    <h2 className="mt-8 text-3xl font-extrabold text-slate-900">Đăng ký</h2>
                    <p className="mt-2 text-sm text-slate-500">Tạo tài khoản khách hàng của bạn.</p>

                    <form onSubmit={handleSubmit} className="mt-8 space-y-4">
                        <div>
                            <label htmlFor="fullName" className="mb-2 block text-sm font-semibold text-slate-700">Họ và tên</label>
                            <div className="flex items-center gap-3 rounded-xl border border-slate-200 px-4 focus-within:border-indigo-500 focus-within:ring-4 focus-within:ring-indigo-50">
                                <UserRound size={18} className="text-slate-400" />
                                <input id="fullName" name="fullName" value={form.fullName} onChange={handleChange} placeholder="Nguyễn Văn A" className="w-full py-3.5 outline-none" required />
                            </div>
                        </div>
                        <div>
                            <label htmlFor="username" className="mb-2 block text-sm font-semibold text-slate-700">Tên đăng nhập</label>
                            <input id="username" name="username" value={form.username} onChange={handleChange} placeholder="username" autoComplete="username" className="w-full rounded-xl border border-slate-200 px-4 py-3.5 outline-none focus:border-indigo-500 focus:ring-4 focus:ring-indigo-50" minLength={3} maxLength={50} required />
                        </div>
                        <div>
                            <label htmlFor="email" className="mb-2 block text-sm font-semibold text-slate-700">Email</label>
                            <div className="flex items-center gap-3 rounded-xl border border-slate-200 px-4 focus-within:border-indigo-500 focus-within:ring-4 focus-within:ring-indigo-50">
                                <Mail size={18} className="text-slate-400" />
                                <input id="email" name="email" type="email" value={form.email} onChange={handleChange} placeholder="email@example.com" autoComplete="email" className="w-full py-3.5 outline-none" required />
                            </div>
                        </div>
                        <div>
                            <label htmlFor="password" className="mb-2 block text-sm font-semibold text-slate-700">Mật khẩu</label>
                            <div className="flex items-center gap-3 rounded-xl border border-slate-200 px-4 focus-within:border-indigo-500 focus-within:ring-4 focus-within:ring-indigo-50">
                                <LockKeyhole size={18} className="text-slate-400" />
                                <input id="password" name="password" type="password" value={form.password} onChange={handleChange} placeholder="Ít nhất 6 ký tự" autoComplete="new-password" className="w-full py-3.5 outline-none" minLength={6} required />
                            </div>
                        </div>
                        <div>
                            <label htmlFor="confirmPassword" className="mb-2 block text-sm font-semibold text-slate-700">Xác nhận mật khẩu</label>
                            <input id="confirmPassword" name="confirmPassword" type="password" value={form.confirmPassword} onChange={handleChange} placeholder="Nhập lại mật khẩu" autoComplete="new-password" className="w-full rounded-xl border border-slate-200 px-4 py-3.5 outline-none focus:border-indigo-500 focus:ring-4 focus:ring-indigo-50" required />
                        </div>

                        {error && <p role="alert" className="rounded-xl bg-red-50 px-4 py-3 text-sm text-red-600">{error}</p>}
                        <button type="submit" disabled={loading} className="w-full rounded-xl bg-indigo-600 py-3.5 font-bold text-white transition hover:bg-indigo-700 disabled:opacity-60">
                            {loading ? "Đang tạo tài khoản..." : "Đăng ký"}
                        </button>
                    </form>
                </div>
            </div>
        </div>
    );
}
