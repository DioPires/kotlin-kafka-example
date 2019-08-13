package com.piresdio.kafkaexample.controller

import com.piresdio.kafkaexample.configs.KafkaConfig
import com.piresdio.kafkaexample.consumers.SnackConsumer
import com.piresdio.kafkaexample.exception.ResourceAlreadyExistsException
import com.piresdio.kafkaexample.exception.ResourceNotFoundException
import com.piresdio.kafkaexample.messages.SnackMessage
import com.piresdio.kafkaexample.model.Snack
import com.piresdio.kafkaexample.producers.SnackProducer
import com.piresdio.kafkaexample.repository.SnackRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.validation.Valid

@RestController
class SnackController(@Autowired final val snackRepository: SnackRepository, @Autowired final val kafkaConfig: KafkaConfig) {
    private val snacksTopic = kafkaConfig.snacksTopic
    private val snackProducer = SnackProducer(kafkaConfig.brokers)

    init {
        GlobalScope.launch { SnackConsumer(kafkaConfig.brokers, snackRepository).consume(listOf(snacksTopic)) }
    }

    @GetMapping("/snacks")
    fun snacks(): List<Snack> {
        return snackRepository.findAll()
    }

    @GetMapping("/snacks/{snackId}")
    fun snackById(@PathVariable snackId: String): Snack {
        return snackRepository
                .findById(snackId)
                .orElseThrow { ResourceNotFoundException("Snack with ID $snackId not found!") }
    }

    @PostMapping("/snacks")
    fun createSnack(@Valid @RequestBody snack: Snack): String {
        if (snackRepository.existsByName(snack.name)) {
            throw ResourceAlreadyExistsException("Snack with name ${snack.name} already exists!")
        }
        val snackId = UUID.randomUUID().toString()
        snack.id = snackId
        snackProducer.produce(snacksTopic, SnackMessage(snack, RequestMethod.POST))
        return snackId
    }

    @PutMapping("/snacks/{snackId}")
    fun updateSnack(@PathVariable snackId: String, @Valid @RequestBody snack: Snack): String {
        if (!snackRepository.existsById(snackId)) {
            throw ResourceNotFoundException("Snack with ID $snackId not found exists!")
        }
        val snackOld: Snack = snackRepository
                .findById(snackId)
                .map { snackOld ->
                    snackOld.name = snack.name
                    snackOld.price = snack.price
                    snackOld
                }.get()
        snackProducer.produce(snacksTopic, SnackMessage(snackOld, RequestMethod.PUT))
        return snackId
    }

    @DeleteMapping("/snacks/{snackId}")
    fun deleteSnack(@PathVariable snackId: String): ResponseEntity<Unit> {
        if (!snackRepository.existsById(snackId)) {
            throw ResourceNotFoundException("Snack with ID $snackId not found exists!")
        }

        val snack = snackRepository.findById(snackId).get()
        snackProducer.produce(snacksTopic, SnackMessage(snack, RequestMethod.DELETE))
        return ResponseEntity.ok().build()
    }
}