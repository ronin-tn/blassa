/*
  🎓 LEARNING: Simple Components & CSS Animation
  -----------------------------------------------
  Not all components need props! This spinner just spins.
  
  KEY CONCEPTS:
  1. SVG in React - Same as HTML, but use className instead of class
  2. Tailwind animations - animate-spin rotates 360° infinitely
  3. Optional props with defaults - size = 'md' if not passed
  
  USAGE:
  <Spinner />                    // Medium, brand color
  <Spinner size="lg" />          // Large spinner
  <Spinner className="text-white" /> // White spinner (for dark backgrounds)
*/

const Spinner = ({ size = 'md', className = '' }) => {
  // Size variants
  const sizes = {
    sm: 'h-4 w-4',
    md: 'h-8 w-8',
    lg: 'h-12 w-12',
  };

  return (
    <svg
      className={`animate-spin ${sizes[size]} ${className}`}
      fill="none"
      viewBox="0 0 24 24"
      aria-label="Chargement..."
    >
      {/* Background circle (faded) */}
      <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" />
      {/* Spinning arc */}
      <path
        className="opacity-75"
        fill="currentColor"
        d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"
      />
    </svg>
  );
};

export default Spinner;
