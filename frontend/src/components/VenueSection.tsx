import VenueCard from './VenueCard'
import type { Venue } from '../types/venues'
import { useState } from 'react'
import AvailabilityPanel from './AvailabilityPanel'
import sungaiBulohCourtImage from "../assets/venues/sungai-buloh-court.png"

const venues: Venue[] = [{
    id: 1,
    name: 'CourtFlow Sungai Buloh',
    address: 'Jalan Industri, Sungai Buloh',
    sports: ['Badminton', 'Pickleball'],
    pricePerHour: 18,
    isOpen: true,
    imageUrl: sungaiBulohCourtImage,
}, {
    id: 2,
    name: 'CourtFlow Kota Damansara',
    address: 'Jalan PJU 5/9, Kota Damansara',
    sports: ['Badminton'],
    pricePerHour: 22,
    isOpen: true,
    imageUrl: sungaiBulohCourtImage,
}, {
    id: 3,
    name: 'CourtFlow Desa ParkCity',
    address: 'Persiaran Residen, Desa ParkCity',
    sports: ['Badminton', 'Pickleball'],
    pricePerHour: 20,
    isOpen: false,
    imageUrl: sungaiBulohCourtImage,
},]

function VenueSection() {
    const [selectedVenue, setSelectedVenue] = useState<Venue | null>(null)
    function handleViewTimes(venue: Venue) { setSelectedVenue(venue) }

    return (
        <section className="venue-section">
            <div className="venue-section-heading">
                <div>
                    <h2>Nearby locations</h2>
                    <p>{venues.length} venues near Sungai Buloh</p>
                </div>

                <button type="button">
                    View all locations →
                </button>
            </div>

            <div className="venue-grid">
                {venues.map((venue) => (
                    <VenueCard key={venue.id} venue={venue} onViewTimes={handleViewTimes}
                    />
                ))}
            </div>

            {selectedVenue && (
                <>
                    <AvailabilityPanel
                        key={selectedVenue.id}
                        venue={selectedVenue}
                    />
                </>
            )}
        </section>
    )
}
export default VenueSection