"use client";

import {motion} from "framer-motion";
import {Book, Bug, Download, Library, LucideInstagram, MinusCircle, RefreshCw, Send, Wrench,} from "lucide-react";
import {Button} from "@/components/ui/button";
import Image from "next/image";
import Link from "next/link";
import Footer from "@/components/Footer";
import HomeMockup from "@/components/HomeMockup";
import {useTranslation} from "react-i18next";
import Navbar from "@/components/Navbar";

export default function PageHome() {
    const {t} = useTranslation("home");

    const features = [
        {
            icon: <Wrench/>,
            title: t("features.3"),
            desc: t("features.4"),
        },
        {
            icon: <LucideInstagram/>,
            title: t("features.5"),
            desc: t("features.6"),
        },
        {
            icon: <RefreshCw/>,
            title: t("features.7"),
            desc: t("features.8"),
        },
        {
            icon: <MinusCircle/>,
            title: t("features.9"),
            desc: t("features.10"),
        },
        {
            icon: <Bug/>,
            title: t("features.11"),
            desc: t("features.12"),
        },
        {
            icon: <Library/>,
            title: t("features.13"),
            desc: t("features.14"),
        },
    ];

    return (
        <>
            <Navbar/>
            <main className="flex min-h-screen flex-col bg-primary-foreground dark:bg-primary-background">
                <div className="mx-auto w-full max-w-7xl px-4 sm:px-6 lg:px-8">
                    <section className="py-16 md:py-24 lg:py-32">
                        <div className="container mx-auto px-4">
                            <div className="flex flex-col items-center justify-center text-center">
                                <motion.h1
                                    initial={{opacity: 0, y: 30}}
                                    animate={{opacity: 1, y: 0}}
                                    transition={{
                                        duration: 0.8,
                                        ease: "easeInOut",
                                    }}
                                    className="mb-6 text-5xl font-bold tracking-tight sm:text-6xl md:text-7xl bg-gradient-to-r from-gray-900 to-gray-600 bg-clip-text text-transparent dark:from-gray-100 dark:to-gray-400"
                                >
                                    {t("landing.1")}
                                </motion.h1>

                                <motion.p
                                    initial={{opacity: 0, y: 30}}
                                    animate={{opacity: 1, y: 0}}
                                    transition={{
                                        delay: 0.3,
                                        duration: 0.8,
                                        ease: "easeOut",
                                    }}
                                    className="mb-8 text-xl max-w-xl text-muted-foreground"
                                >
                                    {t("landing.2")}
                                </motion.p>

                                <motion.div
                                    initial={{opacity: 0, y: 30}}
                                    animate={{opacity: 1, y: 0}}
                                    transition={{
                                        delay: 0.5,
                                        duration: 0.6,
                                        ease: "easeOut",
                                    }}
                                    className="flex flex-col sm:flex-row gap-4 w-full max-w-md justify-center"
                                >
                                    <Button
                                        asChild
                                        size={"lg"}
                                        variant={"default"}
                                        className="w-full flex justify-center items-center group hover:scale-105 transition-transform duration-300"
                                    >
                                        <Link href="/download?version=latest">
                                            <Download className="shrink-0 w-5 h-5 mr-2 group-hover:animate-pulse"/>
                                            {t("landing.3")}
                                        </Link>
                                    </Button>

                                    <Button
                                        asChild
                                        size={"lg"}
                                        variant={"outline"}
                                        className="w-full group hover:scale-105 transition-transform duration-300"
                                    >
                                        <Link href="/wiki">
                                            <Book
                                                className="shrink-0 w-4 h-4 mr-2 group-hover:rotate-12 transition-transform duration-300"/>
                                            {t("landing.4")}
                                        </Link>
                                    </Button>
                                </motion.div>

                                <motion.div
                                    initial={{opacity: 0, y: 50, scale: 0.95}}
                                    animate={{opacity: 1, y: 0, scale: 1}}
                                    transition={{
                                        delay: 0.9,
                                        duration: 0.8,
                                        ease: "easeOut",
                                    }}
                                    className="mt-12 w-full max-w-4xl mx-auto relative"
                                >
                                    <div className="relative overflow-hidden rounded-xl">
                                        <HomeMockup/>
                                    </div>
                                </motion.div>
                            </div>
                        </div>
                    </section>

                    <section
                        id="features"
                        className="py-16 px-4 md:px-12 bg-gray-50 dark:bg-gray-900/20 rounded-3xl my-12"
                    >
                        <motion.h2
                            initial={{opacity: 0, y: 20}}
                            whileInView={{opacity: 1, y: 0}}
                            viewport={{once: true}}
                            transition={{duration: 0.6}}
                            className="text-3xl font-bold text-center mb-6"
                        >
                            {t("features.1")}
                        </motion.h2>

                        <motion.p
                            initial={{opacity: 0, y: 20}}
                            whileInView={{opacity: 1, y: 0}}
                            viewport={{once: true}}
                            transition={{duration: 0.6, delay: 0.2}}
                            className="text-center text-muted-foreground max-w-2xl mx-auto mb-12"
                        >
                            {t("features.2")}
                        </motion.p>

                        <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-8">
                            {features.map((feature, idx) => (
                                <motion.div
                                    key={idx}
                                    initial={{opacity: 0, y: 30}}
                                    whileInView={{opacity: 1, y: 0}}
                                    viewport={{once: true}}
                                    transition={{
                                        delay: idx * 0.15,
                                        duration: 0.5,
                                        ease: "easeOut",
                                    }}
                                    whileHover={{
                                        scale: 1.03,
                                        boxShadow: "0 10px 30px -15px rgba(0, 0, 0, 0.15)",
                                    }}
                                    className="bg-white dark:bg-gray-800/50 p-6 rounded-xl shadow-sm border border-gray-100 dark:border-gray-700/50"
                                >
                                    <div
                                        className="bg-gray-600 text-white p-3 rounded-lg inline-block mb-4 hover:rotate-6 transition-transform duration-300">
                                        {feature.icon}
                                    </div>
                                    <h3 className="text-xl font-semibold mb-2">
                                        {feature.title}
                                    </h3>
                                    <p className="text-muted-foreground">{feature.desc}</p>
                                </motion.div>
                            ))}
                        </div>
                    </section>

                    <section id="telegram" className="py-16 px-4 md:px-12 my-12">
                        <div className="max-w-6xl mx-auto">
                            <div className="flex flex-col md:flex-row items-center gap-12">
                                <motion.div
                                    initial={{opacity: 0, x: -30}}
                                    whileInView={{opacity: 1, x: 0}}
                                    viewport={{once: true}}
                                    transition={{duration: 0.6}}
                                    className="md:w-1/2"
                                >
                                    <div className="max-w-lg">
                    <span
                        className="inline-flex items-center rounded-full bg-blue-50 px-3 py-1 text-sm font-medium text-blue-700 dark:bg-blue-900/30 dark:text-blue-300 mb-4">
                      {t("join_community.1")}
                    </span>
                                        <h2 className="text-3xl md:text-4xl font-bold mb-4 text-center md:text-left">
                                            {t("join_community.2")}
                                        </h2>
                                        <p className="text-muted-foreground mb-6 text-center md:text-left">
                                            {t("join_community.3")}
                                        </p>
                                        <div className="flex justify-center md:justify-start">
                                            <Button
                                                asChild
                                                size="lg"
                                                className="group bg-[#0088cc] hover:bg-[#0099dd] text-white hover:scale-105 transition-all duration-300"
                                            >
                                                <a
                                                    href="https://t.me/instafel"
                                                    target="_blank"
                                                    rel="noopener noreferrer"
                                                    className="flex items-center"
                                                >
                                                    <Send
                                                        size={18}
                                                        className="mr-2 group-hover:translate-y-[-2px] transition-all duration-300"
                                                    />
                                                    {t("join_community.4")}
                                                </a>
                                            </Button>
                                        </div>
                                    </div>
                                </motion.div>

                                <motion.div
                                    initial={{opacity: 0, x: 30}}
                                    whileInView={{opacity: 1, x: 0}}
                                    viewport={{once: true}}
                                    transition={{duration: 0.6, delay: 0.2}}
                                    className="md:w-1/2 flex justify-center"
                                >
                                    <div className="relative">
                                        <div
                                            className="absolute -inset-1 bg-gradient-to-r from-blue-500/20 to-primary/10 rounded-2xl blur-lg opacity-70"></div>
                                        <div
                                            className="bg-white dark:bg-gray-800 rounded-3xl shadow-xl p-6 relative max-w-xs">
                                            <div className="flex items-center mb-4">
                                                <div
                                                    className="relative w-16 h-16 rounded-full overflow-hidden border-2 border-gray-200">
                                                    <Image
                                                        alt="Instafel Logo"
                                                        src="/instafel.jpg"
                                                        width={70}
                                                        height={70}
                                                        quality={80}
                                                        className="object-cover w-full h-full"
                                                    />
                                                </div>
                                                <div className="ml-3">
                                                    <h3 className="font-bold text-lg">Instafel</h3>
                                                    <p className="text-gray-500 text-sm">@instafel</p>
                                                </div>
                                            </div>

                                            <div className="space-y-3">
                                                <div className="bg-gray-100 dark:bg-gray-700/50 rounded-lg p-3">
                                                    <p className="text-gray-800 dark:text-gray-200 text-sm">
                                                        {t("join_community.5")}
                                                    </p>
                                                    <p className="text-gray-500 dark:text-gray-400 text-xs mt-2">
                                                        {t("join_community.6")}
                                                    </p>
                                                </div>
                                                <div className="bg-gray-100 dark:bg-gray-700/50 rounded-lg p-3">
                                                    <p className="text-gray-800 dark:text-gray-200 text-sm">
                                                        {t("join_community.7")}
                                                    </p>
                                                    <p className="text-gray-500 dark:text-gray-400 text-xs mt-2">
                                                        {t("join_community.8")}
                                                    </p>
                                                </div>
                                            </div>

                                            <div
                                                className="flex items-center justify-between mt-4 text-xs text-gray-500">
                                                <span> {t("join_community.9")} </span>
                                                <span> {t("join_community.10")} </span>
                                            </div>
                                        </div>
                                    </div>
                                </motion.div>
                            </div>
                        </div>
                    </section>

                    <motion.section
                        initial={{opacity: 0, y: 30}}
                        whileInView={{opacity: 1, y: 0}}
                        viewport={{once: true}}
                        transition={{duration: 0.6}}
                        className="py-16 px-4 my-12 text-center bg-gradient-to-r from-primary/10 to-primary/5 rounded-3xl"
                    >
                        <h2 className="text-3xl md:text-4xl font-bold mb-4">
                            {t("try_now.1")}
                        </h2>
                        <p className="text-muted-foreground mb-8 max-w-xl mx-auto">
                            {t("try_now.2")}
                        </p>
                        <div className="flex flex-col sm:flex-row gap-4 justify-center">
                            <Button
                                asChild
                                size="lg"
                                className="hover:scale-105 transition-transform duration-300"
                            >
                                <Link href="/download?version=latest">
                                    <Download className="mr-2"/>
                                    {t("try_now.3")}
                                </Link>
                            </Button>
                            <Button
                                asChild
                                size="lg"
                                variant={"outline"}
                                className="hover:scale-105 transition-transform duration-300"
                            >
                                <Link href="/about_updater">
                                    <RefreshCw className="mr-2"/>
                                    {t("try_now.4")}
                                </Link>
                            </Button>
                        </div>
                    </motion.section>
                </div>
                <Footer/>
            </main>
        </>
    );
}
