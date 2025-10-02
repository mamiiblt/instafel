"use client";

import { AnimatePresence, motion } from "framer-motion";
import { FileCog2Icon, FileSpreadsheet, ChevronRight } from "lucide-react";
import { useEffect, useState } from "react";
import { LoadingBar } from "@/components/LoadingBars";
import Footer from "@/components/Footer";
import { Button } from "@/components/ui/button";
import Link from "next/link";
import { Card } from "@/components/ui/card";
import { useTranslation } from "react-i18next";
import Navbar from "@/components/Navbar";
import {Page, PageHeader} from "@/components/PageUtils";

interface Backup {
  id: string;
  name: string;
  author: string;
}

interface BackupInfo {
  tag_name: string;
  backups: Backup[];
}

export default function LibraryBackupContent() {
  const { t } = useTranslation("library_backup");
  const [data, setData] = useState<BackupInfo | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const requestUrl =
          "https://raw.githubusercontent.com/instafel/backups/refs/heads/main/backups.json";
        const res = await fetch(requestUrl);
        const result: BackupInfo = await res.json();
        setData(result);
      } catch (error) {
        console.error(t("errors.fetchBackupsFailed"), error);
      } finally {
        setIsLoading(false);
      }
    };

    fetchData();
  }, [t]);

  if (isLoading) {
    return <LoadingBar />;
  }

  return (
    <>
      <AnimatePresence>
        {data ? (
            <Page
                width={6}
                header={<PageHeader
                    icon={<FileSpreadsheet /> }
                    title={t("title")}
                    subtitle={t("description")} />}
                content={<>
                    <motion.div
                        initial={{ opacity: 0, y: 30 }}
                        animate={{ opacity: 1, y: 0 }}
                        transition={{ delay: 0.3, duration: 0.6 }}
                    >
                        <div className="bg-white dark:bg-gray-800/50 rounded-xl shadow-md overflow-hidden border-2 mb-8">
                            <div className="px-4 py-5 sm:p-6">
                                <div className="grid gap-4">
                                    {data.backups.map((backup, index) => (
                                        <motion.div
                                            key={index}
                                            initial={{ opacity: 0, y: 20 }}
                                            animate={{ opacity: 1, y: 0 }}
                                            transition={{
                                                delay: 0.7 + index * 0.1,
                                                duration: 0.5,
                                            }}
                                            whileHover={{ x: 5 }}
                                            className="transition-all duration-300"
                                        >
                                            <Link
                                                href={`/library/backup/view?id=${backup.id}`}
                                                className="block p-4 hover:bg-gray-50 dark:hover:bg-gray-700/50 rounded-lg transition-colors"
                                            >
                                                <div className="flex items-start">
                                                    <div className="bg-gradient-to-r from-gray-100 to-gray-200 dark:from-gray-700 dark:to-gray-800 p-3 rounded-lg mr-4">
                                                        <FileCog2Icon className="h-6 w-6 text-primary" />
                                                    </div>
                                                    <div className="flex-grow">
                                                        <div className="flex items-center justify-between">
                                                            <h3 className="font-semibold text-lg">
                                                                {backup.name}
                                                            </h3>
                                                            <ChevronRight className="h-5 w-5 text-muted-foreground" />
                                                        </div>
                                                        <p className="text-sm text-muted-foreground">
                                                            {t("createdBy", { author: backup.author })}
                                                        </p>
                                                    </div>
                                                </div>
                                            </Link>
                                        </motion.div>
                                    ))}
                                </div>
                            </div>
                        </div>
                    </motion.div>

                    <motion.div
                        initial={{ opacity: 0, y: 30 }}
                        animate={{ opacity: 1, y: 0 }}
                        transition={{ delay: 1, duration: 0.6 }}
                        className="mt-12 text-center"
                    >
                        <div className="bg-gradient-to-r from-primary/5 to-primary/10 rounded-xl p-8">
                            <h3 className="text-xl font-bold mb-3">
                                {t("contribute.title")}
                            </h3>
                            <p className="text-muted-foreground mb-6 max-w-lg mx-auto">
                                {t("contribute.description")}
                            </p>
                            <Button asChild>
                                <Link href="https://t.me/instafel">
                                    {t("contribute.button")}
                                </Link>
                            </Button>
                        </div>
                    </motion.div>
                </>} />
        ) : (
          <div className="py-12 px-6 text-center">
              <Navbar />
              <Card className="max-w-md mx-auto p-6">
                <h2 className="text-xl font-bold mb-4">{t("error.title")}</h2>
                <p className="text-muted-foreground mb-6">
                    {t("error.description")}
                </p>
                <Button onClick={() => window.location.reload()}>
                    {t("error.retryButton")}
                </Button>
              </Card>
              <Footer />
          </div>
        )}
      </AnimatePresence>
    </>
  );
}
