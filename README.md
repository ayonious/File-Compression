# File Compression

[![CircleCI](https://dl.circleci.com/status-badge/img/gh/ayonious/File-Compression/tree/master.svg?style=svg)](https://dl.circleci.com/status-badge/redirect/gh/ayonious/File-Compression/tree/master)
[![codecov](https://codecov.io/gh/ayonious/File-Compression/branch/master/graph/badge.svg)](https://codecov.io/gh/ayonious/File-Compression)
[![GitHub stars](https://img.shields.io/github/stars/ayonious/File-Compression?style=social)](https://github.com/ayonious/File-Compression/stargazers)

A File Compression software that helps zip/Unzip files using these 2 algorihtms:

1. Huffmans Code
2. Lempel-Ziv-Wells algorithm

# About Huffmans Code

The Huffmans algo creates a 1-1 mapping for each byte of the input file 
and replaces each byte with the mapped bit sequence. For this you need 
to store a dictionary that describes each 1-1 mapping of input byte and
binary sequence.(which needs extraspace)

# About Lempel-Ziv-Wells

Unlike Huffmans code LZW dont need an extra dictionary to be saved. Also
LZW does not create a mapping to byte to bin sequence. It creates mapping
of multiple byte to binary sequence.

## Installation

### Prerequisites
- Java 21 or higher
- Maven (for building)

<details>
<summary> Installing Maven (click to expand)</summary>

On macOS:
```bash
brew install maven
```

On Linux:
```bash
sudo apt-get install maven  # For Debian/Ubuntu
sudo dnf install maven     # For Fedora
```

Verify installation:
```bash
mvn -version
```
</details>

## Building and Running

### Directly Run the jar file
I have included the already build jar file. You can run it simply if you dont want to build
```bash
java -jar FileCompression.jar
```


### Build and Run the project Using Maven
```bash
mvn clean package
mvn test
mvn exec:java
```

### Using JAR directly
After building with Maven, you can run the JAR:
```bash
java -jar target/file-compression-1.0-SNAPSHOT-jar-with-dependencies.jar
```

![Outlook](/git_resource/outlook.png?raw=true "File Compression GUI")

## Zip a file
file>open>click zip>the zipped file will be created on the same folder


## Unzip a file
file>open>click unzip>the unzipped file will be created on the same folder


## Testing environment:

I tested this project in:
Linux Mint, OS X El Capitan (version 10.11.6), macOS Sonoma
