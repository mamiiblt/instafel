import { motion } from "framer-motion";
import Link from "next/link";
import { useTranslation } from "react-i18next";

export default function FlagLibraryCategoryItem({
  idx,
  cid,
  cif,
  icon,
  fsize,
  hoveredId,
  setHoveredId,
}) {
  const { t } = useTranslation(["library_flag", "fcategories"]);

  return (
    <Link
      href={`/library/flag/category?cid=${cid}&page=1`}
      onMouseEnter={() => setHoveredId(idx)}
      onMouseLeave={() => setHoveredId(null)}
    >
      <button
        className={`w-full p-4 h-[160px] flex flex-col items-center 
        justify-center text-center rounded-xl border border-border 
        transition-all duration-300 bg-card text-foreground hover:shadow-lg hover:scale-105`}
      >
        <div
          className={`mb-3 transform transition-transform duration-300 ${
            hoveredId === idx ? "scale-110" : ""
          }`}
        >
          {icon}
        </div>

        <h3 className="font-medium mb-1 transition-colors duration-300">
          {t(`${cif}`, { ns: "fcategories" })}
        </h3>

        <div className="mt-2 text-center">
          <span className="text-xs font-medium px-2 py-1 rounded-full bg-muted text-muted-foreground">
            {t("num_flag", {
              count: fsize,
            })}
          </span>
        </div>
      </button>
    </Link>
  );
}
