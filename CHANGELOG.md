# Changelog

## [0.5.0](https://github.com/xamoom/Morpheus/compare/v0.4.3...v0.5.0)

* Added serializing of objects
  * Serialize resources with their relations
  * Add included if needed

* Fixed [issue #12](https://github.com/xamoom/Morpheus/issues/12)

## [0.4.3](https://github.com/xamoom/Morpheus/compare/v0.4.2...v0.4.3)

* Changed mapAttribute to use gson for primitive types

## [0.4.2](https://github.com/xamoom/Morpheus/compare/v0.4.1...v0.4.2)

* Fixed null fields after parsing [pull #10](https://github.com/xamoom/Morpheus/pull/10)

## [0.4.1](https://github.com/xamoom/Morpheus/compare/v0.4.0...v0.4.1)

* Removed e.printStackTrace()

## 0.4.0

* Integrated Gson for better object mapping (Thanks to [@ycoupe](https://github.com/ycoupe))
* Changed from own annotation 'SerializeName' to Gsons 'SerializedName'

* Refactored Links to be public (Thanks to [@openkwaky](https://github.com/openkwaky))

* Added Travis as CI
