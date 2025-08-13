@echo off
setlocal enabledelayedexpansion

rem Set the constant part of the filename
set constant_part=_AoE2

rem Loop through all files matching the pattern *%constant_part%.webp
for %%f in (*%constant_part%.webp) do (
    rem Extract the filename without the extension
    set filename=%%~nf

    rem Add the _civ to the filename
    set new_filename=!filename!_civ.webp

    rem Convert the new filename to lowercase using PowerShell
    for /f %%i in ('powershell -command "[CultureInfo]::InvariantCulture.TextInfo.ToLower('!new_filename!')"') do set new_filename=%%i


    rem Rename the file
    ren "%%f" "!new_filename!"
)

endlocal