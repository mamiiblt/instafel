"use client";

import { useState } from "react";
import { Input } from "@/components/ui/input";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { X } from "lucide-react";
import { motion, AnimatePresence } from "framer-motion";

interface TagInputProps {
  id: string;
  tags: string[];
  setTags: React.Dispatch<React.SetStateAction<string[]>>;
  placeholder?: string;
}

export default function TagInput({
  id,
  tags,
  setTags,
  placeholder,
}: TagInputProps) {
  const [inputValue, setInputValue] = useState("");

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setInputValue(e.target.value);
  };

  const addTag = () => {
    const trimmed = inputValue.trim();
    if (trimmed && !tags.includes(trimmed)) {
      setTags([...tags, trimmed]);
    }
    setInputValue("");
  };

  const handleKeyDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === "Enter") {
      e.preventDefault();
      addTag();
    }
  };

  const removeTag = (tagToRemove: string) => {
    setTags(tags.filter((tag) => tag !== tagToRemove));
  };

  return (
    <div className="w-full">
      <div className="flex flex-wrap gap-2 mb-2">
        <AnimatePresence>
          {tags.map((tag, index) => (
            <motion.div
              key={`${tag}-${index}`}
              initial={{ opacity: 0, scale: 0.8 }}
              animate={{ opacity: 1, scale: 1 }}
              exit={{ opacity: 0, scale: 0.8 }}
              transition={{ duration: 0.2 }}
            >
              <Badge className="px-3 py-1 flex items-center gap-1">
                {tag}
                <button
                  type="button"
                  onClick={() => removeTag(tag)}
                  className="text-xs rounded-full hover:bg-primary-foreground focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-primary"
                >
                  <X className="h-3 w-3" />
                  <span className="sr-only">Remove {tag}</span>
                </button>
              </Badge>
            </motion.div>
          ))}
        </AnimatePresence>
      </div>
      <div className="flex gap-2">
        <Input
          id={id}
          value={inputValue}
          onChange={handleInputChange}
          onKeyDown={handleKeyDown}
          placeholder={placeholder || "Type and press Enter to add"}
          className="w-full"
        />
        <Button type="button" onClick={addTag} disabled={!inputValue.trim()}>
          Add
        </Button>
      </div>
    </div>
  );
}
