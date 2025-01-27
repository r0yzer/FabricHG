package de.royzer.fabrichg.serialization

import com.mongodb.internal.connection.BsonWriterDecorator
import kotlinx.serialization.ExperimentalSerializationApi
import org.bson.AbstractBsonReader
import org.bson.AbstractBsonWriter
import org.bson.codecs.kotlinx.BsonDecoder
import org.bson.codecs.kotlinx.BsonEncoder
import java.lang.reflect.Field
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaField

@OptIn(ExperimentalSerializationApi::class)
object SerializationHelper {
    fun isNameState(bsonEncoder: BsonEncoder): Boolean {
        return WriterHelper.getState(bsonEncoder) == AbstractBsonWriter.State.NAME
    }

    fun isNameState(bsonDecoder: BsonDecoder): Boolean {
        return ReaderHelper.isNameState(bsonDecoder)
    }

    private object WriterHelper {
        private val BsonEncoder_writer_field: Field by lazy {
            Class.forName("org.bson.codecs.kotlinx.JsonBsonEncoder").kotlin.memberProperties
                .find { it.name == "writer" }?.javaField!!.also {
                    it.isAccessible = true
                }
        }
        private val BsonWriterDecorator_bsonWriter_field: Field by lazy {
            BsonWriterDecorator::class.memberProperties.find { it.name == "bsonWriter" }?.javaField!!.also {
                it.isAccessible = true
            }
        }
        private val BsonWriterDecorator_state_field: Field by lazy {
            AbstractBsonWriter::class.memberProperties.find { it.name == "state" }?.javaField!!.also {
                it.isAccessible = true
            }
        }

        private fun `get BsonWriterDecorator of JsonBsonEncoder`(encoder: BsonEncoder): BsonWriterDecorator {
            val writerDecorator = BsonEncoder_writer_field.get(encoder) as? BsonWriterDecorator
                ?: throw IllegalStateException("writerDecorator is not a ${BsonWriterDecorator::class.qualifiedName}.")
            return writerDecorator
        }

        private fun `get AbstractBsonWriter of BsonWriterDecorator`(bsonWriterDecorator: BsonWriterDecorator): AbstractBsonWriter {
            val abstractBsonWriter =
                BsonWriterDecorator_bsonWriter_field.get(bsonWriterDecorator) as? AbstractBsonWriter
                    ?: throw IllegalStateException("abstractBsonWriter is not a ${AbstractBsonWriter::class.qualifiedName}.")
            return abstractBsonWriter
        }

        private fun `get State of AbstractBsonWriter`(abstractBsonWriter: AbstractBsonWriter): AbstractBsonWriter.State {
            val state = BsonWriterDecorator_state_field.get(abstractBsonWriter) as? AbstractBsonWriter.State
                ?: throw IllegalStateException("abstractBsonWriter is not a ${AbstractBsonWriter::class.qualifiedName}.")
            return state
        }

        fun getState(bsonEncoder: BsonEncoder): AbstractBsonWriter.State {
            val bsonWriterDecoder = `get BsonWriterDecorator of JsonBsonEncoder`(bsonEncoder)
            val abstractBsonWriter = `get AbstractBsonWriter of BsonWriterDecorator`(bsonWriterDecoder)
            val state = `get State of AbstractBsonWriter`(abstractBsonWriter)
            return state
        }
    }

    private object ReaderHelper {
        private val JsonBsonMapDecoderClass: Class<*> by lazy {
            Class.forName("org.bson.codecs.kotlinx.JsonBsonMapDecoder")
        }

       /* private val AbstractBsonDecoderClass: Class<*> by lazy {
            Class.forName("org.bson.codecs.kotlinx.AbstractBsonDecoder")
        }*/

        private val JsonBsonDocumentDecoder_reader_field: Field by lazy {
            JsonBsonMapDecoderClass.kotlin.memberProperties.find { it.name == "reader" }?.javaField!!.also {
                it.isAccessible = true
            }
        }

        private fun `BsonDecoder is JsonBsonMapDecoder`(bsonDecoder: BsonDecoder): Boolean {
            return JsonBsonMapDecoderClass.isInstance(bsonDecoder)
        }

        private fun `BsonDecoder as JsonBsonDocumentDecoder`(bsonDecoder: BsonDecoder): Any {
            val jsonBsonDocumentDecoder = JsonBsonMapDecoderClass.cast(bsonDecoder)
            return jsonBsonDocumentDecoder
        }

        private fun `get AbstractBsonReader of JsonBsonDocumentDecoder`(jsonBsonDocumentDecoder: Any): AbstractBsonReader {
            val reader = JsonBsonDocumentDecoder_reader_field.get(jsonBsonDocumentDecoder) as AbstractBsonReader
            return reader
        }

        private fun `get State of AbstractBsonReader`(abstractBsonReader: AbstractBsonReader): AbstractBsonReader.State {
            return abstractBsonReader.state
        }

        private fun getState(bsonDecoder: BsonDecoder): AbstractBsonReader.State {
            val jsonBsonDocumentDecoder = `BsonDecoder as JsonBsonDocumentDecoder`(bsonDecoder)
            val abstractBsonReader = `get AbstractBsonReader of JsonBsonDocumentDecoder`(jsonBsonDocumentDecoder)
            val state = `get State of AbstractBsonReader`(abstractBsonReader)
            return state
        }

        fun isNameState(bsonDecoder: BsonDecoder): Boolean {
            if (!`BsonDecoder is JsonBsonMapDecoder`(bsonDecoder)) return false
            return getState(bsonDecoder) == AbstractBsonReader.State.NAME
        }
    }
}
