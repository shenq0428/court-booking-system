export type Sport =
|'Badminton'
|'Pickleball'

export type Venue = {
    id:number
    name:string
    address:string
    sports:Sport[]
    pricePerHour:number
    isOpen:boolean
    imageUrl:string
}