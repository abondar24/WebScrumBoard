#Grafana

A short guide to set up Grafana and Loki in Kubernetes cluster.

## Requirements

Helm. Check install guide [here](https://helm.sh/docs/intro/install/)

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
helm repo add loki https://grafana.github.io/loki/charts


helm upgrade --install promtail loki/promtail --set "loki.serviceName=loki"
```

4. Enter Grafana (pod-ip:3000) with default credentials admin/admin. 
5. Set loki:3100 as a data source
6. Open a dashboard and query {app="server"} to see logs from WebScrumBoard app



