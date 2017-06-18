package actors

import akka.stream.alpakka.sqs.SqsSourceSettings
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.sqs.AmazonSQSAsyncClient
import scala.concurrent.duration._

/**
  * Created by fscoward on 2017/06/18.
  */
trait SqsBase {
  val queueUrl = "http://localhost:9324/queue/test"
  val credentials = new BasicAWSCredentials("x", "x")
  implicit val sqsClient: AmazonSQSAsyncClient =
    new AmazonSQSAsyncClient(credentials).withEndpoint("http://localhost:9324")
  val sqsSourceSettings = SqsSourceSettings(20 seconds, 100, 10)
}
