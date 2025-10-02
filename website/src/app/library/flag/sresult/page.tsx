"use client";

import {AnimatePresence, motion} from "framer-motion";
import {useEffect, useState} from "react";
import {useSearchParams} from "next/navigation";
import Link from "next/link";
import {useTranslation} from "react-i18next";
import {contentAPIURL, flagCategories} from "@/wdata/flag_sdata";
import {Copy, SearchCheckIcon, Trash,} from "lucide-react";
import {Page, PageHeader, PageLoading} from "@/components/PageUtils";

interface RespTF {
    result_size: number;
    flags: {
        id: number;
        title: string;
        added_by: string;
        last_edit: string;
        removed_in: string;
        category_id: number;
    }[];
}

export default function FlagListPage() {
    const {t, i18n} = useTranslation(["flags", "fcategories"]);
    const [hoveredId] = useState<number | null>(null);
    const searchParams = useSearchParams();
    const search = String(searchParams.get("searchQuery"));
    const [data, setData] = useState<RespTF | null>(null);

    useEffect(() => {
        const fetchData = async () => {
            try {
                const requestUrl = `${contentAPIURL}/content/flib/filter?sQuery=${encodeURIComponent(
                    search
                )}`;
                const res = await fetch(requestUrl);
                const data: RespTF = await res.json();
                console.log(data);
                setData(data);
            } catch (e) {
                console.error("An error occurred while sending / reading response:", e);
            }
        };
        fetchData();
    }, [data]);

    return (
        <AnimatePresence>
            {data ? (
                <Page
                    width={6}
                    header={<PageHeader
                        icon={<SearchCheckIcon/>}
                        title={t("results")}
                        subtitle={t("s_flag_found", {
                            fcount: data.result_size,
                            query: decodeURIComponent(search),
                        })}/>}
                    content={<>
                        <motion.div
                            initial={{opacity: 0, y: 30}}
                            animate={{opacity: 1, y: 0}}
                            transition={{delay: 0.6, duration: 0.8, ease: "easeInOut"}}
                            className="grid gap-4 mb-2"
                        >
                            <div className="grid gap-4 mb-6">
                                {data.flags.map((flag, index) => (
                                    <Link
                                        key={index}
                                        href={`/library/flag/view?id=${flag.id}`}
                                        className="group relative"
                                    >
                                        <div
                                            className={`
          relative overflow-hidden bg-card rounded-xl border border-border
          transition-all duration-300
          ${hoveredId === index ? "shadow-lg scale-[1.01]" : "hover:shadow-sm"}
        `}
                                        >
                                            <div className="p-4 sm:p-6">
                                                <div className="flex items-start justify-between">
                                                    <div className="flex-12">
                                                        <div className="flex items-center gap-3 mb-2">
                                                            <h3 className="text-lg font-semibold text-foreground">
                                                                {flag.title}
                                                            </h3>
                                                        </div>
                                                        <div
                                                            className="items-center gap-4 text-sm text-muted-foreground">
                                                            <div className="flex items-center gap-2">
                                                                <svg
                                                                    className="w-4 h-4"
                                                                    fill="none"
                                                                    stroke="currentColor"
                                                                    viewBox="0 0 24 24"
                                                                >
                                                                    <path
                                                                        strokeLinecap="round"
                                                                        strokeLinejoin="round"
                                                                        strokeWidth={1.5}
                                                                        d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"
                                                                    />
                                                                </svg>
                                                                {t("added_by", {name: flag.added_by})}
                                                            </div>
                                                            <div className="flex items-center gap-2">
                                                                <svg
                                                                    className="w-4 h-4"
                                                                    fill="none"
                                                                    stroke="currentColor"
                                                                    viewBox="0 0 24 24"
                                                                >
                                                                    <path
                                                                        strokeLinecap="round"
                                                                        strokeLinejoin="round"
                                                                        strokeWidth={1.5}
                                                                        d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"
                                                                    />
                                                                </svg>
                                                                {t("uploaded_at", {
                                                                    date_str: new Intl.DateTimeFormat(
                                                                        i18n.language,
                                                                        {
                                                                            dateStyle: "medium",
                                                                            timeStyle: "short",
                                                                        }
                                                                    ).format(new Date(flag.last_edit)),
                                                                })}
                                                            </div>
                                                            {flag.removed_in != null && (
                                                                <div className="flex items-center gap-2">
                                                                    <Trash className="w-4 h-4"/>
                                                                    {t("removed_with", {
                                                                        version: flag.removed_in,
                                                                    })}
                                                                </div>
                                                            )}
                                                            {flag.category_id != null && (
                                                                <div className="flex items-center gap-2">
                                                                    <Copy className="w-4 h-4"/>
                                                                    {t(
                                                                        `${flagCategories[flag.category_id].cif}`,
                                                                        {
                                                                            ns: "fcategories",
                                                                        }
                                                                    )}
                                                                </div>
                                                            )}
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                            <div
                                                className={`
              absolute inset-0
              bg-gradient-to-tr from-muted/0 via-muted/0 to-muted/50
              transition-opacity duration-300
            `}
                                            />
                                        </div>
                                    </Link>
                                ))}
                            </div>
                        </motion.div>
                        <motion.div
                            initial={{opacity: 0}}
                            animate={{opacity: 1}}
                            transition={{delay: 1, duration: 0.6}}
                            className="text-center text-sm text-muted-foreground"
                        >
                            {data.result_size < 15
                                ? t("found_footer.down", {size: data.result_size})
                                : t("found_footer.up", {size: data.result_size})}
                        </motion.div>
                    </>}/>
            ) : (<PageLoading/>)}
        </AnimatePresence>
    );
}
