import {defaultMetadata} from "@/config/metadata";
import {Metadata} from "next";

export const metadata: Metadata = {
    ...defaultMetadata,
    title: `Instafel Wiki`,
    description: "You can find eveything about of Instafel from here!",
};

export default async function WikiLayout({
                                             children,
                                         }: {
    children: React.ReactNode;
}) {
    return <div>{children}</div>;
}
