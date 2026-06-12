/*
 * (c) 2026 Muhammed Ali Bulut, All rights reserved.
 *
 * See LICENSE file in repository root for copy file of license. For copyright
 * notices, technical issues, feedback, or any other related to this code file or
 * project, please contact me via mamii@mamii.dev or other ways.
 */

import {Spinner} from "@/components/ui/spinner";

export function LoadingBar() {
    return <div className="min-h-screen flex items-center justify-center bg-background">
        <Spinner className="h-14 w-14"/>
    </div>
}
