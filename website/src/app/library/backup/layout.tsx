import {Metadata} from "next";
import {defaultMetadata} from "@/config/metadata";

export const metadata: Metadata = {
    ...defaultMetadata,
    title: `Backup Library`,
    description: "You can find all backups in here!",
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
