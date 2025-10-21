import {Card, CardContent} from "@/components/ui/card";
import Link from "next/link";
import {Button} from "@/components/ui/button";
import {useTranslation} from "react-i18next";
import Image from "next/image";
import {useTheme} from "next-themes";

export function CrowdinSuggestCard({ className }: { className: string }) {
    const {t} = useTranslation("contributors");
    const { theme, resolvedTheme } = useTheme();
    const currentTheme = theme === "system" ? resolvedTheme : theme;
    const crowdinLogoSrc =
        currentTheme === "dark"
            ? "/cpictures/crowdin/crw_light.svg"
            : "/cpictures/crowdin/crw_dark.svg";

    return (<Card className={`${className} border-2`}>
        <CardContent className="pt-8 pb-8">
            <div className="flex flex-col items-center text-center space-y-4 max-w-2xl mx-auto">
                <div className="relative w-full h-8 md:h-14 lg:h-14 p-4">
                    <Image
                        src={crowdinLogoSrc}
                        alt="Crowdin Logo"
                        fill
                        className="object-contain"
                    />
                </div>
                <div>
                    <h3 className="text-3xl font-bold mb-2">{t("pleaseTranslateCard.title")}</h3>
                    <p className="text-muted-foreground">{t("pleaseTranslateCard.desc")}</p>
                </div>
                <Link href={"https://crowdin.com/project/instafel"}>
                    <Button className="mt-2 px-8 py-3 font-medium rounded-lg transition-all shadow-sm flex items-center gap-2" size={"lg"}>
                        {t("pleaseTranslateCard.joinUs")}
                    </Button>
                </Link>
            </div>
        </CardContent>
    </Card>)
}