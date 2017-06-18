package actors

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.routing.RoundRobinPool
import akka.stream.ActorMaterializer
import akka.stream.alpakka.sqs.SqsSourceSettings
import akka.stream.alpakka.sqs.scaladsl.SqsSource
import akka.stream.scaladsl.{Flow, Sink, Source}
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.sqs.AmazonSQSAsyncClient
import com.amazonaws.services.sqs.model.Message

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.Random

/**
  * Created by fscoward on 2017/06/16.
  */
object MySupervisorActor extends MySupervisorActor {
  def props = Props[MySupervisorActor]
}
class MySupervisorActor extends Actor with ActorLogging with SqsBase {
  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  // 子Actorを指定
//  val p = context.actorOf(RoundRobinPool(5).props(Props[MyActor]), "my-actor")

  for (i <- 0 to 20) {
    sqsClient.sendMessage(queueUrl, s"No.$i")
  }

  // 無限に続くInput - http://qiita.com/Showmant/items/bdd2df09655b0b5931fe
  val source = SqsSource(queueUrl = queueUrl, sqsSourceSettings)

  val convert = Flow[Message].mapAsyncUnordered(4) { message =>
    log.debug(s"convert [message]=${message.getBody}")
    Future.successful(message)
  }


  val delete = Flow[Message].mapAsyncUnordered(4) { message =>
    Thread.sleep((Random.nextInt(5) second).toMillis)
    sqsClient.deleteMessage(queueUrl, message.getReceiptHandle)
    Future.successful(message)
  }

  val sink = Sink.foreachParallel[Message](4)(message => log.debug(s"Sink: ${message.getBody}"))

//  source.via(mapC).via(delete).runWith(sink)
  source.async.via(convert).async.via(delete).runWith(sink)


  override def preStart(): Unit = {
    println (s"[preStart] Supervisor") }
  override def postStop(): Unit = { println (s"[postStop] Supervisor") }
  override def receive: Receive = {
    case message: Message => {
      log.debug("HELLO")
//      sqsClient.deleteMessageAsync(queueUrl, message.getReceiptHandle)

    }
  }
}

class MyActor extends Actor with ActorLogging {
  val queueUrl = "http://localhost:9324/queue/test"
  val credentials = new BasicAWSCredentials("x", "x")
  implicit val sqsClient: AmazonSQSAsyncClient =
    new AmazonSQSAsyncClient(credentials).withEndpoint("http://localhost:9324")

  val p = context.actorOf(Props[DeleteMessageActor], "delete-message-actor")

  override def preStart(): Unit = { println (s"[preStart] Child Actor") }
  override def postStop(): Unit = { println (s"[postStop] Child Actor") }
  override def receive: Receive = {
    case message: Message => {
//      log.debug(s"[message]=${message.getBody}, [receipt]=${message.getReceiptHandle}")
      log.debug(s"[message]=${message.getBody}")
      Thread.sleep((3 second).toMillis)
//      sender() ! message
      p ! message
    }
  }

  override def postRestart(reason: Throwable): Unit = println(s"postRestart: $reason")
}

class DeleteMessageActor extends Actor with ActorLogging {
  val queueUrl = "http://localhost:9324/queue/test"
  val credentials = new BasicAWSCredentials("x", "x")
  implicit val sqsClient: AmazonSQSAsyncClient =
    new AmazonSQSAsyncClient(credentials).withEndpoint("http://localhost:9324")

  override def receive: Receive = {
    case message: Message =>
      log.debug(s"DeleteMessageActor: ${message.getBody}")
      Thread.sleep((5 second).toMillis)
      sqsClient.deleteMessageAsync(queueUrl, message.getReceiptHandle)
  }

}