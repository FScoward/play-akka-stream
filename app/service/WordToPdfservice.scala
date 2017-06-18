package service

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.alpakka.sqs.scaladsl.SqsSource
import akka.stream.scaladsl.{ Flow, Sink }
import com.amazonaws.services.sqs.model.Message
import javax.inject.{ Inject, Singleton }

import akka.NotUsed

import scala.concurrent.Future

/**
 * Created by fscoward on 2017/06/18.
 *
 * TODO
 * - [x] SQSからキューを読み取る
 * - [ ] S3ファイルをダウンロードする
 * - [ ] PDFへの変換を行う
 * - [ ] キューを消す
 *
 */
@Singleton
class WordToPdfservice @Inject() (
    implicit
    system: ActorSystem, s3Service: S3Service
) extends SqsBase {
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  val source = SqsSource(queueUrl = queueUrl, sqsSourceSettings)

  val download: Flow[Message, Message, NotUsed] = Flow[Message].mapAsyncUnordered(4) { message =>
    //        s3Service.contents
    Future.successful(message)
  }

  val convert: Flow[Message, Message, NotUsed] = Flow[Message].mapAsyncUnordered(4) { message =>
    println(s"convert [message]=${message.getBody}")
    Future.successful(message)
  }

  val delete: Flow[Message, Message, NotUsed] = Flow[Message].mapAsyncUnordered(4) { message =>
    //    Thread.sleep((Random.nxtInt(5) second).toMillis)
    sqsClient.deleteMessage(queueUrl, message.getReceiptHandle)
    Future.successful(message)
  }

  val sink = Sink.foreachParallel[Message](4)(message => println(s"Sink: ${message.getBody}"))

  //  source.async.via(download).async.via(convert).async.via(delete).runWith(sink)
  source.async.via(s3Service.download).runWith(Sink.foreach(println))
}
