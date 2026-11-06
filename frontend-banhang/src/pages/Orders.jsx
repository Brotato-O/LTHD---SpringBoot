import { useEffect, useState } from "react";
import { PackageCheck, Clock3 } from "lucide-react";
import { useAuth } from "../context/AuthContext";
import api from "../services/api";

const formatPrice = (price) =>
    new Intl.NumberFormat("vi-VN", {
        style: "currency",
        currency: "VND",
    }).format(Number(price) || 0);

function StatusBadge({ status }) {
    return (
        <span className="inline-flex rounded-full bg-indigo-50 px-3 py-1.5 text-xs font-bold text-indigo-700">
            {status}
        </span>
    );
}

export default function Orders() {
    const { user } = useAuth();
    const [orders, setOrders] = useState([]);
    const [error, setError] = useState("");

    useEffect(() => {
        if (!user?.id) return;

        api.get(`/api/orders/user/${user.id}`)
            .then(({ data }) => setOrders(data))
            .catch((err) => {
                console.error("Không thể tải lịch sử đơn hàng:", err);
                setError("Không thể tải lịch sử đơn hàng.");
            });
    }, [user?.id]);

    return (
        <div className="mx-auto max-w-7xl px-4 py-10 sm:px-6 lg:px-8">
            <p className="text-sm font-bold uppercase tracking-widest text-indigo-600">Tài khoản</p>
            <h1 className="mt-2 text-3xl font-extrabold text-slate-900">Đơn hàng của tôi</h1>
            {!user?.id && <p className="mt-2 text-slate-500">Vui lòng đăng nhập để xem đơn hàng.</p>}
            {error && <p className="mt-4 rounded-xl bg-red-50 p-4 text-sm text-red-600">{error}</p>}

            <div className="mt-8 space-y-4">
                {orders.map((order) => (
                    <article key={order.id} className="rounded-2xl border border-slate-200 bg-white p-5 sm:p-6">
                        <div className="flex flex-wrap items-start justify-between gap-4">
                            <div className="flex items-start gap-3">
                                <span className="rounded-xl bg-indigo-50 p-3 text-indigo-600"><PackageCheck size={23} /></span>
                                <div>
                                    <h2 className="font-extrabold text-slate-900">Đơn hàng #{order.id}</h2>
                                    <p className="mt-1 text-sm text-slate-500">
                                        Ngày đặt: {order.createdAt ? new Date(order.createdAt).toLocaleString("vi-VN") : "-"}
                                    </p>
                                    <p className="mt-1 text-sm text-slate-500">{order.items?.length || 0} sản phẩm</p>
                                </div>
                            </div>
                            <StatusBadge status={order.status} />
                        </div>
                        <div className="mt-5 flex flex-wrap items-center justify-between gap-3 border-t border-slate-100 pt-4">
                            <div className="flex items-center gap-2 text-xs text-slate-500"><Clock3 size={15} />{order.shippingAddress}</div>
                            <p className="text-sm text-slate-500">
                                Thành tiền: <strong className="text-lg text-indigo-700">{formatPrice(order.totalAmount)}</strong>
                            </p>
                        </div>
                    </article>
                ))}
                {user?.id && orders.length === 0 && !error && (
                    <p className="rounded-2xl border border-slate-200 bg-white p-10 text-center text-slate-500">Bạn chưa có đơn hàng.</p>
                )}
            </div>
        </div>
    );
}
