#!/bin/bash
#Currently to be run only from the Scraping directory
rm artifacts/kohlsScraper.zip
OLDPWD=$(pwd)
cd build/
zip -r9 ${OLDPWD}/artifacts/kohlsScraper.zip .
cd ${OLDPWD}
zip -r9 artifacts/kohlsScraper.zip *.json
zip -r9 artifacts/kohlsScraper.zip KohlsScraper.py
