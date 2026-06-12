/*
 * (c) 2026 Muhammed Ali Bulut, All rights reserved.
 *
 * See LICENSE file in repository root for copy file of license. For copyright
 * notices, technical issues, feedback, or any other related to this code file or
 * project, please contact me via mamii@mamii.dev or other ways.
 */

"use client";

import {motion} from "framer-motion";
import {useEffect, useState} from "react";
import {Button} from "@/components/ui/button";
import {useSearchParams} from "next/navigation";
import {LoadingBar} from "@/components/LoadingBars";
import Footer from "@/components/Footer";
import {HugeiconsIcon} from "@hugeicons/react";
import {
    Calendar03Icon,
    Download01Icon,
    FileValidationIcon,
    ArrowLeft01Icon,
    ArrowUpRight01Icon,
    Time04Icon,
    InformationCircleIcon,
    SmartPhone01Icon,
    UserIcon,
    CheckmarkCircle02Icon, QuestionFreeIcons, CircleQuestionMarkIcon, LibraryIcon,
} from "@hugeicons/core-free-icons";
import Link from "next/link";
import {useTranslation} from "react-i18next";
import Navbar from "@/components/Navbar";

interface Manifest {
    version_name: string;
    author: string;
    changelog: string;
    last_updated: string;
    description: string;
    name: string;
}

interface Resp {
    manifest_version: number;
    manifest: Manifest;
}

export default function PageBackup() {
    const {t} = useTranslation("backup");
    const searchParams = useSearchParams();
    const id = searchParams.get("id") ?? "null";
    const [data, setData] = useState<Resp | null>(null);
    const [isLoading, setIsLoading] = useState(true);
    const [downloadStarted, setDownloadStarted] = useState(false);
    const [importStarted, setImportStarted] = useState(false);

    useEffect(() => {
        const fetchData = async () => {
            try {
                const requestUrl = `https://raw.githubusercontent.com/instafel/backups/refs/heads/main/${id}/manifest.json`;
                const res = await fetch(requestUrl);
                const result: Resp = await res.json();
                setData(result);
            } catch (error) {
                console.error(t("errors.fetchFailed", {errStr: error}));
            } finally {
                setIsLoading(false);
            }
        };
        fetchData();
    }, [id, t]);

    const handleDownloadBackup = (id: string, version: string) => {
        setDownloadStarted(true);
        const link = document.createElement("a");
        link.href = `https://api.instafel.mamii.dev/content/util/download-backup?id=${id}&version=${version}`;
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

    const changelogItems =
        typeof data.manifest.changelog === "string"
            ? data.manifest.changelog.split("\n").filter((item) => item.trim() !== "")
            : [];

    return (
        <>
            <Navbar/>
            <div className="mx-auto w-full max-w-7xl px-4 py-10">
                <motion.section
                    initial={{opacity: 0, y: 20}}
                    animate={{opacity: 1, y: 0}}
                    transition={{duration: 0.5}}
                    className="relative overflow-hidden rounded-3xl border border-border bg-card/60 p-6 backdrop-blur-xl sm:p-8"
                >
                    <div
                        className="pointer-events-none absolute inset-x-0 top-0 h-px bg-gradient-to-r from-transparent via-border to-transparent"/>

                    <div className="flex flex-col justify-between gap-6 md:flex-row md:items-center">
                        <div>
                            <h1 className="text-balance text-2xl font-bold sm:text-3xl">
                                {data.manifest.name}
                            </h1>
                            <div className="mt-3 flex flex-wrap items-center gap-2">
                <span
                    className="inline-flex items-center gap-1.5 rounded-full border border-border bg-background/50 px-3 py-1 text-xs font-medium">
                  <HugeiconsIcon
                      icon={UserIcon}
                      className="h-3.5 w-3.5 text-primary"
                  />
                    {data.manifest.author}
                </span>
                                <span
                                    className="inline-flex items-center gap-1.5 rounded-full bg-primary/10 px-3 py-1 text-xs font-medium text-primary">
                  <HugeiconsIcon icon={Time04Icon} className="h-3.5 w-3.5"/>
                                    {t("version", {verStr: data.manifest.version_name})}
                </span>
                            </div>
                        </div>

                        <div className="flex flex-col gap-3 sm:flex-row md:flex-col lg:flex-row">
                            <motion.div
                                whileHover={{y: -2}}
                                transition={{type: "spring", stiffness: 400, damping: 25}}
                            >
                                <Button
                                    onClick={() =>
                                        handleDownloadBackup(id, data.manifest.version_name)
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
                                    variant="outline"
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

                <div className="mt-6 grid grid-cols-1 items-stretch gap-6 lg:grid-cols-3">
                    <motion.section
                        initial={{opacity: 0, y: 20}}
                        animate={{opacity: 1, y: 0}}
                        transition={{duration: 0.5, delay: 0.1}}
                        className="rounded-3xl border border-border bg-card/60 p-6 backdrop-blur-xl lg:col-span-2"
                    >
                        <h2 className="mb-3 flex items-center gap-2 text-lg font-semibold">
                            <HugeiconsIcon
                                icon={InformationCircleIcon}
                                className="h-5 w-5 text-primary"
                            />
                            {t("aboutTitle")}
                        </h2>
                        <p className="text-pretty leading-relaxed text-foreground/90">
                            {data.manifest.description}
                        </p>
                    </motion.section>

                    <motion.section
                        initial={{opacity: 0, y: 20}}
                        animate={{opacity: 1, y: 0}}
                        transition={{duration: 0.5, delay: 0.15}}
                        className="flex flex-col justify-center rounded-3xl border border-border bg-card/60 p-6 backdrop-blur-xl"
                    >
                        <h2 className="mb-3 flex items-center gap-2 text-lg font-semibold">
                            <HugeiconsIcon
                                icon={Calendar03Icon}
                                className="h-5 w-5 text-primary"
                            />
                            {t("lastUpdated")}
                        </h2>
                        <p className="text-base font-semibold">
                            {data.manifest.last_updated}
                        </p>
                    </motion.section>

                    {changelogItems.length > 0 && (
                        <motion.section
                            initial={{opacity: 0, y: 20}}
                            animate={{opacity: 1, y: 0}}
                            transition={{duration: 0.5, delay: 0.2}}
                            className="rounded-3xl border border-border bg-card/60 p-6 backdrop-blur-xl lg:col-span-2"
                        >
                            <h2 className="mb-4 flex items-center gap-2 text-lg font-semibold">
                                <HugeiconsIcon
                                    icon={Time04Icon}
                                    className="h-5 w-5 text-primary"
                                />
                                {t("changelogTitle")}
                            </h2>
                            <ul className="space-y-2">
                                {changelogItems.map((item, index) => (
                                    <motion.li
                                        key={index}
                                        initial={{opacity: 0, x: -5}}
                                        animate={{opacity: 1, x: 0}}
                                        transition={{delay: 0.2 + index * 0.06, duration: 0.3}}
                                        className="flex items-start gap-2.5 rounded-2xl border border-border bg-background/40 px-4 py-2.5 text-sm leading-relaxed"
                                    >
                                        <span>{item}</span>
                                    </motion.li>
                                ))}
                            </ul>
                        </motion.section>
                    )}

                    <motion.section
                        initial={{opacity: 0, y: 20}}
                        animate={{opacity: 1, y: 0}}
                        transition={{duration: 0.5, delay: 0.25}}
                        className="rounded-3xl border p-6 bg-card/60"
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

            <Footer/>
        </>
    );
}
