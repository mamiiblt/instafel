"use client";

import {useEffect, useState} from "react";
import {notFound, useParams} from "next/navigation";
import wikiData from "@/wdata/list";
import WikiContent from "@/components/wiki/WikiContent";
import {LoadingBar} from "@/components/LoadingBars";
import {WikiPage} from "@/wdata/wiki";
import {SidebarProvider} from "@/components/ui/sidebar";
import {WikiSidebar} from "@/components/wiki/WikiSidebar";
import Navbar from "@/components/Navbar";
import Footer from "@/components/Footer";

function getPageBySlug(slug: string) {
    var rPage = null;
    for (const category of wikiData) {
        category.subs.forEach((subCategory) => {
            for (const page of subCategory.pages) {
                if (page.slug == slug) {
                    rPage = page;
                }
            }
        });
    }

    return rPage != null ? rPage : null;
}

function getPreviousAndNextPage(currentSlug: string) {
    const pages: WikiPage[] = wikiData.flatMap((category) =>
        category.subs.flatMap((subcat) => subcat.pages)
    );

    const index = pages.findIndex((page) => page.slug === currentSlug);

    return {
        prev: pages[index - 1] ?? null,
        next: pages[index + 1] ?? null,
    };
}

export default function WikiSlugPage() {
    const {slug} = useParams() as { slug: string };
    const [content, setContent] = useState<string | null>(null);
    const [loading, setLoading] = useState(true);

    const page = getPageBySlug(slug);
    const proviData = getPreviousAndNextPage(slug);

    useEffect(() => {
        if (!slug) return;

        fetch(`/wiki/${slug}.md`)
            .then((res) => {
                if (!res.ok) throw new Error("Not found");
                return res.text();
            })
            .then(setContent)
            .catch(() => setContent("## MD can't be loaded."))
            .finally(() => setLoading(false));
    }, [slug]);

    if (!page) return notFound();
    if (loading) return <LoadingBar/>;
    if (!content) return <div className="p-6">Content not found.</div>;

    return (
        <>
            <SidebarProvider>
                <WikiSidebar/>
                <div className="flex-1 overflow-auto">
                    <Navbar/>
                    <WikiContent
                        page={page}
                        content={content}
                        proviData={getPreviousAndNextPage(slug)}
                    />
                </div>
            </SidebarProvider>
            <Footer/>
        </>
    );
}
