export type AvailabilitySportType =
    | 'BADMINTON'
    | 'PICKLEBALL'

export type AvailabilitySlotResponse = {
    startAt: string
    endAt: string
    available: boolean
}

export type CourtAvailabilityResponse = {
    courtId: number
    courtNumber: number
    sport: AvailabilitySportType
    pricePerHour: number
    slots: AvailabilitySlotResponse[]
}

export type VenueAvailabilityResponse = {
    venueId: number
    date: string
    timeZone: string
    courts: CourtAvailabilityResponse[]
}