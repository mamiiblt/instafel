import {Metadata} from "next";
import {defaultMetadata} from "@/config/metadata";

export const metadata: Metadata = {
    ...defaultMetadata,
    title: `Flag Library is Moved!`,
    description: "Flag library is moved to Telegram again!",
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
