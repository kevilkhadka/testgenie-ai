import { useState } from 'react'

export function useTestGenerator() {
  const [result, setResult] = useState(null)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)

  async function generate(featureDescription, extraContext, exportFormat, screenshot) {
    setLoading(true)
    setError(null)
    setResult(null)

    try {
      let data

      if (screenshot) {
        // If screenshot provided — use multipart/form-data upload endpoint
        const formData = new FormData()
        formData.append('screenshot', screenshot)
        if (featureDescription) formData.append('featureDescription', featureDescription)
        if (extraContext) formData.append('extraContext', extraContext)
        formData.append('exportFormat', exportFormat)

        const response = await fetch('/api/generate/upload', {
          method: 'POST',
          body: formData,
          // NOTE: Do NOT set Content-Type header manually
          // Browser sets it automatically with the correct boundary for multipart
        })
        data = await response.json()

        if (!response.ok || !data.success) {
          throw new Error(data.errorMessage || 'Something went wrong. Please try again.')
        }

      } else {
        // No screenshot — use original JSON endpoint
        const response = await fetch('/api/generate', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ featureDescription, extraContext, exportFormat }),
        })
        data = await response.json()

        if (!response.ok || !data.success) {
          throw new Error(data.errorMessage || 'Something went wrong. Please try again.')
        }
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
