sudo apt update
sudo apt install npm
sudo apt install nodejs
sudo apt install nodejs-legacy
npm install
SOLR_PROTOCOL="http" \
SOLR_HOST="localhost" \
SOLR_PORT="8983" \
SOLR_CORE_NAME="search_core" \
PORT=5000 \
PAGE_RANK_FILE_NAME=pageRankFile \
PAGE_RANK_ORDER=desc \
npm start bin/www
