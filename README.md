# Example application for prometheus-grafana monitoring stack on kubernetes
## Prerequisites
To run this project, you must first install the following tools:
* Docker
* Kubernetes cluster
* Helm 3
## Building the app
To build this app, the only required step is to build its docker image.
```
docker build -t java-prometheus-test .
```
## Setup the prometheus-grafana stack
To setup the stack, run the following command:
```
helm install prometheus-grafana-test prometheus-community/kube-prometheus-stack
```
This will setup the stack without any ingress or persistence. If you want to do that, you need to have ingress enabled and have a storage class/persistent volume ready. values.yaml then can look like:
```yaml
---
grafana:
 ingress:
  enabled: true
  ingressClassName: nginx
  hosts:
   - grafana.kube.local
prometheus:
  prometheusSpec:
    storageSpec:
      volumeClaimTemplate:
        spec:
          storageClassName: local-fs-storage
          accessModes: ["ReadWriteOnce"]
          resources:
            requests:
              storage: 1Gi
```

## Deploy the test application as a monitorable component
To deploy the application as a monitored service, run the following command:
```
kubectl apply -f app-kube.yaml
```

Beware that the naming is important. The helm chart for prometheus and grafana stack, by default, uses its name, "prometheus-grafana-test" in this case, as the selector for ServiceMonitors. If you choose a different name, then you have to change ServiceMonitor's definition label
to have a corresponding label:
```yaml
labels:
    release: the chosen name
``` 

If everyting went well, you should be able to see the application in the prometheus targets. If you don't have an ingress running, you can do port-forward for the prometheus service on port 9090. Same for the grafana (default username is admin and password is prom-operator).

## Test
If you have the application running without the ingress, you can check it using a port-forward.
First check name of the services created:
```
kubectl get services
```
There will be one service with its name ending with "-prometheus" and port 9090 open. In my case, the name is "prometheus-grafana-test-ku-prometheus". Another service of our interest is the one having the name we have specified in our helm chart installation. In my case, that is the "prometheus-grafana-test".

To test the monitoring, we can port forward to the pods running the app. Use this command to see running pods:
```
kubectl get pod
```
There should be two pods running with their name being "prometheus-test-app-deplyoment-XXXXX-XXX".
The application has one endpoint: `/test`, that just sleeps for a random time between 0 and 5 seconds and then returns a string.
The exposed metric is called `greeting_time`.