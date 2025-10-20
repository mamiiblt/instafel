import { readdirSync } from "fs";
import path from "path";
import {NextResponse} from "next/server";

export async function GET() {
    try {
        const localesDir = path.resolve("./src/locales");

        const folders = readdirSync(localesDir, { withFileTypes: true })
            .filter(f => f.isDirectory() && f.name !== "en-EN")
            .map(f => f.name);

        return NextResponse.json(["en-EN", ...folders]);
    } catch (err) {
        console.error("Error reading locales:", err);
        return NextResponse.json({ error: "Failed to read locales" }, { status: 500 });
    }
}