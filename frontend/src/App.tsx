import "./App.css"
import Header from './components/Header'
import SearchPanel from './components/SearchPanel'
import VenueSection from './components/VenueSection'
import { Route, Routes } from 'react-router'
import LoginPage from './pages/LoginPage'
import RegisterPage from './pages/RegisterPage'
import VenueDetailsPage from './pages/VenueDetailsPage'
import FindCourtPage from './pages/FindCourtPage'
import MyBookingsPage from './pages/MyBookingsPage'

function App() {
  return (
    <div className="app">
      <Header />
      <Routes>
        <Route path="/"
          element={
            <>
              <SearchPanel />
              <VenueSection />
            </>
          } />
        <Route path="/login"
          element={<LoginPage />}
        />

        <Route path="/register"
          element={<RegisterPage />}
        />

        <Route path="/venues/:venueId"
          element={<VenueDetailsPage />}
        />

        <Route
          path="/find-court"
          element={<FindCourtPage />}
        />

        <Route
         path="/my-bookings"
         element={<MyBookingsPage/>}
         />
      </Routes>
    </div>
  )
}

export default App