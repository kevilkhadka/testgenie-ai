import { useState } from 'react'

export function useTestGenerator() {
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)

  async function generate(featureDescription, extraContext, exportFormat, screenshot) {
    setLoading(true)
    setError(null)

    try {
      let data

      if (screenshot) {
        // Screenshot provided — use multipart upload endpoint
        const formData = new FormData()
        formData.append('screenshot', screenshot)
        if (featureDescription) formData.append('featureDescription', featureDescription)
        if (extraContext) formData.append('extraContext', extraContext)
        formData.append('exportFormat', exportFormat)

        const response = await fetch('/api/generate/upload', {
          method: 'POST',
          body: formData,
          // Do NOT set Content-Type manually —
          // browser sets it automatically with the correct multipart boundary
        })

        data = await response.json()

        if (!response.ok || !data.success) {
          throw new Error(data.errorMessage || 'Something went wrong. Please try again.')
        }

      } else {
        // No screenshot — use plain JSON endpoint
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

      return data

    } catch (err) {
      setError(err.message)
      return null
    } finally {
      setLoading(false)
    }
  }

  return { loading, error, generate }
}
