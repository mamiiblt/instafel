import {Metadata} from "next";
import {defaultMetadata} from "@/config/metadata";

export const metadata: Metadata = {
    ...defaultMetadata,
    title: `About Updater`,
    description:
        "Automatically update Instafel with Shizuku and Root as an additional alternative to the in-app OTA system",
};

export default function AboutLayout({
                                        children,
                                    }: {
    children: React.ReactNode;
}) {
    return (
        <div className="bg-primary-foreground dark:bg-primary-background">
            {children}
        </div>
    );
}
