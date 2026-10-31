k8s/                       # Top-level folder for Kubernetes manifests
├── base/                   # Base manifests common across environments
│   ├── namespace.yaml
│   ├── serviceaccount.yaml
│   ├── rbac.yaml
│   ├── ingress.yaml
│   └── configmap.yaml
├── services/               # Each microservice has its own folder
│   ├── user-service/
│   │   ├── deployment.yaml
│   │   ├── service.yaml
│   │   ├── hpa.yaml
│   │   └── secrets.yaml
│   ├── order-service/
│   │   ├── deployment.yaml
│   │   ├── service.yaml
│   │   ├── hpa.yaml
│   │   └── secrets.yaml
│   └── payment-service/
│       ├── deployment.yaml
│       ├── service.yaml
│       ├── hpa.yaml
│       └── secrets.yaml
├── overlays/               # Environment-specific configurations (if using Kustomize)
│   ├── dev/
│   ├── staging/
│   └── prod/
└── helm/                   # Optional: Helm charts for packaging services
    ├── user-service/
    ├── order-service/
    └── payment-service/
