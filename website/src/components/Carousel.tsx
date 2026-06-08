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

import { useState } from "react";
import Image from "next/image";
import { ChevronLeft, ChevronRight } from "lucide-react";
import { Button } from "@/components/ui/button";

interface FlagLibraryCarouselProps {
  images: string[];
}

export function FlagLibraryCarousel({ images }: FlagLibraryCarouselProps) {
  const [currentImageIndex, setCurrentImageIndex] = useState(0);

  const totalPairs = Math.ceil(images.length / 2);
  const canGoNext = currentImageIndex < totalPairs - 1;
  const canGoPrevious = currentImageIndex > 0;

  const nextImages = () => {
    if (canGoNext) {
      setCurrentImageIndex(currentImageIndex + 1);
    }
  };

  const previousImages = () => {
    if (canGoPrevious) {
      setCurrentImageIndex(currentImageIndex - 1);
    }
  };

  return (
    <div className="relative overflow-hidden">
      <div className="flex overflow-hidden">
        <div
          className="flex transition-transform duration-300 ease-in-out"
          style={{ transform: `translateX(-${currentImageIndex * 100}%)` }}
        >
          {images.map((src, index) => (
            <div key={index} className="min-w-[50%] px-2 ">
              <Image
                src={src || "/placeholder.svg"}
                alt={`App Screenshot ${index + 1}`}
                width={300}
                height={20}
                className="rounded-xl shadow-xs mx-auto"
              />
            </div>
          ))}
        </div>
      </div>

      {images.length > 2 && (
        <>
          <Button
            variant="secondary"
            size="icon"
            className={`absolute left-0 top-1/2 -translate-y-1/2 rounded-full ${
              !canGoPrevious ? "opacity-50 cursor-not-allowed" : ""
            }`}
            onClick={previousImages}
            disabled={!canGoPrevious}
          >
            <ChevronLeft className="h-4 w-4" />
          </Button>

          <Button
            variant="secondary"
            size="icon"
            className={`absolute right-0 top-1/2 -translate-y-1/2 rounded-full ${
              !canGoNext ? "opacity-50 cursor-not-allowed" : ""
            }`}
            onClick={nextImages}
            disabled={!canGoNext}
          >
            <ChevronRight className="h-4 w-4" />
          </Button>
        </>
      )}
    </div>
  );
}
