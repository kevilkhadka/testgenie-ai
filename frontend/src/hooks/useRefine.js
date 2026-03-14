import { useState } from 'react'

export function useRefine() {
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)

  async function refine(originalContext, existingCases, feedback, mode, exportFormat) {
    setLoading(true)
    setError(null)

    try {
      // Convert existing test cases object to JSON string
      // so the backend can send it back to the AI as context
      const existingCasesJson = JSON.stringify(existingCases)

      const response = await fetch('/api/refine', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          originalContext,
          existingCasesJson,
          feedback,
          mode,
          exportFormat,
        }),
      })

      const data = await response.json()

      if (!response.ok || !data.success) {
        throw new Error(data.errorMessage || 'Failed to refine. Please try again.')
      }

      return data

    } catch (err) {
      setError(err.message)
      return null
    } finally {
      setLoading(false)
    }
  }

  return { refine, loading, error }
}
