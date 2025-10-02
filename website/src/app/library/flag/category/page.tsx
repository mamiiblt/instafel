"use client";

import { AnimatePresence, motion } from "framer-motion";
import { useEffect, useState } from "react";
import { LoadingBar } from "@/components/LoadingBars";
import Footer from "@/components/Footer";
import { useSearchParams, useRouter } from "next/navigation";
import Link from "next/link";
import Navbar from "@/components/Navbar";
import { useTranslation } from "react-i18next";
import {contentAPIURL, flagCategories} from "@/wdata/flag_sdata";
import {
  Pagination,
  PaginationContent,
  PaginationItem,
  PaginationLink,
  PaginationNext,
  PaginationPrevious,
  PaginationEllipsis,
} from "@/components/ui/pagination";
import { Copy, Trash, ChevronLeft, ChevronRight } from "lucide-react";

interface RespTF {
  page: number;
  category_size: number;
  page_size: number;
  flags: {
    id: number;
    title: string;
    last_edit: string;
    added_by: string;
    removed_in: string;
    category_id?: number;
  }[];
}

export default function FlagListPage() {
  const { t, i18n } = useTranslation(["flags", "fcategories"]);
  const [hoveredId] = useState<number | null>(null);
  const searchParams = useSearchParams();
  const router = useRouter();
  const categoryID = Number(searchParams.get("cid")) ?? 0;
  const page = Number(searchParams.get("page")) ?? 1;
  const [data, setData] = useState<RespTF | null>(null);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const requestUrl =
          categoryID != 2589
            ? `${contentAPIURL}/content/flib/list?category_id=${categoryID}&page=${page}`
            : `${contentAPIURL}/content/flib/list_all?page=${page}`;
        const res = await fetch(requestUrl);
        const data: RespTF = await res.json();
        console.log(data);
        setData(data);
      } catch (e) {
        console.error("An error occurred while sending / reading response:", e);
      }
    };
    fetchData();
  }, [categoryID, page]);

  const categoryData = flagCategories[categoryID];

  const totalPages = data ? data.page_size : 0;
  const currentPage = data?.page || 1;

  const generatePageNumbers = () => {
    const pages = [];
    const maxVisiblePages = 5;

    if (totalPages <= maxVisiblePages) {
      for (let i = 1; i <= totalPages; i++) {
        pages.push(i);
      }
    } else {
      if (currentPage <= 3) {
        for (let i = 1; i <= 4; i++) {
          pages.push(i);
        }
        pages.push("ellipsis");
        pages.push(totalPages);
      } else if (currentPage >= totalPages - 2) {
        pages.push(1);
        pages.push("ellipsis");
        for (let i = totalPages - 3; i <= totalPages; i++) {
          pages.push(i);
        }
      } else {
        pages.push(1);
        pages.push("ellipsis");
        for (let i = currentPage - 1; i <= currentPage + 1; i++) {
          pages.push(i);
        }
        pages.push("ellipsis");
        pages.push(totalPages);
      }
    }

    return pages;
  };

  const handlePageChange = (newPage: number) => {
    const params = new URLSearchParams(searchParams.toString());
    params.set("page", newPage.toString());
    router.push(`?${params.toString()}`);
  };

  return (
    <AnimatePresence>
      {data ? (
        <div className="bg-background text-foreground">
          <Navbar />
          <div className="min-h-screen py-12 px-4 sm:px-6 lg:px-8">
            <div className="max-w-5xl mx-auto">
              <div className="text-center mb-16">
                <motion.div
                  initial={{ opacity: 0, y: 20 }}
                  animate={{ opacity: 1, y: 0 }}
                  transition={{
                    duration: 0.6,
                    ease: "easeOut",
                  }}
                  className="mb-6 flex justify-center"
                >
                  <div className="w-16 h-16 rounded-2xl bg-card border border/10 flex items-center justify-center backdrop-blur-sm">
                    {categoryData.icon}
                  </div>
                </motion.div>
                <motion.h1
                  initial={{ opacity: 0, y: 30 }}
                  animate={{ opacity: 1, y: 0 }}
                  transition={{
                    delay: 0.2,
                    duration: 0.8,
                    ease: "easeInOut",
                  }}
                  className="text-5xl font-bold tracking-tight mb-4 text-foreground"
                >
                  {t(categoryData.cif, { ns: "fcategories" })}
                </motion.h1>
                <motion.div
                  initial={{ opacity: 0, y: 30 }}
                  animate={{ opacity: 1, y: 0 }}
                  transition={{
                    delay: 0.3,
                    duration: 0.8,
                    ease: "easeInOut",
                  }}
                  className="flex items-center justify-center gap-3 text-muted-foreground"
                >
                  <div className="h-[1px] w-5" />
                  <p className="text-lg">
                    {t("flag_found", {
                      fcount: data.category_size,
                    })}
                  </p>
                  <div className="h-[1px] w-5" />
                </motion.div>
              </div>

              <motion.div
                initial={{ opacity: 0, y: 30 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ delay: 0.6, duration: 0.8, ease: "easeInOut" }}
                className="grid gap-4 mb-8"
              >
                <div className="grid gap-4 mb-6">
                  {data.flags.map((flag, index) => (
                    <Link
                      key={index}
                      href={`/library/flag/view?id=${flag.id}`}
                      className="group relative"
                    >
                      <div
                        className={`
          relative overflow-hidden bg-card rounded-xl border border-border
          transition-all duration-300
          ${hoveredId === index ? "shadow-lg scale-[1.01]" : "hover:shadow-sm"}
        `}
                      >
                        <div className="p-4 sm:p-6">
                          <div className="flex items-start justify-between">
                            <div className="flex-12">
                              <div className="flex items-center gap-3 mb-2">
                                <h3 className="text-lg font-semibold text-foreground">
                                  {flag.title}
                                </h3>
                              </div>
                              <div className="items-center gap-4 text-sm text-muted-foreground">
                                <div className="flex items-center gap-2">
                                  <svg
                                    className="w-4 h-4"
                                    fill="none"
                                    stroke="currentColor"
                                    viewBox="0 0 24 24"
                                  >
                                    <path
                                      strokeLinecap="round"
                                      strokeLinejoin="round"
                                      strokeWidth={1.5}
                                      d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"
                                    />
                                  </svg>
                                  {t("added_by", { name: flag.added_by })}
                                </div>
                                <div className="flex items-center gap-2">
                                  <svg
                                    className="w-4 h-4"
                                    fill="none"
                                    stroke="currentColor"
                                    viewBox="0 0 24 24"
                                  >
                                    <path
                                      strokeLinecap="round"
                                      strokeLinejoin="round"
                                      strokeWidth={1.5}
                                      d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"
                                    />
                                  </svg>
                                  {t("uploaded_at", {
                                    date_str: new Intl.DateTimeFormat(
                                      i18n.language,
                                      {
                                        dateStyle: "medium",
                                        timeStyle: "short",
                                      }
                                    ).format(new Date(flag.last_edit)),
                                  })}
                                </div>
                                {flag.removed_in != null && (
                                  <div className="flex items-center gap-2">
                                    <Trash className="w-4 h-4" />
                                    {t("removed_with", {
                                      version: flag.removed_in,
                                    })}
                                  </div>
                                )}
                                {flag.category_id != null && (
                                  <div className="flex items-center gap-2">
                                    <Copy className="w-4 h-4" />
                                    {t(
                                      `${flagCategories[flag.category_id].cif}`,
                                      {
                                        ns: "fcategories",
                                      }
                                    )}
                                  </div>
                                )}
                              </div>
                            </div>
                          </div>
                        </div>
                        <div
                          className={`
              absolute inset-0
              bg-gradient-to-tr from-muted/0 via-muted/0 to-muted/50
              transition-opacity duration-300
            `}
                        />
                      </div>
                    </Link>
                  ))}
                </div>
              </motion.div>

              {totalPages > 1 && (
                <motion.div
                  initial={{ opacity: 0, y: 20 }}
                  animate={{ opacity: 1, y: 0 }}
                  transition={{ delay: 0.8, duration: 0.6 }}
                  className="flex justify-center"
                >
                  <Pagination>
                    <PaginationContent className="flex items-center gap-1">
                      <PaginationItem>
                        <PaginationPrevious
                          onClick={() =>
                            currentPage > 1 && handlePageChange(currentPage - 1)
                          }
                          className={`
                            flex items-center gap-2 px-3 py-2 rounded-lg transition-all duration-200
                            ${
                              currentPage <= 1
                                ? "opacity-50 cursor-not-allowed"
                                : "hover:bg-muted cursor-pointer"
                            }
                          `}
                        >
                          <ChevronLeft className="h-4 w-4" />
                          <span className="hidden sm:inline">Previous</span>
                        </PaginationPrevious>
                      </PaginationItem>

                      {generatePageNumbers().map((pageNum, index) => (
                        <PaginationItem key={index}>
                          {pageNum === "ellipsis" ? (
                            <PaginationEllipsis className="px-3 py-2" />
                          ) : (
                            <PaginationLink
                              onClick={() =>
                                handlePageChange(pageNum as number)
                              }
                              isActive={currentPage === pageNum}
                              className={`
                                px-3 py-2 rounded-lg transition-all duration-200 cursor-pointer
                                ${
                                  currentPage === pageNum
                                    ? "bg-primary text-foreground shadow-sm"
                                    : "hover:bg-muted"
                                }
                              `}
                            >
                              {pageNum}
                            </PaginationLink>
                          )}
                        </PaginationItem>
                      ))}

                      <PaginationItem>
                        <PaginationNext
                          onClick={() =>
                            currentPage < totalPages &&
                            handlePageChange(currentPage + 1)
                          }
                          className={`
                            flex items-center gap-2 px-3 py-2 rounded-lg transition-all duration-200
                            ${
                              currentPage >= totalPages
                                ? "opacity-50 cursor-not-allowed"
                                : "hover:bg-muted cursor-pointer"
                            }
                          `}
                        >
                          <span className="hidden sm:inline">Next</span>
                          <ChevronRight className="h-4 w-4" />
                        </PaginationNext>
                      </PaginationItem>
                    </PaginationContent>
                  </Pagination>
                </motion.div>
              )}

              {/* Page Info */}
              {totalPages > 1 && (
                <motion.div
                  initial={{ opacity: 0 }}
                  animate={{ opacity: 1 }}
                  transition={{ delay: 1, duration: 0.6 }}
                  className="text-center mt-6 text-sm text-muted-foreground"
                >
                  {t("page_info", {
                    current: currentPage,
                    total: totalPages,
                    defaultValue: `Page ${currentPage} of ${totalPages}`,
                  })}
                </motion.div>
              )}
            </div>
          </div>
          <Footer />
        </div>
      ) : (
        <LoadingBar />
      )}
    </AnimatePresence>
  );
}
