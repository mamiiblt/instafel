"use client";

import { AnimatePresence, motion } from "framer-motion";
import { useEffect, useState } from "react";
import Footer from "@/components/Footer";
import { useTranslation } from "react-i18next";
import Navbar from "@/components/Navbar";
import {contentAPIURL, flagCategories} from "@/wdata/flag_sdata";
import { FlagIcon, GalleryVerticalEnd, Router, Search } from "lucide-react";
import { LoadingBar } from "@/components/LoadingBars";
import FlagLibraryCategoryItem from "@/components/FlagLibraryCategoryItem";
import { Input } from "@/components/ui/input";
import { useRouter } from "next/navigation";

export default function LibraryBackupPage() {
  const { t } = useTranslation(["library_flag", "fcategories"]);
  const [hoveredId, setHoveredId] = useState<number | null>(null);
  const [searchQuery, setSearchQuery] = useState("");
  const [data, setData] = useState<{
    total: number;
    categorized: Record<string, number>;
  } | null>(null);
  const router = useRouter();

  useEffect(() => {
    const fetchData = async () => {
      var requestUrl = `${contentAPIURL}/content/flib/sizes`;
      const res = await fetch(requestUrl);
      const result = await res.json();
      setData(result);
    };
    fetchData();
  }, []);

  const handleSearch = () => {
    if (searchQuery.trim()) {
      router.push(
        `/library/flag/sresult?searchQuery=${encodeURIComponent(searchQuery)}`
      );
    }
  };

  const handleKeyDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === "Enter") {
      handleSearch();
    }
  };

  return (
    <AnimatePresence>
      {data ? (
        <div>
          <div>
            <Navbar />
            <div className="min-h-screen bg-background py-12 px-4 sm:px-6 lg:px-8">
              <div className="max-w-6xl mx-auto">
                <motion.div
                  initial={{ opacity: 0, y: 30 }}
                  animate={{ opacity: 1, y: 0 }}
                  transition={{ duration: 0.6 }}
                  className="text-center mb-12"
                >
                  <div className="flex justify-center mb-6">
                    <div className="relative">
                      <div
                        className="absolute inset-0 rounded-full bg-primary/20 animate-ping"
                        style={{ animationDuration: "3s" }}
                      ></div>
                      <div className="relative bg-primary/30 p-5 rounded-full">
                        <FlagIcon className="h-12 w-12 text-primary" />
                      </div>
                    </div>
                  </div>
                  <h1 className="text-4xl font-bold mb-4 text-foreground">
                    {t("title")}
                  </h1>
                  <p className="text-lg text-muted-foreground max-w-2xl mx-auto mb-8">
                    {t("desc")}
                  </p>
                </motion.div>

                <div>
                  <motion.div
                    initial={{ opacity: 0, y: 20 }}
                    animate={{ opacity: 1, y: 0 }}
                    transition={{ delay: 0.2, duration: 0.6 }}
                    className="max-w-md mx-auto relative mb-4"
                  >
                    <div className="flex gap-2">
                      <div className="relative flex-1">
                        <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-muted-foreground h-4 w-4" />
                        <Input
                          type="text"
                          placeholder={t("search_placeholder")}
                          value={searchQuery}
                          onChange={(e) => setSearchQuery(e.target.value)}
                          className="pl-10 pr-4 py-3 w-full h-12"
                          onKeyDown={handleKeyDown}
                        />
                      </div>
                      <button
                        onClick={handleSearch}
                        className="bg-primary text-primary-foreground px-4 py-3 rounded-md hover:bg-primary/90 transition-colors h-12 flex items-center justify-center"
                      >
                        <Search className="h-4 w-4" />
                      </button>
                    </div>
                  </motion.div>

                  <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 gap-4">
                    <motion.div
                      key={0}
                      initial={{ opacity: 0, y: 30 }}
                      animate={{ opacity: 1, y: 0 }}
                      transition={{
                        delay: 0.4 + 0 * 0.1,
                        duration: 0.8,
                        ease: "easeOut",
                      }}
                    >
                      <FlagLibraryCategoryItem
                        idx={0}
                        cid={2589}
                        cif={"all"}
                        icon={<GalleryVerticalEnd />}
                        fsize={data.total}
                        hoveredId={hoveredId}
                        setHoveredId={setHoveredId}
                      />
                    </motion.div>
                    {Object.entries(data.categorized).map(
                      ([id, count], idx) => {
                        const category = flagCategories[id];
                        return (
                          <motion.div
                            key={id}
                            initial={{ opacity: 0, y: 30 }}
                            animate={{ opacity: 1, y: 0 }}
                            transition={{
                              delay: 0.4 + (idx + 1) * 0.1,
                              duration: 0.8,
                              ease: "easeOut",
                            }}
                          >
                            <FlagLibraryCategoryItem
                              idx={idx + 1}
                              cid={id}
                              cif={category.cif}
                              icon={category.icon}
                              fsize={count}
                              hoveredId={hoveredId}
                              setHoveredId={setHoveredId}
                            />
                          </motion.div>
                        );
                      }
                    )}
                  </div>
                </div>
              </div>
            </div>
            <Footer />
          </div>
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
