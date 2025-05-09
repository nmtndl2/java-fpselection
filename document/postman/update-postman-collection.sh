#!/bin/bash

# Your Postman API Key
POSTMAN_API_KEY="PMAK-681dad06d7fefe0001572175-XXXX"

# Your Collection UID (use _postman_id from the exported collection metadata)
COLLECTION_UID="dbaa5edc-8e65-42e0-8008-215d80100d69"

# URL of the raw Postman collection JSON file from GitHub
GITHUB_RAW_URL="https://raw.githubusercontent.com/nmtndl2/java-fpselection/main/document/postman/FPSelection.postman_collection.json"

# Fetch the JSON data from the raw GitHub URL
JSON_DATA=$(curl -s "$GITHUB_RAW_URL")

# Prepare the PUT body (wrap the collection data into the required format)
PUT_BODY=$(jq -n --argjson collection "$JSON_DATA" '{ collection: $collection }')

# Update the Postman collection using the Postman API
curl --silent --location --request PUT "https://api.getpostman.com/collections/$COLLECTION_UID" \
  --header "X-Api-Key: $POSTMAN_API_KEY" \
  --header "Content-Type: application/json" \
  --data "$PUT_BODY"

# Confirmation message
echo "✅ Postman collection updated in Postman workspace."
