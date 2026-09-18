export type UserRole = 
|'CUSTOMER'
|'ADMIN'

export type AuthUser = {
    id:number
    name:string
    email:string
    role:UserRole
}

export type LoginResponse = {
    accessToken: string
    tokenType: string
    expiresInSeconds: number
    userId: number
    name: string
    email: string
    role: UserRole
}