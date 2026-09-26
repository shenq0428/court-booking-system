import { useEffect, useState } from 'react'
import { Link } from 'react-router'
import { useAuth } from '../auth/AuthContext'
import type { BookingResponse, BookingStatus } from '../types/bookings'

function formatDateTime(value: string) {
    return new Intl.DateTimeFormat('en-MY', {
        dateStyle: 'medium',
        timeStyle: 'short',
        timeZone: 'Asia/Kuala_Lumpur',
    }).format(new Date(value))
}

function formatStatus(status: BookingStatus) {
    return status
        .replaceAll('_', ' ')
        .toLowerCase()
        .replace(/\b\w/g, (letter) => letter.toUpperCase())
}

function MyBookingsPage() {
    const { accessToken, isAuthenticated, clearSession } = useAuth()

    const [bookings, setBookings] = useState<BookingResponse[]>([])
    const [isLoading, setIsLoading] = useState(true)
    const [error, setError] = useState<string | null>(null)
    const [cancellingBookingId, setCancellingBookingId] =
        useState<number | null>(null)

    useEffect(() => {
        if (!accessToken) {
            setIsLoading(false)
            return
        }

        async function loadBookings() {
            setIsLoading(true)
            setError(null)

            try {
                const response = await fetch(
                    'http://localhost:8080/api/bookings/me',
                    {
                        headers: {
                            Authorization: `Bearer ${accessToken}`,
                        },
                    },
                )

                if (response.status === 401) {
                    clearSession()
                    throw new Error(
                        'Your session has expired. Please log in again.',
                    )
                }

                if (!response.ok) {
                    throw new Error(`Failed to load your bookings. Status:${response.status}`)
                }

                const bookingData =
                    await response.json() as BookingResponse[]

                setBookings(bookingData)
            } catch (caughtError) {
                setError(
                    caughtError instanceof Error
                        ? caughtError.message
                        : 'Cannot connect to the server.',
                )
            } finally {
                setIsLoading(false)
            }
        }

        void loadBookings()
    }, [accessToken, clearSession])

    async function handleCancelBooking(bookingId: number) {
        if (!accessToken) {
            return
        }

        const confirmed = window.confirm(
            'Are you sure you want to cancel this booking?',
        )

        if (!confirmed) {
            return
        }

        setCancellingBookingId(bookingId)
        setError(null)

        try {
            const response = await fetch(
                `http://localhost:8080/api/bookings/${bookingId}/cancel`,
                {
                    method: 'POST',
                    headers: {
                        Authorization: `Bearer ${accessToken}`,
                    },
                },
            )

            if (response.status === 401) {
                clearSession()
                throw new Error(
                    'Your session has expired. Please log in again.',
                )
            }

            if (response.status === 404) {
                throw new Error('Booking not found.')
            }

            if (response.status === 409) {
                throw new Error(
                    'This booking can no longer be cancelled.',
                )
            }

            if (!response.ok) {
                throw new Error('Failed to cancel the booking.')
            }

            const updatedBooking =
                await response.json() as BookingResponse

            setBookings((currentBookings) =>
                currentBookings.map((booking) =>
                    booking.id === updatedBooking.id
                        ? updatedBooking
                        : booking,
                ),
            )
        } catch (caughtError) {
            setError(
                caughtError instanceof Error
                    ? caughtError.message
                    : 'Cannot connect to the server.',
            )
        } finally {
            setCancellingBookingId(null)
        }
    }

    if (!isAuthenticated && !isLoading) {
        return (
            <main className="my-bookings-page">
                <section className="my-bookings-empty">
                    <h1>My Bookings</h1>
                    <p>Please log in to view your bookings.</p>
                    <Link to="/login">Log in</Link>
                </section>
            </main>
        )
    }

    const sortedBookings = [...bookings].sort(
        (firstBooking, secondBooking) =>
            new Date(secondBooking.createdAt).getTime()
            - new Date(firstBooking.createdAt).getTime(),
    )

    const upcomingBookings = sortedBookings.filter(
        (booking) =>
            booking.status === 'PENDING_PAYMENT'
            || (
                booking.status === 'CONFIRMED'
                && new Date(booking.endAt).getTime()
                >= Date.now()
            ),
    )

    const historyBookings = sortedBookings.filter(
        (booking) =>
            booking.status === 'CANCELLED'
            || booking.status === 'EXPIRED'
            || (
                booking.status === 'CONFIRMED'
                && new Date(booking.endAt).getTime()
                < Date.now()
            ),
    )

    function renderBookingCard(booking: BookingResponse) {
        return (
            <article
                className="booking-card"
                key={booking.id}
            >
                <div className="booking-card-heading">
                    <div>
                        <p className="booking-reference">
                            Booking #{booking.id}
                        </p>

                        <h3>{booking.venueName}</h3>
                    </div>

                    <span
                        className={`booking-status ${booking.status.toLowerCase()}`}
                    >
                        {formatStatus(booking.status)}
                    </span>
                </div>

                <div className="booking-details">
                    <p>
                        <span>Court</span>
                        Court {booking.courtNumber}
                    </p>

                    <p>
                        <span>Sport</span>
                        {booking.sport === 'BADMINTON'
                            ? 'Badminton'
                            : 'Pickleball'}
                    </p>

                    <p>
                        <span>Start</span>
                        {formatDateTime(booking.startAt)}
                    </p>

                    <p>
                        <span>End</span>
                        {formatDateTime(booking.endAt)}
                    </p>

                    <p>
                        <span>Price</span>
                        RM {booking.priceAtBooking.toFixed(2)}
                    </p>
                </div>

                {booking.status === 'PENDING_PAYMENT' && (
                    <p className="booking-expiry">
                        Payment hold expires at{' '}
                        {formatDateTime(booking.expiresAt)}
                    </p>
                )}

                <div className="booking-card-actions">
                    <Link
                        to={`/venues/${booking.venueId}`}
                    >
                        View venue
                    </Link>

                    {booking.status === 'PENDING_PAYMENT' && (
                        <button
                            type="button"
                            disabled={
                                cancellingBookingId
                                === booking.id
                            }
                            onClick={() =>
                                void handleCancelBooking(
                                    booking.id,
                                )
                            }
                        >
                            {cancellingBookingId === booking.id
                                ? 'Cancelling...'
                                : 'Cancel booking'}
                        </button>
                    )}
                </div>
            </article>
        )
    }

    return (
        <main className="my-bookings-page">
            <div className="my-bookings-heading">
                <div>
                    <p>Your reservations</p>
                    <h1>My Bookings</h1>
                </div>

                <Link to="/find-court">
                    Find another court
                </Link>
            </div>

            {isLoading && (
                <p>Loading your bookings...</p>
            )}

            {error && (
                <p role="alert" className="my-bookings-error">
                    {error}
                </p>
            )}

            {!isLoading && bookings.length === 0 && (
                <section className="my-bookings-empty">
                    <h2>No bookings yet</h2>
                    <p>
                        Your upcoming and previous bookings will
                        appear here.
                    </p>
                    <Link to="/find-court">
                        Find a court
                    </Link>
                </section>
            )}

            {!isLoading && upcomingBookings.length > 0 && (
                <section className="booking-section">
                    <div className="booking-section-heading">
                        <div>
                            <h2>Upcoming Bookings</h2>
                            <p>
                                Your active reservations and
                                pending payments.
                            </p>
                        </div>

                        <span>
                            {upcomingBookings.length}
                        </span>
                    </div>

                    <div className="booking-list">
                        {upcomingBookings.map((booking) =>
                            renderBookingCard(booking),
                        )}
                    </div>
                </section>
            )}

            {!isLoading && historyBookings.length > 0 && (
                <section className="booking-section booking-history-section">
                    <div className="booking-section-heading">
                        <div>
                            <h2>Booking History</h2>
                            <p>
                                Your cancelled, expired and
                                completed reservations.
                            </p>
                        </div>

                        <span>
                            {historyBookings.length}
                        </span>
                    </div>

                    <div className="booking-list">
                        {historyBookings.map((booking) => renderBookingCard(booking),
                        )}
                    </div>
                </section>
            )}
        </main>
    )
}

export default MyBookingsPage