
import { Link, NavLink } from "react-router-dom";
import { ShoppingCart, Store, LogOut, UserRound } from "lucide-react";
import { useAuth } from "../context/AuthContext";
import { useCart } from "../context/CartContext";

export default function Header() {
    const { user, logout } = useAuth();
    const { totalItems } = useCart();

    const linkClass = ({ isActive }) =>
        `text-sm font-medium transition ${isActive ? "text-indigo-600" : "text-slate-600 hover:text-indigo-600"
        }`;

    return (
        <header className="sticky top-0 z-30 border-b border-slate-200 bg-white/95 backdrop-blur">
            <div className="mx-auto flex max-w-7xl items-center justify-between gap-4 px-4 py-4 sm:px-6 lg:px-8">
                <Link to="/" className="flex shrink-0 items-center gap-2">
                    <span className="rounded-xl bg-indigo-600 p-2 text-white">
                        <Store size={22} />
                    </span>
                    <span className="text-xl font-extrabold tracking-tight text-slate-900">
                        Shop<span className="text-indigo-600">Ease</span>
                    </span>
                </Link>

                <nav className="hidden items-center gap-7 md:flex">
                    <NavLink to="/" className={linkClass}>Trang chủ</NavLink>
                    <NavLink to="/orders" className={linkClass}>Đơn hàng</NavLink>
                </nav>

                <div className="flex items-center gap-3">
                    <Link
                        to="/cart"
                        className="relative rounded-xl border border-slate-200 p-2.5 text-slate-700 transition hover:bg-slate-50"
                        aria-label="Giỏ hàng"
                    >
                        <ShoppingCart size={20} />
                        {totalItems > 0 && (
                            <span className="absolute -right-2 -top-2 flex h-5 min-w-5 items-center justify-center rounded-full bg-indigo-600 px-1 text-xs font-bold text-white">
                                {totalItems}
                            </span>
                        )}
                    </Link>

                    {user ? (
                        <div className="flex items-center gap-2">
                            <span className="hidden text-sm font-medium text-slate-700 sm:inline">
                                <UserRound className="mr-1 inline" size={16} />
                                {user.name}
                            </span>
                            <button
                                onClick={logout}
                                className="flex items-center gap-1 rounded-xl bg-slate-100 px-3 py-2.5 text-sm font-semibold text-slate-700 hover:bg-slate-200"
                            >
                                <LogOut size={16} />
                                <span className="hidden sm:inline">Thoát</span>
                            </button>
                        </div>
                    ) : (
                        <Link
                            to="/login"
                            className="rounded-xl bg-indigo-600 px-4 py-2.5 text-sm font-semibold text-white transition hover:bg-indigo-700"
                        >
                            Đăng nhập
                        </Link>
                    )}
                </div>
            </div>
        </header>
    );
}
