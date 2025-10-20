"use client";

import {cookieName, getSupportedLocales} from "@/i18n/settings";
import {
    DropdownMenu,
    DropdownMenuTrigger,
    DropdownMenuContent,
    DropdownMenuItem,
} from "./ui/dropdown-menu";
import {Globe} from "lucide-react";
import {Button} from "./ui/button";
import {useTranslation} from "react-i18next";
import {Avatar, AvatarFallback, AvatarImage} from "@/components/ui/avatar";
import {useEffect, useState} from "react";
import {getLanguageDisplayName} from "@/lib/utils";

export default function LanguageSwitcher() {
    const {i18n} = useTranslation();
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
        <DropdownMenu>
            <DropdownMenuTrigger asChild>
                <Button variant={"ghost"} size="icon" className="relative">
                    <Globe className="h-4 w-4"/>
                </Button>
            </DropdownMenuTrigger>
            <DropdownMenuContent align="end" className="w-40">
                {locales.map((langCode) => (
                    <DropdownMenuItem
                        key={langCode}
                        onClick={() => changeLanguage(langCode)}
                        className={`cursor-pointer ${
                            i18n.resolvedLanguage === langCode
                                ? "bg-accent/40 font-medium"
                                : ""
                        }`}
                    >
                        {getLanguageDisplayName(langCode)}
                        {i18n.resolvedLanguage === langCode && (
                            <span className="ml-auto text-primary">•</span>
                        )}
                    </DropdownMenuItem>
                ))}
            </DropdownMenuContent>
        </DropdownMenu>
    );
}
