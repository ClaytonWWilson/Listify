#!/bin/bash
#Currently to be run only from the Scraping directory
rm artifacts/runOrchestrator.zip
zip -r9 artifacts/runOrchestrator.zip prefix_list_part*.txt
zip -r9 artifacts/runOrchestrator.zip runOrchestrator.py
