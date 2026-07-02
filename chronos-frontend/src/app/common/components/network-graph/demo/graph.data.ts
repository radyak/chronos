import { GraphData } from '../model/graph-data.model';

export const EXAMPLE_GRAPH: GraphData = {
  nodes: [
    { id: 'n1',  label: 'The Matrix',       type: 'Movie',  properties: { released: 1999, tagline: 'Welcome to the Real World' } },
    { id: 'n2',  label: 'Keanu Reeves',     type: 'Person', properties: { born: 1964 } },
    { id: 'n3',  label: 'Carrie-Anne Moss', type: 'Person', properties: { born: 1967 } },
    { id: 'n4',  label: 'Laurence Fishburne',type:'Person', properties: { born: 1961 } },
    { id: 'n5',  label: 'Lilly Wachowski',  type: 'Person', properties: { born: 1967 } },
    { id: 'n6',  label: 'Lana Wachowski',   type: 'Person', properties: { born: 1965 } },
    { id: 'n7',  label: 'Joel Silver',       type: 'Person', properties: { born: 1952 } },
    { id: 'n8',  label: 'Speed',             type: 'Movie',  properties: { released: 1994, tagline: 'Get ready for rush hour' } },
    { id: 'n9',  label: 'Jack Nicholson',    type: 'Person', properties: { born: 1937 } },
    { id: 'n10', label: 'The Departed',      type: 'Movie',  properties: { released: 2006, tagline: 'Cops or criminals' } },
  ],
  links: [
    { id: 'e1',  source: 'n2', target: 'n1', type: 'ACTED_IN',   properties: { roles: 'Neo' } },
    { id: 'e2',  source: 'n3', target: 'n1', type: 'ACTED_IN',   properties: { roles: 'Trinity' } },
    { id: 'e3',  source: 'n4', target: 'n1', type: 'ACTED_IN',   properties: { roles: 'Morpheus' } },
    { id: 'e4',  source: 'n5', target: 'n1', type: 'DIRECTED',   properties: {} },
    { id: 'e5',  source: 'n6', target: 'n1', type: 'DIRECTED',   properties: {} },
    { id: 'e6',  source: 'n7', target: 'n1', type: 'PRODUCED',   properties: {} },
    { id: 'e7',  source: 'n2', target: 'n8', type: 'ACTED_IN',   properties: { roles: 'Jack' } },
    { id: 'e8',  source: 'n9', target: 'n10',type: 'ACTED_IN',   properties: { roles: 'Costello' } },
    { id: 'e9',  source: 'n5', target: 'n10',type: 'DIRECTED',   properties: {} },
  ]
};
