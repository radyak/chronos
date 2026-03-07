// Clear relations
MATCH (a)-[r]->()
DELETE a, r;

// Clear nodes
MATCH (a)
DELETE a;
