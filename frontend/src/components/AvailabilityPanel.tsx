import { useEffect, useState } from 'react'
import { Trophy, MapPin, CalendarDays, Clock3 } from 'lucide-react'
import { fetchVenueAvailability } from '../api/availability'
import { useAuth } from '../auth/AuthContext'
import type { AvailabilitySlotResponse,AvailabilitySportType, VenueAvailabilityResponse,} from '../types/availability'

import type { Venue } from '../types/venues'

type AvailabilityPanelProps = {
    venue: Venue
}

type SelectedSlot = {
    courtId: number
    courtNumber: number
    sport: AvailabilitySportType
    pricePerHour: number
    startAt: string
    endAt: string
}

type CreatedBooking = {
    id: number
    status: string
    expiresAt: string
}

const API_BASE_URL = 'http://localhost:8080'
const MALAYSIA_TIME_ZONE = 'Asia/Kuala_Lumpur'

function getMalaysiaDate() {
    return new Intl.DateTimeFormat('en-CA', {
        timeZone: MALAYSIA_TIME_ZONE,
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
    }).format(new Date())
}

function formatTime(instant: string) {
    return new Intl.DateTimeFormat('en-MY', {
        timeZone: MALAYSIA_TIME_ZONE,
        hour: 'numeric',
        minute: '2-digit',
        hour12: true,
    }).format(new Date(instant))
}

function formatDate(date: string) {
    return new Intl.DateTimeFormat('en-MY', {
        timeZone: 'UTC',
        weekday: 'short',
        day: 'numeric',
        month: 'short',
        year: 'numeric',
    }).format(new Date(`${date}T00:00:00Z`))
}

function formatSport(sport: AvailabilitySportType) {
    return sport === 'BADMINTON'
        ? 'Badminton'
        : 'Pickleball'
}

function AvailabilityPanel({ venue }: AvailabilityPanelProps) {
    const { accessToken, isAuthenticated } = useAuth()

    const [selectedDate, setSelectedDate] = useState(getMalaysiaDate())
    const [availability, setAvailability] = useState<VenueAvailabilityResponse | null>(null)
    const [selectedSlot, setSelectedSlot] =useState<SelectedSlot | null>(null)
    const [isLoading, setIsLoading] = useState(true)
    const [loadError, setLoadError] = useState<string | null>(null)
    const [authError, setAuthError] = useState<string | null>(null)
    const [bookingError, setBookingError] = useState<string | null>(null)
    const [isBookingFormOpen, setIsBookingFormOpen] = useState(false)
    const [isSubmitting, setIsSubmitting] = useState(false)
    const [createdBooking, setCreatedBooking] =useState<CreatedBooking | null>(null)
    const [reloadNumber, setReloadNumber] = useState(0)

    useEffect(() => {
        let ignoreResult = false

        async function loadAvailability() {
            setIsLoading(true)
            setLoadError(null)

            try {
                const result = await fetchVenueAvailability(
                    venue.id,
                    selectedDate,
                )

                if (!ignoreResult) {
                    setAvailability(result)
                }
            } catch {
                if (!ignoreResult) {
                    setLoadError(
                        'Cannot load availability. Please try again.',
                    )
                }
            } finally {
                if (!ignoreResult) {
                    setIsLoading(false)
                }
            }
        }

        void loadAvailability()

        return () => {
            ignoreResult = true
        }
    }, [venue.id, selectedDate, reloadNumber])

    const courts = availability?.courts ?? []
    const timeSlots = courts[0]?.slots ?? []

    const gridStyle = {
        gridTemplateColumns:
            `110px repeat(${courts.length}, minmax(120px, 1fr))`,
    }

    function resetBookingSelection() {
        setSelectedSlot(null)
        setIsBookingFormOpen(false)
        setCreatedBooking(null)
        setAuthError(null)
        setBookingError(null)
    }

    function handleSlotSelect(
        courtId: number,
        courtNumber: number,
        sport: AvailabilitySportType,
        pricePerHour: number,
        slot: AvailabilitySlotResponse,
    ) {
        setSelectedSlot({
            courtId,
            courtNumber,
            sport,
            pricePerHour,
            startAt: slot.startAt,
            endAt: slot.endAt,
        })

        setIsBookingFormOpen(false)
        setCreatedBooking(null)
        setAuthError(null)
        setBookingError(null)
    }

    function handleContinueBooking() {
        if (!isAuthenticated || accessToken === null) {
            setAuthError(
                'Please log in before continuing your booking.',
            )
            return
        }

        setAuthError(null)
        setIsBookingFormOpen(true)
    }

    async function handleCreateBooking() {
        if (selectedSlot === null || accessToken === null) {
            return
        }

        setIsSubmitting(true)
        setBookingError(null)

        try {
            const response = await fetch(
                `${API_BASE_URL}/api/bookings`,
                {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        Authorization: `Bearer ${accessToken}`,
                    },
                    credentials: 'include',
                    body: JSON.stringify({
                        courtId: selectedSlot.courtId,
                        startAt: selectedSlot.startAt,
                        endAt: selectedSlot.endAt,
                    }),
                },
            )

            if (!response.ok) {
                if (response.status === 409) {
                    setBookingError(
                        'This time slot was just taken. Please choose another slot.',
                    )
                } else if (response.status === 401) {
                    setBookingError(
                        'Your login has expired. Please log in again.',
                    )
                } else if (response.status === 400) {
                    setBookingError(
                        'This booking request is invalid.',
                    )
                } else {
                    setBookingError(
                        'Booking failed. Please try again.',
                    )
                }

                return
            }

            const result = await response.json() as CreatedBooking

            setCreatedBooking(result)

            setReloadNumber(
                (currentNumber) => currentNumber + 1,
            )
        } catch {
            setBookingError(
                'Cannot connect to the server.',
            )
        } finally {
            setIsSubmitting(false)
        }
    }

    return (
        <section className="availability-layout">
            <div className="availability-panel">
                <div className="availability-heading">
                    <div>
                        <h2>Check availability</h2>

                        <p>
                            {venue.name} · Select one time slot
                        </p>
                    </div>

                    <label className="availability-date-field">
                        <span>Date</span>

                        <input
                            type="date"
                            min={getMalaysiaDate()}
                            value={selectedDate}
                            onChange={(event) => {
                                setSelectedDate(event.target.value)
                                resetBookingSelection()
                            }}
                        />
                    </label>
                </div>

                {isLoading && (
                    <p>Loading availability...</p>
                )}

                {loadError && (
                    <p role="alert">
                        {loadError}
                    </p>
                )}

                {!isLoading &&
                    !loadError &&
                    courts.length === 0 && (
                        <p>
                            No active courts are available
                            for this date.
                        </p>
                    )}

                {!isLoading &&
                    !loadError &&
                    courts.length > 0 && (
                        <div className="slot-table">
                            <div
                                className="slot-header"
                                style={gridStyle}
                            >
                                <span>Time</span>

                                {courts.map((court) => (
                                    <span key={court.courtId}>
                                        Court {court.courtNumber}
                                    </span>
                                ))}
                            </div>

                            {timeSlots.map((timeSlot) => (
                                <div
                                    className="slot-row"
                                    key={timeSlot.startAt}
                                    style={gridStyle}
                                >
                                    <strong>
                                        {formatTime(timeSlot.startAt)}
                                    </strong>

                                    {courts.map((court) => {
                                        const slot = court.slots.find(
                                            (currentSlot) =>
                                                currentSlot.startAt ===
                                                timeSlot.startAt,
                                        )

                                        const isAvailable =
                                            slot?.available === true

                                        const isSelected =
                                            selectedSlot?.courtId ===
                                                court.courtId &&
                                            selectedSlot.startAt ===
                                                timeSlot.startAt

                                        return (
                                            <button
                                                key={
                                                    `${court.courtId}-${timeSlot.startAt}`
                                                }
                                                type="button"
                                                disabled={!isAvailable}
                                                className={
                                                    isSelected
                                                        ? 'time-slot selected'
                                                        : isAvailable
                                                            ? 'time-slot available'
                                                            : 'time-slot unavailable'
                                                }
                                                onClick={() => {
                                                    if (slot !== undefined) {
                                                        handleSlotSelect(
                                                            court.courtId,
                                                            court.courtNumber,
                                                            court.sport,
                                                            court.pricePerHour,
                                                            slot,
                                                        )
                                                    }
                                                }}
                                            >
                                                {isSelected
                                                    ? 'Selected'
                                                    : isAvailable
                                                        ? 'Available'
                                                        : 'Unavailable'}
                                            </button>
                                        )
                                    })}
                                </div>
                            ))}
                        </div>
                    )}
            </div>

            <aside className="booking-summary-panel">
                <h2>Booking summary</h2>

                {selectedSlot === null ? (
                    <p className="booking-empty">
                        Select an available time slot to continue.
                    </p>
                ) : (
                    <>
                        <div className="summary-venue">
                            <img
                                className="summary-venue-image"
                                src={venue.imageUrl}
                                alt={`${venue.name} indoor courts`}
                            />

                            <div className="summary-venue-details">
                                <strong>{venue.name}</strong>
                                <span>{venue.address}</span>
                            </div>
                        </div>

                        <div className="summary-divider" />

                        <div className="summary-details">
                            <div>
                                <Trophy
                                    className="summary-icon"
                                    size={18}
                                    aria-hidden="true"
                                />

                                <span>
                                    {formatSport(selectedSlot.sport)}
                                </span>
                            </div>

                            <div>
                                <MapPin
                                    className="summary-icon"
                                    size={18}
                                    aria-hidden="true"
                                />

                                <span>
                                    Court {selectedSlot.courtNumber}
                                </span>
                            </div>

                            <div>
                                <CalendarDays
                                    className="summary-icon"
                                    size={18}
                                    aria-hidden="true"
                                />

                                <span>
                                    {formatDate(selectedDate)}
                                </span>
                            </div>

                            <div>
                                <Clock3
                                    className="summary-icon"
                                    size={18}
                                    aria-hidden="true"
                                />

                                <span>
                                    {formatTime(selectedSlot.startAt)}
                                    {' – '}
                                    {formatTime(selectedSlot.endAt)}
                                </span>
                            </div>
                        </div>

                        <div className="summary-total">
                            <span>Total</span>

                            <strong>
                                RM {selectedSlot.pricePerHour.toFixed(2)}
                            </strong>
                        </div>

                        {!isBookingFormOpen &&
                            createdBooking === null && (
                                <>
                                    <button
                                        className="summary-continue-button"
                                        type="button"
                                        onClick={handleContinueBooking}
                                    >
                                        Continue Booking →
                                    </button>

                                    {authError && (
                                        <div
                                            className="auth-required-message"
                                            role="alert"
                                        >
                                            {authError}
                                        </div>
                                    )}

                                    <p className="summary-note">
                                        The slot will be held
                                        for 10 minutes.
                                    </p>
                                </>
                            )}

                        {isBookingFormOpen &&
                            createdBooking === null && (
                                <div className="booking-form-preview">
                                    <p>
                                        This slot will be reserved
                                        while payment is pending.
                                    </p>

                                    <button
                                        className="booking-submit-button"
                                        type="button"
                                        disabled={isSubmitting}
                                        onClick={() =>
                                            void handleCreateBooking()
                                        }
                                    >
                                        {isSubmitting
                                            ? 'Creating booking...'
                                            : 'Reserve Slot'}
                                    </button>

                                    {bookingError && (
                                        <div role="alert">
                                            {bookingError}
                                        </div>
                                    )}
                                </div>
                            )}

                        {createdBooking && (
                            <div
                                className="booking-success"
                                role="status"
                            >
                                <strong>
                                    Slot reserved successfully
                                </strong>

                                <p>
                                    Booking #{createdBooking.id}
                                    {' is '}
                                    {createdBooking.status}.
                                </p>

                                <p>
                                    Complete payment before{' '}
                                    {formatTime(
                                        createdBooking.expiresAt,
                                    )}
                                    .
                                </p>
                            </div>
                        )}
                    </>
                )}
            </aside>
        </section>
    )
}

export default AvailabilityPanel