package com.example.SearchEngine.constants;

public final class Constants {
    private Constants() {
    }

    public static final class Paths {
        private Paths() {
        }

        public static final String SCHEMA_FORMS_PATH = "src/main/java/com/example/SearchEngine/schema/forms/";
        public static final String SCHEMA_STORAGE_PATH = "C:/Search Engine/Directory/";
        public static final String SCHEMA_PATH_DICTIONARY_PATH = "C:/Search Engine/Externals/Path Directory/";
        public static final String SCHEMA_PROPERTIES_BSTs_PATH = "C:/Search Engine/Externals/PropertiesBSTs/";
        public static final String SCHEMA_ALL_CURRENT_DOCUMENTS_PATH = "C:/Search Engine/Externals/AllCurrentDocuments/";
        public static final String SCHEMA_KEYWORD_TRIES_PATH = "C:/Search Engine/Externals/KeywordTries/";
    }

    public static final class Messages {
        private Messages() {
        }

        public static final String NOT_FOUND_OR_INVALID_FIELD = "Filed \"%s\" not found or has an invalid data type";
        public static final String INVALID_SCHEMA_REPRESENTATION = "Invalid schema representation";
        public static final String SCHEMA_ATTRIBUTE_NOT_FOUND = "\"%s\" not found";
        public static final String UNKNOWN_FIELD_NAME = "Unknown filed name \"%s\"";
    }
}
