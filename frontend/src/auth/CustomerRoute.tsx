import {Navigate, Outlet, useLocation} from 'react-router'
import {useAuth} from './AuthContext'

function CustomerRoute(){
    const{ user, isAuthenticated, isRestoringSession, } = useAuth()
    const location = useLocation()

    if(isRestoringSession){
        return(
            <main className="route-loading">
                <p>Checking your session...</p>
            </main>
        )
    }

    if(!isAuthenticated){
        return(
            <Navigate
                to="/login"
                replace
                state={{returnTo: location.pathname}}
            />
        )
    }

    if(user?.role !== 'CUSTOMER'){
        return <Navigate to="/" replace/>
    }

    return <Outlet/>
}

export default CustomerRoute