# Kafka Microservices Verification Guide

This guide provides step-by-step commands to verify all components are working correctly.

---

## 1. Check Docker Containers Status

```bash
docker ps
```

**Expected Output:** You should see containers for:
- `kafka` (or kafka container)
- `order-service`
- `inventory-service`
- `billing-service`
- `api-gateway`

All containers should show status "Up".

---

## 2. Check Kafka Logs (KRaft Mode)

```bash
docker logs kafka
```

**Look for these success indicators:**
- `Kafka cluster ID: <some-id>`
- `Transitioned from RECOVERY to RUNNING`
- `started (kafka.server.KafkaServer)`

**Alternative command with tail:**
```bash
docker logs --tail 100 kafka
```

---

## 3. Check Microservices Logs

**Order Service:**
```bash
docker logs order-service
```
Look for: `Started OrderServiceApplication`

**Inventory Service:**
```bash
docker logs inventory-service
```
Look for: `Started InventoryServiceApplication`

**Billing Service:**
```bash
docker logs billing-service
```
Look for: `Started BillingServiceApplication`

**API Gateway:**
```bash
docker logs api-gateway
```
Look for: `Started ApiGatewayApplication`

---

## 4. Test Kafka Topic Creation

```bash
docker exec kafka kafka-topics --bootstrap-server localhost:9092 --list
```

**Expected Topics:**
- `order-events`
- `inventory-events`
- `billing-events`

---

## 5. Test API Gateway Endpoints

### Health Check - Order Service:
```bash
curl http://localhost:8080/actuator/health
```

### Create an Order (POST):
```bash
curl -X POST http://localhost:8081/orders \
  -H "Content-Type: application/json" \
  -d '{"orderId":"ORD-1001","item":"Laptop","quantity":1}'
```

**Expected Response:**
```json
{
  "orderId": "ORD-1001",
  "item": "Laptop",
  "quantity": 1,
  "status": "CREATED"
}
```

### Get Order by ID:
```bash
curl http://localhost:8081/orders/ORD-1001
```

### Check Inventory:
```bash
curl http://localhost:8082/inventory
```

### Check Billing/Invoices:
```bash
curl http://localhost:8083/invoices
```

---

## 6. Verify Kafka Event Communication

### Check Kafka logs for consumer group activity:
```bash
docker logs kafka 2>&1 | grep -i "Consumer"
```

### Test if consumers are registered:
```bash
docker exec kafka kafka-consumer-groups --bootstrap-server localhost:9092 --list
```

**Expected Consumer Groups:**
- `order-consumer-group`
- `inventory-consumer-group`
- `billing-consumer-group`

### Check consumer group details:
```bash
docker exec kafka kafka-consumer-groups --bootstrap-server localhost:9092 --group order-consumer-group --describe
```

---

## 7. Complete End-to-End Test

### Step 1: Create Order
```bash
curl -X POST http://localhost:8081/orders \
  -H "Content-Type: application/json" \
  -d '{"orderId":"TEST-001","item":"Gaming Laptop","quantity":2}'
```

### Step 2: Verify Order Created
```bash
curl http://localhost:8081/orders/TEST-001
```

### Step 3: Check Inventory (should show deduction)
```bash
curl http://localhost:8082/inventory
```

### Step 4: Check Billing (invoice should be created)
```bash
curl http://localhost:8083/invoices
```

---

## 8. Troubleshooting Commands

### Check Kafka Zookeeper/KRaft status:
```bash
docker exec kafka kafka-metadata --snapshot /var/lib/kafka/data/__cluster_metadata-0/rocksdb/checkpoints/NAME-0000000000.log
```

### Check if port 9092 is accessible:
```bash
telnet localhost 9092
```

### Check container networking:
```bash
docker network ls
docker network inspect kafka-lab_default
```

### Restart a specific service:
```bash
docker restart order-service
```

### View real-time logs:
```bash
docker logs -f kafka
docker logs -f order-service
```

---

## Screenshot Requirements

For your deliverables, capture these screenshots:

1. **Docker Containers Running**
   - `docker ps` showing all 5 containers

2. **Kafka KRaft Mode Success**
   - Kafka logs showing cluster ID and RUNNING status

3. **All Microservices Started**
   - Logs from order-service, inventory-service, billing-service, api-gateway

4. **Postman/API Test Success**
   - Screenshot of successful POST request to create order

5. **Kafka Topics**
   - Output of `kafka-topics --list` showing all topics

6. **Event Communication Proof**
   - Show order created, inventory updated, invoice generated

---

## Quick Health Check Script

Run all health checks at once:
```bash
echo "=== Container Status ===" && docker ps --filter "name=kafka" --filter "name=service" --filter "name=gateway" && \
echo "=== Kafka Logs ===" && docker logs --tail 20 kafka && \
echo "=== Topic List ===" && docker exec kafka kafka-topics --bootstrap-server localhost:9092 --list && \
echo "=== Order Service Health ===" && curl -s http://localhost:8081/actuator/health
```

---

## Expected Architecture Flow

```
Postman/API Request
       │
       ▼
  API Gateway (8080) ─── Routes to ─── Order Service (8081)
       │                              │
       │                              ▼
       │                         Kafka (9092)
       │                              │
       │                              ▼
       │                         Inventory Service (8082)
       │                              │
       │                              ▼
       │                         Billing Service (8083)
       │                              │
       │                         Database
```
