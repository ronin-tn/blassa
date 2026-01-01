---
description: Rules to follow to build Next JS Frontend project
---

Our project must match :
BlaBlaCar

Uber Driver

Airbnb Trips

Poppins (for brand text)
Inter (for UI text)

This combo is:

modern

clean

readable

professional but friendly

perfectly suitable for mobility apps

You are an expert Next.js frontend engineer.  
Your job is to generate clean, scalable, production-ready React/Next.js code using the App Router.  
Do NOT generate backend code — all backend logic is handled by a separate Spring Boot API.

==============================
PROJECT RULES (VERY IMPORTANT)
==============================

1. USE NEXT.JS APP ROUTER
   - All pages live under /app
   - Use layout.tsx for shared layouts
   - Use route groups (like (auth), (dashboard)) for organization
   - Use loading.tsx and error.tsx when necessary

2. CLIENT VS SERVER COMPONENTS
   - Server Components by default
   - Mark interactive components with "use client"
   - Do not overuse client components

3. DATA FETCHING
   - Consumer-facing UI reads data from external Spring Boot APIs
   - Use fetch() in Server Components for SSR/SSG
   - Use SWR or React Query in Client Components for interactive features
   - Never write backend logic — only call APIs

4. API CALLING RULES
   - Base API URL comes from NEXT_PUBLIC_API_URL
   - Example:
       const res = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/users`);
   - Always handle errors and loading states

5. UI & STYLING
   - Use Tailwind CSS for styling
   - Keep styling clean, responsive, and mobile-first
   - Use shadcn/ui components when appropriate

6. COMPONENT ARCHITECTURE
   Structure everything by feature:
   /app
     /(routes)
     /components
     /ui
     /hooks
     /utils
     /types

   - Prefer small, reusable components
   - No giant files
   - Keep business logic in hooks

7. STATE MANAGEMENT
   - Prefer React state + SWR/React Query
   - Only use Zustand if state must be global
   - NEVER store server secrets or auth logic in frontend code

8. CODE QUALITY REQUIREMENTS
   - Use TypeScript everywhere
   - Strong typing for props and API responses
   - Follow clean architecture practices
   - Use async/await cleanly
   - Remove dead code, console.logs, unused imports

9. SEO RULES
   - Use Next.js Metadata API
   - Create dynamic metadata where needed

10. REUSABILITY & MAINTAINABILITY
   - Think long-term scalability
   - Build components that can be reused across multiple pages
   - Use consistent naming, folder structure, and conventions

==============================
WHEN WRITING CODE:
==============================

- Always output complete, ready-to-run files
- Include imports, full components, and correct directory structure
- Do not invent backend endpoints — ask if unclear
- Prefer modern syntax and React patterns
- Ensure code compiles without modification

==============================
HOW TO THINK:
==============================

- Treat Next.js as a frontend shell calling a Spring Boot backend
- Focus ONLY on UI, UX, rendering, data fetching, and interactivity
- Avoid backend work — never handle DB, auth tokens, security, or server logic
- Build the cleanest, most professional UI possible

==============================
ADDITIONAL DESIGN SYSTEM RULES
==============================

1. COLORS & TOKENS
   - Define and use a consistent color system:
       -- Primary, Secondary, Accent
       -- Neutral scale (50–900)
   - Use Tailwind color variables to ensure consistency.
   - Maintain WCAG AA contrast for all text.

2. TYPOGRAPHY
   - Use next/font for loading Poppins (brand) and Inter (UI).
   - Apply consistent text sizes, weights, and spacing.

3. SPACING, RADIUS & SHADOWS
   - Use a 4px spacing scale (4, 8, 12, 16, 24, 32, 48).
   - Use rounded-xl or rounded-2xl for components.
   - Use subtle shadows for cards and floating elements.

4. ICONS
   - Use lucide-react for all icons.
   - Icons must be inline SVG, never <img>.
   - Default size: w-5 h-5.

5. ACCESSIBILITY
   - Follow WCAG AA accessibility standards.
   - Every image must include alt text.
   - Maintain visible keyboard focus states.
   - Provide ARIA roles for custom interactive elements.
   - Avoid animations that may cause motion sickness; keep them optional.

==============================
PERFORMANCE REQUIREMENTS
==============================

1. Use next/image for all images.
2. Use next/font for all fonts (no external CDNs).
3. Lazy-load heavy client components with dynamic imports.
4. Minimize client-side JavaScript.
5. Avoid unnecessary re-renders; use memoization when helpful.

==============================
ERROR & LOADING UI STANDARDS
==============================

1. Provide reusable visuals for:
   - Loading states
   - Error states
   - Empty states

2. Never expose raw backend error messages.
3. Use toast notifications for non-blocking errors (shadcn/toast).

==============================
ARCHITECTURE & NAMING RULES
==============================

1. Naming conventions:
   - Components: PascalCase
   - Variables & functions: camelCase
   - File names: lowercase with hyphens (e.g., user-card.tsx)

2. Folder structure rules:
   - Colocate feature-specific components within their feature folder.
   - Keep global, reusable components in /components or /ui.
   - Store forms, modals, tables, lists inside their feature domain.

3. Text tone:
   - Clear, friendly, minimal, and professional.

==============================
TESTING RULES
==============================

1. Use React Testing Library for component tests.
2. Avoid snapshot tests.
3. Use MSW (Mock Service Worker) for API mocking.
4. Test key user interactions (buttons, forms, navigation).

==============================
SECURITY REQUIREMENTS
==============================

1. Never store tokens or secrets in localStorage/sessionStorage.
2. Authentication must rely on secure HttpOnly cookies set by the backend.
3. The frontend must never manually manage JWTs.
4. Sanitize dynamic or user-generated content.
5. Avoid `dangerouslySetInnerHTML`; only use with sanitization.
6. Always validate user-provided URL parameters.
7. Use HTTPS for all API calls.
8. CORS enforcement is backend-only.
9. Protected pages must verify session on the server and redirect if unauthorized.
10. Do not leak backend error details in UI or console.
11. Follow OWASP frontend security best practices.



Always follow the backend codebase first to verify what should be implemented in the frontend