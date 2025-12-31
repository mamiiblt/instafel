"use client"

import Snowfall from "react-snowfall"

export default function SnowEffect() {
    return (
        <div
            style={{
                position: "fixed",
                inset: 0,
                pointerEvents: "none",
                zIndex: 50,
            }}
        >
            <Snowfall />
        </div>
    )
}
