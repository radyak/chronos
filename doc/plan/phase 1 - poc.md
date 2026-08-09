# Phase 1 - PoC
First PoCs were centered around the Historical data, its model and its representation in the UI, as well as its maintenance.

## Tech Stack
Early attempts were done in a monolithical Java + Spring Boot backend with a self-contained Angular frontend. Once Mongo DB database was used once SQL. 

## Learnings
These were the most important technical and conceptional learnings:

### 1. Relation-centered modelling
Neither a object-relational (i.e. SQL) nor a document-based (e.g. Mongo DB) database is suitable for the purpose, which put the emphasis on **entities** and their attributes. Historical data is by far rather centered around **relations** between *entities* and can themselves have attributes, too.

> 💡 ***Learning:***
> A graphical database (e.g. Neo4j) is necessary to properly reflect historical data.


### 2. Administrable Schema
Some of the first attempts were made with hard-coded model/schema. However, just like data, the schema had to change and scale quickly - which resulted in a lot of changes with a large bandwidth each. Thus, besides the data itself, also the schema must be flexible and abstracted - this allows to build components, that are flexible and and extend reactively, such as the UI.

> 💡 ***Learning:***
> The schema must be configurable from one central place or administrable.

### 3. Highly normalized Model
While some of the first approaches used fix, compiled models, others already used some kind of generic models (e.g. "Entity"), which could be equipped with "Tags". The latter were practially a category together with valid values - but without restrictions to what, how and in which combination they could be assigned. Besides that, only the "Entities" were not capable to carry dates.

> 💡 ***Learning:***
> The meta model must be simple, but powerful and comprehensive enough to allow the definiton of a complex and appropriate schema.

### 4. Modular backend with clear separation and scopes
From the beginning, the PoCs were built as monoliths, which even served the UI. This quickly led to blurring and unclear technical and domain boundaries and adverse couplings. Problems in one component affected others too and development became quickly heavyweight.

> 💡 ***Learning:***
> The application should be built with a Microservice Architecture in mind, i.e. with clearly scoped, small backend components.
> In addition to this, the services can be based on a shared/common lib.

### 5. Clean processes and development
Due to the PoC kind of approach, a process of developing, adding features and fixing issues was practically missing. Functionality was added from a rough idea, tests were only conducted through UI or API calls and the next step or goal was never clear. Insights came fast, but the overall state quickly became messy.

> [!TIP] Learning:
> Planning, conception and development need a clear and clean procedure - which should be followed always. Also intermediate and long-term goals should be defined.
