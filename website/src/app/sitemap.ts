import { MetadataRoute } from "next";

const baseUrl = process.env.NEXT_PUBLIC_SITE_URL || "";
if (!baseUrl) {
  throw new Error("NEXT_PUBLIC_SITE_URL is not defined");
}

export default async function sitemap(): Promise<MetadataRoute.Sitemap> {
  const routes = [
    "/",
    "/wiki",
    "/library/backup",
    "/library/flag",
    "/about_updater",
    "/download?version=latest",
    "/contributors",
  ].map((route) => ({
    url: `${baseUrl}${route}`,
    lastModified: new Date(),
    changeFrequency: "monthly" as const,
    priority: route === "" ? 1 : 0.8,
  }));

  return [...routes];
}
