import SearchBar from './components/SearchBar';
import ResultCard from './components/ResultCard';
import SummaryModal from './components/SummaryModal';
import Highlight from './components/Highlight';
import { fetchProducts } from './api/chatApi';
import { useState } from 'react';

export default function App() {
  const [results, setResults] = useState([]);
  const [selected, setSelected] = useState(null);
  const [keyword, setKeyword] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [searched, setSearched] = useState(false);

  const handleSearch = async (query) => {
    const trimmed = query.trim();
    setKeyword(trimmed);
    setLoading(true);
    setError(null);
    setResults([]);
    setSearched(true);

    try {
      const data = await fetchProducts(trimmed);

      if (data.category === 'UNKNOWN') {
        setResults([]);
        setError(data.message || '질문을 이해하지 못했습니다. 다른 키워드로 검색해보세요.');
        return;
      }

      setResults(
        (data.products || []).map((p) => ({
          title: p.name,
          summary: p.description,
          detail: p.extraText,
          url: p.detailUrl,
        }))
      );
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gray-100 p-6 flex flex-col items-center">
      <h1 className="text-center text-3xl font-bold mb-6 text-red-600">
        Chat BNK
      </h1>

      <div className="w-full flex justify-center">
        <SearchBar onSearch={handleSearch} />
      </div>

      <div className="mt-6 w-full max-w-2xl">
        {error && (
          <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded mb-4">
            {error}
          </div>
        )}

        {loading && (
          <div className="flex justify-center py-8">
            <div className="w-8 h-8 border-4 border-red-600 border-t-transparent rounded-full animate-spin" />
          </div>
        )}

        {!loading && !error && searched && results.length === 0 && (
          <p className="text-center text-gray-500">검색 결과가 없습니다.</p>
        )}

        {!loading && !searched && (
          <p className="text-center text-gray-500">
            검색 결과가 없습니다. 키워드를 입력해보세요!
          </p>
        )}

        {!loading &&
          results.map((r, i) => (
            <ResultCard
              key={i}
              title={<Highlight text={r.title} keyword={keyword} />}
              summary={<Highlight text={r.summary} keyword={keyword} />}
              onClick={() => setSelected(r)}
            />
          ))}
      </div>

      <SummaryModal
        show={!!selected}
        title={selected?.title}
        content={selected?.detail}
        link={selected?.url}
        onClose={() => setSelected(null)}
      />
    </div>
  );
}
