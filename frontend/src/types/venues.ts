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