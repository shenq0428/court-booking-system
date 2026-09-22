export type Sport =
    | 'Badminton'
    | 'Pickleball'

export type Venue = {
    id: number
    name: string
    address: string
    sports: Sport[]
    pricePerHour: number
    isOpen: boolean
    imageUrl: string
}

export type ApiSportType =
    | 'BADMINTON'
    | 'PICKLEBALL'

export type VenueSummaryResponse = {
    id: number
    name: string
    address: string
    sports: ApiSportType[]
    startingPricePerHour: number | null
    imageUrl: string | null
}

export type VenuePageResponse = {
    content: VenueSummaryResponse[]
    empty: boolean
    first: boolean
    last: boolean
    number: number
    numberOfElements: number
    size: number
    totalElements: number
    totalPages: number
}

export type VenueDetailsResponse = {
    id: number
    name: string
    description: string | null
    addressLine1: string
    addressLine2: string | null
    city: string
    state: string
    postalCode: string
    latitude: number | null
    longitude: number | null
    phoneNumber: string | null
    imageUrl: string | null
    active: boolean
}

export type CourtResponse = {
    id: number
    venueId: number
    courtNumber: number
    sport: ApiSportType
    pricePerHour: number
    active: boolean
}