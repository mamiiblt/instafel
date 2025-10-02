"use client";

import Footer from "@/components/Footer";
import Navbar from "@/components/Navbar";
import {Button} from "@/components/ui/button";
import {Card, CardContent, CardDescription, CardHeader, CardTitle,} from "@/components/ui/card";
import {SidebarProvider} from "@/components/ui/sidebar";
import {WikiSidebar} from "@/components/wiki/WikiSidebar";
import {motion} from "framer-motion";
import {ArrowRight, BookOpen, FileText, HelpCircle} from "lucide-react";
import Link from "next/link";

export default function PageHome() {
    return (
        <>
            <SidebarProvider>
                <WikiSidebar/>
                <div className="flex-1 overflow-auto">
                    <Navbar/>
                    <div className="min-h-screen bg-background">
                        <section className="py-16 md:py-24">
                            <div className="container mx-auto px-4 text-center max-w-3xl">
                                <motion.div
                                    initial={{opacity: 0, y: 30}}
                                    animate={{opacity: 1, y: 0}}
                                    transition={{duration: 0.6}}
                                >
                                    <h1 className="text-4xl md:text-5xl font-bold mb-4">
                                        Instafel Wiki
                                    </h1>
                                    <p className="text-xl text-muted-foreground mb-8">
                                        Everything you need to know about Instafel. Find guides,
                                        tutorials, API and sub-project documentations!
                                    </p>

                                    <motion.div
                                        whileHover={{scale: 1.05}}
                                        whileTap={{scale: 0.95}}
                                    >
                                        <Link href={"/wiki/what-is-instafel"}>
                                            <Button size="lg" className="h-12 px-8">
                                                Get Started!
                                                <ArrowRight className="ml-2 h-4 w-4"/>
                                            </Button>
                                        </Link>
                                    </motion.div>
                                </motion.div>
                            </div>
                        </section>

                        <section className="py-16">
                            <div className="container mx-auto px-4">
                                <motion.h2
                                    initial={{opacity: 0}}
                                    whileInView={{opacity: 1}}
                                    className="text-2xl font-bold text-center mb-12"
                                >
                                    Quick Access
                                </motion.h2>

                                <div className="grid md:grid-cols-3 gap-6 max-w-4xl mx-auto">
                                    {[
                                        {
                                            icon: FileText,
                                            title: "What is Instafel?",
                                            description:
                                                "An article for explaing Instafel and Instafel stuffs",
                                            href: "/wiki/getting-started",
                                        },
                                        {
                                            icon: HelpCircle,
                                            title: "Sub-projects",
                                            description:
                                                "Read documentations about sub-projects of Instafel!",
                                            href: "/wiki/faq",
                                        },
                                        {
                                            icon: BookOpen,
                                            title: "API Reference",
                                            description: "Use Instafel API with in your projects!",
                                            href: "/wiki/api",
                                        },
                                    ].map((item, index) => (
                                        <motion.div
                                            key={index}
                                            initial={{opacity: 0, y: 20}}
                                            whileInView={{opacity: 1, y: 0}}
                                            transition={{delay: index * 0.1}}
                                            whileHover={{y: -5}}
                                        >
                                            <Card
                                                className="h-full cursor-pointer hover:shadow-lg transition-shadow dark:bg-black dark:border-gray-800">
                                                <CardHeader>
                                                    <div
                                                        className="w-10 h-10 bg-blue-100 dark:bg-gray-800 rounded-lg flex items-center justify-center mb-3">
                                                        <item.icon
                                                            className="h-5 w-5 text-blue-600 dark:text-blue-400"/>
                                                    </div>
                                                    <CardTitle className="text-lg">
                                                        {item.title}
                                                    </CardTitle>
                                                </CardHeader>
                                                <CardContent>
                                                    <CardDescription>{item.description}</CardDescription>
                                                </CardContent>
                                            </Card>
                                        </motion.div>
                                    ))}
                                </div>
                            </div>
                        </section>
                    </div>
                </div>
            </SidebarProvider>
            <Footer/>
        </>
    );
}
