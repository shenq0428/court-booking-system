import { useEffect, useState,} from 'react'
import {Link, useParams,} from 'react-router'
import type {CourtResponse,VenueDetailsResponse,} from '../types/venues'
import fallbackVenueImage from '../assets/venues/sungai-buloh-court.png'

function VenueDetailsPage() {
    const { venueId } = useParams()
    const [venue, setVenue] = useState<VenueDetailsResponse | null>(null)
    const [courts, setCourts] = useState<CourtResponse[]>([])
    const [isLoading, setIsLoading] = useState(true)
    const [error, setError] = useState<string | null>(null)

    useEffect(() => {
        async function loadVenueDetails() {
            if (!venueId) {
                setError('Invalid venue ID.')
                setIsLoading(false)
                return
            }

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
                     !venueResponse.ok|| !courtsResponse.ok
                ) {
                    throw new Error(
                        'Failed to load venue details.',
                    )
                }

                const venueData = await venueResponse.json() as VenueDetailsResponse

                const courtsData = await courtsResponse.json()as CourtResponse[]

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
        venue.latitude !== null
        && venue.longitude !== null
            ? `https://www.google.com/maps/search/?api=1&query=${venue.latitude},${venue.longitude}`
            : null

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

                    <button type="button">
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
                            No active courts are currently available.
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
        </main>
    )
}

export default VenueDetailsPage