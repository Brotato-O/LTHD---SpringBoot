import { useEffect, useMemo, useState } from "react";
import {
    DollarSign,
    ShoppingBag,
    Users,
    Package,
    TrendingUp,
} from "lucide-react";
import api from "../services/api";

const formatPrice = (price) =>
    new Intl.NumberFormat("vi-VN", {
        style: "currency",
        currency: "VND",
    }).format(Number(price) || 0);

const formatDate = (date) =>
    date ? new Date(date).toLocaleDateString("vi-VN") : "-";

const statStyles = [
    { label: "Doanh thu", icon: DollarSign, color: "bg-emerald-50 text-emerald-600" },
    { label: "Đơn hàng", icon: ShoppingBag, color: "bg-blue-50 text-blue-600" },
    { label: "Sản phẩm", icon: Package, color: "bg-violet-50 text-violet-600" },
    { label: "Người dùng", icon: Users, color: "bg-amber-50 text-amber-600" },
];

export default function AdminDashboard() {
    const [products, setProducts] = useState([]);
    const [users, setUsers] = useState([]);
    const [orders, setOrders] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    useEffect(() => {
        const loadDashboard = async () => {
            try {
                const [{ data: productData }, { data: userData }] = await Promise.all([
                    api.get("/api/products"),
                    api.get("/api/v1/users"),
                ]);

                const orderResults = await Promise.all(
                    userData.map(async (user) => {
                        const { data } = await api.get(`/api/orders/user/${user.id}`);
                        return data.map((order) => ({
                            ...order,
                            customer: user.fullName || user.username || user.email,
                        }));
                    })
                );

                setProducts(productData);
                setUsers(userData);
                setOrders(orderResults.flat());
            } catch (requestError) {
                console.error("Không thể tải dữ liệu dashboard:", requestError);
                setError("Không thể tải dữ liệu dashboard. Hãy kiểm tra quyền Admin.");
            } finally {
                setLoading(false);
            }
        };

        loadDashboard();
    }, []);

    const revenue = useMemo(
        () => orders.reduce((total, order) => total + Number(order.totalAmount || 0), 0),
        [orders]
    );

    const stats = [
        { ...statStyles[0], value: formatPrice(revenue), change: "Từ đơn hàng hiện có" },
        { ...statStyles[1], value: orders.length, change: "Tổng đơn hàng" },
        { ...statStyles[2], value: products.length, change: "Đang kinh doanh" },
        { ...statStyles[3], value: users.length, change: "Tài khoản hệ thống" },
    ];

    const recentOrders = [...orders]
        .sort((first, second) => new Date(second.createdAt || 0) - new Date(first.createdAt || 0))
        .slice(0, 5);

    return (
        <div>
            <div className="flex flex-wrap items-start justify-between gap-4">
                <div>
                    <p className="text-sm font-semibold text-indigo-600">TỔNG QUAN</p>
                    <h1 className="mt-2 text-3xl font-extrabold text-slate-900">Bảng điều khiển</h1>
                    <p className="mt-2 text-sm text-slate-500">Tổng hợp tình hình hoạt động của cửa hàng.</p>
                </div>
                <span className="rounded-xl border border-slate-200 bg-white px-4 py-2.5 text-sm text-slate-500">
                    Dữ liệu từ hệ thống
                </span>
            </div>

            {loading && <p className="mt-8 rounded-xl bg-white p-6 text-center text-slate-500">Đang tải dữ liệu...</p>}
            {error && <p className="mt-8 rounded-xl border border-red-200 bg-red-50 p-5 text-center text-red-700">{error}</p>}

            {!loading && !error && (
                <>
                    <div className="mt-8 grid gap-5 sm:grid-cols-2 xl:grid-cols-4">
                        {stats.map((stat) => {
                            const Icon = stat.icon;
                            return (
                                <article key={stat.label} className="rounded-2xl border border-slate-200 bg-white p-5 shadow-sm">
                                    <div className="flex items-center justify-between">
                                        <span className={`rounded-xl p-3 ${stat.color}`}><Icon size={22} /></span>
                                        <TrendingUp size={14} className="text-emerald-600" />
                                    </div>
                                    <p className="mt-5 text-sm font-medium text-slate-500">{stat.label}</p>
                                    <p className="mt-1 text-2xl font-extrabold text-slate-900">{stat.value}</p>
                                    <p className="mt-1 text-xs text-slate-500">{stat.change}</p>
                                </article>
                            );
                        })}
                    </div>

                    <section className="mt-8 overflow-hidden rounded-2xl border border-slate-200 bg-white">
                        <div className="border-b border-slate-100 px-5 py-5 sm:px-6">
                            <h2 className="text-lg font-extrabold text-slate-900">Đơn hàng gần đây</h2>
                            <p className="mt-1 text-sm text-slate-500">Dữ liệu lấy từ hệ thống đơn hàng.</p>
                        </div>
                        {recentOrders.length === 0 ? (
                            <p className="p-8 text-center text-sm text-slate-500">Chưa có đơn hàng.</p>
                        ) : (
                            <div className="overflow-x-auto">
                                <table className="w-full min-w-[650px] text-left text-sm">
                                    <thead className="bg-slate-50 text-xs uppercase tracking-wider text-slate-500">
                                        <tr>
                                            <th className="px-6 py-4">Mã đơn</th>
                                            <th className="px-6 py-4">Khách hàng</th>
                                            <th className="px-6 py-4">Ngày đặt</th>
                                            <th className="px-6 py-4">Tổng tiền</th>
                                            <th className="px-6 py-4">Trạng thái</th>
                                        </tr>
                                    </thead>
                                    <tbody className="divide-y divide-slate-100">
                                        {recentOrders.map((order) => (
                                            <tr key={order.id} className="hover:bg-slate-50">
                                                <td className="px-6 py-4 font-bold text-indigo-600">#{order.id}</td>
                                                <td className="px-6 py-4 font-medium text-slate-700">{order.customer}</td>
                                                <td className="px-6 py-4 text-slate-500">{formatDate(order.createdAt)}</td>
                                                <td className="px-6 py-4 font-semibold text-slate-900">{formatPrice(order.totalAmount)}</td>
                                                <td className="px-6 py-4">
                                                    <span className="rounded-full bg-indigo-50 px-3 py-1.5 text-xs font-bold text-indigo-700">{order.status}</span>
                                                </td>
                                            </tr>
                                        ))}
                                    </tbody>
                                </table>
                            </div>
                        )}
                    </section>
                </>
            )}
        </div>
    );
}
