import { useState } from "react";

export default function SearchBar({ onSearch }) {
    const [query, setQuery] = useState("");

    const handleSubmit = (e) => {
        e.preventDefault();
        if (query.trim() !== "") onSearch(query);
    };

    return (
        <form
            onSubmit={handleSubmit}
            className="flex items-center justify-center w-full max-w-2xl gap-2"
        >
            <input
                type="text"
                placeholder="BNK 관련 서비스 검색..."
                value={query}
                onChange={(e) => setQuery(e.target.value)}
                className="border p-2 flex-grow rounded-l-lg shadow-sm focus:outline-blue-500"
            />
            <button
                type="submit"
                className="bg-red-600 text-white px-4 py-2 rounded-r-lg hover:bg-red-700"
            >
                🔍 검색
            </button>
        </form>
    );
}