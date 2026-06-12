/*
 * (c) 2026 Muhammed Ali Bulut, All rights reserved.
 *
 * See LICENSE file in repository root for copy file of license. For copyright
 * notices, technical issues, feedback, or any other related to this code file or
 * project, please contact me via mamii@mamii.dev or other ways.
 */

"use client";

import { motion } from "framer-motion";
import { ChevronLeft, ChevronRight, Tag } from "lucide-react";
import { WikiPage } from "@/wdata/wiki";
import MarkdownRenderer from "@/components/MarkdownRenderer";
import Link from "next/link";
import { Card, CardContent, CardHeader, CardTitle } from "../ui/card";

interface WikiContentProps {
  page: WikiPage;
  content: string;
  proviData: ProviPage;
}

interface ProviPage {
  prev: WikiPage;
  next: WikiPage;
}

export default function WikiContent({
  page,
  content,
  proviData,
}: WikiContentProps) {
  return (
    <motion.div
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.5 }}
      className="flex-1 p-8 max-w-4xl mx-auto"
    >
      <div className="mb-8">
        <motion.h1
          initial={{ opacity: 0, y: -20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.1 }}
          className="text-4xl font-bold mb-2"
        >
          {page.title}
        </motion.h1>

        {page.description && (
          <motion.p
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            transition={{ delay: 0.2 }}
            className="text-lg text-slate-600 text-muted-foreground mb-4"
          >
            {page.description}
          </motion.p>
        )}
      </div>

      <MarkdownRenderer content={content} imgSrc={"/wiki-imgs"} />

      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ delay: 0.3, duration: 0.5 }}
        className="grid grid-cols-1 md:grid-cols-2 gap-4 mt-12 pt-8 border-t"
      >
        {proviData.prev ? (
          <Link href={"/wiki/" + proviData.prev.slug} className="block">
            <Card className="h-full transition-all duration-200 hover:shadow-md hover:-translate-y-1 cursor-pointer group">
              <CardHeader className="pb-3">
                <div className="flex items-center gap-2 text-sm text-muted-foreground mb-1">
                  <ChevronLeft className="h-4 w-4" />
                  <span>Previous</span>
                </div>
                <CardTitle className="text-lg group-hover:text-primary transition-colors">
                  {proviData.prev.title}
                </CardTitle>
              </CardHeader>
              {proviData.prev.description && (
                <CardContent className="pt-0">
                  <p className="text-sm text-muted-foreground line-clamp-2">
                    {proviData.prev.description}
                  </p>
                </CardContent>
              )}
            </Card>
          </Link>
        ) : (
          <div></div>
        )}

        {proviData.next && (
          <Link href={"/wiki/" + proviData.next.slug} className="block">
            <Card className="h-full transition-all duration-200 hover:shadow-md hover:-translate-y-1 cursor-pointer group">
              <CardHeader className="pb-3">
                <div className="flex items-center justify-end gap-2 text-sm text-muted-foreground mb-1">
                  <span>Next</span>
                  <ChevronRight className="h-4 w-4" />
                </div>
                <CardTitle className="text-lg text-right group-hover:text-primary transition-colors">
                  {proviData.next.title}
                </CardTitle>
              </CardHeader>
              {proviData.next.description && (
                <CardContent className="pt-0">
                  <p className="text-sm text-muted-foreground line-clamp-2 text-right">
                    {proviData.next.description}
                  </p>
                </CardContent>
              )}
            </Card>
          </Link>
        )}
      </motion.div>
    </motion.div>
  );
}
