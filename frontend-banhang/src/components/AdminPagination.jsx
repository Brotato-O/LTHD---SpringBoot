import { ChevronLeft, ChevronRight } from "lucide-react";

export default function AdminPagination({ page, totalPages, onPageChange }) {
    if (totalPages <= 1) return null;

    return (
        <div className="flex items-center justify-between border-t border-slate-100 px-5 py-4">
            <p className="text-sm text-slate-500">
                Trang <span className="font-bold text-slate-700">{page}</span> / {totalPages}
            </p>
            <div className="flex gap-2">
                <button
                    onClick={() => onPageChange(page - 1)}
                    disabled={page === 1}
                    className="rounded-lg border border-slate-200 p-2 text-slate-600 hover:bg-slate-50 disabled:cursor-not-allowed disabled:opacity-40"
                    aria-label="Trang trước"
                >
                    <ChevronLeft size={17} />
                </button>
                <button
                    onClick={() => onPageChange(page + 1)}
                    disabled={page === totalPages}
                    className="rounded-lg border border-slate-200 p-2 text-slate-600 hover:bg-slate-50 disabled:cursor-not-allowed disabled:opacity-40"
                    aria-label="Trang sau"
                >
                    <ChevronRight size={17} />
                </button>
            </div>
        </div>
    );
}
