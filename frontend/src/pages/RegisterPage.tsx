import {
    useState,
    type SubmitEvent,
} from 'react'
import {
    Eye,
    EyeOff,
} from 'lucide-react'
import { Link } from 'react-router'
import { FaFacebook } from 'react-icons/fa'
import { FcGoogle } from 'react-icons/fc'

type SocialProvider =
    | 'Facebook'
    | 'Google'

function RegisterPage() {
    const [name, setName] = useState('')
    const [email, setEmail] = useState('')
    const [password, setPassword] = useState('')
    const [confirmPassword, setConfirmPassword] = useState('')

    const [showPassword, setShowPassword] = useState(false)

    const [error, setError] = useState<string | null>(null)

    const [message, setMessage] = useState<string | null>(null)

    function handleRegisterSubmit(
        event: SubmitEvent<HTMLFormElement>,
    ) {
        event.preventDefault()

        if (password !== confirmPassword) {
            setMessage(null)
            setError('Passwords do not match.')
            return
        }

        setError(null)
        setMessage('The registration API is not connected yet.',)
    }

    function handleSocialRegistration(
        provider: SocialProvider,
    ) {
        setError(null)
        setMessage( `${provider} registration is not connected yet.`,)
    }

    return (
        <main className="auth-page">
            <section className="auth-card">
                <Link
                    className="auth-back-link"
                    to="/"
                >
                    ← Back to CourtFlow
                </Link>

                <div className="auth-heading">
                    <p>Join CourtFlow</p>

                    <h1>Sign Up</h1>

                    <span>
                        Already have a CourtFlow account?{' '}

                        <Link to="/login">
                            Log In
                        </Link>
                    </span>
                </div>

                <form
                    className="auth-form"
                    onSubmit={handleRegisterSubmit}
                >
                    <label className="auth-field">
                        <span>Full name</span>

                        <input
                            type="text"
                            value={name}
                            onChange={(event) =>
                                setName(event.target.value)
                            }
                            placeholder="Enter your full name"
                            autoComplete="name"
                            required
                        />
                    </label>

                    <label className="auth-field">
                        <span>Email address</span>

                        <input
                            type="email"
                            value={email}
                            onChange={(event) =>
                                setEmail(event.target.value)
                            }
                            placeholder="you@example.com"
                            autoComplete="email"
                            required
                        />
                    </label>

                    <label className="auth-field">
                        <span>Password</span>

                        <div className="password-input-wrapper">
                            <input
                                type={
                                    showPassword
                                        ? 'text'
                                        : 'password'
                                }
                                value={password}
                                onChange={(event) =>
                                    setPassword(event.target.value)
                                }
                                placeholder="At least 8 characters"
                                autoComplete="new-password"
                                minLength={8}
                                required
                            />

                            <button
                                className="password-toggle"
                                type="button"
                                aria-label={
                                    showPassword
                                        ? 'Hide password'
                                        : 'Show password'
                                }
                                aria-pressed={showPassword}
                                onClick={() =>
                                    setShowPassword(
                                        (currentValue) =>
                                            !currentValue,
                                    )
                                }
                            >
                                {showPassword ? (
                                    <EyeOff
                                        size={19}
                                        aria-hidden="true"
                                    />
                                ) : (
                                    <Eye
                                        size={19}
                                        aria-hidden="true"
                                    />
                                )}
                            </button>
                        </div>
                    </label>

                    <label className="auth-field">
                        <span>Confirm password</span>

                        <input
                            type={
                                showPassword
                                    ? 'text'
                                    : 'password'
                            }
                            value={confirmPassword}
                            onChange={(event) =>
                                setConfirmPassword(
                                    event.target.value,
                                )
                            }
                            placeholder="Enter your password again"
                            autoComplete="new-password"
                            minLength={8}
                            required
                        />
                    </label>

                    <button
                        className="auth-submit-button"
                        type="submit"
                    >
                        Create Account
                    </button>
                </form>

                <p className="auth-terms">
                    By signing up, you agree to the CourtFlow
                    Terms of Use and Privacy Policy.
                </p>

                {error && (
                    <div
                        className="auth-error"
                        role="alert"
                    >
                        {error}
                    </div>
                )}

                {message && (
                    <div
                        className="auth-message"
                        role="status"
                    >
                        {message}
                    </div>
                )}

                <div className="auth-divider">
                    <span aria-hidden="true" />
                    <span>or</span>
                    <span aria-hidden="true" />
                </div>

                <div className="social-auth-actions">
                    <button
                        className="social-auth-button"
                        type="button"
                        onClick={() =>
                            handleSocialRegistration('Facebook')
                        }
                    >
                        <FaFacebook
                            className="facebook-brand-icon"
                            size={21}
                            aria-hidden="true"
                        />

                        Continue with Facebook
                    </button>

                    <button
                        className="social-auth-button"
                        type="button"
                        onClick={() =>
                            handleSocialRegistration('Google')
                        }
                    >
                        <FcGoogle
                            size={21}
                            aria-hidden="true"
                        />

                        Continue with Google
                    </button>
                </div>
            </section>
        </main>
    )
}

export default RegisterPage