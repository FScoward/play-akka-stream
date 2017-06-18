package service

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.alpakka.sqs.scaladsl.SqsSource
import akka.stream.scaladsl.{Flow, Sink}
import com.amazonaws.services.sqs.model.Message
import javax.inject.{Inject, Singleton}

import scala.concurrent.Future
import scala.util.Random
import scala.concurrent.duration._

/**
  * Created by fscoward on 2017/06/18.
  */
@Singleton
class WordToPdfservice @Inject()() extends SqsBase {
  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

//  for (i <- 100 to 110) {
//    sqsClient.sendMessage(queueUrl, s"No.$i")
//  }
  val source = SqsSource(queueUrl = queueUrl, sqsSourceSettings)

  val convert = Flow[Message].mapAsyncUnordered(4) { message =>
    println(s"convert [message]=${message.getBody}")
    Future.successful(message)
  }


  val delete = Flow[Message].mapAsyncUnordered(4) { message =>
//    Thread.sleep((Random.nxtInt(5) second).toMillis)
    sqsClient.deleteMessage(queueUrl, message.getReceiptHandle)
    Future.successful(message)
  }

  val sink = Sink.foreachParallel[Message](4)(message => println(s"Sink: ${message.getBody}"))

  source.async.via(convert).async.via(delete).runWith(sink)



}
