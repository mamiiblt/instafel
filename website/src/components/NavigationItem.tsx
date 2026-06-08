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

"use client";

import * as React from "react";
import { NavigationMenuItem, NavigationMenuLink } from "./ui/navigation-menu";
import { LoadingSpinner } from "./ui/loading-spinner";
import { motion } from "framer-motion";

interface NavigationItemProps {
  href: string;
  title: string;
  isActive: boolean;
}

export function NavigationItem({ href, title, isActive }: NavigationItemProps) {
  const [isLoading, setIsLoading] = React.useState(false);
  const [isHovered, setIsHovered] = React.useState(false);

  return (
    <NavigationMenuItem>
      <motion.div whileHover={{ scale: 1.05 }} transition={{ duration: 0.2 }}>
        <NavigationMenuLink
          href={href}
          onClick={() => setIsLoading(true)}
          onMouseEnter={() => setIsHovered(true)}
          onMouseLeave={() => setIsHovered(false)}
          className={`
            relative flex items-center justify-center px-3 py-1.5 text-sm font-medium
            rounded-md transition-colors duration-300
            ${
              isActive
                ? "text-accent-foreground"
                : "text-foreground hover:text-foreground/80"
            }
          `}
        >
          {isLoading ? (
            <LoadingSpinner className="mr-2 h-4 w-4" />
          ) : (
            <>
              <span className="relative z-10">{title}</span>
              {isActive && (
                <motion.div
                  layoutId="navbar-active-indicator"
                  className="absolute inset-0 bg-accent rounded-md"
                  initial={{ opacity: 0 }}
                  animate={{ opacity: 1 }}
                  transition={{ duration: 0.3 }}
                />
              )}
              {isHovered && !isActive && (
                <motion.div
                  className="absolute inset-0 bg-accent/30 rounded-md"
                  initial={{ opacity: 0 }}
                  animate={{ opacity: 1 }}
                  exit={{ opacity: 0 }}
                  transition={{ duration: 0.15 }}
                />
              )}
            </>
          )}
        </NavigationMenuLink>
      </motion.div>
    </NavigationMenuItem>
  );
}
