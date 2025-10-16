export const fallbackLng = "en";
export const navLanguages = [
  { code: "en", name: "English" },
  { code: "tr", name: "Türkçe" },
  { code: "de", name: "Deutsch" },
  { code: "es", name: "Español" },
  { code: "in", name: "हिन्दी" },
  { code: "pl", name: "Polski" },
  { code: "id", name: "Indonesia" },
  { code: "it", name: "Italiano" },
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
