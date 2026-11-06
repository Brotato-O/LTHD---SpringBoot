
import { useEffect, useState } from "react";
import { ArrowRight, ShieldCheck, Truck, Headphones } from "lucide-react";
import { Link } from "react-router-dom";
import ProductCard from "../components/ProductCard";
import api from "../services/api";

export default function Home() {
    const [products, setProducts] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    useEffect(() => {
        const fetchProducts = async () => {
            try {
                const response = await api.get("/api/products");

                if (!Array.isArray(response.data)) {
                    throw new Error(
                        "API không trả về danh sách sản phẩm dạng mảng."
                    );
                }

                // Chuyển dữ liệu backend sang định dạng ProductCard đang sử dụng
                const productList = response.data.map((product) => ({
                    ...product,
                    image: product.imageUrl,
                }));

                setProducts(productList);
            } catch (err) {
                console.error("Lỗi lấy danh sách sản phẩm:", err);

                setError(
                    "Không thể tải sản phẩm. Bạn hãy kiểm tra backend và API sản phẩm."
                );
            } finally {
                setLoading(false);
            }
        };

        fetchProducts();
    }, []);

    return (
        <div>
            {/* Banner */}
            <section className="overflow-hidden bg-gradient-to-br from-indigo-700 via-indigo-600 to-violet-600 text-white">
                <div className="mx-auto grid max-w-7xl items-center gap-10 px-4 py-16 sm:px-6 md:grid-cols-2 md:py-24 lg:px-8">
                    <div>
                        <span className="inline-flex rounded-full border border-white/30 bg-white/10 px-4 py-2 text-sm font-semibold">
                            Chào mừng đến với ShopEase
                        </span>

                        <h1 className="mt-6 text-4xl font-black leading-tight tracking-tight sm:text-5xl lg:text-6xl">
                            Mua sắm dễ dàng,
                            <span className="block text-indigo-200">
                                sống tiện nghi hơn.
                            </span>
                        </h1>

                        <p className="mt-5 max-w-xl text-base leading-7 text-indigo-100 sm:text-lg">
                            Khám phá các sản phẩm được chọn lọc với giao diện mua sắm đơn giản
                            và thuận tiện.
                        </p>

                        <Link
                            to="/products"
                            className="mt-8 inline-flex items-center gap-2 rounded-xl bg-indigo-950 px-6 py-3.5 font-bold text-white shadow-lg transition hover:bg-indigo-900"
                        >
                            Khám phá sản phẩm <ArrowRight size={18} />
                        </Link>
                    </div>

                    <div className="relative hidden md:block">
                        <div className="absolute -inset-4 rounded-full bg-white/10 blur-3xl" />

                        <img
                            src="https://images.unsplash.com/photo-1441986300917-64674bd600d8?w=1000"
                            alt="Không gian mua sắm"
                            className="relative h-80 w-full rounded-3xl object-cover shadow-2xl lg:h-96"
                        />
                    </div>
                </div>
            </section>

            {/* Ưu điểm mua hàng */}
            <section className="mx-auto grid max-w-7xl gap-4 px-4 py-8 sm:grid-cols-3 sm:px-6 lg:px-8">
                {[
                    {
                        icon: Truck,
                        title: "Giao hàng tiện lợi",
                        text: "Trải nghiệm đặt hàng đơn giản.",
                    },
                    {
                        icon: ShieldCheck,
                        title: "Mua sắm an tâm",
                        text: "Thông tin đơn hàng rõ ràng.",
                    },
                    {
                        icon: Headphones,
                        title: "Hỗ trợ khách hàng",
                        text: "Luôn sẵn sàng hỗ trợ bạn.",
                    },
                ].map((item) => {
                    const Icon = item.icon;

                    return (
                        <div
                            key={item.title}
                            className="flex items-center gap-4 rounded-2xl border border-slate-200 bg-white p-5"
                        >
                            <span className="rounded-xl bg-indigo-50 p-3 text-indigo-600">
                                <Icon size={24} />
                            </span>

                            <div>
                                <h3 className="font-bold text-slate-900">
                                    {item.title}
                                </h3>

                                <p className="mt-1 text-sm text-slate-500">
                                    {item.text}
                                </p>
                            </div>
                        </div>
                    );
                })}
            </section>

            {/* Danh sách sản phẩm lấy từ Spring Boot */}
            <section
                id="products"
                className="mx-auto max-w-7xl px-4 py-8 sm:px-6 lg:px-8"
            >
                <div className="mb-7 flex flex-wrap items-end justify-between gap-3">
                    <div>
                        <p className="text-sm font-bold uppercase tracking-widest text-indigo-600">
                            Dành cho bạn
                        </p>

                        <h2 className="mt-2 text-2xl font-extrabold text-slate-900 sm:text-3xl">
                            Sản phẩm nổi bật
                        </h2>

                        <p className="mt-2 text-sm text-slate-500">
                            Sản phẩm được tải từ hệ thống ShopEase.
                        </p>
                    </div>

                    <Link
                        to="/cart"
                        className="text-sm font-bold text-indigo-600 hover:text-indigo-800"
                    >
                        Xem giỏ hàng →
                    </Link>
                </div>

                {/* Đang tải */}
                {loading && (
                    <p className="py-10 text-center text-slate-500">
                        Đang tải sản phẩm...
                    </p>
                )}

                {/* Có lỗi */}
                {!loading && error && (
                    <div className="rounded-xl border border-red-200 bg-red-50 p-5 text-center text-red-700">
                        <p>{error}</p>

                        <button
                            onClick={() => window.location.reload()}
                            className="mt-3 rounded-lg bg-red-600 px-4 py-2 font-semibold text-white hover:bg-red-700"
                        >
                            Thử lại
                        </button>
                    </div>
                )}

                {/* Không có sản phẩm */}
                {!loading && !error && products.length === 0 && (
                    <p className="py-10 text-center text-slate-500">
                        Chưa có sản phẩm nào trong hệ thống.
                    </p>
                )}

                {/* Hiển thị sản phẩm từ API */}
                {!loading && !error && products.length > 0 && (
                    <div className="grid grid-cols-1 gap-5 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-3">
                        {products.map((product) => (
                            <ProductCard
                                key={product.id}
                                product={product}
                            />
                        ))}
                    </div>
                )}
            </section>
        </div>
    );
}
