// Early Rome
CREATE (RomanEmpire:Territory {key:"roman-empire"})
CREATE (Otho:Person {key:"otho", from:date("0032-04-28"), to:date("0069-04-16"), name:"Marcus Salvius Otho"})
CREATE (Vitellius:Person {key:"vitellius", from:date("0015-09-27"), to:date("0069-12-20"), name:"Aulus Vitellius"})
CREATE (Vespasian:Person {key:"vespasian", from:date("0009-09-17"), to:date("0079-07-23"), name:"Titus Flavius Vespasianus"})
CREATE (Titus:Person {key:"titus", from:date("0039-12-30"), to:date("0081-09-13"), name:"Titus Flavius Vespasianus"})
CREATE (Domitian:Person {key:"domitian", from:date("0051-10-24"), to:date("0096-09-18"), name:"Titus Flavius Domitianus"})
CREATE (Domitilla:Person {key:"domitilla-the-elder", name:"Flavia Domitilla Major"})
CREATE (Caenis:Person {key:"caenis", to:"0074", name:"Antonia Caenis"})

// Flavian Family
CREATE (Vespasian)-[:MARRIED_TO]->(Domitilla)
CREATE (Vespasian)-[:RELATIONSHIP_WITH]->(Caenis)
CREATE (Titus)-[:CHILD_OF]->(Domitilla)
CREATE (Titus)-[:CHILD_OF]->(Vespasian)
CREATE (Domitian)-[:CHILD_OF]->(Domitilla)
CREATE (Domitian)-[:CHILD_OF]->(Vespasian)



// TODO: Transform "from" and "to" to date

// Crisis of 3rd century
CREATE (ValerianI:Person {key:"valerian-i", from:"019*", to:"0260", name:"Publius Licinius Valerianus"})
CREATE (Gallienus:Person {key:"gallienus", from:"0218", to:"0268", name:"Publius Licinius Egnatius Gallienus"})
CREATE (ClaudiusGothicus:Person {key:"claudius-gothicus", from:"0214", to:"0270", name:"Marcus Aurelius Claudius"})
CREATE (Quintillus:Person {key:"quintillus", from:"022*", to:"0270", name:"Marcus Aurelius Claudius Quintillus"})
CREATE (Aurelian:Person {key:"aurelian", from:"0214", to:"0275", name:"Lucius Domitius Aurelianus"})
CREATE (Tacitus:Person {key:"tacitus", from:"0200", to:"0276", name:"Marcus Claudius Tacitus"})

////// REL: RULED
CREATE (ValerianI)-[:RULED {titles:["emperor"], from:"0253", to:"0260"}]->(RomanEmpire)
CREATE (Gallienus)-[:RULED {titles:["emperor"], from:"0253", to:"0268"}]->(RomanEmpire)
CREATE (ClaudiusGothicus)-[:RULED {titles:["emperor"], from:"0268", to:"0270"}]->(RomanEmpire)
CREATE (Quintillus)-[:RULED {titles:["emperor"], from:"0270", to:"0270"}]->(RomanEmpire)
CREATE (Aurelian)-[:RULED {titles:["emperor"], from:"0270", to:"0275"}]->(RomanEmpire)
CREATE (Tacitus)-[:RULED {titles:["emperor"], from:"0275", to:"0276"}]->(RomanEmpire)


//// Gallic Empire
CREATE (GallicEmpire:Territory {key:"gallic-empire", from:"0267", to:"0273"})
CREATE (Postumus:Person {key:"postumus", to:"0269", name:"Marcus Cassianius Latinius Postumus"})
CREATE (Marius:Person {key:"marius", to:"0269", name:"Marcus Aurelius Marius"})
CREATE (Victorinus:Person {key:"victorinus", from:"02**", to:"0270|0271", name:"Marcus Piavonius Victorinus"})
CREATE (Tetricus:Person {key:"tetricus", to:"0274", name:"Gaius Pius Esuvius Tetricus"})

////// REL: SECESSIONAL_TO
CREATE (GallicEmpire)-[:SECESSIONAL_TO]->(RomanEmpire)

////// REL: RULED
CREATE (Postumus)-[:RULED {titles:["emperor"], from:"0260", to:"0269"}]->(GallicEmpire)
CREATE (Marius)-[:RULED {titles:["emperor"], from:"0269", to:"0269"}]->(GallicEmpire)
CREATE (Victorinus)-[:RULED {titles:["emperor"], from:"0269", to:"0270"}]->(GallicEmpire)
CREATE (Tetricus)-[:RULED {titles:["emperor"], from:"0270", to:"0274"}]->(GallicEmpire)


//// Palmyrene Empire
CREATE (PalmyreneEmpire:Territory {key:"palmyrene-empire", from:"0260", to:"0274"})
CREATE (Vaballathus:Person {key:"vaballathus", from:"0259", to:"0274", name:"Septimius Vaballathus"})
CREATE (Zenobia:Person {key:"zenobia", from:"0240", to:"0274", name:"Septimia Zenobia"})
CREATE (Antiochus:Person {key:"antiochus", to:"0273", name:"Septimius Antiochus"})

////// REL: SECESSIONAL_TO
CREATE (PalmyreneEmpire)-[:SECESSIONAL_TO]->(RomanEmpire)

////// REL: RULED
CREATE (Vaballathus)-[:RULED {titles:["emperor", "king"], from:"0267", to:"0272"}]->(PalmyreneEmpire)
CREATE (Zenobia)-[:RULED {titles:["empress", "queen"], from:"0272", to:"0273"}]->(PalmyreneEmpire)
CREATE (Antiochus)-[:RULED {titles:["empress", "queen"], from:"0273", to:"0273"}]->(PalmyreneEmpire)
