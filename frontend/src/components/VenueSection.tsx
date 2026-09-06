import VenueCard from './VenueCard'
import type { Venue } from '../types/venues'

const sungaiBulohVenue: Venue = {
    id:1,
    name:'CourtFlow Sungai Buloh',
    address:'Jalan Industri, Sungai Buloh',
    sports:['Badminton','Pickleball'],
    pricePerHour:18,
    isOpen:true,
}

function VenueSection(){
    return (
        <section className="venue-section">
            <div className="venue-section-heading">
                <div>
                    <h2>Nearby locations</h2>
                    <p>Venues near Sungai Buloh</p>
                </div>

                <button type="button">
                    View all locations →
                </button>
            </div>

            <div className="venue-grid">
                <VenueCard venue={sungaiBulohVenue}/>
            </div>
        </section>
    )
}
export default VenueSection