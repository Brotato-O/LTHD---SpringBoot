import { useEffect, useState } from "react";
import { ArrowLeft, ShoppingCart, Star } from "lucide-react";
import { Link, useNavigate, useParams } from "react-router-dom";
import { toast } from "react-toastify";
import { useCart } from "../context/CartContext";
import { useAuth } from "../context/AuthContext";
import api from "../services/api";

const formatPrice = (price) =>
    new Intl.NumberFormat("vi-VN", {
        style: "currency",
        currency: "VND",
    }).format(price);

export default function ProductDetail() {
    const { id } = useParams();
    const navigate = useNavigate();
    const { addToCart } = useCart();
    const { user } = useAuth();
    const [product, setProduct] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    useEffect(() => {
        const fetchProduct = async () => {
            try {
                setLoading(true);
                setError("");
                const { data } = await api.get(`/api/products/${id}`);
                setProduct({ ...data, image: data.imageUrl });
            } catch (requestError) {
                console.error("Lỗi lấy chi tiết sản phẩm:", requestError);
                setError(
                    requestError.response?.data?.message ||
                    "Không thể tải thông tin sản phẩm."
                );
            } finally {
                setLoading(false);
            }
        };

        fetchProduct();
    }, [id]);

    const handleAddToCart = async () => {
        if (!user?.id) {
            toast.info("Bạn cần đăng nhập để thêm sản phẩm vào giỏ hàng.");
            navigate("/login", {
                state: { message: "Vui lòng đăng nhập để tiếp tục mua hàng." },
            });
            return;
        }

        try {
            await addToCart(product);
            toast.success("Đã thêm sản phẩm vào giỏ hàng.");
            navigate("/cart");
        } catch (requestError) {
            console.error("Không thể thêm sản phẩm vào giỏ hàng:", requestError);
            toast.error(
                requestError.response?.data?.message ||
                "Không thể thêm sản phẩm vào giỏ hàng."
            );
        }
    };

    if (loading) {
        return (
            <p className="mx-auto max-w-7xl px-4 py-16 text-center text-slate-500">
                Đang tải sản phẩm...
            </p>
        );
    }

    if (error || !product) {
        return (
            <div className="mx-auto max-w-7xl px-4 py-16 text-center">
                <p className="text-red-600">{error || "Không tìm thấy sản phẩm."}</p>
                <Link
                    to="/"
                    className="mt-6 inline-flex rounded-xl bg-indigo-600 px-5 py-3 font-bold text-white"
                >
                    Về trang chủ
                </Link>
            </div>
        );
    }

    return (
        <div className="mx-auto max-w-7xl px-4 py-10 sm:px-6 lg:px-8">
            <button
                onClick={() => navigate(-1)}
                className="mb-8 inline-flex items-center gap-2 text-sm font-semibold text-slate-600 hover:text-indigo-700"
            >
                <ArrowLeft size={17} /> Quay lại
            </button>

            <div className="grid gap-8 rounded-3xl border border-slate-200 bg-white p-5 shadow-sm md:grid-cols-2 md:p-8">
                <div className="flex min-h-80 items-center justify-center overflow-hidden rounded-2xl bg-gradient-to-br from-indigo-50 to-violet-100">
                    <img
                        src={product.image}
                        alt={product.name}
                        className="h-full max-h-[30rem] w-full object-cover"
                        onError={(event) => {
                            event.currentTarget.src =
                                "https://placehold.co/800x600/e0e7ff/4338ca?text=Product";
                        }}
                    />
                </div>

                <div className="flex flex-col justify-center">
                    <div className="flex items-center gap-1 text-amber-500">
                        <Star size={16} fill="currentColor" />
                        <span className="text-sm font-semibold text-slate-600">5.0</span>
                    </div>
                    <h1 className="mt-3 text-3xl font-black text-slate-900">{product.name}</h1>
                    <p className="mt-4 text-3xl font-extrabold text-indigo-700">
                        {formatPrice(product.price)}
                    </p>
                    <p className="mt-6 leading-7 text-slate-600">
                        {product.description || "Sản phẩm chất lượng từ ShopEase."}
                    </p>
                    <p className="mt-5 text-sm text-slate-500">
                        Còn lại: <span className="font-bold text-slate-700">{product.stock ?? 0}</span> sản phẩm
                    </p>
                    <button
                        onClick={handleAddToCart}
                        className="mt-8 inline-flex w-fit items-center gap-2 rounded-xl bg-indigo-600 px-6 py-3.5 font-bold text-white hover:bg-indigo-700"
                    >
                        <ShoppingCart size={18} /> Thêm vào giỏ hàng
                    </button>
                </div>
            </div>
        </div>
    );
}
