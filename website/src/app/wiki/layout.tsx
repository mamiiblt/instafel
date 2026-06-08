/*
 * (c) 2026 Muhammed Ali Bulut, All rights reserved.
 *
 * See LICENSE file in repository root for copy file of license. For copyright
 * notices, technical issues, feedback, or any other related to this code file or
 * project, please contact me via mamii@mamii.dev or other ways.
 */

/*
 * (c) 2026 Muhammed Ali Bulut, All rights reserved.
 *
 * See LICENSE file in repository root for copy file of license. For copyright
 * notices, technical issues, feedback, or any other related to this code file or
 * project, please contact me via mamii@mamii.dev or other ways.
 */

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
