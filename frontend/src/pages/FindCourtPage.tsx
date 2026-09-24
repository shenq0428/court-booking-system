import {useEffect,useState,type SubmitEvent} from 'react'
import {useNavigate,useSearchParams,} from 'react-router'
import VenueCard from '../components/VenueCard'
import './FindCourtPage.css'

import type {
    ApiSportType,
    Sport,
    Venue,
    VenuePageResponse,
} from '../types/venues'

import fallbackVenueImage
    from '../assets/venues/sungai-buloh-court.png'

function convertSport(
    sport: ApiSportType,
): Sport {
    if (sport === 'BADMINTON') {
        return 'Badminton'
    }

    return 'Pickleball'
}

function FindCourtPage() {
    const navigate = useNavigate()

    const [
        searchParams,
        setSearchParams,
    ] = useSearchParams()

    /*
     * These values come from the URL.
     *
     * Example:
     * /find-court?location=Selangor&sport=BADMINTON&page=0
     */
    const locationFromUrl =
        searchParams.get('location') ?? ''

    const sportFromUrl =
        searchParams.get('sport')

    const dateFromUrl =
        searchParams.get('date') ?? ''

    const pageFromUrl =
        Number(searchParams.get('page') ?? '0')

    const currentPage =
        Number.isInteger(pageFromUrl) &&
        pageFromUrl >= 0
            ? pageFromUrl
            : 0

    const selectedSport: ApiSportType | '' =
        sportFromUrl === 'BADMINTON' ||
        sportFromUrl === 'PICKLEBALL'
            ? sportFromUrl
            : ''

    /*
     * These states control the input fields.
     */
    const [locationInput, setLocationInput] =
        useState(locationFromUrl)

    const [sportInput, setSportInput] =
        useState<ApiSportType | ''>(
            selectedSport,
        )

    const [dateInput, setDateInput] =
        useState(dateFromUrl)

    /*
     * These states hold the API result.
     */
    const [venues, setVenues] =        useState<Venue[]>([])

    const [totalElements, setTotalElements] =       useState(0)

    const [totalPages, setTotalPages] =      useState(0)

    const [isLoading, setIsLoading] = useState(true)

    const [error, setError] =       useState<string | null>(null)

    /*
     * Keep input fields synchronized when the URL changes,
     * for example when the browser Back button is used.
     */
    useEffect(() => {
        setLocationInput(locationFromUrl)
        setSportInput(selectedSport)
        setDateInput(dateFromUrl)
    }, [
        locationFromUrl,
        selectedSport,
        dateFromUrl,
    ])

    /*
     * Fetch search results whenever the URL search
     * conditions or page changes.
     */
    useEffect(() => {
        let ignoreResult = false

        async function loadVenues() {
            setIsLoading(true)
            setError(null)

            const apiParams =
                new URLSearchParams()

            if (locationFromUrl.trim()) {
                apiParams.set(
                    'location',
                    locationFromUrl.trim(),
                )
            }

            if (selectedSport) {
                apiParams.set(
                    'sport',
                    selectedSport,
                )
            }

            apiParams.set(
                'page',
                String(currentPage),
            )

            apiParams.set('size', '9')

            try {
                const response = await fetch(
                    `http://localhost:8080/api/venues/search?${apiParams.toString()}`,
                )

                if (!response.ok) {
                    throw new Error(
                        'Failed to search venues.',
                    )
                }

                const venuePage =
                    await response.json() as VenuePageResponse

                const convertedVenues: Venue[] =
                    venuePage.content.map(
                        (apiVenue) => ({
                            id: apiVenue.id,
                            name: apiVenue.name,
                            address:
                                apiVenue.address,

                            sports:
                                apiVenue.sports.map(
                                    convertSport,
                                ),

                            pricePerHour:
                                apiVenue
                                    .startingPricePerHour
                                ?? 0,

                            /*
                             * The Search API only returns
                             * venues with active courts.
                             */
                            isOpen:
                                apiVenue
                                    .startingPricePerHour
                                !== null,

                            imageUrl:
                                apiVenue.imageUrl
                                ?? fallbackVenueImage,
                        }),
                    )

                if (ignoreResult) {
                    return
                }

                setVenues(convertedVenues)

                setTotalElements(
                    venuePage.totalElements,
                )

                setTotalPages(
                    venuePage.totalPages,
                )
            } catch {
                if (ignoreResult) {
                    return
                }

                setVenues([])
                setTotalElements(0)
                setTotalPages(0)

                setError(
                    'Cannot load venues. Please try again.',
                )
            } finally {
                if (!ignoreResult) {
                    setIsLoading(false)
                }
            }
        }

        void loadVenues()

        return () => {
            /*
             * If another search starts before this one
             * finishes, ignore the older response.
             */
            ignoreResult = true
        }
    }, [
        locationFromUrl,
        selectedSport,
        currentPage,
    ])

    function handleSearch(
        event: SubmitEvent<HTMLFormElement>,
    ) {
        event.preventDefault()

        const nextParams =
            new URLSearchParams()

        if (locationInput.trim()) {
            nextParams.set(
                'location',
                locationInput.trim(),
            )
        }

        if (sportInput) {
            nextParams.set(
                'sport',
                sportInput,
            )
        }

        /*
         * We keep the date in the URL, but the backend
         * does not filter availability by date yet.
         */
        if (dateInput) {
            nextParams.set(
                'date',
                dateInput,
            )
        }

        /*
         * A new search must return to the first page.
         */
        nextParams.set('page', '0')
        nextParams.set('size', '9')

        setSearchParams(nextParams)
    }

    function changePage(nextPage: number) {
        if (
            nextPage < 0 ||
            nextPage >= totalPages
        ) {
            return
        }

        const nextParams =
            new URLSearchParams(searchParams)

        nextParams.set(
            'page',
            String(nextPage),
        )

        nextParams.set('size', '9')

        setSearchParams(nextParams)

        window.scrollTo({
            top: 0,
            behavior: 'smooth',
        })
    }

    function handleViewVenue(
        venue: Venue,
    ) {
        navigate(`/venues/${venue.id}`)
    }

    return (
        <main className="find-court-page">
            <section className="find-court-hero">
                <h1>Find a Court</h1>

                <p>
                    Search badminton and pickleball
                    courts near you.
                </p>

                <form
                    className="find-court-search"
                    onSubmit={handleSearch}
                >
                    <label>
                        <span>Location</span>

                        <input
                            type="text"
                            value={locationInput}
                            onChange={(event) =>
                                setLocationInput(
                                    event.target.value,
                                )
                            }
                            placeholder="Sungai Buloh, Selangor..."
                        />
                    </label>

                    <label>
                        <span>Sport</span>

                        <select
                            value={sportInput}
                            onChange={(event) =>
                                setSportInput(
                                    event.target.value as ApiSportType | '',
                                )
                            }
                        >
                            <option value="">
                                All sports
                            </option>

                            <option value="BADMINTON">
                                Badminton
                            </option>

                            <option value="PICKLEBALL">
                                Pickleball
                            </option>
                        </select>
                    </label>

                    <label>
                        <span>Date</span>

                        <input
                            type="date"
                            value={dateInput}
                            onChange={(event) =>
                                setDateInput(
                                    event.target.value,
                                )
                            }
                        />
                    </label>

                    <button type="submit">
                        Search Courts
                    </button>
                </form>
            </section>

            <section className="find-court-results">
                <div className="find-court-heading">
                    <div>
                        <h2>Available Venues</h2>

                        {!isLoading && !error && (
                            <p>
                                {totalElements}{' '}
                                venues found
                            </p>
                        )}
                    </div>
                </div>

                {isLoading && (
                    <p>Searching venues...</p>
                )}

                {error && (
                    <p role="alert">
                        {error}
                    </p>
                )}

                {!isLoading &&
                    !error &&
                    venues.length === 0 && (
                        <div className="empty-search-result">
                            <h2>
                                No venues found
                            </h2>

                            <p>
                                Try another location
                                or sport.
                            </p>
                        </div>
                    )}

                {!isLoading &&
                    !error &&
                    venues.length > 0 && (
                        <>
                            <div className="venue-grid">
                                {venues.map(
                                    (venue) => (
                                        <VenueCard
                                            key={venue.id}
                                            venue={venue}
                                            onViewTimes={
                                                handleViewVenue
                                            }
                                        />
                                    ),
                                )}
                            </div>

                            <div className="pagination">
                                <button
                                    type="button"
                                    disabled={
                                        currentPage === 0
                                    }
                                    onClick={() =>
                                        changePage(
                                            currentPage - 1,
                                        )
                                    }
                                >
                                    ← Previous
                                </button>

                                <span>
                                    Page{' '}
                                    {currentPage + 1}{' '}
                                    of {totalPages}
                                </span>

                                <button
                                    type="button"
                                    disabled={
                                        currentPage + 1 >=
                                        totalPages
                                    }
                                    onClick={() =>
                                        changePage(
                                            currentPage + 1,
                                        )
                                    }
                                >
                                    Next →
                                </button>
                            </div>
                        </>
                    )}
            </section>
        </main>
    )
}

export default FindCourtPage