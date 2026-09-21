import type {
    VenuePageResponse,
} from '../types/venues'

export async function getVenueSummaries(
    page = 0,
    size = 6,
): Promise<VenuePageResponse> {
    const response = await fetch(
        `http://localhost:8080/api/venues?page=${page}&size=${size}`,
    )

    if (!response.ok) {
        throw new Error(
            'Failed to load venues.',
        )
    }

    return (await response.json()) as VenuePageResponse
}