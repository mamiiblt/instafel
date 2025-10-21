"use client";

import React, { useEffect, useState } from "react";
import i18next from "@/i18n/i18n";
import {cookieName, supportedLocales} from "@/i18n/settings";
import { I18nextProvider } from "react-i18next";
import { LoadingBar } from "@/components/LoadingBars";

function getCookie(name: string) {
  const match = document.cookie.match(new RegExp("(^| )" + name + "=([^;]+)"));
  if (match) return match[2];
  return null;
}

function getBrowserLanguage() {
  return navigator.language;
}

export default function LocaleProvider({
  children,
}: {
  children: React.ReactNode;
}) {
  const [ready, setReady] = useState(false);

  useEffect(() => {
    let lng = getCookie(cookieName);
    const langSupported = supportedLocales.includes(lng)
    if (!lng || !langSupported) {
      lng = getBrowserLanguage();
      if (!langSupported) lng = "en-EN";
      if (!langSupported) lng = "en-EN";
      document.cookie = `${cookieName}=${lng}; path=/; max-age=31536000`;
    }

    i18next.changeLanguage(lng).then(() => {
      setReady(true);
    });
  }, []);

  if (!ready) return <LoadingBar />;

  return <I18nextProvider i18n={i18next}>{children}</I18nextProvider>;
}
