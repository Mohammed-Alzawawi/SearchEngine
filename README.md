# Search Engine Project

## Overview

This project is a **scalable and extensible search engine** built using **Java, Spring Boot, and Maven**. It features a **custom trie-based inverted index** with **BM25 ranking algorithm** for document relevance, along with **fuzzy search capabilities** for handling typos and approximate matches. The system supports a **dynamic schema** for managing various data types and uses a **customized file storage system**. Additionally, it includes pluggable text processing components such as **tokenization, stemming, and result filtering**.

## Features

- **Dynamic Schema Management**: Define and modify schemas for indexing documents.
- **BM25 Ranking Algorithm**: Efficiently ranks search results based on relevance.
- **Fuzzy Search**: Handles typos and approximate matches.
- **Pluggable Text Processing**: Easily extend tokenization, stemming, and filtering.
- **File-Based Storage**: Documents are stored in a file-based system.
- **CRUD Operations**: Add, update, delete, and search indexed documents.
- **Spring REST API**: Provides endpoints for schema and document management.

## Project Setup

### Prerequisites

- **Java 17 or higher**
- **Maven**

### Installation

1. Clone the repository:
   ```sh
   git clone https://github.com/your-repo/search-engine.git
   cd search-engine
   ```
2. Build the project:
   ```sh
   mvn clean install
   ```
3. Run the application:
   ```sh
   mvn spring-boot:run
   ```

## API Endpoints

The search engine provides a **REST API** with the following endpoints:

### **Schema Management**

- **Create Schema**: `POST /schema/createSchema`
- **Load Schema**: `GET /schema/load`
- **Save Schema**: `POST /schema/save`
- **Add Document**: `POST /schema/add/{schemaName}`
- **Update Document**: `PUT /schema/update`
- **Delete Document**: `DELETE /schema/delete/{schemaName}/{documentId}`
- **Search Documents**: `POST /schema/search/{schemaName}`

### **Example Requests**

#### **Create Schema**

```json
{
  "name": "reviews",
  "id": 1,
  "properties": {
    "comment": {
      "type": "text",
      "weight": 1.0,
      "mandatory": true
    },
    "age": {
        "type": "keyword",
        "mandatory": true
    }
  },
  "filters": {
    "comment": {
        "converter": "size"
    }
  }
}
```

#### **Search Request**

```json
{
    "query": "wonderful",
    "filters": {
        "range": {
            "comment": {
                "min": "-inf",
                "max": "inf"
            }
        },
        "match": {
            "age": "35"
        }
    }
}
```

## Configuration

The default system path for file storage is:

```
C:\SearchEngine
```

To customize this, modify the **application.properties** file and update constants in the `constants` package.

## Extensibility

Developers can extend the search engine by modifying the **tokenization package** to add custom tokenizers. Similarly, custom **filters and analyzers** can be implemented by extending the respective classes.

## File Storage System

- Documents are stored in a **file-based system**.
- Users can **add, update, and delete** their indexed documents.

## License

🚫 **This project does not have an open-source license.**

## Contribution

❌ **Contributions are not allowed for this project.**

## Quote
"thenoobestever actually was not the noobest ever". _Mahmoud Alaaraj_
