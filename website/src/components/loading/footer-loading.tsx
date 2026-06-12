/*
 * (c) 2026 Muhammed Ali Bulut, All rights reserved.
 *
 * See LICENSE file in repository root for copy file of license. For copyright
 * notices, technical issues, feedback, or any other related to this code file or
 * project, please contact me via mamii@mamii.dev or other ways.
 */

"use client";

import { Skeleton } from "@/components/ui/skeleton";
import { motion, Variants } from "framer-motion";

export function FooterLoading() {
  const pulseAnimation: Variants = {
    initial: { opacity: 0.6 },
    animate: {
      opacity: 1,
      transition: {
        repeat: Infinity,
        repeatType: "reverse" as const,
        duration: 0.8,
      },
    },
  };

  return (
    <footer className="relative border-t border-border bg-gradient-to-b from-background to-background/50 backdrop-blur-xs">
      <div className="relative container mx-auto px-4 py-8">
        <div className="max-w-6xl mx-auto">
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-8">
            <div className="space-y-4 lg:col-span-2">
              <div className="flex items-center gap-2">
                <motion.div
                  variants={pulseAnimation}
                  initial="initial"
                  animate="animate"
                >
                  <Skeleton className="w-10 h-10 rounded-xl" />
                </motion.div>
                <motion.div
                  variants={pulseAnimation}
                  initial="initial"
                  animate="animate"
                >
                  <Skeleton className="h-5 w-32" />
                </motion.div>
              </div>
              <motion.div
                variants={pulseAnimation}
                initial="initial"
                animate="animate"
              >
                <Skeleton className="h-4 w-3/4" />
                <Skeleton className="h-4 w-1/2 mt-2" />
              </motion.div>
            </div>

            <div>
              <motion.div
                variants={pulseAnimation}
                initial="initial"
                animate="animate"
              >
                <Skeleton className="h-6 w-32 mb-4" />
              </motion.div>
              <div className="space-y-3">
                {[...Array(3)].map((_, i) => (
                  <motion.div
                    key={i}
                    variants={pulseAnimation}
                    initial="initial"
                    animate="animate"
                    transition={{ delay: i * 0.1 }}
                  >
                    <Skeleton className="h-4 w-full max-w-[180px]" />
                  </motion.div>
                ))}
              </div>
            </div>

            <div>
              <motion.div
                variants={pulseAnimation}
                initial="initial"
                animate="animate"
              >
                <Skeleton className="h-6 w-32 mb-4" />
              </motion.div>
              <div className="space-y-3">
                {[...Array(3)].map((_, i) => (
                  <motion.div
                    key={i}
                    variants={pulseAnimation}
                    initial="initial"
                    animate="animate"
                    transition={{ delay: i * 0.1 }}
                  >
                    <Skeleton className="h-4 w-full max-w-[180px]" />
                  </motion.div>
                ))}
              </div>
            </div>
          </div>

          <div className="mt-8 pt-8 border-t border-border">
            <div className="flex flex-col md:flex-row justify-between items-center gap-4">
              <motion.div
                variants={pulseAnimation}
                initial="initial"
                animate="animate"
              >
                <Skeleton className="h-4 w-48" />
              </motion.div>
              <div className="flex items-center gap-4">
                {[...Array(3)].map((_, i) => (
                  <motion.div
                    key={i}
                    variants={pulseAnimation}
                    initial="initial"
                    animate="animate"
                    transition={{ delay: i * 0.1 }}
                  >
                    <Skeleton className="h-5 w-5 rounded-full" />
                  </motion.div>
                ))}
              </div>
            </div>
          </div>
        </div>
      </div>
    </footer>
  );
}
