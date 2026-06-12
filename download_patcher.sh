#!/usr/bin/env bash
#
# (c) 2026 Muhammed Ali Bulut, All rights reserved.
#
# See LICENSE file in repository root for copy file of license. For copyright
# notices, technical issues, feedback, or any other related to this code file or
# project, please contact me via mamii@mamii.dev or other ways.
#

set -euo pipefail

OWNER="instafel"
REPO="p-rel"
ASSET_PREFIX="ifl-patcher-"
ASSET_SUFFIX="-release.jar"

API="https://api.github.com/repos/${OWNER}/${REPO}/releases/latest"

AUTH_HEADER=()

echo "Fetching latest patcher release info from: ${OWNER}/${REPO}"
body=$(curl -sSL "${AUTH_HEADER[@]}" -H "Accept: application/vnd.github.v3+json" "$API")

asset_info=$(echo "$body" | jq -r --arg pre "$ASSET_PREFIX" --arg suf "$ASSET_SUFFIX" '
  .assets[] | select(.name | startswith($pre) and endswith($suf))
')

if [[ -z "$asset_info" || "$asset_info" == "null" ]]; then
  echo "No any asset matched! Prefix: $ASSET_PREFIX, Suffix: $ASSET_SUFFIX"
  echo "All assets in this release:"
  echo "$body" | jq -r '.assets[].name'
  exit 1
fi

download_url=$(echo "$asset_info" | jq -r '.browser_download_url')

outpath="$(pwd)/ifl-patcher.jar"
tmpfile="${outpath}.part"

echo "Downloading → $outpath"

curl -L --fail --progress-bar -o "$tmpfile" \
  "${AUTH_HEADER[@]}" \
  -H "Accept: application/octet-stream" \
  "$download_url"

mv "$tmpfile" "$outpath"
echo "Download Completed: $outpath"