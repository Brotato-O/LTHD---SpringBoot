import { useEffect, useMemo, useState } from "react";
import { Search, Users, UserRound } from "lucide-react";
import api from "../services/api";

export default function AdminUsers() {
    const [users, setUsers] = useState([]);
    const [search, setSearch] = useState("");
    const [filterRole, setFilterRole] = useState("Tất cả");
    const [error, setError] = useState("");

    useEffect(() => {
        api.get("/api/v1/users")
            .then(({ data }) => setUsers(data))
            .catch((err) => {
                console.error("Không thể tải người dùng:", err);
                setError("Không thể tải danh sách người dùng. Hãy kiểm tra quyền Admin.");
            });
    }, []);

    const filteredUsers = useMemo(() => users.filter((user) => {
        const keyword = search.toLowerCase();
        const matchesSearch = [user.fullName, user.username, user.email]
            .filter(Boolean)
            .some((value) => value.toLowerCase().includes(keyword));
        return matchesSearch && (filterRole === "Tất cả" || user.role === filterRole);
    }), [users, search, filterRole]);

    return (
        <div>
            <p className="text-sm font-semibold text-indigo-600">QUẢN LÝ TÀI KHOẢN</p>
            <h1 className="mt-2 text-3xl font-extrabold text-slate-900">Người dùng</h1>
            {error && <p className="mt-4 rounded-xl bg-red-50 p-4 text-sm text-red-600">{error}</p>}
            <div className="mt-7 rounded-2xl border border-slate-200 bg-white">
                <div className="flex flex-wrap items-center justify-between gap-4 border-b border-slate-100 p-5">
                    <p className="font-extrabold text-slate-900">{filteredUsers.length} người dùng</p>
                    <div className="flex flex-wrap gap-3">
                        <div className="flex items-center gap-2 rounded-xl border border-slate-200 px-3 py-2.5">
                            <Search size={17} className="text-slate-400" />
                            <input value={search} onChange={(e) => setSearch(e.target.value)} placeholder="Tìm tên, email..." className="w-44 text-sm outline-none sm:w-56" />
                        </div>
                        <select value={filterRole} onChange={(e) => setFilterRole(e.target.value)} className="rounded-xl border border-slate-200 bg-white px-3 py-2.5 text-sm">
                            <option>Tất cả</option><option>ADMIN</option><option>CUSTOMER</option>
                        </select>
                    </div>
                </div>
                <div className="overflow-x-auto">
                    <table className="w-full min-w-[650px] text-left text-sm">
                        <thead className="bg-slate-50 text-xs uppercase tracking-wider text-slate-500">
                            <tr><th className="px-5 py-4">Người dùng</th><th className="px-5 py-4">Username</th><th className="px-5 py-4">Vai trò</th></tr>
                        </thead>
                        <tbody className="divide-y divide-slate-100">
                            {filteredUsers.map((user) => (
                                <tr key={user.id}>
                                    <td className="px-5 py-4"><div className="flex items-center gap-3"><span className="flex h-11 w-11 items-center justify-center rounded-full bg-indigo-50 text-indigo-600"><UserRound size={20} /></span><div><p className="font-bold text-slate-800">{user.fullName || user.username}</p><p className="text-xs text-slate-500">{user.email}</p></div></div></td>
                                    <td className="px-5 py-4 text-slate-600">{user.username}</td>
                                    <td className="px-5 py-4"><span className="rounded-full bg-violet-50 px-3 py-1.5 text-xs font-bold text-violet-700">{user.role}</span></td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                    {filteredUsers.length === 0 && <div className="p-10 text-center text-sm text-slate-500"><Users className="mx-auto mb-3" size={28} />Không tìm thấy người dùng.</div>}
                </div>
            </div>
        </div>
    );
}
