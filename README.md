# ECHO - Warning Alert App Using P2P Network to Improve Connectivity in Rural Areas

ECHO is a warning alert application designed to enhance connectivity in rural areas by leveraging a peer-to-peer (P2P) network. This project aims to ensure timely and reliable emergency notifications even when traditional network infrastructure is limited.

---

## Installation

To get a local copy up and running, follow these simple steps:

1. **Clone the repository**:

   ```bash

   mkdir echo-app
   cd echo-app
   git clone https://github.com/NoPayneNoGame/cn2025-inclu6ve.git .
   code .

   ```

2. **Install dependencies**:

   ```bash
   npm install
   ```

3. **Set up couchDB using Docker or your preferred installation method.**:

   Official Docs here: https://docs.couchdb.org/en/stable/install/index.html

   Docker
   make sure you have it installed

   ```bash
   docker run -d --name <your-db-name> -e COUCHDB_USER=<choose an admin username> -e COUCHDB_PASSWORD=<choose an admin password> couchdb:latest

   docker ps - confirm it's running
   ```

   Verify the container is running:

   ```bash
   docker ps
   ```

   Your local database will start on http://localhost:5984/
   and add _utils (http://localhost:5984/_utils) to open the built-in web-based GUI known as Fauxton

4. **Start the server**:

   ```bash
   npm run dev
   ```

   The server will start on `http://localhost:3000`.
