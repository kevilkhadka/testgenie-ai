import { useState } from 'react'

const CATEGORY_ICONS = {
  'Happy Path':     '✅',
  'Negative Cases': '❌',
  'Edge Cases':     '⚠️',
  'UI / UX':        '🎨',
  'Performance':    '⚡',
  'Security':       '🔒',
}

export function CategoryCard({ category, testCases }) {
  const [checked, setChecked] = useState(new Set())

  function toggleCheck(index) {
    setChecked(prev => {
      const next = new Set(prev)
      next.has(index) ? next.delete(index) : next.add(index)
      return next
    })
  }

  const icon = CATEGORY_ICONS[category] || '🧪'

  return (
    <div className="category-card">
      <div className="category-header">
        <span className="category-name">{icon} {category}</span>
        <span className="category-count">
          {checked.size > 0 ? `${checked.size}/${testCases.length}` : testCases.length} cases
        </span>
      </div>
      <ul className="test-case-list">
        {testCases.map((tc, i) => (
          <li
            key={i}
            className="test-case-item"
            onClick={() => toggleCheck(i)}
            style={{ opacity: checked.has(i) ? 0.45 : 1 }}
          >
            <input
              type="checkbox"
              checked={checked.has(i)}
              onChange={() => toggleCheck(i)}
              onClick={e => e.stopPropagation()}
            />
            <span style={{ textDecoration: checked.has(i) ? 'line-through' : 'none' }}>
              {tc}
            </span>
          </li>
        ))}
      </ul>
    </div>
  )
}
