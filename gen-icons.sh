#!/bin/bash
#
# Generate icons from the SVG. Ideally, this should be done in the ant task,
# but this really only happens rarely, whenever there is tweaking of the icon.
#
# Generated icons are checked in, so no need to run this script every time.
##

BASE_DRAWABLE=res/drawable

declare -A resolutions=( ["xhdpi"]="96" ["hdpi"]="72" \
                         ["mdpi"]="48" ["ldpi"]="36")
for res in "${!resolutions[@]}"; do
    SIZE="${resolutions[$res]}"
    FILE=${BASE_DRAWABLE}-${res}/ic_launcher_ptt.png
    inkscape --export-png=${FILE}.tmp \
	     --export-width=$SIZE --export-height=$SIZE pitchtrack.svg
    pngcrush -e "" ${FILE}.tmp >/dev/null 2>&1
    rm -f ${FILE}.tmp
done

