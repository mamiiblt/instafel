/*
 * (c) 2026 Muhammed Ali Bulut, All rights reserved.
 *
 * See LICENSE file in repository root for copy file of license. For copyright
 * notices, technical issues, feedback, or any other related to this code file or
 * project, please contact me via mamii@mamii.dev or other ways.
 */

"use client";

import React, { ReactElement, ReactNode, Suspense } from "react";
import { FooterLoading } from "./loading";
import Link from "next/link";
import { motion } from "framer-motion";
import { useTranslation } from "react-i18next";

import {
    BookOpenTextIcon,
    FileBracesIcon,
    FlagIcon,
    SystemUpdate01Icon,
    UserMultiple02Icon,
    Download01Icon,
    CodeIcon,
    Globe02Icon,
    ArrowUpRight01Icon,
    UserCircleIcon,
    TelegramIcon,
} from "@hugeicons/core-free-icons";
import { HugeiconsIcon } from "@hugeicons/react";
import { LucideInstagram } from "lucide-react";

function GithubIcon({ className }: { className?: string }) {
    return (
        <svg
            role="img"
            viewBox="0 0 24 24"
            fill="currentColor"
            aria-hidden="true"
            className={className}
        >
            <path d="M12 .297c-6.63 0-12 5.373-12 12 0 5.303 3.438 9.8 8.205 11.385.6.113.82-.258.82-.577 0-.285-.01-1.04-.015-2.04-3.338.724-4.042-1.61-4.042-1.61C4.422 18.07 3.633 17.7 3.633 17.7c-1.087-.744.084-.729.084-.729 1.205.084 1.838 1.236 1.838 1.236 1.07 1.835 2.809 1.305 3.495.998.108-.776.417-1.305.76-1.605-2.665-.3-5.466-1.332-5.466-5.93 0-1.31.465-2.38 1.235-3.22-.135-.303-.54-1.523.105-3.176 0 0 1.005-.322 3.3 1.23.96-.267 1.98-.399 3-.405 1.02.006 2.04.138 3 .405 2.28-1.552 3.285-1.23 3.285-1.23.645 1.653.24 2.873.12 3.176.765.84 1.23 1.91 1.23 3.22 0 4.61-2.805 5.625-5.475 5.92.42.36.81 1.096.81 2.22 0 1.606-.015 2.896-.015 3.286 0 .315.21.69.825.57C20.565 22.092 24 17.592 24 12.297c0-6.627-5.373-12-12-12" />
        </svg>
    );
}

type LinkInfo = {
    href: string;
    text: string;
    icon: ReactElement;
    external?: boolean;
};

function FooterLink({ href, text, icon, external }: LinkInfo) {
    const isExternal = external ?? href.startsWith("http");

    return (
        <motion.li
            whileHover={{ x: 4 }}
            transition={{ type: "spring", stiffness: 400, damping: 25 }}
        >
            <Link
                href={href}
                {...(isExternal
                    ? { target: "_blank", rel: "noopener noreferrer" }
                    : {})}
                className="group flex items-center gap-3 rounded-2xl px-3 py-2 text-sm text-muted-foreground transition-colors hover:bg-muted/60 hover:text-foreground"
            >
        <span className="flex size-8 shrink-0 items-center justify-center rounded-full bg-muted text-foreground transition-colors group-hover:bg-primary group-hover:text-primary-foreground">
          {React.cloneElement(icon, {
              ...(icon.props as any),
              className: "size-4",
          })}
        </span>
                <span className="flex-1">{text}</span>
                {isExternal && (
                    <HugeiconsIcon
                        icon={ArrowUpRight01Icon}
                        className="size-4 opacity-0 transition-opacity group-hover:opacity-100"
                    />
                )}
            </Link>
        </motion.li>
    );
}

function generateLinkList(links: LinkInfo[]): ReactNode[] {
    return links.map((link) => <FooterLink key={link.href} {...link} />);
}

function SocialButton({
                          href,
                          label,
                          children,
                      }: {
    href: string;
    label: string;
    children: ReactNode;
}) {
    return (
        <motion.a
            whileHover={{ y: -3 }}
            transition={{ type: "spring", stiffness: 400, damping: 20 }}
            href={href}
            target="_blank"
            rel="noopener noreferrer"
            aria-label={label}
            className="flex size-10 items-center justify-center rounded-full border border-border bg-background/60 text-muted-foreground backdrop-blur-md transition-colors hover:bg-muted hover:text-foreground"
        >
            {children}
        </motion.a>
    );
}

export default function Footer() {
    const [loading, setLoading] = React.useState(true);
    const { t } = useTranslation("common");

    React.useEffect(() => {
        const timer = setTimeout(() => {
            setLoading(false);
        }, 300);
        return () => clearTimeout(timer);
    }, []);

    if (loading) {
        return <FooterLoading />;
    }

    return (
        <Suspense fallback={null}>
            <footer className="px-4 pb-6 sm:px-4">
                <motion.div
                    initial={{ opacity: 0, y: 24 }}
                    whileInView={{ opacity: 1, y: 0 }}
                    viewport={{ once: true, margin: "-80px" }}
                    transition={{ duration: 0.5, ease: "easeOut" }}
                    className="mx-auto max-w-8xl overflow-hidden rounded-3xl border border-border bg-background/80 shadow-lg shadow-black/5 backdrop-blur-xl supports-[backdrop-filter]:bg-background/60"
                >
                    <div className="grid grid-cols-1 gap-10 p-6 sm:p-8 md:grid-cols-2 lg:grid-cols-4">
                        <div className="space-y-4 lg:col-span-2">
                            <Link
                                href="/"
                                className="inline-flex items-center gap-2 transition-transform duration-200 hover:scale-[1.03]"
                            >
                                <motion.div
                                    initial={{ opacity: 0, rotate: -10 }}
                                    whileInView={{ opacity: 1, rotate: 0 }}
                                    viewport={{ once: true }}
                                    whileHover={{ rotate: -10 }}
                                    transition={{ duration: 0.5 }}
                                >
                                    <LucideInstagram className="h-6 w-6 text-foreground" />
                                </motion.div>
                                <span className="text-lg font-medium text-foreground">
                                    Instafel
                                </span>
                            </Link>
                            <p className="max-w-md text-sm leading-relaxed text-muted-foreground">
                                {t("footer.1")}
                                <br />
                                <br />
                                {t("footer.2")}
                            </p>
                        </div>

                        <div>
                            <h3 className="mb-3 px-3 text-xs font-semibold uppercase tracking-wider text-muted-foreground">
                                {t("footer.4")}
                            </h3>
                            <ul className="flex flex-col gap-1">
                                {generateLinkList([
                                    {
                                        href: "/wiki",
                                        text: t("footer.5"),
                                        icon: <HugeiconsIcon icon={BookOpenTextIcon} />,
                                    },
                                    {
                                        href: "/library/backup",
                                        text: t("footer.6"),
                                        icon: <HugeiconsIcon icon={FileBracesIcon} />,
                                    },
                                    {
                                        href: "/library/flag",
                                        text: t("footer.11"),
                                        icon: <HugeiconsIcon icon={FlagIcon} />,
                                    },
                                    {
                                        href: "/contributors",
                                        text: t("footer.12"),
                                        icon: <HugeiconsIcon icon={UserMultiple02Icon} />,
                                    },
                                ])}
                            </ul>
                        </div>

                        <div>
                            <h3 className="mb-3 px-3 text-xs font-semibold uppercase tracking-wider text-muted-foreground">
                                {t("footer.7")}
                            </h3>
                            <ul className="flex flex-col gap-1">
                                {generateLinkList([
                                    {
                                        href: "/about_updater",
                                        text: t("footer.8"),
                                        icon: <HugeiconsIcon icon={SystemUpdate01Icon} />,
                                    },
                                    {
                                        href: "/releases/release?version=latest",
                                        text: t("footer.9"),
                                        icon: <HugeiconsIcon icon={Download01Icon} />,
                                    },
                                    {
                                        href: "https://github.com/mamiiblt/instafel",
                                        text: t("footer.10"),
                                        icon: <HugeiconsIcon icon={CodeIcon} />,
                                    },
                                    {
                                        href: "https://crowdin.com/project/instafel",
                                        text: t("footer.13"),
                                        icon: <HugeiconsIcon icon={Globe02Icon} />,
                                    },
                                ])}
                            </ul>
                        </div>
                    </div>

                    <div className="border-t border-border px-6 py-5 sm:px-8">
                        <div className="flex flex-col items-center justify-between gap-4 md:flex-row">
                            <p className="text-sm text-muted-foreground">{t("footer.3")}</p>
                            <div className="flex items-center gap-2">
                                <SocialButton
                                    href="https://mamii.dev/about"
                                    label="About Developer"
                                >
                                    <HugeiconsIcon icon={UserCircleIcon} className="size-5" />
                                </SocialButton>
                                <SocialButton href="https://github.com/mamiiblt" label="GitHub">
                                    <GithubIcon className="size-5" />
                                </SocialButton>
                                <SocialButton href="https://t.me/mamiiblt" label="Telegram">
                                    <HugeiconsIcon icon={TelegramIcon} className="size-5" />
                                </SocialButton>
                            </div>
                        </div>
                    </div>
                </motion.div>
            </footer>
        </Suspense>
    );
}
