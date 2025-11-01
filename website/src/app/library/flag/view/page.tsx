"use client";

import {useEffect, useState} from "react";
import * as DialogPrimitive from "@radix-ui/react-dialog";
import Navbar from "@/components/Navbar";
import Footer from "@/components/Footer";
import {useTranslation} from "react-i18next";
import {useSearchParams} from "next/navigation";
import {iflApiBase, flagCategories} from "@/wdata/flag_sdata";
import {
    AlertCircle,
    BadgeIcon as IdCardLanyardIcon,
    Clock,
    Flag,
    FlagIcon,
    ImageIcon,
    Info,
    MinusIcon,
    PlusIcon,
    TagIcon,
    TrashIcon,
    UserIcon,
    X,
    ZoomIn,
} from "lucide-react";
import {Card, CardContent, CardHeader, CardTitle} from "@/components/ui/card";
import {Badge} from "@/components/ui/badge";
import {motion} from "framer-motion";
import {Button} from "@/components/ui/button";
import {Dialog, DialogContent, DialogTrigger} from "@/components/ui/dialog";
import {FlagCont} from "@/wdata/mconfig";
import MetaConfigContent from "@/components/MetaConfigContent";
import Link from "next/link";
import {Page} from "@/components/PageUtils";

interface FlagData {
    id: number;
    last_edit: string;
    category_id: string;
    added_by: string;
    title: string;
    description: string;
    added_in?: string;
    removed_in?: string;
    flags: FlagCont[];
    screenshots: string[];
}

export default function FlagInfoPage() {
    const {t, i18n} = useTranslation(["flag", "fcategories"]);
    const searchParams = useSearchParams();
    const ID = searchParams.get("id") ?? "0";
    const [flagData, setFlagData] = useState<FlagData | null>(null);
    const [loading, setLoading] = useState(true);
    const [imageLoadErrors, setImageLoadErrors] = useState<Set<string>>(
        new Set()
    );

    useEffect(() => {
        const loadFlagData = async () => {
            try {
                const res = await fetch(`${iflApiBase}/content/flib/flag/${ID}`);
                if (res.status !== 404) {
                    const data: FlagData = await res.json();
                    setFlagData(data);
                }
            } catch (error) {
                console.error("An error occured while loading flag data:", error);
            } finally {
                setLoading(false);
            }
        };

        loadFlagData();
    }, [ID]);

    const handleImageError = (url: string) => {
        setImageLoadErrors((prev) => new Set([...prev, url]));
    };

    const getImageUrl = (screenshot: string) => {
        return `https://raw.githubusercontent.com/instafel/images/refs/heads/main/flib/${screenshot}`;
    };

    const fadeInUp = {
        initial: {opacity: 0, y: 30},
        animate: {opacity: 1, y: 0},
        transition: {duration: 0.6, ease: "easeOut"},
    };

    const slideInLeft = {
        initial: {opacity: 0, x: -30},
        animate: {opacity: 1, x: 0},
        transition: {duration: 0.6, ease: "easeOut"},
    };

    const slideInRight = {
        initial: {opacity: 0, x: 30},
        animate: {opacity: 1, x: 0},
        transition: {duration: 0.6, ease: "easeOut"},
    };

    if (loading) {
        return (
            <>
                <Navbar/>
                <div
                    className="min-h-screen bg-gradient-to-br from-background via-background to-muted/20 flex items-center justify-center p-4">
                    <motion.div
                        initial={{opacity: 0, scale: 1}}
                        animate={{opacity: 1, scale: 1}}
                        transition={{duration: 0.5}}
                        className="text-center space-y-4"
                    >
                        <div className="relative">
                            <div
                                className="w-16 h-16 mx-auto mb-4 rounded-full bg-primary/10 flex items-center justify-center">
                                <Flag className="h-8 w-8 text-primary animate-pulse"/>
                            </div>
                            <div
                                className="absolute inset-0 w-16 h-16 mx-auto rounded-full border-2 border-primary/20 border-t-primary animate-spin"/>
                        </div>
                        <div className="space-y-2">
                            <h3 className="text-lg font-semibold">{t("loading.title")}</h3>
                            <p className="text-sm text-muted-foreground">
                                {t("loading.description")}
                            </p>
                        </div>
                    </motion.div>
                </div>
                <Footer/>
            </>
        );
    }

    if (!flagData) {
        return (
            <>
                <Navbar/>
                <div
                    className="min-h-screen bg-gradient-to-br from-background via-background to-muted/20 flex items-center justify-center p-4">
                    <motion.div
                        initial={{opacity: 0, y: 20}}
                        animate={{opacity: 1, y: 0}}
                        transition={{duration: 0.6}}
                    >
                        <Card className="w-full max-w-md shadow-lg border-destructive/20">
                            <CardContent
                                className="flex flex-col items-center justify-center p-8 text-center space-y-4">
                                <div
                                    className="w-16 h-16 rounded-full bg-destructive/10 flex items-center justify-center">
                                    <AlertCircle className="h-8 w-8 text-destructive"/>
                                </div>
                                <div className="space-y-2">
                                    <h2 className="text-xl font-semibold text-foreground">
                                        {t("not_found.title")}
                                    </h2>
                                    <p className="text-muted-foreground text-sm leading-relaxed">
                                        {t("not_found.description")}
                                    </p>
                                </div>
                                <Button variant="outline" onClick={() => window.history.back()}>
                                    {t("not_found.go_back")}
                                </Button>
                            </CardContent>
                        </Card>
                    </motion.div>
                </div>
                <Footer/>
            </>
        );
    }

    return (
        <Page
            width={6}
            content={<>
                <motion.div
                    className="mb-8"
                    initial={{opacity: 0, y: -20}}
                    animate={{opacity: 1, y: 0}}
                    transition={{duration: 0.6, delay: 0.1}}
                >
                    <div className="text-center space-y-4">
                        <motion.h1
                            className="text-3xl font-bold"
                            initial={{opacity: 0, y: 20}}
                            animate={{opacity: 1, y: 0}}
                            transition={{duration: 0.6, delay: 0.3}}
                        >
                            {flagData.title}
                        </motion.h1>

                        <motion.div
                            className="flex items-center justify-center gap-3 flex-wrap"
                            initial={{opacity: 0, y: 20}}
                            animate={{opacity: 1, y: 0}}
                            transition={{duration: 0.6, delay: 0.4}}
                        >
                            <Link
                                href={`/library/flag/category?cid=${flagData.category_id}&page=1`}
                            >
                                <motion.div
                                    whileHover={{scale: 1.05}}
                                    whileTap={{scale: 0.95}}
                                >
                                    <Badge variant="secondary" className="px-3 py-1">
                                        <TagIcon className="w-3 h-3 mr-1"/>
                                        {t(flagCategories[flagData.category_id].cif, {
                                            ns: "fcategories",
                                        })}
                                    </Badge>
                                </motion.div>
                            </Link>

                            {flagData.removed_in && (
                                <motion.div
                                    whileHover={{scale: 1.05}}
                                    whileTap={{scale: 0.95}}
                                    initial={{opacity: 0, scale: 0.8}}
                                    animate={{opacity: 1, scale: 1}}
                                    transition={{duration: 0.4, delay: 0.5}}
                                >
                                    <Badge variant="destructive" className="px-3 py-1">
                                        <TrashIcon className="w-3 h-3 mr-1"/>
                                        {t("badges.removed")}
                                    </Badge>
                                </motion.div>
                            )}

                            <motion.div
                                whileHover={{scale: 1.05}}
                                whileTap={{scale: 0.95}}
                            >
                                <Badge variant="outline" className="px-3 py-1">
                                    <IdCardLanyardIcon className="w-3 h-3 mr-1"/>
                                    {flagData.id}
                                </Badge>
                            </motion.div>
                        </motion.div>
                    </div>
                </motion.div>

                <div className="grid grid-cols-1 lg:grid-cols-3 gap-6 items-stretch">
                    <motion.div
                        className="lg:col-span-2 h-full"
                        {...slideInLeft}
                        transition={{duration: 0.6, delay: 0.5}}
                    >
                        <motion.div
                            className="h-full"
                            whileHover={{
                                y: -5,
                                boxShadow:
                                    "0 20px 25px -5px rgb(0 0 0 / 0.1), 0 8px 10px -6px rgb(0 0 0 / 0.1)",
                            }}
                            transition={{duration: 0.2}}
                        >
                            <Card className="h-full shadow-md transition-shadow duration-300">
                                <CardContent className="flex-1 pt-6">
                                    <motion.p
                                        className="text-muted-foreground leading-relaxed text-base"
                                        initial={{opacity: 0}}
                                        animate={{opacity: 1}}
                                        transition={{duration: 0.6, delay: 0.7}}
                                    >
                                        {flagData.description}
                                    </motion.p>
                                </CardContent>
                            </Card>
                        </motion.div>
                    </motion.div>

                    <motion.div
                        className="h-full"
                        {...slideInRight}
                        transition={{duration: 0.6, delay: 0.6}}
                    >
                        <motion.div
                            className="h-full"
                            whileHover={{
                                y: -5,
                                boxShadow:
                                    "0 20px 25px -5px rgb(0 0 0 / 0.1), 0 8px 10px -6px rgb(0 0 0 / 0.1)",
                            }}
                            transition={{duration: 0.2}}
                        >
                            <Card className="h-full shadow-md transition-shadow duration-300">
                                <CardHeader className="pb-4">
                                    <CardTitle className="flex items-center gap-2 text-xl">
                                        <Info className="w-5 h-5 text-primary"/>
                                        {t("content.block_titles.information")}
                                    </CardTitle>
                                </CardHeader>
                                <CardContent className="space-y-4 flex-1">
                                    <div className="space-y-3">
                                        {[
                                            {
                                                icon: UserIcon,
                                                label: t("content.block_contents.info.added_by"),
                                                value: flagData.added_by,
                                            },
                                            {
                                                icon: Clock,
                                                label: t("content.block_contents.info.last_edit"),
                                                value: new Intl.DateTimeFormat(i18n.language, {
                                                    dateStyle: "medium",
                                                    timeStyle: "short",
                                                }).format(new Date(flagData.last_edit)),
                                            },
                                            ...(flagData.added_in
                                                ? [
                                                    {
                                                        icon: PlusIcon,
                                                        label: t(
                                                            "content.block_contents.info.added_in"
                                                        ),
                                                        value: "v" + flagData.added_in,
                                                    },
                                                ]
                                                : [
                                                    {
                                                        icon: PlusIcon,
                                                        label: t(
                                                            "content.block_contents.info.added_in"
                                                        ),
                                                        value: t("content.block_contents.info.unknown"),
                                                    },
                                                ]),
                                            ...(flagData.removed_in
                                                ? [
                                                    {
                                                        icon: MinusIcon,
                                                        label: t(
                                                            "content.block_contents.info.removed_in"
                                                        ),
                                                        value: "v" + flagData.removed_in,
                                                    },
                                                ]
                                                : []),
                                        ].map((item, index) => (
                                            <motion.div
                                                key={index}
                                                className={`flex items-start gap-3 p-3 rounded-lg bg-muted/50 transition-colors`}
                                                initial={{opacity: 0, x: 20}}
                                                animate={{opacity: 1, x: 0}}
                                                transition={{
                                                    duration: 0.4,
                                                    delay: 0.8 + index * 0.1,
                                                }}
                                                whileHover={{scale: 1.02}}
                                            >
                                                <item.icon className={`w-4 h-4 mt-0.5`}/>
                                                <div className="flex-1 min-w-0">
                                                    <p className="text-sm font-medium text-foreground">
                                                        {item.label}
                                                    </p>
                                                    {item.value == flagData.added_by ? (
                                                        <Link
                                                            href={`https://t.me/${flagData.added_by}`}
                                                            target="_blank"
                                                        >
                                                            <p
                                                                className={`text-sm text-muted-foreground underline truncate`}
                                                            >
                                                                {item.value}
                                                            </p>
                                                        </Link>
                                                    ) : (
                                                        <p
                                                            className={`text-sm text-muted-foreground ${
                                                                item.value === flagData.added_in ||
                                                                item.value === flagData.removed_in
                                                                    ? "font-mono"
                                                                    : ""
                                                            } truncate`}
                                                        >
                                                            {item.value}
                                                        </p>
                                                    )}
                                                </div>
                                            </motion.div>
                                        ))}
                                    </div>
                                </CardContent>
                            </Card>
                        </motion.div>
                    </motion.div>
                </div>

                {flagData.screenshots && flagData.screenshots.length > 0 && (
                    <motion.div
                        className="mt-6"
                        {...fadeInUp}
                        transition={{duration: 0.6, delay: 0.8}}
                    >
                        <motion.div whileHover={{y: -3}} transition={{duration: 0.2}}>
                            <Card className="shadow-md hover:shadow-lg transition-shadow duration-300">
                                <CardHeader className="pb-4">
                                    <CardTitle className="flex items-center gap-2 text-xl">
                                        <ImageIcon className="w-5 h-5 text-primary"/>
                                        {t("content.block_titles.screenshots")}
                                    </CardTitle>
                                </CardHeader>
                                <CardContent>
                                    <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4">
                                        {flagData.screenshots.map((screenshot, index) => (
                                            <motion.div
                                                key={index}
                                                className="group relative aspect-video bg-muted rounded-lg overflow-hidden border hover:border-primary/30 transition-all duration-300"
                                                initial={{opacity: 0, scale: 0.8, y: 30}}
                                                animate={{opacity: 1, scale: 1, y: 0}}
                                                transition={{
                                                    duration: 0.5,
                                                    delay: 1.1 + index * 0.1,
                                                    type: "spring",
                                                    stiffness: 100,
                                                }}
                                                whileHover={{
                                                    scale: 1.03,
                                                    y: -5,
                                                    transition: {duration: 0.2},
                                                }}
                                            >
                                                {!imageLoadErrors.has(screenshot) ? (
                                                    <Dialog>
                                                        <DialogTrigger asChild>
                                                            <div className="relative cursor-pointer h-full">
                                                                <img
                                                                    src={
                                                                        getImageUrl(screenshot) ||
                                                                        "/placeholder.svg"
                                                                    }
                                                                    alt={`Screenshot ${index + 1} of ${
                                                                        flagData.title
                                                                    }`}
                                                                    className="w-full h-full object-cover transition-transform duration-300 group-hover:scale-105"
                                                                    onError={() => handleImageError(screenshot)}
                                                                    loading="lazy"
                                                                />
                                                                <div
                                                                    className="absolute inset-0 bg-black/0 group-hover:bg-black/20 transition-colors duration-300 flex items-center justify-center">
                                                                    <ZoomIn
                                                                        className="w-6 h-6 text-white opacity-0 group-hover:opacity-100 transition-opacity duration-300"/>
                                                                </div>
                                                                <div
                                                                    className="absolute bottom-2 right-2 bg-black/70 text-white text-xs px-2 py-1 rounded opacity-0 group-hover:opacity-100 transition-opacity duration-300">
                                                                    {index + 1} / {flagData.screenshots.length}
                                                                </div>
                                                            </div>
                                                        </DialogTrigger>
                                                        <DialogContent
                                                            className="max-w-4xl w-full p-4 bg-background border"
                                                            showCloseButton={false}
                                                        >
                                                            <motion.div
                                                                className="flex"
                                                                initial={{opacity: 0, scale: 0.9}}
                                                                animate={{opacity: 1, scale: 1}}
                                                                transition={{duration: 0.3}}
                                                            >
                                                                <img
                                                                    src={
                                                                        getImageUrl(screenshot) ||
                                                                        "/placeholder.svg"
                                                                    }
                                                                    className="w-full h-auto max-h-[80vh] object-contain rounded-lg"
                                                                />
                                                                <DialogPrimitive.Close data-slot="dialog-close">
                                                                    <div
                                                                        className="inline-flex items-center justify-center gap-2 whitespace-nowrap rounded-md text-sm font-medium transition-all disabled:pointer-events-none disabled:opacity-50 [&_svg]:pointer-events-none [&_svg:not([class*='size-'])]:size-4 shrink-0 [&_svg]:shrink-0 outline-none focus-visible:border-ring focus-visible:ring-ring/50 focus-visible:ring-[3px] aria-invalid:ring-destructive/20 dark:aria-invalid:ring-destructive/40 aria-invalid:border-destructive border bg-background shadow-xs hover:bg-accent hover:text-accent-foreground dark:bg-input/30 dark:border-input dark:hover:bg-input/50 size-9 absolute top-2 right-2 bg-background/80 hover:bg-background border"
                                                                    >
                                                                        <X className="h-4 w-4"/>
                                                                    </div>
                                                                </DialogPrimitive.Close>
                                                            </motion.div>
                                                        </DialogContent>
                                                    </Dialog>
                                                ) : (
                                                    <div
                                                        className="flex flex-col items-center justify-center h-full text-muted-foreground bg-muted/50">
                                                        <ImageIcon className="w-8 h-8 mb-2"/>
                                                        <p className="text-xs text-center px-2">
                                                            {t(
                                                                "content.block_contents.screenshot.failed_load_img"
                                                            )}
                                                        </p>
                                                    </div>
                                                )}
                                            </motion.div>
                                        ))}
                                    </div>
                                </CardContent>
                            </Card>
                        </motion.div>
                    </motion.div>
                )}

                <motion.div
                    className="mt-6"
                    {...fadeInUp}
                    transition={{duration: 0.6, delay: 0.7}}
                >
                    <motion.div whileHover={{y: -3}} transition={{duration: 0.2}}>
                        <Card className="shadow-md hover:shadow-lg transition-shadow duration-300">
                            <CardHeader className="pb-4">
                                <CardTitle className="flex items-center gap-2 text-xl">
                                    <FlagIcon className="w-5 h-5 text-primary"/>
                                    {t("content.block_titles.metaconf_options")}
                                </CardTitle>
                            </CardHeader>
                            <CardContent>
                                <MetaConfigContent configData={flagData.flags}/>
                            </CardContent>
                        </Card>
                    </motion.div>
                </motion.div>
            </>
            }
        />
    );
}
