
import { Link } from "react-router-dom";
import { ArrowLeft, MapPin, Minus, Plus, ShoppingCart, Trash2 } from "lucide-react";
import { useCart } from "../context/CartContext";
import { useAuth } from "../context/AuthContext";
import api from "../services/api";
import { toast } from "react-toastify";
import { useState } from "react";

const formatPrice = (price) =>
    new Intl.NumberFormat("vi-VN", {
        style: "currency",
        currency: "VND",
    }).format(price);

export default function Cart() {
    const { user } = useAuth();
    const {
        cartItems,
        updateQuantity,
        removeFromCart,
        totalPrice,
        clearCart,
    } = useCart();
    const [checkoutLoading, setCheckoutLoading] = useState(false);
    const [shippingAddress, setShippingAddress] = useState("");

    const checkout = async () => {
        if (!user?.id) {
            toast.info("Vui lòng đăng nhập trước khi đặt hàng.");
            return;
        }

        if (!shippingAddress.trim()) {
            toast.error("Vui lòng nhập địa chỉ giao hàng.");
            return;
        }

        try {
            setCheckoutLoading(true);
            await api.post(`/api/orders/${user.id}/checkout`, {
                shippingAddress: shippingAddress.trim(),
                paymentMethod: "COD",
            });
            await clearCart();
            setShippingAddress("");
            toast.success("Đặt hàng thành công!");
        } catch (error) {
            toast.error(error.response?.data?.message || "Không thể đặt hàng.");
        } finally {
            setCheckoutLoading(false);
        }
    };

    return (
        <div className="mx-auto max-w-7xl px-4 py-10 sm:px-6 lg:px-8">
            <div>
                <p className="text-sm font-bold uppercase tracking-widest text-indigo-600">
                    Mua sắm
                </p>
                <h1 className="mt-2 text-3xl font-extrabold text-slate-900">
                    Giỏ hàng của bạn
                </h1>
            </div>

            {cartItems.length === 0 ? (
                <div className="mt-8 rounded-3xl border border-slate-200 bg-white px-6 py-16 text-center">
                    <span className="mx-auto flex h-16 w-16 items-center justify-center rounded-2xl bg-indigo-50 text-indigo-600">
                        <ShoppingCart size={30} />
                    </span>
                    <h2 className="mt-5 text-xl font-bold text-slate-900">
                        Giỏ hàng đang trống
                    </h2>
                    <p className="mt-2 text-sm text-slate-500">
                        Hãy chọn sản phẩm bạn yêu thích để thêm vào giỏ hàng.
                    </p>
                    <Link
                        to="/"
                        className="mt-6 inline-flex items-center gap-2 rounded-xl bg-indigo-600 px-5 py-3 font-bold text-white hover:bg-indigo-700"
                    >
                        <ArrowLeft size={17} /> Tiếp tục mua sắm
                    </Link>
                </div>
            ) : (
                <div className="mt-8 grid gap-6 lg:grid-cols-[1fr_340px]">
                    <section className="space-y-4">
                        {cartItems.map((item) => (
                            <article
                                key={item.id}
                                className="flex flex-wrap items-center gap-4 rounded-2xl border border-slate-200 bg-white p-4 sm:flex-nowrap"
                            >
                                <img
                                    src={item.image}
                                    alt={item.name}
                                    className="h-24 w-24 rounded-xl bg-slate-100 object-cover"
                                />

                                <div className="min-w-0 flex-1">
                                    <h2 className="font-bold text-slate-900">{item.name}</h2>
                                    <p className="mt-2 font-bold text-indigo-700">
                                        {formatPrice(item.price)}
                                    </p>
                                    <button
                                        onClick={() => removeFromCart(item.id)}
                                        className="mt-2 inline-flex items-center gap-1 text-xs font-semibold text-red-500 hover:text-red-700"
                                    >
                                        <Trash2 size={14} /> Xóa
                                    </button>
                                </div>

                                <div className="flex items-center gap-3 rounded-xl border border-slate-200 p-1">
                                    <button
                                        onClick={() => updateQuantity(item.id, item.quantity - 1)}
                                        className="rounded-lg p-2 hover:bg-slate-100"
                                        aria-label="Giảm số lượng"
                                    >
                                        <Minus size={15} />
                                    </button>
                                    <span className="min-w-5 text-center text-sm font-bold">
                                        {item.quantity}
                                    </span>
                                    <button
                                        onClick={() => updateQuantity(item.id, item.quantity + 1)}
                                        className="rounded-lg p-2 hover:bg-slate-100"
                                        aria-label="Tăng số lượng"
                                    >
                                        <Plus size={15} />
                                    </button>
                                </div>
                            </article>
                        ))}

                        <button
                            onClick={clearCart}
                            className="text-sm font-semibold text-red-500 hover:text-red-700"
                        >
                            Xóa toàn bộ giỏ hàng
                        </button>
                    </section>

                    <aside className="h-fit rounded-2xl border border-slate-200 bg-white p-6">
                        <h2 className="text-lg font-extrabold text-slate-900">
                            Tổng kết đơn hàng
                        </h2>
                        <div className="mt-5 flex justify-between text-sm text-slate-500">
                            <span>Tạm tính</span>
                            <span>{formatPrice(totalPrice)}</span>
                        </div>
                        <div className="mt-3 flex justify-between text-sm text-slate-500">
                            <span>Phí vận chuyển</span>
                            <span>Chưa tính</span>
                        </div>
                        <div className="my-5 border-t border-dashed border-slate-200" />
                        <div className="flex items-center justify-between gap-3">
                            <span className="font-bold text-slate-900">Tổng tiền</span>
                            <span className="text-xl font-extrabold text-indigo-700">
                                {formatPrice(totalPrice)}
                            </span>
                        </div>
                        <div className="mt-5">
                            <label
                                htmlFor="shipping-address"
                                className="mb-2 flex items-center gap-2 text-sm font-bold text-slate-900"
                            >
                                <MapPin size={16} className="text-indigo-600" />
                                Địa chỉ giao hàng
                            </label>
                            <textarea
                                id="shipping-address"
                                value={shippingAddress}
                                onChange={(event) => setShippingAddress(event.target.value)}
                                placeholder="Nhập địa chỉ nhận hàng của bạn"
                                rows={3}
                                maxLength={500}
                                required
                                className="w-full resize-none rounded-xl border border-slate-200 px-3 py-2.5 text-sm text-slate-800 outline-none transition focus:border-indigo-500 focus:ring-2 focus:ring-indigo-100"
                            />
                            {!shippingAddress.trim() && (
                                <p className="mt-1.5 text-xs text-red-500">
                                    Vui lòng nhập địa chỉ để tiếp tục.
                                </p>
                            )}
                        </div>
                        <button
                            onClick={checkout}
                            disabled={checkoutLoading || !shippingAddress.trim() || !user?.id}
                            className="mt-6 w-full rounded-xl bg-indigo-600 px-4 py-3.5 font-bold text-white hover:bg-indigo-700 disabled:cursor-not-allowed disabled:opacity-50"
                        >
                            {checkoutLoading ? "Đang đặt hàng..." : "Tiến hành đặt hàng"}
                        </button>
                        <Link
                            to="/"
                            className="mt-4 block text-center text-sm font-semibold text-slate-500 hover:text-indigo-600"
                        >
                            Tiếp tục mua sắm
                        </Link>
                    </aside>
                </div>
            )}
        </div>
    );
}
