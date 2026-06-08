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

import { LucideIcon } from "lucide-react";

export interface WikiPage {
  slug: string;
  title: string;
  description: string;
  writer: string;
}

export interface WikiSubCategory {
  name: string;
  icon: LucideIcon;
  pages: WikiPage[];
}

export interface WikiCategory {
  name: string;
  subs: WikiSubCategory[];
}
