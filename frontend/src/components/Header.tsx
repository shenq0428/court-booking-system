import { Link, NavLink, } from 'react-router'

function Header() {
    const isAuthenticated = false

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
                    <NavLink to="/#find-court"
                        className={({ isActive }) =>
                            isActive ? 'active' : undefined
                        }
                    >
                        Find a Court
                    </NavLink>

                    <Link
                        to="/#find-court"
                        state={{
                            returnTo: '/my-bookings',
                        }}
                    >
                        My bookings
                    </Link>
                </nav>

                {!isAuthenticated?(<div className="header-auth-actions">
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
                        <span>My account</span>

                        <button type="button">
                            Log out
                        </button>
                    </div>
                )}
            </div>
        </header>
    )
}

export default Header