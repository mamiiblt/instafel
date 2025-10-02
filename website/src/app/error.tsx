"use client";

import {motion} from "framer-motion";
import {Button} from "@/components/ui/button";
import {Home, RotateCcw} from "lucide-react";
import Link from "next/link";

export default function Error({
                                  error,
                                  reset,
                              }: {
    error: Error & { digest?: string };
    reset: () => void;
}) {
    return (
        <motion.div
            initial={{opacity: 0, y: 40}}
            animate={{opacity: 1, y: 0}}
            transition={{duration: 0.5, ease: "easeOut"}}
            className="flex flex-col items-center justify-center min-h-[80vh] gap-6 p-6 text-center bg-background"
        >
            <motion.h2
                initial={{scale: 0.8}}
                animate={{scale: 1}}
                transition={{delay: 0.2, duration: 0.4}}
                className="text-3xl md:text-4xl font-bold tracking-tight"
            >
                Something went wrong
            </motion.h2>
            <motion.p
                initial={{opacity: 0}}
                animate={{opacity: 1}}
                transition={{delay: 0.4}}
                className="text-base md:text-lg text-muted-foreground max-w-lg mx-auto leading-relaxed"
            >
                {error.message}
            </motion.p>
            <motion.div
                initial={{opacity: 0, y: 20}}
                animate={{opacity: 1, y: 0}}
                transition={{delay: 0.6}}
                className="flex flex-col sm:flex-row gap-4 sm:gap-6 w-full max-w-md justify-center"
            >
                <Button
                    onClick={reset}
                    variant="outline"
                    className="w-full sm:w-auto hover:scale-105 transition-transform"
                >
                    <RotateCcw className="w-4 h-4 mr-2"/>
                    Retry
                </Button>
                <Button
                    asChild
                    className="w-full sm:w-auto hover:scale-105 transition-transform"
                >
                    <Link href="/">
                        <Home className="w-4 h-4 mr-2"/>
                        Home
                    </Link>
                </Button>
            </motion.div>
        </motion.div>
    );
}
