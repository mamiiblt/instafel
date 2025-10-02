import {Metadata} from "next";
import {defaultMetadata} from "@/config/metadata";

export const metadata: Metadata = {
    ...defaultMetadata,
    title: `Instafel Releases`,
    description: "View all Instafel Releases in one page!",
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
