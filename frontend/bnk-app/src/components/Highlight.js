export default function Highlight({ text, keyword }) {
  if (!keyword || !text) return <>{text}</>;

  const escaped = keyword.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
  const parts = text.split(new RegExp(`(${escaped})`, 'gi'));

  return (
    <>
      {parts.map((part, i) =>
        part.toLowerCase() === keyword.toLowerCase() ? (
          <mark key={i} className="bg-yellow-300">{part}</mark>
        ) : (
          part
        )
      )}
    </>
  );
}
