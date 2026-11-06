import { useEffect, useState } from "react";
import { ArrowLeft } from "lucide-react";
import { Link } from "react-router-dom";
import ProductCard from "../components/ProductCard";
import api from "../services/api";

export default function Products() {
    const [products, setProducts] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    useEffect(() => {
        const fetchProducts = async () => {
            try {
                const response = await api.get("/api/products");

                if (!Array.isArray(response.data)) {
                    throw new Error("API không trả về danh sách sản phẩm dạng mảng.");
                }

                setProducts(
                    response.data.map((product) => ({
                        ...product,
                        image: product.imageUrl,
                    }))
                );
            } catch (requestError) {
                console.error("Lỗi lấy danh sách sản phẩm:", requestError);
                setError("Không thể tải danh sách sản phẩm.");
            } finally {
                setLoading(false);
            }
        };

        fetchProducts();
    }, []);

    return (
        <div className="mx-auto max-w-7xl px-4 py-10 sm:px-6 lg:px-8">
            <Link
                to="/"
                className="mb-8 inline-flex items-center gap-2 text-sm font-semibold text-slate-600 hover:text-indigo-700"
            >
                <ArrowLeft size={17} /> Về trang chủ
            </Link>

            <div className="mb-8">
                <p className="text-sm font-bold uppercase tracking-widest text-indigo-600">
                    ShopEase
                </p>
                <h1 className="mt-2 text-3xl font-extrabold text-slate-900">
                    Tất cả sản phẩm
                </h1>
                <p className="mt-2 text-slate-500">
                    Khám phá toàn bộ sản phẩm đang có trong hệ thống.
                </p>
            </div>

            {loading && (
                <p className="py-16 text-center text-slate-500">
                    Đang tải sản phẩm...
                </p>
            )}

            {!loading && error && (
                <div className="rounded-xl border border-red-200 bg-red-50 p-5 text-center text-red-700">
                    {error}
                </div>
            )}

            {!loading && !error && products.length === 0 && (
                <p className="py-16 text-center text-slate-500">
                    Chưa có sản phẩm nào trong hệ thống.
                </p>
            )}

            {!loading && !error && products.length > 0 && (
                <div className="grid grid-cols-1 gap-5 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4">
                    {products.map((product) => (
                        <ProductCard key={product.id} product={product} />
                    ))}
                </div>
            )}
        </div>
    );
}
