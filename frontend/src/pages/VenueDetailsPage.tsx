import { useEffect, useRef, useState } from 'react'
import { Link, useParams } from 'react-router'
import AvailabilityPanel from '../components/AvailabilityPanel'
import type { CourtResponse, Venue, VenueDetailsResponse,VenueSummaryResponse,} from '../types/venues'
import fallbackVenueImage from '../assets/venues/sungai-buloh-court.png'

function VenueDetailsPage() {
    const { venueId } = useParams()

    const [venue, setVenue] =
        useState<VenueDetailsResponse | null>(null)

    const [courts, setCourts] =
        useState<CourtResponse[]>([])

    const [nearbyVenues, setNearbyVenues] =
        useState<VenueSummaryResponse[]>([])

    const [isLoading, setIsLoading] =
        useState(true)

    const [error, setError] =
        useState<string | null>(null)

    const [showAvailability, setShowAvailability] =
        useState(false)

    const availabilityRef =
        useRef<HTMLDivElement | null>(null)

    useEffect(() => {
        async function loadVenueDetails() {
            if (!venueId) {
                setError('Invalid venue ID.')
                setIsLoading(false)
                return
            }

            setIsLoading(true)
            setError(null)
            setVenue(null)
            setCourts([])
            setShowAvailability(false)

            try {
                const [
                    venueResponse,
                    courtsResponse,
                ] = await Promise.all([
                    fetch(
                        `http://localhost:8080/api/venues/${venueId}`,
                    ),
                    fetch(
                        `http://localhost:8080/api/venues/${venueId}/courts`,
                    ),
                ])

                if (
                    !venueResponse.ok ||
                    !courtsResponse.ok
                ) {
                    throw new Error(
                        'Failed to load venue details.',
                    )
                }

                const venueData =                await venueResponse.json()   as VenueDetailsResponse

                const courtsData =   await courtsResponse.json() as CourtResponse[]

                setVenue(venueData)
                setCourts(courtsData)
            } catch {
                setError(
                    'Cannot load venue details. Please try again.',
                )
            } finally {
                setIsLoading(false)
            }
        }

        void loadVenueDetails()
    }, [venueId])

    useEffect(() => {
        if (!venueId) {
            return
        }

        async function loadNearbyVenues() {
            setNearbyVenues([])

            try {
                const response = await fetch(
                    `http://localhost:8080/api/venues/${venueId}/nearby?limit=3`,
                )

                if (!response.ok) {
                    return
                }

                const nearbyData =                await response.json()    as VenueSummaryResponse[]

                setNearbyVenues(nearbyData)
            } catch {
                // Nearby venues are optional.
            }
        }

        void loadNearbyVenues()
    }, [venueId])

    useEffect(() => {
        if (!showAvailability) {
            return
        }

        availabilityRef.current?.scrollIntoView({
            behavior: 'smooth',
            block: 'start',
        })
    }, [showAvailability])

    function handleViewAvailability() {
        if (showAvailability) {
            availabilityRef.current?.scrollIntoView({
                behavior: 'smooth',
                block: 'start',
            })
            return
        }

        setShowAvailability(true)
    }

    if (isLoading) {
        return (
            <main className="venue-details-page">
                <p>Loading venue details...</p>
            </main>
        )
    }

    if (error || !venue) {
        return (
            <main className="venue-details-page">
                <p role="alert">
                    {error ?? 'Venue not found.'}
                </p>

                <Link to="/">
                    Back to venues
                </Link>
            </main>
        )
    }

    const fullAddress = [
        venue.addressLine1,
        venue.addressLine2,
        venue.city,
        venue.state,
        venue.postalCode,
    ]
        .filter(Boolean)
        .join(', ')

    const googleMapsUrl =
        venue.latitude !== null &&
        venue.longitude !== null
            ? `https://www.google.com/maps/search/?api=1&query=${venue.latitude},${venue.longitude}`
            : null

    const venueSports: Venue['sports'] = []

    for (const court of courts) {
        const sport: Venue['sports'][number] =
            court.sport === 'BADMINTON'
                ? 'Badminton'
                : 'Pickleball'

        if (!venueSports.includes(sport)) {
            venueSports.push(sport)
        }
    }

    const courtPrices = courts.map(
        (court) => court.pricePerHour,
    )

    const startingPrice =
        courtPrices.length > 0
            ? Math.min(...courtPrices)
            : 0

    const availabilityVenue: Venue = {
        id: venue.id,
        name: venue.name,
        address: fullAddress,
        sports: venueSports,
        pricePerHour: startingPrice,
        isOpen: venue.active && courts.length > 0,
        imageUrl:
            venue.imageUrl ?? fallbackVenueImage,
    }

    return (
        <main className="venue-details-page">
            <Link
                className="venue-details-back"
                to="/"
            >
                ← Back to venues
            </Link>

            <section className="venue-details-hero">
                <img
                    src={
                        venue.imageUrl
                        ?? fallbackVenueImage
                    }
                    alt={venue.name}
                />

                <div>
                    <p className="venue-details-label">
                        Indoor sports venue
                    </p>

                    <h1>{venue.name}</h1>

                    <p>{fullAddress}</p>

                    <button
                        type="button"
                        onClick={handleViewAvailability}
                    >
                        View Availability
                    </button>
                </div>
            </section>

            <div className="venue-details-layout">
                <section className="venue-information-card">
                    <h2>About this venue</h2>

                    <p>
                        {venue.description
                            ?? 'Venue description coming soon.'}
                    </p>

                    {venue.phoneNumber && (
                        <p>
                            Phone: {venue.phoneNumber}
                        </p>
                    )}
                </section>

                <section className="venue-information-card">
                    <h2>Courts & Pricing</h2>

                    {courts.length === 0 ? (
                        <p>
                            No active courts are currently
                            available.
                        </p>
                    ) : (
                        <div className="court-details-grid">
                            {courts.map((court) => (
                                <article
                                    className="court-details-card"
                                    key={court.id}
                                >
                                    <h3>
                                        Court {court.courtNumber}
                                    </h3>

                                    <p>
                                        {court.sport === 'BADMINTON'
                                            ? 'Badminton'
                                            : 'Pickleball'}
                                    </p>

                                    <strong>
                                        RM {court.pricePerHour}/hour
                                    </strong>
                                </article>
                            ))}
                        </div>
                    )}
                </section>

                <section className="venue-information-card">
                    <h2>Venue Information</h2>

                    <p>
                        Parking information coming soon.
                    </p>

                    {googleMapsUrl && (
                        <a
                            href={googleMapsUrl}
                            target="_blank"
                            rel="noreferrer"
                        >
                            Open in Google Maps →
                        </a>
                    )}
                </section>
            </div>

            {showAvailability && (
                <div
                    className="venue-availability-section"
                    ref={availabilityRef}
                >
                    <AvailabilityPanel
                        venue={availabilityVenue}
                    />
                </div>
            )}

            {nearbyVenues.length > 0 && (
                <section className="nearby-venues-section">
                    <div className="nearby-venues-heading">
                        <div>
                            <p>Explore more</p>
                            <h2>Venues Nearby</h2>
                        </div>

                        <Link to="/">
                            View all venues →
                        </Link>
                    </div>

                    <div className="nearby-venues-grid">
                        {nearbyVenues.map(
                            (nearbyVenue) => (
                                <Link
                                    className="nearby-venue-card"
                                    key={nearbyVenue.id}
                                    to={`/venues/${nearbyVenue.id}`}
                                >
                                    <img
                                        src={
                                            nearbyVenue.imageUrl
                                            ?? fallbackVenueImage
                                        }
                                        alt={nearbyVenue.name}
                                    />

                                    <div className="nearby-venue-content">
                                        <div className="nearby-venue-sports">
                                            {nearbyVenue.sports.map(
                                                (sport) => (
                                                    <span key={sport}>
                                                        {sport ===
                                                        'BADMINTON'
                                                            ? 'Badminton'
                                                            : 'Pickleball'}
                                                    </span>
                                                ),
                                            )}
                                        </div>

                                        <h3>
                                            {nearbyVenue.name}
                                        </h3>

                                        <p>
                                            {nearbyVenue.address}
                                        </p>

                                        <div className="nearby-venue-footer">
                                            <strong>
                                                {nearbyVenue
                                                    .startingPricePerHour
                                                !== null
                                                    ? `From RM ${nearbyVenue.startingPricePerHour}/hour`
                                                    : 'Pricing unavailable'}
                                            </strong>

                                            <span>
                                                See venue →
                                            </span>
                                        </div>
                                    </div>
                                </Link>
                            ),
                        )}
                    </div>
                </section>
            )}
        </main>
    )
}

export default VenueDetailsPage