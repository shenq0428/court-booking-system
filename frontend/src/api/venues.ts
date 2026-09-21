import type { VenuePage, } from '../types/venues'

const API_BASE_URL = 'http://localhost:8080'

export async function getVenueSummaries(
    page = 0,
    size = 6,
): Promise<VenuePage> {
    const response = await fetch(
        `${API_BASE_URL}/api/venues?page=${page}&size=${size}`,
    )

    if (!response.ok) {
        throw new Error('failed to load venues.')
    }
    return await response.json() as VenuePage
}
