import { useState } from 'react'
import { useTestGenerator } from './hooks/useTestGenerator'
import { CategoryCard } from './components/CategoryCard'
import { RefinePanel } from './components/RefinePanel'

const EXAMPLE_PROMPTS = [
  "User login with email and password",
  "User can upload a profile picture",
  "Search and filter products by category",
  "Checkout flow with credit card payment",
]

export default function App() {
  const [input, setInput] = useState('')
  const [extraContext, setExtraContext] = useState('')
  const [format, setFormat] = useState('markdown')
  const [copied, setCopied] = useState(false)
  const [screenshot, setScreenshot] = useState(null)
  const [screenshotPreview, setScreenshotPreview] = useState(null)
  const [result, setResult] = useState(null)
  const { loading, error, generate } = useTestGenerator()

  function handleFileChange(e) {
    const file = e.target.files[0]
    if (!file) return
    setScreenshot(file)
    setScreenshotPreview(URL.createObjectURL(file))
  }

  function handleDrop(e) {
    e.preventDefault()
    const file = e.dataTransfer.files[0]
    if (!file) return
    setScreenshot(file)
    setScreenshotPreview(URL.createObjectURL(file))
  }

  function removeScreenshot() {
    setScreenshot(null)
    setScreenshotPreview(null)
  }

  async function handleSubmit() {
    const hasInput = input.trim().length > 0
      || extraContext.trim().length > 0
      || screenshot !== null
    if (!hasInput) return
    const data = await generate(input.trim(), extraContext.trim(), format, screenshot)
    if (data) setResult(data)
  }

  function handleKeyDown(e) {
    if (e.key === 'Enter' && (e.metaKey || e.ctrlKey)) handleSubmit()
  }

  async function handleCopy() {
    if (!result?.formattedOutput) return
    await navigator.clipboard.writeText(result.formattedOutput)
    setCopied(true)
    setTimeout(() => setCopied(false), 2000)
  }

  // Called when RefinePanel gets a response from the AI
  function handleRefined(refinedResult) {
    setResult(refinedResult)
    // Scroll back up to results
    window.scrollTo({ top: 400, behavior: 'smooth' })
  }

  return (
    <div className="app">

      <header className="header">
        <div className="header-tag">AI-Powered · Built for QA Engineers</div>
        <h1>Test<span>Genie</span> AI</h1>
        <p className="header-sub">
          Upload a Jira ticket screenshot or describe a feature —
          get critical test cases with P1/P2/P3 ratings and Given/When/Then steps.
        </p>
      </header>

      <div className="input-section">

        {/* Screenshot Upload */}
        <label className="input-label">Jira Ticket Screenshot (optional)</label>
        <div
          className={`upload-zone ${screenshotPreview ? 'has-image' : ''}`}
          onDrop={handleDrop}
          onDragOver={e => e.preventDefault()}
        >
          {screenshotPreview ? (
            <div className="upload-preview">
              <img src={screenshotPreview} alt="Jira ticket preview" />
              <button className="btn-remove" onClick={removeScreenshot}>✕ Remove</button>
            </div>
          ) : (
            <label className="upload-placeholder">
              <span className="upload-icon">📎</span>
              <span>Drop your Jira screenshot here or <u>browse</u></span>
              <span className="upload-hint">PNG, JPG up to 10MB</span>
              <input
                type="file"
                accept="image/*"
                onChange={handleFileChange}
                style={{ display: 'none' }}
              />
            </label>
          )}
        </div>

        {/* Feature Description */}
        <label className="input-label" style={{ marginTop: '24px' }}>
          Feature Description (optional)
        </label>
        <div className="textarea-wrapper">
          <textarea
            value={input}
            onChange={e => setInput(e.target.value)}
            onKeyDown={handleKeyDown}
            placeholder="e.g. User can reset their password via email link..."
            maxLength={2000}
          />
          <span className="char-count">{input.length} / 2000</span>
        </div>

        {/* Example prompts */}
        <div style={{ marginTop: '10px', display: 'flex', gap: '8px', flexWrap: 'wrap' }}>
          {EXAMPLE_PROMPTS.map(p => (
            <button
              key={p}
              onClick={() => setInput(p)}
              style={{
                background: 'transparent',
                border: '1px solid var(--border)',
                borderRadius: '20px',
                color: 'var(--text-muted)',
                fontFamily: 'var(--font-mono)',
                fontSize: '11px',
                padding: '4px 12px',
                cursor: 'pointer',
              }}
            >
              {p}
            </button>
          ))}
        </div>

        {/* Extra Context */}
        <label className="input-label" style={{ marginTop: '24px' }}>
          Additional Context (optional)
        </label>
        <textarea
          value={extraContext}
          onChange={e => setExtraContext(e.target.value)}
          onKeyDown={handleKeyDown}
          placeholder="e.g. This is a high traffic feature used by 1M users. Payment provider is Stripe..."
          maxLength={1000}
          style={{ minHeight: '80px' }}
        />

        <div className="controls">
          <select
            className="format-select"
            value={format}
            onChange={e => setFormat(e.target.value)}
          >
            <option value="markdown">Export: Markdown</option>
            <option value="jira">Export: Jira</option>
            <option value="csv">Export: CSV</option>
          </select>

          <button
            className="btn-generate"
            onClick={handleSubmit}
            disabled={loading || (!input.trim() && !extraContext.trim() && !screenshot)}
          >
            {loading ? 'Analyzing...' : '⚡ Generate Critical Test Cases'}
          </button>

          <span style={{ fontSize: '11px', color: 'var(--text-muted)' }}>
            or Cmd+Enter
          </span>
        </div>

        {error && <div className="error-box">❌ {error}</div>}
      </div>

      {result && (
        <div className="results">

          {/* Summary */}
          {result.summary && (
            <div className="summary-box">
              <span className="summary-label">AI Summary</span>
              <p>{result.summary}</p>
            </div>
          )}

          {/* Risk Areas */}
          {result.riskAreas && result.riskAreas.length > 0 && (
            <div className="risk-box">
              <span className="risk-label">⚠️ Risk Areas</span>
              <ul className="risk-list">
                {result.riskAreas.map((risk, i) => (
                  <li key={i}>{risk}</li>
                ))}
              </ul>
            </div>
          )}

          <div className="results-header">
            <div className="results-meta">
              <strong>{result.totalCases}</strong> test cases generated
              <span style={{ marginLeft: '12px' }}>
                for "{result.featureDescription}"
              </span>
            </div>
            <button
              className={`btn-copy ${copied ? 'copied' : ''}`}
              onClick={handleCopy}
            >
              {copied ? '✓ Copied!' : 'Copy output'}
            </button>
          </div>

          <div className="categories">
            {Object.entries(result.testCases).map(([category, cases]) => (
              <CategoryCard key={category} category={category} testCases={cases} />
            ))}
          </div>

          <div className="output-panel">
            <div className="output-panel-header">
              <span className="output-panel-title">
                Raw output — {format.toUpperCase()} format
              </span>
            </div>
            <pre>{result.formattedOutput}</pre>
          </div>

          {/* Refinement Panel — always shown after results */}
          <RefinePanel
            originalContext={input || result.featureDescription}
            existingCases={result.testCases}
            exportFormat={format}
            onRefined={handleRefined}
          />

        </div>
      )}

      <footer className="footer">
        <span>TestGenie AI — open source, built for QA engineers</span>
        <a href="https://github.com/kevilkhadka/testgenie-ai" target="_blank" rel="noreferrer">
          ★ Star on GitHub
        </a>
      </footer>

    </div>
  )
}
