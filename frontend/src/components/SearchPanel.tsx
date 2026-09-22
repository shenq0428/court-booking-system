import { useState, } from 'react'
import type { Sport } from '../types/venues'
import { useNavigate,}from 'react-router'
import type{SubmitEvent,}from 'react'


const sports: Sport[] = [
  'Badminton',
  'Pickleball',
]


function SearchPanel() {
    
    const [location, setLocation] = useState('Sungai Buloh')
    const [sport, setSport] = useState<Sport>('Badminton')
    const [date, setDate] = useState('2026-09-06')
    const navigate = useNavigate()

    function handleSearch(
    event: SubmitEvent<HTMLFormElement>,
) {
    event.preventDefault()

    const params = new URLSearchParams()

    if (location.trim()) {
        params.set( 'location', location.trim(), )
    }

    if (sport === 'Badminton') {
        params.set( 'sport', 'BADMINTON',)
    }

    if (sport === 'Pickleball') {
        params.set( 'sport','PICKLEBALL',)
    }

    if (date) {
        params.set(
            'date',
            date,
        )
    }

    params.set('page', '0')
    params.set('size', '9')

    navigate(
        `/find-court?${params.toString()}`,
    )
}

    return (<section id="find-court" className="search-section">
        <div className="search-content">
            <p className="search-eyebrow">
                Badminton & Pickleball
            </p>

            <h1 className="search-title">
                Book your next game
            </h1>

            <p className="search-description">
                Choose a location, pick a time, and get on court.
            </p>

            <form
                className="search-form"
                onSubmit={handleSearch}
            >
                <label className="search-field">
                    <span>Location</span>
                    <select value={location} onChange={(event) => setLocation(event.target.value)}>
                        <option value="Sungai Buloh">
                            Sungai Buloh
                        </option>
                        <option value="Kota Damansara">
                            Kota Damansara
                        </option>
                        <option value="Desa Park City">
                            Desa Park City
                        </option>
                    </select>

                </label>

                <label className="search-field">
                    <span>Sport</span>

                    <select value={sport} onChange={(event) => setSport(event.target.value as Sport)}>
                        {sports.map((sportOption) => (
                            <option
                                key={sportOption}
                                value={sportOption}
                            >
                                {sportOption}
                            </option>
                        ))}
                    </select>
                </label>

                <label className="search-field">
                    <span>Date</span>

                    <input
                        type="date"
                        value={date}
                        onChange={(event) =>
                            setDate(event.target.value)
                        }
                    />

                </label>

                <button className="search-button" type="submit">
                    Search Courts
                </button>
            </form>


        </div>
    </section>
    )
}
export default SearchPanel;