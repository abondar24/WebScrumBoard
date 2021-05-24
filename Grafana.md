# Grafana

A short guide to set up Grafana and Loki in Kubernetes cluster.

## Order
1. Install Grafana
```
kubectl apply -f pods/grafana.yml
```

2. Install Loki
```
kubectl apply -f pods/loki.yml
```

3. Install Promtail

```
kubectl apply -f pods/promtail.yml
```

4. Enter Grafana (pod-ip:3000) with default credentials admin/admin. 
5. Set loki:3100 as a data source
6. Open a dashboard and query {app="server"} to see logs from WebScrumBoard app



