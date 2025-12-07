/*
  LocationAutocomplete Component - Geoapify Version
  --------------------------------------------------
  Uses Geoapify Address Autocomplete API for location search.
  Returns coordinates (lat, lon) directly from the API.
  
  Features:
  - Real-time autocomplete suggestions
  - Tunisia-focused search (countrycodes=tn)
  - Returns { name, label, lat, lon } on selection
  - Debounced input to reduce API calls
  
  Usage:
  <LocationAutocomplete
    label="Départ"
    placeholder="Tunis, Sousse..."
    value={departure}
    onChange={(location) => setDeparture(location)}
  />
  
  Where location = { 
    name: "Sousse", 
    label: "Sousse, Sousse Governorate, Tunisia",
    lat: 35.82,
    lon: 10.64
  }
*/

import { useState, useEffect, useRef } from 'react';
import { MapPin, Loader2 } from 'lucide-react';
import { Input as ShadcnInput } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { cn } from '@/lib/utils';

// Geoapify API configuration
const GEOAPIFY_API_KEY = import.meta.env.VITE_GEOAPIFY_API_KEY;
const GEOAPIFY_BASE_URL = 'https://api.geoapify.com/v1/geocode/autocomplete';

// Debounce hook
const useDebounce = (value, delay) => {
  const [debouncedValue, setDebouncedValue] = useState(value);

  useEffect(() => {
    const handler = setTimeout(() => {
      setDebouncedValue(value);
    }, delay);

    return () => clearTimeout(handler);
  }, [value, delay]);

  return debouncedValue;
};

// Transform Geoapify feature to our location format
const transformFeature = (feature) => {
  const props = feature.properties;
  const coords = feature.geometry.coordinates;

  // Build a readable name from available properties
  const name =
    props.city || props.suburb || props.district || props.county || props.name || props.formatted;

  return {
    id: props.place_id,
    name: name,
    label: props.formatted,
    lat: coords[1], // GeoJSON is [lon, lat]
    lon: coords[0],
    // Additional useful data
    city: props.city,
    state: props.state,
    country: props.country,
  };
};

const LocationAutocomplete = ({
  label,
  placeholder = 'Rechercher une ville...',
  value,
  onChange,
  error,
  className,
  // Tunisia only by default, pass null for worldwide
  countryFilter = 'tn',
}) => {
  const [inputValue, setInputValue] = useState(value?.label || value?.name || '');
  const [suggestions, setSuggestions] = useState([]);
  const [isOpen, setIsOpen] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [highlightedIndex, setHighlightedIndex] = useState(-1);
  const wrapperRef = useRef(null);
  const inputRef = useRef(null);
  const abortControllerRef = useRef(null);

  // Debounce the input value (300ms for API calls)
  const debouncedSearch = useDebounce(inputValue, 300);

  // Sync input value when value prop changes
  useEffect(() => {
    const displayValue = value?.label || value?.name || '';
    if (displayValue && displayValue !== inputValue) {
      setInputValue(displayValue);
    }
  }, [value]);

  // Fetch suggestions from Geoapify when debounced value changes
  useEffect(() => {
    // Don't search if input is too short
    if (!debouncedSearch || debouncedSearch.length < 2) {
      setSuggestions([]);
      setIsOpen(false);
      return;
    }

    // Don't search if input matches a selected value
    if (value?.label === debouncedSearch || value?.name === debouncedSearch) {
      return;
    }

    // Cancel previous request if still pending
    if (abortControllerRef.current) {
      abortControllerRef.current.abort();
    }

    const fetchSuggestions = async () => {
      setIsLoading(true);
      abortControllerRef.current = new AbortController();

      try {
        // Build URL with parameters
        const params = new URLSearchParams({
          text: debouncedSearch,
          apiKey: GEOAPIFY_API_KEY,
          limit: '8',
          lang: 'fr', // French results
          type: 'city', // Focus on cities/towns
        });

        // Add country filter if specified
        if (countryFilter) {
          params.append('filter', `countrycode:${countryFilter}`);
        }

        const response = await fetch(`${GEOAPIFY_BASE_URL}?${params.toString()}`, {
          signal: abortControllerRef.current.signal,
        });

        if (!response.ok) {
          throw new Error(`Geoapify API error: ${response.status}`);
        }

        const data = await response.json();

        // Transform features to our format
        const locations = data.features?.map(transformFeature) || [];

        setSuggestions(locations);
        setIsOpen(locations.length > 0);
        setHighlightedIndex(-1);
      } catch (error) {
        // Ignore abort errors (they're expected when user types fast)
        if (error.name !== 'AbortError') {
          console.error('Geoapify search error:', error);
          setSuggestions([]);
        }
      } finally {
        setIsLoading(false);
      }
    };

    fetchSuggestions();

    // Cleanup: abort on unmount or new search
    return () => {
      if (abortControllerRef.current) {
        abortControllerRef.current.abort();
      }
    };
  }, [debouncedSearch, value, countryFilter]);

  // Handle selection
  const handleSelect = (location) => {
    setInputValue(location.label);
    setSuggestions([]);
    setIsOpen(false);

    onChange?.({
      name: location.name,
      label: location.label,
      lat: location.lat,
      lon: location.lon,
    });
  };

  // Handle input change
  const handleInputChange = (e) => {
    const newValue = e.target.value;
    setInputValue(newValue);

    // Clear the selected location if user is typing
    if (value?.name && newValue !== value.name && newValue !== value.label) {
      onChange?.(null);
    }
  };

  // Keyboard navigation
  const handleKeyDown = (e) => {
    if (!isOpen || suggestions.length === 0) return;

    switch (e.key) {
      case 'ArrowDown':
        e.preventDefault();
        setHighlightedIndex((prev) => (prev < suggestions.length - 1 ? prev + 1 : prev));
        break;
      case 'ArrowUp':
        e.preventDefault();
        setHighlightedIndex((prev) => (prev > 0 ? prev - 1 : prev));
        break;
      case 'Enter':
        e.preventDefault();
        if (highlightedIndex >= 0) {
          handleSelect(suggestions[highlightedIndex]);
        }
        break;
      case 'Escape':
        setIsOpen(false);
        break;
    }
  };

  // Close dropdown when clicking outside
  useEffect(() => {
    const handleClickOutside = (event) => {
      if (wrapperRef.current && !wrapperRef.current.contains(event.target)) {
        setIsOpen(false);
      }
    };

    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  return (
    <div ref={wrapperRef} className={cn('relative w-full space-y-2', className)}>
      {/* Label */}
      {label && <Label className={cn(error && 'text-destructive')}>{label}</Label>}

      {/* Input with icon */}
      <div className="relative">
        <MapPin className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-muted-foreground pointer-events-none" />
        <ShadcnInput
          ref={inputRef}
          type="text"
          placeholder={placeholder}
          value={inputValue}
          onChange={handleInputChange}
          onKeyDown={handleKeyDown}
          onFocus={() => suggestions.length > 0 && setIsOpen(true)}
          className={cn('pl-9', error && 'border-destructive focus-visible:ring-destructive')}
          autoComplete="off"
        />

        {/* Loading indicator */}
        {isLoading && (
          <div className="absolute right-3 top-1/2 -translate-y-1/2">
            <Loader2 className="h-4 w-4 animate-spin text-primary" />
          </div>
        )}
      </div>

      {/* Suggestions dropdown */}
      {isOpen && suggestions.length > 0 && (
        <ul className="absolute z-50 w-full mt-1 bg-white border rounded-md shadow-lg max-h-60 overflow-auto">
          {suggestions.map((location, index) => (
            <li
              key={location.id}
              className={cn(
                'px-3 py-2 cursor-pointer flex items-center gap-2',
                index === highlightedIndex ? 'bg-primary/10 text-primary' : 'hover:bg-muted'
              )}
              onClick={() => handleSelect(location)}
            >
              <MapPin className="h-4 w-4 text-muted-foreground shrink-0" />
              <div className="flex flex-col overflow-hidden">
                <span className="truncate font-medium">{location.name}</span>
                <span className="truncate text-xs text-muted-foreground">{location.label}</span>
              </div>
            </li>
          ))}
        </ul>
      )}

      {/* Error message */}
      {error && <p className="text-sm text-destructive">{error}</p>}
    </div>
  );
};

export default LocationAutocomplete;
