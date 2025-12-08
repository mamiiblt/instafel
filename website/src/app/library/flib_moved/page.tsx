"use client"

import {motion} from "framer-motion"
import React, {useEffect, useState} from "react"
import {ArrowRight, Flag, Send} from "lucide-react"
import {useTranslation} from "react-i18next"
import Navbar from "@/components/Navbar";
import Footer from "@/components/Footer";

export default function MovedPage() {
    const [isLoaded, setIsLoaded] = useState(false)
    const {t} = useTranslation("flib_moved")

    useEffect(() => {
        setIsLoaded(true)
    }, [])

    const containerVariants = {
        hidden: {opacity: 0},
        visible: {
            opacity: 1,
            transition: {
                staggerChildren: 0.2,
                delayChildren: 0.3,
            },
        },
    }

    const itemVariants = {
        hidden: {opacity: 0, y: 20},
        visible: {
            opacity: 1,
            y: 0,
            transition: {duration: 0.8, ease: "easeOut" as const},
        },
    }

    const floatingVariants = {
        animate: {
            y: [0, -20, 0],
            transition: {
                duration: 4,
                repeat: Number.POSITIVE_INFINITY,
                ease: "easeInOut" as const,
            },
        },
    }

    return (
        <div>
            <Navbar/>
            <div
                className="min-h-screen bg-gradient-to-br from-background via-background to-background overflow-hidden relative">
                <motion.div
                    className="absolute top-20 right-10 w-72 h-72 bg-gradient-to-br from-blue-400/20 to-blue-600/20 rounded-full blur-3xl"
                    animate={{x: [0, 30, 0], y: [0, -30, 0]}}
                    transition={{duration: 8, repeat: Number.POSITIVE_INFINITY, ease: "easeInOut"}}
                />
                <motion.div
                    className="absolute bottom-20 left-10 w-72 h-72 bg-gradient-to-tr from-purple-400/20 to-pink-600/20 rounded-full blur-3xl"
                    animate={{x: [0, -30, 0], y: [0, 30, 0]}}
                    transition={{duration: 10, repeat: Number.POSITIVE_INFINITY, ease: "easeInOut"}}
                />

                <div className="relative z-10 min-h-screen flex flex-col items-center justify-center px-4 py-20">
                    <motion.div
                        className="w-full max-w-3xl"
                        variants={containerVariants}
                        initial="hidden"
                        animate={isLoaded ? "visible" : "hidden"}
                    >
                        <motion.div className="flex justify-center mb-12" variants={itemVariants}>
                            <motion.div variants={floatingVariants} animate="animate">
                                <Flag className="w-16 h-16 text-blue-500 dark:text-blue-400" strokeWidth={1.5} />
                            </motion.div>
                        </motion.div>

                        <motion.div variants={itemVariants} className="text-center mb-8">
                            <h1 className="text-5xl md:text-7xl font-bold bg-clip-text text-transparent bg-gradient-to-r from-foreground via-foreground to-foreground mb-4">
                                {t("movedTitle")}
                            </h1>
                            <p className="text-xl md:text-2xl text-muted-foreground font-light">{t("movedSubtitle")}</p>
                        </motion.div>

                        <motion.div variants={itemVariants}
                                    className="flex flex-col sm:flex-row gap-6 justify-center mb-20">
                            <motion.a
                                href="https://t.me/instafel"
                                target="_blank"
                                rel="noopener noreferrer"
                                className="group relative inline-flex items-center justify-center px-8 py-4 text-lg font-semibold rounded-full bg-gradient-to-r from-blue-500 to-blue-600 text-white hover:shadow-lg transition-all duration-300"
                                whileHover={{scale: 1.05}}
                                whileTap={{scale: 0.95}}
                            >
              <span className="flex items-center gap-2">
                <Send className="w-5 h-5"/>
                  {t("goToTelegram")}
                  <ArrowRight className="w-5 h-5 group-hover:translate-x-1 transition-transform"/>
              </span>
                            </motion.a>

                            <motion.a
                                href="https://t.me/instafel/13/146660"
                                className="group relative inline-flex items-center justify-center px-8 py-4 text-lg font-semibold rounded-full border-2 border-foreground/30 text-foreground hover:bg-foreground/5 transition-all duration-300"
                                whileHover={{scale: 1.05}}
                                whileTap={{scale: 0.95}}
                            >
                                {t("learnMore")}
                            </motion.a>
                        </motion.div>

                        <motion.div variants={itemVariants} className="grid md:grid-cols-3 gap-6">
                            {[
                                {
                                    title: t("directAccessTitle"),
                                    description: t("directAccessDesc"),
                                    icon: "⚡",
                                },
                                {
                                    title: t("communityTitle"),
                                    description: t("communityDesc"),
                                    icon: "👥",
                                },
                                {
                                    title: t("resourcesTitle"),
                                    description: t("resourcesDesc"),
                                    icon: "📚",
                                },
                            ].map((card, idx) => (
                                <motion.div
                                    key={idx}
                                    className="p-6 rounded-2xl bg-card/50 backdrop-blur-sm border border-foreground/10 hover:border-foreground/20 transition-all duration-300"
                                    whileHover={{y: -5}}
                                    variants={itemVariants}
                                >
                                    <div className="text-3xl mb-4">{card.icon}</div>
                                    <h3 className="text-lg font-semibold mb-2 text-foreground">{card.title}</h3>
                                    <p className="text-sm text-foreground/60">{card.description}</p>
                                </motion.div>
                            ))}
                        </motion.div>
                    </motion.div>
                </div>
            </div>
            <Footer/>
        </div>
    )
}
