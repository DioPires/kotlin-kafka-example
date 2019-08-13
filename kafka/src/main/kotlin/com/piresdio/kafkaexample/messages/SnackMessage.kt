package com.piresdio.kafkaexample.messages

import com.piresdio.kafkaexample.model.Snack
import org.springframework.web.bind.annotation.RequestMethod

data class SnackMessage(val snack: Snack, val method: RequestMethod)