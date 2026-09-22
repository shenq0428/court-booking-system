import {useEffect, useState,} from 'react'
import {useNavigate,} from 'react-router'
import VenueCard from './VenueCard'
import type {ApiSportType,Sport,Venue, VenuePageResponse,VenueSummaryResponse,} from '../types/venues'
import sungaiBulohCourtImage from '../assets/venues/sungai-buloh-court.png'

function convertSport(
    sport: ApiSportType,
): Sport {
    if (sport === 'BADMINTON') {
        return 'Badminton'
    }

    return 'Pickleball'
}

function convertApiVenue(
    apiVenue: VenueSummaryResponse,
): Venue {
    return {
        id: apiVenue.id,
        name: apiVenue.name,
        address: apiVenue.address,

        sports: apiVenue.sports.map(
            convertSport,
        ),

        pricePerHour:
            apiVenue.startingPricePerHour
            ?? 0,

        isOpen:
            apiVenue.startingPricePerHour
            !== null,

        imageUrl:
            apiVenue.imageUrl
            ?? sungaiBulohCourtImage,
    }
}

function VenueSection() {
    const [venues, setVenues] =useState<Venue[]>([])

    const [totalVenues, setTotalVenues] = useState(0)

    const [isLoading, setIsLoading] = useState(true)

    const [error, setError] = useState<string | null>(null)

    const navigate = useNavigate()

    useEffect(() => {
        async function loadVenues() {
            try {
                setError(null)

                const response = await fetch(
                    'http://localhost:8080/api/venues?page=0&size=6',
                )

                if (!response.ok) {
                    throw new Error('Failed to load venues.', )
                }

                const venuePage =(await response.json()) as VenuePageResponse

                const convertedVenues = venuePage.content.map( convertApiVenue,)

                setVenues(convertedVenues)

                setTotalVenues( venuePage.totalElements,)
            } catch {
                setError(
                    'Cannot load venues. Please try again.',
                )
            } finally {
                setIsLoading(false)
            }
        }

        void loadVenues()
    }, [])

    function handleViewTimes(venue: Venue) {
        navigate(`/venues/${venue.id}`)
    }

    return (
        <section className="venue-section">
            <div className="venue-section-heading">
                <div>
                    <h2>Nearby locations</h2>

                    <p>
                        {totalVenues} venues near
                        Sungai Buloh
                    </p>
                </div>

                <button type="button">
                    View all locations →
                </button>
            </div>

            {isLoading && (
                <p>Loading venues...</p>
            )}

            {error && (
                <p role="alert">
                    {error}
                </p>
            )}

            {!isLoading && !error && (
                <div className="venue-grid">
                    {venues.map((venue) => (
                        <VenueCard
                            key={venue.id}
                            venue={venue}
                            onViewTimes={
                                handleViewTimes
                            }
                        />
                    ))}
                </div>
            )}

        </section>
    )
}

export default VenueSection