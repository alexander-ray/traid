package utils

import com.fasterxml.jackson.dataformat.csv.CsvMapper
import com.fasterxml.jackson.dataformat.csv.CsvSchema
import java.io.FileReader
import java.io.FileWriter

/**
 * Reader and writer are based on https://medium.com/att-israel/jackson-csv-reader-writer-using-kotlin-f37ae771bd6d
 */
inline fun <reified T> CsvMapper.writeCsvFile(data: Collection<T>, fileName: String) {
    FileWriter(fileName).use { writer ->
        this.writer(this.schemaFor(T::class.java).withHeader())
            .writeValues(writer)
            .writeAll(data)
            .close()
    }
}

inline fun <reified T> CsvMapper.readCsvFile(fileName: String): List<T> {
    FileReader(fileName).use { reader ->
        return this
            .readerFor(T::class.java)
            .with(CsvSchema.emptySchema().withHeader())
            .readValues<T>(reader)
            .readAll()
            .toList()
    }
}