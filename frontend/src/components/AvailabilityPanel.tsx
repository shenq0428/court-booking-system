import { useState, type SubmitEvent } from 'react'
import type { Venue } from '../types/venues'
import { Trophy, MapPin, CalendarDays, Clock3, } from 'lucide-react'

type AvailabilityPanelProps = {
    venue: Venue
}

type SelectedSlot = {
    time: string
    courtNumber: number
}
const selectedDateLabel = 'Thu, 16 May 2026'

const timeRangeLabels: Record<string, string> = {
    '6.00 PM': '6:00 PM – 7:00 PM',
    '7.00 PM': '7:00 PM – 8:00 PM',
    '8.00 PM': '8:00 PM – 9:00 PM',
}

const times: string[] = [
    '6.00 PM',
    '7.00 PM',
    '8.00 PM',
]

const courts: number[] = [1, 2, 3, 4]

const unavailableSlotIds: string[] = [
    '6.00 PM-3',
    '7.00 PM-2',
    '8.00 PM-3',
]

function AvailabilityPanel({ venue, }: AvailabilityPanelProps) {
    //用户选中了哪个 Court 和时间
    const [selectedSlot, setSelectedSlot] = useState<SelectedSlot | null>(null)
    //要不要显示预约表单
    const [isBookingFormOpen, setIsBookingFormOpen] = useState(false)
    //顾客姓名和电话
    const [customerName, setCustomerName] = useState('')
    const [customerPhone, setCustomerPhone] = useState('')
    //是否已经模拟提交
    const [bookingSubmitted, setBookingSubmitted] = useState(false)
    //拒绝非登录用户付费
    const isAuthenticated = false
    const [authError, setAuthError] = useState<string | null>(null)


    function handleSlotSelect(
        time: string,
        courtNumber: number,
    ) {
        setSelectedSlot({
            time,
            courtNumber,
        })
        setIsBookingFormOpen(false)
        setBookingSubmitted(false)
        setCustomerName('')
        setCustomerPhone('')
        setAuthError(null)
    }

    function handleContinueBooking() {
        if (!isAuthenticated) {
            setAuthError('Please log in before continuing your booking.',)
            return
        }
        setAuthError(null)
        setIsBookingFormOpen(true)
    }

    function handleBookingSubmit(event: SubmitEvent<HTMLFormElement>,) {
        event.preventDefault()
        setBookingSubmitted(true)
    }
    return (
        <section className="availability-layout">
            <div className="availability-panel">
                <div className="availability-heading">
                    <div>
                        <h2>Available today</h2>
                        <p>{venue.name} · Select one time slot</p>
                    </div>
                </div>

                <div className="slot-table">
                    <div className="slot-header">
                        <span>Time</span>

                        {courts.map((courtNumber) => (
                            <span key={courtNumber}>
                                Court {courtNumber}
                            </span>
                        ))}

                    </div>

                    {times.map((time) => (
                        <div className="slot-row" key={time}>
                            <strong>{time}</strong>
                            {courts.map((courtNumber) => {
                                const slotId = `${time}-${courtNumber}`
                                const isAvailable = !unavailableSlotIds.includes(slotId)
                                const isSelected =
                                    selectedSlot?.time === time &&
                                    selectedSlot?.courtNumber === courtNumber
                                return (
                                    <button key={slotId} type="button" disabled={!isAvailable} className={
                                        isSelected ? 'time-slot selected' : isAvailable ? 'time-slot available' : 'time-slot unavailable'}
                                        onClick={() => handleSlotSelect(time, courtNumber)}>
                                        {isSelected ? 'Selected' : isAvailable ? 'Available' : 'Unavailable'}
                                    </button>
                                )
                            })}
                        </div>
                    ))}
                </div>
            </div>
            <aside className="booking-summary-panel">
                <h2>Booking summary</h2>
                {!selectedSlot ? (
                    <p className="booking-empty">
                        Select an available time slot to continue.
                    </p>
                ) : (
                    <>
                        <div className="summary-venue">
                            <img className="summary-venue-image"
                                src={venue.imageUrl}
                                alt={`${venue.name} indoor courts`}
                            />
                            <div className="summary-venue-details">
                                <strong>{venue.name}</strong>
                                <strong>{venue.address}</strong>
                            </div>
                        </div>

                        <div className="summary-divider" />

                        <div className="summary-details">
                            <div>
                                <Trophy
                                    className="summary-icon"
                                    size={18}
                                    aria-hidden="true" />
                                <span>{venue.sports.join(' & ')}</span>
                            </div>

                            <div>
                                <MapPin
                                    className="summary-icon"
                                    size={18}
                                    aria-hidden="true" />
                                <span>Court {selectedSlot.courtNumber}</span>
                            </div>

                            <div>
                                <CalendarDays
                                    className="summary-icon"
                                    size={18}
                                    aria-hidden="true"
                                />
                                <span>{selectedDateLabel}</span>
                            </div>

                            <div>
                                <Clock3
                                    className="summary-icon"
                                    size={18}
                                    aria-hidden="true"
                                />
                                <span>
                                    {timeRangeLabels[selectedSlot.time]}
                                </span>
                            </div>

                        </div>

                        <div className="summary-total">
                            <span>Total</span>
                            <strong>RM {venue.pricePerHour.toFixed(2)}</strong>
                        </div>

                        {!isBookingFormOpen && (
                            <>
                                <button className="summary-continue-button"
                                    type="button"
                                    onClick={handleContinueBooking}>
                                    Continue Booking →
                                </button>
                                {authError&&(<div className="auth-required-message"
                                role="alert">
                                    {authError}
                                </div>)}
                                <p className="summary-note"> ♢ No payment required in this demo</p>
                            </>
                        )}
                        {/*bookingform open*/}
                        {isBookingFormOpen && (
                            <div className="booking-form-preview">
                                {!bookingSubmitted ? (
                                    <form
                                        className="booking-form"
                                        onSubmit={handleBookingSubmit}
                                    >
                                        <label className="booking-field">
                                            <span>Customer name</span>

                                            <input
                                                type="text"
                                                value={customerName}
                                                onChange={(event) =>
                                                    setCustomerName(event.target.value)
                                                }
                                                placeholder="Enter customer name"
                                                required
                                            />
                                        </label>

                                        <label className="booking-field">
                                            <span>Phone number</span>

                                            <input
                                                type="tel"
                                                value={customerPhone}
                                                onChange={(event) =>
                                                    setCustomerPhone(event.target.value)
                                                }
                                                placeholder="e.g. 012-345-6789"
                                                required
                                            />
                                        </label>

                                        <button
                                            className="booking-submit-button"
                                            type="submit"
                                        >
                                            Confirm Booking
                                        </button>
                                    </form>
                                ) : (
                                    <div
                                        className="booking-success"
                                        role="status"
                                    >
                                        <strong>
                                            Booking created successfully
                                        </strong>

                                        <p>
                                            {customerName}, your booking for
                                            Court {selectedSlot.courtNumber} at{' '}
                                            {timeRangeLabels[selectedSlot.time]} has
                                            been recorded.
                                        </p>
                                    </div>
                                )}
                            </div>
                        )}
                    </>
                )}
            </aside>

        </section >
    )
}

export default AvailabilityPanel