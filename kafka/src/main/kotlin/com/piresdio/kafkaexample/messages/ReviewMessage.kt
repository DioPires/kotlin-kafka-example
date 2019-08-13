package com.piresdio.kafkaexample.messages

import com.piresdio.kafkaexample.model.Review
import org.springframework.web.bind.annotation.RequestMethod

data class ReviewMessage(val review: Review? = null, val reviewId: String, val snackId: String? = null, val method: RequestMethod)