{\rtf1\ansi\ansicpg1252\cocoartf2868
\cocoatextscaling0\cocoaplatform0{\fonttbl\f0\fswiss\fcharset0 Helvetica;}
{\colortbl;\red255\green255\blue255;}
{\*\expandedcolortbl;;}
\paperw11900\paperh16840\margl1440\margr1440\vieww21080\viewh12980\viewkind0
\pard\tx566\tx1133\tx1700\tx2267\tx2834\tx3401\tx3968\tx4535\tx5102\tx5669\tx6236\tx6803\pardirnatural\partightenfactor0

\f0\fs24 \cf0 # Ticketing System Architecture & Context\
\
## 1. System Requirements & Scale Estimates\
* **Target Load:** 500,000 concurrent users during flash sales (e.g., Taylor Swift Eras Tour scenario).\
* **Throughput:** ~10,000+ concurrent bookings per second during peak.\
* **Latency:** <500ms for seat availability check, <2s for reservation.\
* **Core Constraints:** Zero double-booking (strict consistency required), handle payment gateway failures gracefully.\
* **Storage Estimates:** ~15 GB/year (Accounting for 12M bookings/year, ~2.4 GB active seat inventory).\
\
## 2. Microservices Architecture (Domain Decomposition)\
The system is divided into bounded contexts to scale independently and maintain consistency:\
* **API Gateway / Load Balancer:** Single entry point, rate limiting, auth routing.\
* **Auth Service:** JWT-based authentication and user management.\
* **Search Service:** OpenSearch/Elasticsearch based. Read-optimized and eventually consistent. \
* **Events Service:** Manages event lifecycle, metadata, and dynamic pricing rules.\
* **Inventory Service:** The core state machine for seats (`AVAILABLE`, `RESERVED`, `BOOKED`). Handles strict locking and reservation holds.\
* **Booking Service:** Orchestrator for the checkout flow (Reservation \uc0\u8594  Payment \u8594  Confirmation). Enforces idempotency.\
* **Payment Adapter:** Isolates 3rd-party payment gateways, retries, and webhooks.\
* **Indexer / Notification Services:** Async processors reading from Kafka (CDC/Outbox pattern) to update search indexes and send emails/SMS.\
\
## 3. Database Schema (Per-Service Databases)\
To prevent cross-domain entanglement, databases are split by service without cross-DB foreign keys (referential integrity is enforced at the application layer).\
\
* **Events DB (`events` table):**\
  * Canonical record for events. Holds total/available seats and sale times.\
* **Inventory DB (`seats`, `reservations` tables):**\
  * `seats`: Live seat inventory (e.g., "A1"). Tracks `status`, `price`, `reserved_by`, `reserved_until`, and `version` (for fallback optimistic locking).\
  * `reservations`: Ephemeral holds with an `expires_at` timestamp.\
* **Bookings DB (`bookings`, `booking_seats` tables):**\
  * `bookings`: Final confirmed order header with `payment_id` and `total_amount`.\
  * `booking_seats`: Junction table mapping the booking to the seat IDs and locking in the snapshot price.\
\
## 4. Concurrency & Locking Strategy\
The system evaluates and rejects standard database locks due to the distributed, multi-server nature of the system:\
* **Rejected - Pessimistic Locking (`SELECT FOR UPDATE`):** Blocks requests and only locks within a single database connection. If Server 1 and 2 query different DB replicas, both can get the same seat.\
* **Rejected - Optimistic Locking (`version` column):** Results in massive `ConcurrentModificationException` spikes. 500,000 users retrying at once creates a "thundering herd" that crashes the database.\
* **Selected Strategy - Distributed Locking with Redis:**\
  * Uses Redis `SETNX` with a unique UUID and a short Time-To-Live (TTL) (e.g., 30s) keyed to the seat (e.g., `seat:lock:123:A1`).\
  * **Redlock Algorithm:** Used for production Redis Clusters. It attempts to acquire locks on N Redis instances (typically 5) to guarantee consistency even if a Redis node fails.\
  * **Critical Lua Script Requirement:** Releasing the lock *must* use a Lua script to check that the UUID in Redis matches the application thread's UUID before deleting. This prevents a slow thread from accidentally deleting a new thread's lock.\
\
## 5. Implementation Patterns (Java / Spring Boot)\
The system leverages specific Spring Boot patterns for execution:\
* **Redis Interactions:** Utilizes `RedisTemplate<String, String>` and `DefaultRedisScript` to execute the lock acquisition and the release Lua script.\
* **Database Transactions:** Relies on Spring `@Transactional` boundaries. The Redis lock wraps the database transaction. If the lock is acquired, the thread proceeds to check the DB and update the seat status to `RESERVED`.\
* **Idempotency:** The Booking Service uses an idempotency key stored in Redis (e.g., `idempotency:\{key\}`) for 24 hours to prevent duplicate bookings if a user double-clicks or a network retry occurs.\
* **Compensating Transactions:** A scheduled job (`@Scheduled(fixedRate = 60000)`) runs every minute to find expired records in the `reservations` table, release their holds, and revert the seat status back to `AVAILABLE`.}