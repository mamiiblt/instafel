export const fallbackLng = "en";
export const navLanguages = [
  { code: "en", name: "English", flag: "🇬🇧" },
  { code: "tr", name: "Türkçe", flag: "🇹🇷" },
  { code: "de", name: "Deutsch", flag: "🇩🇪" },
  { code: "es", name: "Español", flag: "🇪🇸" },
  { code: "hi", name: "हिन्दी", flag: "🇮🇳" },
  { code: "pl", name: "Polski", flag: "🇵🇱" },
  { code: "id", name: "Indonesia", flag: "🇮🇩" },
  { code: "it", name: "Italiano", flag: "🇮🇹" },
];
export const languages = navLanguages.map((lang) => lang.code);
export const defaultNS = "common";
export const namespaces = [
  "backup",
  "common",
  "download",
  "home",
  "library_backup",
  "updater",
  "library_flag",
  "fcategories",
  "flag",
  "flags",
];
export const cookieName = "WPG_LANG";
