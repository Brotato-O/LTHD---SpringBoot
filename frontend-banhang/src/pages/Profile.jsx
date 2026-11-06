import { useEffect, useState } from "react";
import { ArrowLeft, Mail, Shield, UserRound } from "lucide-react";
import { Link } from "react-router-dom";
import { toast } from "react-toastify";
import { useAuth } from "../context/AuthContext";
import api from "../services/api";

export default function Profile() {
    const { user } = useAuth();
    const [profile, setProfile] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        if (!user?.id) {
            setLoading(false);
            return;
        }

        const loadProfile = async () => {
            try {
                const { data } = await api.get(`/api/v1/users/${user.id}`);
                setProfile(data);
            } catch (error) {
                console.error("Không thể tải thông tin người dùng:", error);
                toast.error(
                    error.response?.data?.message ||
                    "Không thể tải thông tin người dùng."
                );
            } finally {
                setLoading(false);
            }
        };

        loadProfile();
    }, [user?.id]);

    if (!user?.id) {
        return (
            <div className="mx-auto max-w-3xl px-4 py-16 text-center">
                <p className="text-slate-600">Vui lòng đăng nhập để xem thông tin cá nhân.</p>
                <Link to="/login" className="mt-5 inline-flex rounded-xl bg-indigo-600 px-5 py-3 font-bold text-white">
                    Đăng nhập
                </Link>
            </div>
        );
    }

    if (loading) {
        return <p className="mx-auto max-w-3xl px-4 py-16 text-center text-slate-500">Đang tải thông tin...</p>;
    }

    return (
        <div className="mx-auto max-w-3xl px-4 py-10 sm:px-6 lg:px-8">
            <Link to="/" className="inline-flex items-center gap-2 text-sm font-semibold text-slate-600 hover:text-indigo-700">
                <ArrowLeft size={17} /> Về trang chủ
            </Link>

            <section className="mt-8 overflow-hidden rounded-3xl border border-slate-200 bg-white shadow-sm">
                <div className="bg-gradient-to-r from-indigo-700 to-violet-600 px-6 py-10 text-white sm:px-10">
                    <div className="flex h-16 w-16 items-center justify-center rounded-2xl bg-white/15">
                        <UserRound size={32} />
                    </div>
                    <h1 className="mt-5 text-3xl font-black">{profile?.fullName || profile?.username}</h1>
                    <p className="mt-1 text-indigo-100">Thông tin tài khoản</p>
                </div>

                <div className="grid gap-4 p-6 sm:grid-cols-2 sm:p-10">
                    <div className="rounded-2xl bg-slate-50 p-5">
                        <p className="text-sm text-slate-500">Tên đăng nhập</p>
                        <p className="mt-2 font-bold text-slate-900">{profile?.username}</p>
                    </div>
                    <div className="rounded-2xl bg-slate-50 p-5">
                        <p className="flex items-center gap-2 text-sm text-slate-500"><Mail size={16} /> Email</p>
                        <p className="mt-2 font-bold text-slate-900">{profile?.email}</p>
                    </div>
                    <div className="rounded-2xl bg-slate-50 p-5">
                        <p className="text-sm text-slate-500">Họ và tên</p>
                        <p className="mt-2 font-bold text-slate-900">{profile?.fullName}</p>
                    </div>
                    <div className="rounded-2xl bg-slate-50 p-5">
                        <p className="flex items-center gap-2 text-sm text-slate-500"><Shield size={16} /> Vai trò</p>
                        <p className="mt-2 font-bold text-slate-900">{profile?.role}</p>
                    </div>
                </div>
            </section>
        </div>
    );
}
