import i18next from "i18next";
import resourcesToBackend from "i18next-resources-to-backend";
import { initReactI18next } from "react-i18next/initReactI18next";
import {fallbackLng, defaultNS, supportedLocales} from "./settings";

const runsOnServerSide = typeof window === "undefined";

i18next
  .use(initReactI18next)
  .use(
    resourcesToBackend((language, namespace) =>
      import(`../locales/${language}/${namespace}.json`)
    )
  )
  .init({
    debug: process.env.NODE_ENV === "development",
    supportedLocales,
    fallbackLng,
    defaultNS,
    ns: [defaultNS],
    interpolation: {
      escapeValue: false,
    },
    preload: runsOnServerSide ? supportedLocales : [],
  });

export default i18next;
