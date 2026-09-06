import type { Venue } from '../types/venues'

type VenueCardProps = {
    venue: Venue
}

function VenueCard({ venue }: VenueCardProps) {
    return (
        <article className="venue-card">
            <div className="venue-cover">
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
                <p className="venue-adddress">
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
                    <button type="button">
                        View Times
                    </button>
                </div>
            </div>
        </article>
    )
}
export default VenueCard