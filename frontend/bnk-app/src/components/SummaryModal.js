export default function SummaryModal({ show, title, content, link, onClose }) {
    if (!show) return null;

    return (
        <div className="fixed inset-0 bg-black bg-opacity-40 flex justify-center items-center z-50">
            <div className="bg-white rounded-2xl p-6 w-96 shadow-lg">
                <h2
                    className="text-xl font-bold mb-3 text-black-700"
                    dangerouslySetInnerHTML={{ __html: title.replace(/<\/?mark[^>]*>/g, "") }}
                ></h2>
                <p className="text-gray-700 mb-4" dangerouslySetInnerHTML={{ __html: content }}></p>
                <div className="flex justify-end gap-3">
                    <button
                        onClick={onClose}
                        className="px-3 py-1 bg-gray-400 rounded-lg text-white hover:bg-gray-500"
                    >
                        닫기
                    </button>
                    <a
                        href={link}
                        target="_blank"
                        rel="noreferrer"
                        className="px-3 py-1 bg-red-500 rounded-lg text-white hover:bg-red-700"
                    >
                        서비스 이동
                    </a>
                </div>
            </div>
        </div>
    );
}