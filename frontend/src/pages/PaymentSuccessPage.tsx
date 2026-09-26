import { useEffect, useState } from 'react'
import { Link, useSearchParams } from 'react-router'
import { useAuth } from '../auth/AuthContext'
import { getPaymentStatus } from '../api/payments'

type PageState = 'checking' | 'confirmed' | 'processing' | 'failed'

function PaymentSuccessPage() {
    const [searchParams] = useSearchParams()
    const { accessToken } = useAuth()
    const sessionId = searchParams.get('session_id')

    const [pageState, setPageState] = useState<PageState>('checking')
    const [bookingId, setBookingId] = useState<number | null>(null)

    useEffect(() => {
        if (!sessionId || !accessToken) return

        let stopped = false
        let timeoutId: number | undefined
        let attempts = 0

        async function checkStatus() {
            attempts += 1

            try {
                const result = await getPaymentStatus(sessionId!, accessToken!)
                if (stopped) return
                setBookingId(result.bookingId)

                if (
                    result.paymentStatus === 'SUCCEEDED'&& result.bookingStatus === 'CONFIRMED'
                ) {
                    setPageState('confirmed')
                    return
                }

                if (
                    result.paymentStatus === 'FAILED' || result.paymentStatus === 'CANCELLED'
                ) {
                    setPageState('failed')
                    return
                }

                if (attempts < 10) {
                    timeoutId = window.setTimeout(checkStatus, 1000)
                } else {
                    setPageState('processing')
                }
            } catch {
                if (!stopped) setPageState('failed')
            }
        }

        void checkStatus()

        return () => {
            stopped = true
            if (timeoutId !== undefined) window.clearTimeout(timeoutId)
        }
    }, [sessionId, accessToken])

    if (!sessionId) {
        return (
            <main className="payment-result-page">
                <section className="payment-result-card">
                    <div className="payment-result-icon error">!</div>
                    <h1>Invalid payment link</h1>
                    <p>The Checkout Session ID is missing.</p>
                    <Link to="/my-bookings">View My Bookings</Link>
                </section>
            </main>
        )
    }

    return (
        <main className="payment-result-page">
            <section className="payment-result-card">
                <div className={`payment-result-icon ${pageState === 'confirmed' ? 'success' : ''}`}>
                    {pageState === 'confirmed' ? '✓' : '…'}
                </div>

                {pageState === 'checking' && (
                    <>
                        <h1>Checking your payment</h1>
                        <p>Please wait while CourtFlow confirms your booking.</p>
                    </>
                )}

                {pageState === 'confirmed' && (
                    <>
                        <p className="payment-result-label">Payment confirmed</p>
                        <h1>Your court is booked</h1>
                        <p>Booking #{bookingId} has been confirmed successfully.</p>
                    </>
                )}

                {pageState === 'processing' && (
                    <>
                        <h1>Payment is processing</h1>
                        <p>Your payment was submitted, but confirmation is taking longer than expected.</p>
                    </>
                )}

                {pageState === 'failed' && (
                    <>
                        <h1>Unable to verify payment</h1>
                        <p>Please check My Bookings before attempting another payment.</p>
                    </>
                )}

                <div className="payment-result-actions">
                    <Link className="payment-primary-link" to="/my-bookings">
                        View My Bookings
                    </Link>
                    <Link to="/find-court">Find another court</Link>
                </div>
            </section>
        </main>
    )
}

export default PaymentSuccessPage