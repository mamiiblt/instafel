import { clsx, type ClassValue } from "clsx";
import { twMerge } from "tailwind-merge";
import ISO6391 from "iso-639-1";

export function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs));
}

export const handleVersionInput = (
  value: string,
  setter: (value: string) => void
) => {
  const regex = /^[0-9.]*$/;
  if (regex.test(value)) {
    setter(value);
  }
};

export function getLanguageDisplayName(languageCode: string, displayLocale = "en"): string {
    if (!languageCode) return "";

    const code = languageCode.trim().replace(/_/g, "-");
    const base = code.split("-")[0];

    try {
        const dn = new Intl.DisplayNames([displayLocale], { type: "language" });
        const name = dn.of(code);
        if (name) return capitalizeFirstLetter(name, displayLocale);
    } catch {
        const name = ISO6391.getName(base) || ISO6391.getNativeName(base);
        if (name) return capitalizeFirstLetter(name, displayLocale);
        return code;
    }
}

function capitalizeFirstLetter(str: string, locale = "en"): string {
    return str
        ? str[0].toLocaleUpperCase(locale) + str.slice(1)
        : "";
}
