import { Link, Outlet } from "react-router-dom";
import { Store, Mail, Phone, Share2 } from "lucide-react";
import Header from "../components/Header";

export default function ClientLayout() {
    return (
        <div className="flex min-h-screen flex-col bg-slate-50">
            <Header />

            <main className="flex-1">
                <Outlet />
            </main>

            <footer className="mt-12 bg-slate-950 text-slate-300">
                <div className="mx-auto grid max-w-7xl gap-8 px-4 py-10 sm:grid-cols-2 sm:px-6 lg:grid-cols-3 lg:px-8">
                    <div>
                        <Link
                            to="/"
                            className="flex items-center gap-2 text-xl font-extrabold text-white"
                        >
                            <Store size={24} className="text-indigo-400" />
                            ShopEase
                        </Link>

                        <p className="mt-3 max-w-sm text-sm leading-6 text-slate-400">
                            Website bán hàng trực tuyến với trải nghiệm mua sắm đơn giản,
                            tiện lợi và thân thiện.
                        </p>
                    </div>

                    <div>
                        <h3 className="font-bold text-white">Liên kết</h3>

                        <div className="mt-3 flex flex-col gap-3 text-sm">
                            <Link to="/" className="hover:text-white">
                                Trang chủ
                            </Link>
                            <Link to="/cart" className="hover:text-white">
                                Giỏ hàng
                            </Link>
                            <Link to="/orders" className="hover:text-white">
                                Đơn hàng của tôi
                            </Link>
                        </div>
                    </div>

                    <div>
                        <h3 className="font-bold text-white">Liên hệ</h3>

                        <p className="mt-3 flex items-center gap-2 text-sm">
                            <Mail size={16} /> support@shopease.vn
                        </p>

                        <p className="mt-3 flex items-center gap-2 text-sm">
                            <Phone size={16} /> 0123 456 789
                        </p>

                        <p className="mt-3 flex items-center gap-2 text-sm">
                            <Share2 size={16} /> ShopEase
                        </p>
                    </div>
                </div>

                <div className="border-t border-slate-800 px-4 py-4 text-center text-xs text-slate-500">
                    © {new Date().getFullYear()} ShopEase — Giao diện đồ án Spring Boot.
                </div>
            </footer>
        </div>
    );
}
