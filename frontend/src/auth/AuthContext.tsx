import { createContext, useContext, useEffect, useRef, useState, type ReactNode, } from 'react'
import type { AuthUser, LoginResponse } from '../types/auth'

type AuthContextValue = {
    user: AuthUser | null
    accessToken: string | null
    isAuthenticated: boolean
    startSession: (loginResponse: LoginResponse,) => void
    clearSession: () => void
}

type AuthProviderProps = { children: ReactNode }

const AuthContext = createContext<AuthContextValue | undefined>(undefined,)

export function AuthProvider({ children, }: AuthProviderProps) {
    const [user, setUser] = useState<AuthUser | null>(null)
    const [accessToken, setAccessToken] = useState<string | null>(null)

    const hasTriedToRestoreSession = useRef(false)
    useEffect(() => {
        if (hasTriedToRestoreSession.current) {
            return
        }
        hasTriedToRestoreSession.current = true

        async function restoreSession() {
            try {
                const response = await fetch(
                    'http://localhost:8080/api/auth/refresh',
                    {
                        method: 'POST',
                        credentials: 'include',
                    },
                )

                if (!response.ok) { return }

                const refreshData = await response.json() as LoginResponse

                setAccessToken(refreshData.accessToken,)

                setUser({
                    id: refreshData.userId,
                    name: refreshData.name,
                    email: refreshData.email,
                    role: refreshData.role,
                })
            } catch {
                // Server unavailable:
                // remain logged out for now.
            }
        }
        void restoreSession()
    }, [])


    function startSession(loginResponse: LoginResponse,) {
        setAccessToken(loginResponse.accessToken,)

        setUser({
            id: loginResponse.userId,
            name: loginResponse.name,
            email: loginResponse.email,
            role: loginResponse.role,
        })
    }

    function clearSession() {
        setAccessToken(null)
        setUser(null)
    }

    const isAuthenticated = accessToken !== null

    return (
        <AuthContext.Provider
            value={{
                user,
                accessToken,
                isAuthenticated,
                startSession,
                clearSession,
            }}
        >
            {children}
        </AuthContext.Provider>
    )
}

export function useAuth() {
    const context = useContext(AuthContext)

    if (context === undefined) {
        throw new Error('useAuth must be used inside AuthProvider',)
    }

    return context
}