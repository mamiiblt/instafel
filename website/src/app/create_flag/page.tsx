"use client";

import type React from "react";
import { useEffect, useState } from "react";
import { motion } from "framer-motion";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import { Button } from "@/components/ui/button";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { contentAPIURL, flagCategories } from "@/wdata/flag_sdata";
import { useTranslation } from "react-i18next";
import Navbar from "@/components/Navbar";
import Footer from "@/components/Footer";
import { toast } from "sonner";
import {
  FileText,
  Flag,
  Info,
  Tag,
  Text,
  Edit2Icon,
  ChevronDownIcon,
  Clock,
  User,
  Image,
} from "lucide-react";
import AdminLoginProvider from "@/components/AdminLoginProvider";
import Cookies from "js-cookie";
import type { FlagCont } from "@/wdata/mconfig";
import { FlagDataEditor } from "@/components/FlagDataEditor";
import { Label } from "@/components/ui/label";
import { Popover, PopoverContent } from "@/components/ui/popover";
import { PopoverTrigger } from "@radix-ui/react-popover";
import { Calendar } from "@/components/ui/calendar";
import { Checkbox } from "@/components/ui/checkbox";
import { Calendar as CalendarIcon } from "lucide-react";
import { checkFlagsValidity } from "@/components/MetaConfigContent";
import { handleVersionInput } from "@/lib/utils";
import ImageUploadInterface, {
  UploadedFile,
} from "@/components/ImageUploadInterface";

interface UploadFileInf {
  uploading: boolean;
  name: string;
}

export default function CreateContentPage() {
  const { t } = useTranslation("fcategories");
  const [title, setTitle] = useState<string>("");
  const [description, setDescription] = useState<string>("");
  const [addedIn, setAddedIn] = useState<string>("");
  const [removedIn, setRemovedIn] = useState<string>("");
  const [flags, setFlags] = useState<FlagCont[]>([]);
  const [screenshotList, setScreenshotList] = useState<string[]>([]);
  const [selectedCategory, setSelectedCategory] = useState<string>("");
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [adminUsername, setAdminUsername] = useState<string>("");
  const [adminPassword, setAdminPassword] = useState<string>("");
  const [lastEdit, setLastEdit] = useState<Date>(new Date());
  const [datePickerOpen, setDatePickerOpen] = useState(false);
  const [useCurrentTime, setUseCurrentTime] = useState(true);
  const [customTime, setCustomTime] = useState("10:30:00");
  const [addedBy, setAddedBy] = useState<string>("");
  const [uploadedFiles, setUploadedFiles] = useState<UploadedFile[]>([]);
  const [isProcessed, setIsProcessed] = useState(false);
  const [fUploadData, setFUploadData] = useState<UploadFileInf>({
    uploading: false,
    name: undefined,
  });

  const handleFilesChange = (files: UploadedFile[]) => {
    setUploadedFiles(files);
  };

  const handleImageUpload = async () => {
    console.log("Uploading files:", uploadedFiles);

    if (!isProcessed && uploadedFiles.length > 0) {
      setIsProcessed(true);
    }

    var errorCatch = false;
    try {
      for (const file of uploadedFiles) {
        setFUploadData({ uploading: true, name: file.name });

        const formData = new FormData();
        formData.append("image", file.file);
        formData.append("name", file.name);

        const fUploadResp = await fetch(
          "https://api.mamii.me/ifl/admin/user/upload-image",
          {
            method: "POST",
            body: formData,
            headers: {
              "ifl-admin-username": btoa(adminUsername),
              "ifl-admin-password": btoa(adminPassword),
            },
          }
        );

        const resp = await fUploadResp.json();
        console.log(resp);
        if (resp.status !== "SUCCESS") {
          throw new Error(resp.extra.desc);
        }

        await new Promise((resolve) => setTimeout(resolve, 120)); // cooldown
      }
    } catch (err) {
      console.error(err);
      toast("Failed to upload", {
        description:
          `An error occured while uploading images into server\n\n` + err,
        action: {
          label: "Okay",
          onClick: () => {},
        },
      });
      setFUploadData({ uploading: true, name: "error" });
      errorCatch = true;
    }

    if (!errorCatch) {
      toast("All images uploaded", {
        description: "Images uploaded succesfully",
        action: {
          label: "Okay",
          onClick: () => {},
        },
      });

      setFUploadData({ uploading: false, name: undefined });
      setScreenshotList(uploadedFiles.map((file) => file.name));
    }
  };

  useEffect(() => {
    setAdminUsername(Cookies.get("a_username"));
    setAdminPassword(Cookies.get("a_pass"));
    setAddedBy(Cookies.get("a_username"));
  }, []);

  useEffect(() => {
    if (useCurrentTime) {
      setLastEdit(new Date());
    }
  }, [useCurrentTime]);

  const handleTimeChange = (timeString: string) => {
    setCustomTime(timeString);
    if (!useCurrentTime && lastEdit) {
      const [hours, minutes, seconds] = timeString.split(":").map(Number);
      const newDate = new Date(lastEdit);
      newDate.setHours(hours, minutes, seconds || 0);
      setLastEdit(newDate);
    }
  };

  const handleDateChange = (date: Date | undefined) => {
    if (date && !useCurrentTime) {
      const [hours, minutes, seconds] = customTime.split(":").map(Number);
      date.setHours(hours, minutes, seconds || 0);
      setLastEdit(date);
    }
    setDatePickerOpen(false);
  };

  const handleUseCurrentTimeChange = (checked: boolean) => {
    setUseCurrentTime(checked);
    if (checked) {
      setLastEdit(new Date());
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsSubmitting(true);

    const payload = {
      category_id: Number(selectedCategory),
      title,
      description,
      added_by: addedBy,
      added_in: addedIn == "" ? null : addedIn,
      removed_in: removedIn == "" ? null : removedIn,
      last_edit: lastEdit.toISOString(),
      flags,
      screenshots: screenshotList,
    };

    console.log(payload);

    try {
      if (checkFlagsValidity(flags)) {
        const res = await fetch(`${contentAPIURL}/creator/create-flag`, {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            "ifl-admin-username": btoa(adminUsername),
            "ifl-admin-password": btoa(adminPassword),
          },
          body: JSON.stringify(payload),
        });
        const data = await res.json();
        if (data.status == "SUCCESS") {
          toast("Flag Created", {
            description: data.extra.desc,
            action: {
              label: "Okay",
              onClick: () => {},
            },
          });
        } else if (data.status == "AUTHENTICATION_REJECTED") {
          toast("Authentication Rejected", {
            description: "Please write true admin login credentials",
            action: {
              label: "Okay",
              onClick: () => {},
            },
          });
        } else if (data.status == "FAILURE") {
          toast("Failure on API side", {
            description: data.extra.desc,
            action: {
              label: "Okay",
              onClick: () => {},
            },
          });
        } else {
          toast("Unknown Response", {
            description: JSON.stringify(data),
            action: {
              label: "Okay",
              onClick: () => {},
            },
          });
        }
      } else {
        toast("Flags are invalid", {
          description: "You has missing fields in flags editor",
          action: {
            label: "Okay",
            onClick: () => {},
          },
        });
      }
    } catch (err) {
      toast("Error occurred", {
        description:
          "An error occurred while sending / response of flag\n" + err,
        action: {
          label: "Okay",
          onClick: () => {},
        },
      });
      console.error("❌ Error:", err);
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <>
      <Navbar />
      <AdminLoginProvider>
        <div className="min-h-screen bg-background">
          <div className="container mx-auto px-4 py-6 sm:py-8 lg:py-12 max-w-6xl">
            <motion.div
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.5 }}
              className="space-y-6 lg:space-y-8"
            >
              <div className="text-center space-y-4">
                <div className="flex items-center justify-center gap-2 mb-4">
                  <h1 className="text-3xl sm:text-4xl lg:text-5xl font-bold">
                    Create Flag
                  </h1>
                </div>
              </div>

              <form onSubmit={handleSubmit} className="space-y-6">
                <Card className="shadow-lg border-0 bg-card">
                  <CardHeader className="pb-4">
                    <div className="flex items-center gap-2">
                      <Info className="h-5 w-5 text-primary" />
                      <CardTitle className="text-xl">Informations</CardTitle>
                    </div>
                    <CardDescription>
                      Essential details about new flag
                    </CardDescription>
                  </CardHeader>
                  <CardContent className="space-y-6">
                    <div className="grid grid-cols-1 lg:grid-cols-2 gap-4 lg:gap-6">
                      <div className="space-y-2">
                        <label
                          htmlFor="title"
                          className="text-sm font-semibold flex items-center gap-2"
                        >
                          <FileText className="h-4 w-4" />
                          Title{" "}
                          <Badge variant="destructive" className="text-xs">
                            Required
                          </Badge>
                        </label>
                        <Input
                          id="title"
                          value={title}
                          onChange={(e) => setTitle(e.target.value)}
                          placeholder="Write feature title"
                          className="h-11"
                          required
                        />
                      </div>

                      <div className="space-y-2">
                        <label
                          htmlFor="category"
                          className="text-sm font-semibold flex items-center gap-2"
                        >
                          <Tag className="h-4 w-4" />
                          Category{" "}
                          <Badge variant="destructive" className="text-xs">
                            Required
                          </Badge>
                        </label>
                        <select
                          id="category"
                          value={selectedCategory}
                          onChange={(e) => setSelectedCategory(e.target.value)}
                          className="flex h-11 w-full rounded-md border border-input bg-card px-3 py-2 text-sm ring-offset-background focus:outline-none focus:ring-2 focus:ring-ring focus:ring-offset-2"
                          required
                        >
                          <option value="">Select a category</option>
                          {Object.entries(flagCategories)
                            .filter(([key]) => key !== "2589")
                            .map(([key, { cif }]) => (
                              <option key={key} value={key}>
                                {t(`${cif}`, { ns: "fcategories" })}
                              </option>
                            ))}
                        </select>
                      </div>
                    </div>

                    <div className="space-y-2">
                      <label
                        htmlFor="addedBy"
                        className="text-sm font-semibold flex items-center gap-2"
                      >
                        <User className="h-4 w-4" />
                        Added by (TG Nickname){" "}
                        <Badge variant="destructive" className="text-xs">
                          Required
                        </Badge>
                      </label>
                      <Input
                        id="addedBy"
                        value={addedBy}
                        onChange={(e) => setAddedBy(e.target.value)}
                        placeholder="Directly pass telegram username (without t.me)"
                        className="h-11"
                        required
                      />
                    </div>

                    <div className="space-y-4">
                      <div className="flex items-center gap-2">
                        <Clock className="h-4 w-4 text-muted-foreground" />
                        <h3 className="font-semibold text-sm">
                          Last Edit Time{" "}
                          <Badge variant="destructive" className="text-xs">
                            Required
                          </Badge>
                        </h3>
                      </div>

                      <div className="flex items-center space-x-2">
                        <Checkbox
                          id="use-current-time"
                          checked={useCurrentTime}
                          onCheckedChange={handleUseCurrentTimeChange}
                        />
                        <Label
                          htmlFor="use-current-time"
                          className="text-sm font-medium"
                        >
                          Use current date and time (If not needed, don't edit
                          that)
                        </Label>
                      </div>

                      {useCurrentTime ? (
                        <div className="p-3 bg-muted/50 rounded-md border">
                          <p className="text-sm text-muted-foreground">
                            Current time:{" "}
                            <span className="font-medium text-foreground">
                              {lastEdit.toLocaleString()}
                            </span>
                          </p>
                        </div>
                      ) : (
                        <div className="grid grid-cols-1 lg:grid-cols-2 gap-4 lg:gap-6">
                          <div className="flex flex-col gap-3">
                            <Label htmlFor="date-picker" className="px-1">
                              Date
                            </Label>
                            <Popover
                              open={datePickerOpen}
                              onOpenChange={setDatePickerOpen}
                            >
                              <PopoverTrigger asChild>
                                <Button
                                  variant="outline"
                                  id="date-picker"
                                  className="w-full justify-between font-normal bg-transparent"
                                >
                                  {lastEdit
                                    ? lastEdit.toLocaleDateString()
                                    : "Select date"}
                                  <ChevronDownIcon className="h-4 w-4" />
                                </Button>
                              </PopoverTrigger>
                              <PopoverContent
                                className="w-auto overflow-hidden p-0"
                                align="start"
                              >
                                <Calendar
                                  mode="single"
                                  selected={lastEdit}
                                  captionLayout="dropdown"
                                  onSelect={handleDateChange}
                                />
                              </PopoverContent>
                            </Popover>
                          </div>

                          <div className="flex flex-col gap-3">
                            <Label htmlFor="time-picker" className="px-1">
                              Time
                            </Label>
                            <Input
                              type="time"
                              id="time-picker"
                              step="1"
                              value={customTime}
                              onChange={(e) => handleTimeChange(e.target.value)}
                              className="bg-background appearance-none [&::-webkit-calendar-picker-indicator]:hidden [&::-webkit-calendar-picker-indicator]:appearance-none"
                            />
                          </div>
                        </div>
                      )}
                    </div>

                    <div className="space-y-2">
                      <label
                        htmlFor="description"
                        className="text-sm font-semibold flex items-center gap-2"
                      >
                        <Text className="h-4 w-4" />
                        Description{" "}
                        <Badge variant="destructive" className="text-xs">
                          Required
                        </Badge>
                      </label>
                      <Textarea
                        value={description}
                        onChange={(e) => setDescription(e.target.value)}
                        placeholder="Write a description about flag (like what is that, what is the purpose etc. and more things about that)"
                        className="min-h-[400px] sm:min-h-[200px] text-sm resize-none"
                        required
                      />
                    </div>

                    <div className="space-y-4">
                      <div className="flex items-center gap-2">
                        <CalendarIcon className="h-4 w-4 text-muted-foreground" />
                        <h3 className="font-semibold text-sm">
                          Version Information
                        </h3>
                      </div>
                      <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                        <div className="space-y-2">
                          <label
                            htmlFor="addedIn"
                            className="text-sm font-medium text-muted-foreground"
                          >
                            Added In Version
                          </label>
                          <Input
                            id="addedIn"
                            value={addedIn}
                            onChange={(e) =>
                              handleVersionInput(e.target.value, setAddedIn)
                            }
                            placeholder="e.g., 331.0.0.0.20"
                            className="h-10"
                          />
                        </div>
                        <div className="space-y-2">
                          <label
                            htmlFor="removedIn"
                            className="text-sm font-medium text-muted-foreground"
                          >
                            Removed In Version
                          </label>
                          <Input
                            id="removedIn"
                            value={removedIn}
                            onChange={(e) =>
                              handleVersionInput(e.target.value, setRemovedIn)
                            }
                            placeholder="e.g., 385.0.0.0.11"
                            className="h-10"
                          />
                        </div>
                      </div>
                    </div>
                  </CardContent>
                </Card>

                <Card className="shadow-lg border-0 bg-card">
                  <CardHeader className="pb-4">
                    <div className="flex items-center gap-2">
                      <Edit2Icon className="h-5 w-5 text-primary" />
                      <CardTitle className="text-xl">
                        Flags Editor{" "}
                        <Badge variant="destructive" className="text-xs">
                          Required
                        </Badge>
                      </CardTitle>
                    </div>
                    <CardDescription>
                      Add MetaConfig flag informations
                    </CardDescription>
                  </CardHeader>
                  <CardContent>
                    <FlagDataEditor
                      initialFlags={flags}
                      onFlagsChange={(flags: FlagCont[]) => setFlags(flags)}
                    />
                  </CardContent>
                </Card>

                <Card className="shadow-lg border-0 bg-card">
                  <CardHeader className="pb-4">
                    <div className="flex items-center gap-2">
                      <Image className="h-5 w-5 text-primary" />
                      <CardTitle className="text-xl">
                        Screenshots{" "}
                        <Badge variant={"secondary"} className="text-xs">
                          Not Required
                        </Badge>
                      </CardTitle>
                    </div>
                    <CardDescription>
                      Upload images about related flag
                    </CardDescription>
                  </CardHeader>
                  <CardContent>
                    <ImageUploadInterface
                      onFilesChange={handleFilesChange}
                      maxFiles={5}
                      disabled={isProcessed}
                    />

                    {uploadedFiles.length > 0 && (
                      <div className="space-y-4">
                        {uploadedFiles.length > 0 && !isProcessed && (
                          <Button
                            onClick={handleImageUpload}
                            className="w-full"
                          >
                            Upload Files
                          </Button>
                        )}

                        {isProcessed && (
                          <>
                            {fUploadData.uploading ? (
                              <>
                                {fUploadData.name == "error" ? (
                                  <div className="space-y-2">
                                    <div className="items-center justify-center text-center p-4 bg-card-50 border rounded-lg ">
                                      <a className="text-foreground-muted text-center text-red-500 w-full font-italic">
                                        An error occured while uploading
                                        images..
                                      </a>
                                    </div>
                                  </div>
                                ) : (
                                  <div className="space-y-2">
                                    <div className="items-center justify-center text-center p-4 bg-card-50 border rounded-lg ">
                                      <a className="text-green-800 flex items-center justify-center w-full mt-3 mb-3">
                                        <div className="h-8 w-8 animate-spin rounded-full border-2 border-primary border-t-transparent" />
                                      </a>
                                      <a className="text-foreground-muted text-center w-full font-italic">
                                        Uploading {fUploadData.name}
                                      </a>
                                    </div>
                                  </div>
                                )}
                              </>
                            ) : (
                              <div className="space-y-2">
                                <div className="items-center justify-center text-center p-4 bg-card-50 border rounded-lg">
                                  <a className="text-green-800 text-center w-full">
                                    Files have been uploaded successfully!
                                  </a>
                                  <br />
                                  <a className="text-foreground-muted text-center w-full font-italic text-xs">
                                    Now, you can't upload more images about this
                                    flag.
                                  </a>
                                </div>
                              </div>
                            )}
                          </>
                        )}
                      </div>
                    )}
                  </CardContent>
                </Card>

                <Card className="shadow-lg border-0 bg-gradient-to-r from-primary/5 to-primary/10 border-primary/20">
                  <CardContent className="pt-6">
                    <div className="flex flex-col sm:flex-row items-center justify-between gap-4">
                      <div className="text-center sm:text-left">
                        <p className="font-semibold">Send API Request</p>
                        <p className="text-sm text-muted-foreground ">
                          You should check API logs at Telegram after sending
                          request!
                        </p>
                        <p className="text-sm text-muted-foreground mt-2">
                          Logged as <b>{adminUsername}</b>
                        </p>
                      </div>
                      <Button
                        type="submit"
                        size="lg"
                        disabled={isSubmitting}
                        className="w-full sm:w-auto min-w-[140px] h-12"
                      >
                        {isSubmitting ? (
                          <div className="flex items-center gap-2">
                            <div className="h-4 w-4 animate-spin rounded-full border-2 border-current border-t-transparent" />
                            Creating...
                          </div>
                        ) : (
                          <div className="flex items-center gap-2">
                            <Flag className="h-4 w-4" />
                            Create Flag
                          </div>
                        )}
                      </Button>
                    </div>
                  </CardContent>
                </Card>
              </form>
            </motion.div>
          </div>
        </div>
      </AdminLoginProvider>
      <Footer />
    </>
  );
}
