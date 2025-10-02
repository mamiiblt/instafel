"use client";

import { AnimatePresence, motion } from "framer-motion";
import React, {useEffect, useState} from "react";
import Footer from "@/components/Footer";
import { useTranslation } from "react-i18next";
import Navbar from "@/components/Navbar";
import {contentAPIURL} from "@/wdata/flag_sdata";
import { LoadingBar } from "@/components/LoadingBars";
import {useRouter, useSearchParams} from "next/navigation";
import {CardTitle} from "@/components/ui/card";
import {
    AlertCircle,
    ChevronLeft,
    ChevronRight,
    Package,
    Sparkles, Star
} from "lucide-react";
import {Badge} from "@/components/ui/badge";
import {
    Pagination,
    PaginationContent,
    PaginationEllipsis,
    PaginationItem, PaginationLink, PaginationNext,
    PaginationPrevious
} from "@/components/ui/pagination";
import {ReleaseInfo, ReleaseListCard, ReleaseListInfoCard} from "@/components/release/ReleaseListComps";

export default function ReleasesPage() {
    const { t } = useTranslation(["releases"]);
    const searchParams = useSearchParams();
    const currentPage = Number(searchParams.get("page")) ?? 1;
    const router = useRouter();
    const [data, setData] = useState<{
        manifest_version: number;
        page_size: number;
        releases: ReleaseInfo[]
    } | null>(null);

    useEffect(() => {
        const fetchData = async () => {
            var requestUrl = `${contentAPIURL}/content/rels/list?page=${currentPage}`;
            const res = await fetch(requestUrl);
            const result = await res.json();

            const releases = result.releases.map((release) => ({
                iflVersion: release[0].toString(),
                iflGenID: release[1],
                igVersion: release[2],
                releaseDate: release[3],
                isDeleted: release[4],
                changelogs: release[5],
                patcherVersion: release[6]
            }));

            setData({
                manifest_version: result.manifest_version,
                page_size: result.page_size,
                releases: releases
            });
        };
        fetchData();
    }, [currentPage]);

    const container = {
        hidden: { opacity: 0 },
        show: {
            opacity: 1,
            transition: {
                staggerChildren: 0.1
            }
        }
    };



    const generatePageNumbers = () => {
        const pages = [];
        const maxVisiblePages = 5;

        if (data.page_size <= maxVisiblePages) {
            for (let i = 1; i <= data.page_size; i++) {
                pages.push(i);
            }
        } else {
            if (currentPage <= 3) {
                for (let i = 1; i <= 4; i++) {
                    pages.push(i);
                }
                pages.push("ellipsis");
                pages.push(data.page_size);
            } else if (currentPage >= data.page_size - 2) {
                pages.push(1);
                pages.push("ellipsis");
                for (let i = data.page_size - 3; i <= data.page_size; i++) {
                    pages.push(i);
                }
            } else {
                pages.push(1);
                pages.push("ellipsis");
                for (let i = currentPage - 1; i <= currentPage + 1; i++) {
                    pages.push(i);
                }
                pages.push("ellipsis");
                pages.push(data.page_size);
            }
        }

        return pages;
    };

    const handlePageChange = (newPage: number) => {
        const params = new URLSearchParams(searchParams.toString());
        params.set("page",  newPage.toString());
        router.push(`?${params.toString()}`);
    };

  return (
    <AnimatePresence>
      {data ? (
        <div>
            <Navbar />
            <div className="container max-w-6xl mx-auto py-8 px-4">
                {currentPage == 1 && <div className="text-center space-y-6 mb-12 py-12">
                    <motion.div
                        initial={{ opacity: 0, x: -20 }}
                        animate={{ opacity: 1, x: 0 }}
                        transition={{ duration: 0.5 }}
                    >
                        <h1 className="text-5xl md:text-6xl font-bold tracking-tight">
                            {t("title")}
                        </h1>
                        <p className="text-xl text-muted-foreground max-w-2xl mt-3 mx-auto leading-relaxed">
                            {t("subtitle")}
                        </p>
                    </motion.div>
                </div>}

                <motion.div
                    variants={container}
                    initial="hidden"
                    animate="show"
                    className="space-y-4"
                >
                    {data.releases.map((release, index) => (
                        <div key={index}>
                            {currentPage == 1 && index == 0 ?
                                <ReleaseListCard
                                    release={release}
                                    isLatest={true}
                                    cardBody={<div className="space-y-2 flex-1">
                                    <Badge className="mb-2">
                                        <Sparkles className="mr-1 h-3 w-3" />
                                        {t("latestVersion")}
                                    </Badge>

                                    <CardTitle className="flex items-center gap-2 text-3xl font-bold">
                                        <Package className="h-7 w-7" />
                                        {t("releaseText", { version: release.iflVersion })}
                                    </CardTitle>

                                    <ReleaseListInfoCard release={release} />
                                </div>} />
                                : <ReleaseListCard
                                    release={release}
                                    isLatest={false}
                                    cardBody={<div className="space-y-2 flex-1">
                                    <div className="flex items-center gap-2 flex-wrap">
                                        <CardTitle className="flex items-center gap-2">
                                            <Package className="h-5 w-5" />
                                            {t("releaseText", { version: release.iflVersion })}
                                        </CardTitle>
                                        {release.isDeleted && (
                                            <Badge variant="destructive">
                                                <AlertCircle className="mr-1 h-3 w-3" />
                                                {t("deleted")}
                                            </Badge>
                                        )}
                                    </div>

                                    <ReleaseListInfoCard release={release} />
                                </div>} />}
                        </div>
                    ))}
                </motion.div>

                <motion.div
                    initial={{ opacity: 0, y: 20 }}
                    animate={{ opacity: 1, y: 0 }}
                    transition={{ delay: 0.8, duration: 0.6 }}
                    className="flex justify-center mt-6"
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
                                        currentPage < data.page_size &&
                                        handlePageChange(currentPage + 1)
                                    }
                                    className={`
                            flex items-center gap-2 px-3 py-2 rounded-lg transition-all duration-200
                            ${
                                        currentPage >= data.page_size
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
            </div>
            <Footer />
        </div>
      ) : (
        <>
          <Navbar />
          <LoadingBar />
          <Footer />
        </>
      )}
    </AnimatePresence>
  );
}