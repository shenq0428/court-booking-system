export type SportType =
    | 'Badminton'
    | 'Pickleball'

export type VenueSummary = {
    id: number
    name: string
    address: string
    sports: SportType[]
    pricePerHour: number | null
    imageUrl: string | null
}

export type VenuePage = {
    content: VenueSummary[]
    empty: boolean
    first: boolean
    last: boolean
    number: number
    numberOfElements: number
    size: number
    totalElements: number
    totalPages: number
}