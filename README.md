[![Run Status](https://api.shippable.com/projects/57eb01226fb4bc0e008f0352/badge?branch=master)](https://app.shippable.com/projects/57eb01226fb4bc0e008f0352)

# NiFi ExecuteScript Samples
This repo contains samples scripts for use with Apache NiFi's ExecuteScript processor.
Additionally, the repo may be cloned and modified to unit test custom scripts using NiFi's mock framework.

## Sample Scripts
Scripts are designed to demonstrate basic techniques in various languages:

| Topic | Javascript | Python |
| :--- | :--- | :--- |
| Reading and writing flowfile attributes | [attributes.js](src/test/resources/executescript/attributes/attributes.js) | [attributes.py](src/test/resources/executescript/attributes/attributes.py) |
| Logging | [log.js](src/test/resources/executescript/log/log.js) | [log.py](src/test/resources/executescript/log/log.py) |
| Transforming an input flowfile to a single output | [transform.js](src/test/resources/executescript/content/transform.js) | [transform.py](src/test/resources/executescript/content/transform.py) |
| Splitting an input flowfile to multiple outputs | [split.js](src/test/resources/executescript/content/split.js) | [split.py](src/test/resources/executescript/content/split.py) |
| Writing counter metrics | [counter.js](src/test/resources/executescript/counter/counter.js) | [counter.py](src/test/resources/executescript/counter/counter.py) |
| Reading nifi.properties | [properties.js](src/test/resources/executescript/properties/properties.js) | [properties.py](src/test/resources/executescript/properties/properties.py) |
| Reading and writing state | [state.js](src/test/resources/executescript/state/state.js) | [state.py](src/test/resources/executescript/state/state.py) |

## Contributing
Please help.  These sample scripts are very likely to be buggy, unnecessarily complicated, misguided, downright stupid, or all of the above.
Please open an issue for bugs and new contributions.

## License
Apache License 2.0
