/*
 * (c) 2026 Muhammed Ali Bulut, All rights reserved.
 *
 * See LICENSE file in repository root for copy file of license. For copyright
 * notices, technical issues, feedback, or any other related to this code file or
 * project, please contact me via mamii@mamii.dev or other ways.
 */

'use client';

import React, { useState } from 'react';
import { motion } from 'framer-motion';
import { ChevronLeft, ChevronRight } from 'lucide-react';
import {Button} from "@/components/ui/button";

interface ScreenshotViewerProps {
    screenshots: string[];
    backup_id: number;
}

const MotionButton = motion.create(Button);

export default function ScreenshotViewer({ screenshots, backup_id }: ScreenshotViewerProps) {
    const [current, setCurrent] = useState(0);
    const [direction, setDirection] = useState(0);

    const paginate = (newDirection) => {
        setDirection(newDirection);
        setCurrent((prev) => {
            let next = prev + newDirection;
            if (next < 0) next = screenshots.length - 1;
            if (next >= screenshots.length) next = 0;
            return next;
        });
    };

    const handleDragEnd = (event, info) => {
        const swipeThreshold = 50;
        const swipe = Math.abs(info.offset.x);
        if (swipe > swipeThreshold) {
            paginate(info.offset.x > 0 ? -1 : 1);
        }
    };

    return (
        <div className="flex items-center justify-center">
            <div className="relative w-full max-w-md aspect-[9/19] overflow-hidden rounded-3xl bg-card border group">
                {screenshots.map((screenshot, index) => (
                    <motion.img
                        key={index}
                        src={`https://cdn.mamii.dev/instafel/backup_ss/${backup_id}/${screenshot}.png`}
                        alt={`Image ${index + 1}`}
                        animate={{
                            zIndex: index === current ? 10 : -1,
                            opacity: index === current ? 1 : 0,
                            x: index === current ? 0 : (direction > 0 ? 400 : -400),
                        }}
                        transition={{
                            x: { type: 'spring', stiffness: 300, damping: 30 },
                            opacity: {
                                duration: 0.2,
                                delay: index === current ? 0 : 0.35
                            },
                            zIndex: { duration: 0 },
                        }}
                        drag={index === current ? "x" : false}
                        dragElastic={0.2}
                        onDragEnd={index === current ? handleDragEnd : undefined}
                        className="absolute inset-0 w-full h-full object-cover cursor-grab active:cursor-grabbing"
                    />
                ))}

                <MotionButton
                    variant={"outline"}
                    size={"icon"}
                    onClick={() => paginate(-1)}
                    className="absolute left-4 top-1/2 z-20 text-white/50 hover:text-white transition-colors opacity-100"
                    aria-label="Previous"
                >
                    <ChevronLeft size={32} />
                </MotionButton>

                <Button
                    variant={"outline"}
                    size={"icon"}
                    onClick={() => paginate(1)}
                    className="absolute right-4 top-1/2 z-20 text-white/50 hover:text-white transition-colors opacity-100"
                    aria-label="Next"
                >
                    <ChevronRight size={32} />
                </Button>

                <div className="absolute bottom-3 right-3 text-xs text-white bg-black/50 px-3 py-1 rounded-full backdrop-blur z-20">
                    {current + 1}/{screenshots.length}
                </div>
            </div>
        </div>
    );
}