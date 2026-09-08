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

function LoginPage() {
    const [email, setEmail] = useState('')
    const [password, setPassword] = useState('')
    const [showPassword, setShowPassword] =
        useState(false)

    const [message, setMessage] =
        useState<string | null>(null)

    function handleLoginSubmit(
        event: SubmitEvent<HTMLFormElement>,
    ) {
        event.preventDefault()

        setMessage(
            'The login API is not connected yet.',
        )
    }

    function handleSocialLogin(
        provider: SocialProvider,
    ) {
        setMessage(
            `${provider} login is not connected yet.`,
        )
    }

    function handleForgotPassword() {
        setMessage(
            'The forgot password page is coming next.',
        )
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
                    <p>Welcome back</p>

                    <h1>Log In</h1>

                    <span>
                        Don&apos;t have a CourtFlow account?{' '}
                        <Link to="/register">
                            Register
                        </Link>
                    </span>
                </div>

                <form
                    className="auth-form"
                    onSubmit={handleLoginSubmit}
                >
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
                                placeholder="Enter your password"
                                autoComplete="current-password"
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

                    <div className="auth-form-options">
                        <button
                            className="auth-text-button"
                            type="button"
                            onClick={handleForgotPassword}
                        >
                            Forgot password?
                        </button>
                    </div>

                    <button
                        className="auth-submit-button"
                        type="submit"
                    >
                        Log In
                    </button>
                </form>

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
                            handleSocialLogin('Facebook')
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
                            handleSocialLogin('Google')
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

export default LoginPage