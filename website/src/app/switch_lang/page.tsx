"use client";

import {Card} from "@/components/ui/card";
import {Check, Globe} from "lucide-react";
import {useTranslation} from "react-i18next";
import {Page, PageHeader} from "@/components/PageUtils";
import React from "react";
import {getLanguageDisplayName} from "@/lib/utils";
import {cookieName, supportedLocales} from "@/i18n/settings";
import {CrowdinSuggestCard} from "@/components/CrowdinSuggestCard";

export default function ContributorsPage() {
    const {t, i18n} = useTranslation("switchlang");

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
                    {supportedLocales.map((langCode) => (
                        <button
                            key={langCode}
                            onClick={() => changeLanguage(langCode)}
                            className={`flex items-center justify-between rounded-lg border p-4 text-left transition-colors hover:bg-accent ${
                                i18n.language === langCode ? "border-primary bg-accent" : "border-border"
                            }`}
                        >
                            <div className="flex flex-col gap-1">
                                <span className="font-medium">{getLanguageDisplayName(langCode, langCode)}</span>
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
