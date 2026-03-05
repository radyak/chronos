// Clear relations
MATCH (a)-[r]->()
DELETE a, r;
