"use client";

import {Globe} from "lucide-react";
import {Button} from "./ui/button";
import Link from "next/link";

export default function LanguageSwitcher() {
    return (
        <Link href="/switch_lang">
            <Button
                variant={"ghost"}
                size="icon"
                className="relative"
            >
                <Globe className="h-[1.2rem] w-[1.2rem] rotate-0 scale-100 transition-all " />
            </Button>
        </Link>
    );
}
