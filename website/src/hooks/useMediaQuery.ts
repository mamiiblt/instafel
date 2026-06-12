/*
 * (c) 2026 Muhammed Ali Bulut, All rights reserved.
 *
 * See LICENSE file in repository root for copy file of license. For copyright
 * notices, technical issues, feedback, or any other related to this code file or
 * project, please contact me via mamii@mamii.dev or other ways.
 */

"use client";

import { useState, useEffect } from "react";

const useMediaQuery = (query: string): boolean => {
  // Initialize with null to indicate "not determined yet"
  const [matches, setMatches] = useState<boolean>(() => {
    // Check if window is defined (client-side)
    if (typeof window !== "undefined") {
      return window.matchMedia(query).matches;
    }
    return false; // Default to false on server-side
  });

  useEffect(() => {
    // Check if window is defined (client-side)
    if (typeof window === "undefined") {
      return;
    }

    const mediaQuery = window.matchMedia(query);

    // Update matches state initially
    setMatches(mediaQuery.matches);

    // Create event listener function
    const handleChange = (event: MediaQueryListEvent) => {
      setMatches(event.matches);
    };

    // Add event listener
    mediaQuery.addEventListener("change", handleChange);

    // Cleanup
    return () => {
      mediaQuery.removeEventListener("change", handleChange);
    };
  }, [query]); // Re-run effect if query changes

  return matches;
};

export default useMediaQuery;
