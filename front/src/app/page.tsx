import Navbar from "@/components/layout/Navbar";
import Footer from "@/components/layout/Footer";
import LiveTicker from "@/components/landing/LiveTicker";
import HeroSection from "@/components/landing/HeroSection";
import VisionSection from "@/components/landing/VisionSection";
import ServicesGrid from "@/components/landing/ServicesGrid";
import CTASection from "@/components/landing/CTASection";

export default function Home() {
  return (
    <div id="main-content" className="min-h-screen flex flex-col font-sans bg-slate-50 relative overflow-x-hidden">
      <Navbar />


      <div className="h-16"></div>


      <LiveTicker />


      <div className="absolute inset-0 pointer-events-none overflow-hidden">
        <svg
          className="absolute w-full h-full opacity-[0.03]"
          xmlns="http://www.w3.org/2000/svg"
        >
          <defs>
            <pattern
              id="grid-pattern"
              width="40"
              height="40"
              patternUnits="userSpaceOnUse"
            >
              <path
                d="M0 40L40 0H20L0 20M40 40V20L20 40"
                stroke="currentColor"
                strokeWidth="2"
                fill="none"
              />
            </pattern>
          </defs>
          <rect width="100%" height="100%" fill="url(#grid-pattern)" />
        </svg>
      </div>


      <HeroSection />


      <VisionSection />


      <ServicesGrid />


      <CTASection />


      <Footer />
    </div>
  );
}
