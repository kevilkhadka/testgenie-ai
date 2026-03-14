import { useState } from 'react'
import { useRefine } from '../hooks/useRefine'

const QUICK_ACTIONS = [
  '+ More P1 critical cases',
  '+ Add mobile scenarios',
  '+ More security test cases',
  '+ Add payment edge cases',
  '+ More negative cases',
  '+ Add accessibility cases',
  '+ Simplify the test cases',
  '+ Add API testing scenarios',
]

/**
 * RefinePanel
 *
 * Shown after test cases are generated.
 * Lets the user give feedback and refine the results.
 *
 * Props:
 *   - originalContext  : the original feature description
 *   - existingCases    : the current test cases object
 *   - exportFormat     : current export format
 *   - onRefined        : callback when refinement is done
 */
export function RefinePanel({ originalContext, existingCases, exportFormat, onRefined }) {
  const [feedback, setFeedback] = useState('')
  const [mode, setMode] = useState('replace')
  const { refine, loading, error } = useRefine()

  function handleQuickAction(action) {
    setFeedback(prev => {
      if (prev.trim() === '') return action
      return prev + ', ' + action
    })
  }

  async function handleRefine() {
    if (!feedback.trim()) return
    const result = await refine(
      originalContext,
      existingCases,
      feedback,
      mode,
      exportFormat
    )
    if (result) {
      onRefined(result)
      setFeedback('')
    }
  }

  return (
    <div className="refine-panel">

      <div className="refine-header">
        <span className="refine-title">🔁 Refine Test Cases</span>
        <span className="refine-subtitle">
          Not happy with the results? Tell the AI what to fix.
        </span>
      </div>

      {/* Quick action buttons */}
      <div className="quick-actions">
        {QUICK_ACTIONS.map(action => (
          <button
            key={action}
            className="quick-action-btn"
            onClick={() => handleQuickAction(action)}
          >
            {action}
          </button>
        ))}
      </div>

      {/* Free form feedback */}
      <div className="textarea-wrapper" style={{ marginTop: '16px' }}>
        <textarea
          value={feedback}
          onChange={e => setFeedback(e.target.value)}
          placeholder="e.g. You missed offline scenarios. Add more cases for when the network is unavailable..."
          maxLength={1000}
          style={{ minHeight: '100px' }}
        />
        <span className="char-count">{feedback.length} / 1000</span>
      </div>

      {/* Mode selector + submit */}
      <div className="refine-controls">
        <div className="mode-selector">
          <button
            className={`mode-btn ${mode === 'replace' ? 'active' : ''}`}
            onClick={() => setMode('replace')}
          >
            Replace
          </button>
          <button
            className={`mode-btn ${mode === 'append' ? 'active' : ''}`}
            onClick={() => setMode('append')}
          >
            Append
          </button>
          <span className="mode-hint">
            {mode === 'replace'
              ? 'AI rewrites all test cases with your feedback'
              : 'AI adds new test cases on top of existing ones'}
          </span>
        </div>

        <button
          className="btn-generate"
          onClick={handleRefine}
          disabled={loading || !feedback.trim()}
          style={{ whiteSpace: 'nowrap' }}
        >
          {loading ? 'Refining...' : '✨ Refine Test Cases'}
        </button>
      </div>

      {error && <div className="error-box" style={{ marginTop: '12px' }}>❌ {error}</div>}

    </div>
  )
}
