import { useRef, useState } from 'react'
import { Link, NavLink, } from 'react-router'
import {useAuth,} from '../auth/AuthContext'

function Header() {
    const auth = useAuth()
    const logoutDialog = useRef<HTMLDialogElement>(null)
    const cancelLogoutButton = useRef<HTMLButtonElement>(null)
    const [isLoggingOut, setIsLoggingOut] = useState(false)
    const [logoutError, setLogoutError] = useState<string | null>(null)

    function openLogoutDialog() {
        setLogoutError(null)
        logoutDialog.current?.showModal()
        cancelLogoutButton.current?.focus()
    }

    async function handleLogout(){
        if (isLoggingOut) return
        setIsLoggingOut(true)
        setLogoutError(null)
        try{
            const response = await fetch('http://localhost:8080/api/auth/logout',
                {
                    method:'POST',
                    credentials:'include',
                },
            )
            
            if(!response.ok){
                throw new Error('Logout request failed',)
            }
            logoutDialog.current?.close()
            auth.clearSession()
        }catch{
            setLogoutError('Unable to log out. Please try again.')
        } finally {
            setIsLoggingOut(false)
        }
    }

    return (
        <header className="site-header">
            <div className="header-inner">
                <Link
                    className="brand"
                    to="/"
                    aria-label="CourtFlow home"
                >
                    Court<span>Flow</span>
                </Link>

                <nav
                    className="main-nav"
                    aria-label="Main navigation"
                >
                    <NavLink to="/find-court"
                        className={({ isActive }) => isActive ? 'active' : undefined}
                    >
                        Find a Court
                    </NavLink>

                    <Link
                        to="/my-bookings"
                    >
                        My bookings
                    </Link>
                </nav>

                {!auth.isAuthenticated?(<div className="header-auth-actions">
                    <Link
                        className="header-login-link"
                        to="/login"
                    >
                        Log in
                    </Link>

                    <Link
                        className="sign-in-button"
                        to="/register"
                    >
                        Register
                    </Link>
                </div>):(
                    <div className="header-user-actions">
                        <span>{auth.user?auth.user.name:"My account"}</span>

                        <button className="logout-button" type="button" onClick={openLogoutDialog}>
                            Log out
                        </button>
                    </div>
                )}
            </div>
            <dialog
                ref={logoutDialog}
                className="logout-dialog"
                aria-labelledby="logout-dialog-title"
                aria-describedby="logout-dialog-description"
                onCancel={(event) => {
                    if (isLoggingOut) event.preventDefault()
                }}
            >
                <h2 id="logout-dialog-title">Log out?</h2>
                <p id="logout-dialog-description">Are you sure you want to log out?</p>
                {logoutError && <p className="logout-error" role="alert">{logoutError}</p>}
                <div className="logout-dialog-actions">
                    <button
                        ref={cancelLogoutButton}
                        className="logout-cancel-button"
                        type="button"
                        disabled={isLoggingOut}
                        onClick={() => logoutDialog.current?.close()}
                    >
                        No
                    </button>
                    <button
                        className="logout-confirm-button"
                        type="button"
                        disabled={isLoggingOut}
                        onClick={handleLogout}
                    >
                        {isLoggingOut ? 'Logging out…' : 'Yes'}
                    </button>
                </div>
            </dialog>
        </header>
    )
}

export default Header
