"use client";

import React, {useEffect, useState} from 'react';
import {AnimatePresence, motion} from 'framer-motion';
import {
    Download,
    Info,
    Calendar,
    Hash,
    Package,
    Shield,
    LogsIcon,
    DownloadIcon,
    Star, LucideIcon, SquaresExcludeIcon
} from 'lucide-react';
import { Badge } from '@/components/ui/badge';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Dialog, DialogContent, DialogDescription, DialogHeader, DialogTitle, DialogTrigger } from '@/components/ui/dialog';
import Footer from "@/components/Footer";
import { useTranslation } from "react-i18next";
import Navbar from "@/components/Navbar";
import {contentAPIURL} from "@/wdata/flag_sdata";
import { LoadingBar } from "@/components/LoadingBars";
import {useSearchParams} from "next/navigation";
import {toast} from "sonner";

interface RelInfo {
    manifest_version: number;
    release_date: string;
    is_deleted: boolean;
    changelogs: string[];
    patcher: {
        commit: string;
        version: string;
    },
    hash: {
        unclone: string;
        clone: string;
    },
    fnames: {
        unclone: string;
        clone: string;
    }
    patcher_data: {
        ifl: {
            version: number;
            gen_id: string;
        },
        ig: {
            version: string;
            ver_code: string;
        }
    }
}

const item = {
    hidden: { opacity: 0, y: 20 },
    show: { opacity: 1, y: 0 }
};


export function InfoTileComp({ icon: Icon, title, subtitle, copiable, copyData }: {
    icon: LucideIcon
    title: string
    subtitle: string
    copiable?: boolean
    copyData?: string
}) {
    return (
        <div
            onClick={() => copiable && copyData && copyToClipboard(copyData)}
            className={`grid grid-cols-[auto_1fr] gap-2 items-start ${copiable ? "cursor-pointer hover:opacity-80 transition-opacity" : ""}`}
        >
            <Icon className="h-3.5 w-3.5 text-muted-foreground mt-0.5" />
            <div className="space-y-1">
                <p className="text-sm text-muted-foreground leading-none">{title}</p>
                <p className="font-medium font-mono text-sm leading-none">{subtitle}</p>
            </div>
        </div>
    )
}
type BadgeVariant = React.ComponentProps<typeof Badge>["variant"];

export function VariantCard({ badges, cardTitle, cardDesc, dialogInfo, downloadText, downloadDataInfo }: {
    badges: {
        text: string;
        icon: LucideIcon;
        variant: BadgeVariant;
        className: string;
    }[];
    cardTitle: string;
    cardDesc: string;
    dialogInfo: {
        title: string;
        description: string;
    };
    downloadText: string;
    downloadDataInfo: {
        iflVersion: number;
        fileName: string;
    }
}) {
    return (
        <motion.div variants={item}>
            <Card className="h-full border-2 hover:border-primary transition-colors">
                <CardHeader>
                    <div className="flex items-start justify-between    ">
                        <div className="flex flex-wrap gap-2">
                            {badges.map(({ text, icon: Icon, variant, className }, i) => (
                                <Badge key={i}
                                       variant={variant}
                                       className={className}>
                                    <Icon className={"mr-1 h-3 w-3"}/>
                                    {text}
                                </Badge>
                            ))}
                        </div>
                    </div>
                    <CardTitle className="text-2xl mb-2">{cardTitle}</CardTitle>
                    <CardDescription className="text-base">{cardDesc}</CardDescription>
                </CardHeader>
                <CardContent className="space-y-4">
                    <div className="flex items-center gap-2">
                        <Button
                            className="flex-1"
                            size="lg"
                            onClick={() =>
                                window.open(
                                    `https://github.com/mamiiblt/instafel/releases/download/v${downloadDataInfo.iflVersion}/${downloadDataInfo.fileName}`,
                                    "_blank",
                                )
                            }
                        >
                            <Download className="mr-2 h-5 w-5" />
                            {downloadText}
                        </Button>

                        <Dialog>
                            <DialogTrigger asChild>
                                <Button variant="default" size="lg" className="h-10 w-10 shrink-0">
                                    <Info className="h-5 w-5" />
                                </Button>
                            </DialogTrigger>
                            <DialogContent>
                                <DialogHeader>
                                    <DialogTitle>{dialogInfo.title}</DialogTitle>
                                    <DialogDescription className="pt-4">{dialogInfo.description}</DialogDescription>
                                </DialogHeader>
                            </DialogContent>
                        </Dialog>
                    </div>
                </CardContent>
            </Card>
        </motion.div>
    );
}

const copyToClipboard = (text: string) => {
    navigator.clipboard.writeText(text);
    toast("Copied", {
        description: "Copied to clipboard!",
        action: {
            label: "Okay",
            onClick: () => {},
        },
    });
};

export default function ReleaseInfoPage() {
    const { t, i18n } = useTranslation(["release"]);
    const searchParams = useSearchParams();
    const version = Number(searchParams.get("version")) ?? 1;
    const [data, setData] = useState<RelInfo>(null);

    useEffect(() => {
        const fetchData = async () => {
            var requestUrl = `${contentAPIURL}/content/rels/get/${version}`;
            const res = await fetch(requestUrl);
            const result = await res.json();
            setData(result);
        };
        fetchData();
    }, []);

    const formatDate = (lang: string, isoString: string) => {
        const date = new Date(isoString);
        return new Intl.DateTimeFormat(lang, {
            day: "2-digit",
            month: "2-digit",
            year: "numeric",
            hour: "2-digit",
            minute: "2-digit",
        }).format(date);
    };

    const container = {
        hidden: { opacity: 0 },
        show: {
            opacity: 1,
            transition: {
                staggerChildren: 0.1
            }
        }
    };

    return (
        <AnimatePresence>
            {data ? (
                <div>
                    <Navbar />
                    <div className="container max-w-5xl mx-auto py-8 px-4">
                        <div className="flex flex-col items-center justify-center mb-8">
                            <motion.div
                                initial={{ opacity: 0, x: -20 }}
                                animate={{ opacity: 1, x: 0 }}
                                transition={{ duration: 0.5 }}
                                className="flex flex-col items-center justify-center"
                            >
                                <DownloadIcon className="h-8 w-8 mb-2" />
                                <h1 className="text-3xl font-bold tracking-tight text-center">{t("title")}</h1>
                                <p className="text-center text-muted-foreground mt-2">{t("subtitle", { version: data.patcher_data.ifl.version })}</p>
                            </motion.div>
                        </div>

                        <motion.div
                            variants={container}
                            initial="hidden"
                            animate="show"
                            className="space-y-6"
                        >
                            <div className="grid md:grid-cols-2 gap-4">
                                <VariantCard
                                    badges={[
                                        {
                                            icon: Package,
                                            text: t("unclone"),
                                            variant: "secondary",
                                            className: "mb-3",
                                        },
                                        {
                                            icon: Star,
                                            text: t("recommended"),
                                            variant: "secondary",
                                            className: "ml-1 mb-3",
                                        }
                                    ]}
                                    cardTitle={t("releaseStr", { iflVersion: data.patcher_data.ifl.version })}
                                    cardDesc={t("uncloneDesc")}
                                    dialogInfo={{
                                        title: t("uncloneInfo"),
                                        description: t("uncloneInfoDesc")
                                    }}
                                    downloadText={t("download")}
                                    downloadDataInfo={{
                                        iflVersion: data.patcher_data.ifl.version,
                                        fileName: data.fnames.unclone
                                    }} />

                                <VariantCard
                                    badges={[
                                        {
                                            icon: Package,
                                            text: t("clone"),
                                            variant: "secondary",
                                            className: "mb-3",
                                        }
                                    ]}
                                    cardTitle={t("releaseStr", { iflVersion: data.patcher_data.ifl.version })}
                                    cardDesc={t("cloneDesc")}
                                    dialogInfo={{
                                        title: t("cloneInfo"),
                                        description: t("cloneInfoDesc")
                                    }}
                                    downloadText={t("download")}
                                    downloadDataInfo={{
                                        iflVersion: data.patcher_data.ifl.version,
                                        fileName: data.fnames.clone
                                    }} />
                            </div>

                            {data.changelogs.length > 0 && (
                                <motion.div variants={item}>
                                    <Card>
                                        <CardHeader>
                                            <CardTitle className="flex items-center gap-2">
                                                <LogsIcon className="h-5 w-5" />
                                                {t("changelogs")}
                                            </CardTitle>
                                        </CardHeader>
                                        <CardContent>
                                            <ul className="space-y-2">
                                                {data.changelogs.map((log, idx) => (
                                                    <li key={idx} className="flex items-center gap-3 text-sm">
                                                        <div className="h-1.5 w-1.5 rounded-full bg-primary flex-shrink-0" />
                                                        {log}
                                                    </li>
                                                ))}
                                            </ul>
                                        </CardContent>
                                    </Card>
                                </motion.div>
                            )}

                            <motion.div variants={item}>
                                <Card>
                                    <CardHeader>
                                        <CardTitle className="flex items-center gap-2">
                                            <Info className="h-5 w-5" />
                                            {t("releaseInfo")}
                                        </CardTitle>
                                    </CardHeader>
                                    <CardContent className="space-y-4">
                                        <div className="grid md:grid-cols-2 gap-4">
                                            <div className="space-y-3">
                                                <InfoTileComp
                                                    icon={Calendar}
                                                    title={t("releaseDate")}
                                                    subtitle={formatDate(i18n.language, data.release_date)} />
                                                <InfoTileComp
                                                    icon={Package}
                                                    title={t("igVersion")}
                                                    subtitle={`v${data.patcher_data.ig.version} (${data.patcher_data.ig.ver_code})`} />
                                                <InfoTileComp
                                                    icon={Hash}
                                                    title={t("generationId")}
                                                    subtitle={data.patcher_data.ifl.gen_id} />
                                            </div>

                                            <div className="space-y-3">
                                                <InfoTileComp
                                                    icon={SquaresExcludeIcon}
                                                    title={t("patcherInfo")}
                                                    subtitle={t("patcherInfoDesc", {
                                                        version: data.patcher.version,
                                                        commit: data.patcher.commit
                                                    })} />

                                                <InfoTileComp
                                                    icon={Shield}
                                                    title={t("hash", { type: t("unclone")})}
                                                    subtitle={data.hash.unclone}
                                                    copiable={true}
                                                    copyData={data.hash.unclone} />

                                                <InfoTileComp
                                                    icon={Shield}
                                                    title={t("hash", { type: t("clone")})}
                                                    subtitle={data.hash.clone}
                                                    copiable={true}
                                                    copyData={data.hash.clone} />
                                            </div>
                                        </div>
                                    </CardContent>
                                </Card>
                            </motion.div>
                        </motion.div>
                    </div>
                    <Footer />
                </div>
            ) : (
                <>
                    <Navbar />
                    <LoadingBar />
                    <Footer />
                </>
            )}
        </AnimatePresence>
    );
}