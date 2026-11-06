
import { ShoppingCart, Star } from "lucide-react";
import { Link } from "react-router-dom";
import { toast } from "react-toastify";
import { useCart } from "../context/CartContext";
import { useAuth } from "../context/AuthContext";

const formatPrice = (price) =>
    new Intl.NumberFormat("vi-VN", {
        style: "currency",
        currency: "VND",
    }).format(price);

export default function ProductCard({ product }) {
    const { addToCart } = useCart();
    const { user } = useAuth();

    const handleAddToCart = async () => {
        if (!user?.id) {
            toast.info("Bạn cần đăng nhập để thêm sản phẩm vào giỏ hàng.");
            return;
        }

        try {
            await addToCart(product);
            toast.success("Đã thêm sản phẩm vào giỏ hàng.");
        } catch (error) {
            console.error("Không thể thêm sản phẩm vào giỏ hàng:", error);
            toast.error(
                error.response?.data?.message ||
                "Không thể thêm sản phẩm vào giỏ hàng."
            );
        }
    };

    return (
        <article className="group overflow-hidden rounded-2xl border border-slate-200 bg-white shadow-sm transition duration-300 hover:-translate-y-1 hover:shadow-lg">
            <Link to={`/products/${product.id}`} aria-label={`Xem ${product.name}`}>
              <div className="relative flex h-52 items-center justify-center overflow-hidden bg-gradient-to-br from-indigo-50 to-violet-100">
                <img
                    src={product.image}
                    alt={product.name}
                    className="h-full w-full object-cover transition duration-500 group-hover:scale-105"
                    onError={(event) => {
                        event.currentTarget.src =
                            "https://placehold.co/600x400/e0e7ff/4338ca?text=Product";
                    }}
                />
                {product.badge && (
                    <span className="absolute left-3 top-3 rounded-full bg-white px-3 py-1 text-xs font-bold text-indigo-700 shadow-sm">
                        {product.badge}
                    </span>
                )}
              </div>
            </Link>

            <div className="p-4">
                <div className="mb-2 flex items-center gap-1 text-amber-500">
                    <Star size={15} fill="currentColor" />
                    <span className="text-xs font-semibold text-slate-600">
                        {product.rating ?? "5.0"}
                    </span>
                </div>

                <Link
                    to={`/products/${product.id}`}
                    className="line-clamp-1 text-base font-bold text-slate-900 hover:text-indigo-700"
                >
                    {product.name}
                </Link>
                <p className="mt-1 min-h-10 text-sm leading-5 text-slate-500">
                    {product.description}
                </p>

                <div className="mt-4 flex items-center justify-between gap-2">
                    <span className="font-extrabold text-indigo-700">
                        {formatPrice(product.price)}
                    </span>
                    <button
                        onClick={handleAddToCart}
                        className="flex items-center gap-2 rounded-xl bg-indigo-600 px-3 py-2.5 text-sm font-semibold text-white transition hover:bg-indigo-700"
                    >
                        <ShoppingCart size={16} />
                        Thêm
                    </button>
                </div>
            </div>
        </article>
    );
}
