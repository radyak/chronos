# Phase 2 - MVP

With the learnings from the first PoCs, a new PoC had been started:

* **Persistence:** graphical database (Neo4j)
* **Administrable Schema:** dedicated domain/service and UI
* **Normalized Model:** powerful meta model (also covered by graphical database)
* **Microservice Architecture:** Initially a monolith with few left-overs from previos PoCs, soon every domain was encapsulated as dedicated Microservice
* **Well-defined procedure:** Functionality was asserted with tests, clear requirement & dev process with Github issues and branches and discipline was increased through a up-front clean architecture and design.

With this approach, it was possible to move forward a lot cleaner and a lot faster. After some time, the (close-to-)MVP included:
* Dashboard with random entry (Wiki) article
* Login as admin
* Management of schema
* Management of data
* First display of data mesh
* API: Comprehensive mesh query definition

### New Challenges
* With the increase velocity, the functionality became so comprehensive and the codebase so complex, that extensions and adaptions need more and more conception and time to develop.
* The direction and priority which user group to focus on and which functionality to provide in the first version is not clear.
* Since perfectly clean design would have been time-consuming, here and there some shortcuts were taken (e.g. mix of DTO, domain and AO - "Admin Objects").
* UI design and UX were improvised here and there became a bit inconsistent, ugly and unintuitive.
* It is unclear how to proceed regarding the aspects "scientificity of data" (debated data) and "review & approval".