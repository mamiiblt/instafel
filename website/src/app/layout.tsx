import {Geist} from "next/font/google";
import "@/app/globals.css";
import {ThemeProvider} from "@/components/providers/theme-provider";
import {SITE_CONFIG} from "@/config/config";
import {defaultMetadata} from "@/config/metadata";
import LocaleProvider from "@/i18n/LocaleProvider";
import {Toaster} from "sonner";
import SnowEffect from "@/components/SnowEffect";

const appleTitle = SITE_CONFIG.siteName;
const geist = Geist({
    subsets: ["latin"],
});

export const metadata = defaultMetadata;

export default function RootLayout({
                                       children,
                                   }: Readonly<{
    children: React.ReactNode;
}>) {
    return (
        <html suppressHydrationWarning>
        <head>
            <meta name="apple-mobile-web-app-title" content={appleTitle}/>
        </head>
        <body
            className={`${geist.className} flex min-h-screen flex-col bg-background text-foreground`}
        >
        <ThemeProvider attribute="class" defaultTheme="system" enableSystem>
            <LocaleProvider>
                <Toaster/>
                <main className="flex-1">
                    <SnowEffect />
                    {children}
                </main>
            </LocaleProvider>
        </ThemeProvider>
        </body>
        </html>
    );
}
