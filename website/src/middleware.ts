import { NextResponse } from "next/server";
import type { NextRequest } from "next/server";

export function middleware(request: NextRequest) {
    const { pathname, searchParams } = request.nextUrl;

    if (pathname === "/download") {
        const versionParam = searchParams.get("version");
        if (versionParam && versionParam.startsWith("v")) {
            const cleanVersion = versionParam.substring(1);
            const newUrl = new URL(`/releases/view?version=${cleanVersion}`, request.url);
            return NextResponse.redirect(newUrl);
        }
    }

    return NextResponse.next();
}

export const config = {
    matcher: ["/download"],
};
