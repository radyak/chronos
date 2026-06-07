//// EARLY ROME
// Nodes
CREATE (RomanEmpire:Territory {
  key: 'roman-empire'
})
CREATE (Otho:Person {
  key:     'otho',
  start:   '0032-04-28',
  end:     '0069-04-16',
  wikiqid: 'Q1416',
  name:    'Marcus Salvius Otho'
})
CREATE (Vitellius:Person {
  key:     'vitellius',
  start:   '0015-09-27',
  end:     '0069-12-20',
  wikiqid: 'Q1417',
  name:    'Aulus Vitellius'
})
CREATE (Vespasian:Person {
  key:     'vespasian',
  start:   '0009-09-17',
  end:     '0079-07-23',
  wikiqid: 'Q1419',
  name:    'Titus Flavius Vespasianus'
})
CREATE(Titus:Person {
  key:     'titus',
  start:   '0039-12-30',
  end:     '0081-09-13',
  wikiqid: 'Q1421',
  name:    'Titus Flavius Vespasianus'
})
CREATE (Domitian:Person {
  key:   'domitian',
  start: '0051-10-24',
  end:   '0096-09-18',
  name:  'Titus Flavius Domitianus'
})
CREATE (Domitilla:Person {
  key:  'domitilla-the-elder',
  name: 'Flavia Domitilla Major'
})
CREATE (Caenis:Person {
  key:  'caenis',
  end:  '0074',
  name: 'Antonia Caenis'
})

// Relations: RULED
CREATE (Otho)-[:RULED {
  titles: ['emperor'],
  start:  '0069-01-15',
  end:    '0069-04-16'
}]->(RomanEmpire)
CREATE (Vitellius)-[:RULED {
  titles: ['emperor'],
  start:  '0069-04-19',
  end:    '0069-12-20'
}]->(RomanEmpire)
CREATE (Vespasian)-[:RULED {
  titles: ['emperor']
}]->(RomanEmpire)
CREATE (Titus)-[:RULED {
  titles: ['emperor']
}]->(RomanEmpire)
CREATE (Domitian)-[:RULED {
  titles: ['emperor']
}]->(RomanEmpire)

// Flavian Family
// Relations: RELATIONSHIP_WITH
CREATE (Vespasian)-[:RELATIONSHIP_WITH {
  status: 'married'
}]->(Domitilla)
CREATE (Vespasian)-[:RELATIONSHIP_WITH {
  status: 'cohabitation'
}]->(Caenis)

// Relations: CHILD_OF
CREATE (Titus)-[:CHILD_OF {
  type: 'biological'
}]->(Domitilla)
CREATE (Titus)-[:CHILD_OF {
  type: 'biological'
}]->(Vespasian)
CREATE (Domitian)-[:CHILD_OF {
  type: 'biological'
}]->(Domitilla)
CREATE (Domitian)-[:CHILD_OF {
  type: 'biological'
}]->(Vespasian)



//// CRISIS OF 3RD CENTURY
// Nodes
CREATE (ValerianI:Person {
  key:   'valerian-i',
  start: '019*',
  end:   '0260',
  name:  'Publius Licinius Valerianus'
})
CREATE (Gallienus:Person {
  key:   'gallienus',
  start: '0218',
  end:   '0268',
  name:  'Publius Licinius Egnatius Gallienus'
})
CREATE (ClaudiusGothicus:Person {
  key:   'claudius-gothicus',
  start: '0214',
  end:   '0270',
  name:  'Marcus Aurelius Claudius'
})
CREATE (Quintillus:Person {
  key:   'quintillus',
  start: '022*',
  end:   '0270',
  name:  'Marcus Aurelius Claudius Quintillus'
})
CREATE (Aurelian:Person {
  key:   'aurelian',
  start: '0214',
  end:   '0275',
  name:  'Lucius Domitius Aurelianus'
})
CREATE (Tacitus:Person {
  key:   'tacitus',
  start: '0200',
  end:   '0276',
  name:  'Marcus Claudius Tacitus'
})

// Relations: RULED
CREATE (ValerianI)-[:RULED {
  titles: ['emperor'],
  start:  '0253',
  end:    '0260'
}]->(RomanEmpire)
CREATE (Gallienus)-[:RULED {
  titles: ['emperor'],
  start:  '0253', to:
          '0268'
}]->(RomanEmpire)
CREATE (ClaudiusGothicus)-[:RULED {
  titles: ['emperor'],
  start:  '0268',
  end:    '0270'
}]->(RomanEmpire)
CREATE (Quintillus)-[:RULED {
  titles: ['emperor'],
  start:  '0270',
  end:    '0270'
}]->(RomanEmpire)
CREATE (Aurelian)-[:RULED {
  titles: ['emperor'],
  start:  '0270',
  end:    '0275'
}]->(RomanEmpire)
CREATE (Tacitus)-[:RULED {
  titles: ['emperor'],
  start:  '0275',
  end:    '0276'
}]->(RomanEmpire)



//// Gallic Empire
// Nodes
CREATE (GallicEmpire:Territory {
  key:   'gallic-empire',
  start: '0267',
  end:   '0273'
})
CREATE (Postumus:Person {
  key:  'postumus',
  end:  '0269',
  name: 'Marcus Cassianius Latinius Postumus'
})
CREATE (Marius:Person {
  key:  'marius',
  end:  '0269',
  name: 'Marcus Aurelius Marius'
})
CREATE (Victorinus:Person {
  key:   'victorinus',
  start: '02**',
  end:   '0270|0271',
  name:  'Marcus Piavonius Victorinus'
})
CREATE (Tetricus:Person {
  key:  'tetricus',
  end:  '0274',
  name: 'Gaius Pius Esuvius Tetricus'
})

// Relations: RULED
CREATE (Postumus)-[:RULED {
  titles: ['emperor'],
  start:  '0260',
  end:    '0269'
}]->(GallicEmpire)
CREATE (Marius)-[:RULED {
  titles: ['emperor'],
  start:  '0269',
  end:    '0269'
}]->(GallicEmpire)
CREATE (Victorinus)-[:RULED {
  titles: ['emperor'],
  start:  '0269',
  end:    '0270'
}]->(GallicEmpire)
CREATE (Tetricus)-[:RULED {
  titles: ['emperor'],
  start:  '0270',
  end:    '0274'
}]->(GallicEmpire)



// Palmyrene Empire
// Nodes
CREATE (PalmyreneEmpire:Territory {
  key:   'palmyrene-empire',
  start: '0260',
  end:   '0274'
})
CREATE (Vaballathus:Person {
  key:   'vaballathus',
  start: '0259',
  end:   '0274',
  name:  'Septimius Vaballathus'
})
CREATE (Zenobia:Person {
  key:   'zenobia',
  start: '0240',
  end:   '0274',
  name:  'Septimia Zenobia'
})
CREATE (Antiochus:Person {
  key:  'antiochus',
  end:  '0273',
  name: 'Septimius Antiochus'
})

// Relations: RULED
CREATE (Vaballathus)-[:RULED {
  titles: ['emperor', 'king'],
  start:  '0267',
  end:    '0272'
}]->(PalmyreneEmpire)
CREATE (Zenobia)-[:RULED {
  titles: ['emperor', 'king'],
  start:  '0272',
  end:    '0273'}]->(PalmyreneEmpire)
CREATE (Antiochus)-[:RULED {
  titles: ['emperor', 'king'],
  start:  '0273',
  end:    '0273'
}]->(PalmyreneEmpire)

// Relations: SECESSIONAL_TO
CREATE (GallicEmpire)-[:SECESSIONAL_TO]->(RomanEmpire)
CREATE (PalmyreneEmpire)-[:SECESSIONAL_TO]->(RomanEmpire)
