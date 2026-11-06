import { useEffect, useMemo, useState } from "react";
import { Search, ShoppingBag } from "lucide-react";
import api from "../services/api";

const formatPrice = (price) => new Intl.NumberFormat("vi-VN", { style: "currency", currency: "VND" }).format(Number(price) || 0);
const statuses = ["PENDING", "CONFIRMED", "SHIPPING", "COMPLETED", "CANCELLED"];

export default function AdminOrders() {
    const [orders, setOrders] = useState([]);
    const [search, setSearch] = useState("");
    const [filterStatus, setFilterStatus] = useState("Tất cả");
    const [error, setError] = useState("");

    useEffect(() => {
        const loadOrders = async () => {
            try {
                const { data: users } = await api.get("/api/v1/users");
                const results = await Promise.all(users.map(async (user) => {
                    const { data } = await api.get(`/api/orders/user/${user.id}`);
                    return data.map((order) => ({ ...order, customer: user.fullName || user.username || user.email }));
                }));
                setOrders(results.flat());
            } catch (err) {
                console.error("Không thể tải đơn hàng:", err);
                setError("Không thể tải danh sách đơn hàng. Hãy kiểm tra quyền truy cập.");
            }
        };
        loadOrders();
    }, []);

    const filteredOrders = useMemo(() => orders.filter((order) => {
        const keyword = search.toLowerCase();
        return (`${order.id}`.includes(keyword) || order.customer.toLowerCase().includes(keyword)) &&
            (filterStatus === "Tất cả" || order.status === filterStatus);
    }), [orders, search, filterStatus]);

    const changeStatus = async (id, status) => {
        try {
            const { data } = await api.put(`/api/orders/admin/${id}`, { status });
            setOrders((current) => current.map((order) => order.id === id ? { ...order, ...data } : order));
        } catch (err) {
            window.alert(err.response?.data?.message || "Không thể cập nhật trạng thái đơn hàng.");
        }
    };

    return (
        <div>
            <p className="text-sm font-semibold text-indigo-600">QUẢN LÝ BÁN HÀNG</p>
            <h1 className="mt-2 text-3xl font-extrabold text-slate-900">Đơn hàng</h1>
            {error && <p className="mt-4 rounded-xl bg-red-50 p-4 text-sm text-red-600">{error}</p>}
            <div className="mt-7 rounded-2xl border border-slate-200 bg-white">
                <div className="flex flex-wrap items-center justify-between gap-4 border-b border-slate-100 p-5">
                    <p className="font-extrabold text-slate-900">{filteredOrders.length} đơn hàng</p>
                    <div className="flex flex-wrap gap-3">
                        <div className="flex items-center gap-2 rounded-xl border border-slate-200 px-3 py-2.5"><Search size={17} className="text-slate-400" /><input value={search} onChange={(e) => setSearch(e.target.value)} placeholder="Tìm mã đơn, khách hàng..." className="w-44 text-sm outline-none sm:w-56" /></div>
                        <select value={filterStatus} onChange={(e) => setFilterStatus(e.target.value)} className="rounded-xl border border-slate-200 bg-white px-3 py-2.5 text-sm"><option>Tất cả</option>{statuses.map((status) => <option key={status}>{status}</option>)}</select>
                    </div>
                </div>
                <div className="overflow-x-auto">
                    <table className="w-full min-w-[800px] text-left text-sm">
                        <thead className="bg-slate-50 text-xs uppercase tracking-wider text-slate-500"><tr><th className="px-5 py-4">Mã đơn</th><th className="px-5 py-4">Khách hàng</th><th className="px-5 py-4">Ngày đặt</th><th className="px-5 py-4">Tổng tiền</th><th className="px-5 py-4">Trạng thái</th></tr></thead>
                        <tbody className="divide-y divide-slate-100">{filteredOrders.map((order) => <tr key={order.id}><td className="px-5 py-4 font-bold text-indigo-600">#{order.id}</td><td className="px-5 py-4">{order.customer}</td><td className="px-5 py-4">{order.createdAt ? new Date(order.createdAt).toLocaleDateString("vi-VN") : "-"}</td><td className="px-5 py-4 font-bold">{formatPrice(order.totalAmount)}</td><td className="px-5 py-4"><select value={order.status} onChange={(e) => changeStatus(order.id, e.target.value)} className="rounded-lg border border-slate-200 px-2 py-2 text-xs">{statuses.map((status) => <option key={status}>{status}</option>)}</select></td></tr>)}</tbody>
                    </table>
                    {filteredOrders.length === 0 && <div className="p-10 text-center text-sm text-slate-500"><ShoppingBag className="mx-auto mb-3" size={28} />Không tìm thấy đơn hàng.</div>}
                </div>
            </div>
        </div>
    );
}
