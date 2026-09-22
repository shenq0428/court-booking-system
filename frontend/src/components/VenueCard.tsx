import type { Venue } from '../types/venues'
import { Link } from 'react-router'

type VenueCardProps = {
    venue: Venue
    onViewTimes:(venue:Venue)=>void
}

function VenueCard({ venue,onViewTimes, }: VenueCardProps) {
    return (
        <article className="venue-card">
            <Link
                className="venue-cover venue-cover-link"
                to={`/venues/${venue.id}`}
                aria-label={`View ${venue.name}`}
            >
                <img
                    className="venue-cover-image"
                    src={venue.imageUrl}
                    alt={`${venue.name} indoor courts`}
                />

                <div
                    className="venue-cover-overlay"
                    aria-hidden="true"
                />

                <span className="venue-cover-text">
                    Indoor courts
                </span>

                <span
                    className={
                        venue.isOpen
                            ? 'venue-status open'
                            : 'venue-status closed'
                    }
                >
                    {venue.isOpen
                        ? 'Available'
                        : 'Unavailable'}
                </span>
            </Link>

            <div className="venue-card-content">
                <h3>{venue.name}</h3>

                <p className="venue-address">
                    {venue.address}
                </p>

                <div className="venue-sports">
                    {venue.sports.map((sport) => (
                        <span key={sport}>
                            {sport}
                        </span>
                    ))}
                </div>

                <div className="venue-card-footer">
                    <p>
                        {venue.pricePerHour > 0 ? (
                            <>
                                From{' '}
                                <strong>
                                    RM {venue.pricePerHour}/hour
                                </strong>
                            </>
                        ) : (
                            <span>Pricing unavailable</span>
                        )}
                    </p>

                    <div className="venue-card-actions">
                        <Link
                            className="venue-view-button"
                            to={`/venues/${venue.id}`}
                        >
                            View
                        </Link>

                        <button
                            className="venue-book-button"
                            type="button"
                            disabled={!venue.isOpen}
                            onClick={() =>
                                onViewTimes(venue)
                            }
                        >
                            Book Now
                        </button>
                    </div>
                </div>
            </div>
        </article>
    )
}

export default VenueCard