import "./App.css"
import Header from './components/Header'
import SearchPanel from './components/SearchPanel'
import VenueSection from './components/VenueSection'
function App() {
  return (
    <div className="app">
      <Header />
      <SearchPanel />
      <VenueSection />
    </div>
  )
}

export default App