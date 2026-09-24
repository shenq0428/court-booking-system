import type { AvailabilitySportType, VenueAvailabilityResponse,} from '../types/availability'

const API_BASE_URL = 'http://localhost:8080'

export async function fetchVenueAvailability(
    venueId: number,
    date: string,
    sport?: AvailabilitySportType,
): Promise<VenueAvailabilityResponse> {
    const queryParameters = new URLSearchParams({
        date,
    })

    if (sport) {
        queryParameters.set('sport', sport)
    }

    const response = await fetch(
        `${API_BASE_URL}/api/venues/${venueId}/availability?${queryParameters.toString()}`,
    )

    if (!response.ok) {
        throw new Error(
            'Failed to load venue availability.',
        )
    }

    return await response.json()as VenueAvailabilityResponse
}