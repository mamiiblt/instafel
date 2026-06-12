/*
 * (c) 2026 Muhammed Ali Bulut, All rights reserved.
 *
 * See LICENSE file in repository root for copy file of license. For copyright
 * notices, technical issues, feedback, or any other related to this code file or
 * project, please contact me via mamii@mamii.dev or other ways.
 */

"use client";

import { Skeleton } from "@/components/ui/skeleton";
import { motion } from "framer-motion";

export function NavbarLoading() {
  return (
    <motion.div
      initial={{ y: -10, opacity: 0 }}
      animate={{ y: 0, opacity: 1 }}
      transition={{ duration: 0.3 }}
      className="sticky top-0 z-50 border-b border-border bg-background/95 backdrop-blur-sm supports-backdrop-filter:bg-background/60"
    >
      <div className="container mx-auto flex h-16 items-center justify-between px-4 sm:px-6">
        {/* Logo skeleton */}
        <div className="flex items-center space-x-2">
          <div className="flex items-center gap-2">
            <Skeleton className="h-6 w-6 rounded-full" />
            <Skeleton className="h-5 w-24" />
          </div>
        </div>

        {/* Center nav items with pulse animation */}
        <div className="hidden md:flex flex-1 justify-center">
          <div className="flex items-center space-x-4">
            {[...Array(4)].map((_, i) => (
              <motion.div
                key={i}
                initial={{ opacity: 0 }}
                animate={{ opacity: 1 }}
                transition={{
                  duration: 0.5,
                  delay: 0.1 * i,
                  repeat: Infinity,
                  repeatType: "reverse",
                  repeatDelay: 0.5,
                }}
              >
                <Skeleton key={i} className="h-8 w-20 rounded-md" />
              </motion.div>
            ))}
          </div>
        </div>

        {/* Right section - buttons and menu */}
        <div className="flex items-center space-x-3">
          {/* Download button (desktop only) */}
          <div className="hidden md:block">
            <Skeleton className="h-9 w-24 rounded-md" />
          </div>

          {/* GitHub button (desktop only) */}
          <div className="hidden md:block">
            <Skeleton className="h-9 w-9 rounded-md" />
          </div>

          {/* Mobile menu button */}
          <div className="md:hidden">
            <Skeleton className="h-9 w-9 rounded-md" />
          </div>
        </div>
      </div>

      {/* Animated loading bar at the bottom */}
      <motion.div
        initial={{ width: "0%", opacity: 0.7 }}
        animate={{ width: "100%", opacity: 0.7 }}
        transition={{
          duration: 1.5,
          repeat: Infinity,
          repeatType: "loop",
        }}
        className="h-[2px] bg-gradient-to-r from-primary/80 via-primary to-primary/80"
      />
    </motion.div>
  );
}
