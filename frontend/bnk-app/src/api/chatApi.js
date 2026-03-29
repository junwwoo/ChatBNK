const API_BASE = 'http://localhost:8080';

export async function fetchProducts(query, limit = 10) {
  const params = new URLSearchParams({ q: query, limit });
  const res = await fetch(`${API_BASE}/api/chat/query?${params}`);

  if (!res.ok) {
    const body = await res.json().catch(() => null);
    throw new Error(body?.message || `서버 오류 (${res.status})`);
  }

  return res.json();
}
