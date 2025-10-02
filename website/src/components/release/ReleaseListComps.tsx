"use client";

import {useTranslation} from "react-i18next";
import {ReactNode} from "react";
import {Card, CardContent, CardDescription, CardHeader} from "@/components/ui/card";
import {Separator} from "@/components/ui/separator";
import { motion } from "framer-motion";
import {CalendarArrowUp, HashIcon, InstagramIcon} from "lucide-react";
import Link from "next/link";

export interface ReleaseInfo {
    iflVersion: number,
    iflGenID: string,
    igVersion: string,
    releaseDate: string,
    isDeleted: boolean,
    changelogs: string[],
    patcherVersion: string
}

const item = {
    hidden: { opacity: 0, y: 20 },
    show: { opacity: 1, y: 0 }
};

const formatDate = (isoString) => {
    const { i18n } = useTranslation(["releases"]);
    const date = new Date(isoString);
    return new Intl.DateTimeFormat(i18n.language, {
        day: "2-digit",
        month: "2-digit",
        year: "numeric",
        hour: "2-digit",
        minute: "2-digit",
    }).format(date);
};

export function ReleaseListCard({ cardBody, release, isLatest }: { cardBody: ReactNode, release: ReleaseInfo, isLatest: boolean }) {
    const { t } = useTranslation(["releases"]);
    return (
        <motion.div
            variants={item}
            whileHover={{ scale: 1.01 }}
            transition={{ type: 'spring', stiffness: 300 }}
        >
            <Link href={`/download?version=v${release.iflVersion}`}>
                <Card className={`${ isLatest == true ? "border-2 border-primary" : "" }`}>
                    <CardHeader>
                        <div className="flex items-start justify-between gap-4">
                            {cardBody}
                        </div>
                    </CardHeader>

                    {release.changelogs.length > 0 && (
                        <>
                            <Separator />
                            <CardContent className="pt-6">
                                <h4 className="font-semibold mb-3">{t("changes")}</h4>
                                <ul className="space-y-2 text-muted-foreground">
                                    {release.changelogs.map((log, idx) => (
                                        <motion.li
                                            key={idx}
                                            initial={{ opacity: 0, x: -10 }}
                                            animate={{ opacity: 1, x: 0 }}
                                            transition={{ delay: idx * 0.1 }}
                                            className="flex items-center gap-3 text-sm"
                                        >
                                            <div className="h-1.5 w-1.5 rounded-full bg-primary flex-shrink-0" />
                                            {log}
                                        </motion.li>
                                    ))}
                                </ul>
                            </CardContent>
                        </>
                    )}
                </Card>
            </Link>
        </motion.div>
    )
}

export function ReleaseListInfoCard({ release }: { release: ReleaseInfo }) {
    const { t } = useTranslation(["releases"]);
    return (
        <>
            {release.patcherVersion && (
                <CardDescription>
                    {t("genByPatcher", { patcherVersion: release.patcherVersion })}
                </CardDescription>
            )}

            <div className="flex items-center gap-2 text-sm text-muted-foreground pt-2">
                <InstagramIcon className="h-4 w-4" />
                {t("basedOn", { version: release.igVersion })}
            </div>

            <div className="flex items-center gap-2 text-sm text-muted-foreground pt-2">
                <CalendarArrowUp className="h-4 w-4" />
                {t("releasedOn", { dateFormatted: formatDate(release.releaseDate) })}
            </div>

            {release.iflGenID != null && <div className="flex items-center gap-2 text-sm text-muted-foreground pt-2">
                <HashIcon className="h-4 w-4" />
                {`ID ${release.iflGenID}`}
            </div>}
        </>
    )
}
