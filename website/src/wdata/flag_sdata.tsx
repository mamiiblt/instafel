"use client";

import {
  Bot,
  BriefcaseBusiness,
  Camera,
  CircleEllipsis,
  CircleFadingPlus,
  GalleryVerticalEnd,
  MessageCircleMore,
  PhoneCall,
  Play,
  Send,
  StickyNote,
  SwatchBook,
  TvMinimalPlay,
  UserCircle,
  Wallpaper,
  Wrench,
} from "lucide-react";

export const iflApiBase = "https://api.instafel.app";

export const flagCategories = {
  "2589": {
    cif: "all",
    icon: <GalleryVerticalEnd />,
  },
  "0": {
    cif: "direct",
    icon: <Send />,
  },
  "1": {
    cif: "reels",
    icon: <Play />,
  },
  "2": {
    cif: "stories",
    icon: <CircleFadingPlus />,
  },
  "3": {
    cif: "feed",
    icon: <GalleryVerticalEnd />,
  },
  "4": {
    cif: "interface",
    icon: <SwatchBook />,
  },
  "5": {
    cif: "notes",
    icon: <StickyNote />,
  },
  "6": {
    cif: "quality",
    icon: <Wallpaper />,
  },
  "7": {
    cif: "profile",
    icon: <UserCircle />,
  },
  "8": {
    cif: "comments",
    icon: <MessageCircleMore />,
  },
  "9": {
    cif: "camera",
    icon: <Camera />,
  },
  "10": {
    cif: "meta-ai",
    icon: <Bot />,
  },
  "11": {
    cif: "call",
    icon: <PhoneCall />,
  },
  "12": {
    cif: "fixes",
    icon: <Wrench />,
  },
  "13": {
    cif: "professional",
    icon: <BriefcaseBusiness />,
  },
  "14": {
    cif: "livestreams",
    icon: <TvMinimalPlay />,
  },
  "15": {
    cif: "other",
    icon: <CircleEllipsis />,
  },
};
