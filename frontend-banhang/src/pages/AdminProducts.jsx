
import { useEffect, useMemo, useState } from "react";
import { Plus, Search, Pencil, Trash2, Package, X } from "lucide-react";
import api from "../services/api";

const formatPrice = (price) =>
    new Intl.NumberFormat("vi-VN", {
        style: "currency",
        currency: "VND",
    }).format(Number(price) || 0);

const emptyForm = {
    name: "",
    description: "",
    price: "",
    stock: "",
    imageUrl: "",
};

export default function AdminProducts() {
    const [products, setProducts] = useState([]);
    const [search, setSearch] = useState("");
    const [showForm, setShowForm] = useState(false);
    const [loading, setLoading] = useState(true);
    const [saving, setSaving] = useState(false);
    const [error, setError] = useState("");
    const [form, setForm] = useState(emptyForm);
    const [editingId, setEditingId] = useState(null);
    const [editingVersion, setEditingVersion] = useState(null);

    // Lấy danh sách sản phẩm từ database thông qua backend
    const fetchProducts = async () => {
        try {
            setError("");
            const response = await api.get("/api/products");
            setProducts(response.data);
        } catch (err) {
            console.error("Lỗi lấy danh sách sản phẩm:", err);
            setError("Không thể tải sản phẩm. Bạn hãy kiểm tra backend và đăng nhập lại nếu cần.");
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchProducts();
    }, []);

    const filteredProducts = useMemo(
        () =>
            products.filter((product) =>
                product.name.toLowerCase().includes(search.toLowerCase())
            ),
        [products, search]
    );

    const handleChange = (event) => {
        const { name, value } = event.target;
        setForm((current) => ({
            ...current,
            [name]: value,
        }));
    };

    const openAddForm = () => {
        setEditingId(null);
        setEditingVersion(null);
        setForm(emptyForm);
        setShowForm(true);
    };

    const openEditForm = (product) => {
        console.log("Sản phẩm đang sửa:", product);

        setEditingId(product.id);
        setEditingVersion(product.version);

        setForm({
            name: product.name || "",
            description: product.description || "",
            price: product.price ?? "",
            stock: product.stock ?? 0,
            imageUrl: product.imageUrl || "",
        });

        setShowForm(true);
    };

    const handleSubmit = async (event) => {
        event.preventDefault();

        if (
            !form.name.trim() ||
            form.price === "" ||
            Number(form.price) < 0 ||
            form.stock === "" ||
            Number(form.stock) < 0
        ) {
            window.alert("Vui lòng nhập tên, giá và số lượng hợp lệ.");
            return;
        }

        const productData = {
            name: form.name.trim(),
            description: form.description.trim(),
            price: Number(form.price),
            stock: Number(form.stock),
            imageUrl: form.imageUrl.trim(),
        };

        if (editingId !== null) {
            productData.version = editingVersion;
        }

        try {
            setSaving(true);

            if (editingId !== null) {
                await api.put(`/api/products/${editingId}`, productData);
                window.alert("Cập nhật sản phẩm thành công!");
            } else {
                await api.post("/api/products", productData);
                window.alert("Thêm sản phẩm thành công!");
            }

            setShowForm(false);
            setForm(emptyForm);
            setEditingId(null);
            setEditingVersion(null);
            await fetchProducts();
        } catch (err) {
            console.error("Lỗi lưu sản phẩm:", err);
            window.alert(
                err.response?.data?.message ||
                "Không thể lưu sản phẩm. Hãy kiểm tra quyền Admin và dữ liệu gửi lên."
            );
        } finally {
            setSaving(false);
        }
    };

    const handleDelete = async (id) => {
        if (!window.confirm("Bạn có chắc muốn xóa sản phẩm này khỏi database?")) {
            return;
        }

        try {
            await api.delete(`/api/products/${id}`);
            setProducts((current) =>
                current.filter((product) => product.id !== id)
            );
            window.alert("Xóa sản phẩm thành công!");
        } catch (err) {
            console.error("Lỗi xóa sản phẩm:", err);
            window.alert(
                err.response?.data?.message ||
                "Không thể xóa sản phẩm. Hãy kiểm tra quyền Admin và backend."
            );
        }
    };

    return (
        <div>
            <div className="flex flex-wrap items-start justify-between gap-4">
                <div>
                    <p className="text-sm font-semibold text-indigo-600">
                        QUẢN LÝ CỬA HÀNG
                    </p>
                    <h1 className="mt-2 text-3xl font-extrabold text-slate-900">
                        Sản phẩm
                    </h1>
                    <p className="mt-2 text-sm text-slate-500">
                        Quản lý danh sách sản phẩm của cửa hàng.
                    </p>
                </div>

                <button
                    onClick={openAddForm}
                    className="flex items-center gap-2 rounded-xl bg-indigo-600 px-4 py-3 text-sm font-bold text-white hover:bg-indigo-700"
                >
                    <Plus size={18} /> Thêm sản phẩm
                </button>
            </div>

            {showForm && (
                <form
                    onSubmit={handleSubmit}
                    className="mt-6 grid gap-4 rounded-2xl border border-slate-200 bg-white p-5 sm:grid-cols-2"
                >
                    <div>
                        <label className="mb-2 block text-sm font-semibold">
                            Tên sản phẩm
                        </label>
                        <input
                            name="name"
                            value={form.name}
                            onChange={handleChange}
                            className="w-full rounded-xl border border-slate-200 px-4 py-3 outline-none focus:border-indigo-500"
                            placeholder="Nhập tên sản phẩm"
                            required
                        />
                    </div>

                    <div>
                        <label className="mb-2 block text-sm font-semibold">
                            Giá bán (VNĐ)
                        </label>
                        <input
                            name="price"
                            type="number"
                            min="0"
                            step="0.01"
                            value={form.price}
                            onChange={handleChange}
                            className="w-full rounded-xl border border-slate-200 px-4 py-3 outline-none focus:border-indigo-500"
                            placeholder="Nhập giá bán"
                            required
                        />
                    </div>

                    <div>
                        <label className="mb-2 block text-sm font-semibold">
                            Số lượng tồn kho
                        </label>
                        <input
                            name="stock"
                            type="number"
                            min="0"
                            step="1"
                            value={form.stock}
                            onChange={handleChange}
                            className="w-full rounded-xl border border-slate-200 px-4 py-3 outline-none focus:border-indigo-500"
                            placeholder="Nhập số lượng"
                            required
                        />
                    </div>

                    <div>
                        <label className="mb-2 block text-sm font-semibold">
                            Đường dẫn hình ảnh
                        </label>
                        <input
                            name="imageUrl"
                            type="url"
                            value={form.imageUrl}
                            onChange={handleChange}
                            className="w-full rounded-xl border border-slate-200 px-4 py-3 outline-none focus:border-indigo-500"
                            placeholder="https://..."
                        />
                    </div>

                    <div className="sm:col-span-2">
                        <label className="mb-2 block text-sm font-semibold">
                            Mô tả sản phẩm
                        </label>
                        <textarea
                            name="description"
                            value={form.description}
                            onChange={handleChange}
                            rows={3}
                            className="w-full rounded-xl border border-slate-200 px-4 py-3 outline-none focus:border-indigo-500"
                            placeholder="Nhập mô tả sản phẩm"
                        />
                    </div>

                    <div className="flex flex-wrap gap-3 sm:col-span-2">
                        <button
                            type="submit"
                            disabled={saving}
                            className="rounded-xl bg-emerald-600 px-5 py-3 font-bold text-white hover:bg-emerald-700 disabled:opacity-50"
                        >
                            {saving
                                ? "Đang lưu..."
                                : editingId !== null
                                    ? "Lưu thay đổi"
                                    : "Thêm vào database"}
                        </button>

                        <button
                            type="button"
                            onClick={() => {
                                setShowForm(false);
                                setEditingId(null);
                                setEditingVersion(null);
                                setForm(emptyForm);
                            }}
                            className="flex items-center gap-2 rounded-xl border border-slate-200 px-5 py-3 font-semibold text-slate-600 hover:bg-slate-50"
                        >
                            <X size={16} /> Hủy
                        </button>
                    </div>
                </form>
            )}

            <div className="mt-7 rounded-2xl border border-slate-200 bg-white">
                <div className="flex flex-wrap items-center justify-between gap-4 border-b border-slate-100 p-5">
                    <div>
                        <h2 className="font-extrabold text-slate-900">
                            Danh sách sản phẩm
                        </h2>
                        <p className="mt-1 text-sm text-slate-500">
                            {filteredProducts.length} sản phẩm
                        </p>
                    </div>

                    <div className="flex items-center gap-2 rounded-xl border border-slate-200 px-3 py-2.5">
                        <Search size={17} className="text-slate-400" />
                        <input
                            value={search}
                            onChange={(e) => setSearch(e.target.value)}
                            placeholder="Tìm sản phẩm..."
                            className="w-44 text-sm outline-none sm:w-60"
                        />
                    </div>
                </div>

                {loading ? (
                    <div className="p-10 text-center text-sm text-slate-500">
                        Đang tải danh sách sản phẩm...
                    </div>
                ) : error ? (
                    <div className="p-10 text-center text-sm text-red-600">
                        <p>{error}</p>
                        <button
                            onClick={fetchProducts}
                            className="mt-3 rounded-lg bg-indigo-600 px-4 py-2 font-semibold text-white"
                        >
                            Thử tải lại
                        </button>
                    </div>
                ) : (
                    <div className="overflow-x-auto">
                        <table className="w-full min-w-[760px] text-left text-sm">
                            <thead className="bg-slate-50 text-xs uppercase tracking-wider text-slate-500">
                                <tr>
                                    <th className="px-5 py-4">Sản phẩm</th>
                                    <th className="px-5 py-4">Mã</th>
                                    <th className="px-5 py-4">Giá bán</th>
                                    <th className="px-5 py-4">Tồn kho</th>
                                    <th className="px-5 py-4 text-right">Thao tác</th>
                                </tr>
                            </thead>

                            <tbody className="divide-y divide-slate-100">
                                {filteredProducts.map((product) => (
                                    <tr key={product.id} className="hover:bg-slate-50">
                                        <td className="px-5 py-4">
                                            <div className="flex items-center gap-3">
                                                <img
                                                    src={
                                                        product.imageUrl ||
                                                        product.image ||
                                                        "https://placehold.co/100x100?text=No+Image"
                                                    }
                                                    alt={product.name}
                                                    className="h-12 w-12 rounded-lg bg-slate-100 object-cover"
                                                />
                                                <div>
                                                    <p className="font-semibold text-slate-800">
                                                        {product.name}
                                                    </p>
                                                    <p className="mt-1 max-w-xs truncate text-xs text-slate-500">
                                                        {product.description || "Chưa có mô tả"}
                                                    </p>
                                                </div>
                                            </div>
                                        </td>

                                        <td className="px-5 py-4 text-slate-500">
                                            SP-{product.id}
                                        </td>

                                        <td className="px-5 py-4 font-bold text-slate-900">
                                            {formatPrice(product.price)}
                                        </td>

                                        <td className="px-5 py-4 text-slate-600">
                                            {product.stock ?? 0}
                                        </td>

                                        <td className="px-5 py-4">
                                            <div className="flex justify-end gap-2">
                                                <button
                                                    onClick={() => openEditForm(product)}
                                                    className="rounded-lg border border-slate-200 p-2 text-slate-500 hover:bg-slate-100"
                                                    aria-label="Sửa sản phẩm"
                                                >
                                                    <Pencil size={16} />
                                                </button>

                                                <button
                                                    onClick={() => handleDelete(product.id)}
                                                    className="rounded-lg border border-red-100 p-2 text-red-500 hover:bg-red-50"
                                                    aria-label="Xóa sản phẩm"
                                                >
                                                    <Trash2 size={16} />
                                                </button>
                                            </div>
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>

                        {filteredProducts.length === 0 && (
                            <div className="p-10 text-center text-sm text-slate-500">
                                <Package className="mx-auto mb-3" size={28} />
                                Không tìm thấy sản phẩm phù hợp.
                            </div>
                        )}
                    </div>
                )}
            </div>
        </div>
    );
}
