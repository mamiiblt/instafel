export const fallbackLng = "en-EN";
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

let cachedLocales: string[] | null = null;

export async function getSupportedLocales(): Promise<string[]> {
    if (cachedLocales) {
        return cachedLocales;
    }

    try {
        const res = await fetch("/api/locales");
        cachedLocales = await res.json()
        console.log("✅ Locales cached:", cachedLocales);
        return cachedLocales;
    } catch (err) {
        console.error("❌ Error fetching locales:", err);
        cachedLocales = [];
        return cachedLocales;
    }
}
