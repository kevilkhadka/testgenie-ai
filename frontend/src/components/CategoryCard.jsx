import { useState } from 'react'

const CATEGORY_ICONS = {
  'Happy Path':     '✅',
  'Negative Cases': '❌',
  'Edge Cases':     '⚠️',
  'UI / UX':        '🎨',
  'Performance':    '⚡',
  'Security':       '🔒',
}

const PRIORITY_COLORS = {
  P1: { bg: 'rgba(255, 68, 68, 0.15)', border: 'rgba(255, 68, 68, 0.5)', text: '#ff4444' },
  P2: { bg: 'rgba(255, 165, 0, 0.15)', border: 'rgba(255, 165, 0, 0.5)', text: '#ffa500' },
  P3: { bg: 'rgba(0, 255, 136, 0.15)', border: 'rgba(0, 255, 136, 0.4)', text: '#00ff88' },
}

export function CategoryCard({ category, testCases }) {
  const [checked, setChecked] = useState(new Set())
  const [expanded, setExpanded] = useState(new Set())

  function toggleCheck(index) {
    setChecked(prev => {
      const next = new Set(prev)
      next.has(index) ? next.delete(index) : next.add(index)
      return next
    })
  }

  function toggleExpand(index) {
    setExpanded(prev => {
      const next = new Set(prev)
      next.has(index) ? next.delete(index) : next.add(index)
      return next
    })
  }

  const icon = CATEGORY_ICONS[category] || '🧪'
  const p1Count = testCases.filter(tc => tc.priority === 'P1').length

  return (
    <div className="category-card">
      <div className="category-header">
        <span className="category-name">{icon} {category}</span>
        <div style={{ display: 'flex', gap: '8px', alignItems: 'center' }}>
          {p1Count > 0 && (
            <span style={{
              fontSize: '11px',
              background: 'rgba(255,68,68,0.15)',
              border: '1px solid rgba(255,68,68,0.5)',
              color: '#ff4444',
              borderRadius: '20px',
              padding: '2px 8px',
            }}>
              {p1Count} P1
            </span>
          )}
          <span className="category-count">
            {checked.size > 0 ? `${checked.size}/${testCases.length}` : testCases.length} cases
          </span>
        </div>
      </div>

      <ul className="test-case-list">
        {testCases.map((tc, i) => {
          const priority = tc.priority || 'P2'
          const colors = PRIORITY_COLORS[priority] || PRIORITY_COLORS.P2
          const isExpanded = expanded.has(i)
          const isChecked = checked.has(i)

          return (
            <li
              key={i}
              className="test-case-item"
              style={{
                opacity: isChecked ? 0.45 : 1,
                flexDirection: 'column',
                alignItems: 'flex-start',
                gap: '8px',
              }}
            >
              {/* Title row */}
              <div style={{ display: 'flex', alignItems: 'flex-start', gap: '12px', width: '100%' }}>
                <input
                  type="checkbox"
                  checked={isChecked}
                  onChange={() => toggleCheck(i)}
                  style={{ marginTop: '3px', flexShrink: 0 }}
                />

                {/* Priority badge */}
                <span style={{
                  fontSize: '10px',
                  fontWeight: '700',
                  background: colors.bg,
                  border: `1px solid ${colors.border}`,
                  color: colors.text,
                  borderRadius: '4px',
                  padding: '2px 6px',
                  flexShrink: 0,
                  marginTop: '1px',
                }}>
                  {priority}
                </span>

                <span
                  style={{
                    flex: 1,
                    textDecoration: isChecked ? 'line-through' : 'none',
                    cursor: 'pointer',
                  }}
                  onClick={() => toggleExpand(i)}
                >
                  {tc.title}
                </span>

                {/* Expand toggle */}
                <button
                  onClick={() => toggleExpand(i)}
                  style={{
                    background: 'transparent',
                    border: 'none',
                    color: 'var(--text-muted)',
                    cursor: 'pointer',
                    fontSize: '12px',
                    flexShrink: 0,
                    padding: '0 4px',
                  }}
                >
                  {isExpanded ? '▲' : '▼'}
                </button>
              </div>

              {/* Given/When/Then steps — shown when expanded */}
              {isExpanded && (
                <div style={{
                  marginLeft: '52px',
                  background: 'var(--bg-elevated)',
                  border: '1px solid var(--border)',
                  borderRadius: '6px',
                  padding: '12px 16px',
                  width: 'calc(100% - 52px)',
                  fontSize: '12px',
                  lineHeight: '1.8',
                }}>
                  {tc.given && (
                    <div>
                      <span style={{ color: 'var(--accent)', fontWeight: '600' }}>Given </span>
                      <span style={{ color: 'var(--text-secondary)' }}>{tc.given}</span>
                    </div>
                  )}
                  {tc.when && (
                    <div>
                      <span style={{ color: '#ffa500', fontWeight: '600' }}>When </span>
                      <span style={{ color: 'var(--text-secondary)' }}>{tc.when}</span>
                    </div>
                  )}
                  {tc.then && (
                    <div>
                      <span style={{ color: '#00aaff', fontWeight: '600' }}>Then </span>
                      <span style={{ color: 'var(--text-secondary)' }}>{tc.then}</span>
                    </div>
                  )}
                </div>
              )}
            </li>
          )
        })}
      </ul>
    </div>
  )
}
