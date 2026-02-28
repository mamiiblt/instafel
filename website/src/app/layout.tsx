import {Geist} from "next/font/google";
import "@/app/globals.css";
import {ThemeProvider} from "@/components/providers/theme-provider";
import {SITE_CONFIG} from "@/config/config";
import {defaultMetadata} from "@/config/metadata";
import LocaleProvider from "@/i18n/LocaleProvider";
import {Toaster} from "sonner";
import Script from "next/script";

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
                    {children}
                </main>
            </LocaleProvider>
        </ThemeProvider>
        <Script
          src="https://stats.mamii.dev/script.js"
          data-website-id="741237d4-25f5-4ff3-b263-ad8c3351bdc5"
          strategy="afterInteractive"
        />
        </body>
        </html>
    );
}
