#!/bin/bash
#Currently to be run only from the Scraping directory
mkdir build
pip3 install --target build requests
pip3 install --target build PyMySQL
pip3 install --target build beautifulsoup4
mkdir artifacts
