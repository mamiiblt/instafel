"use client";

import React, {ReactElement, ReactNode, Suspense} from "react";
import { FooterLoading } from "./loading";
import {
    BookOpenText,
    Download,
    FileCog2Icon,
    FlagIcon,
    GithubIcon, Languages,
    LucideInstagram,
    RefreshCcwDot,
    Send,
    User, Users,
} from "lucide-react";
import Link from "next/link";
import { motion } from "framer-motion";
import { useTranslation } from "react-i18next";

function LinkListComponent({ href, text, icon }: { href: string, icon: ReactElement, text: string }) {
    return (
        <motion.li
            whileHover={{ x: 5 }}
            transition={{ type: "spring", stiffness: 400 }}
        >
            <Link
                href={href}
                className="text-muted-foreground hover:text-foreground flex items-center gap-2 transition-colors"
            >
                {React.cloneElement(icon, {
                    ...(icon.props as any),
                    className: "w-4 h-4",
                })}
                {text}
            </Link>
        </motion.li>
    )
}

function generateListComponents(
    listCompInfos: [string, string, ReactElement][]
): ReactNode[] {
    return listCompInfos.map((subArray) => {
        const [href, text, icon] = subArray;
        return <LinkListComponent key={href} href={href} icon={icon} text={text} />;
    });
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
                  <div className="w-10 h-10 rounded-xl bg-[#3e7c91] flex items-center justify-center">
                    <LucideInstagram className="text-[#a2ddff] font-bold text-xl" />
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
                    {generateListComponents([
                        ["/wiki", t("footer.5"), <BookOpenText />],
                        ["/library/backup", t("footer.6"), <FileCog2Icon />],
                        ["/library/flag", t("footer.11"), <FlagIcon />],
                        ["/contributors", t("footer.12"), <Users />]
                    ])}
                </ul>
              </div>

              <div>
                <h3 className="font-semibold mb-4 text-lg">{t("footer.7")}</h3>
                <ul className="space-y-3">
                    {generateListComponents([
                        ["/about_updater", t("footer.8"), <RefreshCcwDot />],
                        ["/releases/view?version=latest", t("footer.9"), <Download />],
                        ["https://github.com/mamiiblt/instafel", t("footer.10"), <GithubIcon />],
                        ["https://crowdin.com/project/instafel", t("footer.13"), <Languages />]
                    ])}
                </ul>
              </div>
            </div>

            <div className="mt-8 pt-8 border-t border-border">
              <div className="flex flex-col md:flex-row justify-between items-center gap-4">
                <p className="text-sm text-muted-foreground">{t("footer.3")}</p>
                <div className="flex items-center gap-4">
                  <motion.a
                    whileHover={{ y: -3 }}
                    href="https://mamii.dev/about"
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
