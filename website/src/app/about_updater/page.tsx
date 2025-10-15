"use client";

import {AnimatePresence, motion} from "framer-motion";
import Link from "next/link";
import {Card, CardContent} from "@/components/ui/card";
import {
    AudioLines,
    CheckCircle,
    Download,
    GitBranch,
    PawPrint,
    RefreshCcwDot,
    Shield,
    Smartphone,
    Zap,
} from "lucide-react";
import {Button} from "@/components/ui/button";
import Footer from "@/components/Footer";
import {useTranslation} from "react-i18next";
import Navbar from "@/components/Navbar";
import {useEffect, useState} from "react";
import {LoadingBar} from "@/components/LoadingBars";

interface FetchInfo {
    version: string;
    download_url: string;
}

export default function PageUpdater() {
    const {t} = useTranslation("updater");
    const [data, setData] = useState<FetchInfo | null>(null);

    useEffect(() => {
        const fetchData = async () => {
            const res = await fetch(
                "https://api.github.com/repos/instafel/u-rel/releases/latest"
            );
            const result = await res.json();
            setData({
                version: `${result.tag_name}`,
                download_url: `https://github.com/instafel/u-rel/releases/download/${result.tag_name}/ifl-updater-${result.tag_name}-release.apk`,
            });
        };
        fetchData();
    }, []);

    return (
        <>
            <Navbar/>
            <AnimatePresence>
                {!data ? (
                    <LoadingBar/>
                ) : (
                    <div>
                        <div className="min-h-screen py-12 px-4 sm:px-6 lg:px-8">
                            <div className="max-w-4xl mx-auto">
                                <motion.div
                                    initial={{opacity: 0, y: 30}}
                                    animate={{opacity: 1, y: 0}}
                                    transition={{duration: 0.6}}
                                    className="text-center mb-16"
                                >
                                    <div className="flex justify-center mb-6">
                                        <div className="relative">
                                            <div
                                                className="absolute inset-0 rounded-full bg-primary/20 animate-ping"
                                                style={{animationDuration: "3s"}}
                                            ></div>
                                            <div className="relative bg-primary/30 p-5 rounded-full">
                                                <RefreshCcwDot className="h-12 w-12 text-primary"/>
                                            </div>
                                        </div>
                                    </div>

                                    <h1 className="text-4xl font-extrabold tracking-tight sm:text-5xl md:text-6xl">
                    <span className="text-primary">
                      {t("heroHeader.title")}
                    </span>
                                    </h1>
                                    <h2 className="text-2xl font-bold mt-2 text-foreground/80">
                                        {t("heroHeader.subtitle")}
                                    </h2>
                                    <p className="mt-6 max-w-2xl mx-auto text-lg text-muted-foreground">
                                        {t("heroHeader.description")}
                                    </p>

                                    <div className="mt-10 flex flex-col sm:flex-row gap-4 justify-center">
                                        <Button
                                            asChild
                                            size={"lg"}
                                            variant="default"
                                            className="relative overflow-hidden group"
                                        >
                                            <Link href={data.download_url || "#"}>
                                                <span
                                                    className="absolute inset-0 bg-gradient-to-r from-primary/30 to-primary/0 group-hover:opacity-50 opacity-0 transition-opacity"/>
                                                <Download className="mr-2"/>
                                                {t("buttons.download", {version: data.version})}
                                            </Link>
                                        </Button>
                                        <Button
                                            asChild
                                            size={"lg"}
                                            variant="outline"
                                            className="group"
                                        >
                                            <Link href="https://github.com/mamiiblt/instafel">
                                                <GitBranch
                                                    className="mr-2 group-hover:rotate-12 transition-transform duration-300"/>
                                                {t("buttons.sourceCode")}
                                            </Link>
                                        </Button>
                                    </div>
                                </motion.div>

                                <motion.div
                                    initial={{opacity: 0, y: 30}}
                                    animate={{opacity: 1, y: 0}}
                                    transition={{delay: 0.3, duration: 0.6}}
                                    className="mb-16"
                                >
                                    <h2 className="text-2xl font-bold text-center mb-8">
                                        {t("howItWorks.title")}
                                    </h2>

                                    <div className="grid md:grid-cols-3 gap-4">
                                        {[
                                            {
                                                step: "1",
                                                title: t("howItWorks.steps.install.title"),
                                                description: t("howItWorks.steps.install.description"),
                                                icon: <Shield className="h-6 w-6 text-emerald-500"/>,
                                            },
                                            {
                                                step: "2",
                                                title: t("howItWorks.steps.configure.title"),
                                                description: t(
                                                    "howItWorks.steps.configure.description"
                                                ),
                                                icon: <Smartphone className="h-6 w-6 text-blue-500"/>,
                                            },
                                            {
                                                step: "3",
                                                title: t("howItWorks.steps.automatic.title"),
                                                description: t(
                                                    "howItWorks.steps.automatic.description"
                                                ),
                                                icon: <Zap className="h-6 w-6 text-amber-500"/>,
                                            },
                                        ].map((item, index) => (
                                            <Card
                                                key={index}
                                                className="relative overflow-hidden border-2 hover:border-primary/50 transition-colors group"
                                            >
                                                <div
                                                    className="absolute -right-4 -top-4 bg-gray-100 dark:bg-gray-800 rounded-full w-12 h-12 flex items-center justify-center text-lg font-bold opacity-70">
                                                    {item.step}
                                                </div>
                                                <CardContent className="pt-6">
                                                    <div
                                                        className="mb-4 rounded-full bg-primary/10 p-3 w-fit group-hover:bg-primary/20 transition-colors">
                                                        {item.icon}
                                                    </div>
                                                    <h3 className="text-lg font-semibold mb-2">
                                                        {item.title}
                                                    </h3>
                                                    <p className="text-muted-foreground">
                                                        {item.description}
                                                    </p>
                                                </CardContent>
                                            </Card>
                                        ))}
                                    </div>
                                </motion.div>

                                <motion.div
                                    initial={{opacity: 0, y: 30}}
                                    animate={{opacity: 1, y: 0}}
                                    transition={{delay: 0.5, duration: 0.6}}
                                >
                                    <h2 className="text-2xl font-bold text-center mb-8">
                                        {t("features.title")}
                                    </h2>
                                    <div className="grid gap-8 md:grid-cols-2 lg:grid-cols-3 mb-16">
                                        {[
                                            {
                                                icon: <AudioLines className="h-8 w-8 text-primary"/>,
                                                title: t("features.silent.title"),
                                                description: t("features.silent.description"),
                                            },
                                            {
                                                icon: <PawPrint className="h-8 w-8 text-primary"/>,
                                                title: t("features.shizuku.title"),
                                                description: t("features.shizuku.description"),
                                            },
                                            {
                                                icon: <GitBranch className="h-8 w-8 text-primary"/>,
                                                title: t("features.openSource.title"),
                                                description: t("features.openSource.description"),
                                            },
                                        ].map((feature, index) => (
                                            <motion.div
                                                key={index}
                                                initial={{opacity: 0, y: 30}}
                                                animate={{opacity: 1, y: 0}}
                                                transition={{delay: 0.6 + index * 0.2, duration: 0.6}}
                                                whileHover={{y: -5}}
                                            >
                                                <Card
                                                    className="flex flex-col items-center text-center p-6 h-full border-2 hover:border-primary/40 transition-colors">
                                                    <div className="mb-4 rounded-full bg-primary/10 p-4">
                                                        {feature.icon}
                                                    </div>
                                                    <h3 className="text-lg font-semibold mb-3">
                                                        {feature.title}
                                                    </h3>
                                                    <p className="text-muted-foreground">
                                                        {feature.description}
                                                    </p>
                                                </Card>
                                            </motion.div>
                                        ))}
                                    </div>
                                </motion.div>

                                <motion.div
                                    initial={{opacity: 0, y: 30}}
                                    animate={{opacity: 1, y: 0}}
                                    transition={{delay: 0.7, duration: 0.6}}
                                    className="mb-16"
                                >
                                    <div
                                        className="bg-gradient-to-r from-gray-100/50 to-gray-50/50 dark:from-gray-800/50 dark:to-gray-900/50 rounded-2xl p-8">
                                        <div className="flex flex-col md:flex-row items-center">
                                            <div className="md:w-1/2 mb-6 md:mb-0">
                                                <h2 className="text-2xl font-bold mb-4">
                                                    {t("whyUse.title")}
                                                </h2>
                                                <ul className="space-y-4">
                                                    {[
                                                        t("whyUse.benefits.latestFeatures"),
                                                        t("whyUse.benefits.saveTime"),
                                                        t("whyUse.benefits.lightweight"),
                                                        t("whyUse.benefits.notifications"),
                                                        t("whyUse.benefits.compatibility"),
                                                    ].map((benefit, index) => (
                                                        <li key={index} className="flex items-start">
                                                            <CheckCircle
                                                                className="h-5 w-5 text-green-500 mr-2 shrink-0 mt-0.5"/>
                                                            <span>{benefit}</span>
                                                        </li>
                                                    ))}
                                                </ul>
                                            </div>
                                        </div>
                                    </div>
                                </motion.div>

                                <motion.div
                                    initial={{opacity: 0, y: 30}}
                                    animate={{opacity: 1, y: 0}}
                                    transition={{delay: 0.9, duration: 0.6}}
                                    className="text-center bg-gradient-to-r from-primary/5 to-primary/10 p-8 rounded-2xl"
                                >
                                    <h2 className="text-2xl font-bold mb-4">
                                        {t("callToAction.title")}
                                    </h2>
                                    <p className="text-lg text-muted-foreground mb-6">
                                        {t("callToAction.description")}
                                    </p>
                                    <Button
                                        asChild
                                        size="lg"
                                        variant="default"
                                        className="px-8 py-6 text-lg shadow-lg hover:shadow-xl transition-shadow"
                                    >
                                        <Link href={data.download_url || "#"}>
                                            <Download className="mr-2 h-5 w-5"/>
                                            {t("buttons.download", {version: data.version})}
                                        </Link>
                                    </Button>
                                </motion.div>
                            </div>
                        </div>
                        <Footer/>
                    </div>
                )}
            </AnimatePresence>
        </>
    );
}
