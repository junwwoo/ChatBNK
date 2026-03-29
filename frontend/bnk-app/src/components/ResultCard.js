export default function ResultCard({ title, summary, onClick }) {
    return (
        <div
            className="bg-white border rounded-xl p-4 shadow-md hover:shadow-xl transition mb-4 cursor-pointer"
            onClick={onClick}
        >
            <h3 className="font-semibold text-lg mb-2 text-black">{title}</h3>
            <p className="text-gray-600">{summary}</p>
            <div className="text-right mt-2">
                <button className="text-red-600 underline hover:text-red-800">
                    자세히 보기 →
                </button>
            </div>
        </div>
    );
}