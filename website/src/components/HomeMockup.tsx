/*
 * (c) 2026 Muhammed Ali Bulut, All rights reserved.
 *
 * See LICENSE file in repository root for copy file of license. For copyright
 * notices, technical issues, feedback, or any other related to this code file or
 * project, please contact me via mamii@mamii.dev or other ways.
 */

import { useTheme } from "next-themes";
import Image from "next/image";
import { useEffect, useState } from "react";

export default function HomeMockup() {
  const { theme, resolvedTheme } = useTheme();
  const [mounted, setMounted] = useState(false);

  useEffect(() => setMounted(true), []);

  if (!mounted) return null;

  const currentTheme = theme === "system" ? resolvedTheme : theme;
  const imageSrc =
    currentTheme === "dark"
      ? "/mockups/mockup_dark_t.png"
      : "/mockups/mockup_light_t.png";

  return (
    <Image
      src={imageSrc}
      alt="Instafel App Mockup"
      width={800}
      height={800}
      quality="100"
      className="w-full h-auto object-cover rounded-xl transition-transform duration-700"
    />
  );
}
