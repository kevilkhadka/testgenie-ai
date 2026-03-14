import { useState } from 'react'

export function useTestGenerator() {
  const [result, setResult] = useState(null)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)

  async function generate(featureDescription, exportFormat) {
    setLoading(true)
    setError(null)
    setResult(null)

    try {
      const response = await fetch('/api/generate', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ featureDescription, exportFormat }),
      })

      const data = await response.json()

      if (!response.ok || !data.success) {
        throw new Error(data.errorMessage || 'Something went wrong. Please try again.')
      }

      setResult(data)
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  function reset() {
    setResult(null)
    setError(null)
  }

  return { result, loading, error, generate, reset }
}
