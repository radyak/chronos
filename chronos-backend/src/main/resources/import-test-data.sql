INSERT INTO TAG_CATEGORY(ID, NAME) VALUES
   (1, 'Role'),
   (2, 'Dynasty');

INSERT INTO TAG(ID, TAG_CATEGORY_ID, NAME, COLOR) VALUES
    (1,	1,	'Emperor',      '#e25'),
    (2,	1,	'King',         '#32a877'),
    (3,	2,	'Carolingian',  '#20d'),
    (4,	2,	'Merovingian',  '#8932a8');

INSERT INTO ENTRY(ID, TITLE, SUBTITLE, WIKIPEDIA_PAGE) VALUES
    (1,	'Childeric III',	null,	                null),
    (2,	'Charles Martel',   null,	                null),
    (3,	'Pepin',            'The Short',	        null),
    (4,	'Charlemagne',      'Charles the Great',    null),
    (5,	'Louis',            'The Pious',	        null),
    (6,	'Augustus', 	    null,	                'Augustus'),
    (7,	'Tiberius',         null,	                null);

INSERT INTO ENTRY_TAG(ENTRY_ID, TAG_ID) VALUES
    (1,	2),
    (1,	4),
    (2,	3),
    (3,	2),
    (3,	3),
    (4,	1),
    (4,	3),
    (5,	1),
    (5,	3),
    (6,	1),
    (7,	1);

INSERT INTO DATE_RANGE(ID, ENTRY_ID, START_DATE, END_DATE, RANGE_TYPE) VALUES
    (1, 1,	'0717-01-01',	'0754-01-01',	'REIGN'),
    (2,	2,	'0688-01-01',	'0741-10-22',	'REIGN'),
    (3,	3,	'0714-01-01',	'0768-09-24',	'REIGN'),
    (4,	4,	'0768-10-09',	'0814-01-28',	'REIGN'),
    (5,	4,	'0748-04-02',	'0814-01-28',	'LIFE'),
    (6, 5,	'0778-01-01',	'0840-06-20',	'REIGN'),
    (7, 6,	'-0031-01-01',	'0014-08-19',	'REIGN'),
    (8, 6,	'-0063-09-23',	'0014-08-19',	'LIFE'),
    (9, 7,	'0014-08-19',	'0037-03-13',	'REIGN'),
    (10,7,	'-0042-11-06',	'0037-03-13',	'LIFE');

INSERT INTO RELATION_TYPE(ID, NAME, LABEL, INVERSE_RELATION_LABEL) VALUES
    (1, 'PREDECESSOR_OF', 'predecessor of', 'successor of');

INSERT INTO RELATION(ID, REL_VALUE, ENTRY_FROM_ID, ENTRY_TO_ID, RELATION_TYPE_ID) VALUES
    (1, NULL, 1, 3, 1),
    (2, NULL, 2, 3, 1),
    (3, NULL, 3, 4, 1),
    (4, NULL, 4, 5, 1),
    (5, NULL, 6, 7, 1);