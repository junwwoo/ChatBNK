import SearchBar from './components/SearchBar';
import ResultCard from './components/ResultCard';
import SummaryModal from './components/SummaryModal';
import { useState } from 'react';

export default function App() {
  const [results, setResults] = useState([]);
  const [selected, setSelected] = useState(null);

  // 🔹 백엔드 없이 테스트용 가짜 데이터
  const handleSearch = async (query) => {
    const mockResults = [
      {
        title: "BNK e-파킹통장",
        summary: "입출금이 자유로운 예금으로, 높은 금리 혜택과 자동이체, 즉시이체가 가능한 자유형 통장입니다.",
        detail: "BNK e-파킹통장은 수시입출금식 예금이지만 높은 금리를 제공하는 통장입니다. 자유롭게 입금과 출금이 가능하며, 자동이체 및 간편이체도 지원합니다. 잔액에 따라 차등 금리가 적용될 수 있으며, 모바일로 간편 개설이 가능합니다.",
        url: "https://www.busanbank.co.kr/ib20/mnu/FPMNUPRS000000"
      },
      {
        title: "BNK 정기예금",
        summary: "목돈을 일정 기간 예치하여 안정적인 이자를 받을 수 있는 정기 예금 상품입니다.",
        detail: "BNK 정기예금은 일정 기간 동안 자금을 예치하여 확정 금리를 제공받는 예금 상품입니다. 금리는 예치 기간과 금액에 따라 달라질 수 있으며, 중도 해지 시 약정 금리보다 낮은 이자가 적용될 수 있습니다. 안정적인 자산 관리에 적합합니다.",
        url: "https://www.busanbank.co.kr/ib20/mnu/FPMNUPRS000000"
      },
      {
        title: "BNK 청년e-존예금",
        summary: "부산 지역 청년을 위한 특화 예금 상품으로 우대금리 및 다양한 혜택을 제공합니다.",
        detail: "부산 지역 청년을 대상으로 제공되는 BNK 청년e-존예금은 우대 금리와 혜택을 제공하는 예금 상품입니다. 청년을 위한 적금 및 예금 관련 혜택과 함께, 조건 충족 시 금리를 추가로 받을 수 있습니다. 청년 자산 형성에 특화된 상품입니다.",
        url: "https://www.busanbank.co.kr/ib20/mnu/FPMNUPRS000000"
      }
    ];

    setResults([]); // 로딩 전 초기화
    const keyword = query.trim();
    // 1초 뒤 결과 표시 (API 호출 시뮬레이션)
    setTimeout(() => {
      setResults(
        mockResults.map(r => ({
          ...r,
          summary: r.summary.replace(new RegExp(keyword, "gi"), match => `<mark class="bg-yellow-300">${match}</mark>`),
          title: r.title.replace(new RegExp(keyword, "gi"), match => `<mark class="bg-yellow-300">${match}</mark>`)
        }))
      );
    }, 1000);
  };

  return (
    <div className="min-h-screen bg-gray-100 p-6 flex flex-col items-center">
      <h1 className="text-center text-3xl font-bold mb-6 text-red-600">
        Chat BNK
      </h1>

      {/* ✅ 검색창을 중앙에 배치 */}
      <div className="w-full flex justify-center">
        <SearchBar onSearch={handleSearch} />
      </div>

      {/* ✅ 결과 카드 중앙 정렬 + 폭 제한 */}
      <div className="mt-6 w-full max-w-2xl">
        {results.length === 0 ? (
          <p className="text-center text-gray-500">
            검색 결과가 없습니다. 키워드를 입력해보세요!
          </p>
        ) : (
          results.map((r, i) => (
            <ResultCard
              key={i}
              title={<span dangerouslySetInnerHTML={{ __html: r.title }} />}
              summary={<span dangerouslySetInnerHTML={{ __html: r.summary }} />}
              onClick={() => setSelected(r)}
            />
          ))
        )}
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