import { useState } from 'react'
import { useTestGenerator } from './hooks/useTestGenerator'
import { CategoryCard } from './components/CategoryCard'

const EXAMPLE_PROMPTS = [
  "User login with email and password",
  "User can upload a profile picture",
  "Search and filter products by category",
  "Checkout flow with credit card payment",
]

export default function App() {
  const [input, setInput] = useState('')
  const [format, setFormat] = useState('markdown')
  const [copied, setCopied] = useState(false)
  const { result, loading, error, generate } = useTestGenerator()

  function handleSubmit() {
    if (input.trim().length < 10) return
    generate(input.trim(), format)
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

  return (
    <div className="app">

      <header className="header">
        <div className="header-tag">AI-Powered · Built for QA Engineers</div>
        <h1>Test<span>Genie</span> AI</h1>
        <p className="header-sub">
          Describe a feature. Get comprehensive test cases in seconds —
          happy path, edge cases, security, and more.
        </p>
      </header>

      <div className="input-section">
        <label className="input-label">Feature Description</label>

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
            disabled={loading || input.trim().length < 10}
          >
            {loading ? 'Generating...' : '⚡ Generate Test Cases'}
          </button>

          <span style={{ fontSize: '11px', color: 'var(--text-muted)' }}>
            or Cmd+Enter
          </span>
        </div>

        {error && <div className="error-box">❌ {error}</div>}
      </div>

      {result && (
        <div className="results">
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
