"use client";

import Footer from "@/components/Footer";
import Navbar from "@/components/Navbar";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import { motion } from "framer-motion";
import {
  Github,
  Twitter,
  Linkedin,
  Globe,
  Mail,
  Code,
  Users,
  Languages,
  Heart,
  SendIcon,
} from "lucide-react";
import { useTranslation } from "react-i18next";

const containerVariants = {
  hidden: { opacity: 0 },
  visible: {
    opacity: 1,
    transition: {
      staggerChildren: 0.08,
    },
  },
};

const itemVariants = {
  hidden: { opacity: 0, y: 30 },
  visible: {
    opacity: 1,
    y: 0,
    transition: {
      duration: 0.6,
      ease: "easeOut",
    },
  },
};

const cardHoverVariants = {
  hover: {
    y: -8,
    transition: {
      duration: 0.3,
      ease: "easeOut",
    },
  },
};

export default function Page() {
  const { t } = useTranslation("contributors");
  const mainDeveloper = {
    name: "mamii.",
    role: t("roles.dev"),
    avatar: "https://mamii.me/mamiiblt.png",
    bio: "Developer of Instafel, passionate about open source, web & android technologies, and building inclusive communities.",
    socials: {
      github: "mamiiblt",
      x: "mamiiblt",
      website: "mamii.me",
      telegram: "mamiiblt",
    },
  };

  const teamMembers = [
    {
      name: "Zain",
      role: t("roles.mod"),
      avatar: "/cpictures/zain.jpg",
      socials: {
        github: "Iamzainnnn",
        telegram: "iamzainnnn",
      },
    },
    {
      name: "burak",
      role: t("roles.mod"),
      avatar: "/cpictures/burak.jpg",
      socials: {
        telegram: "buraakkcayir",
        github: "patrickozdag",
      },
    },
    {
      name: "berkmirsat",
      role: t("roles.mod"),
      avatar: "https://avatars.githubusercontent.com/u/83542897?v=4",
      socials: {
        telegram: "berkmirsatk",
        github: "berkmirsatk",
      },
    },
    {
      name: "John Xirouchakis",
      role: t("roles.cre"),
      avatar: "/cpictures/john.jpg",
      socials: {
        telegram: "ioannisxir",
        github: "johnxirouchakis",
      },
    },
    {
      name: "Vinit",
      role: t("roles.cre"),
      avatar: "/placeholder.svg?height=60&width=60",
      socials: {
        telegram: "Pixelishfr",
      },
    },
    {
      name: "Night-X",
      role: t("roles.cre"),
      avatar: "/cpictures/night-x.jpeg",
      socials: {
        telegram: "NightX24",
        website: "t.me/MobilePhotographyConnect",
      },
    },
    {
      name: "votsz",
      role: t("roles.cre"),
      avatar: "/cpictures/votsz.jpg",
      socials: {
        telegram: "votsz",
      },
    },
  ];

  const translators = [
    {
      name: "Munir Nasibzade",
      language: "Azerbaycanca",
      avatar: "https://avatars.githubusercontent.com/u/160434174?v=4",
      flag: "🇦🇿",
      socials: {
        github: "mnasibzade",
        telegram: "mnasibzade",
      },
    },
    {
      name: "Rüşan Gür",
      language: "Deutsch",
      avatar: "https://avatars.githubusercontent.com/u/34343052?v=4",
      flag: "🇩🇪",
      socials: {
        website: "github.com/xxOrdulu52xx",
      },
    },
    {
      name: "Hem Pal",
      language: "Français",
      avatar: "/cpictures/hempal.jpg",
      flag: "🇫🇷",
      socials: {
        telegram: "hemk651",
        website: "www.instagram.com/sincrypt.hemk651",
      },
    },
    {
      name: "nubesurrealista",
      language: "Español",
      avatar: "https://avatars.githubusercontent.com/u/136946098?v=4",
      flag: "🇪🇸",
      socials: {
        github: "nubesurrealista",
        website: "nube.codeberg.page/links/",
      },
    },
    {
      name: "Vinicius",
      language: "Portugal",
      avatar: "/cpictures/vinicius.jpg",
      flag: "🇵🇹",
      socials: {
        telegram: "exteraDev",
        github: "exteraDev",
      },
    },
    {
      name: "Zan",
      language: "Magyar",
      avatar: "/placeholder.svg?height=60&width=60",
      flag: "🇭🇺",
      socials: {
        telegram: "Zan1456",
        website: "youtsit.ee/zan1456",
      },
    },
    {
      name: "Sahil Ensari",
      language: "हिंदी",
      avatar: "/cpictures/sahil.jpg",
      flag: "🇮🇳",
      socials: {
        telegram: "imsahilansarii",
        github: "imsahilansarii",
      },
    },
    {
      name: "John Xirouchakis",
      language: "Ελληνικά",
      avatar: "/cpictures/john.jpg",
      flag: "🇬🇷",
      socials: {
        telegram: "ioannisxir",
        github: "johnxirouchakis",
      },
    },
    {
      name: "krvstek.",
      language: "Polski",
      avatar: "/cpictures/krvstek.jpg",
      flag: "🇵🇱",
      socials: {
        website: "insane.rip/krvstek"
      },
    },
  ];

  const specialThanks = [
    {
      name: "Bluepapilte",
      reason: "Developer of MyInsta",
      avatar: "/placeholder.svg?height=60&width=60",
      socials: {
        telegram: "Carpaxel",
        website: "https://t.me/instasmashrepo",
      },
    },
    {
      name: "Amàzing World",
      reason: "Author of snoozing & link fix patches",
      avatar: "/placeholder.svg?height=60&width=60",
      socials: {
        telegram: "world669",
        website: "t.me/amazingscripts",
      },
    },
    {
      name: "Adwaith Varma",
      reason: "Instagram scholar",
      avatar: "/cpictures/varma.jpg",
      socials: {
        telegram: "VarmaAdwaith",
        website: "t.me/RelevantUpdates",
      },
    },
    {
      name: "Dani",
      reason: "He is like a flag!",
      avatar: "/cpictures/dani.jpg",
      socials: {
        telegram: "danii5",
        website: "t.me/igdevdani",
      },
    },
    {
      name: "EreN",
      reason: "Helped me a lot of in website!",
      avatar: "https://avatars.githubusercontent.com/u/77717109?v=4",
      socials: {
        telegram: "Mr_ErenK",
        github: "MrErenK",
      },
    },
  ];

  const SocialButton = ({
    icon: Icon,
    href,
    label,
  }: {
    icon: any;
    href: string;
    label: string;
  }) => (
    <motion.div whileHover={{ scale: 1.1 }} whileTap={{ scale: 0.95 }}>
      <Button
        variant="ghost"
        size="sm"
        className="h-8 w-8 p-0 text-gray-400 hover:text-foreground hover:bg-gray-100 transition-colors"
        asChild
      >
        <a
          href={href}
          target="_blank"
          rel="noopener noreferrer"
          aria-label={label}
        >
          <Icon className="h-4 w-4" />
        </a>
      </Button>
    </motion.div>
  );

  const SocialLinks = ({ socials }: { socials: any }) => (
    <div className="flex items-center gap-1">
      {socials.telegram && (
        <SocialButton
          icon={SendIcon}
          href={`https://t.me/${socials.telegram}`}
          label="Telegram"
        />
      )}
      {socials.github && (
        <SocialButton
          icon={Github}
          href={`https://github.com/${socials.github}`}
          label="GitHub"
        />
      )}
      {socials.twitter && (
        <SocialButton
          icon={Twitter}
          href={`https://twitter.com/${socials.twitter}`}
          label="Twitter"
        />
      )}
      {socials.website && (
        <SocialButton
          icon={Globe}
          href={`https://${socials.website}`}
          label="Website"
        />
      )}
    </div>
  );

  return (
    <>
      <Navbar />
      <div className="min-h-screen text-foreground">
        <motion.div
          className="max-w-6xl mx-auto px-6 py-16"
          variants={containerVariants}
          initial="hidden"
          animate="visible"
        >
          <motion.div
            className="text-center space-y-6 mb-20"
            variants={itemVariants}
          >
            <h1 className="text-5xl md:text-6xl font-bold tracking-tight">
              {t("title")}
            </h1>
            <p className="text-xl text-muted-foreground max-w-2xl mx-auto leading-relaxed">
              {t("desc")}
            </p>
          </motion.div>

          <motion.section variants={itemVariants}>
            <div className="flex items-center gap-3 mb-8">
              <Code className="w-6 h-6 text-foreground" />
              <h2 className="text-2xl font-semibold">{t("roles.dev")}</h2>
            </div>
            <motion.div variants={cardHoverVariants} whileHover="hover">
              <Card>
                <CardContent className="p-8">
                  <div className="flex flex-col md:flex-row items-start space-y-4 gap-8">
                    <Avatar className="w-24 h-24 border-2">
                      <AvatarImage
                        src={mainDeveloper.avatar || "/placeholder.svg"}
                        alt={mainDeveloper.name}
                      />
                      <AvatarFallback className="text-2xl font-bold bg-card-800 text-foreground">
                        {mainDeveloper.name
                          .split(" ")
                          .map((n) => n[0])
                          .join("")}
                      </AvatarFallback>
                    </Avatar>
                    <div className="flex-1 space-y-4">
                      <div>
                        <h3 className="text-2xl font-bold">
                          {mainDeveloper.name}
                        </h3>
                        <p className="text-muted-foreground font-medium">
                          @mamiiblt
                        </p>
                      </div>
                      <p className="text-muted-foreground leading-relaxed">
                        {mainDeveloper.bio}
                      </p>
                      <SocialLinks socials={mainDeveloper.socials} />
                    </div>
                  </div>
                </CardContent>
              </Card>
            </motion.div>
          </motion.section>

          <motion.section variants={itemVariants}>
            <div className="flex items-center gap-3 mb-8 mt-8">
              <Users className="w-6 h-6 text-foreground" />
              <h2 className="text-2xl font-semibold">{t("titles.team")}</h2>
            </div>
            <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-6">
              {teamMembers.map((member, index) => (
                <motion.div
                  key={member.name}
                  variants={cardHoverVariants}
                  whileHover="hover"
                  initial={{ opacity: 0, y: 30 }}
                  animate={{ opacity: 1, y: 0 }}
                  transition={{ delay: index * 0.1, duration: 0.6 }}
                >
                  <Card className="h-full">
                    <CardContent className="p-6">
                      <div className="flex flex-col items-center text-center space-y-4">
                        <Avatar className="w-16 h-16 border-2">
                          <AvatarImage
                            src={member.avatar || "/placeholder.svg"}
                            alt={member.name}
                          />
                          <AvatarFallback className="bg-black-900 text-foreground font-semibold">
                            {member.name
                              .split(" ")
                              .map((n) => n[0])
                              .join("")}
                          </AvatarFallback>
                        </Avatar>
                        <div>
                          <h3 className="text-lg font-semibold">
                            {member.name}
                          </h3>
                          <p className="text-muted-foreground text-sm">
                            {member.role}
                          </p>
                        </div>
                        <SocialLinks socials={member.socials} />
                      </div>
                    </CardContent>
                  </Card>
                </motion.div>
              ))}
            </div>
          </motion.section>

          <motion.section variants={itemVariants}>
            <div className="flex items-center gap-3 mb-8 mt-8">
              <Languages className="w-6 h-6 text-foreground" />
              <h2 className="text-2xl font-semibold">
                {t("titles.translators")}
              </h2>
            </div>
            <div className="grid md:grid-cols-2 lg:grid-cols-4 gap-4">
              {translators.map((translator, index) => (
                <motion.div
                  key={translator.name}
                  variants={cardHoverVariants}
                  whileHover="hover"
                  initial={{ opacity: 0, scale: 0.9 }}
                  animate={{ opacity: 1, scale: 1 }}
                  transition={{ delay: index * 0.1, duration: 0.6 }}
                >
                  <Card>
                    <CardContent className="p-6 text-center">
                      <div className="space-y-4">
                        <Avatar className="w-12 h-12 mx-auto border-2">
                          <AvatarImage
                            src={translator.avatar || "/placeholder.svg"}
                            alt={translator.name}
                          />
                          <AvatarFallback className="bg-gray-800 text-foreground">
                            {translator.name
                              .split(" ")
                              .map((n) => n[0])
                              .join("")}
                          </AvatarFallback>
                        </Avatar>
                        <div className="flex flex-col items-center">
                          <div>
                            <h3 className="font-semibold text-sm">
                              {translator.name}
                            </h3>
                            <div className="flex items-center justify-center gap-2  mb-2">
                              <span className="text-lg">{translator.flag}</span>
                              <span className="text-muted-foreground text-xs">
                                {translator.language}
                              </span>
                            </div>
                          </div>
                          <SocialLinks socials={translator.socials} />
                        </div>
                      </div>
                    </CardContent>
                  </Card>
                </motion.div>
              ))}
            </div>
          </motion.section>

          <motion.section variants={itemVariants}>
            <div className="flex items-center gap-3 mb-8 mt-8">
              <Heart className="w-6 h-6 text-foreground" />
              <h2 className="text-2xl font-semibold">{t("titles.special")}</h2>
            </div>
            <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-6">
              {specialThanks.map((thanks, index) => (
                <motion.div
                  key={thanks.name}
                  variants={cardHoverVariants}
                  whileHover="hover"
                  initial={{ opacity: 0, x: -30 }}
                  animate={{ opacity: 1, x: 0 }}
                  transition={{ delay: index * 0.1, duration: 0.6 }}
                >
                  <Card className="transition-colors h-full">
                    <CardContent className="p-6">
                      <div className="flex items-start gap-4">
                        <Avatar className="w-12 h-12 border-2 flex-shrink-0">
                          <AvatarImage
                            src={thanks.avatar || "/placeholder.svg"}
                            alt={thanks.name}
                          />
                          <AvatarFallback className="bg-gray-800 text-foreground">
                            {thanks.name
                              .split(" ")
                              .map((n) => n[0])
                              .join("")}
                          </AvatarFallback>
                        </Avatar>
                        <div className="flex-1 space-y-3">
                          <div>
                            <h3 className="font-semibold">{thanks.name}</h3>
                            <p className="text-sm text-muted-foreground leading-relaxed">
                              {thanks.reason}
                            </p>
                          </div>
                          <SocialLinks socials={thanks.socials} />
                        </div>
                      </div>
                    </CardContent>
                  </Card>
                </motion.div>
              ))}
            </div>
          </motion.section>
        </motion.div>
      </div>
      <Footer />
    </>
  );
}
