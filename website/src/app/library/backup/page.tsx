/*
 * (c) 2026 Muhammed Ali Bulut, All rights reserved.
 *
 * See LICENSE file in repository root for copy file of license. For copyright
 * notices, technical issues, feedback, or any other related to this code file or
 * project, please contact me via mamii@mamii.dev or other ways.
 */

"use client";

import {AnimatePresence, motion} from "framer-motion";
import {useEffect, useState} from "react";
import {LoadingBar} from "@/components/LoadingBars";
import {Button} from "@/components/ui/button";
import Link from "next/link";
import {useTranslation} from "react-i18next";
import {Page, PageHeader} from "@/components/PageUtils";
import {HugeiconsIcon} from "@hugeicons/react";
import {
    ArrowRight01Icon, DocumentCodeFreeIcons,
    FileValidationIcon, LibraryIcon,
    RefreshIcon,
    TelegramIcon,
    UserIcon
} from "@hugeicons/core-free-icons";

interface Backup {
    id: string;
    name: string;
    author: string;
}

interface BackupInfo {
    tag_name: string;
    backups: Backup[];
}

export default function LibraryBackupContent() {
    const {t} = useTranslation("library_backup");
    const [data, setData] = useState<BackupInfo | null>(null);
    const [isLoading, setIsLoading] = useState(true);

    useEffect(() => {
        const fetchData = async () => {
            try {
                const requestUrl =
                    "https://raw.githubusercontent.com/instafel/backups/refs/heads/main/backups.json";
                const res = await fetch(requestUrl);
                const result: BackupInfo = await res.json();
                setData(result);
            } catch (error) {
                console.error(t("errors.fetchBackupsFailed"), error);
            } finally {
                setIsLoading(false);
            }
        };

        fetchData();
    }, [t]);

    if (isLoading) {
        return <LoadingBar/>;
    }

    return (
        <>
            <AnimatePresence>
                {data ? (
                    <Page
                        width={6}
                        header={<PageHeader
                            icon={<HugeiconsIcon icon={LibraryIcon} />}
                            title={t("title")}
                            subtitle={t("description")}/>}
                        content={<>
                            <motion.div
                                key="content"
                                initial={{ opacity: 0 }}
                                animate={{ opacity: 1 }}
                                exit={{ opacity: 0 }}
                            >
                                <div className="grid gap-3">
                                    {data.backups.map((backup, index) => (
                                        <motion.div
                                            key={backup.id}
                                            initial={{ opacity: 0, y: 18 }}
                                            animate={{ opacity: 1, y: 0 }}
                                            transition={{ delay: 0.15 + index * 0.06, duration: 0.45 }}
                                        >
                                            <Link
                                                href={`/library/backup/view?id=${backup.id}`}
                                                className="group flex items-center gap-4 rounded-3xl border border-border bg-card/40 p-4 backdrop-blur-sm transition-colors duration-300 hover:border-primary/40 hover:bg-card/70"
                                            >
                                                <div className="flex h-12 w-12 shrink-0 items-center justify-center rounded-2xl border border-border bg-background/60 transition-colors duration-300 group-hover:border-primary">
                                                    <HugeiconsIcon
                                                        icon={DocumentCodeFreeIcons}
                                                        className="h-6 w-6 text-primary transition-colors duration-300"
                                                    />
                                                </div>

                                                <div className="min-w-0 flex-grow">
                                                    <h3 className="truncate text-lg font-semibold">{backup.name}</h3>
                                                    <p className="mt-0.5 flex items-center gap-1.5 text-sm text-muted-foreground">
                                                        <HugeiconsIcon icon={UserIcon} className="h-3.5 w-3.5" />
                                                        {t("createdBy", { author: backup.author })}
                                                    </p>
                                                </div>

                                                <HugeiconsIcon
                                                    icon={ArrowRight01Icon}
                                                    className="h-5 w-5 shrink-0 text-muted-foreground transition-all duration-300 group-hover:translate-x-1 group-hover:text-primary"
                                                />
                                            </Link>
                                        </motion.div>
                                    ))}
                                </div>

                                <motion.section
                                    initial={{ opacity: 0, y: 24 }}
                                    animate={{ opacity: 1, y: 0 }}
                                    transition={{ delay: 0.4, duration: 0.6 }}
                                    className="relative mt-8 overflow-hidden rounded-3xl border border-border bg-card/60 px-6 py-12 text-center backdrop-blur-xl"
                                >
                                    <div className="pointer-events-none absolute inset-x-0 top-0 h-px bg-gradient-to-r from-transparent via-border to-transparent" />
                                    <div className="mx-auto mb-5 flex h-12 w-12 items-center justify-center rounded-2xl border border-border bg-background/60">
                                        <HugeiconsIcon icon={LibraryIcon} className="h-6 w-6 text-primary" />
                                    </div>
                                    <h3 className="mb-3 text-balance text-2xl font-bold">
                                        {t("contribute.title")}
                                    </h3>
                                    <p className="mx-auto mb-7 max-w-lg text-pretty leading-relaxed text-muted-foreground">
                                        {t("contribute.description")}
                                    </p>
                                    <motion.div
                                        className="inline-block"
                                        whileHover={{ y: -2 }}
                                        transition={{ type: "spring", stiffness: 400, damping: 25 }}
                                    >
                                        <Button asChild size="lg" className="group rounded-full px-6">
                                            <Link href="https://t.me/instafel">
                                                <HugeiconsIcon icon={TelegramIcon} className="mr-2 h-5 w-5" />
                                                {t("contribute.button")}
                                            </Link>
                                        </Button>
                                    </motion.div>
                                </motion.section>
                            </motion.div>
                        </>}/>
                ) : (
                    <motion.div
                        key="error"
                        initial={{ opacity: 0, scale: 0.97 }}
                        animate={{ opacity: 1, scale: 1 }}
                        className="flex min-h-[50vh] items-center justify-center"
                    >
                        <div className="w-full max-w-md rounded-3xl border border-border bg-card/60 p-8 text-center backdrop-blur-xl">
                            <div className="mx-auto mb-5 flex h-12 w-12 items-center justify-center rounded-2xl border border-border bg-background/60">
                                <HugeiconsIcon icon={FileValidationIcon} className="h-6 w-6 text-primary" />
                            </div>
                            <h2 className="mb-3 text-xl font-bold">{t("error.title")}</h2>
                            <p className="mb-7 text-pretty leading-relaxed text-muted-foreground">
                                {t("error.description")}
                            </p>
                            <Button
                                onClick={() => window.location.reload()}
                                className="rounded-full px-6"
                            >
                                <HugeiconsIcon icon={RefreshIcon} className="mr-2 h-4 w-4" />
                                {t("error.retryButton")}
                            </Button>
                        </div>
                    </motion.div>
                )}
            </AnimatePresence>
        </>
    );
}
