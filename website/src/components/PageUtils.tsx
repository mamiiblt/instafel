import {motion} from "framer-motion";
import React, {cloneElement, ReactNode} from "react";
import Navbar from "@/components/Navbar";
import Footer from "@/components/Footer";
import {LoadingBar} from "@/components/LoadingBars";

export function PageHeader({icon: Icon, title, subtitle}: { icon: ReactNode, title: string, subtitle: string }) {
    return (
        <div className="flex flex-col items-center justify-center mb-8">
            <motion.div
                initial={{opacity: 0, x: -20}}
                animate={{opacity: 1, x: 0}}
                transition={{duration: 0.5}}
                className="flex flex-col items-center justify-center"
            >
                {Icon && cloneElement(Icon as any, {className: "h-8 w-8 mb-2"})}
                <h1 className="text-3xl font-bold tracking-tight text-center">{title}</h1>
                <p className="text-center text-muted-foreground mt-2">{subtitle}</p>
            </motion.div>
        </div>
    )
}

export function Page({width, header, content}: { width: number, header?: ReactNode, content: ReactNode }) {
    return (
        <div>
            <Navbar/>
            <div className={`container max-w-${width}xl mx-auto py-8 px-4`}>
                {header && header}
                {content}
            </div>
            <Footer/>
        </div>
    )
}

export function PageLoading() {
    return (
        <>
            <Navbar/>
            <LoadingBar/>
            <Footer/>
        </>
    )
}