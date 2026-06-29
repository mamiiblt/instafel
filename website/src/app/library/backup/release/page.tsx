/*
 * (c) 2026 Muhammed Ali Bulut, All rights reserved.
 *
 * See LICENSE file in repository root for copy file of license. For copyright
 * notices, technical issues, feedback, or any other related to this code file or
 * project, please contact me via mamii@mamii.dev or other ways.
 */

"use client";

import {motion} from "framer-motion";
import React, {useEffect, useState} from "react";
import {Button} from "@/components/ui/button";
import {useSearchParams} from "next/navigation";
import {LoadingBar} from "@/components/LoadingBars";
import Footer from "@/components/Footer";
import {HugeiconsIcon} from "@hugeicons/react";
import {
    Download01Icon,
    Time04Icon,
    InformationCircleIcon,
    SmartPhone01Icon,
    UserIcon,
    CheckmarkCircle02Icon, CircleQuestionMarkIcon, LibraryIcon, Sparkles, Package01Icon, Github01Icon, TelegramIcon,
    Link01Icon, File01Icon, PackageProcess01Icon, InstagramIcon, ChevronRightIcon,
} from "@hugeicons/core-free-icons";
import Link from "next/link";
import {useTranslation} from "react-i18next";
import Navbar from "@/components/Navbar";

import ScreenshotViewer from "@/components/ScreenshotViewer";
import {SocialButton, SocialButton2} from "@/app/contributors/page";
import {Card, CardFooter} from "@/components/ui/card";
import {Calendar, Download, ExternalLink, InfoIcon, ScrollText, Tag, ViewIcon, X} from "lucide-react";
import {formatDate, formatDateWithTime} from "../page";
import {Dialog, DialogContent, DialogHeader, DialogTitle} from "@/components/ui/dialog";
import {
    Pagination,
    PaginationContent,
    PaginationEllipsis,
    PaginationItem, PaginationLink, PaginationNext,
    PaginationPrevious
} from "@/components/ui/pagination";

interface ReleaseResponse {
    admin_info: {
        username: string;
        pp_url: string;
    },
    backup_info: {
        id: number,
        author_id: number,
        name: string,
        description: string,
        shown_author_name: string,
        screenshots: string[],
        socials: {
            gh: string | null,
            tg: string | null,
            web: string | null,
        }
    },
    release_info: {
        is_latest: boolean,
        version_name: string,
        version_code: number,
        changelog: string,
        download_count: number,
        total_download_count: number,
        release_date: string,
        release_count: number
        pref_ig_ver: string | null
    }
}

interface LastReleaseInfo {
    release_id: string
    version_name: string
    release_date: string
    download_count: number
    changelog: string
    preferred_ig_version: string | null
}

interface LastReleasesResponse {
    page: number;
    page_size: number;
    list: LastReleaseInfo[]
}

export default function PageBackup() {
    const { t, i18n } = useTranslation("backup");
    const searchParams = useSearchParams();
    const id = searchParams.get("rid") ?? "null";
    const [data, setData] = useState<ReleaseResponse | null>(null);
    const [lastReleases, setLastReleases] = useState<LastReleasesResponse | null>(null);
    const [isLoading, setIsLoading] = useState(true);
    const [downloadStarted, setDownloadStarted] = useState(false);
    const [importStarted, setImportStarted] = useState(false);

    const [selectedRelease, setSelectedRelease] = useState<LastReleaseInfo | null>(null)
    const [showChangelog, setShowChangelog] = useState(false)

    const [lastPage, setLastPage] = useState<number>(1);

    useEffect(() => {
        const fetchData = async () => {
            try {
                const request_ReleaseInfo = `${process.env.API_BASE}/content/backups/release_info?release_id=${id}`;
                const res_ReleaseInfo = await fetch(request_ReleaseInfo);
                const result_ReleaseInfo = await res_ReleaseInfo.json();
                setData(result_ReleaseInfo.data.info as ReleaseResponse);

                await fetchLastReleases(1, parseInt(result_ReleaseInfo.data.info.backup_info.id));
            } catch (error) {
                console.error(t("errors.fetchFailed", {errStr: error}));
            } finally {
                setIsLoading(false);
            }
        };
        fetchData();
    }, [id, t]);

    useEffect(() => {
        const renewData = async () => {
            try {
                if (data != null) {
                    await fetchLastReleases(lastPage, data.backup_info.id)
                }
            } catch (e) {
                console.error(e)
            }
        }
        renewData()
    }, [lastPage]);

    const fetchLastReleases = async (page: number, backup_id: number) => {
        const request_LastRelease = `${process.env.API_BASE}/content/backups/last_releases?page=${page}&backup_id=${backup_id}`;
        const res_LastRelease = await fetch(request_LastRelease);
        const result_LastRelease = await res_LastRelease.json();
        setLastReleases(result_LastRelease.data as LastReleasesResponse);
    }

    const handleDownloadBackup = (id: string) => {
        setDownloadStarted(true);
        const link = document.createElement("a");
        link.href = `${process.env.API_BASE}/content/backups/generate_ibackup?release_id=${id}`;
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
        setTimeout(() => {
            setDownloadStarted(false);
        }, 2500);
    };

    const handleImportInstafel = () => {
        setImportStarted(true);
        setTimeout(() => {
            setImportStarted(false);
        }, 2500);
    };

    const generatePageNumbers = () => {
        const pages = [];
        const maxVisiblePages = 5;

        if (lastReleases.page_size <= maxVisiblePages) {
            for (let i = 1; i <= lastReleases.page_size; i++) {
                pages.push(i);
            }
        } else {
            if (lastPage <= 3) {
                for (let i = 1; i <= 4; i++) {
                    pages.push(i);
                }
                pages.push("ellipsis");
                pages.push(lastReleases.page_size);
            } else if (lastPage >= lastReleases.page_size - 2) {
                pages.push(1);
                pages.push("ellipsis");
                for (let i = lastReleases.page_size - 3; i <= lastReleases.page_size; i++) {
                    pages.push(i);
                }
            } else {
                pages.push(1);
                pages.push("ellipsis");
                for (let i = lastPage - 1; i <= lastPage + 1; i++) {
                    pages.push(i);
                }
                pages.push("ellipsis");
                pages.push(lastReleases.page_size);
            }
        }

        return pages;
    };

    if (isLoading) {
        return <LoadingBar/>;
    }

    if (!data) {
        return (
            <>
                <div className="mx-auto flex min-h-[60vh] w-full max-w-7xl items-center justify-center px-4 py-12">
                    <motion.div
                        initial={{opacity: 0, y: 20}}
                        animate={{opacity: 1, y: 0}}
                        transition={{duration: 0.5}}
                        className="w-full max-w-md rounded-3xl border border-border bg-card/60 p-8 text-center backdrop-blur-xl"
                    >
                        <div
                            className="mx-auto mb-5 flex h-12 w-12 items-center justify-center rounded-2xl border border-border bg-background/60">
                            <HugeiconsIcon
                                icon={InformationCircleIcon}
                                className="h-6 w-6 text-primary"
                            />
                        </div>
                        <h2 className="mb-3 text-xl font-bold">{t("notFound.title")}</h2>
                        <p className="mb-6 text-pretty leading-relaxed text-muted-foreground">
                            {t("notFound.description")}
                        </p>
                        <Button asChild className="rounded-full px-6">
                            <Link href="/library/backup">{t("notFound.returnButton")}</Link>
                        </Button>
                    </motion.div>
                </div>
                <Footer/>
            </>
        );
    }

    return (
        <>
            <Navbar/>
            <div className="mx-auto w-full max-w-7xl px-4 py-8">
                <motion.section
                    initial={{opacity: 0, y: 20}}
                    animate={{opacity: 1, y: 0}}
                    transition={{duration: 0.5}}
                    className="relative overflow-hidden rounded-3xl border border-border bg-card/60 p-6 backdrop-blur-xl shadow-md sm:p-8"
                >
                    <div className="pointer-events-none absolute inset-x-0 top-0 h-px bg-gradient-to-r from-transparent via-border to-transparent"/>

                    <div className="flex flex-col justify-between gap-6 md:flex-row md:items-center">
                        <div>
                            <h1 className="text-balance text-2xl font-bold sm:text-3xl">
                                {data.backup_info.name}
                            </h1>
                            <p className="mt-2 text-pretty leading-relaxed text-muted-foreground/80">
                                {data.backup_info.description}
                            </p>
                        </div>

                        <div className="flex flex-col gap-3 ">
                            <motion.div
                                whileHover={{y: -2}}
                                transition={{type: "spring", stiffness: 400, damping: 25}}
                            >
                                <Button
                                    onClick={() =>
                                        handleDownloadBackup(id)
                                    }
                                    disabled={downloadStarted}
                                    className="w-full rounded-full px-6"
                                >
                                    <HugeiconsIcon
                                        icon={downloadStarted ? CheckmarkCircle02Icon : Download01Icon}
                                        className="mr-2 h-4 w-4"
                                    />
                                    {downloadStarted ? t("downloading") : t("downloadButton")}
                                </Button>
                            </motion.div>

                            <motion.div
                                whileHover={{y: -2}}
                                transition={{type: "spring", stiffness: 400, damping: 25}}
                            >
                                <Button
                                    onClick={handleImportInstafel}
                                    disabled={importStarted}
                                    variant="secondary"
                                    className="w-full rounded-full border-border bg-background/40 px-6 backdrop-blur"
                                >
                                    <HugeiconsIcon
                                        icon={SmartPhone01Icon}
                                        className="mr-2 h-4 w-4"
                                    />
                                    {importStarted ? t("openingInstafel") : t("importButton")}
                                </Button>
                            </motion.div>
                        </div>
                    </div>
                </motion.section>

                <div className="mt-6 grid grid-cols-1 gap-6 lg:grid-cols-3">
                    <div className="flex flex-col gap-6 lg:col-span-2">
                        {/* About Backup */}
                        <motion.section
                            initial={{ opacity: 0, y: 16 }}
                            animate={{ opacity: 1, y: 0 }}
                            transition={{ duration: 0.45, delay: 0.08 }}
                            className="relative overflow-hidden"
                        >
                            <div className="relative z-10">
                                <div className="grid grid-cols-1 gap-6 lg:grid-cols-3">
                                    <div className="bg-card/60 relative w-full overflow-hidden flex flex-row sm:flex-col items-center sm:items-center justify-center text-left sm:text-center rounded-2xl border border-border p-6 gap-4">
                                        <div className="flex-shrink-0">
                                            <img
                                                src={data.admin_info.pp_url}
                                                alt={data.backup_info.shown_author_name}
                                                className="h-20 w-20 shrink-0 rounded-full object-cover shadow-lg ring-3 ring-border"
                                                loading="lazy"
                                            />
                                        </div>

                                        <div className="flex flex-col items-start sm:items-center pr-4 md:pr-0">
                                            <h3 className="truncate text-xl font-bold leading-tight text-foreground">
                                                {data.backup_info.shown_author_name}
                                            </h3>
                                            <p className="truncate text-sm font-regular text-muted-foreground mb-2 md:mb-4 lg:mb-4 mt-1">
                                                {t("authorOfBackup")}
                                            </p>

                                            <div className="flex items-center justify-start sm:justify-center gap-3">
                                                {data.backup_info.socials.gh && (
                                                    <SocialButton
                                                        label={"GitHub"}
                                                        icon={<HugeiconsIcon icon={Github01Icon} className="h-4 w-4" />}
                                                        href={`https://github.com/${data.backup_info.socials.gh}`}
                                                    />
                                                )}

                                                {data.backup_info.socials.tg && (
                                                    <SocialButton
                                                        label={"Telegram"}
                                                        icon={<HugeiconsIcon icon={TelegramIcon} className="h-4 w-4" />}
                                                        href={`https://t.me/${data.backup_info.socials.tg}`}
                                                    />
                                                )}

                                                {data.backup_info.socials.web && (
                                                    <SocialButton
                                                        label={"Website"}
                                                        icon={<HugeiconsIcon icon={Link01Icon} className="h-4 w-4" />}
                                                        href={
                                                            data.backup_info.socials.web.startsWith("http")
                                                                ? data.backup_info.socials.web
                                                                : `https://${data.backup_info.socials.web}`
                                                        }
                                                    />
                                                )}
                                            </div>
                                        </div>
                                    </div>

                                    <div className="grid grid-cols-1 sm:grid-cols-2 lg:col-span-2 gap-3 sm:gap-4">
                                        <div className="group flex flex-col justify-center p-4 rounded-3xl border border-border bg-card/60">
                                            <div className="flex items-start sm:items-center gap-2 text-muted-foreground mb-3">
                                                <HugeiconsIcon icon={Package01Icon} className="h-4 w-4 text-primary shrink-0 mt-0.5 sm:mt-0" />
                                                <p className="font-bold tracking-wider leading-tight">
                                                    {t("version_label")}
                                                </p>
                                                {data.release_info.is_latest && (
                                                    <span className="self-start sm:self-auto inline-flex items-center gap-1 rounded-md bg-primary/10 px-2 py-1 text-[10px] font-bold text-primary shrink-0">
                                                        <HugeiconsIcon icon={Sparkles} className="h-3 w-3" />
                                                        <span>{t("latestRelease")}</span>
                                                    </span>
                                                )}
                                            </div>
                                            <p className="text-2xl font-bold tracking-tight text-foreground truncate">
                                                <span className="text-foreground/65">v</span>{data.release_info.version_name}
                                            </p>
                                        </div>

                                        <div className="group flex flex-col justify-center p-4 rounded-3xl border border-border bg-card/60">
                                            <div className="flex items-start sm:items-center gap-2 text-muted-foreground mb-3">
                                                <HugeiconsIcon icon={Download01Icon} className="h-4 w-4 text-primary shrink-0 mt-0.5 sm:mt-0" />
                                                <p className="font-bold tracking-wider leading-tight">
                                                    {t("downloads_label")}
                                                </p>
                                            </div>
                                            <p className="text-2xl font-bold tracking-tight text-foreground truncate">
                                                {data.release_info.total_download_count.toLocaleString()}
                                            </p>
                                        </div>

                                        <div className="group flex flex-col justify-center p-4 rounded-3xl border border-border bg-card/60">
                                            <div className="flex items-start sm:items-center gap-2 text-muted-foreground mb-3">
                                                <HugeiconsIcon icon={InformationCircleIcon} className="h-4 w-4 text-primary shrink-0 mt-0.5 sm:mt-0" />
                                                <p className="font-bold tracking-wider leading-tight">
                                                    {t("release_date_label")}
                                                </p>
                                            </div>
                                            <p className="text-xl font-bold tracking-tight text-foreground truncate">
                                                {formatDateWithTime(data.release_info.release_date, i18n.language)}
                                            </p>
                                        </div>

                                        <div className="group flex flex-col justify-center p-4 rounded-3xl border border-border bg-card/60">
                                            <div className="flex items-start sm:items-center gap-2 text-muted-foreground mb-3">
                                                <HugeiconsIcon icon={InstagramIcon} className="h-4 w-4 text-primary shrink-0 mt-0.5 sm:mt-0 " />
                                                <p className="font-bold tracking-wider leading-tight">
                                                    {t("suggested_ig_version_label")}
                                                </p>
                                            </div>
                                            <p className="text-2xl font-bold tracking-tight text-foreground truncate">
                                                {data.release_info.pref_ig_ver == null ? t("not_specified") : <>
                                                    <span className="text-foreground/65">v</span>{data.release_info.pref_ig_ver}
                                                </>}
                                            </p>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </motion.section>

                        {/* Changelog */}
                        <motion.section
                            initial={{opacity: 0, y: 20}}
                            animate={{opacity: 1, y: 0}}
                            transition={{duration: 0.5, delay: 0.2}}
                            className="rounded-3xl border border-border bg-card/60 p-6 shadow-sm backdrop-blur-xl"
                        >
                            <h2 className="mb-4 flex items-center gap-2 text-lg font-semibold">
                                <HugeiconsIcon
                                    icon={Time04Icon}
                                    className="h-5 w-5 text-primary"
                                />
                                {t("changelogTitle")}
                            </h2>

                            <div className="flex flex-col gap-2 space-y-1 pl-1 mt-2.5">
                                {data.release_info.changelog.split('\n').filter(Boolean).map((change, index) => (
                                    <motion.div
                                        key={index}
                                        initial={{opacity: 0, x: -5}}
                                        animate={{opacity: 1, x: 0}}
                                        transition={{
                                            delay: 0.2 + 0.06 + (index * 0.05),
                                            duration: 0.3
                                        }}
                                        className="flex items-start gap-4"
                                    >
                                        <div className="w-1.5 h-1.5 bg-primary rounded-full mt-2.5 flex-shrink-0"></div>

                                        <p className="flex-1 text-sm leading-relaxed text-foreground/90 whitespace-pre-wrap">
                                            {change}
                                        </p>
                                    </motion.div>
                                ))}
                            </div>
                        </motion.section>

                        {/* Recent Releases */}
                        <motion.section
                            initial={{opacity: 0, y: 20}}
                            animate={{opacity: 1, y: 0}}
                            transition={{duration: 0.5, delay: 0.2}}
                            className="rounded-3xl border border-border bg-card/60 p-6 shadow-sm backdrop-blur-xl"
                        >
                            <h2 className="mb-4 flex items-center gap-2 text-lg font-semibold">
                                <HugeiconsIcon
                                    icon={PackageProcess01Icon}
                                    className="h-5 w-5 text-primary"
                                />
                                {t("recents.title")}
                            </h2>
                            <div className="space-y-6 overflow-y-auto [&::-webkit-scrollbar]:hidden [scrollbar-width:none]">
                                <div>
                                    {lastReleases == undefined || lastReleases.list.length == 0 ? (
                                        <div className="text-center py-12">
                                            <X className="size-12 text-muted-foreground mx-auto mb-4" />
                                            <p className="text-muted-foreground">{t("recents.noAnyRelease")}</p>
                                        </div>
                                    ) : (
                                        <>
                                            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-2 p-1 gap-4">
                                                {lastReleases.list.map((release) => {
                                                    return ((
                                                        <Card key={release.release_id} className="flex flex-col">
                                                            <div className="mt-auto px-(--card-spacing) space-y-1.5">
                                                                <div className="flex items-center justify-between text-sm">
                                                                <span
                                                                    className="text-muted-foreground flex items-center gap-1.5"><Tag
                                                                    className="size-3.5"/>{t("recents.version")}</span>
                                                                    <div className={"gap-2 flex flex-row"}>
                                                                        {release.release_id == lastReleases.list[0].release_id && lastPage == 1 && (
                                                                            <span className=" inline-flex items-center gap-1 rounded-md bg-primary/10 px-2 py-1 text-[10px] font-bold text-primary shrink-0">
                                                                            <HugeiconsIcon icon={Sparkles} className="h-3 w-3" />
                                                                            <span>{t("latestRelease")}</span>
                                                                        </span>
                                                                        )}
                                                                        <span>v{release.version_name}</span>
                                                                    </div>
                                                                </div>
                                                                <div className="flex items-center justify-between text-sm">
                                                                <span
                                                                    className="text-muted-foreground flex items-center gap-1.5"><Download
                                                                    className="size-3.5"/>{t("recents.downloadCount")}</span>
                                                                    <span>{Number(release.download_count).toLocaleString()}</span>
                                                                </div>
                                                                <div className="flex items-center justify-between text-sm">
                                                                <span
                                                                    className="text-muted-foreground flex items-center gap-1.5"><Calendar
                                                                    className="size-3.5"/>{t("recents.releaseDate")}</span>
                                                                    <span className="text-sm whitespace-nowrap">{formatDate(release.release_date, i18n.language)}</span>
                                                                </div>
                                                                <div className="flex items-center justify-between text-sm">
                                                                <span
                                                                    className="text-muted-foreground flex items-center gap-1.5"><InfoIcon
                                                                    className="size-3.5"/>{t("suggested_ig_version_label")}</span>
                                                                    <span className="text-sm whitespace-nowrap">{
                                                                        release.preferred_ig_version != null ? `v${release.preferred_ig_version}` : t("not_specified")
                                                                    }</span>
                                                                </div>
                                                            </div>
                                                            <CardFooter className="border-t">
                                                                <div className="flex items-end justify-end gap-2 w-full">
                                                                    <Button className="gap-2" variant={"outline"}
                                                                            size={"icon"} onClick={() => {
                                                                        setSelectedRelease(release);
                                                                        setShowChangelog(true);
                                                                    }}>
                                                                        <ScrollText className="size-4"/>
                                                                    </Button>
                                                                    <Link
                                                                        href={`/library/backup/release?rid=${release.release_id}`}>
                                                                        <Button className="gap-2" variant={"outline"}>
                                                                            <ExternalLink className="size-4"/>
                                                                            {t("recents.viewDetails")}
                                                                        </Button>
                                                                    </Link>
                                                                </div>
                                                            </CardFooter>
                                                        </Card>
                                                    ))
                                                })}
                                            </div>
                                            <motion.div
                                                initial={{opacity: 0, y: 20}}
                                                animate={{opacity: 1, y: 0}}
                                                transition={{delay: 0.8, duration: 0.6}}
                                                className="flex justify-center mt-6"
                                            >
                                                <Pagination>
                                                    <PaginationContent className="flex items-center gap-1">
                                                        <PaginationItem>
                                                            <PaginationPrevious
                                                                onClick={() => lastPage > 1 && setLastPage(lastPage - 1)}
                                                                className={`
                            flex items-center gap-2 px-3 py-2 rounded-lg transition-all duration-200
                            ${lastPage <= 1
                                                                    ? "opacity-50 cursor-not-allowed"
                                                                    : "hover:bg-muted cursor-pointer"}
                          `}
                                                            >
                                                                <HugeiconsIcon icon={ChevronRightIcon}
                                                                               className="h-4 w-4"/>
                                                                <span className="hidden sm:inline">Previous</span>
                                                            </PaginationPrevious>
                                                        </PaginationItem>

                                                        {generatePageNumbers().map((pageNum, index) => (
                                                            <PaginationItem key={index}>
                                                                {pageNum === "ellipsis" ? (
                                                                    <PaginationEllipsis className="px-3 py-2"/>
                                                                ) : (
                                                                    <PaginationLink
                                                                        onClick={() => setLastPage(pageNum as number)}
                                                                        isActive={lastPage === pageNum}
                                                                        className={`
                                                                            px-3 py-2 rounded-lg transition-all duration-200 cursor-pointer
                                                                                ${lastPage === pageNum
                                                                            ? "text-foreground shadow-sm"
                                                                            : "hover:bg-muted"}
                                                                           `}
                                                                    >
                                                                        {pageNum}
                                                                    </PaginationLink>
                                                                )}
                                                            </PaginationItem>
                                                        ))}

                                                        <PaginationItem>
                                                            <PaginationNext
                                                                onClick={() => lastPage < lastReleases.page_size &&
                                                                    setLastPage(lastPage + 1)}
                                                                className={`
                                                                    flex items-center gap-2 px-3 py-2 rounded-lg transition-all duration-200
                                                                        ${lastPage >= lastReleases.page_size
                                                                    ? "opacity-50 cursor-not-allowed"
                                                                    : "hover:bg-muted cursor-pointer"}
                                                                `}
                                                            >
                                                                <span className="hidden sm:inline">Next</span>
                                                                <HugeiconsIcon icon={ChevronRightIcon} className="h-4 w-4"/>
                                                            </PaginationNext>
                                                        </PaginationItem>
                                                    </PaginationContent>
                                                </Pagination>
                                            </motion.div>
                                        </>
                                    )}
                                </div>
                            </div>
                        </motion.section>
                    </div>

                    <div className="flex flex-col gap-6">
                        {/* Screenshots */}
                        <motion.section
                            initial={{opacity: 0, y: 20}}
                            animate={{opacity: 1, y: 0}}
                            transition={{duration: 0.5, delay: 0.25}}
                            className="rounded-3xl border border-border bg-card/60 p-6 shadow-sm backdrop-blur-xl"
                        >
                            <h2 className="mb-4 flex items-center gap-2 text-lg font-semibold">
                                <HugeiconsIcon
                                    icon={SmartPhone01Icon}
                                    className="h-5 w-5 text-primary"
                                />
                                {t("screenshots")}
                            </h2>
                            {data.backup_info.screenshots?.length > 0 ? (
                                <ScreenshotViewer screenshots={data.backup_info.screenshots} backup_id={data.backup_info.id} />
                            ) : (
                                <div className="flex aspect-[4/3] items-center justify-center rounded-2xl border border-dashed border-border bg-background/20">
                                    <p className="text-sm text-muted-foreground/60">
                                        {t("noScreenshots")}
                                    </p>
                                </div>
                            )}
                        </motion.section>

                        {/* How to use :) */}
                        <motion.section
                            initial={{opacity: 0, y: 20}}
                            animate={{opacity: 1, y: 0}}
                            transition={{duration: 0.5, delay: 0.25}}
                            className="rounded-3xl border p-6 bg-card/60 shadow-sm backdrop-blur-xl"
                        >
                            <h2 className="mb-3 flex items-center gap-2 text-lg font-semibold">
                                <HugeiconsIcon
                                    icon={CircleQuestionMarkIcon}
                                    className="h-5 w-5 text-primary"
                                />
                                {t("howToUseTitle")}
                            </h2>
                            <p className="text-pretty text-sm leading-relaxed text-foreground/80">
                                {t("howToUseDescription")}
                            </p>
                        </motion.section>
                    </div>
                </div>

                <motion.div
                    initial={{opacity: 0, y: 20}}
                    animate={{opacity: 1, y: 0}}
                    transition={{duration: 0.5, delay: 0.3}}
                    className="mt-8 text-center"
                >
                    <motion.div
                        whileHover={{y: -2}}
                        transition={{type: "spring", stiffness: 400, damping: 25}}
                        className="inline-block"
                    >
                        <Button
                            asChild
                            variant="outline"
                            className="group rounded-full border-border bg-card/50 px-6 backdrop-blur"
                        >
                            <Link href="/library/backup">
                                <HugeiconsIcon
                                    icon={LibraryIcon}
                                    className="mr-2 h-4 w-4"
                                />
                                {t("viewAllBackups")}
                            </Link>
                        </Button>
                    </motion.div>
                </motion.div>
            </div>

            {selectedRelease && <Dialog open={showChangelog} onOpenChange={setShowChangelog}>
                <DialogContent className="max-w-2xl max-h-[80vh]">
                    <DialogHeader>
                        <DialogTitle>{t("recents.changelogOf", { verStr: selectedRelease.version_name })}</DialogTitle>
                    </DialogHeader>
                    {<a className="bg-muted p-4 rounded-lg overflow-auto whitespace-pre-line max-h-[60vh]">
                        {selectedRelease.changelog}
                    </a>}
                </DialogContent>
            </Dialog>}

            <Footer/>
        </>
    );
}
