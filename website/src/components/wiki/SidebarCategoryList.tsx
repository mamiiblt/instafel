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

import { ChevronRight, type LucideIcon } from "lucide-react";

import {
  Collapsible,
  CollapsibleContent,
  CollapsibleTrigger,
} from "@/components/ui/collapsible";
import {
  SidebarGroup,
  SidebarGroupLabel,
  SidebarMenu,
  SidebarMenuButton,
  SidebarMenuItem,
  SidebarMenuSub,
  SidebarMenuSubButton,
  SidebarMenuSubItem,
} from "@/components/ui/sidebar";
import { WikiCategory } from "@/wdata/wiki";
import { usePathname } from "next/navigation";

interface SidebarCategoryProps {
  items: WikiCategory[];
}

export function SidebarCategoryList({ items }: SidebarCategoryProps) {
  const pathname = usePathname();

  var activePageSlug = null;
  var activeCategoryName = null;
  items.forEach((category) => {
    category.subs.forEach((subcategory) => {
      subcategory.pages.forEach((page) => {
        if (pathname.includes(page.slug)) {
          activePageSlug = page.slug;
          activeCategoryName = subcategory.name;
        }
      });
    });
  });

  return (
    <>
      {items.map((category, idx) => (
        <SidebarGroup key={idx}>
          <SidebarGroupLabel>{category.name}</SidebarGroupLabel>
          <SidebarMenu>
            {category.subs.map((item) => (
              <Collapsible
                key={item.name}
                asChild
                defaultOpen={item.name == activeCategoryName}
                className="group/collapsible"
              >
                <SidebarMenuItem>
                  <CollapsibleTrigger asChild>
                    <SidebarMenuButton tooltip={item.name}>
                      {item.icon && <item.icon />}
                      <span>{item.name}</span>
                      <ChevronRight className="ml-auto transition-transform duration-200 group-data-[state=open]/collapsible:rotate-90" />
                    </SidebarMenuButton>
                  </CollapsibleTrigger>
                  <CollapsibleContent>
                    <SidebarMenuSub>
                      {item.pages?.map((subItem) => (
                        <SidebarMenuSubItem key={subItem.title}>
                          <SidebarMenuSubButton
                            asChild
                            isActive={subItem.slug == activePageSlug}
                          >
                            <a href={"/wiki/" + subItem.slug}>
                              <span>{subItem.title}</span>
                            </a>
                          </SidebarMenuSubButton>
                        </SidebarMenuSubItem>
                      ))}
                    </SidebarMenuSub>
                  </CollapsibleContent>
                </SidebarMenuItem>
              </Collapsible>
            ))}
          </SidebarMenu>
        </SidebarGroup>
      ))}
    </>
  );
}
