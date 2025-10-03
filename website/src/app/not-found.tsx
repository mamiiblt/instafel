"use client";

import {motion} from "framer-motion";
import {Button} from "@/components/ui/button";
import {useTranslation} from "react-i18next";
import {Home} from "lucide-react";
import Link from "next/link";

export default function NotFound() {
    const {t} = useTranslation("misc");

    return (
        <motion.div
            initial={{opacity: 0, y: 40, scale: 0.95}}
            animate={{opacity: 1, y: 0, scale: 1}}
            transition={{duration: 0.5, ease: "easeOut"}}
            className="flex flex-col items-center justify-center min-h-screen gap-6 md:gap-8 p-6 md:p-8 text-center bg-background"
        >
            <motion.div
                initial={{opacity: 0, y: 20}}
                animate={{opacity: 1, y: 0}}
                transition={{delay: 0.2, duration: 0.4}}
                className="space-y-3 md:space-y-4"
            >
                <motion.h2
                    initial={{scale: 0.8}}
                    animate={{scale: 1}}
                    transition={{type: "spring", stiffness: 200}}
                    className="text-2xl md:text-3xl text-red-500 font-bold"
                >
                    {t("not_found.1")}
                </motion.h2>
                <h2 className="text-3xl md:text-4xl lg:text-5xl font-bold tracking-tight">
                    {t("not_found.2")}
                </h2>
            </motion.div>
            <motion.p
                initial={{opacity: 0}}
                animate={{opacity: 1}}
                transition={{delay: 0.4}}
                className="text-base md:text-lg text-muted-foreground max-w-lg mx-auto"
            >
                {t("not_found.3")}
            </motion.p>
            <motion.div
                initial={{opacity: 0, y: 20}}
                animate={{opacity: 1, y: 0}}
                transition={{delay: 0.6}}
            >
                <Button
                    asChild
                    className="mt-4 px-6 py-2 text-base md:text-lg hover:scale-105 transition-transform"
                >
                    <Link href="/" className="flex items-center gap-3">
                        <Home className="h-5 w-5"/>
                        <span>{t("not_found.4")}</span>
                    </Link>
                </Button>
            </motion.div>
        </motion.div>
    );
}
