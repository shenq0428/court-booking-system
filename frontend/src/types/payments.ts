import type { BookingStatus } from './bookings'

export type PaymentStatus =
    | 'PENDING'
    | 'SUCCEEDED'
    | 'FAILED'
    | 'CANCELLED'
    | 'REFUND_PENDING'
    | 'REFUNDED'

export type PaymentStatusResponse = {
    paymentId: number
    bookingId: number
    paymentStatus: PaymentStatus
    bookingStatus: BookingStatus
}

export type PaymentCheckoutRequest = {
    bookingId: number
}

export type PaymentCheckoutResponse = {
    paymentId: number
    checkoutUrl: string
}

export type ApiErrorResponse = {
    message?: string
}
