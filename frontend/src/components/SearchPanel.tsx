import {useState, type SubmitEvent} from 'react'

type Sport = 'Badminton' | 'Pickleball'

const sports: Sport[] = [
    'Badminton',
    'Pickleball',
]


function SearchPanel(){
//useState
const [location,setLocation] = useState('Sungai Buloh')
const [sport,setSport] = useState<Sport>('Badminton')
const [date,setDate] = useState('2026-09-06')
const [hasSearched,setHasSearched] = useState(false)

function handleSearch(event: SubmitEvent<HTMLFormElement>){
    event.preventDefault()
    setHasSearched(true)
}

    return(<section id="find-court" className="search-section">
        <div className="search-content">
            <p className ="search-eyebrow">
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
                    <select value={location} onChange={(event)=>setLocation(event.target.value)}>
                        <option value="Sungai Buloh">
                            Sungai Buloh
                        </option>
                        <option value = "Kota Damansara">
                            Kota Damansara
                        </option>
                        <option value = "Desa Park City">
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
                    onChange={(event)=>
                        setDate(event.target.value)
                    }
                    />

                </label>

                <button className="search-button" type="submit">
                    Search Courts
                </button>
            </form>

            {hasSearched&&(
                <div className="search-result">
                    <strong>
                        Searching for {sport} courts
                    </strong>
                    <span>
                        {location} - {date}
                    </span>
                    </div>
            )}
        </div>
    </section>
    )
}
export default SearchPanel;