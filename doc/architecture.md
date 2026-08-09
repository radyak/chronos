# Architecture

Chronos is built in a (quasi-)microservice architecture.

```mermaid
---
config:
    theme: 'neutral'
---
flowchart
    classDef DomainService fill:#f9d5e5,stroke:#a33
    classDef core stroke-width:2px

    IDP["Keycloak"]
    Auth((Auth))

    subgraph Browser
        UI["UI"]
    end

    subgraph Cluster
        UIServer["UI Server"]
        GW["Gateway"]

        UIS["UI Service"]
        SDS["Schema Definition"]
        HDS["Historical Data"]
        Wiki["Wiki Service"]

    end

    %% Serve UI
    UIServer -.-> UI

    %% UI Access
    UI --> GW
    GW -->|/api/schema| SDS
    GW -->|/api/data| HDS
    GW -->|/api/wiki| Wiki
    GW -->|/api/ui| UIS

    %% Cluster-internal Access
    HDS -->|validation| SDS

    %% Auth Integration
    Auth --- IDP
    UI --> Auth
    HDS --> Auth
    SDS --> Auth
    Wiki --> Auth

    class UI,SDS,HDS,Wiki DomainService
    class UI,SDS,HDS core

```