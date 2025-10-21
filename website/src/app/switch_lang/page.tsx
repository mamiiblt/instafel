"use client";

import {Card, CardContent} from "@/components/ui/card";
import {Check, Globe} from "lucide-react";
import {useTranslation} from "react-i18next";
import {Page, PageHeader} from "@/components/PageUtils";
import React, {useEffect, useState} from "react";
import {getLanguageDisplayName} from "@/lib/utils";
import {cookieName, getSupportedLocales} from "@/i18n/settings";
import {CrowdinSuggestCard} from "@/components/CrowdinSuggestCard";

export default function ContributorsPage() {
    const {t, i18n} = useTranslation("switchlang");
    const [locales, setLocales] = useState<string[]>([]);

    useEffect(() => {
        let isMounted = true;

        getSupportedLocales().then((res) => {
            if (isMounted) setLocales(res);
        });

        return () => {
            isMounted = false;
        };
    }, []);

    const changeLanguage = (newLng: string) => {
        i18n.changeLanguage(newLng, () => {
            document.cookie = `${cookieName}=${newLng}; path=/`;
        })
    };

    return (
        <Page
            width={6}
            header={<PageHeader
                icon={<Globe />}
                title={t("title")}
                subtitle={t("desc")}/>}
            content={<>
                <Card className="grid gap-3 p-3">
                    {locales.map((langCode) => (
                        <button
                            key={langCode}
                            onClick={() => changeLanguage(langCode)}
                            className={`flex items-center justify-between rounded-lg border p-4 text-left transition-colors hover:bg-accent ${
                                i18n.language === langCode ? "border-primary bg-accent" : "border-border"
                            }`}
                        >
                            <div className="flex flex-col gap-1">
                                <span className="font-medium">{getLanguageDisplayName(langCode, langCode)}</span>
                                <span className="text-sm text-muted-foreground">{getLanguageDisplayName(langCode, "en")}</span>
                            </div>
                            {i18n.language === langCode && <Check className="h-5 w-5 text-primary" />}
                        </button>
                    ))}
                </Card>

                <CrowdinSuggestCard className="mt-4" />
            </>}
        />
    );
}
