# FoodLyst Helm Charts

This directory contains standalone, production-ready Helm charts for every microservice and infrastructure component in the **FoodLyst** platform.

## Architecture & Principles
- **Separate Helm Charts**: Each microservice has its own independent Helm chart.
- **Values-Driven**: Microservices share standard, robust templates (`templates/deployment.yaml`, `templates/service.yaml`, `templates/_helpers.tpl`). Service behaviors (ports, images, replica counts, environment variables, probes, resources) are customized entirely via `values.yaml`.
- **Specialized Templates Only When Necessary**: Only infrastructure components with unique Kubernetes needs (e.g., MySQL StatefulSet + PVC + init ConfigMap; Kafka KRaft StatefulSet + headless service) define custom templates.

---

## Directory Structure

```text
helm/
├── mysql/                   # MySQL StatefulSet + PVC + Database Init Scripts
├── kafka/                   # Apache Kafka (KRaft mode) + Headless Service
├── zipkin/                  # Zipkin Distributed Tracing
├── config-server/           # Spring Cloud Config Server
├── eureka-server/           # Netflix Eureka Service Registry
├── gateway-service/         # Spring Cloud Gateway (LoadBalancer/NodePort)
├── user-service/            # User Microservice (Port 8079)
├── balance-service/         # Balance Microservice (Port 8081)
├── restaurant-service/      # Restaurant Microservice (Port 8083)
├── offer-service/           # Offer Microservice (Port 8084)
├── order-service/           # Order Microservice (Port 8080)
├── transaction-service/     # Transaction Microservice (Port 8082)
├── graborder-service/       # GrabOrder / Rating Microservice (Port 8088)
└── notification-service/    # Notification Microservice (Port 8087)
```

---

## Service Port Mapping

| Service | Port | Default Type | NodePort |
| :--- | :--- | :--- | :--- |
| **config-server** | 8888 | ClusterIP | - |
| **eureka-server** | 8761 | ClusterIP | - |
| **gateway-service** | 8222 | LoadBalancer | - |
| **user-service** | 8079 | NodePort | 30079 |
| **balance-service** | 8081 | NodePort | 30081 |
| **restaurant-service** | 8083 | NodePort | 30083 |
| **offer-service** | 8084 | NodePort | 30084 |
| **order-service** | 8080 | NodePort | 30080 |
| **transaction-service** | 8082 | NodePort | 30082 |
| **graborder-service** | 8088 | NodePort | 30088 |
| **notification-service** | 8087 | NodePort | 30087 |
| **mysql** | 3306 | ClusterIP | - |
| **kafka** | 9092 | ClusterIP | - |
| **zipkin** | 9411 | ClusterIP | - |

---

## Deployment Quickstart

### 1. Create Namespace (if not existing)
```bash
kubectl create namespace foodlyst-microservice
```

### 2. Deploy Infrastructure
```bash
helm install mysql ./mysql -n foodlyst-microservice
helm install kafka ./kafka -n foodlyst-microservice
helm install zipkin ./zipkin -n foodlyst-microservice
```

### 3. Deploy Platform Core Services
```bash
helm install config-server ./config-server -n foodlyst-microservice
helm install eureka-server ./eureka-server -n foodlyst-microservice
helm install gateway-service ./gateway-service -n foodlyst-microservice
```

### 4. Deploy Business Microservices
```bash
helm install user-service ./user-service -n foodlyst-microservice
helm install balance-service ./balance-service -n foodlyst-microservice
helm install restaurant-service ./restaurant-service -n foodlyst-microservice
helm install offer-service ./offer-service -n foodlyst-microservice
helm install order-service ./order-service -n foodlyst-microservice
helm install transaction-service ./transaction-service -n foodlyst-microservice
helm install graborder-service ./graborder-service -n foodlyst-microservice
helm install notification-service ./notification-service -n foodlyst-microservice
```

---

## Modifying Configurations

To customize any microservice, edit its `values.yaml` or provide overrides on the command line:

```bash
# Example: Scale replicas and change image tag
helm upgrade --install user-service ./user-service \
  --set replicaCount=3 \
  --set image.tag="v1.2.0" \
  -n foodlyst-microservice
```
