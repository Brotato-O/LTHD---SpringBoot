
import { Link, NavLink, Outlet, useNavigate } from "react-router-dom";
import {
    LayoutDashboard,
    Package,
    ShoppingBag,
    Users,
    Store,
    LogOut,
    Menu,
    X,
} from "lucide-react";
import { useState } from "react";
import { useAuth } from "../context/AuthContext";

const menuItems = [
    { to: "/admin", label: "Tổng quan", icon: LayoutDashboard, end: true },
    { to: "/admin/products", label: "Sản phẩm", icon: Package },
    { to: "/admin/orders", label: "Đơn hàng", icon: ShoppingBag },
    { to: "/admin/users", label: "Người dùng", icon: Users },
];

export default function AdminLayout() {
    const [mobileOpen, setMobileOpen] = useState(false);
    const { user, logout } = useAuth();
    const navigate = useNavigate();

    const handleLogout = () => {
        logout();
        navigate("/login");
    };

    const sidebar = (
        <div className="flex h-full flex-col bg-slate-950 text-slate-300">
            <Link to="/admin" className="flex h-20 items-center gap-3 border-b border-slate-800 px-6">
                <span className="rounded-xl bg-indigo-600 p-2 text-white">
                    <Store size={22} />
                </span>
                <div>
                    <p className="text-lg font-extrabold text-white">ShopEase</p>
                    <p className="text-xs text-slate-500">ADMIN PANEL</p>
                </div>
            </Link>

            <div className="px-4 py-6">
                <p className="mb-3 px-3 text-xs font-bold uppercase tracking-widest text-slate-500">
                    Quản lý
                </p>

                <nav className="space-y-1">
                    {menuItems.map((item) => {
                        const Icon = item.icon;

                        return (
                            <NavLink
                                key={item.to}
                                to={item.to}
                                end={item.end}
                                onClick={() => setMobileOpen(false)}
                                className={({ isActive }) =>
                                    `flex items-center gap-3 rounded-xl px-3 py-3 text-sm font-semibold transition ${isActive
                                        ? "bg-indigo-600 text-white shadow-lg shadow-indigo-950/30"
                                        : "text-slate-400 hover:bg-slate-900 hover:text-white"
                                    }`
                                }
                            >
                                <Icon size={19} />
                                {item.label}
                            </NavLink>
                        );
                    })}
                </nav>
            </div>

            <div className="mt-auto border-t border-slate-800 p-4">
                <div className="mb-3 rounded-xl bg-slate-900 p-3">
                    <p className="truncate text-sm font-semibold text-white">
                        {user?.name || "Quản trị viên"}
                    </p>
                    <p className="mt-1 text-xs text-slate-500">Tài khoản quản trị</p>
                </div>

                <button
                    onClick={handleLogout}
                    className="flex w-full items-center gap-3 rounded-xl px-3 py-3 text-sm font-semibold text-slate-400 hover:bg-slate-900 hover:text-white"
                >
                    <LogOut size={18} />
                    Đăng xuất
                </button>
            </div>
        </div>
    );

    return (
        <div className="min-h-screen bg-slate-100">
            <aside className="fixed inset-y-0 left-0 z-40 hidden w-64 lg:block">
                {sidebar}
            </aside>

            {mobileOpen && (
                <div className="fixed inset-0 z-50 lg:hidden">
                    <button
                        className="absolute inset-0 bg-black/50"
                        onClick={() => setMobileOpen(false)}
                        aria-label="Đóng menu"
                    />
                    <aside className="relative h-full w-72 max-w-[85vw]">
                        <button
                            onClick={() => setMobileOpen(false)}
                            className="absolute right-3 top-5 z-10 rounded-lg p-2 text-white hover:bg-slate-800"
                            aria-label="Đóng"
                        >
                            <X size={20} />
                        </button>
                        {sidebar}
                    </aside>
                </div>
            )}

            <div className="min-h-screen lg:pl-64">
                <header className="sticky top-0 z-20 flex h-16 items-center justify-between border-b border-slate-200 bg-white px-4 sm:px-8">
                    <div className="flex items-center gap-3">
                        <button
                            onClick={() => setMobileOpen(true)}
                            className="rounded-lg p-2 hover:bg-slate-100 lg:hidden"
                            aria-label="Mở menu"
                        >
                            <Menu size={21} />
                        </button>
                        <div>
                            <p className="font-bold text-slate-900">Khu vực quản trị</p>
                            <p className="hidden text-xs text-slate-500 sm:block">
                                Quản lý hệ thống bán hàng
                            </p>
                        </div>
                    </div>

                    <Link to="/" className="text-sm font-semibold text-indigo-600 hover:text-indigo-800">
                        Xem cửa hàng →
                    </Link>
                </header>

                <main className="p-4 sm:p-8">
                    <Outlet />
                </main>
            </div>
        </div>
    );
}
