function Header() {
    return (
        <header className="site-header">
            <div className="header-inner">
                <a href="/" aria-label="CourtFlow Home">
                    Court<span>Flow</span>
                </a>


                <nav className="main-nav" aria-label="Main navigation">
                    <a className="active" href="#find-court">find a Court</a>
                    <a href="#my-bookings">My bookings</a>
                </nav>

                <button className="sign-in-button" type="button">Sign in</button>
            </div>
        </header>
    )
}
export default Header