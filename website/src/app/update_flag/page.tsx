"use client";

import type React from "react";

import { motion } from "framer-motion";
import { useEffect, useState } from "react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import {
  ChevronDownIcon,
  Clock,
  Edit2Icon,
  FileText,
  Image,
  Info,
  Loader2,
  Text,
  UploadIcon,
  User,
} from "lucide-react";
import { contentAPIURL } from "@/wdata/flag_sdata";
import Navbar from "@/components/Navbar";
import Footer from "@/components/Footer";
import { toast } from "sonner";
import { Badge } from "@/components/ui/badge";
import { Textarea } from "@/components/ui/textarea";
import AdminLoginProvider from "@/components/AdminLoginProvider";
import Cookies from "js-cookie";
import { FlagCont } from "@/wdata/mconfig";
import { handleVersionInput } from "@/lib/utils";
import {
  Popover,
  PopoverContent,
  PopoverTrigger,
} from "@/components/ui/popover";
import { Label } from "@/components/ui/label";
import { Calendar } from "@/components/ui/calendar";
import { Calendar as CalendarIcon } from "lucide-react";
import { Checkbox } from "@/components/ui/checkbox";
import { FlagDataEditor } from "@/components/FlagDataEditor";

export default function UpdateFlagPage() {
  const [id, setId] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const [hasSubmitted, setHasSubmitted] = useState(false);
  const [description, setDescription] = useState<string>("");
  const [title, setTitle] = useState<string>("");
  const [addedIn, setAddedIn] = useState<string>("");
  const [removedIn, setRemovedIn] = useState<string>("");
  const [flags, setFlags] = useState<FlagCont[]>([]);
  const [isSubmittingForm, setIsSubmittingForm] = useState(false);
  const [lastEdit, setLastEdit] = useState<Date>(new Date());
  const [datePickerOpen, setDatePickerOpen] = useState(false);
  const [useCurrentTime, setUseCurrentTime] = useState(true);
  const [customTime, setCustomTime] = useState("10:30:00");
  const [addedBy, setAddedBy] = useState<string>("");

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

  const submitFlagID = async (e: React.FormEvent) => {
    e.preventDefault();

    setIsLoading(true);
    try {
      const response = await fetch(`${contentAPIURL}/content/flib/flag/${id}`);

      if (!response.ok) {
        if (response.status == 404) {
          toast("404", {
            description: "Flag not found in respository, please check the ID",
            action: {
              label: "Okay",
              onClick: () => {},
            },
          });
        } else {
          throw new Error(`HTTP error! status: ${response.status}`);
        }
      } else {
        const data = await response.json();
        setTitle(data.title);
        setDescription(data.description);
        setAddedIn(data.added_in);
        setRemovedIn(data.removed_in == null ? "" : data.removed_in);
        setFlags(data.flags);
        setAddedBy(data.added_by == null ? "" : data.added_by);
        setLastEdit(new Date(data.last_edit));
        setUseCurrentTime(false);
        setHasSubmitted(true);
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
      console.error("Error:", err);
    } finally {
      setIsLoading(false);
    }
  };

  const submitUpdateReq = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsSubmittingForm(true);

    const payload = {
      id: parseInt(id),
      title,
      added_by: addedBy,
      last_edit: lastEdit,
      description,
      added_in: addedIn == "" ? null : addedIn,
      removed_in: removedIn == "" ? null : removedIn,
      flags,
    };

    try {
      console.log(payload);
      const res = await fetch(`${contentAPIURL}/creator/update-flag`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          "ifl-admin-username": btoa(Cookies.get("a_username")),
          "ifl-admin-password": btoa(Cookies.get("a_pass")),
        },
        body: JSON.stringify(payload),
      });

      const data = await res.json();

      if (data.status == "SUCCESS") {
        toast("Flag Updated", {
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
    } catch (err) {
      toast("Error occurred", {
        description:
          "An error occurred while sending / response of flag\n" + err,
        action: {
          label: "Okay",
          onClick: () => {},
        },
      });
      console.error("Error:", err);
    } finally {
      setIsSubmittingForm(false);
    }
  };

  return (
    <AdminLoginProvider>
      {!hasSubmitted ? (
        <>
          <Navbar />
          <div className="min-h-screen bg-background flex items-center justify-center p-4">
            <Card className="w-full max-w-md">
              <CardHeader className="text-center">
                <CardTitle className="text-2xl font-bold">
                  Update Flag
                </CardTitle>
                <CardDescription>
                  Enter Flag ID for update an existing flag
                </CardDescription>
              </CardHeader>

              <CardContent className="space-y-4">
                <form onSubmit={submitFlagID} className="space-y-4">
                  <div className="space-y-2">
                    <Input
                      id="id"
                      type="text"
                      placeholder="Enter Flag ID"
                      value={id}
                      onChange={(e) => setId(e.target.value)}
                      disabled={isLoading}
                    />
                  </div>

                  <Button
                    type="submit"
                    className="w-full"
                    disabled={isLoading || !id.trim()}
                  >
                    {isLoading ? (
                      <>
                        <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                        Loading...
                      </>
                    ) : (
                      "Load Flag"
                    )}
                  </Button>
                </form>
              </CardContent>
            </Card>
          </div>
          <Footer />
        </>
      ) : (
        <>
          <Navbar />
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
                      Update Flag
                    </h1>
                  </div>
                </div>

                <form onSubmit={submitUpdateReq} className="space-y-6">
                  <Card className="shadow-lg border-0 bg-card">
                    <CardHeader className="pb-4">
                      <div className="flex items-center gap-2">
                        <Info className="h-5 w-5 text-primary" />
                        <CardTitle className="text-xl">Informations</CardTitle>
                      </div>
                      <CardDescription>
                        If you want, you can edit metadatas about flag too.
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
                            placeholder="Enter feature title"
                            className="h-11"
                            required
                          />
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
                      </div>

                      <div className="space-y-4">
                        <div className="flex items-center gap-2">
                          <Clock className="h-4 w-4 text-muted-foreground" />
                          <h3 className="font-semibold text-sm">
                            Last Edit Time
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
                            Use current date and time (If not needed, don't
                            disable that)
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
                                onChange={(e) =>
                                  handleTimeChange(e.target.value)
                                }
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
                        <CardTitle className="text-xl">Flags Editor</CardTitle>
                      </div>
                      <CardDescription>
                        Add MetaConfig flag informations manually
                      </CardDescription>
                    </CardHeader>
                    <CardContent>
                      <FlagDataEditor
                        initialFlags={flags}
                        onFlagsChange={(flags: FlagCont[]) => setFlags(flags)}
                      />
                    </CardContent>
                  </Card>

                  <Card className="shadow-lg border-0 bg-gradient-to-r from-primary/5 to-primary/10 border-primary/20">
                    <CardContent className="pt-6">
                      <div className="flex flex-col sm:flex-row items-center justify-between gap-4">
                        <div className="text-center sm:text-left">
                          <p className="font-semibold">Send API Request</p>
                          <p className="text-sm text-muted-foreground">
                            You should check telegram API logs after sending
                            request!
                          </p>
                          <p className="text-sm text-muted-foreground mt-2">
                            Logged as <b>{Cookies.get("a_username")}</b>
                          </p>
                        </div>
                        <Button
                          type="submit"
                          size="lg"
                          disabled={isSubmittingForm}
                          className="w-full sm:w-auto min-w-[140px] h-12"
                        >
                          {isSubmittingForm ? (
                            <div className="flex items-center gap-2">
                              <div className="h-4 w-4 animate-spin rounded-full border-2 border-current border-t-transparent" />
                              Updating...
                            </div>
                          ) : (
                            <div className="flex items-center gap-2">
                              <UploadIcon className="h-4 w-4" />
                              Update Flag
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
          <Footer />
        </>
      )}
    </AdminLoginProvider>
  );
}
