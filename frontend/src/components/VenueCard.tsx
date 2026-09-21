import type { Venue } from '../types/venues'

type VenueCardProps = {
    venue: Venue
    onViewTimes:(venue:Venue)=>void
}

function VenueCard({ venue,onViewTimes, }: VenueCardProps) {
    return (
        <article className="venue-card">
            <div className="venue-cover">
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

                <span className={venue.isOpen
                    ? 'venue-status open'
                    : 'venue-status closed'
                }>
                    {venue.isOpen ? 'Open now' : 'Closed'}
                </span>
            </div>

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
                    <p>From{' '} <strong> RM {venue.pricePerHour}/hour</strong></p>
                    <button type="button"
                        disabled={!venue.isOpen}
                            onClick={()=> onViewTimes(venue)}>
                        View Times
                    </button>
                </div>
            </div>
        </article>
    )
}
export default VenueCard