// READ QUERIES

// Get all nodes + relations
MATCH (n1)-[r]->(n2) RETURN r, n1, n2 LIMIT 25

// Get all Persons
MATCH (p:Person) RETURN p

// Get all nodes related to Territory "gallic-empire"
MATCH (t:Territory {key:"gallic-empire"})-[r]-(q) RETURN t,r,q

MATCH (p:Person)-[r]->(t:Territory)
WHERE p.to < "100"
RETURN p,r,t


// UPDATE QUERIES

// Update a node
MATCH (p:Person {key: "valerian-i"})
SET p.key = "valerianus-i"
RETURN p

// Update a relationship
MATCH (p:Person {key: "zenobia"})-[r:RULED]->(t:Territory {key: "palmyrene-empire"})
SET r.titles = ["empress"], r.to = "273"
RETURN p,r,t


// META READ QUERIES

// Get all node labels
MATCH (n) RETURN distinct labels(n)
// ALTERNATIVE:
call db.labels();

// Get all relationship types
MATCH ()-[r]->() RETURN distinct type(r)
// ALTERNATIVE:
call db.relationshipTypes();


// META UPDATE QUERIES
CREATE CONSTRAINT KeyRequired
FOR (t:Territory)
REQUIRE t.key IS UNIQUE
// Note: IS NOT NULL is only supported in Neo4j enterprise edition