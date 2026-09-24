export type BookingStatus =
| 'PENDING_PAYMENT'
| 'CONFIRMED'
| 'CANCELLED'
| 'EXPIRED'

export type BookingSport =
| 'BADMINTON'
| 'PICKLEBALL'

export type BookingResponse ={
    id:number
    userId:number
    courtId:number
    venueId:number
    venueName:string
    courtNumber:number
    sport:BookingSport
    startAt:string
    endAt:string
    priceAtBooking:number
    status:BookingStatus
    expiresAt:string
    createdAt:string
}