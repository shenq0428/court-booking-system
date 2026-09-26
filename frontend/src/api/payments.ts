import type { ApiErrorResponse,PaymentCheckoutRequest,PaymentCheckoutResponse,PaymentStatusResponse} from '../types/payments'

const API_BASE_URL = 'http://localhost:8080'

export async function createPaymentCheckout(
    bookingId: number,
    accessToken: string,
): Promise<PaymentCheckoutResponse> {
    const requestBody: PaymentCheckoutRequest = {
        bookingId,
    }

    const response = await fetch(
        `${API_BASE_URL}/api/payments/checkout`,
        {
            method: 'POST',
            headers: {
                Authorization: `Bearer ${accessToken}`,
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(requestBody),
        },
    )

    if (!response.ok) {
        let message = 'Unable to start payment.'

        try {
            const errorData =
                await response.json() as ApiErrorResponse

            if (errorData.message) {
                message = errorData.message
            }
        } catch {
            // Response did not contain JSON.
        }

        throw new Error(message)
    }

    return await response.json() as PaymentCheckoutResponse
}

export async function getPaymentStatus(
    sessionId: string,
    accessToken: string,
): Promise<PaymentStatusResponse> {
    const url = new URL(
        'http://localhost:8080/api/payments/checkout-status',
    )

    url.searchParams.set('sessionId', sessionId)

    const response = await fetch(url, {
        headers: {
            Authorization: `Bearer ${accessToken}`,
        },
    })

    if (!response.ok) {
        throw new Error('Unable to verify payment status.')
    }

    return await response.json() as PaymentStatusResponse
}