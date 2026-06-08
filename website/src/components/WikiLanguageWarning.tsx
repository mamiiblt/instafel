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

"use client";

import { Button } from "@/components/ui/button";
import { toast } from "sonner";
import { Globe } from "lucide-react";

export default function WikiLanguageWarning() {
  return (
    <Button
      variant={"ghost"}
      size="icon"
      className="relative dark"
      onClick={() =>
        toast("Translations currently aren't available in Wiki contents!", {
          description:
            "But, you can use Google Translator or other extensions for translate pages!",
          action: {
            label: "Okay!",
            onClick: () => {},
          },
        })
      }
    >
      <Globe className="h-4 w-4" />
    </Button>
  );
}
