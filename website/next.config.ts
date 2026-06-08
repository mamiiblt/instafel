/*
 * (c) 2026 Muhammed Ali Bulut, All rights reserved.
 *
 * See LICENSE file in repository root for copy file of license. For copyright
 * notices, technical issues, feedback, or any other related to this code file or
 * project, please contact me via mamii@mamii.dev or other ways.
 */

/*
 * (c) 2026 Muhammed Ali Bulut, All rights reserved.
 *
 * See LICENSE file in repository root for copy file of license. For copyright
 * notices, technical issues, feedback, or any other related to this code file or
 * project, please contact me via mamii@mamii.dev or other ways.
 */

import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  turbopack: {},
  images: {
    remotePatterns: [
      {
        protocol: "https",
        hostname: "raw.githubusercontent.com",
        pathname: "/**",
      },
    ],
  },
  env: {
    NEXT_PUBLIC_SITE_URL: "https://instafel.mamii.dev",
  },
  redirects: async () => {
    return [
      { source: "/home", destination: "/", permanent: true },
      { source: "/guide", destination: "/wiki", permanent: true },
      { source: "/guides", destination: "/wiki", permanent: true },
      {
        source: "/library_backup",
        destination: "/library/backup",
        permanent: true,
      },
      {
        source: "/library_flag",
        destination: "/library/flib_moved",
        permanent: true,
      },
      { source: "/flag", destination: "/library/flib_moved", permanent: true },
      {
        source: "/library/flag",
        destination: "/library/flib_moved",
        permanent: true,
      },
      {
        source: "/library/flag/view",
        destination: "/library/flib_moved",
        permanent: true,
      },
      {
        source: "/backup",
        destination: "/library/backup/view",
        permanent: true,
      },
    ];
  },
};

export default nextConfig;
