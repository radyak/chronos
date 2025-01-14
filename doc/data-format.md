# Data Format

An underlying standardizing data format should be established.
This data format should

- allow to model any historical entity in a normalized way.

Brainstorming for a standard data structures:

## Entity

The core structure

- ID: Unique identifier
- Type ([Value Set](#value-set)): The type of the entity (e.g. person, event, territory etc.)
- Names: List of [Names](#name)
- Span ([hspan](#hspan))
-

## Name

- Name (string): The name or names
- Role ([Value Set](#value-set)): The role of the name
- Language ([Language Code](#language-code))

## Value Key

- Code

## Value Set

- [Value Key](#value-key)
- Code
- Label
- Language ([Language Code](#language-code))

## sref - Source Reference

Reference of the proving source.

- Abbreviation
- Title
- Author
- Issue
- Location: Chapter, page(s) etc.
- ISBN

### hspan

Time span between [Historical date](#hdate)s

- Start:
  - Date ([hdate](#hdate))
  - Location ([hlocation](#hlocation))
- End:
  - Date ([hdate](#hdate))
  - Location ([hlocation](#hlocation))

### hlocation

TBD...

Historical notation of a location with certain relevance, including all historical uncertainties.

## Atomic types:

### hdate

Historical notation of a date with certain relevance, including all historical uncertainties.
Subtype of `string`; based on the ISO 8601 date notation

Structure:
YYYY[\_YYYY][|YYYY][-MM[\_MM][|MM]][-dd[\_dd][|dd]]

Examples:

- Normal: 1789-07-14
- Before turn of the millennium: 0840-06-20
- Before turn of the eras: -0063-09-23 (year 63 BC)
- Unsecure year: 0747|0748-04-02 (either 747 or 748 AD)
- Year range: 688_691

### Language Code

Standard [IETF](https://en.wikipedia.org/wiki/IETF_language_tag) language tags.
Subtype of `string`

## Full example

[Emperor Diocletian](https://en.wikipedia.org/wiki/Diocletian):

```json
{
  "id": "665372614115329212",
  "type": {
    "system": "entity-type",
    "code": "person"
  },

  "names": [
    {
      "name": "Diocletian",
      "role": {
        "system": "entity-name",
        "code": "common-name"
      },
      "lang": "en"
    },
    {
      "name": "Gaius Aurelius Valerius Diocletianus",
      "role": {
        "system": "entity-name",
        "code": "official-name"
      },
      "lang": "en"
    }
  ],

  "span": {
    "start": {
      "date": "242_245-12-22",
      "location": {
        // Diocles; TBD
      }
    },
    "end": {
      "date": "311|312-12-03",
      "location": {
        // Aspalathos; TBD
      }
    }
  },

  "subspans": [
    {
      "role": {
        "system": "subspan-role",
        "code": "reign"
      },
      "start": {
        "date": "284-11-20"
      },
      "end": {
        "date": "305-05-01"
      }
    }
  ],

  "relations": [
    {
      "type": {
        "system": "relation-type",
        "code": "predecessor"
      },
      "ref": "18471109184302499" // Carinus
    },
    {
      "type": {
        "system": "relation-type",
        "code": "spouse"
      },
      "span": {
        "start": {
          "date": "282" // made up
        },
        "end": {
          "date": "311|312-12-03"
        }
      },
      "ref": "10396048483553200" // Prisca
    },
    {
      "type": {
        "system": "relation-type",
        "code": "territory"
      },
      "ref": "692939875900124533" // Roman Empire
    }
    // ...
  ],

  "attributes": [
    {
      "type": {
        "system": "attribute-type",
        "code": "title"
      },
      "value": {
        "system": "attribute-value",
        "code": "emperor"
      }
    }
  ]
}
```
