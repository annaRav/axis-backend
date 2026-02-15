# Gateway API Setup (Ingress 2.0)

Gateway API - это современная замена Ingress с более мощными возможностями routing.

Проект использует **NGINX Gateway Fabric** версии 1.5.0 от F5/NGINX Inc. как реализацию Gateway API.

## Установка в Minikube

### 1. Установить Gateway API CRDs

```bash
kubectl kustomize "https://github.com/nginx/nginx-gateway-fabric/config/crd/gateway-api/standard?ref=v1.5.0" | kubectl apply -f -
```

### 2. Установить NGINX Gateway Fabric Controller

```bash
# Установить CRDs для NGINX Gateway Fabric
kubectl apply -f https://raw.githubusercontent.com/nginx/nginx-gateway-fabric/v1.5.0/deploy/crds.yaml

# Установить контроллер
kubectl apply -f https://raw.githubusercontent.com/nginx/nginx-gateway-fabric/v1.5.0/deploy/default/deploy.yaml
```

**Примечание:** cert-manager НЕ требуется для базовой установки.

**Альтернативные контроллеры:**
- **Envoy Gateway**: https://gateway.envoyproxy.io
- **Istio Gateway**: https://istio.io/latest/docs/tasks/traffic-management/ingress/gateway-api/
- **Traefik**: https://doc.traefik.io/traefik/routing/providers/kubernetes-gateway/

### 3. Проверить что контроллер установлен

```bash
# Проверить namespace
kubectl get namespace nginx-gateway

# Проверить что pod запущен (может занять 1-2 минуты)
kubectl wait --for=condition=ready pod -l app.kubernetes.io/name=nginx-gateway -n nginx-gateway --timeout=120s

# Проверить статус
kubectl get pods -n nginx-gateway
kubectl get gatewayclass
```

Вы должны увидеть:
```
NAME                             READY   STATUS    RESTARTS   AGE
nginx-gateway-575fccd7f6-xxxxx   2/2     Running   0          1m

NAME    CONTROLLER                                      ACCEPTED   AGE
nginx   gateway.nginx.org/nginx-gateway-controller      True       1m
```

### 4. Развернуть приложение

```bash
# Настроить Docker для Minikube
eval $(minikube docker-env)

# Развернуть с Gateway API
skaffold dev
```

**Примечание:** Если требуется чистая установка:
```bash
kubectl delete namespace axis
skaffold dev
```

### 5. Проверить Gateway и Routes

```bash
# Gateway
kubectl get gateway -n axis
kubectl describe gateway axis-gateway -n axis

# HTTPRoutes
kubectl get httproute -n axis
```

Вы должны увидеть:
```
NAME            CLASS   ADDRESS        PROGRAMMED   AGE
axis-gateway    nginx   192.168.49.2   True         1m

NAME                  HOSTNAMES   AGE
goal-route            ["*"]       1m
notification-route    ["*"]       1m
media-route           ["*"]       1m
health-route          ["*"]       1m
```

### 6. Доступ к сервисам

Все запросы идут через Gateway на порту 8080 (Skaffold port-forward):

```bash
# Goal service
curl http://localhost:8080/api/goals

# Notification service
curl http://localhost:8080/api/notifications

# Media service
curl http://localhost:8080/api/media

# Health check
curl http://localhost:8080/actuator/health
```

## Преимущества Gateway API

### 1. Более мощный routing

```yaml
# Header-based routing
matches:
  - headers:
    - name: "X-Version"
      value: "v2"

# Query parameter routing
matches:
  - queryParams:
    - name: "env"
      value: "staging"

# Method-based routing
matches:
  - method: "POST"
```

### 2. Traffic splitting (A/B testing, Canary)

```yaml
backendRefs:
  - name: axis-goal-v1
    port: 8081
    weight: 90
  - name: axis-goal-v2
    port: 8081
    weight: 10  # 10% трафика на v2
```

### 3. Request/Response modifications

```yaml
filters:
  - type: RequestHeaderModifier
    requestHeaderModifier:
      add:
        - name: X-Custom-Header
          value: "my-value"
  - type: URLRewrite
    urlRewrite:
      path:
        type: ReplacePrefixMatch
        replacePrefixMatch: /v2/
```

### 4. Timeout и retry policies

```yaml
rules:
  - matches:
    - path:
        type: PathPrefix
        value: /api/goals
    timeouts:
      request: 30s
    backendRefs:
    - name: axis-goal
      port: 8081
```

## Сравнение с Ingress

| Feature | Ingress | Gateway API |
|---------|---------|-------------|
| Path routing | ✅ | ✅ |
| Header routing | ❌ (annotations) | ✅ (native) |
| Query param routing | ❌ | ✅ |
| Traffic splitting | ❌ | ✅ |
| Request modification | ❌ (annotations) | ✅ (native) |
| Multi-protocol | ❌ | ✅ (HTTP, gRPC, TCP, UDP) |
| Role separation | ❌ | ✅ (Class/Gateway/Route) |
| GA Status | ✅ (since 2019) | ✅ (since 2023) |

## Troubleshooting

### Gateway не получает ADDRESS

```bash
# Проверить статус Gateway
kubectl describe gateway axis-gateway -n axis

# Проверить Gateway Controller
kubectl get pods -n nginx-gateway
kubectl logs -n nginx-gateway -l app.kubernetes.io/name=nginx-gateway -c nginx-gateway

# Проверить логи NGINX data plane
kubectl logs -n nginx-gateway -l app.kubernetes.io/name=nginx-gateway -c nginx
```

### HTTPRoute не работает

```bash
# Проверить статус маршрута
kubectl describe httproute goal-route -n axis

# Посмотреть события
kubectl get events -n axis

# Проверить что Gateway принял маршрут
kubectl get httproute -n axis -o wide
```

### 404 Not Found

Проверьте что сервисы запущены:
```bash
kubectl get svc -n axis
kubectl get pods -n axis

# Проверить endpoint'ы
kubectl get endpoints -n axis
```

### Controller не запускается

```bash
# Проверить события
kubectl get events -n nginx-gateway --sort-by='.lastTimestamp'

# Проверить логи
kubectl logs -n nginx-gateway deployment/nginx-gateway --all-containers

# Переустановить контроллер
kubectl delete namespace nginx-gateway
kubectl delete gatewayclass nginx
# Затем повторить установку с шага 1
```

## Полная установка (Quick Start)

Для нового кластера или коллеги:

```bash
# 1. Запустить Minikube
minikube start

# 2. Установить Gateway API CRDs
kubectl kustomize "https://github.com/nginx/nginx-gateway-fabric/config/crd/gateway-api/standard?ref=v1.5.0" | kubectl apply -f -

# 3. Установить NGINX Gateway Fabric
kubectl apply -f https://raw.githubusercontent.com/nginx/nginx-gateway-fabric/v1.5.0/deploy/crds.yaml
kubectl apply -f https://raw.githubusercontent.com/nginx/nginx-gateway-fabric/v1.5.0/deploy/default/deploy.yaml

# 4. Подождать готовности контроллера
kubectl wait --for=condition=ready pod -l app.kubernetes.io/name=nginx-gateway -n nginx-gateway --timeout=120s

# 5. Проверить установку
kubectl get gatewayclass
kubectl get pods -n nginx-gateway

# 6. Запустить приложение
eval $(minikube docker-env)
skaffold dev
```

## Обновление версии

Для обновления на новую версию NGINX Gateway Fabric:

```bash
# Проверить текущую версию
kubectl get deployment nginx-gateway -n nginx-gateway -o jsonpath='{.spec.template.spec.containers[0].image}'

# Обновить на v2.4.1 (последняя стабильная)
kubectl apply -f https://raw.githubusercontent.com/nginx/nginx-gateway-fabric/v2.4.1/deploy/crds.yaml
kubectl apply -f https://raw.githubusercontent.com/nginx/nginx-gateway-fabric/v2.4.1/deploy/default/deploy.yaml

# Проверить статус обновления
kubectl rollout status deployment/nginx-gateway -n nginx-gateway
```

## Migration Path

Если позже захотите вернуться к простому Ingress:
```bash
# Удалить Gateway API ресурсы
kubectl delete -f k8s/gateway-api.yaml

# Удалить NGINX Gateway Fabric
kubectl delete namespace nginx-gateway
kubectl delete gatewayclass nginx

# Включить Ingress addon в Minikube
minikube addons enable ingress

# Применить старый Ingress
kubectl apply -f k8s/ingress.yaml
```

## Рекомендации

- ✅ **Используйте Gateway API** для новых проектов
- ✅ **Лучше** чем аннотации Ingress
- ✅ **Стабильный** (GA v1.0)
- ✅ **Будущее** Kubernetes networking
- ✅ **Не нужен service mesh** для advanced routing
- ✅ **cert-manager не требуется** для базовой установки

## Дополнительные ресурсы

### Документация

- **NGINX Gateway Fabric**: https://docs.nginx.com/nginx-gateway-fabric/
- **Gateway API**: https://gateway-api.sigs.k8s.io/
- **GitHub Repository**: https://github.com/nginx/nginx-gateway-fabric
- **Release Notes**: https://github.com/nginx/nginx-gateway-fabric/releases

### Примеры конфигурации

- **HTTPRoute примеры**: https://docs.nginx.com/nginx-gateway-fabric/examples/
- **Gateway API гайды**: https://gateway-api.sigs.k8s.io/guides/
- **Traffic Splitting**: https://gateway-api.sigs.k8s.io/guides/traffic-splitting/

### Используемая версия в проекте

- **NGINX Gateway Fabric**: v1.5.0
- **Gateway API**: v1.2.0
- **Deployment**: default (без cert-manager)
- **Namespace**: nginx-gateway
- **GatewayClass**: nginx