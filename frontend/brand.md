Based on the Tunisian context, the trust-based nature of the app, and the need for gender safety, I have designed a visual theme called "Mediterranean Motion".

This theme balances Professionalism (Trust) with Warmth (Community). It avoids the "aggressive" reds of competitors (like Yassir) and the generic greens of others (like Bolt/InDriver), giving Blassa a unique, calm, and reliable identity.

Here are your Design Tokens and the Tailwind configuration.

The "Blassa" Theme Philosophy
Primary (The Sea - Trust): A deep, rich Ocean Blue. In Tunisia, blue represents the Mediterranean and the architecture (Sidi Bou Said). Psychologically, blue induces trust and calm—critical for a stranger-to-stranger carpooling app.

Secondary (The Sun - Action): A warm Amber/Gold. This represents the "Taxi" heritage, energy, and the warmth of the Tunisian south. It is used for "Call to Action" buttons (e.g., "Réserver").

Gender Safety (The Coral): A distinct, non-patronizing Soft Coral color used exclusively to highlight FEMALE_ONLY rides. It stands out from the blue/gold but isn't a stereotypical "hot pink."




Token Name,Hex Code,Visual Meaning,Usage
Brand Primary,#0E7490,Cyan-700 (Deep Teal),"Navbar, Primary Headers, Icons. Represents the ""Blassa"" brand."
Brand Action,#F59E0B,Amber-500 (Warm Gold),"""Book Ride"", ""Post Ride"", Primary Buttons. High visibility in sunlight."
Surface,#F8FAFC,Slate-50 (Off White),Page background. Reduces glare compared to pure white.
Text Main,#1E293B,Slate-800 (Dark Grey),"Headings, Main Text. Softer than pure black."
Text Muted,#64748B,Slate-500 (Grey),"Metadata (Dates, Seat count)."
Safety/Female,#F43F5E,Rose-500 (Coral Red),"FEMALE_ONLY badges, Gender icons for women."
Status Safe,#10B981,Emerald-500,"Available seats, confirmed bookings."
Status Alert,#EF4444,Red-500,"Full rides, Cancelled, Error messages."

Based on the Tunisian context, the trust-based nature of the app, and the need for gender safety, I have designed a visual theme called "Mediterranean Motion".This theme balances Professionalism (Trust) with Warmth (Community). It avoids the "aggressive" reds of competitors (like Yassir) and the generic greens of others (like Bolt/InDriver), giving Blassa a unique, calm, and reliable identity.Here are your Design Tokens and the Tailwind configuration.The "Blassa" Theme PhilosophyPrimary (The Sea - Trust): A deep, rich Ocean Blue. In Tunisia, blue represents the Mediterranean and the architecture (Sidi Bou Said). Psychologically, blue induces trust and calm—critical for a stranger-to-stranger carpooling app.1Secondary (The Sun - Action): A warm Amber/Gold. This represents the "Taxi" heritage, energy, and the warmth of the Tunisian south. It is used for "Call to Action" buttons (e.g., "Réserver").Gender Safety (The Coral): A distinct, non-patronizing Soft Coral color used exclusively to highlight FEMALE_ONLY rides. It stands out from the blue/gold but isn't a stereotypical "hot pink."1. The Color Palette (Design Tokens)Token NameHex CodeVisual MeaningUsageBrand Primary#0E7490Cyan-700 (Deep Teal)Navbar, Primary Headers, Icons. Represents the "Blassa" brand.Brand Action#F59E0BAmber-500 (Warm Gold)"Book Ride", "Post Ride", Primary Buttons. High visibility in sunlight.Surface#F8FAFCSlate-50 (Off White)Page background. Reduces glare compared to pure white.Text Main#1E293BSlate-800 (Dark Grey)Headings, Main Text. Softer than pure black.Text Muted#64748BSlate-500 (Grey)Metadata (Dates, Seat count).Safety/Female#F43F5ERose-500 (Coral Red)FEMALE_ONLY badges, Gender icons for women.Status Safe#10B981Emerald-500Available seats, confirmed bookings.Status Alert#EF4444Red-500Full rides, Cancelled, Error messages.2. Implementation: tailwind.config.jsCopy this directly into your React project. This creates semantic class names (e.g., bg-brand-primary) so you can change the colors globally later without editing every file.2JavaScript/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        brand: {
          DEFAULT: '#0E7490', // Cyan-700: The main Blassa Blue
          dark: '#164E63',    // Cyan-900: Hover states
          light: '#CFFAFE',   // Cyan-100: Background highlights
        },
        action: {
          DEFAULT: '#F59E0B', // Amber-500: The "Book" button
          hover: '#D97706',   // Amber-600: Hover state
        },
        gender: {
          female: '#F43F5E', // Rose-500: Female preference
          male: '#3B82F6',   // Blue-500: Male preference
          any: '#64748B',    // Slate-500: Any preference
        },
        status: {
          success: '#10B981', // Emerald-500
          error: '#EF4444',   // Red-500
          warning: '#F59E0B', // Amber-500
        }
      },
      fontFamily: {
        sans: ['Inter', 'system-ui', 'sans-serif'], // Clean, modern, supports French/Arabic well
      },
      boxShadow: {
        'card': '0 2px 8px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06)',
      }
    },
  },
  plugins: [],
}
3. Usage Examples (Applying to your Components)Here is how these tokens apply to the specific constraints of your project.A. The RideCard.jsx (Visual Hierarchy)Background: White with shadow-card.Price: Big, Bold, text-brand-DEFAULT (The Blue).Time: text-gray-900 (Bold).Route: Origin/Destination text-gray-600.B. The GenderCard.jsx (Safety Logic)This is critical for your "Gender Safety" business logic.JavaScriptconst GenderBadge = ({ preference }) => {
  if (preference === 'FEMALE_ONLY') {
    return (
      <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-rose-100 text-rose-800">
        <svg className="w-3 h-3 mr-1" fill="currentColor" viewBox="0 0 20 20">
            {/* Female Icon SVG */}
        </svg>
        Réservé aux femmes
      </span>
    );
  }
  // Default for ANY
  return (
    <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-gray-100 text-gray-800">
      Mixte
    </span>
  );
};
C. The Primary Button (Button.jsx)Used for "Réserver" (Book) or "Publier un trajet" (Post Ride).JavaScript// src/components/Button.jsx
const Button = ({ children, variant = 'primary', ...props }) => {
  const baseStyle = "w-full py-3 px-4 rounded-lg font-semibold transition-colors duration-200 focus:outline-none focus:ring-2 focus:ring-offset-2";
  
  const variants = {
    primary: "bg-brand-DEFAULT hover:bg-brand-dark text-white focus:ring-brand-DEFAULT", // Main Blue
    action: "bg-action-DEFAULT hover:bg-action-hover text-white focus:ring-action-DEFAULT", // Gold/Amber (Use for Booking!)
    outline: "border-2 border-brand-DEFAULT text-brand-DEFAULT hover:bg-brand-light",
  };

  return (
    <button className={`${baseStyle} ${variants[variant]}`} {...props}>
      {children}
    </button>
  );
};
4. Why this works for TunisiaTrust: The Teal/Cyan is slightly different from the corporate blues of banks (BIAT/Attijari), making it feel tech-forward but still trustworthy.Visibility: The Amber action color is highly visible on mobile screens even in direct Tunisian sunlight.Clarity: The strict distinction between gender-female (Rose) and gender-any (Grey) reduces cognitive load—users know instantly if a ride is safe for them.