/*
 * (c) 2026 Muhammed Ali Bulut, All rights reserved.
 *
 * See LICENSE file in repository root for copy file of license. For copyright
 * notices, technical issues, feedback, or any other related to this code file or
 * project, please contact me via mamii@mamii.dev or other ways.
 */

"use client";

import { AnimatePresence, motion } from "framer-motion";
import Link from "next/link";
import { HugeiconsIcon } from "@hugeicons/react";
import {
    RefreshIcon,
    Download01Icon,
    GitBranchIcon,
    Shield01Icon,
    SmartPhone01Icon,
    FlashIcon,
    AudioWave01Icon,
    CatIcon,
    CheckmarkCircle02Icon,
    ArrowUpRight01Icon, RefreshDotIcon,
} from "@hugeicons/core-free-icons";
import { Button } from "@/components/ui/button";
import Footer from "@/components/Footer";
import { useTranslation } from "react-i18next";
import Navbar from "@/components/Navbar";
import { useEffect, useState } from "react";
import { LoadingBar } from "@/components/LoadingBars";

interface FetchInfo {
    version: string;
    download_url: string;
}

export default function PageUpdater() {
    const { t } = useTranslation("updater");
    const [data, setData] = useState<FetchInfo | null>(null);

    useEffect(() => {
        const fetchData = async () => {
            const res = await fetch(
                "https://raw.githubusercontent.com/instafel/instafel/refs/heads/main/latest_updater.json"
            );
            const result = await res.json();
            setData({
                version: `v${result.version}`,
                download_url: `https://github.com/instafel/u-rel/releases/download/v${result.version}/ifl-updater-v${result.version}-release.apk`,
            });
        };
        fetchData();
    }, []);

    const steps = [
        {
            step: "1",
            title: t("howItWorks.steps.install.title"),
            description: t("howItWorks.steps.install.description"),
            icon: Shield01Icon,
        },
        {
            step: "2",
            title: t("howItWorks.steps.configure.title"),
            description: t("howItWorks.steps.configure.description"),
            icon: SmartPhone01Icon,
        },
        {
            step: "3",
            title: t("howItWorks.steps.automatic.title"),
            description: t("howItWorks.steps.automatic.description"),
            icon: FlashIcon,
        },
    ];

    const features = [
        {
            icon: AudioWave01Icon,
            title: t("features.silent.title"),
            description: t("features.silent.description"),
        },
        {
            icon: CatIcon,
            title: t("features.shizuku.title"),
            description: t("features.shizuku.description"),
        },
        {
            icon: GitBranchIcon,
            title: t("features.openSource.title"),
            description: t("features.openSource.description"),
        },
    ];

    const benefits = [
        t("whyUse.benefits.latestFeatures"),
        t("whyUse.benefits.saveTime"),
        t("whyUse.benefits.lightweight"),
        t("whyUse.benefits.notifications"),
        ];

    return (
        <>
            <Navbar />
            <AnimatePresence>
                {!data ? (
                    <LoadingBar />
                ) : (
                    <div>
                        <div className="min-h-screen px-4 py-12 sm:px-6 lg:px-8">
                            <div className="mx-auto w-full max-w-7xl">
                                <motion.section
                                    initial={{ opacity: 0, y: 30 }}
                                    animate={{ opacity: 1, y: 0 }}
                                    transition={{ duration: 0.6 }}
                                    className="relative mb-12 overflow-hidden rounded-3xl border border-border bg-card/60 px-4 py-16 text-center backdrop-blur-xl"
                                >
                                    <div className="pointer-events-none absolute inset-x-0 top-0 h-px bg-gradient-to-r from-transparent via-border to-transparent" />

                                    <div className="mb-6 flex justify-center">
                                        <motion.div
                                            initial={{ scale: 0.9, opacity: 0 }}
                                            animate={{ scale: 1, opacity: 1 }}
                                            transition={{ duration: 0.5, delay: 0.1 }}
                                            className="flex h-16 w-16 items-center justify-center rounded-2xl border border-border bg-background/60"
                                        >
                                            <HugeiconsIcon
                                                icon={RefreshDotIcon}
                                                className="h-8 w-8 text-primary"
                                            />
                                        </motion.div>
                                    </div>

                                    <h1 className="text-balance text-4xl font-extrabold tracking-tight sm:text-5xl md:text-6xl">
                                        <span className="text-primary">{t("heroHeader.title")}</span>
                                    </h1>
                                    <h2 className="mt-2 text-2xl font-bold text-foreground/80">
                                        {t("heroHeader.subtitle")}
                                    </h2>
                                    <p className="mx-auto mt-6 max-w-2xl text-pretty text-lg leading-relaxed text-muted-foreground">
                                        {t("heroHeader.description")}
                                    </p>

                                    <div className="mt-10 flex flex-col justify-center gap-4 sm:flex-row">
                                        <motion.div
                                            whileHover={{ y: -2 }}
                                            transition={{ type: "spring", stiffness: 400, damping: 25 }}
                                        >
                                            <Button asChild size="lg" className="group rounded-full px-6">
                                                <Link href={data.download_url || "#"}>
                                                    <HugeiconsIcon
                                                        icon={Download01Icon}
                                                        className="mr-2 h-5 w-5"
                                                    />
                                                    {t("buttons.download", { version: data.version })}

                                                </Link>
                                            </Button>
                                        </motion.div>
                                        <motion.div
                                            whileHover={{ y: -2 }}
                                            transition={{ type: "spring", stiffness: 400, damping: 25 }}
                                        >
                                            <Button
                                                asChild
                                                size="lg"
                                                variant="outline"
                                                className="group rounded-full border-border bg-background/40 px-6 backdrop-blur"
                                            >
                                                <Link href="https://github.com/mamiiblt/instafel">
                                                    <HugeiconsIcon
                                                        icon={GitBranchIcon}
                                                        className="mr-2 h-5 w-5 transition-transform duration-300"
                                                    />
                                                    {t("buttons.sourceCode")}
                                                </Link>
                                            </Button>
                                        </motion.div>
                                    </div>
                                </motion.section>

                                <motion.section
                                    initial={{ opacity: 0, y: 30 }}
                                    animate={{ opacity: 1, y: 0 }}
                                    transition={{ delay: 0.3, duration: 0.6 }}
                                    className="mb-12"
                                >
                                    <h2 className="mb-8 text-center text-2xl font-bold">
                                        {t("howItWorks.title")}
                                    </h2>
                                    <div className="grid gap-4 md:grid-cols-3">
                                        {steps.map((item, index) => (
                                            <motion.div
                                                key={index}
                                                initial={{ opacity: 0, y: 20 }}
                                                animate={{ opacity: 1, y: 0 }}
                                                transition={{ delay: 0.35 + index * 0.1, duration: 0.5 }}
                                                className="group rounded-3xl border border-border bg-card/60 p-6 backdrop-blur-xl transition-colors hover:border-primary/40"
                                            >
                                                <div className="mb-4 flex h-12 w-12 items-center justify-center rounded-2xl border border-border bg-background/60 transition-colors group-hover:border-primary/40">
                                                    <HugeiconsIcon
                                                        icon={item.icon}
                                                        className="h-6 w-6 text-primary"
                                                    />
                                                </div>
                                                <h3 className="mb-2 text-lg font-semibold">
                                                    {`${item.step}. ${item.title}`}
                                                </h3>
                                                <p className="leading-relaxed text-muted-foreground">
                                                    {item.description}
                                                </p>
                                            </motion.div>
                                        ))}
                                    </div>
                                </motion.section>

                                {/* Features */}
                                <motion.section
                                    initial={{ opacity: 0, y: 30 }}
                                    animate={{ opacity: 1, y: 0 }}
                                    transition={{ delay: 0.5, duration: 0.6 }}
                                    className="mb-12"
                                >
                                    <h2 className="mb-8 text-center text-2xl font-bold">
                                        {t("features.title")}
                                    </h2>
                                    <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-3">
                                        {features.map((feature, index) => (
                                            <motion.div
                                                key={index}
                                                initial={{ opacity: 0, y: 20 }}
                                                animate={{ opacity: 1, y: 0 }}
                                                transition={{ delay: 0.55 + index * 0.1, duration: 0.5 }}
                                                whileHover={{ y: -4 }}
                                                className="flex h-full flex-col items-center rounded-3xl border border-border bg-card/60 p-6 text-center backdrop-blur-xl transition-colors hover:border-primary/40"
                                            >
                                                <div className="mb-4 flex h-14 w-14 items-center justify-center rounded-2xl border border-border bg-background/60">
                                                    <HugeiconsIcon
                                                        icon={feature.icon}
                                                        className="h-7 w-7 text-primary"
                                                    />
                                                </div>
                                                <h3 className="mb-3 text-lg font-semibold">
                                                    {feature.title}
                                                </h3>
                                                <p className="leading-relaxed text-muted-foreground">
                                                    {feature.description}
                                                </p>
                                            </motion.div>
                                        ))}
                                    </div>
                                </motion.section>

                                {/* Why use */}
                                <motion.section
                                    initial={{ opacity: 0, y: 30 }}
                                    animate={{ opacity: 1, y: 0 }}
                                    transition={{ delay: 0.7, duration: 0.6 }}
                                    className="mb-12 rounded-3xl border border-border bg-card/60 p-8 backdrop-blur-xl"
                                >
                                    <h2 className="mb-6 text-2xl font-bold">{t("whyUse.title")}</h2>
                                    <ul className="grid gap-3 sm:grid-cols-2">
                                        {benefits.map((benefit, index) => (
                                            <motion.li
                                                key={index}
                                                initial={{ opacity: 0, x: -5 }}
                                                animate={{ opacity: 1, x: 0 }}
                                                transition={{ delay: 0.75 + index * 0.06, duration: 0.3 }}
                                                className="flex items-start gap-2.5 rounded-2xl border border-border bg-background/40 px-4 py-3 text-sm leading-relaxed"
                                            >
                                                <HugeiconsIcon
                                                    icon={CheckmarkCircle02Icon}
                                                    className="mt-0.5 h-5 w-5 shrink-0 text-primary"
                                                />
                                                <span>{benefit}</span>
                                            </motion.li>
                                        ))}
                                    </ul>
                                </motion.section>

                                {/* Call to action */}
                                <motion.section
                                    initial={{ opacity: 0, y: 30 }}
                                    animate={{ opacity: 1, y: 0 }}
                                    transition={{ delay: 0.9, duration: 0.6 }}
                                    className="relative overflow-hidden rounded-3xl border border-primary/30 bg-primary/5 p-8 text-center backdrop-blur-xl"
                                >
                                    <div className="pointer-events-none absolute inset-x-0 top-0 h-px bg-gradient-to-r from-transparent via-primary/40 to-transparent" />
                                    <h2 className="mb-4 text-balance text-2xl font-bold">
                                        {t("callToAction.title")}
                                    </h2>
                                    <p className="mx-auto mb-6 max-w-xl text-pretty text-lg leading-relaxed text-muted-foreground">
                                        {t("callToAction.description")}
                                    </p>
                                    <motion.div
                                        className="inline-block"
                                        whileHover={{ y: -2 }}
                                        transition={{ type: "spring", stiffness: 400, damping: 25 }}
                                    >
                                        <Button asChild size="lg" className="group rounded-full px-8">
                                            <Link href={data.download_url || "#"}>
                                                <HugeiconsIcon
                                                    icon={Download01Icon}
                                                    className="mr-2 h-5 w-5"
                                                />
                                                {t("buttons.download", { version: data.version })}
                                            </Link>
                                        </Button>
                                    </motion.div>
                                </motion.section>
                            </div>
                        </div>
                        <Footer />
                    </div>
                )}
            </AnimatePresence>
        </>
    );
}
