"use client";

import React, { Suspense } from "react";
import { FooterLoading } from "./loading";
import {
  BookOpenText,
  Download,
  FileCog2Icon,
  FlagIcon,
  GithubIcon,
  LucideInstagram,
  RefreshCcwDot,
  Send,
  User,
} from "lucide-react";
import Link from "next/link";
import { motion } from "framer-motion";
import { useTranslation } from "react-i18next";

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
      <motion.footer
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.5 }}
        className="relative border-t border-border bg-background"
      >
        <div className="absolute inset-0 pointer-events-none" />

        <div className="relative container mx-auto px-4 py-8">
          <div className="max-w-6xl mx-auto">
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-8">
              <div className="space-y-4 lg:col-span-2">
                <div className="flex items-center gap-2">
                  <div className="w-10 h-10 rounded-xl bg-instafelcolor flex items-center justify-center">
                    <LucideInstagram className="text-[#a3b18a] font-bold text-xl" />
                  </div>
                  <span className="font-bold text-xl text-foreground">
                    Instafel
                  </span>
                </div>
                <p className="text-muted-foreground">
                  {t("footer.1")}
                  <br />
                  <br />
                  {t("footer.2")}
                </p>
              </div>

              <div>
                <h3 className="font-semibold mb-4 text-lg">{t("footer.4")}</h3>
                <ul className="space-y-3">
                  <motion.li
                    whileHover={{ x: 5 }}
                    transition={{ type: "spring", stiffness: 400 }}
                  >
                    <Link
                      href="/wiki"
                      className="text-muted-foreground hover:text-foreground flex items-center gap-2 transition-colors"
                    >
                      <BookOpenText className="w-4 h-4" />
                      {t("footer.5")}
                    </Link>
                  </motion.li>
                  <motion.li
                    whileHover={{ x: 5 }}
                    transition={{ type: "spring", stiffness: 400 }}
                  >
                    <Link
                      href="/library/backup"
                      className="text-muted-foreground hover:text-foreground flex items-center gap-2 transition-colors"
                    >
                      <FileCog2Icon className="w-4 h-4" />
                      {t("footer.6")}
                    </Link>
                  </motion.li>
                  <motion.li
                    whileHover={{ x: 5 }}
                    transition={{ type: "spring", stiffness: 400 }}
                  >
                    <Link
                      href="/library/flag"
                      className="text-muted-foreground hover:text-foreground flex items-center gap-2 transition-colors"
                    >
                      <FlagIcon className="w-4 h-4" />
                      {t("footer.11")}
                    </Link>
                  </motion.li>
                </ul>
              </div>

              <div>
                <h3 className="font-semibold mb-4 text-lg">{t("footer.7")}</h3>
                <ul className="space-y-3">
                  <motion.li
                    whileHover={{ x: 5 }}
                    transition={{ type: "spring", stiffness: 400 }}
                  >
                    <Link
                      href="/about_updater"
                      className="text-muted-foreground hover:text-foreground flex items-center gap-2 transition-colors"
                    >
                      <RefreshCcwDot className="w-4 h-4" />
                      {t("footer.8")}
                    </Link>
                  </motion.li>
                  <motion.li
                    whileHover={{ x: 5 }}
                    transition={{ type: "spring", stiffness: 400 }}
                  >
                    <Link
                      href="/releases/view?version=latest"
                      className="text-muted-foreground hover:text-foreground flex items-center gap-2 transition-colors"
                    >
                      <Download className="w-4 h-4" />
                      {t("footer.9")}
                    </Link>
                  </motion.li>
                  <motion.li
                    whileHover={{ x: 5 }}
                    transition={{ type: "spring", stiffness: 400 }}
                  >
                    <a
                      href="https://github.com/mamiiblt/instafel"
                      className="text-muted-foreground hover:text-foreground flex items-center gap-2 transition-colors"
                      target="_blank"
                    >
                      <GithubIcon className="w-4 h-4" />
                      {t("footer.10")}
                    </a>
                  </motion.li>
                </ul>
              </div>
            </div>

            <div className="mt-8 pt-8 border-t border-border">
              <div className="flex flex-col md:flex-row justify-between items-center gap-4">
                <p className="text-sm text-muted-foreground">{t("footer.3")}</p>
                <div className="flex items-center gap-4">
                  <motion.a
                    whileHover={{ y: -3 }}
                    href="https://mamii.me/about"
                    className="text-muted-foreground hover:text-foreground transition-colors"
                    aria-label="About Developer"
                    target="_blank"
                  >
                    <User className="w-5 h-5" />
                  </motion.a>
                  <motion.a
                    whileHover={{ y: -3 }}
                    href="https://github.com/mamiiblt"
                    className="text-muted-foreground hover:text-foreground transition-colors"
                    aria-label="GitHub"
                    target="_blank"
                  >
                    <GithubIcon className="w-5 h-5" />
                  </motion.a>
                  <motion.a
                    whileHover={{ y: -3 }}
                    href="https://t.me/mamiiblt"
                    className="text-muted-foreground hover:text-foreground transition-colors"
                    aria-label="Telegram"
                    target="_blank"
                  >
                    <Send className="w-5 h-5" />
                  </motion.a>
                </div>
              </div>
            </div>
          </div>
        </div>
      </motion.footer>
    </Suspense>
  );
}
