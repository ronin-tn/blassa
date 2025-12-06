/*
  Common Components Barrel Export
  --------------------------------
  Export components from this folder.
  Note: Button now comes from shadcn, not here.
*/

// Input wrapper (uses shadcn Input + Label internally)
export { default as Input } from './Input';

// Spinner (custom - shadcn doesn't provide one)
export { default as Spinner } from './Spinner';

// Location Autocomplete (uses Nominatim/OpenStreetMap)
export { default as LocationAutocomplete } from './LocationAutocomplete';

// Re-export shadcn Button for convenience
export { Button } from '@/components/ui/button';
