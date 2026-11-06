
import { ShoppingCart, Star } from "lucide-react";
import { useCart } from "../context/CartContext";

const formatPrice = (price) =>
    new Intl.NumberFormat("vi-VN", {
        style: "currency",
        currency: "VND",
    }).format(price);

export default function ProductCard({ product }) {
    const { addToCart } = useCart();

    return (
        <article className="group overflow-hidden rounded-2xl border border-slate-200 bg-white shadow-sm transition duration-300 hover:-translate-y-1 hover:shadow-lg">
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

            <div className="p-4">
                <div className="mb-2 flex items-center gap-1 text-amber-500">
                    <Star size={15} fill="currentColor" />
                    <span className="text-xs font-semibold text-slate-600">
                        {product.rating ?? "5.0"}
                    </span>
                </div>

                <h3 className="line-clamp-1 text-base font-bold text-slate-900">
                    {product.name}
                </h3>
                <p className="mt-1 min-h-10 text-sm leading-5 text-slate-500">
                    {product.description}
                </p>

                <div className="mt-4 flex items-center justify-between gap-2">
                    <span className="font-extrabold text-indigo-700">
                        {formatPrice(product.price)}
                    </span>
                    <button
                        onClick={() => addToCart(product)}
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
