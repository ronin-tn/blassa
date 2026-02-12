import type { NextConfig } from "next";

const backendUrl = process.env.BACKEND_URL || "http://localhost:8088";

const nextConfig: NextConfig = {
  images: {
    remotePatterns: [
      {
        protocol: "https",
        hostname: "images.unsplash.com",
        pathname: "/**",
      },
      {
        protocol: "https",
        hostname: "lh3.googleusercontent.com",
        pathname: "/**",
      },
      {
        protocol: "https",
        hostname: "res.cloudinary.com",
        pathname: "/**",
      },
    ],
  },
  async rewrites() {
    return [
      {
        source: "/api/:path*",
        destination: `${backendUrl}/api/:path*`,
      },
      {
        source: "/ws/:path*",
        destination: `${backendUrl}/ws/:path*`,
      },
      {
        source: "/oauth2/:path*",
        destination: `${backendUrl}/oauth2/:path*`,
      },
      {
        source: "/login/oauth2/:path*",
        destination: `${backendUrl}/login/oauth2/:path*`,
      },
    ];
  },
};

export default nextConfig;
