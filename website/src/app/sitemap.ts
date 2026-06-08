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

import {MetadataRoute} from "next";

const baseUrl = process.env.NEXT_PUBLIC_SITE_URL || "";
if (!baseUrl) {
    throw new Error("NEXT_PUBLIC_SITE_URL is not defined");
}

export default async function sitemap(): Promise<MetadataRoute.Sitemap> {
    const routes = [
        "/",
        "/wiki",
        "/library/backup",
        "/library/flib_moved",
        "/about_updater",
        "/releases/list?page=1",
        "/contributors",
    ].map((route) => ({
        url: `${baseUrl}${route}`,
        lastModified: new Date(),
        changeFrequency: "monthly" as const,
        priority: route === "" ? 1 : 0.8,
    }));

    return [...routes];
}
