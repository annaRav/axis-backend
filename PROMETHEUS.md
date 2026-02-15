# Prometheus Monitoring

Prometheus настроен для автоматического сбора метрик со всех микросервисов Axis Backend.

## Доступ к Prometheus

После запуска `skaffold dev`, Prometheus будет доступен по адресу:

**http://localhost:9090**

## Метрики микросервисов

Каждый сервис экспортирует метрики в формате Prometheus по пути `/q/metrics`:

- **axis-goal**: http://localhost:8081/q/metrics
- **axis-notification**: http://localhost:8082/q/metrics
- **axis-media**: http://localhost:8083/q/metrics

## Конфигурация

Prometheus автоматически скрапирует метрики со всех сервисов каждые 15 секунд. Конфигурация находится в:

```
k8s/infrastructure/prometheus.yaml
```

## Основные метрики Quarkus

### JVM метрики
- `jvm_memory_used_bytes` - использование памяти
- `jvm_gc_memory_allocated_bytes` - выделенная память для GC
- `jvm_threads_live_threads` - количество потоков

### HTTP метрики
- `http_server_requests_seconds_count` - количество HTTP запросов
- `http_server_requests_seconds_sum` - общее время обработки запросов
- `http_server_requests_seconds_max` - максимальное время обработки

### Database метрики (Hibernate)
- `hikaricp_connections_active` - активные подключения к БД
- `hikaricp_connections_idle` - незанятые подключения
- `hibernate_query_executions_max_seconds` - максимальное время выполнения запроса

### Worker Pool метрики
- `worker_pool_active` - активные worker threads
- `worker_pool_queue_size` - размер очереди задач

## Полезные PromQL запросы

### Количество HTTP запросов по эндпоинтам
```promql
rate(http_server_requests_seconds_count[5m])
```

### 95-й перцентиль времени ответа
```promql
histogram_quantile(0.95, rate(http_server_requests_seconds_bucket[5m]))
```

### Использование памяти JVM
```promql
jvm_memory_used_bytes{area="heap"}
```

### Количество активных подключений к БД
```promql
hikaricp_connections_active
```

### Количество ошибок 5xx
```promql
rate(http_server_requests_seconds_count{status=~"5.."}[5m])
```

## Пример дашборда в Prometheus UI

1. Откройте http://localhost:9090
2. Перейдите в раздел **Graph**
3. Введите PromQL запрос в поле **Expression**
4. Нажмите **Execute**
5. Переключитесь на вкладку **Graph** для визуализации

## Targets (цели мониторинга)

Проверить статус всех целей можно в разделе **Status → Targets**:

http://localhost:9090/targets

Все сервисы должны иметь статус **UP**.

## Troubleshooting

### Сервис не отображается в Targets

```bash
# Проверить что Prometheus запущен
kubectl get pods -n axis -l app=prometheus

# Проверить логи Prometheus
kubectl logs -n axis -l app=prometheus

# Проверить что сервис доступен
kubectl exec -n axis -it <prometheus-pod> -- wget -O- http://axis-goal:8081/q/metrics
```

### Метрики не обновляются

```bash
# Проверить время последнего скрапа в Prometheus UI
# Status → Targets → Last Scrape

# Проверить что метрики доступны напрямую
curl http://localhost:8081/q/metrics
```

### Перезапустить Prometheus

```bash
kubectl rollout restart deployment/prometheus -n axis
```

## Настройка пути метрик

По умолчанию Quarkus использует путь `/q/metrics`. Для изменения пути добавьте в `application.properties`:

```properties
# Изменить путь на /actuator/prometheus (для совместимости со Spring Boot)
quarkus.micrometer.export.prometheus.path=/actuator/prometheus
```

**Важно:** После изменения пути также обновите конфигурацию Prometheus в `k8s/infrastructure/prometheus.yaml`:

```yaml
scrape_configs:
  - job_name: 'axis-goal'
    metrics_path: '/actuator/prometheus'  # Измените здесь
    static_configs:
      - targets: ['axis-goal:8081']
```

## Grafana (опционально)

Для более продвинутой визуализации можно добавить Grafana:

```bash
# Установить Grafana
kubectl apply -f https://raw.githubusercontent.com/grafana/grafana/main/deploy/kubernetes/grafana.yaml

# Port-forward
kubectl port-forward -n axis svc/grafana 3000:3000
```

Затем добавьте Prometheus как источник данных:
- URL: http://prometheus:9090
- Access: Server (default)

## Дополнительные ресурсы

- **Prometheus Documentation**: https://prometheus.io/docs/
- **PromQL Basics**: https://prometheus.io/docs/prometheus/latest/querying/basics/
- **Quarkus Micrometer Guide**: https://quarkus.io/guides/micrometer
- **Grafana Dashboards**: https://grafana.com/grafana/dashboards/